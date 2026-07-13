from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models.user import User
from app.models.activity import Activity
from app.models.participation import Participation
from app.schemas.profile_dto import ProfileUpdateRequest

from app.enums.participation_status_enum import ParticipationStatusEnum
from app.enums.activity_status_enum import ActivityStatusEnum

import logging

logger = logging.getLogger(__name__)


def get_profile(current_user: User):
    #Return the authenticated user's profile.
    logger.info(f"User '{current_user.email}' fetched their profile.")
    return current_user


def update_profile(update_data: ProfileUpdateRequest,current_user: User,db: Session):
    #Update the authenticated user's profile.

    if not update_data.model_dump(exclude_unset=True):
        logger.warning("Profile update failed: No fields provided.")

        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="No update fields provided.")

    # Update only the fields supplied in the request.
    if update_data.name is not None:
        current_user.name = update_data.name

    if update_data.phone_number is not None:
        current_user.phone_number = update_data.phone_number

    if update_data.city is not None:
        current_user.city = update_data.city

    if update_data.bio is not None:
        current_user.bio = update_data.bio

    db.commit()
    db.refresh(current_user)

    logger.info(f"User '{current_user.email}' updated their profile.")

    return current_user


def get_my_activities(db: Session, current_user: User):
    #Return activities created, joined and pending for the authenticated user.

    created_activities = db.query(Activity).filter(
        Activity.organizer_id == current_user.id
    ).all()

    joined_activities = db.query(Participation).filter(
        Participation.participant_id == current_user.id,
        Participation.status == ParticipationStatusEnum.APPROVED
    ).all()

    joined = [
        req.activity
        for req in joined_activities
    ]

    pending_requests = db.query(Participation).filter(
        Participation.participant_id == current_user.id,
        Participation.status == ParticipationStatusEnum.PENDING
    ).all()

    pending = [
        req.activity
        for req in pending_requests
    ]

    logger.info(f"User '{current_user.email}' fetched their activities.")

    return {
        "created_activities": created_activities,
        "joined_activities": joined,
        "pending_requests": pending
    }


def get_dashboard(db: Session,current_user: User):
    #Build dashboard statistics, recent activities and pending participation requests.

    created = db.query(Activity).filter(
        Activity.organizer_id == current_user.id
    ).count()

    joined = db.query(Participation).filter(
        Participation.participant_id == current_user.id,
        Participation.status == ParticipationStatusEnum.APPROVED
    ).count()

    pending = db.query(Participation).filter(
        Participation.participant_id == current_user.id,
        Participation.status == ParticipationStatusEnum.PENDING
    ).count()

    completed = db.query(Activity).filter(
        Activity.organizer_id == current_user.id,
        Activity.status == ActivityStatusEnum.COMPLETED
    ).count()

    recent_activities = (
        db.query(Activity)
        .filter(Activity.organizer_id == current_user.id)
        .order_by(Activity.created_at.desc())
        .limit(5)
        .all()
    )

    # Fetch pending requests received on user's activities.
    requests = (
        db.query(Participation)
        .join(Activity)
        .join(User, Participation.participant_id == User.id)
        .filter(
            Activity.organizer_id == current_user.id,
            Participation.status == ParticipationStatusEnum.PENDING
        )
        .all()
    )

    logger.info(f"User '{current_user.email}' opened their dashboard.")

    return {
        "user": current_user.name,

        "stats": {
            "created": created,
            "joined": joined,
            "pending": pending,
            "completed": completed
        },

        "activities": [
            {
                "id": activity.id,
                "title": activity.title
            }
            for activity in recent_activities
        ],

        "requests": [
            {
                "request_id": request.id,
                "activity_id": request.activity_id,
                "activity": request.activity.title,
                "participant": request.user.name,
                "participant_email": request.user.email
            }
            for request in requests
        ]
    }


def get_dashboard_profile(db: Session,current_user: User):
    #Return profile information displayed on the dashboard profile page.

    created = db.query(Activity).filter(
        Activity.organizer_id == current_user.id
    ).count()

    joined = db.query(Participation).filter(
        Participation.participant_id == current_user.id,
        Participation.status == ParticipationStatusEnum.APPROVED
    ).count()

    pending = db.query(Participation).filter(
        Participation.participant_id == current_user.id,
        Participation.status == ParticipationStatusEnum.PENDING
    ).count()

    logger.info(f"User '{current_user.email}' viewed their profile.")

    return {
        "name": current_user.name,
        "email": current_user.email,
        "phone": current_user.phone_number,
        "city": current_user.city,
        "bio": current_user.bio,
        "created": created,
        "joined": joined,
        "pending": pending
    }