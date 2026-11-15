from sqlalchemy import Column, ForeignKey, Integer, String, TIMESTAMP
from app.db.database import Base
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship

class Participation(Base):
    __tablename__ = "participation_requests"

    id = Column(Integer, primary_key=True, index=True)
    activity_id = Column(Integer, ForeignKey("activities.id"), nullable=False)
    participant_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    status = Column(String(10), default="PENDING")
    created_at = Column(TIMESTAMP, server_default=func.now())

    activity = relationship("Activity", backref="requests")
    user = relationship("User", backref="participation_requests")