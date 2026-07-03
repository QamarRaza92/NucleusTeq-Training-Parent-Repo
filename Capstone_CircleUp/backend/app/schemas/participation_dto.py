from pydantic import BaseModel, Field, field_validator, ConfigDict
from typing import Optional
from datetime import date, time
from enum import Enum

class RequestStatusEnum(str, Enum):
    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"


class ParticipationRequest(BaseModel):
    activity_id : int


class ParticipationResponse(BaseModel):
    id : int 
    activity_id : int 
    participant_id : int 
    status : RequestStatusEnum

    class Config:
        from_attributes = True
