# 22. Use math module to find square root, power, and factorial.
print("Q22. Use Math Module (Square Root, Power, Factorial)")
import math 
sqrt = math.sqrt(16)
power = math.pow(3,2)
factorial  = math.factorial(5)
print(f"Square Root: {sqrt}\nPower: {power}\nFactorial: {factorial}")
print("-"*30)

# 23. Generate random numbers using random module.
print("Q23. Generate Random Numbers Using Random Module")
import random
print("Random number between 1 and 10: ",random.randint(1,10))
print("-"*30)

# 24. Create your own module and import it.
print("Q24. Create and Import Own Module")
import add 
print("4+5 = ",add.Addition(4,5))
print("-"*30)