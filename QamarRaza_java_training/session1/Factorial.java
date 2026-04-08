import java.util.Scanner;
class Factorial
{
    static int fact(int n)
    {
        if (n==0)
        {
            return 1;
        }
        else if(n==1)
        {
            return 1;
        }
        else
        {
            return n*fact(n-1);
        }
    }
    public static void main(String args[])
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a number: ");
        int number = sc.nextInt();
        if(number<0)
        {
            System.out.println("Undefined !!!");
        }
        else
        {
            System.out.println("Factorial of "+number+" is: "+fact(number));
        }
    }
}