# 6. Create a function that raises a ValueError if a number is negative.
try:
    income = int(input("Enter your income: "))
    if income<0:
        raise ValueError("Income can not be negative!")
except ValueError as e:
    print(f"Exception: {e}")
else:
    print("Entered income: ",income)