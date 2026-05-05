const EVENT_API = "http://localhost:8080/api/events";

function getEventIdFromURL() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

async function loadEventData() {
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
        showNotification("Failed to load event data","error");
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
            showNotification("Please fill all required fields","error");
            return;
        }
        
        const eventDateTime = new Date(`${eventDate}T${startTime}`);
        const endDateTime = new Date(`${eventDate}T${endTime}`);
        
        if (endDateTime <= eventDateTime) {
            showNotification("End time must be after start time","error");
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
            const response = await fetch(`${EVENT_API}/update/${eventId}`, {
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
                showNotification("✅ Event updated successfully!","success");
                window.location.href = "dashboard.html";
            } else {
                showNotification(data.error || data.message || "Failed to update event","error");
            }
        } catch (error) {
            showNotification("Server error. Please try again.","error");
        }
    });
}

loadEventData();