const token = getToken();

window.onload = loadActivity;
const activityId = window.location.pathname.split("/").pop();
async function loadActivity(){
    const response = await fetch(`${API_BASE}/activity/${activityId}`,
                                 {
                                 headers:{
                                            Authorization:`Bearer ${getToken()}`
                                         }
                                 }
                                );

    if(!response.ok)
    {
        showNotification(data.detail);
        return;
    }   

    const data = await response.json();
    document.getElementById("title").value = data.title;
    document.getElementById("description").value = data.description;
    document.getElementById("category").value = data.category;
    document.getElementById("location").value = data.location;
    document.getElementById("date").value = data.date;
    document.getElementById("time").value = data.time;
    document.getElementById("max_participants").value = data.max_participants;
}

if (document.getElementById("editActivityForm"))
{
  document.getElementById("editActivityForm").addEventListener("submit", 
                                                                async(e) => {
                                                                             e.preventDefault();
                                                                             const title = document.getElementById("title").value;
                                                                             const description = document.getElementById("description").value;
                                                                             const category = document.getElementById("category").value;
                                                                             const location = document.getElementById("location").value;
                                                                             const date = document.getElementById("date").value;
                                                                             const time = document.getElementById("time").value;
                                                                             const max_participants = document.getElementById("max_participants").value;

                                                                             if(!title && !category && !location && !date && !time && !max_participants)
                                                                            {
                                                                                showNotification("Please fill all required fields");
                                                                                return;
                                                                            }
                                                                            try
                                                                            {
                                                                                const response = await fetch(
                                                                                                            `${API_BASE}/activity/${activityId}`,
                                                                                                            {
                                                                                                                "method": "PUT",
                                                                                                                headers: {
                                                                                                                        "Content-Type": "application/json",
                                                                                                                        "Authorization": `Bearer ${token}`
                                                                                                                        },
                                                                                                                body: JSON.stringify(
                                                                                                                                    {
                                                                                                                                        title: title || undefined,
                                                                                                                                        description: description || undefined,
                                                                                                                                        category: category || undefined,
                                                                                                                                        location: location || undefined,
                                                                                                                                        date: date || undefined,
                                                                                                                                        time: time || undefined,
                                                                                                                                        max_participants: Number(max_participants) || undefined
                                                                                                                                    }
                                                                                                                                    )

                                                                                                            }
                                                                                                            );
                                                                                const data     = await response.json();
                                                                                if (response.ok)
                                                                                {
                                                                                    alert("Activity Updated Successfully 🎉!");
                                                                                    window.location.href="/dashboard";
                                                                                }
                                                                                else
                                                                                {
                                                                                    if (data.detail && Array.isArray(data.detail))
                                                                                    {
                                                                                        const errorMessages = data.detail.map(err => err.msg).join(", ");
                                                                                        showNotification(errorMessages);
                                                                                    }
                                                                                    else if (data.detail) 
                                                                                    {
                                                                                        showNotification(data.detail);
                                                                                    }
                                                                                    else 
                                                                                    {
                                                                                        showNotification("Something went wrong. Please try again.");
                                                                                    }
                                                                                }
                                                                            }
                                                                            catch(error)
                                                                            {
                                                                                showNotification("Network error. Please check your connection.");
                                                                            }
                                                                            }
                                                               );   
}