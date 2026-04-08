import java.util.Scanner;

class SumOfEvenNumbers
{
    public static void main(String args[])
    {
        Scanner sc = new Scanner(System.in);
        int sum = 0;
        int i=1;
        while(i<=10)
        {
            if(i%2==0)
            {
                sum+=i;
            }
            i+=1;
        }
        System.out.print("sum of even numbers from 1 to 10 = "+sum);
    }
}