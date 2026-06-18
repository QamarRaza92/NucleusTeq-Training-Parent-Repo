#Q2. Write pytest test cases for a function that checks whether a number is prime.
print("Q2. Write pytest test cases for a function that checks whether a number is prime.")

def check_prime(num):
    if num <= 1:
        return False
    counter = 0
    for i in range(1, num + 1):
        if num % i == 0:
            counter += 1
    return counter == 2

def test_check_prime():
    assert check_prime(3) == True
    assert check_prime(2) == True
    assert check_prime(31) == True

    assert check_prime(10) == False
    assert check_prime(15) == False
    assert check_prime(0) == False
    assert check_prime(1) == False