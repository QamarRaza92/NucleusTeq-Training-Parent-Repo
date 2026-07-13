import uuid

# POST:- Send Participation Request
#--------------------------------------------------------------------------------------------------------------------------------------
def test_participation_success(client, created_activity, another_authenticated_user):
    response = client.post(f"/activities/{created_activity['id']}/requests", headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    assert response.status_code == 200
    data = response.json()
    assert data['id'] > 0
    assert data['activity_id'] == created_activity['id']
    assert data['participant_id'] == another_authenticated_user['id']
    assert data['status'] == "PENDING"


def test_participation_without_token_throws_401(client, created_activity):
    response = client.post(f"/activities/{created_activity['id']}/requests")
    assert response.status_code == 401
    assert response.json()['detail'] == "Not authenticated"


def test_participation_duplication_throws_400(client, created_activity, another_authenticated_user):
    # Applied first time
    client.post(f"/activities/{created_activity['id']}/requests", headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})

    # Applying again
    response = client.post(f"/activities/{created_activity['id']}/requests", headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == "You have already applied for participation"


def test_participation_by_owner_throws_400(client, created_activity):
    response = client.post(f"/activities/{created_activity['id']}/requests", headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == "Organizers cannot join their own activity"


def test_participation_using_invalid_id_throws_404(client, created_activity, another_authenticated_user):
    invalid_activity_id = 99999
    response = client.post(f"/activities/{invalid_activity_id}/requests", headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    assert response.status_code == 404
    assert response.json()['detail'] == "Failed To Participate: Activity not found"


# def test_participation_for_completed_activity_throws_400(client, created_activity):
#     completed_activity_id = 13
#     response = client.post(f"/activities/{completed_activity_id}/requests", headers={"Authorization": f"Bearer {created_activity['token']}"})
#     assert response.status_code == 400
#     assert response.json()['detail'] == "Failed to participate! Activity is 'COMPLETED'"


def test_participation_for_cancelled_activity_throws_400(client, created_activity, another_authenticated_user):
    # 1:- First cancelling the activity
    client.delete(f"/activity/{created_activity['id']}",headers={"Authorization": f"Bearer {created_activity['token']}"})

    #2:- Then apply for it sends 400
    response = client.post(f"/activities/{created_activity['id']}/requests", headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == "Failed To Participate: Activity is 'CANCELLED'"


def test_participation_for_full_activity_throws_400(client, created_activity, another_authenticated_user, update_activity_data):
    #Step1: Register and login with dummy user1
    email1 = f"{uuid.uuid4()}@example.com"
    password1 = "test2@123"
    client.post(
        "/auth/register",
        json={"name": "Another User1","phone_number": "9876543210","city": "Mumbai","bio": "Another","email": email1,"password": password1})

    dummy_user = client.post("/auth/login",json={"email": email1,"password": password1})
    dummy_user_data = dummy_user.json()
    #Step2: Send Participation request by dummy user  1  
    req1 = client.post(f"/activities/{created_activity['id']}/requests",json={"activity_id":created_activity['id']},
                headers={"Authorization": f"Bearer {dummy_user_data['access_token']}"})
    
    #Step3: Update max seats to 1
    update_activity_data['max_participants'] = 1
    client.put(f"/activity/{created_activity['id']}", 
                          json=update_activity_data, 
                          headers={"Authorization": f"Bearer {created_activity['token']}"})
    
    #Step4: Approve participation requests by owner (created_activity owner)
    req1_data = req1.json()
    client.put(f"/activities/{created_activity['id']}/requests/{req1_data['id']}/approve",headers={"Authorization": f"Bearer {created_activity['token']}"})
   
    #Step5: Now Send Participation request by "another_authenticated_user"
    response = client.post(f"/activities/{created_activity['id']}/requests",json={"activity_id":created_activity['id']},
                headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    assert response.status_code == 400
#--------------------------------------------------------------------------------------------------------------------------------------
















# GET:- Get Participation Requests for an activity
#--------------------------------------------------------------------------------------------------------------------------------------
def test_get_request_success(client, created_activity):
    response = client.get(f"/activities/{created_activity['id']}/requests", headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 200

    
def test_get_request_without_token_throws_401(client, created_activity):
    response = client.get(f"/activities/{created_activity['id']}/requests")
    assert response.status_code == 401
    assert response.json()['detail'] == "Not authenticated"

    
def test_get_request_with_invalid_id_throws_404(client, created_activity):
    invalid_id = 99999
    response = client.get(f"/activities/{invalid_id}/requests", headers={"Authorization": f"Bearer {created_activity['token']}"})
    assert response.status_code == 404
    assert response.json()['detail'] == "Activity not found"


def test_get_request_by_non_owner_throws_403(client, created_activity, another_authenticated_user):
    response = client.get(f"/activities/{created_activity['id']}/requests", headers={"Authorization": f"Bearer {another_authenticated_user['token']}"})
    assert response.status_code == 403
    assert response.json()['detail'] == "You are not owner of this activity"
#--------------------------------------------------------------------------------------------------------------------------------------
















# PUT:- Approve Participation Requests
#--------------------------------------------------------------------------------------------------------------------------------------
def test_approve_request_success(client, participation_requested, authenticated_user):
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/approve",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 200
    data = response.json()
    assert data['id'] == participation_requested['id']
    assert data['status'] == "APPROVED"
    assert data['participant_id'] == participation_requested['participant_id']
    assert data['activity_id'] == participation_requested['activity_id']


def test_approve_request_without_token_throws_401(client, participation_requested):
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/approve")
    assert response.status_code == 401
    assert response.json()['detail'] == "Not authenticated"


def test_approve_request_with_invalid_activity_throws_404(client, participation_requested, authenticated_user):
    invalid_id = 999999
    response = client.put(f"activities/{invalid_id}/requests/{participation_requested['id']}/approve",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 404
    assert response.json()['detail'] == "Activity not found"


def test_approve_request_with_non_owner_token_throws_403(client, participation_requested):
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/approve",
                          headers={"Authorization": f"Bearer {participation_requested['token']}"})
    assert response.status_code == 403
    assert response.json()['detail'] == "You are not owner of this activity"


def test_approve_request_with_invalid_participation_id_token_throws_404(client, participation_requested, authenticated_user):
    invalid_participation_id = 99999999
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{invalid_participation_id}/approve",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 404
    assert response.json()['detail'] == "Request not found"


def test_approve_already_approved_request_throws_400(client, participation_requested, authenticated_user):
    # Already approving request
    client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/approve",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    
    # Approving again throws error
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/approve",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == "Request is already APPROVED"


def test_approve_already_rejected_request_throws_400(client, participation_requested, authenticated_user):
    # Rejecting the request first
    client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/reject",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    
    # Now Approving it again throws error
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/approve",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == "Request is already REJECTED"


def test_approve_request_when_seats_full_throws_400(client, participation_requested, authenticated_user, update_activity_data):
    #Step1: Register and login with dummy user1
    email1 = f"{uuid.uuid4()}@example.com"
    password1 = "test2@123"
    client.post(
        "/auth/register",
        json={"name": "Another User1","phone_number": "9876543210","city": "Mumbai","bio": "Another","email": email1,"password": password1})
    dummy_user = client.post("/auth/login",json={"email": email1,"password": password1})
    dummy_user_data = dummy_user.json()

    #Step2: Send Participation request by dummy user1  
    req1 = client.post(f"/activities/{participation_requested['activity_id']}/requests",
                       json={"activity_id":participation_requested['activity_id']},
                headers={"Authorization": f"Bearer {dummy_user_data['access_token']}"})
    
    #Step3: Approve participation request for req1 by owner (After this, seats will be 'FULL')
    req1_data = req1.json()
    client.put(f"/activities/{participation_requested['activity_id']}/requests/{req1_data['id']}/approve",
               headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    
    # Step4:- Update activity seats to 1
    update_activity_data['max_participants'] = 1
    step1 = client.put(f"/activity/{participation_requested['activity_id']}", 
                          json=update_activity_data,
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    
    # Step5:- Approving the request now throws 400
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/approve",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == f"Activity {update_activity_data['title']} is already full"
#--------------------------------------------------------------------------------------------------------------------------------------
















# PUT:- Reject Participation Requests
#--------------------------------------------------------------------------------------------------------------------------------------
def test_reject_request_success(client, participation_requested, authenticated_user):
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/reject",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 200
    data = response.json()
    assert data['id'] == participation_requested['id']
    assert data['status'] == "REJECTED"
    assert data['participant_id'] == participation_requested['participant_id']
    assert data['activity_id'] == participation_requested['activity_id']


def test_reject_request_without_token_throws_401(client, participation_requested):
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/reject")
    assert response.status_code == 401
    assert response.json()['detail'] == "Not authenticated"


def test_reject_request_with_invalid_activity_throws_404(client, participation_requested, authenticated_user):
    invalid_id = 999999
    response = client.put(f"activities/{invalid_id}/requests/{participation_requested['id']}/reject",
                           headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 404
    assert response.json()['detail'] == "Activity not found"


def test_reject_request_with_non_owner_token_throws_403(client, participation_requested):
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/reject",
                          headers={"Authorization": f"Bearer {participation_requested['token']}"})
    assert response.status_code == 403
    assert response.json()['detail'] == "You are not owner of this activity"


def test_reject_request_with_invalid_participation_id_token_throws_404(client, participation_requested, authenticated_user):
    invalid_participation_id = 99999999
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{invalid_participation_id}/reject",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 404
    assert response.json()['detail'] == "Request not found"


def test_reject_already_rejected_request_throws_400(client, participation_requested, authenticated_user):
    # Already rejecting request
    client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/reject",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    
    # Rejecting again throws error
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/reject",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == "Request is already REJECTED"


def test_reject_already_approved_request_throws_400(client, participation_requested, authenticated_user):
    # Approving the request first
    client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/approve",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    
    # Now Approving it again throws error
    response = client.put(f"activities/{participation_requested['activity_id']}/requests/{participation_requested['id']}/reject",
                          headers={"Authorization": f"Bearer {authenticated_user['token']}"})
    assert response.status_code == 400
    assert response.json()['detail'] == "Request is already APPROVED"
#--------------------------------------------------------------------------------------------------------------------------------------