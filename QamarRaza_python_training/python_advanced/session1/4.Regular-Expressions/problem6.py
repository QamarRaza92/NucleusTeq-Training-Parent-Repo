import re

# Q6. Replace multiple spaces in a string with a single space using re.sub().
print("Q6. Replace multiple spaces in a string with a single space using re.sub().")

sentence = "Hey there,   how    are you"

answer6 = re.sub(r"\s+", " ", sentence)

print(answer6)