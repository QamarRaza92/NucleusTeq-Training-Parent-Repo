# 2. Write a program to divide two numbers entered by the user and handle ZeroDivisionError.
num1 = int(input("Enter 1st number: "))
num2 = int(input("Enter 2nd number: "))
try:
    result = num1/num2
    print("Result: ",result)
except ZeroDivisionError as e:
    print(f"Exception: {e}")