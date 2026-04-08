import java.util.Scanner;
class CheckPrime
{
    static int primeChecker(int num)
    {
        int count = 0;
        for(int i=1;i<=num;i++)
        {
            if(num%i==0)
            {
                count += 1;
            }
        }
        return count;
    }
    
    public static void main(String args[])
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a number: ");
        
        int num = sc.nextInt();

        int count = primeChecker(num);

        if(count==2)
        {
            System.out.println("Number is prime.");
        }
        else
        {
            System.out.println("Number is not prime.");
        }
    }
}