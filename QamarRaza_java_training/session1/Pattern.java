import java.util.Scanner;
class Pattern
{
    static void square()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number of rows in square: ");
        int rows = sc.nextInt();
        for(int i=0;i<rows;i++)
        {
            for(int j=0;j<rows;j++)
            {
                System.out.print("*  ");
            }
            System.out.println("");
        }
    }

    static void traingle()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number of rows in traingle: ");
        int rows = sc.nextInt();
        for(int i=0;i<rows;i++)
        {
            for(int j=i;j<rows;j++)
            {
                System.out.print("*");
            }
            System.out.println("");
        }
    }

    public static void main(String args[])
    {
        System.out.println("The square pattern:-");
        square();

        System.out.println("The traingle pattern:-");
        traingle();
    }
}