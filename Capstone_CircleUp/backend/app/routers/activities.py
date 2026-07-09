from fastapi import APIRouter, HTTPException, status, Depends, Query
from app.db.database import get_db
from sqlalchemy.orm import Session
from app.schemas.activity_dto import ActivityRequest, ActivityResponse, ActivityUpdateRequest, ActivityUpdateResponse
from app.models.activity import Activity
from app.models.user import User 
from app.models.participation import Participation
from app.utils.dependencies import get_current_user
from datetime import datetime, date
from typing import Optional
import logging

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/activity",tags=['Activities'])

@router.get("/",response_model=list[ActivityResponse])
def get_all_activities(
                        location: Optional[str] = Query(default=None),
                        category: Optional[str] = Query(default=None),
                        date: Optional[date] = Query(default=None),
                        sort: Optional[str] = Query(default=None),
                        db: Session = Depends(get_db),
                        current_user: User = Depends(get_current_user)
                      ):
    query = db.query(Activity).filter(Activity.status!="CANCELLED")
    if location:
        query = query.filter(Activity.location.ilike(f"%{location}%"))
    if category:
        query = query.filter(Activity.category == category.upper())
    if date:
        query = query.filter(Activity.date == date)

    if sort == "date_asc":
        query = query.order_by(Activity.date.asc())
    elif sort == "date_desc":
        query = query.order_by(Activity.date.desc())

    activities = query.all()

    for activity in activities:
        activity.status = activity.computed_status

    logger.info(f"User '{current_user.email}' fetched all activities")
    return activities




@router.get("/my-created", response_model=list[ActivityResponse])
def my_created_activities(db: Session = Depends(get_db),current_user: User = Depends(get_current_user)):
    activities = db.query(Activity).filter(
        Activity.organizer_id == current_user.id
    ).all()
    for activity in activities:
        activity.status = activity.computed_status
    return activities


@router.get("/my-joined", response_model=list[ActivityResponse])
def my_joined_activities(db: Session = Depends(get_db),current_user: User = Depends(get_current_user)):
    activities = (
                    db.query(Activity)
                    .join(Participation).filter(
                                                Participation.participant_id == current_user.id,
                                                Participation.status == "APPROVED"
                                               ).all()
                 )
    for activity in activities:
        activity.status = activity.computed_status
    return activities


@router.get("/my-pending", response_model=list[ActivityResponse])
def my_pending_activities(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    activities = (
                    db.query(Activity).join(Participation).filter(
                                                                  Participation.participant_id == current_user.id,
                                                                  Participation.status == "PENDING"
                                                                 ).all()
                 )
    for activity in activities:
        activity.status = activity.computed_status
    return activities


@router.get("/my-rejected", response_model=list[ActivityResponse])
def my_rejected_activities(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    activities = (
                  db.query(Activity).join(Participation).filter(
                                                                Participation.participant_id == current_user.id,
                                                                Participation.status == "REJECTED"
                                                               ).all()
                 )
    for activity in activities:
        activity.status = activity.computed_status
    return activities

@router.get("/{activity_id}",response_model=ActivityResponse)
def get_activity(activity_id:int, db:Session=Depends(get_db), current_user:User=Depends(get_current_user)):
    activity = db.query(Activity).filter(Activity.id==activity_id).first()
    if not activity:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Activity not found")
    
    activity.status = activity.computed_status

    is_organizer = current_user.id == activity.organizer_id
    is_approved = db.query(Participation).filter(
                                                 Participation.activity_id == activity_id,
                                                 Participation.participant_id == current_user.id,
                                                 Participation.status == "APPROVED"
                                                ).first() is not None
    if is_organizer or is_approved:
        activity.organizer_phone = activity.organizer.phone_number
    else:
        activity.organizer_phone = None
    return activity



@router.post("/",response_model=ActivityResponse)
def create_activity(activity:ActivityRequest, db:Session=Depends(get_db), current_user:User=Depends(get_current_user)):
    if datetime.combine(activity.date, activity.time) <= datetime.now():
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail="Activity must be scheduled for a future date and time")

    new_activity = Activity(
                            title = activity.title,
                            description = activity.description,
                            category = activity.category,
                            location = activity.location,
                            date = activity.date ,
                            time = activity.time ,
                            max_participants = activity.max_participants,
                            organizer_id = current_user.id
                           )
    db.add(new_activity)
    db.commit()
    db.refresh(new_activity)
    logger.info(f"Sucess: User {current_user.email} created an activity '{new_activity.title}'")
    return new_activity


@router.put("/{activity_id}",response_model=ActivityUpdateResponse)
def update_activity(activity_id:int, update_data:ActivityUpdateRequest, db:Session=Depends(get_db), current_user:User=Depends(get_current_user)):
    activity = db.query(Activity).filter(Activity.id==activity_id).first()
    if not activity:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,detail=f"Activity with id '{activity_id}' not found!")
    if current_user.id != activity.organizer_id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN,detail="You are not owner of this activity")
    
    if activity.status=='CANCELLED':
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail="Cancelled activity cannot be updated")

    if update_data.title is not None:
        activity.title = update_data.title
    if update_data.description is not None:
        activity.description = update_data.description
    if update_data.category is not None:
        activity.category = update_data.category
    if update_data.location is not None:
        activity.location = update_data.location
    if update_data.date is not None or update_data.time is not None:
        check_date = update_data.date or activity.date
        check_time = update_data.time or activity.time
        if datetime.combine(check_date, check_time) <= datetime.now():
            raise HTTPException(status_code=400, detail="Activity must be scheduled for a future date")
    if update_data.max_participants is not None:
        participants_count = db.query(Participation).filter( Participation.activity_id==activity.id,
                                                                    Participation.status=="APPROVED").count()
        
        if participants_count<update_data.max_participants and activity.status=="FULL":
            activity.status = "OPEN"
        if participants_count>update_data.max_participants:
            raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Cannot set max_participants({update_data.max_participants}) lower than current approved count ({participants_count})!")
        activity.max_participants = update_data.max_participants
    
    db.commit()
    db.refresh(activity)
    logger.info(f"Success: User '{current_user.email}' updated activity '{activity.title}' successfully!")
    return activity
    

@router.delete("/{activity_id}", status_code=status.HTTP_200_OK)
def cancel_activity(activity_id:int, db:Session=Depends(get_db), current_user:User=Depends(get_current_user)):
    activity = db.query(Activity).filter(Activity.id == activity_id).first()
    if not activity:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,detail="Activity not found")
    if activity.organizer_id != current_user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN,detail="You are not owner of this activity")
    if activity.status == "CANCELLED":
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail="Cannot cancel an already cancelled activity")
    if activity.status == "COMPLETED":
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail="Cannot cancel 'COMPLETED' activity")
    
    activity.status = "CANCELLED"
    db.commit()
    logger.info(f"Success: User '{current_user.email}' cancelled an activity '{activity.title}'")
    return {"message":f"Activity '{activity.title}' has been cancelled!"}