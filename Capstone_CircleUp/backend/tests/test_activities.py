import datetime
import uuid
from utils import convert_to_error_messages


#Testing for 'POST' activity route
#--------------------------------------------------------------------------------------------------------------------------------------
def test_create_activity_success(client, authenticated_user):
    response = client.post("/activity",
                           json={
            "title": "Python Workshop",
            "description": "Learn FastAPI",
            "category": "EDUCATION",
            "location": "Indore",
            "date": (datetime.date.today() + datetime.timedelta(days=5)).isoformat(),
            "time": "18:00:00",
            "max_participants": 20
                           },
                           headers={
                               "Authorization": f"Bearer {authenticated_user['token']}"
                           }
                           )
    assert response.status_code == 201

    data = response.json()
    assert data['id'] > 0
    assert data['title'] == "Python Workshop"
    assert data['description'] == 'Learn FastAPI'   
    assert data['status'] == "OPEN"
    assert data['category'] == "EDUCATION"
    assert data['location'] == 'Indore'
    assert data['date'] == (datetime.date.today() + datetime.timedelta(days=5)).isoformat()
    assert data["time"].startswith("18:00")
    assert data['max_participants'] == 20
    assert data['organizer_id'] == authenticated_user['id']



def test_create_activity_without_token_sends_401(client, authenticated_user):
    response = client.post("/activity",
                           json={
            "title": "Python Workshop",
            "description": "Learn FastAPI",
            "category": "EDUCATION",
            "location": "Indore",
            "date": (datetime.date.today() + datetime.timedelta(days=5)).isoformat(),
            "time": "18:00:00",
            "max_participants": 20
                           },
                           headers={
                           }
                           )
    assert response.status_code == 401
    assert response.json()['detail'] == "Not authenticated"



def test_create_activity_with_title_less_than_3_chars_throws_422(client, authenticated_user):
    response = client.post("/activity",
                           json={
            "title": "Py",
            "description": "Learn FastAPI",
            "category": "EDUCATION",
            "location": "Indore",
            "date": (datetime.date.today() + datetime.timedelta(days=5)).isoformat(),
            "time": "18:00:00",
            "max_participants": 20
                           },
                           headers={
                               "Authorization": f"Bearer {authenticated_user['token']}"
                           }
                           )
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "String should have at least 3 characters" in error_messages


def test_create_activity_with_random_city_throws_422(client, authenticated_user):
    response = client.post("/activity",
                           json={
            "title": "Python Training",
            "description": "Learn FastAPI",
            "category": "EDUCATION",
            "location": "random",
            "date": (datetime.date.today() + datetime.timedelta(days=5)).isoformat(),
            "time": "18:00:00",
            "max_participants": 20
                           },
                           headers={
                               "Authorization": f"Bearer {authenticated_user['token']}"
                           }
                           )
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "Value error, City Not available" in error_messages



def test_create_activity_with_random_category_throws_422(client, authenticated_user):
    response = client.post("/activity",
                           json={
            "title": "Python Training",
            "description": "Learn FastAPI",
            "category": "random",
            "location": "Indore",
            "date": (datetime.date.today() + datetime.timedelta(days=5)).isoformat(),
            "time": "18:00:00",
            "max_participants": 20
                           },
                           headers={
                               "Authorization": f"Bearer {authenticated_user['token']}"
                           }
                           )
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "Input should be 'SPORTS', 'EDUCATION', 'MUSIC', 'ART', 'CHARITY' or 'CORPORATE'" in error_messages



def test_create_activity_with_max_participants_less_than_1_throws_422(client, authenticated_user):
    response = client.post("/activity",
                           json={
            "title": "Python Training",
            "description": "Learn FastAPI",
            "category": "EDUCATION",
            "location": "Indore",
            "date": (datetime.date.today() + datetime.timedelta(days=5)).isoformat(),
            "time": "18:00:00",
            "max_participants": 0
                           },
                           headers={
                               "Authorization": f"Bearer {authenticated_user['token']}"
                           }
                           )
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "Input should be greater than 0" in error_messages



def test_create_activity_with_past_date_throws_400(client, authenticated_user):
    response = client.post("/activity",
                           json={
            "title": "Python Training",
            "description": "Learn FastAPI",
            "category": "EDUCATION",
            "location": "Indore",
            "date": (datetime.date.today() - datetime.timedelta(days=1)).isoformat(),
            "time": "00:00:00",
            "max_participants": 2
                           },
                           headers={
                               "Authorization": f"Bearer {authenticated_user['token']}"
                           }
                           )
    assert response.status_code == 400
    assert response.json()['detail'] == "Activity must be scheduled for a future date and time"
#--------------------------------------------------------------------------------------------------------------------------------------












#Testing for 'GET' activity route
#--------------------------------------------------------------------------------------------------------------------------------------
def test_get_all_activities_success(client, authenticated_user, created_activity):
    response = client.get(
                          "/activity/",
                          headers={"Authorization": f"Bearer {created_activity['token']}"}
                         )
    assert response.status_code == 200
    activities = response.json()
    activity = next((a for a in activities if a["id"] == created_activity["id"]), None)
    assert activity is not None
    assert activity['id'] > 0
    assert activity['title'] == created_activity['title']
    assert activity['description'] == created_activity['description']
    assert activity['category'] == created_activity["category"]
    assert activity['location'] == created_activity['location']
    assert activity['max_participants'] == created_activity['max_participants']
    assert activity['status'] == created_activity['status']
    assert activity['organizer_id'] == authenticated_user['id']




def test_get_activity_by_id_as_organizer(client, authenticated_user, created_activity):
    response = client.get(f"/activity/{created_activity['id']}",headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 200
    data = response.json()
    assert data['id'] > 0
    assert data['title'] == created_activity['title']
    assert data['description'] == created_activity['description']
    assert data['category'] == created_activity["category"]
    assert data['location'] == created_activity['location']
    assert data['max_participants'] == created_activity['max_participants']
    assert data['status'] == created_activity['status']
    assert data['organizer_id'] == authenticated_user['id']
    assert data['organizer_phone'] is not None



def test_get_activity_by_id_as_non_participant(client, authenticated_user, created_activity, another_authenticated_user):
    response = client.get(f"/activity/{created_activity['id']}",headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    assert response.status_code == 200
    data = response.json()

    print("Owner ID:", authenticated_user["id"])
    print("Other User ID:", another_authenticated_user["id"])
    assert data['id'] > 0
    assert data['title'] == created_activity['title']
    assert data['description'] == created_activity['description']
    assert data['category'] == created_activity["category"]
    assert data['location'] == created_activity['location']
    assert data['max_participants'] == created_activity['max_participants']
    assert data['status'] == created_activity['status']
    assert data['organizer_id'] == authenticated_user['id']
    assert data['organizer_phone'] is None


def test_get_my_created_activities_success(client, created_activity, authenticated_user):
    response = client.get("/activity/my-created",
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 200
    activities = response.json()
    activity = next((a for a in activities if a["id"] == created_activity["id"]), None)
    assert activity is not None
    assert activity['id'] > 0
    assert activity['title'] == created_activity['title']
    assert activity['description'] == created_activity['description']
    assert activity['category'] == created_activity["category"]
    assert activity['location'] == created_activity['location']
    assert activity['max_participants'] == created_activity['max_participants']
    assert activity['status'] == created_activity['status']
    assert activity['organizer_id'] == authenticated_user['id']


def test_get_activity_by_id_using_invalid_id_throws_404(client, authenticated_user):
    invalid_id = 999
    response = client.get(f"/activity/{invalid_id}",headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 404
    assert response.json()['detail'] == "Activity not found"


def test_get_activity_by_id_for_approved_participant_phone_visibility(client, created_activity, another_authenticated_user):
    #Step1: Send a participation request by "another_authenticated_user"
    participation_request = client.post(f"/activities/{created_activity['id']}/requests",json={"activity_id":created_activity['id']},
                headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    
    #Step2: Approve request by organizer
    client.put(f"/activities/{created_activity['id']}/requests/{participation_request.json()['id']}/approve",
               headers={"Authorization": f"Bearer {created_activity['token']}"})
    
    #Check for organizers info by "another_authenticated_user"
    response = client.get(f"/activity/{created_activity['id']}",headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    assert response.status_code == 200
    data = response.json()
    assert data['organizer_phone'] == created_activity['organizer_phone']
# --------------------------------------------------------------------------------------------------------------------------------------















#Testing for 'PUT' update_activity route
#--------------------------------------------------------------------------------------------------------------------------------------
def test_update_activity_success(client, created_activity, update_activity_data):
    response = client.put(f"/activity/{created_activity['id']}", 
                          json=update_activity_data,
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 200
    data = response.json()
    assert data['title'] == update_activity_data['title']
    assert data['description'] == update_activity_data['description']
    assert data['category'] == update_activity_data['category']
    assert data['location'] == update_activity_data['location']
    assert data['status'] == 'OPEN'
    assert data['max_participants'] == update_activity_data['max_participants']



def test_update_activity_by_non_owner_throws_403(client, created_activity, update_activity_data, another_authenticated_user):
    response = client.put(f"/activity/{created_activity['id']}", 
                          json=update_activity_data,
                          headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    assert response.status_code == 403
    assert response.json()['detail'] == 'You are not owner of this activity'



def test_update_cancelled_activity_throws_400(client, created_activity, update_activity_data):
    client.delete(f"/activity/{created_activity['id']}",headers={"Authorization": f"Bearer {created_activity['token']}"})
    response = client.put(f"/activity/{created_activity['id']}", 
                          json=update_activity_data,
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == 'Cancelled activity cannot be updated'



def test_update_activity_with_invalid_activity_id_throws_404(client, update_activity_data, created_activity):
    id = 1000
    response = client.put(f"/activity/{id}", 
                          json=update_activity_data,
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 404
    assert response.json()['detail'] == f"Activity with id '{id}' not found!"



def test_update_activity_with_past_date_throws_400(client, update_activity_data, created_activity):
    update_activity_data['date'] = (datetime.date.today() - datetime.timedelta(days=5)).isoformat()
    response = client.put(f"/activity/{created_activity['id']}", 
                          json=update_activity_data, 
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == "Activity must be scheduled for a future date"



def test_update_activity_with_max_participant_lower_than_current_approved_throws_400(client, update_activity_data,
                                                                                      created_activity, another_authenticated_user):
    #Step1: Register and login with dummy user
    email = f"{uuid.uuid4()}@example.com"
    password = "test@123"
    client.post(
        "/auth/register",
        json={"name": "Another User","phone_number": "9876543210","city": "Mumbai","bio": "Another","email": email,"password": password})

    dummy_user = client.post("/auth/login",json={"email": email,"password": password})
    dummy_user_data = dummy_user.json()
    #Step2: Send Participation request by dummy user    
    req1 = client.post(f"/activities/{created_activity['id']}/requests",json={"activity_id":created_activity['id']},
                headers={"Authorization": f"Bearer {dummy_user_data['access_token']}"})
    
    #Step3: Send Participation request by "another_authenticated_user"
    req2 = client.post(f"/activities/{created_activity['id']}/requests",json={"activity_id":created_activity['id']},
                headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    
    #Step4: Approve both participation requests by owner (created_activity owner)
    req1_data = req1.json()
    req2_data = req2.json()
    client.put(f"/activities/{created_activity['id']}/requests/{req1_data['id']}/approve",headers={"Authorization": f"Bearer {created_activity['token']}"})
    client.put(f"/activities/{created_activity['id']}/requests/{req2_data['id']}/approve",headers={"Authorization": f"Bearer {created_activity['token']}"})
    participants_count = 2

    #Step5: Now we check for updating max_participants lower than approved
    update_activity_data['max_participants'] = 1
    response = client.put(f"/activity/{created_activity['id']}", 
                          json=update_activity_data, 
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == f"Cannot set max_participants({update_activity_data['max_participants']}) lower than current approved count ({participants_count})!"



def test_update_activity_with_status_full_changes_to_open_when_increase_max_participant_count(client, update_activity_data,
                                                                                      created_activity, another_authenticated_user):
    #Step1: Register and login with dummy user
    email = f"{uuid.uuid4()}@example.com"
    password = "test@123"
    client.post(
        "/auth/register",
        json={"name": "Another User","phone_number": "9876543210","city": "Mumbai","bio": "Another","email": email,"password": password})

    dummy_user = client.post("/auth/login",json={"email": email,"password": password})
    dummy_user_data = dummy_user.json()
    #Step2: Send Participation request by dummy user    
    req1 = client.post(f"/activities/{created_activity['id']}/requests",json={"activity_id":created_activity['id']},
                headers={"Authorization": f"Bearer {dummy_user_data['access_token']}"})
    
    #Step3: Send Participation request by "another_authenticated_user"
    req2 = client.post(f"/activities/{created_activity['id']}/requests",json={"activity_id":created_activity['id']},
                headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    
    #Step4: Approve both participation requests by owner (created_activity owner)
    req1_data = req1.json()
    req2_data = req2.json()
    client.put(f"/activities/{created_activity['id']}/requests/{req1_data['id']}/approve",headers={"Authorization": f"Bearer {created_activity['token']}"})
    client.put(f"/activities/{created_activity['id']}/requests/{req2_data['id']}/approve",headers={"Authorization": f"Bearer {created_activity['token']}"})
    participants_count = 2

    #Step5: Now we check for updating max_participants lower than approved
    update_activity_data['max_participants'] = 5
    response = client.put(f"/activity/{created_activity['id']}", 
                          json=update_activity_data, 
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 200
    assert response.json()['status'] == "OPEN"



def test_update_activity_with_title_less_than_3_chars_throws_422(client, created_activity, update_activity_data):
    update_activity_data['title'] = "Up" 
    response = client.put(f"/activity/{created_activity['id']}", 
                          json=update_activity_data,
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "String should have at least 3 characters" in error_messages


def test_update_activity_with_random_city_throws_422(client, created_activity, update_activity_data):
    update_activity_data['location'] = "random"
    response = client.put(f"/activity/{created_activity['id']}", 
                          json=update_activity_data,
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "Value error, City Not available" in error_messages



def test_update_activity_with_random_category_throws_422(client, created_activity, update_activity_data):
    update_activity_data['category'] = "random"
    response = client.put(f"/activity/{created_activity['id']}", 
                          json=update_activity_data,
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "Input should be 'SPORTS', 'EDUCATION', 'MUSIC', 'ART', 'CHARITY' or 'CORPORATE'" in error_messages



def test_update_activity_with_max_participants_less_than_1_throws_422(client, created_activity, update_activity_data):
    update_activity_data['max_participants'] = 0
    response = client.put(f"/activity/{created_activity['id']}", 
                          json=update_activity_data,
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "Input should be greater than 0" in error_messages
#--------------------------------------------------------------------------------------------------------------------------------------

















#Testing for 'Delete' activity route
#--------------------------------------------------------------------------------------------------------------------------------------
def test_cancel_activity_by_owner_success(client, created_activity):
    response = client.delete(f"/activity/{created_activity['id']}",headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 200
    assert response.json()['message'] == f"Activity '{created_activity['title']}' has been cancelled!"



def test_cancel_activity_by_unauthorized_user_throws_403(client, created_activity, another_authenticated_user):
    response = client.delete(f"/activity/{created_activity['id']}",headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    assert response.status_code == 403
    assert response.json()['detail'] == "You are not owner of this activity"



def test_cancel_activity_without_token_throws_401(client, created_activity):
    response = client.delete(f"/activity/{created_activity['id']}")
    assert response.status_code == 401
    assert response.json()['detail'] == "Not authenticated"



def test_cancel_activity_with_invalid_id_throws_404(client, created_activity):
    invalid_activity_id = 999
    response = client.delete(f"/activity/{invalid_activity_id}",headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 404
    assert response.json()['detail'] == "Activity not found"



def test_cancel_already_cancelled_activity_throws_400(client, created_activity):
    #Cancelling 1st time
    client.delete(f"/activity/{created_activity['id']}",headers={"Authorization": f"Bearer {created_activity['token']}"})

    #Cancelling again
    response = client.delete(f"/activity/{created_activity['id']}",headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == "Cannot cancel an already cancelled activity" 

#--------------------------------------------------------------------------------------------------------------------------------------