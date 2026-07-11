from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.models.user import User
from app.schemas.participation_dto import ParticipationResponse
from app.services import participation_service
from app.utils.dependencies import get_current_user

router = APIRouter(prefix="/activities", tags = ["Participation"])

@router.post("/{activity_id}/requests", response_model=ParticipationResponse,summary="Request to Join Activity",
    description="""
    Submit a participation request for an activity.

    - Organizers cannot join their own activity.
    - Duplicate requests are not allowed.
    - Completed, cancelled and full activities cannot be joined.
    """,
    responses={
        200: {"description": "Participation request submitted successfully"},
        400: {"description": "Invalid participation request"},
        401: {"description": "Unauthorized"},
        404: {"description": "Activity not found"}
    }
)
def send_participation_request(activity_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Send a participation request for an activity.
    return participation_service.send_participation_request(activity_id, db, current_user)



@router.get("/{activity_id}/requests", summary="Get Pending Participation Requests",
    description="""
    Retrieve all pending participation requests for a specific activity.

    Only the activity organizer can access this endpoint.
    """,
    responses={
        200: {"description": "Pending requests fetched successfully"},
        401: {"description": "Unauthorized"},
        403: {"description": "Not the activity organizer"},
        404: {"description": "Activity not found"}
    }
)
def get_requests(activity_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Return pending participation requests for an activity.
    return participation_service.get_requests(activity_id, db, current_user)




@router.put("/{activity_id}/requests/{request_id}/approve", response_model=ParticipationResponse, summary="Approve Participation Request",
    description="""
    Approve a pending participation request.

    - Only the activity organizer can approve requests.
    - Activity status becomes FULL when all seats are occupied.
    """,
    responses={
        200: {"description": "Participation request approved successfully"},
        400: {"description": "Request already processed or activity full"},
        401: {"description": "Unauthorized"},
        403: {"description": "Not the activity organizer"},
        404: {"description": "Activity or request not found"}
    }
)
def approve_request(activity_id: int, request_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Approve a pending participation request.
    return participation_service.approve_request(activity_id, request_id, db, current_user)



@router.put("/{activity_id}/requests/{request_id}/reject", response_model=ParticipationResponse,
    summary="Reject Participation Request",
    description="""
    Reject a pending participation request.

    - Only the activity organizer can reject requests.
    - Only pending requests can be rejected.
    """,
    responses={
        200: {"description": "Participation request rejected successfully"},
        400: {"description": "Request already processed"},
        401: {"description": "Unauthorized"},
        403: {"description": "Not the activity organizer"},
        404: {"description": "Activity or request not found"}
    }
)
def reject_request(activity_id: int, request_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Reject a pending participation request.
    return participation_service.reject_request(activity_id, request_id, db, current_user)