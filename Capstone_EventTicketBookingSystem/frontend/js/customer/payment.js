const params = new URLSearchParams(window.location.search);
const eventId = params.get("id");
const tickets = params.get("tickets");
const amount = params.get("amount");

document.getElementById("eventId").textContent = eventId;
document.getElementById("tickets").textContent = tickets;
document.getElementById("amount").textContent = amount * tickets;

async function processPayment() {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    const bookingData = {
        eventId: parseInt(eventId),
        noOfTickets: parseInt(tickets),
        totalAmount: parseFloat(amount * tickets)
    };

    try {
        const response = await fetch("http://localhost:8080/api/bookings/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(bookingData)
        });

        if (response.ok) {
            alert("Booking Successful!");
            showNotification("✅ Payment successful! Booking confirmed.","success");
            window.location.href = "my-bookings.html";
        } else {
            const error = await response.json();
            showNotification("❌ Payment failed: " + (error.message || "Unknown error"),"error");
        }
    } catch (error) {
        showNotification("Server error. Please try again.","error");
    }
}

fetch(`http://localhost:8080/api/events/${eventId}`, {
    headers: { "Authorization": `Bearer ${localStorage.getItem("token")}` }
}).then(res => res.json()).then(event => {
    const available = event.totalSeats - (event.bookedSeats || 0);
    if (tickets > available) {
        showNotification(`Only ${available} seats available!`,"error");
        document.getElementById("payBtn").disabled = true;
    } else {
        document.getElementById("payBtn").onclick = processPayment;
    }
}).catch(err => {
        showNotification("Failed to verify event details.", "error");
    });
