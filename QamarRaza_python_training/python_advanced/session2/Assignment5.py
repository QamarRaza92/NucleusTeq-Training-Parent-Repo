# Assignment 5: Matplotlib Charts
# Create basic charts
# Using this data:
# Departments = HR, IT, Finance
#  Employees = 5, 12, 7
# Tasks:
# Create Bar Chart
# Create Line Chart
# Create Histogram using salaries:
#  [30000, 40000, 50000, 60000, 45000]
# Create Scatter Plot:
#  Age vs Salary

import matplotlib.pyplot as plt

Departments = ["HR", "IT", "Finance"]
Employees = [5, 12, 7]

# Bar Chart
plt.bar(Departments, Employees)
plt.title("Bar Chart")
plt.xlabel("Departments")
plt.ylabel("Employee Count")
plt.show()

# Line Chart
plt.plot(Departments, Employees)
plt.title("Line Chart")
plt.xlabel("Departments")
plt.ylabel("Employee Count")
plt.show()

# Histogram
salaries = [30000, 40000, 50000, 60000, 45000]

plt.hist(salaries)
plt.title("Histogram")
plt.xlabel("Salary")
plt.ylabel("Frequency")
plt.show()

# Scatter Plot
age = [25, 30, 23, 50, 40, 39, 27]
salary = [40000, 50000, 41000, 70000, 50000, 55000, 45000]

plt.scatter(age, salary)
plt.title("Scatter Plot")
plt.xlabel("Employee Age")
plt.ylabel("Employee Salary")
plt.show()