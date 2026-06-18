# Q6. Write a multiprocessing program to calculate the square of numbers using Process class.
from multiprocessing import Process
print("Q6. Write a multiprocessing program to calculate the square of numbers using Process class.")

def calculate_square(arg):
    print(f"Square of {arg} = {arg * arg}")

nums = [1, 2, 3, 4, 5]

processes = []

for num in nums:
    p = Process(target=calculate_square, args=(num,))
    p.start()
    processes.append(p)

for p in processes:
    p.join()