const EVENT_API = "http://localhost:8080/api/events";

if (document.getElementById("createEventForm")) {
    document.getElementById("createEventForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        
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
        
        let eventDateTime = new Date(`${eventDate}T${startTime}`);
        let endDateTime = new Date(`${eventDate}T${endTime}`);

        if (endDateTime <= eventDateTime) {
            showNotification("End time must be after start time","error");
            return;
        }
        
        const token = localStorage.getItem("token");
        const organizerId = localStorage.getItem("userId");
        
        if (!token || !organizerId) {
            showNotification("Please login again","error");
            window.location.href = "../login.html";
            return;
        }
        
        const eventData = {
            name: name,
            description: description,
            eventDate: eventDateTime,
            startTime: eventDateTime,
            endTime: endDateTime,
            venue: venue,
            totalSeats: totalSeats,
            price: price,
            category: category
        };
        
        try {
            const response = await fetch(`${EVENT_API}/create`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(eventData)
            });
            
            const data = await response.json();
            
            if (response.ok) {
                alert("🎉 Event created successfully!");
                window.location.href = "dashboard.html";
            } else {
                showNotification(data.error || data.message || "Failed to create event","error");
            }
        } catch (error) {
            showNotification("Server error. Please try again.","error");
        }
    });
}