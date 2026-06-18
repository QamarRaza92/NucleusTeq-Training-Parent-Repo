# Q3. Use filter() to extract even numbers from a list.
print("Q3. Use filter() to extract even numbers from a list.")
evens = filter(lambda x: x % 2 == 0, [1, 2, 3, 4, 5, 6, 7, 8, 10])
print(next(evens))
print(next(evens))
print(next(evens))
print(next(evens))
print(next(evens))