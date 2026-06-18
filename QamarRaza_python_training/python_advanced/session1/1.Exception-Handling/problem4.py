# 4. Handle multiple exceptions  in a single program.
try:
    num1 = int(input("Enter 1st number: "))
    num2 = int(input("Enter 2nd number: "))
    result = num1/num2
except ValueError as e:
    print(f"Exception: {e}")
except ZeroDivisionError as e:
    print(f"Exception: {e}")
else:
    print(f"Division of {num1}/{num2} = {result}")