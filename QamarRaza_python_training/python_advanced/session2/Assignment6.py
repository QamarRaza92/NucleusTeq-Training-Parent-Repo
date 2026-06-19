# Assignment 6: Seaborn Visualizations
# Create advanced charts
# Using employee dataset:
# Tasks:
# Create:
# Barplot → Department vs Salary
# Create:
# Boxplot → Salary distribution
# Create:

import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd

# Create Mock Employee Dataset
mock_data = {
    "Name": [f"Employee_{i}" for i in range(1, 21)],
    "Age": np.random.randint(22, 60, size=20),
    "Department": np.random.choice(["HR", "IT", "Finance", "Marketing"], size=20),
    "Salary": np.random.randint(30000, 90000, size=20),
}

df = pd.DataFrame(mock_data)

# Set theme
sns.set_theme(style="whitegrid")

# Task 1: Barplot → Department vs Salary
plt.figure(figsize=(8, 5))
sns.barplot(
    x="Department",
    y="Salary",
    data=df,
    palette="Blues_d"
)

plt.title("Average Salary by Department (Bar Plot)")
plt.xlabel("Department")
plt.ylabel("Average Salary (INR)")
plt.tight_layout()
plt.show()


# Task 2: Boxplot → Salary Distribution
plt.figure(figsize=(6, 5))
sns.boxplot(
    y="Salary",
    data=df,
    color="#2ecc71",
    width=0.4
)
plt.title("Salary Distribution (Box Plot)")
plt.ylabel("Salary")
plt.tight_layout()
plt.show()


# Task 3: Heatmap → Correlation between Age and Salary
plt.figure(figsize=(5, 4))
correlation_matrix = df[["Age", "Salary"]].corr()
sns.heatmap(
    correlation_matrix,
    annot=True,
    cmap="coolwarm",
    fmt=".2f"
)
plt.title("Correlation Heatmap: Age vs Salary")
plt.tight_layout()
plt.show()