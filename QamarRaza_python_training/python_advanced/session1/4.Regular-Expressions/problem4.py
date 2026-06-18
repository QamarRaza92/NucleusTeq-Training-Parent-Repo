import re

# Q4. Use re.search() to check whether a word exists in a sentence.
print("Q4. Use re.search() to check whether a word exists in a sentence.")
word = "cat"
sentence = "I love my cat"

if re.search(word, sentence):
    print("Word Found")
else:
    print("Word Not Found")