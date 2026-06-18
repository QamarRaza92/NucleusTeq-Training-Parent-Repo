# 3. Write a generator function that yields square numbers up to N.
print("Q3. Write a generator function that yields square numbers up to N.")
def my_generator():
    val = 1
    while True:
        yield val*val
        val+=1
fun = my_generator()
print(next(fun))
print(next(fun))
print(next(fun))
print(next(fun))