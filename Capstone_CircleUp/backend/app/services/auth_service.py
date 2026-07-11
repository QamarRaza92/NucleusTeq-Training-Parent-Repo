from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models.user import User
from app.schemas.register_dto import RegisterRequest
from app.schemas.login_dto import LoginRequest
from app.utils.hashing import hash_password, verify_password
from app.utils.jwt import create_access_token

import logging

logger = logging.getLogger(__name__)


def register_user(user: RegisterRequest, db: Session):
    """
    Register a new user.

    Workflow:
    1. Check whether the email is already registered.
    2. Hash the user's password.
    3. Save the user in the database.
    4. Return the created user.
    """

    existing_user = db.query(User).filter(User.email == user.email).first()

    if existing_user:
        logger.warning(f"Registration failed: Email '{user.email}' already exists.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail="Email already registered")

    hashed_password = hash_password(user.password)

    new_user = User(
        name=user.name,
        email=user.email,
        password=hashed_password,
        phone_number=user.phone_number,
        city=user.city,
        bio=user.bio
    )

    db.add(new_user)
    db.commit()
    db.refresh(new_user)

    logger.info(f"User '{new_user.email}' registered successfully.")

    return new_user


def login_user(user: LoginRequest, db: Session):
    """
    Authenticate a user and generate an access token.

    Workflow:
    1. Verify user exists.
    2. Verify password.
    3. Generate JWT token.
    4. Return login response.
    """

    db_user = db.query(User).filter(User.email == user.email).first()

    if not db_user:
        logger.warning(f"Login failed: Email '{user.email}' not found.")

        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")

    if not verify_password(user.password, db_user.password):
        logger.warning(f"Login failed: Incorrect password for '{user.email}'.")

        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid password")

    token = create_access_token(data={"sub": db_user.email})

    logger.info(f"User '{db_user.email}' logged in successfully.")

    return {
        "access_token": token,
        "token_type": "bearer",
        "id": db_user.id,
        "name": db_user.name,
        "email": db_user.email
    }