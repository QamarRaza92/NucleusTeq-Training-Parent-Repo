import re

# Q2. Write a regular expression to validate an email address.
print("Q2. Write a regular expression to validate an email address.")
email = "raza8bb805@gmail.com"

if re.fullmatch(r"\w+(\.\w+)*@\w+(\.\w+)+", email):
    print("Valid Email")
else:
    print("Invalid Email")