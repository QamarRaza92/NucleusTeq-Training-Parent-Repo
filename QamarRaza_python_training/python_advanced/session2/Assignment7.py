# End-to-End Practice
# Create a dataset of Students:
# Name      Marks       Hours Studied
# Rahul     70          2
# Priya     80          3
# Siri      90          5
# Anuj      60          1
# Tasks:
# Load into Pandas
# Add:
#  Performance = Pass if Marks > 65 else Fail
# Visualize:
# Line Chart → Hours vs Marks
# Scatter Plot → Study vs Marks
# Use Seaborn:
# Barplot → Performance vs Marks



import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

# 1. Load data into Pandas DataFrame
data = {
    "Name": ["Rahul", "Priya", "Siri", "Anuj"],
    "Marks": [70,90,80,60],
    "Hours Studied": [2,3,5,1]
}
df = pd.DataFrame(data)

# 2. Add Performance column (Pass if Marks > 65 else Fail)
df["Performance"] = df["Marks"].apply(lambda x: "Pass" if x > 65 else "Fail")
sns.set_theme(style="whitegrid")
# Visualization 1: Line Chart (Hours vs Marks)
# We sort values by Hours Studied so the line connects chronologically
df_sorted = df.sort_values(by="Hours Studied")

plt.figure(figsize=(6, 4))
plt.plot(
    df_sorted["Hours Studied"],
    df_sorted["Marks"],
    marker="o",
    color="purple",
    linewidth=2,
)
plt.title("Study Hours vs Marks Trend", fontsize=12, fontweight="bold")
plt.xlabel("Hours Studied", fontsize=10)
plt.ylabel("Marks", fontsize=10)
plt.tight_layout()
plt.show()

# Visualization 2: Scatter Plot (Study vs Marks)
plt.figure(figsize=(6, 4))
sns.scatterplot(
    x="Hours Studied", y="Marks", data=df, s=100, color="darkorange", edgecolor="black"
)
plt.title("Correlation: Study Hours vs Marks", fontsize=12, fontweight="bold")
plt.xlabel("Hours Studied", fontsize=10)
plt.ylabel("Marks", fontsize=10)
plt.tight_layout()
plt.show()

# Visualization 3: Seaborn Barplot (Performance vs Marks)
plt.figure(figsize=(6, 4))
sns.barplot(
    x="Performance",
    y="Marks",
    data=df,
    palette="Set2",
    hue="Performance",
    legend=False,
)
plt.title("Average Marks by Performance Status", fontsize=12, fontweight="bold")
plt.xlabel("Performance Status", fontsize=10)
plt.ylabel("Average Marks", fontsize=10)
plt.tight_layout()
plt.show()
print("Final DataFrame:")
print(df)