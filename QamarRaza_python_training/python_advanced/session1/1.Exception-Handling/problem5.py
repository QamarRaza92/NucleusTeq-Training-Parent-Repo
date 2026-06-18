# 5. Write a program that catches all exceptions and prints the error message.
try:
    num = int(input("Enter number: "))
    result = 10/num
except Exception as e:
    print(f"Exception: {e}")
else:
    print(f"Division of {10}/{num} = {result}")