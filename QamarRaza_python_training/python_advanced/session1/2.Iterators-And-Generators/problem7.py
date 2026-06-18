# 7. Write a program that processes a large dataset using a generator instead of storing all values in a list.
print("Q7. Write a program that processes a large dataset using a generator instead of storing all values in a list.")
def squares_till_crore():
    for num in range(10000000):
        yield num*num
gen = squares_till_crore()
print(next(gen))
print(next(gen))
print(next(gen))
print(next(gen))
print(next(gen))
print(next(gen))