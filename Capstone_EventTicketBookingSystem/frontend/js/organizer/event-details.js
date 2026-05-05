const EVENT_API = "http://localhost:8080/api/events";

function getEventIdFromURL() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

async function loadEventDetails() {
    const eventId = getEventIdFromURL();
    if (!eventId) {
        showNotification("No event ID found","error");
        window.location.href = "dashboard.html";
        return;
    }
    
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "../login.html";
        return;
    }
    
    try {
        const response = await fetch(`${EVENT_API}/${eventId}`, {
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
        showNotification("Failed to load event details","error");
        console.error(error);
    }
}

function displayEventDetails(event) {
    const now = new Date();
    const eventDate = new Date(event.eventDate);
     let statusBadge = '';
    if (event.status === 'CANCELLED') {
        statusBadge = '<span class="status-badge status-cancelled"><i class="fa-solid fa-circle-xmark" style="color: #dc3545;"></i> CANCELLED</span>';
    } else if (event.status === 'COMPLETED') {
        statusBadge = '<span class="status-badge status-completed"><i class="fa-solid fa-circle-check" style="color: #28a745;"></i> COMPLETED</span>';
    } else if (eventDate > now) {
        statusBadge = '<span class="status-badge status-active"><i class="fa-solid fa-circle" style="color: #28a745;"></i> UPCOMING</span>';
    } else {
        statusBadge = '<span class="status-badge status-completed"><i class="fa-solid fa-circle-check" style="color: #28a745;"></i> COMPLETED</span>';
    }
    
    const availableSeats = event.totalSeats - (event.bookedSeats || 0);
    
    const container = document.getElementById("eventInfo");
    container.innerHTML = `
        <div class="info-row">
            <div class="info-label"><i class="fa-solid fa-microphone" style="color: #6c5ce7;"></i> Event Name:</div>
            <div class="info-value">${event.name}</div>
        </div>
        <div class="info-row">
            <div class="info-label"><i class="fa-solid fa-align-left" style="color: #636e72;"></i> Description:</div>
            <div class="info-value">${event.description || "-"}</div>
        </div>
        <div class="info-row">
            <div class="info-label"><i class="fa-solid fa-calendar-day" style="color: #0984e3;"></i> Date:</div>
            <div class="info-value">${new Date(event.eventDate).toLocaleDateString()}</div>
        </div>
        <div class="info-row">
            <div class="info-label"><i class="fa-solid fa-clock" style="color: #fdcb6e;"></i> Time:</div>
            <div class="info-value">${event.startTime.split('T')[1].substring(0,5)} - ${event.endTime.split('T')[1].substring(0,5)}</div>
        </div>
        <div class="info-row">
            <div class="info-label"><i class="fa-solid fa-location-dot" style="color: #d63031;"></i> Venue:</div>
            <div class="info-value">${event.venue}</div>
        </div>
        <div class="info-row">
            <div class="info-label"><i class="fa-solid fa-tags" style="color: #00cec9;"></i> Category:</div>
            <div class="info-value">${event.category || "-"}</div>
        </div>
        <div class="info-row">
            <div class="info-label"><i class="fa-solid fa-indian-rupee-sign" style="color: #27ae60;"></i> Price:</div>
            <div class="info-value">₹${event.price}</div>
        </div>
        <div class="info-row">
            <div class="info-label"><i class="fa-solid fa-circle-check" style="color: #2ecc71;"></i> Status:</div>
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
        const response = await fetch(`${EVENT_API}/cancel/${eventId}`, {
            method: "PUT",
            headers: { "Authorization": `Bearer ${token}` }
        });
        
        if (response.ok) {
            alert("Event cancelled successfully!");
            window.location.href = "dashboard.html";
        } else {
            const error = await response.json();
            showNotification(error.message || error.error || "Failed to cancel event","error");
        }
    } catch (error) {
        showNotification("Server error. Please try again.","error");
    }
}

loadEventDetails();