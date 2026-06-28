from fastapi import Depends,HTTPException,status 
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session 
from app.db.database import get_db
from app.models.user import User 
from app.utils.jwt import verify_token
import logging 

logger = logging.getLogger(__name__)
oauth_scheme = OAuth2PasswordBearer(tokenUrl="/auth/token")

def get_current_user(token: str = Depends(oauth_scheme),db:Session = Depends(get_db)):
    payload = verify_token(token)
    if payload is None:
        logger.warning("Authentication failed: Invalid or expired token provided")
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED,detail="Invalid or expired token")
    email = payload.get("sub")
    user = db.query(User).filter(User.email == email).first()
    if user is None:
        logger.warning("Authentication failed: Valid token but user: '{a}' not found! ".format(a=email))
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED,detail="User not found")
    logger.info(f"User: '{email}' authenticated successfully!")
    return user