import re

# Q7. Write a pattern to check if a string contains only alphabets.
print("Q7. Write a pattern to check if a string contains only alphabets.")

my_str = "i love india"

answer7 = re.search(r"[^a-zA-Z\s]", my_str)

if answer7:
    print(f"The string '{my_str}' does not contain only alphabets. Invalid character: {answer7.group()}")
else:
    print(f"The string '{my_str}' contains only alphabets.")