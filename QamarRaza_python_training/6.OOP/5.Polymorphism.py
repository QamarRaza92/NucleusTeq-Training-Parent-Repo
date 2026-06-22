# 44. Demonstrate polymorphism using different classes with the same method name.
class Dog:
    def sound(self):
        print("Dog barks")
class Cat:
    def sound(self):
        print("Cat meows")
class Cow:
    def sound(self):
        print("Cow moos")


print("Q44. Polymorphism Output")
animals = [Dog(), Cat(), Cow()]
for animal in animals:
    animal.sound()
print("-"*30)