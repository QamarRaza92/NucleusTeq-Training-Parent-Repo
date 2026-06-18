#Q1. Write a program to create two threads that print numbers from 1 to 5 simultaneously.
print("Q1. Write a program to create two threads that print numbers from 1 to 5 simultaneously.")
from threading import Thread
def printer():
    for loop in range(1,6):
        print(loop)
t1 = Thread(target=printer)
t2 = Thread(target=printer)
t1.start()
t2.start()
t1.join()
t2.join()