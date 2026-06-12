# 7. Write a program to check whether a number is even or odd.
print("Q7. Check Whether Number is Even or Odd")
num = int(input("Enter any number: "))
if num%2==0:
    print(f"The number  {num} is even.")
else:
    print(f"The number  {num} is odd.")
print("-"*30)


# 8. Check whether a number is positive, negative, or zero.
print("Q8. Check Whether Number is Positive, Negative or Zero")
num = int(input("Enter any number: "))
if num>0:
    print(f"The number  {num} is positive(+).")
elif num==0:
    print(f"The number  {num} is zero.")
else:
    print(f"The number  {num} is negative(-).")
print("-"*30)


# 9. Find the largest of three numbers.
print("Q9. Find Largest of Three Numbers")
num1 = int(input("Enter 1st number: "))
num2 = int(input("Enter 2nd number: "))
num3 = int(input("Enter 3rd number: "))
if num1>=num2 :
    if num1>=num3:
        print(f"The largest number is num1: {num1}")
    else:
        print(f"The largest number is num3: {num3}")
elif num2>num1 :
    if num2>=num3:
        print(f"The largest number is num2: {num2}")
    else:
        print(f"The largest number is num3: {num3}")
else:
    print(f"The largest number is num3: {num3}")
print("-"*30)


# 10. Calculate grade based on marks (A/B/C/Fail).
print("Q10. Calculate Grade Based on Marks")
marks = int(input("Enter marks: "))
if marks>=80:
    print("Grade A")
elif marks>=60:
    print("Grade B")
elif marks>=40:
    print("Grade C")
else:
    print("Fail")
print("-"*30)


# 11. Check whether a year is a leap year.
print("Q11. Check Whether a Year is Leap Year")
year = int(input("Enter an year: "))
if year%100==0:
    if year%400==0:
        print(f"{year} is a leap year.")
    else:
        print(f"{year} is not a leap year")
elif year%4==0:
        print(f"{year} is a leap year.")
else:
    print(f"{year} is not a leap year.")
print("-"*30)