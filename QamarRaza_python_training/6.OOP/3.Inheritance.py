# 42. Implement inheritance using Person and Employee class.
class Person:
    def __init__(self,name,age,gender):
        self.age = age
        self.name = name 
        self.gender = gender
    def display(self):
        print(f"Name: {self.name}\nAge: {self.age}\nGender: {self.gender}")
class Employee(Person):
    def __init__(self, name, age, gender,salary,department):
        super().__init__(name, age, gender)
        self.salary = salary 
        self.department = department
    def display(self):
        super().display()
        print(f"Salary: {self.salary}\nDepartment: {self.department}")


print("Q42. Inheritance Output")
employee = Employee("Qamar", 21, "Male", 50000, "Data Science")
employee.display()
print("-"*30)