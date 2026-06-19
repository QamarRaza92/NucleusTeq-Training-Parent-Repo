# Assignment 1: NumPy Basics
# Understand arrays and numerical operations
# Tasks:
# Create a NumPy array:
#  [10, 20, 30, 40, 50]
# Perform:
# Mean
# Max
# Min
# Sum
import numpy as np
print("Task1: Arithmatic Operations on NumPy")
arr = np.array([10,20,30,40,50])
print("Mean: ",arr.mean())
print("Max: ",arr.max())
print("Min: ",arr.min())
print("Sum: ",arr.sum())
print(50*"-")

# Create two arrays:
#  Perform:
# Addition
# Multiplication
arr_1 = np.array([1,2,3])
arr_2 = np.array([4,5,6])
print("Task2: Add and Sub using NumPy")
print(f"Addition: {arr_1} + {arr_2} = {arr_1+arr_2}")
print(f"Multiplication: {arr_1} * {arr_2} = {arr_1*arr_2}")
print(50*"-")

# Create a 3×3 matrix using NumPy
print("Task3: 3 X 3 Matrix NumPy")
matrix = np.array([[1,2,3], [4,5,6],[7,8,9]])
print("Matrix:")
print(matrix)
print("Shape:", matrix.shape)
print(50*"-")