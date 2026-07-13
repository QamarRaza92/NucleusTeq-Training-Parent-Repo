from datetime import datetime, date
from typing import Optional

from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models.activity import Activity
from app.models.user import User
from app.models.participation import Participation
from app.schemas.activity_dto import ActivityRequest, ActivityUpdateRequest

from app.enums.participation_status_enum import ParticipationStatusEnum
from app.enums.activity_catergory_enum import ActivityCategoryEnum
from app.enums.activity_status_enum import ActivityStatusEnum

import logging

logger = logging.getLogger(__name__)

def get_all_activities(location: Optional[str],category: Optional[str],date: Optional[date],sort: Optional[str],db: Session,current_user: User):
    """
    Fetch all available activities with optional
    filtering and sorting.
    """

    query = db.query(Activity).filter(Activity.status != ActivityStatusEnum.CANCELLED)

    # Apply optional filters.
    if location:
        query = query.filter(Activity.location.ilike(f"%{location}%"))

    if category:
        query = query.filter(Activity.category == ActivityCategoryEnum(category.upper()))

    if date:
        query = query.filter(Activity.date == date)

    # Apply sorting if requested.
    if sort == "date_asc":
        query = query.order_by(Activity.date.asc())

    elif sort == "date_desc":
        query = query.order_by(Activity.date.desc())

    activities = query.all()

    # Compute latest activity status dynamically.
    for activity in activities:
        activity.status = activity.computed_status

    logger.info(f"User '{current_user.email}' fetched all activities.")

    return activities


def my_created_activities(db: Session, current_user: User):
    """
    Return all activities created by
    the authenticated user.
    """

    activities = db.query(Activity).filter(Activity.organizer_id == current_user.id).all()

    # Compute latest status before returning.
    for activity in activities:
        activity.status = activity.computed_status

    logger.info(f"User '{current_user.email}' fetched their created activities.")

    return activities


def my_joined_activities(db: Session, current_user: User):
    """
    Return all activities where the authenticated
    user has an approved participation request.
    """

    activities = (
        db.query(Activity)
        .join(Participation)
        .filter(
            Participation.participant_id == current_user.id,
            Participation.status == ParticipationStatusEnum.APPROVED
        )
        .all()
    )

    # Compute latest status before returning.
    for activity in activities:
        activity.status = activity.computed_status

    logger.info(f"User '{current_user.email}' fetched their joined activities.")

    return activities


def my_pending_activities(db: Session, current_user: User):
    """
    Return all activities where the authenticated
    user has a pending participation request.
    """

    activities = (
        db.query(Activity)
        .join(Participation)
        .filter(
            Participation.participant_id == current_user.id,
            Participation.status == ParticipationStatusEnum.PENDING
        )
        .all()
    )

    # Compute latest status before returning.
    for activity in activities:
        activity.status = activity.computed_status

    logger.info(f"User '{current_user.email}' fetched their pending activities.")

    return activities


def my_rejected_activities(db: Session, current_user: User):
    """
    Return all activities where the authenticated
    user's participation request was rejected.
    """

    activities = (
        db.query(Activity)
        .join(Participation)
        .filter(
            Participation.participant_id == current_user.id,
            Participation.status == ParticipationStatusEnum.REJECTED
        )
        .all()
    )

    # Compute latest status before returning.
    for activity in activities:
        activity.status = activity.computed_status

    logger.info(f"User '{current_user.email}' fetched their rejected activities.")

    return activities


def get_activity(activity_id: int, db: Session, current_user: User):
    """
    Return complete details of a specific activity.

    Organizer contact is visible only to:
    - Activity organizer
    - Approved participants
    """

    activity = db.query(Activity).filter(Activity.id == activity_id).first()

    if not activity:
        logger.warning(f"Activity fetch failed: Activity '{activity_id}' not found.")
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Activity not found")

    # Compute the latest activity status.
    activity.status = activity.computed_status

    # Check whether the current user can view organizer contact details.
    is_organizer = current_user.id == activity.organizer_id

    is_approved = (
        db.query(Participation)
        .filter(
            Participation.activity_id == activity_id,
            Participation.participant_id == current_user.id,
            Participation.status == ParticipationStatusEnum.APPROVED
        ).first()
        is not None
    )

    activity.organizer_phone = (
        activity.organizer.phone_number
        if is_organizer or is_approved
        else None
    )

    logger.info(f"User '{current_user.email}' viewed activity '{activity.title}'.")

    return activity


def create_activity(activity: ActivityRequest, db: Session, current_user: User):
    """
    Create a new activity for the authenticated user.

    Activity date and time must be in the future.
    """

    if datetime.combine(activity.date, activity.time) <= datetime.now():
        logger.warning(f"Activity creation failed: User '{current_user.email}' provided past date/time.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Activity must be scheduled for a future date and time")

    new_activity = Activity(
        title=activity.title,
        description=activity.description,
        category=activity.category,
        location=activity.location,
        date=activity.date,
        time=activity.time,
        max_participants=activity.max_participants,
        organizer_id=current_user.id
    )

    db.add(new_activity)
    db.commit()
    db.refresh(new_activity)

    logger.info(f"User '{current_user.email}' created activity '{new_activity.title}' successfully.")

    return new_activity



def update_activity(activity_id: int, update_data: ActivityUpdateRequest, db: Session, current_user: User):
    """
    Update an existing activity.

    Only the organizer can update an activity.
    Cancelled and completed activities cannot be updated.
    """

    activity = db.query(Activity).filter(Activity.id == activity_id).with_for_update().first()

    if not activity:
        logger.warning(f"Activity update failed: Activity '{activity_id}' not found.")
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"Activity with id '{activity_id}' not found!")

    if current_user.id != activity.organizer_id:
        logger.warning(f"Activity update failed: User '{current_user.email}' is not the organizer.")
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="You are not owner of this activity")

    if activity.status == ActivityStatusEnum.CANCELLED:
        logger.warning(f"Activity update failed: Activity '{activity.title}' is cancelled.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Cancelled activity cannot be updated")

    if activity.status == ActivityStatusEnum.COMPLETED:
        logger.warning(f"Activity update failed: Activity '{activity.title}' is completed.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Completed activity cannot be updated")

    # Update only the fields provided by the user.
    if update_data.title is not None:
        activity.title = update_data.title

    if update_data.description is not None:
        activity.description = update_data.description

    if update_data.category is not None:
        activity.category = update_data.category

    if update_data.location is not None:
        activity.location = update_data.location

    # Validate updated activity schedule.
    if update_data.date is not None or update_data.time is not None:
        check_date = update_data.date or activity.date
        check_time = update_data.time or activity.time

        if datetime.combine(check_date, check_time) <= datetime.now():
            logger.warning(f"Activity update failed: Invalid schedule for activity '{activity.title}'.")
            raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Activity must be scheduled for a future date")

        if update_data.date is not None:
            activity.date = update_data.date

        if update_data.time is not None:
            activity.time = update_data.time

    # Validate participant limit before updating.
    if update_data.max_participants is not None:

        participants_count = db.query(Participation).filter(
            Participation.activity_id == activity.id,
            Participation.status == ParticipationStatusEnum.APPROVED
        ).count()

        if participants_count < update_data.max_participants and activity.status == ActivityStatusEnum.FULL:
            activity.status = ActivityStatusEnum.OPEN

        if participants_count > update_data.max_participants:
            logger.warning(f"Activity update failed: max_participants lower than approved count.")
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Cannot set max_participants({update_data.max_participants}) lower than current approved count ({participants_count})!"
            )

        activity.max_participants = update_data.max_participants

    db.commit()
    db.refresh(activity)

    logger.info(f"User '{current_user.email}' updated activity '{activity.title}' successfully.")

    return activity



def cancel_activity(activity_id: int, db: Session, current_user: User):
    """
    Cancel an existing activity.

    Only the organizer can cancel an activity.
    Completed activities cannot be cancelled.
    """

    activity = db.query(Activity).filter(Activity.id == activity_id).first()

    if not activity:
        logger.warning(f"Activity cancellation failed: Activity '{activity_id}' not found.")
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Activity not found")

    if activity.organizer_id != current_user.id:
        logger.warning(f"Activity cancellation failed: User '{current_user.email}' is not the organizer.")
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="You are not owner of this activity")

    if activity.status == ActivityStatusEnum.CANCELLED:
        logger.warning(f"Activity cancellation failed: Activity '{activity.title}' is already cancelled.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Cannot cancel an already cancelled activity")

    if activity.status == ActivityStatusEnum.COMPLETED:
        logger.warning(f"Activity cancellation failed: Activity '{activity.title}' is already completed.")
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Cannot cancel 'COMPLETED' activity")
    
    db.query(Participation).filter(
        Participation.activity_id == activity_id, Participation.status.in_(
            [ParticipationStatusEnum.PENDING,ParticipationStatusEnum.APPROVED])).update(
            {Participation.status: ParticipationStatusEnum.REJECTED}, synchronize_session=False)

    activity.status = ActivityStatusEnum.CANCELLED

    db.commit()

    logger.info(f"User '{current_user.email}' cancelled activity '{activity.title}' successfully.")

    return {
        "message": f"Activity '{activity.title}' has been cancelled!"
    }