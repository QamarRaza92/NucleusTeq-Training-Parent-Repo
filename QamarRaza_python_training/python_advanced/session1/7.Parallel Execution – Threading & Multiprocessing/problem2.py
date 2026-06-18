# Q2. Create a thread that calculates the sum of numbers from 1 to 100.
print("Q2. Create a thread that calculates the sum of numbers from 1 to 100.")
from threading import Thread
def summator():
    print("Sum =", sum(range(1, 101)))

t1 = Thread(target=summator)
t1.start()
t1.join()