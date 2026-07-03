window.onload = () => loadCreated();

document.getElementById("createdBtn").onclick = loadCreated;
document.getElementById("joinedBtn").onclick = loadJoined;
document.getElementById("pendingBtn").onclick = loadPending;
document.getElementById("rejectedBtn").onclick = loadRejected;

async function loadCreated() {
    document.getElementById("createdBtn").classList.add("active");
    document.getElementById("joinedBtn").classList.remove("active");
    document.getElementById("pendingBtn").classList.remove("active");
    document.getElementById("rejectedBtn").classList.remove("active");
    const response = await fetch(`${API_BASE}/activity/my-created`, {
        headers: {
            Authorization: `Bearer ${getToken()}`
        }
    });
    const data = await response.json();
    if (!response.ok) {
        showNotification(data.detail);
        return;
    }
    renderActivities(data, true);
}

async function loadJoined() {
    document.getElementById("joinedBtn").classList.add("active");
    document.getElementById("createdBtn").classList.remove("active");
    document.getElementById("pendingBtn").classList.remove("active");
    document.getElementById("rejectedBtn").classList.remove("active");
    const response = await fetch(`${API_BASE}/activity/my-joined`, {
        headers: {
            Authorization: `Bearer ${getToken()}`
        }
    });
    const data = await response.json();
    if (!response.ok) {
        showNotification(data.detail);
        return;
    }
    renderActivities(data, false);
}

async function loadPending()
{
    document.getElementById("pendingBtn").classList.add("active");
    document.getElementById("joinedBtn").classList.remove("active");
    document.getElementById("createdBtn").classList.remove("active");
    document.getElementById("rejectedBtn").classList.remove("active");
    const response = await fetch(`${API_BASE}/activity/my-pending`,
                                 {
                                    headers: {Authorization: `Bearer ${getToken()}`}
                                 });
    const data = await response.json();
    if(!response.ok)
    {
        showNotification(data.detail);
        return;
    }
    renderActivities(data,false);
}

async function loadRejected()
{
    document.getElementById("rejectedBtn").classList.add("active");
    document.getElementById("joinedBtn").classList.remove("active");
    document.getElementById("createdBtn").classList.remove("active");
    document.getElementById("pendingBtn").classList.remove("active");

    const response = await fetch(`${API_BASE}/activity/my-rejected`,
                                 {
                                    headers: {Authorization: `Bearer ${getToken()}`}
                                 }
                                );
    const data = await response.json();
    if(!response.ok)
    {
        showNotification(data.detail);
        return;
    }
    renderActivities(data,false);

}

async function cancelActivity(id) {
    if (!confirm("Cancel this activity?")) return;
    const response = await fetch(`${API_BASE}/activity/${id}`, {
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${getToken()}`
        }
    });
    const data = await response.json();
    if (response.ok) {
        showNotification(data.message, "success");
        loadCreated();
    }
    else {
        showNotification(data.detail);
    }
}

function renderActivities(activities, created) {
    const container = document.getElementById("activityContainer");
    container.innerHTML = "";

    if (activities.length === 0) {
        container.innerHTML = `
            <div class="no-data">
                No Activities Found
            </div>
        `;
        return;
    }

    activities.forEach(a => {
        const disableActions =
            a.status === "COMPLETED" ||
            a.status === "CANCELLED";

        container.innerHTML += `
        <div class="activity-card">
            <div class="card-top">
                <h3>
                    <i class="fa-solid fa-calendar-days"></i>
                    ${a.title}
                </h3>
                <span class="category">
                    ${a.category}
                </span>
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
                    ${a.time.substring(0,5)}
                </span>
                <span>
                    <i class="fa-solid fa-users"></i>
                    ${a.max_participants}
                </span>
                <span>
                    <i class="fa-solid fa-circle-info"></i>
                    ${a.status}
                </span>
            </div>

            <div class="card-buttons">
                <button class="view-btn"
                    onclick="window.location.href='/activity-detail/${a.id}'">
                    <i class="fa-solid fa-eye"></i>
                    View
                </button>

                ${
                    created
                    ?
                    `
                    <button class="view-btn"
                        ${disableActions ? "disabled" : `onclick="window.location.href='/edit-activity/${a.id}'"`}>
                        <i class="fa-solid fa-pen"></i>
                        Edit
                    </button>

                    <button class="join-btn"
                        ${disableActions ? "disabled" : `onclick="cancelActivity(${a.id})"`}>
                        <i class="fa-solid fa-ban"></i>
                        Cancel
                    </button>
                    `
                    :
                    ""
                }
            </div>
        </div>
        `;
    });
}