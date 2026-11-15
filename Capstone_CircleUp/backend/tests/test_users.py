from utils import convert_to_error_messages

def test_profile_success(client, authenticated_user):
    response = client.get(
        "/users/dashboard/profile",
        headers={
            "Authorization": f"Bearer {authenticated_user['token']}"
        }
        )
    assert response.status_code == 200

    data = response.json()
    assert data['name'] == authenticated_user['name']
    assert data['email'] == authenticated_user['email']

    assert data['phone'] == "8319417300"
    assert data['city'] == "Indore"
    assert data['bio'] == "test"


def test_getMyActivities_success(client, authenticated_user):
    response = client.get("/users/me/activities",
                          headers={
                              "Authorization": f"Bearer {authenticated_user['token']}"
                          })
    assert response.status_code == 200

    data = response.json()
    assert 'created_activities' in data
    assert 'joined_activities' in data
    assert 'pending_requests' in data


def test_getDashboard_success(client, authenticated_user):
    response = client.get("/users/dashboard",
                          headers={
                              "Authorization": f"Bearer {authenticated_user['token']}"
                          })
    assert response.status_code == 200
    data = response.json()
    assert "user" in data
    assert "stats" in data
    assert "activities" in data
    assert "requests" in data

    assert "created" in data["stats"]
    assert "joined" in data["stats"]
    assert "pending" in data["stats"]
    assert "completed" in data["stats"]


def test_update_profile_success(client, authenticated_user):
    response = client.put(
        "/users/me",
        json={
            "name": "Update name",
            "phone_number": "8319417301",
            "city": "Mumbai",
            "bio": "Update bio"
        },
        headers={
            "Authorization": f'Bearer {authenticated_user["token"]}'
        }
        )
    assert response.status_code == 200

    data = response.json()
    assert data['name'] == "Update name"
    assert data['phone_number'] == "8319417301"
    assert data['city'] == "Mumbai"
    assert data['bio'] == "Update bio"


def test_update_profile_with_no_fields(client, authenticated_user):
    response = client.put(
        "/users/me",
        json={
        },
        headers={
            "Authorization": f'Bearer {authenticated_user["token"]}'
        }
        )
    
    assert response.status_code == 400
    assert response.json()['detail'] == "No update fields provided."


def test_update_profile_with_invalid_city_sends_422(client, authenticated_user):
    response = client.put(
        "/users/me",
        json={
            "name": "Update name",
            "phone_number": "83193417301",
            "city": "random",
            "bio": "Update bio"
        },
        headers={
            "Authorization": f'Bearer {authenticated_user["token"]}'
        }
        )
    assert response.status_code == 422

    error_messages = convert_to_error_messages(response.json())
    assert "Value error, City Not available" in error_messages


def test_update_profile_with_non_indian_phone_number_sends_422(client, authenticated_user):
    response = client.put(
        "/users/me",
        json={
            "name": "Update name",
            "phone_number": "1119417301",
            "city": "Indore",
            "bio": "Update bio"
        },
        headers={
            "Authorization": f'Bearer {authenticated_user["token"]}'
        }
        )
    assert response.status_code == 422
    
    error_messages = convert_to_error_messages(response.json())
    assert "Value error, Invalid Indian mobile number." in error_messages


def test_update_profile_with_phone_number_less_than_10_sends_422(client, authenticated_user):
    response = client.put(
        "/users/me",
        json={
            "name": "Update Data",
            "phone_number": "831",
            "city": "Indore",
            "bio": "Update bio"
        },
        headers={
            "Authorization": f'Bearer {authenticated_user["token"]}'
        }
        )
    assert response.status_code == 422
    
    error_messages = convert_to_error_messages(response.json())
    assert 'String should have at least 10 characters' in error_messages


def test_update_profile_with_phone_number_more_than_10_sends_422(client, authenticated_user):
    response = client.put(
        "/users/me",
        json={
            "name": "Update name",
            "phone_number": "918319417300",
            "city": "Indore",
            "bio": "Update bio"
        },
        headers={
            "Authorization": f'Bearer {authenticated_user["token"]}'
        }
        )
    assert response.status_code == 422
    
    error_messages = convert_to_error_messages(response.json())
    assert "String should have at most 10 characters" in error_messages


def test_update_profile_with_name_less_than_3_sends_422(client, authenticated_user):
    response = client.put(
        "/users/me",
        json={
            "name": "ab",
            "phone_number": "8319417300",
            "city": "Indore",
            "bio": "Update bio"
        },
        headers={
            "Authorization": f'Bearer {authenticated_user["token"]}'
        }
        )
    assert response.status_code == 422
    
    error_messages = convert_to_error_messages(response.json())
    assert "String should have at least 3 characters" in error_messages


def test_update_profile_with_name_more_than_50_sends_422(client, authenticated_user):
    response = client.put(
        "/users/me",
        json={
            "name": "abcdefgh 12345678901234567890123456789012345678901234567890",
            "phone_number": "8319417300",
            "city": "Indore",
            "bio": "Update bio"
        },
        headers={
            "Authorization": f'Bearer {authenticated_user["token"]}'
        }
        )
    assert response.status_code == 422
    
    error_messages = convert_to_error_messages(response.json())
    assert "String should have at most 50 characters" in error_messages


