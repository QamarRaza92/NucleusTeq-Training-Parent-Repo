import java.util.Scanner;

class Fibonacci
{
    static int fib(int n)
    {
        if(n==0)
        {
            return 0;
        }
        else if(n==1)
        {
            return 1;
        }
        return fib(n-1)+fib(n-2);
    }
    public static void main(String args[])
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter fibonnaci term number: ");
        int number = sc.nextInt();
        System.out.println("Fib("+number+") is: "+fib(number));
    }
}