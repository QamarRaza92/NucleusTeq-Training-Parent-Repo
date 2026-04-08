import java.util.Scanner;
class Area
{
    static float areaOfRectangle(float length,float breadth)
    {
        return (length*breadth);
    }

    static float areaOfCircle(float radius)
    {
        return (22*radius*radius)/7;
    }

    static float areaOfTriangle(float base,float height)
    {
        return (base*height);
    }
    public static void main(String args[])
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter 0 to exit\nEnter 1 to calculate area of Rectangle:\nEnter 2 to calculate area of Circle:\nEnter 3 to calculate area of Triangle:");
        int flag = sc.nextInt();
        if(flag==0)
        {
            System.out.println("Program ends");
        }
        else if(flag==1)
        {
            System.out.println("Enter length: ");
            float length = sc.nextFloat();
            System.out.println("Enter  breadth: ");
            float breadth = sc.nextFloat();
            System.out.println("Area of rectangle is: "+areaOfRectangle(length,breadth)+" square units");
        }
        else if(flag==2)
        {
            System.out.println("Enter radius: ");
            float radius = sc.nextFloat();
            System.out.println("Area of cicle is: "+areaOfCircle(radius)+" square units");
        }
        else if(flag==3)
        {
            System.out.println("Enter base: ");
            float base = sc.nextFloat();
            System.out.println("Enter  height: ");
            float height = sc.nextFloat();
            System.out.println("Area of triangle is: "+areaOfTriangle(base,height)+" square units");
        }
    }
}