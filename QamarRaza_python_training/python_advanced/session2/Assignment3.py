# Assignment 3: Data Cleaning
# Handle missing values
# Create this dataset:
# Create a DataFrame for employees:
# Name      Age       Salary
# Rahul     25        30000
# Priya     None      40000
# Anuj      29        None
# Tasks:
# Detect missing values
# Replace missing Age with mean
# Replace missing Salary with 0

import pandas as pd
df = pd.DataFrame(
              {
                  "Name":["Rahul","Priya","Anuj"],
                  "Age":[25,None,29],
                  "Salary":[30000,40000,None]
              }
            )
print("Task1: Detect missing values:-")
print(df.isnull().sum())
print("-" * 50)

print("Task2: Replace missing Age with mean:-")
df["Age"] = df['Age'].fillna(df['Age'].mean())
print(df)
print("-" * 50)

print("Task3: Replace missing Salary with 0:-")
df["Salary"] = df['Salary'].fillna(0)
print(df)
print("-" * 50)