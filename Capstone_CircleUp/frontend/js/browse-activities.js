window.onload = function () {
    loadActivities();
};

document.getElementById("searchBtn").onclick = function () {
    loadActivities();
};

async function loadActivities() {
    try {
        const category = document.getElementById("category").value;
        const location = document.getElementById("location").value;
        const sort = document.getElementById("sort").value;
        const search = document.getElementById("searchBox").value.toLowerCase();

        let url = `${API_BASE}/activity/?`;

        if (sort) {
            url += `sort=${sort}&`;
        }

        if (category) {
            url += `category=${category}&`;
        }

        if (location) {
            url += `location=${location}`;
        }

        const response = await fetch(url, {
            headers: {
                Authorization: `Bearer ${getToken()}`
            }
        });

        const data = await response.json();

        if (!response.ok) {
            showNotification(data.detail);
            return;
        }

        // Filter activities based on search text entered by the user.
        const filtered = data.filter(a =>
            a.title.toLowerCase().includes(search) ||
            (a.description && a.description.toLowerCase().includes(search))
        );

        renderActivities(filtered);

    } catch (error) {
        showNotification("Failed to load activities. Please try again.", "error");
    }
}

function renderActivities(filtered) {
    const container = document.getElementById("activityContainer");

    container.innerHTML = "";

    if (filtered.length === 0) {
        container.innerHTML = `
            <div class="no-data">
                No Activities Found
            </div>
        `;
        return;
    }
    const user_id = Number(sessionStorage.getItem("user_id"))

    // Dynamically create activity cards for each activity.
    filtered.forEach(a => {
        // Disable join for organizers
        const disableJoin = a.organizer_id === user_id  ;

        container.innerHTML += `
            <div class="activity-card">
                <div class="card-top">
                    <h3>
                        <i class="fa-solid fa-calendar-days"></i> ${a.title}
                    </h3>
                    <span class="category">${a.category}</span>
                </div>

                <p class="description">
                    ${a.description || "No Description"}
                </p>

                <div class="card-info">
                    <span>
                        <i class="fa-solid fa-location-dot"></i>
                        ${a.location}
                    </span>

                    <span>
                        <i class="fa-solid fa-calendar"></i>
                        ${a.date}
                    </span>

                    <span>
                        <i class="fa-solid fa-clock"></i>
                        ${a.time.substring(0, 5)}
                    </span>

                    <span>
                        <i class="fa-solid fa-users"></i>
                        ${a.max_participants} Seats
                    </span>

                    <span>
                        <i class="fa-solid fa-circle-info"></i>
                        ${a.status}
                    </span>
                </div>

                <div class="card-buttons">
                    <button
                        class="view-btn"
                        onclick="window.location.href='/activity-detail/${a.id}'">
                        <i class="fa-solid fa-eye"></i> View
                    </button>

                    <button  
                        class="join-btn" 
                        ${disableJoin ? "disabled": `onclick="joinActivity(${a.id})"`}>
                        <i class="fa-solid fa-user-plus"></i> ${disableJoin ? "Owner" : "Join"}
                    </button>
                </div>
            </div>
        `;
    });
}

async function joinActivity(id) {
    try {
        const response = await fetch(
            `${API_BASE}/activities/${id}/requests`,
            {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${getToken()}`
                }
            }
        );

        const data = await response.json();

        if (response.ok) {
            showNotification("Request Sent", "success");
        } else {
            showNotification(data.detail);
        }

    } catch (error) {
        showNotification("Failed to send request. Please try again.", "error");
    }
}