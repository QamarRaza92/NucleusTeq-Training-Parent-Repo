from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.schemas.register_dto import RegisterRequest, RegisterResponse
from app.schemas.login_dto import LoginRequest, LoginResponse
from app.services.auth_service import register_user, login_user

router = APIRouter(prefix="/auth",tags=["Authentication"])


@router.post("/register", response_model=RegisterResponse, status_code=status.HTTP_201_CREATED, summary="Register a new user",
    description="""
    Create a new user account.

    - Email must be unique.
    - Password is securely hashed before storing.
    - Returns the newly created user details.
    """,
    responses={
        201: {"description": "User registered successfully"},
        400: {"description": "Email already registered"},
        422: {"description": "Validation error"}
    }
)
def register(user: RegisterRequest, db: Session = Depends(get_db)):
    # Register a new CircleUp user.
    return register_user(user, db)


@router.post("/login", response_model=LoginResponse, summary="User Login",
    description="""
    Authenticate an existing user.

    - Validates email and password.
    - Generates a JWT access token.
    - Returns authenticated user information.
    """,
    responses={
        200: {"description": "Login successful"},
        401: {"description": "Invalid password"},
        404: {"description": "User not found"},
        422: {"description": "Validation error"}
    }
)
def login(user: LoginRequest,db: Session = Depends(get_db)):
    #Authenticate a registered user.
    return login_user(user, db)