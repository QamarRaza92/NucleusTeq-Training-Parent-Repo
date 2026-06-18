import re

# Q5. Use re.findall() to extract all words starting with a capital letter.
print("Q5. Use re.findall() to extract all words starting with a capital letter.")
sentence = "Hey guys, I live in MP and I love India"

answer5 = re.findall(r"[A-Z]\w*", sentence)

print(answer5)