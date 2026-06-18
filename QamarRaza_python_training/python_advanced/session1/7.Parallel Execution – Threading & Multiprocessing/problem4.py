# Q4. Create multiple threads to simulate file downloading using time.sleep().
print("Q4. Create multiple threads to simulate file downloading using time.sleep().")
from threading import Thread
import time
def download(file):
    print(f"Downloading {file}")
    time.sleep(3)
    print(f"{file} downloaded successfully :)")

t1 = Thread(target=download, args=("File1",))
t2 = Thread(target=download, args=("File2",))

t1.start()
t2.start()

t1.join()
t2.join()    