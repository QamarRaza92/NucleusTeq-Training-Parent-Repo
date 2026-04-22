const API_BASE = "http://localhost:8081/api/auth";

function showError(message)
{
    const popup = document.getElementById("errorPopup");
    const popupMessage = document.getElementById("popupMessage");

    if (popup && popupMessage)
    {
        popupMessage.textContent = message;
        popup.style.display = "block";

        setTimeout(
                    () => {popup.style.display = "none";},
                    3000
                  );       
    }
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
                                                showError("Please fill all fields");
                                                return;
                                            }

                                            const passwordCheck = isValidPassword(password);
                                            if(!passwordCheck.valid)
                                            {
                                                showError(passwordCheck.message);
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
                                                    showError(data.error || "Registration Failed!");
                                                }
                                            }
                                            catch(error)
                                            {
                                                showError("Server error. Please try again.");
                                            }
                                            
                                            }
                                            );

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
                                        showError("Please fill all fields");
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
                                        if(response.ok)
                                        {
                                            localStorage.setItem("token",data.token);
                                            localStorage.setItem("email",data.email);
                                            localStorage.setItem("userRole",data.role);

                                            if(data.role=="CUSTOMER")
                                            {
                                                window.location.href = "customer/dashboard.html";
                                            }
                                            else if (data.role == "ORGANIZER")
                                            {
                                                window.location.href = "organizer/dashboard.html";
                                            }
                                            else 
                                            {
                                                showError(data.error || "Invalid email or password");
                                            }
                                        }
                                    }
                                    catch(error)
                                    {
                                        showError("Server error. Please try again.");
                                    }
                                }
                                );
}
}