from datetime import date
from typing import Optional

from fastapi import APIRouter, Depends, Query, status
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.models.user import User
from app.schemas.activity_dto import ActivityRequest, ActivityResponse, ActivityUpdateRequest, ActivityUpdateResponse
from app.services import activity_service
from app.utils.dependencies import get_current_user

router = APIRouter(prefix="/activity", tags=["Activities"])

#Get all availabe activities (Except cancelled ones)
@router.get("/",response_model=list[ActivityResponse], status_code=status.HTTP_200_OK, summary="Browse all activites created by all users",
            description="Returns all non-cancelled activities. Supports filtering by category, location, date and sorting by date.",
            responses={200: {"description": "Activities fetched successfully"},401: {"description": "Unauthorized"}})
def get_all_activities(
    location: Optional[str] = Query(None, description="Filter by location"),
    category: Optional[str] = Query(None, description="Filter by category"),
    date: Optional[date] = Query(None, description="Filter by activity date"),
    sort: Optional[str] = Query(None, description="Sort: date_asc | date_desc"),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    # Return all matching non-cancelled activities.
    return activity_service.get_all_activities(location, category, date, sort, db, current_user)




#Get activities created by me
@router.get("/my-created", response_model=list[ActivityResponse], status_code=status.HTTP_200_OK, summary="My created activities",
    description="Returns all activities created by the currently logged-in user.",
    responses={200: {"description": "Created activities fetched successfully"},401: {"description": "Unauthorized"}})
def my_created_activities(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Return activities created by the authenticated user.
    return activity_service.my_created_activities(db, current_user)


#Get activities which i joined successfully
@router.get("/my-joined", response_model=list[ActivityResponse], status_code=status.HTTP_200_OK, summary="My joined activities",
    description="Returns all activities where the current user's participation request is approved.",
    responses={200: {"description": "Joined activities fetched successfully"},401: {"description": "Unauthorized"}})
def my_joined_activities(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Return activities joined by the authenticated user.
    return activity_service.my_joined_activities(db, current_user)


#Get Pending request of users who applied to my activities
@router.get("/my-pending", response_model=list[ActivityResponse], status_code=status.HTTP_200_OK, summary="My pending requests",
    description="Returns all activities where the current user has a pending participation request.",
    responses={200: {"description": "Pending activities fetched successfully"},401: {"description": "Unauthorized"}})
def my_pending_activities(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Return activities with pending participation requests.
    return activity_service.my_pending_activities(db, current_user)



#Get activities where my request was rejected
@router.get("/my-rejected", response_model=list[ActivityResponse], status_code=status.HTTP_200_OK, summary="My rejected requests",
    description="Returns all activities where the current user's participation request was rejected.",
    responses={200: {"description": "Pending activities fetched successfully"},401: {"description": "Unauthorized"}})
def my_rejected_activities(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Return activities whose participation requests were rejected.
    return activity_service.my_rejected_activities(db, current_user)



# Get activity by ID
@router.get("/{activity_id}",response_model=ActivityResponse, status_code=status.HTTP_200_OK, summary="Get activity detail",
    description="Returns full detail of a single activity. Phone number is visible only to the organizer or approved participants.",
    responses={
        200: {"description": "Activity fetched successfully"},
        401: {"description": "Unauthorized"},
        404: {"description": "Activity not found"}
    }
)
def get_activity(activity_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Return complete activity details.
    return activity_service.get_activity(activity_id, db, current_user)



# Create new activity (POST)
@router.post("/",response_model=ActivityResponse, status_code=status.HTTP_201_CREATED,
    summary="Create Activity",
    description="""
    Create a new activity.

    - Only authenticated users can create activities.
    - Activity date and time must be in the future.
    """,
    responses={
        201: {"description": "Activity created successfully"},
        400: {"description": "Activity date/time must be in the future"},
        401: {"description": "Unauthorized"},
        422: {"description": "Validation error"}
    }
)
def create_activity(activity: ActivityRequest, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Create a new activity.
    return activity_service.create_activity(activity, db, current_user)



# To Update an activity (PUT)
@router.put("/{activity_id}",response_model=ActivityUpdateResponse, status_code=status.HTTP_200_OK, summary="Update Activity",
    description="""
    Update an existing activity.

    - Only the organizer can update the activity.
    - Cancelled and completed activities cannot be modified.
    - Activity date/time must remain in the future.
    """,
    responses={
        200: {"description": "Activity updated successfully"},
        400: {"description": "Invalid activity state or request"},
        401: {"description": "Unauthorized"},
        403: {"description": "Not the activity organizer"},
        404: {"description": "Activity not found"},
        422: {"description": "Validation error"}
    }
)
def update_activity(activity_id: int, update_data: ActivityUpdateRequest, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Update an existing activity.
    return activity_service.update_activity(activity_id, update_data, db, current_user)
    

# Delete an activity route
@router.delete("/{activity_id}", status_code=status.HTTP_200_OK, summary="Cancel an activity",
    description="Cancels an activity by setting its status to CANCELLED. Only the organizer can cancel. Completed activities cannot be cancelled.",
    responses={
        200: {"description": "Activity cancelled successfully"},
        400: {"description": "Activity already cancelled or completed"},
        401: {"description": "Unauthorized"},
        403: {"description": "Not the activity organizer"},
        404: {"description": "Activity not found"}
    }
)
def cancel_activity(activity_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Cancel an activity created by the authenticated user.
    return activity_service.cancel_activity(activity_id, db, current_user)