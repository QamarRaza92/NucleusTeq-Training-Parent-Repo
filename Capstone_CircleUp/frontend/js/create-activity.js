const token = getToken();

if (document.getElementById("createActivityForm"))
{
  document.getElementById("createActivityForm").addEventListener("submit", 
                                                                async(e) => {
                                                                             e.preventDefault();
                                                                             const title = document.getElementById("title").value;
                                                                             const description = document.getElementById("description").value;
                                                                             const category = document.getElementById("category").value;
                                                                             const location = document.getElementById("location").value;
                                                                             const date = document.getElementById("date").value;
                                                                             const time = document.getElementById("time").value;
                                                                             const max_participants = document.getElementById("max_participants").value;

                                                                             if(!title || !category || !location || !date || !time || !max_participants)
                                                                            {
                                                                                showNotification("Please fill all required fields");
                                                                                return;
                                                                            }
                                                                            try
                                                                            {
                                                                                const response = await fetch(
                                                                                                            `${API_BASE}/activity`,
                                                                                                            {
                                                                                                                "method": "POST",
                                                                                                                headers: {
                                                                                                                        "Content-Type": "application/json",
                                                                                                                        "Authorization": `Bearer ${token}`
                                                                                                                        },
                                                                                                                body: JSON.stringify(
                                                                                                                                    {
                                                                                                                                        title: title,
                                                                                                                                        description: description,
                                                                                                                                        category: category,
                                                                                                                                        location: location,
                                                                                                                                        date: date,
                                                                                                                                        time: time,
                                                                                                                                        max_participants: Number(max_participants)

                                                                                                                                    }
                                                                                                                                    )

                                                                                                            }
                                                                                                            );
                                                                                const data = await response.json();
                                                                                if (response.ok)
                                                                                {
                                                                                    alert("Event Created Successfully 🎉!");
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