# 43. Implement encapsulation using private variables in Bank class.
class Bank:
    def __init__(self, account_holder, balance):
        self.account_holder = account_holder
        self.__balance = balance     # Private variable
    def deposit(self, amount):
        self.__balance += amount
    def withdraw(self, amount):
        if amount <= self.__balance:
            self.__balance -= amount
        else:
            print("Insufficient Balance")
    def display_balance(self):
        print(f"Current Balance: {self.__balance}")
        

print("Q43. Bank Class (Encapsulation) Output")
account = Bank("Qamar", 10000)
account.deposit(5000)
account.withdraw(3000)
account.display_balance()
print("-"*30)