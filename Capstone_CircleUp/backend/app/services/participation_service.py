from datetime import datetime

from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models.activity import Activity
from app.models.participation import Participation
from app.models.user import User

from app.enums.participation_status_enum import ParticipationStatusEnum
from app.enums.activity_status_enum import ActivityStatusEnum

import logging

logger = logging.getLogger(__name__)


def send_participation_request(activity_id: int, db: Session, current_user: User):
    """
    Send a participation request for an activity.

    Organizers cannot join their own activities.
    """

    activity = db.query(Activity).filter(Activity.id == activity_id).first()

    if not activity:
        logger.warning(f"Participation failed: Activity '{activity_id}' not found.")
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Failed To Participate: Activity not found")

    # Automatically mark expired activities as completed.
    if datetime.combine(activity.date, activity.time) <= datetime.now():
        activity.status = ActivityStatusEnum.COMPLETED
        db.commit()

        logger.warning(f"Participation failed: Activity '{activity.title}' is completed.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Failed to participate! Activity is 'COMPLETED'")

    if activity.organizer_id == current_user.id:
        logger.warning(f"Participation failed: Organizer '{current_user.email}' attempted to join own activity.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Organizers cannot join their own activity")

    existing_request = db.query(Participation).filter(
        Participation.activity_id == activity_id,
        Participation.participant_id == current_user.id
    ).first()

    if existing_request:
        logger.warning(f"Participation failed: User '{current_user.email}' has already applied.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="You have already applied for participation")

    if activity.max_participants <= db.query(Participation).filter(
        Participation.activity_id == activity_id,
        Participation.status == ParticipationStatusEnum.APPROVED
    ).count():
        logger.warning(f"Participation failed: Activity '{activity.title}' is full.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=f"Seats full for activity '{activity.title}'!")

    if activity.status == ActivityStatusEnum.COMPLETED:
        logger.warning(f"Participation failed: Activity '{activity.title}' is completed.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Failed To Participate: Activity is 'COMPLETED'")

    if activity.status == ActivityStatusEnum.FULL:
        logger.warning(f"Participation failed: Activity '{activity.title}' is full.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Failed To Participate: Activity is 'FULL'")

    if activity.status == ActivityStatusEnum.CANCELLED:
        logger.warning(f"Participation failed: Activity '{activity.title}' is cancelled.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Failed To Participate: Activity is 'CANCELLED'")

    new_request = Participation(
        activity_id=activity.id,
        participant_id=current_user.id
    )

    db.add(new_request)
    db.commit()
    db.refresh(new_request)

    logger.info(f"User '{current_user.email}' requested to join activity '{activity.title}'.")

    return new_request



def get_requests(activity_id: int, db: Session, current_user: User):
    """
    Return all pending participation requests
    for an activity owned by the authenticated user.
    """

    activity = db.query(Activity).filter(Activity.id == activity_id).first()

    if not activity:
        logger.warning(f"Request fetch failed: Activity '{activity_id}' not found.")
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Activity not found")

    if activity.organizer_id != current_user.id:
        logger.warning(f"Request fetch failed: User '{current_user.email}' is not the organizer.")
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="You are not owner of this activity")

    requests = db.query(Participation).filter(
        Participation.activity_id == activity_id,
        Participation.status == ParticipationStatusEnum.PENDING
    ).all()

    logger.info(f"User '{current_user.email}' fetched pending requests for activity '{activity.title}'.")

    return [
        {
            "id": request.id,
            "participant_id": request.participant_id,
            "participant_name": request.user.name,
            "participant_email": request.user.email,
            "status": request.status
        }
        for request in requests
    ]


def approve_request(activity_id: int, request_id: int, db: Session, current_user: User):
    """
    Approve a pending participation request.

    Only the activity organizer can approve requests.
    """

    activity = db.query(Activity).filter(Activity.id == activity_id).with_for_update().first()

    if not activity:
        logger.warning(f"Approval failed: Activity '{activity_id}' not found.")
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Activity not found")

    if activity.organizer_id != current_user.id:
        logger.warning(f"Approval failed: User '{current_user.email}' is not the organizer.")
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="You are not owner of this activity")

    request = db.query(Participation).filter(
        Participation.id == request_id,
        Participation.activity_id == activity.id
    ).first()

    if not request:
        logger.warning(f"Approval failed: Request '{request_id}' not found.")
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Request not found")

    if request.status != ParticipationStatusEnum.PENDING:
        logger.warning(f"Approval failed: Request '{request.id}' is already {request.status}.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=f"Request is already {request.status}")

    approved_requests = db.query(Participation).filter(
        Participation.activity_id == activity.id,
        Participation.status == ParticipationStatusEnum.APPROVED
    ).count()

    if approved_requests >= activity.max_participants:
        logger.warning(f"Approval failed: Activity '{activity.title}' is already full.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=f"Activity {activity.title} is already full")

    request.status = ParticipationStatusEnum.APPROVED

    # Mark activity as FULL once all seats are occupied.
    if approved_requests + 1 == activity.max_participants:
        activity.status = ActivityStatusEnum.FULL

    db.commit()
    db.refresh(request)

    logger.info(f"Owner '{current_user.email}' approved request '{request.id}' for activity '{activity.title}'.")

    return request



def reject_request(activity_id: int, request_id: int, db: Session, current_user: User):
    """
    Reject a pending participation request.

    Only the activity organizer can reject requests.
    """

    activity = db.query(Activity).filter(Activity.id == activity_id).with_for_update().first()

    if not activity:
        logger.warning(f"Rejection failed: Activity '{activity_id}' not found.")
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Activity not found")

    if activity.organizer_id != current_user.id:
        logger.warning(f"Rejection failed: User '{current_user.email}' is not the organizer.")
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="You are not owner of this activity")

    request = db.query(Participation).filter(
        Participation.id == request_id,
        Participation.activity_id == activity_id
    ).first()

    if not request:
        logger.warning(f"Rejection failed: Request '{request_id}' not found.")
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Request not found")

    if request.status != ParticipationStatusEnum.PENDING:
        logger.warning(f"Rejection failed: Request '{request.id}' is already {request.status}.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=f"Request is already {request.status}")

    request.status = ParticipationStatusEnum.REJECTED

    db.commit()
    db.refresh(request)

    logger.info(f"Owner '{current_user.email}' rejected request '{request.id}' for activity '{activity.title}'.")

    return request


