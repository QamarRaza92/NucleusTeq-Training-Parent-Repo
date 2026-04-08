abstract class Animal {
    abstract void makeSound();
    
    public void sleep() {
        System.out.println("Zzz");
    }
}

interface Pet {
    void play();
}

class Dog extends Animal implements Pet {
    public void makeSound() {
        System.out.println("Woof");
    }

    public void play() {
        System.out.println("Playing fetch");
    }
}

public class AbstractionAndInterfaceExample {
    public static void main(String[] args) {
        Dog myDog = new Dog();
        myDog.makeSound();
        myDog.sleep();
        myDog.play();
    }
}