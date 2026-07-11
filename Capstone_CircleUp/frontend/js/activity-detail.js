const activityId = window.location.pathname.split("/").pop();

window.onload = () => loadActivity();


async function loadActivity() {
    try {
        const response = await fetch(`${API_BASE}/activity/${activityId}`, {
            headers: {
                Authorization: `Bearer ${getToken()}`
            }
        });

        const a = await response.json();

        if (!response.ok) {
            showNotification(a.detail);
            return;
        }

        // Fill activity details.
        document.getElementById("activityTitle").innerHTML =
            `<i class="fa-solid fa-calendar-days"></i> ${a.title}`;

        document.getElementById("activityCategory").innerText = a.category;
        document.getElementById("activityStatus").innerText = a.status;
        document.getElementById("activityStatus").classList.add(
            `status-${a.status.toLowerCase()}`
        );
        document.getElementById("activityLocation").innerText = a.location;
        document.getElementById("activityDate").innerText = a.date;
        document.getElementById("activityTime").innerText =
            a.time.substring(0, 5);
        document.getElementById("activitySeats").innerText =
            a.max_participants;
        document.getElementById("activityDescription").innerHTML =
            `<b>Description</b><br><br>${a.description}`;

        // Phone number visibility is controlled by the backend.
        document.getElementById("organizerPhone").innerText =
            a.organizer_phone || "Hidden";

        // Back button is always visible.
        const buttons = document.getElementById("actionButtons");

        buttons.innerHTML = `
            <button class="btn-back-detail" onclick="history.back()">
                <i class="fa-solid fa-arrow-left"></i> Back
            </button>
        `;

        // Show organizer actions only when i am the organizer of activity.
        if (sessionStorage.getItem("user_id") == a.organizer_id) {

            // Prevent editing/cancelling once activity is completed or cancelled.
            const disableActions =
                a.status === "COMPLETED" || a.status === "CANCELLED";

            buttons.innerHTML += `
                <button
                    class="btn-edit-detail"
                    ${
                        disableActions
                            ? "disabled"
                            : `onclick="window.location.href='/edit-activity/${a.id}'"`
                    }>
                    <i class="fa-solid fa-pen"></i> Edit
                </button>

                <button
                    class="btn-cancel-detail"
                    ${
                        disableActions
                            ? "disabled"
                            : `onclick="cancelActivity(${a.id})"`
                    }>
                    <i class="fa-solid fa-ban"></i> Cancel
                </button>
            `;

            // Load all pending participation requests for this activity.
            loadRequests();

        } else if (a.status == "OPEN") {

            // Non-organizers can join only if the activity is open.
            buttons.innerHTML += `
                <button
                    class="btn-join-detail"
                    onclick="joinActivity(${a.id})">
                    <i class="fa-solid fa-user-plus"></i>
                    Join Activity
                </button>
            `;
        }

    } catch (error) {
        showNotification("Failed to load activity. Please try again.","error");
    }
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
        showNotification("Failed to send request. Please try again.","error");
    }
}


async function cancelActivity(id) {

    // Ask for confirmation before cancelling the activity.
    if (!confirm("Cancel this activity?")) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/activity/${id}`, {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${getToken()}`
            }
        });

        const data = await response.json();

        if (response.ok) {
            showNotification(data.message, "success");

            // Redirect back after successful cancellation.
            setTimeout(() => history.back(), 2000);

        } else {
            showNotification(data.detail);
        }

    } catch (error) {
        showNotification("Failed to cancel activity. Please try again.","error");
    }
}


async function loadRequests() {
    try {
        const response = await fetch(
            `${API_BASE}/activities/${activityId}/requests`,
            {
                headers: {
                    Authorization: `Bearer ${getToken()}`
                }
            }
        );

        const requests = await response.json();

        // Return silently if there are no pending requests.
        if (!response.ok || requests.length == 0) {
            return;
        }

        let html = `
            <div class="requests-card">
                <h2>Pending Requests</h2>
        `;

        // Build request cards dynamically for each pending participant.
        requests.forEach(r => {
            html += `
                <div class="request">
                    <span>
                        <i class="fa-solid fa-user"></i>
                        ${r.participant_name}
                        (${r.participant_email})
                        wants to join this activity
                    </span>

                    <div class="request-buttons">
                        <button
                            class="approve-btn"
                            onclick="updateRequest(${r.id}, 'approve')">
                            Approve
                        </button>

                        <button
                            class="reject-btn"
                            onclick="updateRequest(${r.id}, 'reject')">
                            Reject
                        </button>
                    </div>
                </div>
            `;
        });

        html += "</div>";

        document.getElementById("requestsSection").innerHTML = html;

    } catch (error) {
        showNotification("Failed to load requests. Please try again.","error");
    }
}


async function updateRequest(requestId, type) {
    try {
        const response = await fetch(
            `${API_BASE}/activities/${activityId}/requests/${requestId}/${type}`,
            {
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${getToken()}`
                }
            }
        );

        const data = await response.json();

        if (response.ok) {
            showNotification(`Request ${type}d`, "success");

            // Refresh the pending requests list after approval/rejection.
            loadRequests();

        } else {
            showNotification(data.detail);
        }

    } catch (error) {
        showNotification("Failed to update request. Please try again.","error");
    }
}