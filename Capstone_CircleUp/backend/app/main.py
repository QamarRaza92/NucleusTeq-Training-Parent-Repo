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