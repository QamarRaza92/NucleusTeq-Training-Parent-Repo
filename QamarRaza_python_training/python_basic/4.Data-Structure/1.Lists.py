# 25. Create a list of 10 numbers and find sum, max, sort it, and remove duplicates.
print("Q25. List Operations (Sum, Max, Sort, Remove Duplicates)")
my_list = [4,2,5,3,4,6,3,5,2,8]
print(f"Max: {max(my_list)}")
print(f"Sum: {sum(my_list)}")
my_list.sort(reverse=True)
print(f"Sorted List: {my_list}")
print(f"List(without duplicates): {list(set(my_list))}")
print("-"*30)

# 26. Count even and odd numbers in a list.
print("Q26. Count Even and Odd Numbers in a List")
evens = odds = 0
for k in [5,1,7,8,3,5,7,2,8,4,41,46]:
    if k%2==0:
        evens+=1
    else:
        odds+=1
print(f"Total no. of even values: {evens}\nTotal no. of odds values: {odds}\n")
print("-"*30)

# 27. Reverse a list without using reverse()
print("Q27. Reverse a List Without Using reverse()")
new_list = [34,22,6,42,45,78,12,33]
print(f"Reversed list: {new_list[::-1]}")
print("-"*30)