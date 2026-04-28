const BOOKING_API = "http://localhost:8080/api/bookings";

function getToken() {
    return localStorage.getItem("token");
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

async function loadBookings() {
    const token = getToken();
    if (!token) {
        window.location.href = "../login.html";
        return;
    }

    try {
        const response = await fetch(`${BOOKING_API}/my-bookings-customer`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Failed to load bookings");

        const bookings = await response.json();
        const container = document.getElementById("bookingsList");

        if (!bookings || bookings.length === 0) {
            container.innerHTML = `<div class="empty-state">No bookings found</div>`;
            return;
        }

        container.innerHTML = `
            <table>
                <thead>
                    <tr>
                        <th>Booking ID</th>
                        <th>Event ID</th>
                        <th>Tickets</th>
                        <th>Total Amount</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    ${bookings.map(b => {
                        let statusText = b.status || 'CONFIRMED';
                        let statusClass = 'status-confirmed';
                        
                        if (b.status === 'CANCELLED') {
                            statusText = 'CANCELLED';
                            statusClass = 'status-cancelled';
                        } else if (b.status === 'CANCELLED_BY_ORGANIZER') {
                            statusText = 'CANCELLED BY ORGANIZER';
                            statusClass = 'status-cancelled';
                        }
                        
                        return `
                            <tr>
                                <td>${b.id}</td>
                                <td>${b.eventId}</td>
                                <td>${b.noOfTickets}</td>
                                <td>₹${b.totalAmount}</td>
                                <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                                <td>
                                    ${b.status !== 'CANCELLED' && b.status !== 'CANCELLED_BY_ORGANIZER' ? 
                                        `<button class="cancel-btn" onclick="cancelBooking(${b.id})">Cancel</button>` : 
                                        '<span class="no-action">-</span>'
                                    }
                                </td>
                            </tr>
                        `;
                    }).join("")}
                </tbody>
            </table>
        `;

    } catch (error) {
        document.getElementById("bookingsList").innerHTML = `<div class="empty-state">Failed to load bookings</div>`;
    }
}

async function cancelBooking(bookingId) {
    if (!confirm("Are you sure you want to cancel this booking?")) return;

    const token = getToken();
    try {
        const response = await fetch(`${BOOKING_API}/customer/cancel/${bookingId}`, {
            method: "PUT",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (response.ok) {
            showError("Booking cancelled successfully!");
            loadBookings();
        } else {
           const err = await response.text();
            showError(err || "Failed to cancel booking");
        }
    } catch (error) {
        showError("Server error");
    }
}

loadBookings();