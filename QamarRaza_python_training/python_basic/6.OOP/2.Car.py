# 41. Create a Car class with a constructor.
class Car:
    def __init__(self,name,mileage,fuel_type):
        self.mileage = mileage
        self.name = name
        self.fuel_type = fuel_type
    def display(self):
        print("Car Details:-")
        print(f"Name: {self.name}\nMileage: {self.mileage}\nFuel Type: {self.fuel_type}")

        
print("Q41. Car Class Output")
car = Car("Swift", 22, "Petrol")
car.display()
print("-"*30)