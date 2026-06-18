# Q6. Write a recursive function to calculate fibonacci.
print("Q6. Write a recursive function to calculate fibonacci.")
def fib(n):
    if n == 0:
        return 0
    elif n in (1, 2):
        return 1
    return fib(n - 1) + fib(n - 2)

term = 8
print(f"Fib({term}) = {fib(term)}")