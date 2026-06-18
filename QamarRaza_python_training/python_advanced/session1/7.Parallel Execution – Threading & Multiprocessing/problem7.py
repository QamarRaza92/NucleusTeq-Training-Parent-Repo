# Q7. Convert a normal function into parallel execution using ThreadPoolExecutor.
from concurrent.futures import ThreadPoolExecutor

if __name__ == "__main__":
    print("Q7. Convert a normal function into parallel execution using ThreadPoolExecutor.")

    def calculate_square(arg):
        return arg * arg

    nums = [1, 2, 3, 4, 5]

    with ThreadPoolExecutor() as executor:
        result = executor.map(calculate_square, nums)

    print(list(result))  