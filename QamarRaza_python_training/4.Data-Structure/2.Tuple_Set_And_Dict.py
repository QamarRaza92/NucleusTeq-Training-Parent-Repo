# 28. Create a tuple and access elements.
print("Q28. Create a Tuple and Access Elements")
my_tuple = (4,5,7,6)
print("my_tuple[2] = ",my_tuple[2])
print("-"*30)

# 29. Convert tuple into list and modify it.
print("Q29. Convert Tuple into List and Modify It")
modified_tuple = list(my_tuple)
modified_tuple[2] = 100
print(f"Modified Tuple: {modified_tuple}")
print("-"*30)

# 30. Perform union, intersection, and difference on two sets.
print("Q30. Perform Union, Intersection and Difference of Sets")
set1 = {3,5,2,6,4,7,8}
set2 = {4,2,6,1,7,5,9}
print(f"Union of set1 & set2: {set1.union(set2)}")
print(f"Intersection of set1 & set2: {set1.intersection(set2)}")
print(f"Difference:- set1 - set2: {set1.difference(set2)}\nDifference:- set2 - set1: {set2.difference(set1)}")
print("-"*30)

# 31. Remove duplicates from list using set.
print("Q31. Remove Duplicates from List Using Set")
demo_list = [4,6,2,5,6,3,6,4]
print(f"List(with duplicates):{demo_list}\nList(without duplicates): {list(set(demo_list))}")
print("-"*30)

# 32. Create a student dictionary and access values.
print("Q32. Create Student Dictionary and Access Values")
student = {"name":"Qamar","age":21,"cgpa":9.1}
print("Student Dictionary:- ")
print("name",student["name"])
print("age",student["age"])
print("cgpa",student["cgpa"])
print("-"*30)

# 33. Count frequency of characters in a string using dictionary.
print("Q33. Count Frequency of Characters Using Dictionary")
demo_string = "mississippi"
frequency = dict()
for char in demo_string:
    if char in frequency:
        frequency[char] += 1
    else:
        frequency[char] = 1
print(f"Frequency of characters in the word {demo_string} (using dictionary):-\n{frequency}")
print("-"*30)

# 34. Merge two dictionaries.
print("Q34. Merge Two Dictionaries")
dict2 = {"gender":"male"}
temp = dict2.copy()
dict2.update(student)
print(f"Dict1: {student}\nDict2: {temp}\nMerged Dictionary: {dict2}") 
print("-"*30)