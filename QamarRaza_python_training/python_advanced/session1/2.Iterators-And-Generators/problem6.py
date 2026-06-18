# 6. Explain the difference between iterator and generator with a small example.
print("Q6. Explain the difference between iterator and generator with a small example.")
#Iterator
class Iterator:
    def __init__(self):
        self.current = 0
    def __iter__(self):
        return self 
    def __next__(self):
        if self.current <= 3:
            result = self.current
            self.current += 1
            return result
        raise StopIteration
#Generator
def generator():
    current = 0
    while current<=3:
        yield current
        current+=1
my_iter = Iterator()
my_gen  = generator()
print("Iter: ",next(my_iter))
print("Iter: ",next(my_iter))
print("Iter: ",next(my_iter))
print("Gen: ",next(my_gen))
print("Gen: ",next(my_gen))
print("Gen: ",next(my_gen))