#Q4. Use pdb breakpoints inside a loop and inspect variable values.
import pdb

print("Q4. Use pdb breakpoints inside a loop and inspect variable values.")
for i in range(5):
    pdb.set_trace()
    print(i)