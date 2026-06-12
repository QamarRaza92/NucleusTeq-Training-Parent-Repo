# 12. Print numbers from 1 to 100 using loop.
print("Numbers from 1 - 100")
for k in range(1,101):
    print(k)
print("-"*30)

# 13. Print multiplication table of a number.
num = int(input("Enter a number: "))
print(f"Multiplication table of {num}:-")
for k in range(1,11):
    print(f"{num} X {k} = {num*k}")
print("-"*30)

# 14. Find factorial of a number.
num = int(input("Enter a number: "))
if num < 0:
    print("Factorial does not exist for negative numbers")
else:
    fact = 1
    for k in range(1,num+1):
        fact *= k
    print(f'Factorial of {num}: {fact}')
print("-"*30)

# 15. Reverse a number using loop.
num = int(input("Enter a number: "))
reversed = ''
temp = num 
while len(reversed)<len(str(num)):
    reversed += str(temp%10)
    temp = int(temp/10)
print(f"Reverse number is: {reversed}")
print("-"*30)


# 16. Check whether a number is prime.
num = int(input("Enter a number: "))
counter = 0
for k in range(1,num+1):
    if num%k==0:
        counter+=1
if num <= 1:
    print("Not prime")
elif counter==2:
    print(f"{num} is a prime number")
else:
    print(f"{num} is not a prime number")
print("-"*30)