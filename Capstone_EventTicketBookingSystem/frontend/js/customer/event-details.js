const EVENT_API = "http://localhost:8080/api/events";

function getEventId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

function goBack() {
    window.history.back();
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

async function loadEventDetails() {
    const eventId = getEventId();
    if (!eventId) {
        showError("No event found");
        goBack();
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
        const orgResponse = await fetch(`http://localhost:8081/api/auth/user/${event.organizerId}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        
        const organizer = orgResponse.ok ? await orgResponse.json() : null;
        console.log("Organizer data:", organizer);
        displayEventDetails(event,organizer);

        const availableSeats = event.totalSeats - (event.bookedSeats || 0);
        if (availableSeats > 0) {
            document.getElementById("bookingSection").style.display = "block";
            
            const ticketCount = document.getElementById("ticketCount");
            const totalSpan = document.getElementById("totalAmount");
            
            ticketCount.addEventListener("input", () => {
                const count = parseInt(ticketCount.value) || 1;
                totalSpan.textContent = count * event.price;
            });
            totalSpan.textContent = event.price;
            
            document.getElementById("bookNowBtn").onclick = () => {
                window.location.href = `payment.html?id=${event.id}&tickets=${ticketCount.value}&amount=${event.price}`;
            };
        }

    } catch (error) {
        showError("Failed to load event details");
    }
}

function displayEventDetails(event,organizer) {
    const availableSeats = event.totalSeats - (event.bookedSeats || 0);
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

    document.getElementById("eventDetails").innerHTML = `
        <div class="event-name">${event.name}</div>
        <div class="event-description">${event.description || "No description available"}</div>
        <div class="info-row">
            <div class="info-label">Date:</div>
            <div class="info-value">${new Date(event.eventDate).toLocaleDateString()}</div>
        </div>
        <div class="info-row">
            <div class="info-label">Time:</div>
            <div class="info-value">${event.startTime?.split('T')[1]?.substring(0,5) || 'N/A'} - ${event.endTime?.split('T')[1]?.substring(0,5) || 'N/A'}</div>
        </div>
        <div class="info-row">
            <div class="info-label">Venue:</div>
            <div class="info-value">${event.venue}</div>
        </div>
        <div class="info-row">
            <div class="info-label">Category:</div>
            <div class="info-value">${event.category || "General"}</div>
        </div>
        <div class="info-row">
            <div class="info-label">Price:</div>
            <div class="info-value">₹${event.price} per ticket</div>
        </div>
        <div class="info-row">
            <div class="info-label">📌 Status:</div>
            <div class="info-value">${statusBadge}</div>
        </div>
        <div class="info-row organizer-info">
            <div class="info-label">🎪 Organized by:</div>
            <div class="info-value">
                <strong>${organizer?.name || 'Organizer'}</strong>
                ${organizer?.email ? `<br><span class="organizer-email">${organizer.email}</span>` : ''}
            </div>
        </div>
        <div class="available-seats">✅ ${availableSeats} seats available</div>
    `;
}

loadEventDetails();