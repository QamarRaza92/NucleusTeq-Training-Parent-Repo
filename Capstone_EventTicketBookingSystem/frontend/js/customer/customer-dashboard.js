const EVENT_API  = "http://localhost:8080/api/events";
 
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

async function loadCustomerProfile() {
    const email = localStorage.getItem("email");
    const name = email ? email.split('@')[0] : "Customer";
    
    document.getElementById("userName").textContent = name.charAt(0).toUpperCase() + name.slice(1);
    document.getElementById("welcomeName").textContent = name.charAt(0).toUpperCase() + name.slice(1);
    document.getElementById("userEmail").textContent = email || "customer@example.com";
    document.getElementById("avatar").textContent = name.charAt(0).toUpperCase();
}

async function loadEvents() {
    const token = getToken();
    if (!token) {
        window.location.href = "../login.html";
        return;
    }

    try {
        const response = await fetch(`${EVENT_API}/customer-events?status=ACTIVE`, {
            headers: { "Authorization": `Bearer ${token.trim()}` }
        });
        
        if (!response.ok) throw new Error("Failed to fetch events");
        
        let events = await response.json();
        
        const searchInput = document.getElementById("searchInput");
        if (searchInput) {
            searchInput.oninput = (e) => {
                const filtered = events.filter(event => 
                    event.name.toLowerCase().includes(e.target.value.toLowerCase())
                );
                displayEvents(filtered);
            };
        }
        
        displayEvents(events);
        
    } catch (error) {
        console.error("Error loading events:", error);
        document.getElementById("eventsList").innerHTML = `<div class="empty-state">Failed to load events</div>`;
    }
}

function displayEvents(events) {
    const container = document.getElementById("eventsList");
    
    if (!events || events.length === 0) {
        container.innerHTML = `<div class="empty-state">No upcoming events found</div>`;
        return;
    }
    
    container.innerHTML = events.map(event => {
        const availableSeats = event.totalSeats - (event.bookedSeats || 0);
        return `
            <div class="event-card" onclick="goToEventDetails(${event.id})">
                <div class="event-title">${event.name}</div>
                <div class="event-details">📍 ${event.venue}</div>
                <div class="event-details">📅 ${new Date(event.eventDate).toLocaleDateString()}</div>
                <div class="price">₹${event.price}</div>
                <div class="available-seats">✅ ${availableSeats} seats available</div>
                <button class="book-btn" onclick="event.stopPropagation(); bookEvent(${event.id})">Book Now</button>
            </div>
        `;
    }).join("");
}

function goToEventDetails(eventId) {
    window.location.href = `event-details.html?id=${eventId}`;
}

function bookEvent(eventId) {
    window.location.href = `event-details.html?id=${eventId}`;
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


loadCustomerProfile();
loadEvents();