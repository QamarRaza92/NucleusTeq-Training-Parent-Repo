const BOOKING_API = "http://localhost:8080/api/bookings";

function getToken() {
    return localStorage.getItem("token");
}

async function loadBookings() {
    const token = getToken();

    if (!token) {
        window.location.href = "dashboard.html";
        return;
    }

    try {
        const response = await fetch(`${BOOKING_API}/my-bookings-organizer`, {
            headers: { "Authorization": `Bearer ${token.trim()}` }
        });

        const container = document.getElementById("bookingsList");

        if (!response.ok || response.status === 204) {
            showEmptyState(container);
            return;
        }

        const bookings = await response.json();

        if (!bookings || bookings.length === 0) {
            showEmptyState(container);
        } else {
            renderTable(container, bookings);
        }

    } catch (error) {
        console.error(error);
        document.getElementById("bookingsList").innerHTML = `
            <div class="empty-state">
                <span class="empty-icon">⚠️</span>
                <p>Could not load bookings</p>
            </div>`;
    }
}

function showEmptyState(container) {
    container.innerHTML = `
        <div class="empty-state">
            <div class="empty-icon">📭</div>
            <h3>No Bookings Yet</h3>
        </div>
    `;
}

function renderTable(container, bookings) {
    container.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>Booking ID</th>
                    <th>Event ID</th>
                    <th>Customer</th>
                    <th>Email</th>  
                    <th>Tickets</th>
                    <th>Amount</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                ${bookings.map(b => {
                    const status = b.status;

                    let badge = "✅ Confirmed";
                    if (status === "CANCELLED") badge = "❌ Cancelled";
                    if (status === "CANCELLED_BY_ORGANIZER") badge = "🚫 Cancelled by Organizer";

                    return `
                        <tr>
                            <td>${b.id}</td>
                            <td>${b.eventId}</td>
                            <td>${b.customerName || 'N/A'}</td> 
                            <td>${b.customerEmail || 'N/A'}</td>
                            <td>${b.noOfTickets}</td>
                            <td>₹${b.totalAmount}</td>
                            <td><span class="status-badge">${badge}</span></td>
                        </tr>
                    `;
                }).join("")}
            </tbody>
        </table>
    `;
}

loadBookings();