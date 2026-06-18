# 8. Write a program that handles FileNotFoundError when trying to open a file.
try:
    file = open("file1.txt","r")
except FileNotFoundError as e:
    print(f"Exception: {e}")
else:
    print(f"File data: {file.read()}")
finally:
    file.close()