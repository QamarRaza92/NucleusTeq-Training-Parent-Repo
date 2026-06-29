const API_BASE = "http://127.0.0.1:8000";

function getToken(){
    return localStorage.getItem("token");
}

function logout(){
    localStorage.clear();
    window.location.href = "../login.html";
}


function showNotification(message, type = "error") {
    const popup = document.getElementById("errorPopup");
    const popupMessage = document.getElementById("popupMessage");
    const popupContent = document.querySelector(".popup-content");
    
    if (!popup || !popupMessage || !popupContent) return; 

    if (type === "success") {
        popupContent.style.borderLeftColor = "#22c55e"; 
    } else {
        popupContent.style.borderLeftColor = "#ef4444"; 
    }
    
    popupMessage.textContent = message;
    popup.style.display = "block";
    
    setTimeout(() => {
        popup.style.display = "none";
        popupContent.style.borderLeftColor = "#ef4444"; 
    }, 3000);
}


const closeBtn = document.querySelector(".close-btn");
if(closeBtn)
{
    closeBtn.addEventListener(
                            "click",
                            () => {
                                const popup = document.getElementById("errorPopup");
                                if(popup){popup.style.display = "none";}
                            }
                              );
}


if(document.getElementById("registerForm"))
{
    document.getElementById("registerForm").addEventListener(
                                                            "submit",
                                                            async(e) => {
                                                                         e.preventDefault();
                                                                         const name = document.getElementById("name").value;
                                                                         const email = document.getElementById("email").value;
                                                                         const password = document.getElementById("password").value;
                                                                         const phone_number = document.getElementById("phone").value;
                                                                         const city = document.getElementById("city").value;
                                                                         const bio = document.getElementById("bio").value;

                                                                         if (!name || !email || !password || !phone_number || !city)
                                                                         {
                                                                            showNotification("Please fill all fields!");
                                                                            return;
                                                                         }

                                                                         try 
                                                                         {
                                                                            const response = await fetch(
                                                                                                         `${API_BASE}/auth/register`,
                                                                                                         {
                                                                                                            method: "POST",
                                                                                                            headers: {"Content-Type": "application/json"},
                                                                                                            body: JSON.stringify({email,name,password,phone_number,city,bio})
                                                                                                         }
                                                                                                        );
                                                                            const data = await response.json();
                                                                            if (response.ok)
                                                                            {
                                                                                alert("Registration successful! Please login");
                                                                                window.location.href = "/login"
                                                                            }
                                                                            else 
                                                                            {
                                                                                showNotification(data.detail || "Registration failed!", "error");
                                                                            }
                                                                         }
                                                                         catch(error)
                                                                         {
                                                                            showNotification("Internal error", error.detail);
                                                                         }
                                                                        }
                                                           );
}

if (document.getElementById("loginForm"))
{
    document.getElementById("loginForm").addEventListener(
                                "submit",
                                async(e) => {
                                    e.preventDefault();
                                    const email = document.getElementById("email").value;
                                    const password = document.getElementById("password").value;

                                    if(!email || !password)
                                    {
                                        showNotification("Please fill all fields","error");
                                        return;
                                    }
                                    try 
                                    {
                                        const response = await fetch(
                                            `${API_BASE}/auth/login`,
                                            {
                                                method:"POST",
                                                headers:{"Content-Type":"application/json"},
                                                body:JSON.stringify({email,password})
                                            }
                                        );

                                        const data = await response.json();
                                        if(response.ok)
                                        {
                                            alert("login success")
                                            

                                            console.log("Login success")

                                            localStorage.setItem("token",data.access_token);
                                            localStorage.setItem("user_id",data.id);
                                            localStorage.setItem("user_name",data.name);
                                            localStorage.setItem("user_email", data.email);

                                            console.log("Redirecting to:", "/dashboard")
                                            window.location.href = "/dashboard";
                                        }

                                        else
                                        {
                                            showNotification(data.detail || "Login failed", "error")
                                        }
                                    }
                                    catch(error)
                                    {
                                        showNotification("Server error. Please try again.","error");
                                    }
                                }
                                );
}