# Assignment 4: Data Analysis
# Use GroupBy
# Using employee dataset:
# Tasks:
# Find average salary by department
# Find max salary by department
# Count employees per department

import pandas as pd
import numpy as np

mock_data = {
    "Name": [f"Employee_{i}" for i in range(1, 21)],
    "Age": np.random.randint(22, 60, size=20),
    "Department": np.random.choice(["HR", "IT", "Finance", "Marketing"], size=20),
    "Salary": np.random.randint(30000, 90000, size=20),
}

df = pd.DataFrame(mock_data)

avg_salary_by_department = df.groupby("Department")["Salary"].mean()

max_salary_by_department = df.groupby("Department")["Salary"].max()

count_employees_by_department = df.groupby("Department").size()

print("Task1: Average Salary by Department:")
print(avg_salary_by_department)
print("-"*50)

print("Task2: Maximum Salary by Department:")
print(max_salary_by_department)
print("-"*50)

print("Task3: Employee Count by Department:")
print(count_employees_by_department)
print("-"*50)