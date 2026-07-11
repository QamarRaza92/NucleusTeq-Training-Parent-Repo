const token = getToken();

if (!token) {
    window.location.href = "/";
}

document.querySelector(".logout-btn").addEventListener("click", logout);

document.getElementById("profileLink").addEventListener(
    "click",
    () => {
        window.location.href = "/profile";
    }
);

window.onload = loadDashboard;

async function loadDashboard() {
    try {
        const response = await fetch(
            `${API_BASE}/users/dashboard`,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        const data = await response.json();

        if (!response.ok) {
            showNotification(data.detail || "Failed to load dashboard");
            return;
        }

        renderDashboard(data);

    } catch(error) {
        showNotification("Server Error");
    }
}

function renderDashboard(data) {

    // Fill user information and dashboard statistics.
    document.querySelector(".right span").innerHTML =
        `<i class="fa-solid fa-user"></i> ${data.user}`;

    document.querySelector(".content h2").innerHTML =
        `Welcome back, ${data.user} 👋`;

    document.querySelectorAll(".card p")[0].textContent = data.stats.created;
    document.querySelectorAll(".card p")[1].textContent = data.stats.joined;
    document.querySelectorAll(".card p")[2].textContent = data.stats.pending;
    document.querySelectorAll(".card p")[3].textContent = data.stats.completed;

    loadActivities(data.activities);
    loadRequests(data.requests);
}

function loadActivities(activities) {

    const section = document.querySelectorAll(".section")[0];

    let html = `<h3>Recent Activities</h3>`;

    if (activities.length === 0) {
        html += `<p>No activities found.</p>`;
    }
    else {
        // Display recently created activities.
        activities.forEach(activity => {
            html += `
            <div class="section-child">
                <span>${activity.title}</span>

                <button onclick="viewActivity(${activity.id})">
                    View
                </button>
            </div>
            `;
        });
    }

    section.innerHTML = html;
}

function loadRequests(requests) {

    const section = document.querySelectorAll(".section")[1];

    let html = `<h3>Notifications</h3>`;

    if (requests.length === 0) {
        html += `<p>No pending requests.</p>`;
    }
    else {
        // Display pending participation requests for organizer.
        requests.forEach(req => {
            html += `
            <div class="section-child">
                <span>
                    Request ${req.request_id}:- ${req.participant} (${req.participant_email}) wants to join '${req.activity_id}':
                    <b>${req.activity}</b>
                </span>

                <div>
                    <button
                        class="accept-btn"
                        onclick="approveRequest(${req.activity_id},${req.request_id})">
                        Accept
                    </button>

                    <button
                        class="reject-btn"
                        onclick="rejectRequest(${req.activity_id},${req.request_id})">
                        Reject
                    </button>
                </div>
            </div>
            `;
        });
    }

    section.innerHTML = html;
}

async function approveRequest(activityId, requestId) {
    try {
        const response = await fetch(
            `${API_BASE}/activities/${activityId}/requests/${requestId}/approve`,
            {
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        const data = await response.json();

        if (response.ok) {
            showNotification("Request Approved", "success");
            loadDashboard();
        }
        else {
            showNotification(data.detail);
        }
    }
    catch(error) {
        showNotification("Failed to approve request. Please try again.");
    }
}

async function rejectRequest(activityId, requestId) {
    try {
        const response = await fetch(
            `${API_BASE}/activities/${activityId}/requests/${requestId}/reject`,
            {
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        const data = await response.json();

        if (response.ok) {
            showNotification("Request Rejected", "success");
            loadDashboard();
        }
        else {
            showNotification(data.detail);
        }
    }
    catch(error) {
        showNotification("Failed to reject request. Please try again.");
    }
}

function viewActivity(id)
{
    window.location.href = `/activity-detail/${id}`;
}