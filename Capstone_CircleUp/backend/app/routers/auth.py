from fastapi import APIRouter,Depends,HTTPException,status
from sqlalchemy.orm import Session
from app.db.database import get_db
from app.models.user import User
from app.schemas.register_dto import RegisterRequest,RegisterResponse
from app.schemas.login_dto import LoginRequest,LoginResponse
from app.utils.hashing import hash_password,verify_password
from app.utils.jwt import create_access_token
import logging 
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

router = APIRouter(prefix="/auth", tags=["Auth"])

@router.post("/register",response_model=RegisterResponse,status_code=status.HTTP_201_CREATED)
def register(user:RegisterRequest, db:Session = Depends(get_db)):
    existing_user = db.query(User).filter(User.email == user.email).first()
    if existing_user:
        logger.warning(f"Registration Failed: Email: {user.email} already exists")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail="Email already registered")
    hashed = hash_password(user.password)

    new_user = User(
                        name = user.name,
                        email = user.email,
                        password = hashed,
                        phone_number = user.phone_number,
                        city = user.city,
                        bio = user.bio
                    )
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    logger.info(f"New user '{new_user.email}' registered successfully!")
    return new_user

@router.post("/login",response_model=LoginResponse)
def login(user:LoginRequest, db:Session = Depends(get_db)):
    db_user = db.query(User).filter(User.email == user.email).first()
    if not db_user:
        logger.warning(f"Failed login: Email '{user.email}' not found")
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,detail="User not found")
    
    if not verify_password(user.password, db_user.password):
        logger.warning(f"Failed login: Incorrect password")
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED,detail="Invalid password")
    
    token = create_access_token(data={"sub":db_user.email})
    logger.info(f"Success: User '{db_user.email}' logged in successfully!")
    return {
            "access_token":token, "token_type":"bearer", 
            "id":db_user.id,"name":db_user.name,"email":db_user.email
           }

from fastapi.security import OAuth2PasswordRequestForm

@router.post("/token")
def swagger_login(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    db_user = db.query(User).filter(User.email == form_data.username).first()
    if not db_user or not verify_password(form_data.password, db_user.password):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid credentials")
    token = create_access_token(data={"sub": db_user.email})
    return {"access_token": token, "token_type": "bearer"}