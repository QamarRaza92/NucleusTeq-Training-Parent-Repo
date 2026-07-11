const API_BASE = "http://127.0.0.1:8000";

function getToken(){
    return sessionStorage.getItem("token");
}

function logout(){
    sessionStorage.clear();
    window.location.href = "/";
}

//Password must be 8-15 chars long, have at least 1 number and at least one special char
function isValidPassword(password)
{
    if (password.length<8 || password.length>15)
    {
        return {valid:false,message:"Password must be 8-15 characters"};
    }
    if(!/[!@#$%^&*(),.?":{}|<>]/.test(password))
    {
        return {valid:false,message:"Password must have at least one special character"};
    }
    if(!/[0-9]/.test(password))
    {
        return {valid:false,message:"Password must have at least one number"};
    }
    return {valid:true,message:""};
}

//Phone number must valid indian phone number and have length 10 and must start with 6 or 7 or 8 or 9
function isValidIndianNumber(number)
{
     if(!/^[6-9]\d{9}$/.test(number))
    {
        return {valid:false,message:"Number must be valid Indian phone number "};
    }
    return {valid:true, message:""};
}


function showNotification(message, type = "error") {
    const popup = document.getElementById("errorPopup");
    const popupMessage = document.getElementById("popupMessage");
    const popupContent = document.querySelector(".popup-content");
    
    if (!popup || !popupMessage || !popupContent) return; 

    //left border is green when notification is a "success" 
    if (type === "success") {
        popupContent.style.borderLeftColor = "#22c55e"; 
    } else {
        //left border is reddish when notification is an "error" 
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

            const passwordCheck = isValidPassword(password);
            if(!passwordCheck.valid)
            {
                showNotification(passwordCheck.message);
                return;
            }

            const numberCheck = isValidIndianNumber(phone_number);
            if(!numberCheck.valid)
            {
                showNotification(numberCheck.message);
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
                window.location.href = "/";
            }
            else {
                if (Array.isArray(data.detail)) {
                    const messages = data.detail.map(err => {
                        const field = err.loc[err.loc.length - 1]; 
                        return `${field}: ${err.msg}. `;
                    });
                    showNotification(messages.join("\n"), "error");
                } else {
                    showNotification(data.detail || "Registration failed!", "error");
                }
            }
            }
            catch(error)
            {
            showNotification("Internal error: ", "error");
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

        const passwordCheck = isValidPassword(password);
        if(!passwordCheck.valid)
        {
            showNotification(passwordCheck.message);
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
                alert("Login success")

                sessionStorage.setItem("token",data.access_token);
                sessionStorage.setItem("user_id",data.id);
                sessionStorage.setItem("user_name",data.name);
                sessionStorage.setItem("user_email", data.email);
                window.location.href = "/dashboard";
            }
            else {
                if (Array.isArray(data.detail)) {
                    const messages = data.detail.map(err => {
                        const field = err.loc[err.loc.length - 1]; 
                        return `${field}: ${err.msg}. `;
                    });
                    showNotification(messages.join("\n"), "error");
                } else {
                    showNotification(data.detail || "Login failed!", "error");
                }
            }
        }
        catch(error)
        {
            showNotification(error);
        }
    }
    );
}