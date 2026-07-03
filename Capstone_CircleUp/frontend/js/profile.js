const token = getToken();

window.onload = loadProfile;

async function loadProfile() {

    const response = await fetch(
        `${API_BASE}/users/dashboard/profile`,
        {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }
    );

    const data = await response.json();

    document.getElementById("name").textContent = data.name;
    document.getElementById("email").textContent = data.email;
    document.getElementById("phone").textContent = data.phone;
    document.getElementById("city").textContent = data.city;
    document.getElementById("bio").textContent = data.bio;

    document.getElementById("created").textContent = data.created;
    document.getElementById("joined").textContent = data.joined;
    document.getElementById("pending").textContent = data.pending;
}