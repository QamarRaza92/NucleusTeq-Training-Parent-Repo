from sqlalchemy import Column, Integer, String, Text
from app.db.database import Base

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), nullable=False)
    email = Column(String(100), nullable=False, unique=True)
    password = Column(String(255), nullable=False)
    phone_number = Column(String(15), nullable=False)
    city = Column(String(100), nullable=False)
    bio = Column(Text, nullable=False)