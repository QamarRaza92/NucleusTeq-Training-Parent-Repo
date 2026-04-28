const EVENT_API = "http://localhost:8080/api/events";
const BOOKING_API = "http://localhost:8080/api/bookings";
function logout() {
    localStorage.clear();
    window.location.href = "../login.html";
}

function getToken() {
    return localStorage.getItem("token");
}

function updateDateTime() {
    const now = new Date();
    const dateTimeStr = now.toLocaleString('en-US', { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
    const dateTimeElement = document.getElementById("currentDateTime");
    if (dateTimeElement) {
        dateTimeElement.textContent = dateTimeStr;
    }
}

setInterval(updateDateTime, 1000);
updateDateTime();

async function loadOrganizerProfile() {
    const email = localStorage.getItem("email");
    const name = email ? email.split('@')[0] : "Organizer";
    
    document.getElementById("orgName").textContent = name.charAt(0).toUpperCase() + name.slice(1);
    document.getElementById("welcomeName").textContent = name.charAt(0).toUpperCase() + name.slice(1);
    document.getElementById("orgEmail").textContent = email || "organizer@example.com";
    document.getElementById("avatar").textContent = name.charAt(0).toUpperCase();
}

async function loadEvents() {
    const token = getToken();
    if (!token) {
        window.location.href = "../login.html";
        return;
    }

    try {
        const organizerId = localStorage.getItem("userId");
        const response = await fetch(`${EVENT_API}/organizer/${organizerId}`, {
            headers: {"Content-Type": "application/json", "Authorization": `Bearer ${token}`}
        });
        
        if (!response.ok) {
            const text = await response.text();
            console.log("Backend error:", text);
            throw new Error("Failed to fetch events");
        }
        
        const events = await response.json();
        
        const container = document.getElementById("eventsList");
        if (!events || events.length === 0) {
            container.innerHTML = `<div class="empty-state">
                <p>No events yet</p>
                <a href="create-event.html" class="create-event-btn">Create your first event</a>
            </div>`;
            return;
        }
        
        const now = new Date();
        let ongoingCount = 0;
        let completedCount = 0;
        let totalBookings = 0;
        let totalRevenue = 0;
        
        container.innerHTML = events.map(event => {
            const eventDate = new Date(event.eventDate);
            const isOngoing = eventDate > now;
            const isCompleted = eventDate < now;
            
            if (isOngoing) ongoingCount++;
            if (isCompleted) completedCount++;
            totalBookings += event.bookedSeats || 0;
            totalRevenue += (event.bookedSeats || 0) * (event.price || 0);
            let statusBadge = '';
            if (event.status === 'CANCELLED') {
                statusBadge = '<span class="status-badge status-cancelled">❌ CANCELLED</span>';
            } else if (event.status === 'COMPLETED') {
                statusBadge = '<span class="status-badge status-completed">✅ COMPLETED</span>';
            } else if (isOngoing) {
                statusBadge = '<span class="status-badge status-active">🟢 ACTIVE</span>';
            } else if (isCompleted) {
                statusBadge = '<span class="status-badge status-completed">✅ COMPLETED</span>';
            }
            return `
                <div class="event-card">
                    <div class="event-title">${event.name}</div>
                    <div class="event-details">
                        <i class="material-icons">location_on</i> ${event.venue}
                    </div>
                    <div class="event-details">
                        <i class="material-icons">event</i> ${new Date(event.eventDate).toLocaleDateString()}
                    </div>
                    <div class="seat-info">
                        <span>🎫 Total: ${event.totalSeats}</span>
                        <span class="seat-booked">📌 Booked: ${event.bookedSeats || 0}</span>
                        <span class="seat-available">✅ Available: ${event.totalSeats - (event.bookedSeats || 0)}</span>
                    </div>
                    <div class="event-status">${statusBadge}</div>
                    <div class="event-actions">
                        <a href="edit-event.html?id=${event.id}" class="edit-btn">Edit</a>
                        <button onclick="cancelEvent(${event.id})" class="delete-btn">Cancel</button>
                        <a href="event-details.html?id=${event.id}" class="view-btn">View</a>
                    </div>
                </div>
            `;
        }).join("");
        
        document.getElementById("ongoingEventsCount").textContent = ongoingCount;
        document.getElementById("completedEventsCount").textContent = completedCount;
        document.getElementById("totalBookingsCount").textContent = totalBookings;
        document.getElementById("revenueCount").textContent = `₹${totalRevenue.toLocaleString()}`;
        document.getElementById("totalEvents").textContent = events.length;
        document.getElementById("activeEvents").textContent = ongoingCount;
        
    } catch (error) {
        console.error("Error loading events:", error);
        showError("Failed to load events");
    }
}

async function cancelEvent(eventId) {
    if (!confirm("Are you sure you want to cancel this event? This action cannot be undone.")) return;
    
    const token = getToken();
    try {
        const response = await fetch(`${EVENT_API}/cancel/${eventId}`, {
            method: "PUT",  
            headers: {"Content-Type": "application/json", "Authorization": `Bearer ${token}`}
        });
        
        if (response.ok) {
            showError("Event cancelled successfully!", "success");
            loadEvents();
        } else {
            const error = await response.json();
            showError(error.error || "Failed to cancel event");
        }
    } catch (error) {
        showError("Server error. Please try again.");
    }
}

function showError(message, type = "error") {
    const popup = document.getElementById("errorPopup");
    const popupMessage = document.getElementById("popupMessage");
    const popupContent = document.querySelector(".popup-content");
    
    if (type === "success") {
        popupContent.style.borderLeftColor = "#22c55e";
    } else {
        popupContent.style.borderLeftColor = "#ef4444";
    }
    
    popupMessage.textContent = message;
    popup.style.display = "block";
    
    setTimeout(() => {
        popup.style.display = "none";
        popupContent.style.borderLeftColor = "#ef4444";
    }, 3000);
}

loadOrganizerProfile();
loadEvents();