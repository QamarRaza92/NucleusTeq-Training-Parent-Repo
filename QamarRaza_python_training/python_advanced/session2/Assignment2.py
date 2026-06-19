# Assignment 2: Pandas DataFrame Creation
# Learn DataFrame structure
# Tasks:
# Create a DataFrame for employees:
# Name      Age        Department        Salary
# Rahul     25          HR               30000
# Priya     30          IT               50000
# Amit      28          Finance          45000
# Anuj      35          IT               60000
# Then:
# Show first 2 rows
# Show summary statistics
# Display only IT employees
# Add new column:
#  Bonus = Salary * 0.10


import pandas as pd
df = pd.DataFrame(
    {
        "Name": ["Rahul", "Priya", "Amit", "Anuj"],
        "Age": [25, 30, 28, 35],
        "Department": ["HR", "IT", "Finance", "IT"],
        "Salary": [30000, 50000, 45000, 60000]
    }
)
print("Q1. First 2 rows:")
print(df.head(2))
print("-" * 50)

print("Q2. Summary statistics:")
print(df.describe())
print("-" * 50)

print("Q3. IT Employees:")
print(df[df["Department"] == "IT"])
print("-" * 50)

df["Bonus"] = df["Salary"] * 0.10
print("Q4. DataFrame after adding Bonus column:")
print(df)
