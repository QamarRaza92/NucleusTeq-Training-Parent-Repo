# 4. Create variables of type int, float, string, and boolean. Print their types using type().
int_var = 21
float_var = 21.8
string_var = "Qamar Raza"
boolean_var = True
print(f"{int_var}: {type(int_var)}\n{float_var}: {type(float_var)}\n{string_var}: {type(string_var)}\n{boolean_var}: {type(boolean_var)}\n")

# 5. Write a program to swap two numbers.
a,b = 10,20
print("Numbers before:\na = {}\nb = {}".format(a,b))
b,a = a,b
print("Numbers after:\na = {}\nb = {}".format(a,b))

# 6. Take two numbers and print sum, difference, multiplication, and division.
num1 = int(input("Enter 1st number: "))
num2 = int(input("Enter 2nd number: "))
print(f"Addition: {num1} + {num2} = {num1+num2}")
print(f"Difference: {num1} - {num2} = {num1-num2}")
print(f"Multiplication: {num1} * {num2} = {num1*num2}")
try:
    print(f"Division: {num1} / {num2} = {num1/num2}")
except ZeroDivisionError:
    print("You cannot divide by zero!")