from pydantic import Field, EmailStr, BaseModel, field_validator
import re
class LoginRequest(BaseModel):
    email : EmailStr
    password : str = Field(...,title="password",min_length=8,max_length=15,
                    description="Password must be 8-15 characters long, contain 1 number, and 1 special character.",
                    examples=["wxyz@123"])
    
    @field_validator("password")
    @classmethod
    def validate_password_strength(cls, password: str) -> str:
        if not re.search(r"[0-9]", password):
            raise ValueError("Password must contain at least 1 number.")
        if not re.search(r"[!@#$%^&*(),.?\":{}|<>]", password):
            raise ValueError("Password must contain at least 1 special character.")
        return password
        

class LoginResponse(BaseModel):
    access_token : str 
    token_type : str
    id : int
    name : str 
    email : EmailStr

    class Config:
        from_attributes = True