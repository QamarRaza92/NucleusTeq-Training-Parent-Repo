const token = getToken();
window.onload = loadProfile;
async function loadProfile(){
    const response = await fetch(`${API_BASE}/users/me`,{
        headers:{
            Authorization:`Bearer ${token}`
        }
    });
    const data = await response.json();
    document.getElementById("name").value = data.name;
    document.getElementById("phone_number").value = data.phone_number;
    document.getElementById("bio").value = data.bio || "";
    document.getElementById("city").value = data.city;
}

if (document.getElementById("editProfileForm"))
{
    document.getElementById("editProfileForm").addEventListener(
                                                                "submit",
                                                                async(e) => {
                                                                            e.preventDefault();
                                                                            const name = document.getElementById("name").value;
                                                                            const bio = document.getElementById("bio").value;
                                                                            const phone_number = document.getElementById("phone_number").value;
                                                                            const city = document.getElementById("city").value;

                                                                            if (!name && !bio && !phone_number && !city)
                                                                            {
                                                                                showNotification("Fill at least 1 field to update profile");
                                                                                return;
                                                                            }

                                                                            try
                                                                            {
                                                                                const response = await fetch(
                                                                                                          `${API_BASE}/users/me`,
                                                                                                          {
                                                                                                            method: "PUT",
                                                                                                            headers: {
                                                                                                                      "Content-Type": "application/json",
                                                                                                                      "Authorization": `Bearer ${token}`
                                                                                                                     },
                                                                                                            body: JSON.stringify(
                                                                                                                                    {
                                                                                                                                        name: name || undefined,
                                                                                                                                        bio: bio || undefined,
                                                                                                                                        phone_number: phone_number || undefined,
                                                                                                                                        city: city || undefined

                                                                                                                                    }
                                                                                                                                )
                                                                                                          }
                                                                                                        );

                                                                                const data = await response.json();

                                                                                if(response.ok)
                                                                                {
                                                                                    showNotification("Profile Updated Successfully!","success");
                                                                                    setTimeout(()=>{
                                                                                        window.location.href="/profile";
                                                                                    },1000);
                                                                                }
                                                                                else 
                                                                                {
                                                                                    showNotification(data.detail || "Updation Failed");
                                                                                }
                                                                            }
                                                                            catch(error)
                                                                            {
                                                                                console.error("Error: Profile Updation Failed! ", error);
                                                                                showNotification(error.message || "Network connection failed!");
                                                                            }
                                                                            }
                                                               );
}