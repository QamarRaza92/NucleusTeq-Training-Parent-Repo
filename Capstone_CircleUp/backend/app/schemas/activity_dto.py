from pydantic import Field, field_validator, BaseModel, ConfigDict
from typing import Optional
import datetime
from app.enums.activity_catergory_enum import ActivityCategoryEnum
from app.enums.activity_status_enum import ActivityStatusEnum

ALLOWED_CITIES = {
    'Mumbai', 'New Delhi', 'Bengaluru', 'Indore', 'california', 'Alburquerque',
    'New York', 'Scranton', 'Pune', 'Patna', 'Jaipur', 'Lucknow', 'Hyderabad'
}

class ActivityRequest(BaseModel):
    title: str = Field(..., examples=['Cricket Match'], min_length=3, max_length=50)
    description: Optional[str] = None
    category: ActivityCategoryEnum
    location: str = Field(..., examples=['Indore'])
    date: datetime.date 
    time: datetime.time
    max_participants: int = Field(..., gt=0, description="maximum participants")

    @field_validator("location")
    @classmethod
    def validate_location(cls, location: str) -> str:
        if location not in ALLOWED_CITIES:
            raise ValueError("City Not available")
        return location


class ActivityResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True) 

    id: int 
    title: str = Field(..., examples=['Cricket Match'])
    description: Optional[str] = None
    status: ActivityStatusEnum
    category: ActivityCategoryEnum
    location: str = Field(..., examples=['Indore'])
    date: datetime.date
    time: datetime.time
    max_participants: int = Field(..., gt=0, description="Max no. of people to apply")
    organizer_id : int
    organizer_phone : Optional[str] = None


class ActivityUpdateRequest(BaseModel):
    title: Optional[str] = Field(default=None, examples=['Cricket Match'], min_length=3, max_length=50)
    description: Optional[str] = None
    category: Optional[ActivityCategoryEnum] = None 
    location: Optional[str] = None
    date: Optional[datetime.date] = None
    time: Optional[datetime.time] = None 
    max_participants: Optional[int] = Field(default=None, gt=0, description="Max no. of people to apply") 

    @field_validator("location")
    @classmethod
    def validate_location(cls, location: Optional[str]) -> Optional[str]:
        if location is None:
            return None 
        if location not in ALLOWED_CITIES:
            raise ValueError("City Not available")
        return location


class ActivityUpdateResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    title: str
    description: Optional[str] = None
    status: ActivityStatusEnum
    category: ActivityCategoryEnum
    location: str 
    date: datetime.date
    time: datetime.time 
    max_participants: int 
