# 3. Write a program using try-except-else-finally to read a number from a file  and print its square.
def write_in_file(data):
    file = open("file1.txt","w")
    file.write(data)
    file.close()
data = input("Enter number to write in file: ")
write_in_file(data)
try:
    file = open("file1.txt","r")
    num = int(file.read())
except ValueError as e:
    print("Exception: ",e)
except FileNotFoundError as e:
    print("Exception: ",e)
else:
    print(f"Square of {num} = {num*num}")
finally:
    file.close()