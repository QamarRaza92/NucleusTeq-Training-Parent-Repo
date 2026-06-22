const API_BASE = "http://localhost:8081/api/auth";

function getToken() {
    return localStorage.getItem("token");
}

function logout() {
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

function isValidName(name) {
    const nameRegex = /^(?=(.*[A-Za-z]){2,})[A-Za-z\s]+$/;
    if (!nameRegex.test(name)) {
        return { valid: false, message: "Name must contain only alphabets and be at least 2 characters" };
    }
    return { valid: true, message: "" };
}

function isValidEmail(email) {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
    if (!emailRegex.test(email)) {
        return { valid: false, message: "Email must be in format: username@gmail.com" };
    }
    return { valid: true, message: "" };
}

function isValidPhone(phone) {
    const phoneRegex = /^[0-9]{10}$/;
    if (!phoneRegex.test(phone)) {
        return { valid: false, message: "Phone number must be exactly 10 digits" };
    }
    return { valid: true, message: "" };
}

function isValidPassword(password)
{
    if (password.length<8 || password.length>12)
    {
        return {valid:false,message:"Password must be 8-12 characters"};
    }
    if(!/[A-Z]/.test(password))
    {
        return {valid:false,message:"Password must have at least one capital letter"};
    }
    if(!/[!@#$%^&*(),.?":{}|<>]/.test(password))
    {
        return {valid:false,message:"Password must have at least one special character"};
    }
    return {valid:true,message:""};
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
                                            const phone = document.getElementById("phone").value;
                                            const role = document.getElementById("role").value;

                                            if(!name || !email || !password || !phone || !role)
                                            {
                                                showNotification("Please fill all fields");
                                                return;
                                            }

                                            const passwordCheck = isValidPassword(password);
                                            if(!passwordCheck.valid)
                                            {
                                                showNotification(passwordCheck.message);
                                                return;
                                            }

                                            const nameCheck = isValidName(name);
                                            if (!nameCheck.valid) {
                                                showNotification(nameCheck.message);
                                                return;
                                            }

                                            const emailCheck = isValidEmail(email);
                                            if (!emailCheck.valid) {
                                                showNotification(emailCheck.message);
                                                return;
                                            }

                                            const phoneCheck = isValidPhone(phone);
                                            if (!phoneCheck.valid) {
                                                showNotification(phoneCheck.message);
                                                return;
                                            }

                                            try 
                                            {
                                                const response = await fetch(
                                                    `${API_BASE}/register`,
                                                    {
                                                        method:"POST",
                                                        headers:{"Content-Type":"application/json"},
                                                        body:JSON.stringify({name,email,password,phone,role})
                                                    }
                                                                            );

                                                const data = await response.json();

                                                if (response.ok)
                                                {
                                                    alert("Registration Successful! Please login.");
                                                    window.location.href = "login.html";
                                                }
                                                else
                                                {
                                                    showNotification(data.error || "Registration Failed!");
                                                }
                                            }
                                            catch(error)
                                            {
                                                showNotification("Server error. Please try again.");
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
                                            `${API_BASE}/login`,
                                            {
                                                method:"POST",
                                                headers:{"Content-Type":"application/json"},
                                                body:JSON.stringify({email,password})
                                            }
                                        );

                                        const data = await response.json();
                                        console.log(data);
                                        if(response.ok)
                                        {
                                            localStorage.setItem("token",data.token);
                                            localStorage.setItem("email",data.email);
                                            localStorage.setItem("userRole",data.role);
                                            localStorage.setItem("userId", data.id);

                                            if(data.role==="CUSTOMER")
                                            {
                                                window.location.href = "customer/dashboard.html";
                                            }
                                            else if (data.role === "ORGANIZER")
                                            {
                                                window.location.href = "./organizer/dashboard.html";
                                            }
                                            else 
                                            {
                                                showNotification(data.error || "Invalid email or password","error");
                                            }
                                        }
                                    }
                                    catch(error)
                                    {
                                        showNotification("Server error. Please try again.","error");
                                    }
                                }
                                );
}
