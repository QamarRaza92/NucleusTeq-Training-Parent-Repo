const API_BASE = "http://localhost:8080/api/events";

function getEventIdFromURL() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

function showError(message) {
    const popup = document.getElementById("errorPopup");
    const popupMessage = document.getElementById("popupMessage");
    
    if (popup && popupMessage) {
        popupMessage.textContent = message;
        popup.style.display = "block";
        setTimeout(() => {
            popup.style.display = "none";
        }, 3000);
    }
}

const closeBtn = document.querySelector(".close-btn");
if (closeBtn) {
    closeBtn.addEventListener("click", () => {
        document.getElementById("errorPopup").style.display = "none";
    });
}

async function loadEventDetails() {
    const eventId = getEventIdFromURL();
    if (!eventId) {
        showError("No event ID found");
        window.location.href = "dashboard.html";
        return;
    }
    
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "../login.html";
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/${eventId}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        
        if (!response.ok) throw new Error("Failed to load event");
        
        const event = await response.json();
        displayEventDetails(event);
        
        document.getElementById("actionButtons").style.display = "flex";
        
        const editLink = document.getElementById("editLink");
        editLink.href = `edit-event.html?id=${event.id}`;
        
        document.getElementById("cancelBtn").onclick = () => cancelEvent(event.id);
        
    } catch (error) {
        showError("Failed to load event details");
        console.error(error);
    }
}

function displayEventDetails(event) {
    const now = new Date();
    const eventDate = new Date(event.eventDate);
     let statusBadge = '';
    if (event.status === 'CANCELLED') {
        statusBadge = '<span class="status-badge status-cancelled">❌ CANCELLED</span>';
    } else if (event.status === 'COMPLETED') {
        statusBadge = '<span class="status-badge status-completed">✅ COMPLETED</span>';
    } else if (eventDate > now) {
        statusBadge = '<span class="status-badge status-active">🟢 UPCOMING</span>';
    } else {
        statusBadge = '<span class="status-badge status-completed">✅ COMPLETED</span>';
    }
    
    const availableSeats = event.totalSeats - (event.bookedSeats || 0);
    
    const container = document.getElementById("eventInfo");
    container.innerHTML = `
        <div class="info-row">
            <div class="info-label">Event Name:</div>
            <div class="info-value">${event.name}</div>
        </div>
        <div class="info-row">
            <div class="info-label">Description:</div>
            <div class="info-value">${event.description || "-"}</div>
        </div>
        <div class="info-row">
            <div class="info-label">Date:</div>
            <div class="info-value">${new Date(event.eventDate).toLocaleDateString()}</div>
        </div>
        <div class="info-row">
            <div class="info-label">Time:</div>
            <div class="info-value">${event.startTime.split('T')[1].substring(0,5)} - ${event.endTime.split('T')[1].substring(0,5)}</div>
        </div>
        <div class="info-row">
            <div class="info-label">Venue:</div>
            <div class="info-value">${event.venue}</div>
        </div>
        <div class="info-row">
            <div class="info-label">Category:</div>
            <div class="info-value">${event.category || "-"}</div>
        </div>
        <div class="info-row">
            <div class="info-label">Price:</div>
            <div class="info-value">₹${event.price}</div>
        </div>
        <div class="info-row">
            <div class="info-label">Status:</div>
            <div class="info-value">${statusBadge}</div>
        </div>
        
        <div class="seat-stats">
            <div class="seat-stat total-seats">
                <div class="number">${event.totalSeats}</div>
                <div class="label">Total Seats</div>
            </div>
            <div class="seat-stat booked-seats">
                <div class="number">${event.bookedSeats || 0}</div>
                <div class="label">Booked</div>
            </div>
            <div class="seat-stat available-seats">
                <div class="number">${availableSeats}</div>
                <div class="label">Available</div>
            </div>
        </div>
    `;
}

async function cancelEvent(eventId) {
    if (!confirm("Are you sure you want to cancel this event? All bookings will be cancelled.")) return;
    
    const token = localStorage.getItem("token");
    
    try {
        const response = await fetch(`${API_BASE}/cancel/${eventId}`, {
            method: "PUT",
            headers: { "Authorization": `Bearer ${token}` }
        });
        
        if (response.ok) {
            alert("Event cancelled successfully!");
            window.location.href = "dashboard.html";
        } else {
            const error = await response.json();
            showError(error.error || "Failed to cancel event");
        }
    } catch (error) {
        showError("Server error. Please try again.");
    }
}

loadEventDetails();