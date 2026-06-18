import re

# Q3. Write a regular expression to validate a 10-digit mobile number.
print("Q3. Write a regular expression to validate a 10-digit mobile number.")
mobile = "9893221705"

answer3 = re.match(r"^\d{10}$", mobile)

if answer3:
    print("Valid Mobile Number")
else:
    print("Invalid Mobile Number")