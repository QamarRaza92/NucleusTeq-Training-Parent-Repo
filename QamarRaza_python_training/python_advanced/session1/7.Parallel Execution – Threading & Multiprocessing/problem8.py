# Q8. Convert a normal function into parallel execution using ProcessPoolExecutor.
from concurrent.futures import ThreadPoolExecutor, ProcessPoolExecutor

if __name__ == "__main__":
    print("Q8. Convert a normal function into parallel execution using ProcessPoolExecutor.")

    def square(n):
        return n * n

    nums = [1, 2, 3, 4]

    with ProcessPoolExecutor() as executor:
        result = executor.map(square, nums)

    print(list(result))