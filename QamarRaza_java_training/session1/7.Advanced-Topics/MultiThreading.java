class BankAccount {
    private int balance = 1000;

    public synchronized void withdraw(int amount, String name) {
        if (balance >= amount) {
            System.out.println(name + " is withdrawing " + amount);
            balance = balance - amount;
            System.out.println(name + " completed withdrawal. Remaining balance: " + balance);
        } else {
            System.out.println(name + " tried to withdraw " + amount + ". Insufficient balance.");
        }
    }
}

class Customer extends Thread {
    private BankAccount account;
    private String customerName;

    public Customer(BankAccount account, String name) {
        this.account = account;
        this.customerName = name;
    }

    public void run() {
        account.withdraw(800, customerName);
    }
}

public class MultiThreading {
    public static void main(String[] args) {
        BankAccount sharedAccount = new BankAccount();

        Customer person1 = new Customer(sharedAccount, "Qamar Raza");
        Customer person2 = new Customer(sharedAccount, "Lata Mangeshkar");

        person1.start();
        person2.start();
    }
}