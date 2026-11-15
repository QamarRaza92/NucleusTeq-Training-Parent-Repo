from fastapi import FastAPI 
from app.db.database import Base,engine
from app.routers import auth
from app.routers import users
from app.routers import activities
from app.routers import participations
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import FileResponse

Base.metadata.create_all(bind=engine)

app = FastAPI(title="CircleUp")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.mount("/static", StaticFiles(directory="../frontend"), name="static")

app.include_router(auth.router)
app.include_router(users.router)
app.include_router(activities.router)
app.include_router(participations.router)

@app.get("/")
def root():
    return FileResponse("../frontend/html/login.html")

@app.get("/register")
def register():
    return FileResponse("../frontend/html/register.html")

@app.get("/dashboard")
def dashboard():
    return FileResponse("../frontend/html/dashboard.html")

@app.get("/activity/{activity_id}")
def activity_page(activity_id: int):
    return FileResponse("../frontend/html/activity.html")

@app.get("/profile")
def profile():
    return FileResponse("../frontend/html/profile.html")

@app.get("/edit-profile")
def edit_profile():
    return FileResponse("../frontend/html/edit-profile.html")

@app.get("/browse-activities")
def browse():
    return FileResponse("../frontend/html/browse-activities.html")

@app.get("/activity-detail/{activity_id}")
def activity_detail(activity_id: int):
    return FileResponse("../frontend/html/activity-detail.html")

@app.get("/edit-activity/{activity_id}")
def edit_activity(activity_id: int):
    return FileResponse("../frontend/html/edit-activity.html")

@app.get("/create-activity")
def create_activity():
    return FileResponse("../frontend/html/create-activity.html")

@app.get("/my-activities")
def my_activities():
    return FileResponse("../frontend/html/my-activities.html")