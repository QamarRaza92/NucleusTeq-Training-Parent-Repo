from utils import convert_to_error_messages


def test_client(client):
    response = client.get("/")
    assert response.status_code == 200




def test_register_success(client):
    response = client.post(
                            "/auth/register",
                            json= {
                                    "name": "test2",
                                    "email": "test_duplicate@example.com",
                                    "password": "test2@123",
                                    "phone_number": "8319417300",
                                    "city": "Indore",
                                    "bio": "test2" 
                                    }
                            )
    assert response.status_code == 201
    data = response.json()
    assert  data["name"] == "test2"
    assert  data["email"] == "test_duplicate@example.com"
    assert  data["phone_number"] == "8319417300"
    assert  data["city"] == "Indore"
    assert  data["bio"] == "test2"

    assert "password" not in data 
    assert "id" in data





def test_register_duplicate_email_sends_400(client):
    response = client.post(
                            "/auth/register",
                            json= {
                                    "name": "test3",
                                    "email": "test_duplicate@example.com",
                                    "password": "test@123",
                                    "phone_number": "8319417300",
                                    "city": "Indore",
                                    "bio": "test2" 
                                    }
                            )
    assert response.status_code == 400
    assert response.json()["detail"] == "Email already registered"


def test_register_phone_number_less_than_10_digits_sends_422(client):
    response = client.post(
                            "/auth/register",
                            json= {
                                    "name": "test2",
                                    "email": "test3@example.com",
                                    "password": "test3@123",
                                    "phone_number": "83194173",
                                    "city": "Indore",
                                    "bio": "test2" 
                                    }
                            )
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "String should have at least 10 characters" in error_messages


def test_non_indian_phone_number_sends_422(client):
    response = client.post(
                            "/auth/register",
                            json= {
                                    "name": "test4",
                                    "email": "test4@example.com",
                                    "password": "test4@123",
                                    "phone_number": "1119417300",
                                    "city": "Indore",
                                    "bio": "test2" 
                                    }
                            )
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "Value error, Invalid Indian mobile number." in error_messages

def test_register_name_less_than_3_chars_sends_422(client):
    response = client.post(
                            "/auth/register",
                            json= {
                                    "name": "te",
                                    "email": "test3@example.com",
                                    "password": "test3@123",
                                    "phone_number": "83194173",
                                    "city": "Indore",
                                    "bio": "test2" 
                                    }
                            )
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "String should have at least 3 characters" in error_messages


def test_register_password_less_than_8_chars_sends_422(client):
    response = client.post(
                            "/auth/register",
                            json= {
                                    "name": "test2",
                                    "email": "test3@example.com",
                                    "password": "12345",
                                    "phone_number": "83194173",
                                    "city": "Indore",
                                    "bio": "test2" 
                                    }
                            )
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "String should have at least 8 characters" in error_messages


def test_register_password_with_no_special_char_sends_422(client):
    response = client.post(
                            "/auth/register",
                            json= {
                                    "name": "test2",
                                    "email": "test3@example.com",
                                    "password": "1234abcd",
                                    "phone_number": "83194173",
                                    "city": "Indore",
                                    "bio": "test2" 
                                    }
                            )
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "Value error, Password must contain at least 1 special character." in error_messages


def test_register_password_with_no_numeric_sends_422(client):
    response = client.post(
                            "/auth/register",
                            json= {
                                    "name": "test2",
                                    "email": "test3@example.com",
                                    "password": "abcdefgh",
                                    "phone_number": "83194173",
                                    "city": "Indore",
                                    "bio": "test2" 
                                    }
                            )
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "Value error, Password must contain at least 1 number." in error_messages



def test_register_random_city_name_sends_422(client):
    response = client.post(
                            "/auth/register",
                            json= {
                                    "name": "test2",
                                    "email": "test3@example.com",
                                    "password": "12345",
                                    "phone_number": "83194173",
                                    "city": "xyz",
                                    "bio": "test2" 
                                    }
                            )
    assert response.status_code == 422
    error_messages = convert_to_error_messages(response.json())
    assert "Value error, City Not available" in error_messages


def test_login_success(client,registered_user):
    response = client.post(
        "/auth/login",
        json={
            "email": registered_user["email"],
            "password": registered_user["password"]
            }
            )
    assert response.status_code == 200
    data = response.json()


def test_login_wrong_email_sends_404(client, registered_user):
    response = client.post(
        "/auth/login",
        json={
            "email":"wrong@example.com",
            "password": "wrong@123"
        })
    assert response.status_code == 404
    assert response.json()['detail'] == "User not found"


def test_login_unmatched_password_sends_401(client,registered_user):
    response = client.post(
        "/auth/login",
        json={
            "email": registered_user["email"],
            "password": 'wxyz@123'
        }
        )
    assert response.status_code == 401
    assert response.json()["detail"] == "Invalid password"
