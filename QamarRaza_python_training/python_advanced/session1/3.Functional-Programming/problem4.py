# Q4. Use reduce() to find the product of all elements in a list.
print("Q4. Use reduce() to find the product of all elements in a list.")
from functools import reduce

product = reduce(lambda x, y: x * y, [1, 2, 3, 4, 5])
print("Reduced Product:", product)