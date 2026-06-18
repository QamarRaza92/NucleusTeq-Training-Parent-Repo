# 17. Write a function to calculate square of a number.
def Square(arg):
    return arg*arg

# 18. Write a function to check palindrome(Number and string).
def CheckPalindrome(arg):
    arg = str(arg)
    if arg == arg[::-1]:
        print(f"{arg} is palindrome")
    else:
        print(f"{arg} is not palindrome")


# 19. Write a function that returns maximum number from a list.
def Max(arg:list):
    maximum = arg[0]
    for k in arg:
        if maximum>=k:
            continue
        else:
            maximum = k
    return maximum



# 20. Write a function using default parameters.
def DefaultParams(name,section="DS",university="Medicaps"):
    print("Student details:-")
    print(f"Name: {name}\nSection: {section}\nUniversity: {university}")


print("Q17. Find Square of a Number Using Function")
print(Square(6))
print("-"*30)


print("Q18. Check Palindrome (Number and String)")
CheckPalindrome(452)
print("-"*30)


print("Q19. Find Maximum Number from a List")
print(Max([1,5,22,4,5,22,4]))
print("-"*30)


print("Q20. Function Using Default Parameters")
DefaultParams(name="Qamar")
print("-"*30)