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

async function loadEventData() {
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
        
        document.getElementById("eventId").value = event.id;
        document.getElementById("name").value = event.name;
        document.getElementById("description").value = event.description;
        document.getElementById("venue").value = event.venue;
        document.getElementById("totalSeats").value = event.totalSeats;
        document.getElementById("price").value = event.price;
        document.getElementById("category").value = event.category || "";
        
        const eventDate = new Date(event.eventDate);
        const dateStr = eventDate.toISOString().split('T')[0];
        const startTimeStr = event.startTime.split('T')[1].substring(0, 5);
        const endTimeStr = event.endTime.split('T')[1].substring(0, 5);
        
        document.getElementById("eventDate").value = dateStr;
        document.getElementById("startTime").value = startTimeStr;
        document.getElementById("endTime").value = endTimeStr;
        
    } catch (error) {
        showError("Failed to load event data");
        console.error(error);
    }
}

if (document.getElementById("editEventForm")) {
    document.getElementById("editEventForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        
        const eventId = document.getElementById("eventId").value;
        const name = document.getElementById("name").value;
        const description = document.getElementById("description").value;
        const eventDate = document.getElementById("eventDate").value;
        const startTime = document.getElementById("startTime").value;
        const endTime = document.getElementById("endTime").value;
        const venue = document.getElementById("venue").value;
        const totalSeats = parseInt(document.getElementById("totalSeats").value);
        const price = parseFloat(document.getElementById("price").value);
        const category = document.getElementById("category").value;
        
        if (!name || !description || !eventDate || !startTime || !endTime || !venue || !totalSeats || !price) {
            showError("Please fill all required fields");
            return;
        }
        
        const eventDateTime = new Date(`${eventDate}T${startTime}`);
        const endDateTime = new Date(`${eventDate}T${endTime}`);
        
        if (endDateTime <= eventDateTime) {
            showError("End time must be after start time");
            return;
        }
        
        const token = localStorage.getItem("token");
        const organizerId = localStorage.getItem("userId");
        
        const eventData = {
            name: name,
            description: description,
            eventDate: eventDateTime.toISOString(),
            startTime: eventDateTime.toISOString(),
            endTime: endDateTime.toISOString(),
            venue: venue,
            totalSeats: totalSeats,
            price: price,
            category: category
        };
        
        try {
            const response = await fetch(`${API_BASE}/update/${eventId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(eventData)
            });
            
            const data = await response.json();
            
            if (response.ok) {
                alert("✅ Event updated successfully!");
                window.location.href = "dashboard.html";
            } else {
                showError(data.error || data.message || "Failed to update event");
            }
        } catch (error) {
            showError("Server error. Please try again.");
        }
    });
}

loadEventData();