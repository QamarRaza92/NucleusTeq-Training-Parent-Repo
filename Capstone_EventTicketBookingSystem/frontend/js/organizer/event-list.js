const EVENT_API = "http://localhost:8080/api/events";

function getStatusFromPage() {
    const path = window.location.pathname;
    if (path.includes("cancelled")) return "CANCELLED";
    if (path.includes("completed")) return "COMPLETED";
    return "ACTIVE"; 
}

async function loadEvents() {
    const token = getToken();
    if (!token) { window.location.href = "../login.html"; return; }
    
    const status = getStatusFromPage(); 
    
    try {
        const response = await fetch(`${EVENT_API}/organizer/events?status=${status}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const events = await response.json();
        displayEvents(events);
    } catch (error) {
        document.getElementById("eventsList").innerHTML = '<div class="empty-state">Failed to load events</div>';
    }
}

function displayEvents(events) {
    const container = document.getElementById("eventsList");
    if (!events || events.length === 0) {
        container.innerHTML = '<div class="empty-state">No events found</div>';
        return;
    }
    
    container.innerHTML = events.map(event => `
        <div class="event-card">
            <div class="event-title">${event.name}</div>
            <div class="event-details"><i class="fa-solid fa-location-dot" style="color: #e67e22;"></i> ${event.venue}</div>
            <div class="event-details"><i class="fa-solid fa-calendar-days" style="color: #3498db;"></i> ${new Date(event.eventDate).toLocaleDateString()}</div>
            <div class="available-seats"><i class="fa-solid fa-circle-check" style="color: #28a745;"></i> ${event.totalSeats - (event.bookedSeats || 0)} seats available</div>
            <div class="actions">
                ${event.status === 'ACTIVE' ? 
                    `<a href="edit-event.html?id=${event.id}" class="edit-btn">Edit</a>
                    <button onclick="cancelEvent(${event.id})" class="cancel-btn">Cancel</button>` : 
                    ''}
            </div>
        </div>
    `).join("");
}

async function cancelEvent(eventId) {
    if (!confirm("Are you sure you want to cancel this event? All bookings will be cancelled.")) return;
    
    const token = getToken();
    try {
        const response = await fetch(`${EVENT_API}/cancel/${eventId}`, {
            method: "PUT",
            headers: { "Authorization": `Bearer ${token}` }
        });
        
        if (response.ok) {
            showNotification("Event cancelled successfully!","success");
            loadEvents();
        } else {
            const error = await response.json();
            showNotification(error.message || "Failed to cancel event","error");
        }
    } catch (error) {
        showNotification("Server error. Please try again.","error");
    }
}

loadEvents();