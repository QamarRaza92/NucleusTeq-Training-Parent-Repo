from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from app.db.database import Base, get_db
from app.main import app


import os
from dotenv import load_dotenv

load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), "../.env.test"))

TEST_DATABASE_URL = os.getenv("TEST_DATABASE_URL")

engine = create_engine(TEST_DATABASE_URL)

TestingSessionLocal = sessionmaker(
    autocommit=False,
    autoflush=False,
    bind=engine
)


def override_get_db():
    db = TestingSessionLocal()
    try:
        yield db
    except Exception:
        db.rollback()
        raise
    finally:
        db.close()

app.dependency_overrides[get_db] = override_get_db



import pytest
from fastapi.testclient import TestClient
import uuid
import datetime


@pytest.fixture(scope="session", autouse=True)
def setup_database():
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)

    yield

    Base.metadata.drop_all(bind=engine)


@pytest.fixture
def client():
    return TestClient(app)


@pytest.fixture
def registered_user(client):
    email = f"{uuid.uuid4()}@example.com"
    password = "test@123"
    
    client.post(
                "/auth/register", 
                json={
                      "name":"test",
                      "phone_number":"8319417300",
                      "city":"Indore",
                      "bio":"test",
                      "email":email,
                      "password":password
                     }
                )
    return {"email":email, "password":password}


@pytest.fixture
def authenticated_user(client, registered_user):
    response = client.post("/auth/login",json={"email": registered_user["email"],"password": registered_user["password"]})

    assert response.status_code == 200
    data = response.json()
    return {
        "token": data["access_token"],
        "id": data["id"],
        "name": data["name"],
        "email": data["email"]
        }


@pytest.fixture
def another_authenticated_user(client):
    email = f"{uuid.uuid4()}@example.com"
    password = "test@123"
    client.post(
        "/auth/register",
        json={
            "name": "Another User",
            "phone_number": "9876543210",
            "city": "Mumbai",
            "bio": "Another",
            "email": email,
            "password": password
        }
    )

    response = client.post("/auth/login",json={"email": email,"password": password})
    assert response.status_code == 200 
    data = response.json()
    return {
        "token": data["access_token"],
        "id": data["id"],
        "name": data["name"],
        "email": data["email"]
    }


@pytest.fixture
def created_activity(client, authenticated_user):
    response = client.post("/activity",
                           json={
            "title": "Python Workshop",
            "description": "Learn FastAPI",
            "category": "EDUCATION",
            "location": "Indore",
            "date": (datetime.date.today() + datetime.timedelta(days=5)).isoformat(),
            "time": "18:00:00",
            "max_participants": 2
            },
                           headers={
                               "Authorization": f"Bearer {authenticated_user['token']}"
                           })
    assert response.status_code == 201

    data = response.json()
    return {
        "id": data["id"],
        "title": data["title"],
        "description": data["description"],
        "category": data["category"],
        "location": data["location"],
        "date": data["date"],
        "time": data["time"],
        "status": data["status"],
        "max_participants": data["max_participants"],
        "organizer_id": data["organizer_id"],
        "token": authenticated_user["token"],
        "organizer_phone": "8319417300"
    }


@pytest.fixture
def participation_requested(client, created_activity, another_authenticated_user):
    response = client.post(f"/activities/{created_activity['id']}/requests", headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    data = response.json()
    return {
        "id": data['id'],
        "activity_id": data['activity_id'],
        "participant_id": data['participant_id'],
        "status": data['status'],
        "token": another_authenticated_user['token']
    }


@pytest.fixture
def update_activity_data(client):
    return {
            "title": "Updated Event",
            "description": "Updated desc",
            "category": "EDUCATION",
            "location": "Indore",
            "date": (datetime.date.today() + datetime.timedelta(days=5)).isoformat(),
            "time": "18:00:00",
            "max_participants": 1
    }
