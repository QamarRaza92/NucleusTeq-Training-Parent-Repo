# Q5. Write a program to create two processes that print their Process IDs.
from multiprocessing import Process
import os
print("Q5. Write a program to create two processes that print their Process IDs.")

def print_process_id():
    print("PID:", os.getpid())

if __name__ == "__main__":

    p1 = Process(target=print_process_id)
    p2 = Process(target=print_process_id)

    p1.start()
    p2.start()

    p1.join()
    p2.join()