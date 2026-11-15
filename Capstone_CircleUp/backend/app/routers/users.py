from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.models.user import User
from app.schemas.profile_dto import ProfileUpdateRequest, ProfileResponse
from app.services.user_service import get_profile, update_profile, get_my_activities, get_dashboard, get_dashboard_profile

from app.utils.dependencies import get_current_user

router = APIRouter(prefix="/users",tags=["Users"])


@router.get("/me", response_model=ProfileResponse, summary="Get User Profile",
    description="""
    Fetch the profile details of the currently authenticated user.
    """,
    responses={
        200: {"description": "Profile fetched successfully"},
        401: {"description": "Unauthorized"}
    }
)
def profile(current_user: User = Depends(get_current_user)):
    # Return authenticated user's profile.
    return get_profile(current_user)


@router.put("/me", response_model=ProfileResponse, summary="Update User Profile",
    description="""
    Update the authenticated user's profile.

    - Updates only the provided fields.
    - Returns updated profile details.
    """,
    responses={
        200: {"description": "Profile updated successfully"},
        400: {"description": "No fields provided"},
        401: {"description": "Unauthorized"},
        422: {"description": "Validation error"}
    }
)
def edit_profile(update_data: ProfileUpdateRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    # Update authenticated user's profile.
    return update_profile(update_data, current_user, db)


@router.get("/me/activities", summary="Get User Activities",
    description="""
    Fetch all activities associated with the authenticated user.

    Includes:
    - Created activities
    - Joined activities
    - Pending requests
    """,
    responses={
        200: {"description": "Activities fetched successfully"},
        401: {"description": "Unauthorized"}
    }
)
def my_activities(db: Session = Depends(get_db),current_user: User = Depends(get_current_user)):
    # Return activities related to the authenticated user.
    return get_my_activities(db, current_user)


@router.get("/dashboard", summary="Get Dashboard",
    description="""
    Fetch dashboard information for the authenticated user.

    Returns:
    - User statistics
    - Recent activities
    - Pending participation requests
    """,
    responses={
        200: {"description": "Dashboard loaded successfully"},
        401: {"description": "Unauthorized"}
    }
)
def dashboard(db: Session = Depends(get_db),current_user: User = Depends(get_current_user)):
    # Return dashboard data.
    return get_dashboard(db, current_user)


@router.get("/dashboard/profile", summary="Get Dashboard Profile",
    description="""
    Fetch profile information displayed on the dashboard page.
    """,
    responses={
        200: {"description": "Dashboard profile fetched successfully"},
        401: {"description": "Unauthorized"}
    }
)
def dashboard_profile(db: Session = Depends(get_db),current_user: User = Depends(get_current_user)):
    # Return dashboard profile details.
    return get_dashboard_profile(db, current_user)