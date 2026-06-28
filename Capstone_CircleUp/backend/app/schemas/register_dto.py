from pydantic import BaseModel,Field,EmailStr, field_validator
from typing import Optional
import re 

class RegisterRequest(BaseModel):
    name: str = Field(...,title="name", description="Name to login",examples=["Qamar Raza","Chandler Bing","Michael Scott"])
    email: EmailStr
    password: str = Field(...,title="password",min_length=8,max_length=15,
                    description="Password must be 8-15 characters long, contain 1 number, and 1 special character.",
                    examples=["wxyz@123"])
    phone_number: str = Field(...,title="phone",description="user's phone number")
    city: str = Field(...,title="city",description="user's city",examples=["Indore","New Delhi","Mumbai"])
    bio: Optional[str] = Field(default=None,title="bio",description="user bio")

    @field_validator("city")
    @classmethod
    def validate_city(cls, city_name):
        if city_name not in ['Mumbai', 'New Delhi', 'Bengaluru', 'Indore', 'california', 'Alburquerque',
    'New York', 'Scranton', 'Pune', 'Patna', 'Jaipur', 'Lucknow', 'Hyderabad']:
            raise ValueError("City Not available")
        else:
            return city_name
        
    @field_validator("password")
    @classmethod
    def validate_password_strength(cls, password: str) -> str:
        if not re.search(r"[0-9]", password):
            raise ValueError("Password must contain at least 1 number.")
        if not re.search(r"[!@#$%^&*(),.?\":{}|<>]", password):
            raise ValueError("Password must contain at least 1 special character.")
        return password
    

class RegisterResponse(BaseModel):
    id: int = Field(...,gt=0,title="User Id",description="Id of user in table",examples=[1,28,12])
    name: str = Field(...,title="name", description="Name to login",examples=["Qamar Raza","Chandler Bing","Michael Scott"])
    email: EmailStr
    phone_number: str = Field(...,title="phone",description="user's phone number")
    city: str = Field(...,title="city",description="user's city",examples=["Indore","New Delhi","Mumbai"])
    bio: Optional[str] = Field(default=None,title="bio",description="user bio")

    class Config:
        from_attributes = True