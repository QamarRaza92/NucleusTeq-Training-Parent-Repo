# 35. Create a file and write your name into it.
print("Q35. Create a File and Write Name")
with open("new_file.txt", "w") as file:
    file.write("I am Qamar Raza")
print("-"*30)


# 36. Read a file and count words, lines, and characters.
print("Q36. Read File and Count Words, Lines and Characters")
with open("new_file.txt", "r") as file:
    content = file.read()
words = len(content.split())
characters = len(content)
lines = len(content.splitlines())
print(f"Words: {words}")
print(f"Lines: {lines}")
print(f"Characters: {characters}")
print("-"*30)


# 37. Append data to existing file.
print("Q37. Append Data to Existing File")
with open("new_file.txt", "a") as file:
    file.write("\nI am a B.Tech student")
print("Data appended successfully")
print("-"*30)


# 38. Copy content from one file to another.
print("Q38. Copy Content from One File to Another")
with open("new_file.txt", "r") as source:
    content = source.read()
with open("copy_file.txt", "w") as destination:
    destination.write(content)
print("File copied successfully")
print("-"*30)


# 39. Search a word in a file.
print("Q39. Search a Word in a File")
word = input("Enter word to search: ")
with open("new_file.txt", "r") as file:
    content = file.read()
if word in content:
    print(f'"{word}" found in file')
else:
    print(f'"{word}" not found in file')
print("-"*30)