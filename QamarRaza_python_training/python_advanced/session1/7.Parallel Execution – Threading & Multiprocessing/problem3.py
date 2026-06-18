# Q3. Demonstrate the use of join() method in threading.
print("Q3. Demonstrate the use of join() method in threading.")
from threading import Thread
import time
def demo():
    print("Thread is waiting for 3 seconds...")
    time.sleep(3)
    print("Thread execution completed")

print("With join():")
t1 = Thread(target=demo)
t1.start()
t1.join()
print("Rest of program")

print("\nWithout join():")
t2 = Thread(target=demo)
t2.start()
print("Rest of program")

t2.join()