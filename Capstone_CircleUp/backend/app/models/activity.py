from sqlalchemy import Column, Integer, String, TEXT, Date, Time, ForeignKey, TIMESTAMP
from app.db.database import Base
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func 
from datetime import datetime

class Activity(Base):
    __tablename__ = "activities"
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(100), nullable=False)
    description = Column(TEXT)
    category = Column(String(20), nullable=False)
    location = Column(String(100), nullable=False)
    date = Column(Date, nullable=False)
    time = Column(Time, nullable=False)
    max_participants = Column(Integer, nullable=False)
    status = Column(String(20), default="OPEN")
    organizer_id = Column(Integer,ForeignKey("users.id"))
    created_at = Column(TIMESTAMP, server_default=func.now())

    organizer = relationship("User",backref="activities")


    @property
    def computed_status(self):
        if self.status == "CANCELLED":
            return "CANCELLED"
        if datetime.combine(self.date, self.time) < datetime.now():
            return "COMPLETED"
        return self.status