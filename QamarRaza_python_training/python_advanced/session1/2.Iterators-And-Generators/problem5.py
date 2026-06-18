# 5. Write a generator expression to generate even numbers from 1 to 50.
print("Q5. Write a generator expression to generate even numbers from 1 to 50.")
gen = (x for x in range(50) if x%2==0)
for k in range(25):
    print(next(gen))