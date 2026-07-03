from fastapi import APIRouter, HTTPException, status, Depends
from app.models.participation import Participation
from app.models.user import User 
from app.models.activity import Activity
from app.schemas.participation_dto import ParticipationRequest, ParticipationResponse
from app.db.database import get_db
from app.utils.dependencies import get_current_user
from sqlalchemy.orm import Session
import logging
from datetime import datetime

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/activities", tags = ["Participation"])

@router.post("/{activity_id}/requests", response_model = ParticipationResponse)
def send_participation_request(activity_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    activity = db.query(Activity).filter(Activity.id == activity_id).first()

    if not activity:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Failed To Participate: Activity not found")
    
    if datetime.combine(activity.date, activity.time) <= datetime.now():
        activity.status = "COMPLETED"
        db.commit()
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail="Failed to participate! Activity is 'COMPLETED'")   
     
    if activity.organizer_id == current_user.id:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Organizers cannot join their own activity")

    existing_request = db.query(Participation).filter(
                                                        Participation.activity_id == activity_id,
                                                        Participation.participant_id == current_user.id
                                                     ).first()

    if existing_request:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="You have already applied for participation")
    
    if activity.max_participants <= db.query(Participation).filter(Participation.activity_id == activity_id).count():
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail=f"Seats full for activity '{activity.title}'!")
    
    if activity.status == "COMPLETED":
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Failed To Participate: Activity is 'COMPLETED'")

    if activity.status == "FULL" :
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Failed To Participate: Activity is 'FULL'")
    
    if activity.status == "CANCELLED":
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Failed To Participate: Activity is 'CANCELLED'")

    new_request = Participation(activity_id = activity.id, participant_id = current_user.id)
    db.add(new_request)
    db.commit()
    db.refresh(new_request)
    logger.info(f"User '{current_user.email}' requested to join activity '{activity.title}'")
    return new_request



@router.get("/{activity_id}/requests")
def get_requests(activity_id: int,db: Session = Depends(get_db),current_user: User = Depends(get_current_user)):
    activity = db.query(Activity).filter(
                                            Activity.id == activity_id
                                        ).first()
    if not activity:
        raise HTTPException(
                            status_code=404,
                            detail="Activity not found"
                            )
    if activity.organizer_id != current_user.id:
        raise HTTPException(
                            status_code=403,
                            detail="You are not owner of this activity"
                            )
    requests = db.query(Participation).filter(
                                                Participation.activity_id == activity_id,Participation.status == "PENDING"
                                             ).all()
    return [
            {
                "id": r.id,
                "participant_id": r.participant_id,
                "participant_name": r.user.name,
                "status": r.status
            }
            for r in requests
          ]




@router.put("/{activity_id}/requests/{request_id}/approve", response_model=ParticipationResponse)
def approve_request(activity_id: int, request_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    activity = db.query(Activity).filter(Activity.id==activity_id).with_for_update().first()

    if not activity:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Activity not found")

    if activity.organizer_id != current_user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN,detail="You are not owner of this activity")

    request = db.query(Participation).filter(
                                             Participation.id==request_id,
                                             Participation.activity_id==activity.id
                                            ).first()

    if not request:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,detail="Request No Found")

    if request.status != "PENDING":
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail=f"Request is {request.status}")

    approved_requests = db.query(Participation).filter(
                                                        Participation.activity_id==activity.id,
                                                        Participation.status=="APPROVED"
                                                       ).count()

    if approved_requests >= activity.max_participants:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,detail=f"Activity {activity.title} is already full")

    request.status = "APPROVED"

    if approved_requests + 1 >= activity.max_participants:
        activity.status = "FULL"

    db.commit()
    db.refresh(request)
    logger.info(f"Owner '{current_user.email}' approved request '{request.id}' for activity '{activity.title}'")
    return request


@router.put("/{activity_id}/requests/{request_id}/reject", response_model = ParticipationResponse)
def reject_request(activity_id: int, request_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    activity = db.query(Activity).filter(Activity.id==activity_id).first()

    if not activity:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Activity not found")

    if activity.organizer_id != current_user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="You are not owner of this activity")

    request = db.query(Participation).filter(
                                             Participation.id == request_id,
                                             Participation.activity_id == activity_id
                                            ).first()

    if not request:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Request not found")

    if request.status != "PENDING":
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=f"Request is {request.status}")

    request.status = "REJECTED"
    db.commit()
    db.refresh(request)
    logger.info(f"Owner '{current_user.email}' rejected request '{request_id}' for activity '{activity.title}'")
    return request
