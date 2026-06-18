#Q3. Create a function with a logical bug and use pdb to identify the issue.
import pdb
print("Q3. Create a function with a logical bug and use pdb to identify the issue.")

def average(a, b):
    pdb.set_trace()
    return a + b / 2

print(average(10, 20))