#Q1. Write pytest test cases for a function that adds two numbers.
print("Q1. Write pytest test cases for a function that adds two numbers.")

#Demo Function
def add(x,y):
    return x+y

#Testing Logic
def test_add():
    assert add(3,4) == 7
    assert add(-3,3) == 0
    assert add(4,-5) == -1
    assert add(0,0) == 0