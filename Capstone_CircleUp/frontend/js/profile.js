const token = getToken();

window.onload = loadProfile;

async function loadProfile() {

    try
    {
        const response = await fetch(
            `${API_BASE}/users/dashboard/profile`,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        const data = await response.json();

        if (!response.ok)
        {
            showNotification(data.detail);
            return;
        }

        // Fill profile information received from the dashboard API.
        document.getElementById("name").textContent = data.name;
        document.getElementById("email").textContent = data.email;
        document.getElementById("phone").textContent = data.phone;
        document.getElementById("city").textContent = data.city;
        document.getElementById("bio").textContent = data.bio;

        // Display user activity statistics.
        document.getElementById("created").textContent = data.created;
        document.getElementById("joined").textContent = data.joined;
        document.getElementById("pending").textContent = data.pending;
    }
    catch(error)
    {
        showNotification("Failed to load profile.");
    }
}