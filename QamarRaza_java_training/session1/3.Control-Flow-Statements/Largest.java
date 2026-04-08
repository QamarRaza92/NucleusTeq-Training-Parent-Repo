import java.util.Scanner;
class Largest
{
    public static void main(String args[])
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter 3 numbers: ");
        int a = sc.nextInt();
        int b = sc.nextInt();
        int c = sc.nextInt();

        if(a>b)
        {
            if(a>=c)
            {
                System.out.println("largest number is: "+a);
            }
            else 
            {
                System.out.println("largest number is: "+c);
            }
        }
        else
        {
            if(b>c || b==c)
            {
                System.out.println("largest number is: "+b);
            }
            else
            {
                System.out.println("largest number is: "+c);
            }
        }
    }
}