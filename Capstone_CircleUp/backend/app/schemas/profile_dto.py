from pydantic import BaseModel,Field,EmailStr, field_validator
from typing import Optional

class ProfileUpdateRequest(BaseModel):
    name : Optional[str] = Field(default=None,title="name",examples=["MS Dhoni","Leonel Messi"], min_length=3, max_length=50)
    phone_number : Optional[str] = Field(default=None,title="phone number",examples=["83194173XX"], min_length=3, max_length=10)
    city : Optional[str] = Field(default=None,title="city",examples=["Mumbai"])
    bio : Optional[str] = Field(default=None,title="bio",examples=["Just vibing"])

    @field_validator("city")
    @classmethod
    def validate_city(cls,city_name):
        if city_name is None:
            return city_name
        if city_name not in ['Mumbai', 'New Delhi', 'Bengaluru', 'Indore', 'california', 'Alburquerque',
    'New York', 'Scranton', 'Pune', 'Patna', 'Jaipur', 'Lucknow', 'Hyderabad']:
            raise ValueError("City Not available")
        else:
            return city_name
    
    @field_validator("phone_number")
    @classmethod
    def validate_indian_phone_number(cls, phone_number: str) -> str:
        phone_number = phone_number.strip()
        if not phone_number.isdigit():
            raise ValueError("Phone number must contain only digits.")
        if len(phone_number) != 10:
            raise ValueError("Phone number must be exactly 10 digits.")
        if not phone_number.startswith(("6", "7", "8", "9")):
            raise ValueError("Invalid Indian mobile number.")
        return phone_number


class ProfileResponse(BaseModel):
    id : int 
    name : str 
    email : EmailStr
    phone_number : str 
    city : str 
    bio : Optional[str] = None

    class Config:
        from_attributes = True