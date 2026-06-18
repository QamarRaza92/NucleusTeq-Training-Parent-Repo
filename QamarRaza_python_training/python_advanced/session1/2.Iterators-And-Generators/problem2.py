# 2. Write a custom iterator class that returns numbers from 1 to N.
print("Q2. Write a custom iterator class that returns numbers from 1 to N.")
class MyIter:
    def __init__(self,limit):
        self.limit = limit
        self.counter = 1
    def __iter__(self):
        return self
    def __next__(self):
        if self.counter<=self.limit:
            value = self.counter
            self.counter+=1
            return value
        else:
            raise StopIteration
iter1 = MyIter(5)
for val in iter1:
    print(val)