# 40. Create a Student class with attributes and display details.
class Student:
    def __init__(self,name,age,marks):
        self.age = age
        self.name = name 
        self.marks = marks
    def display(self):
        print("Student Details:-")
        print(f"Name: {self.name}\nAge: {self.age}\nMarks: {self.marks}")


print("Q40. Student Class Output")
student = Student("Qamar", 21, 95)
student.display()
print("-"*30)