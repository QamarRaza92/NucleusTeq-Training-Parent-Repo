from pydantic import BaseModel, Field, field_validator, ConfigDict
from typing import Optional
from datetime import date, time
from enum import Enum
from app.enums.participation_status_enum import ParticipationStatusEnum


class ParticipationRequest(BaseModel):
    activity_id : int

class ParticipationResponse(BaseModel):
    id : int 
    activity_id : int 
    participant_id : int 
    status : ParticipationStatusEnum

    class Config:
        from_attributes = True
