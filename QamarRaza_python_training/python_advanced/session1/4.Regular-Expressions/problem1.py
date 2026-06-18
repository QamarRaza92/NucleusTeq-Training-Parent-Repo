import re

# Q1. Write a program to extract all numbers from a given string using regular expressions.
print("Q1. Write a program to extract all numbers from a given string using regular expressions.")
text = "This is 18 number jersey with 10k test runs and 14k ODI runs"
answer1 = re.findall(r"\d+", text)
print(answer1)
