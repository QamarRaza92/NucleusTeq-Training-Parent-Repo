const activityId = window.location.pathname.split("/").pop();
window.onload = () => loadActivity();

async function loadActivity(){
    const response = await fetch(`${API_BASE}/activity/${activityId}`,
                                {
                                    headers:{
                                             Authorization:`Bearer ${getToken()}`
                                            }
                                });

    const a = await response.json();
    console.log(response.status);
    console.log(a);

    if(!response.ok){
        showNotification(a.detail);
        return;
    }
    document.getElementById("activityTitle").innerHTML =`<i class="fa-solid fa-calendar-days"></i> ${a.title}`;
    document.getElementById("activityCategory").innerText = a.category;
    document.getElementById("activityStatus").innerText = a.status;
    document.getElementById("activityStatus").classList.add(`status-${a.status.toLowerCase()}`);
    document.getElementById("activityLocation").innerText = a.location;
    document.getElementById("activityDate").innerText = a.date;
    document.getElementById("activityTime").innerText = a.time.substring(0,5);
    document.getElementById("activitySeats").innerText = a.max_participants;
    document.getElementById("activityDescription").innerHTML =`<b>Description</b><br><br>${a.description}`;
    document.getElementById("organizerPhone").innerText = a.organizer_phone || "Hidden";
    const buttons = document.getElementById("actionButtons");
    buttons.innerHTML = `
        <button class="btn-back-detail"
         onclick="history.back()">
            <i class="fa-solid fa-arrow-left"></i> Back
        </button>
    `;

    if(sessionStorage.getItem("user_id")==a.organizer_id){
        const disableActions = a.status === "COMPLETED" || a.status === "CANCELLED"
        buttons.innerHTML += `
            <button class="btn-edit-detail"
                    ${disableActions
                        ? "disabled"
                        : `onclick="window.location.href='/edit-activity/${a.id}'"`
                    }>
                <i class="fa-solid fa-pen"></i> Edit
            </button>

            <button class="btn-cancel-detail"
                        ${disableActions
                            ? "disabled"
                            : `onclick="cancelActivity(${a.id})"`
                        }>
                <i class="fa-solid fa-ban"></i> Cancel
            </button>
        `;
        loadRequests();
    }
    else if(a.status=="OPEN"){
        buttons.innerHTML += `
            <button class="btn-join-detail"
            onclick="joinActivity(${a.id})">
                <i class="fa-solid fa-user-plus"></i> Join Activity
            </button>
        `;
    }

}

async function joinActivity(id){
    const response = await fetch(
        `${API_BASE}/activities/${id}/requests`,
        {
            method:"POST",
            headers:{
                Authorization:`Bearer ${getToken()}`
            }
        }
    );
    const data = await response.json();
    if(response.ok)
        showNotification("Request Sent","success");
    else
        showNotification(data.detail);

}

async function cancelActivity(id){
    if(!confirm("Cancel this activity?")) return;
    const response = await fetch(
        `${API_BASE}/activity/${id}`,
        {
            method:"DELETE",
            headers:{
                Authorization:`Bearer ${getToken()}`
            }
        }
    );
    const data = await response.json();
    if(response.ok){
        showNotification(data.message,"success");
        setTimeout(()=>{
            history.back();
        },2000);
    }
    else{
        showNotification(data.detail);
    }
}

async function loadRequests(){
    const response = await fetch(
        `${API_BASE}/activities/${activityId}/requests`,
        {
            headers:{
                Authorization:`Bearer ${getToken()}`
            }
        }
    );
    const requests = await response.json();
    if(!response.ok || requests.length==0)
        return;
    let html = `
    <div class="requests-card">
        <h2>Pending Requests</h2>
    `;
    requests.forEach(r=>{
        html += `
        <div class="request">
            <span>
                <i class="fa-solid fa-user"></i>
                ${r.participant_name}
            </span>
            <div class="request-buttons">
                <button class="approve-btn"
                onclick="updateRequest(${r.id},'approve')">
                Approve
                </button>
                <button class="reject-btn"
                onclick="updateRequest(${r.id},'reject')">
                Reject
                </button>
            </div>
        </div>
        `;
    });
    html += "</div>";
    document.getElementById("requestsSection").innerHTML = html;
}

async function updateRequest(requestId,type){
    const response = await fetch(
        `${API_BASE}/activities/${activityId}/requests/${requestId}/${type}`,
        {
            method:"PUT",
            headers:{
                Authorization:`Bearer ${getToken()}`
            }
        }
    );
    const data = await response.json();
    if(response.ok){
        showNotification(`Request ${type}d`,"success");
        loadRequests();
    }
    else{
        showNotification(data.detail);
    }
}