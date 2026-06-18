# 4. Write a generator to produce Fibonacci numbers.
print("Q4. Write a generator to produce Fibonacci numbers.")
def fib():
    curr = 1
    result = 0
    while True:
        yield result
        result,curr = result + curr, result
fib_generator = fib()
print(next(fib_generator))
print(next(fib_generator))
print(next(fib_generator))
print(next(fib_generator))
print(next(fib_generator))
print(next(fib_generator))
print(next(fib_generator))
print(next(fib_generator))
print(next(fib_generator))