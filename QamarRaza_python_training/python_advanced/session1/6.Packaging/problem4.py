#Q4. Create a package for mathematical operations (add, subtract, multiply, divide) and use it.

print("Q4. Create a package for mathematical operations (add, subtract, multiply, divide) and use it.")
from math_module import add,sub,mul,div
a = 10
b = 5
print(f"{a} + {b} = {add.addition(a,b)}")
print(f"{a} - {b} = {sub.subtraction(a,b)}")
print(f"{a} * {b} = {mul.multiplication(a,b)}")
print(f"{a} / {b} = {div.division(a,b)}")
print("-"*50)