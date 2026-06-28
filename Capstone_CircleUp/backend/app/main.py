from fastapi import FastAPI 
from app.db.database import Base,engine
from app.routers import auth
from app.routers import users
from app.routers import activities
from app.routers import participations
from fastapi.middleware.cors import CORSMiddleware

Base.metadata.create_all(bind=engine)

app = FastAPI(title="CircleUp")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router)
app.include_router(users.router)
# app.include_router(activities.router)
# app.include_router(participations.router)

@app.get("/")
def root():
    return {"Message":"Testing DB connection."}