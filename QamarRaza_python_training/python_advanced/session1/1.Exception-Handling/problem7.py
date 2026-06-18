# 7.Create a custom exception called AgeException and raise it if age is less than 18.
class AgeException(Exception):
    pass
try:
    age = int(input("Enter your age: "))
    if age<18:
        raise AgeException("You are minor! You must be 18 years or more old")
except AgeException as e:
    print(f"Exception: {e}")
except ValueError as e:
    print(f"Exception: {e}")
else:
    print("Welcome to our website :)")