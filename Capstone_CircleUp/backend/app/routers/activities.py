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

#Get all availabe activities (Except cancelled ones)
@router.get("/",response_model=list[ActivityResponse], status_code=status.HTTP_200_OK, summary="Browse all activites created by all users",
            description="Returns all non-cancelled activities. Supports filtering by category, location, date and sorting by date.")
def get_all_activities(
                        location: Optional[str] = Query(default=None, description="Filter by location"),
                        category: Optional[str] = Query(default=None, description="Filter by category e.g. SPORTS, MUSIC"),
                        date: Optional[date] = Query(default=None, description="Filter by date (YYYY-MM-DD)"),
                        sort: Optional[str] = Query(default=None, description="Sort by date: date_asc or date_desc"),
                        db: Session = Depends(get_db),
                        current_user: User = Depends(get_current_user)
                      ):
    query = db.query(Activity).filter(Activity.status!="CANCELLED")
    # apply optional filters
    if location:
        query = query.filter(Activity.location.ilike(f"%{location}%"))
    if category:
        query = query.filter(Activity.category == category.upper())
    if date:
        query = query.filter(Activity.date == date)

    # apply sorting
    if sort == "date_asc":
        query = query.order_by(Activity.date.asc())
    elif sort == "date_desc":
        query = query.order_by(Activity.date.desc())

    activities = query.all()

    # lazily compute COMPLETED status on read
    for activity in activities:
        activity.status = activity.computed_status

    logger.info(f"User '{current_user.email}' fetched all activities")
    return activities




#Get activities created by me
@router.get("/my-created", response_model=list[ActivityResponse], status_code=status.HTTP_200_OK, summary="My created activities",
    description="Returns all activities created by the currently logged-in user.")
def my_created_activities(db: Session = Depends(get_db),current_user: User = Depends(get_current_user)):
    activities = db.query(Activity).filter(
        Activity.organizer_id == current_user.id
    ).all()
    for activity in activities:
        activity.status = activity.computed_status
    return activities


#Get activities which i joined successfully
@router.get("/my-joined", response_model=list[ActivityResponse], status_code=status.HTTP_200_OK, summary="My joined activities",
    description="Returns all activities where the current user's participation request is approved.")
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


#Get Pending request of users who applied to my activities
@router.get("/my-pending", response_model=list[ActivityResponse], status_code=status.HTTP_200_OK, summary="My pending requests",
    description="Returns all activities where the current user has a pending participation request.")
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



#Get activities where my request was rejected
@router.get("/my-rejected", response_model=list[ActivityResponse], status_code=status.HTTP_200_OK, summary="My rejected requests",
    description="Returns all activities where the current user's participation request was rejected.")
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



# Get activity by ID
@router.get("/{activity_id}",response_model=ActivityResponse, status_code=status.HTTP_200_OK, summary="Get activity detail",
    description="Returns full detail of a single activity. Phone number is visible only to the organizer or approved participants.",
    responses={404: {"description": "Activity not found"}})
def get_activity(activity_id:int, db:Session=Depends(get_db), current_user:User=Depends(get_current_user)):
    activity = db.query(Activity).filter(Activity.id==activity_id).first()
    if not activity:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Activity not found")
    
    activity.status = activity.computed_status

    #Show organizer phone only to organizer or approved participants
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



# Create new activity (POST)
@router.post("/",response_model=ActivityResponse, status_code=status.HTTP_201_CREATED, summary="Create a new activity",
    description="Creates a new activity. Date and time must be in the future.",
    responses={400: {"description": "Activity date/time must be in the future"}})
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
    logger.info(f"Success: User '{current_user.email}' created an activity '{new_activity.title}'")
    return new_activity



# To Update an activity (PUT)
@router.put("/{activity_id}",response_model=ActivityUpdateResponse, status_code=status.HTTP_200_OK, summary="Update an activity",
    description="Updates an existing activity. Only the organizer can update. Cancelled activities cannot be updated.",
    responses={
                403: {"description": "Not the owner of this activity"},
                404: {"description": "Activity not found"},
                400: {"description": "Cancelled activity or invalid max_participants"}
              })
def update_activity(activity_id:int, update_data:ActivityUpdateRequest, db:Session=Depends(get_db), current_user:User=Depends(get_current_user)):
    activity = db.query(Activity).filter(Activity.id==activity_id).first()
    if not activity:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,detail=f"Activity with id '{activity_id}' not found!")
    if current_user.id != activity.organizer_id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN,detail="You are not owner of this activity")
    
    if activity.status=='CANCELLED':
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail="Cancelled activity cannot be updated")
    if activity.status=='COMPLETED':
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail="Completed activity cannot be updated")

    #Add checks if fields are not empty, only then update data
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
        
    # Since after accepting request if seats are full; the status was changed to "FULL". Now if we increase seats, status should be "OPEN" now
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
    

# Delete an activity route
@router.delete("/{activity_id}", status_code=status.HTTP_200_OK, summary="Cancel an activity",
    description="Cancels an activity by setting its status to CANCELLED. Only the organizer can cancel. Completed activities cannot be cancelled.",
    responses={
                403: {"description": "Not the owner of this activity"},
                404: {"description": "Activity not found"},
                400: {"description": "Activity already cancelled or completed"}
              })
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