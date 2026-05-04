const EVENT_API = "http://localhost:8080/api/events";

async function loadPastEvents() {
    const token = getToken();
    if (!token) { window.location.href = "../login.html"; return; }
    
    try {
        const response = await fetch(`${EVENT_API}/customer-events?status=COMPLETED`, {
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
        container.innerHTML = '<div class="empty-state">No past events found</div>';
        return;
    }
    
    container.innerHTML = events.map(event => `
        <div class="event-card">
            <span class="badge">PAST EVENT</span>
            <div class="event-title">${event.name}</div>
            <div class="event-details">📍 ${event.venue}</div>
            <div class="event-details">📅 ${new Date(event.eventDate).toLocaleDateString()}</div>
            <div class="price">₹${event.price}</div>
            <div class="available-seats">✅ ${event.totalSeats - (event.bookedSeats || 0)} seats available</div>
        </div>
    `).join("");
}

loadPastEvents();