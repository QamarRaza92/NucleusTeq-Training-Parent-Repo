import java.util.Scanner;

class Operators
{
    public static void main(String args[])
    {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter first number: ");
        int num1 = sc.nextInt();

        System.out.println("Enter second number: ");
        int num2 = sc.nextInt();

        System.out.println("Arithmatic operators:-");
        System.out.println(num1+" + "+num2+" = "+(num1+num2));
        System.out.println(num1+" - "+num2+" = "+(num1-num2));
        System.out.println(num1+" * "+num2+" = "+(num1*num2));
        System.out.println(num1+" / "+num2+" = "+(num1/num2));
        System.out.println(num1+" % "+num2+" = "+(num1%num2));
        System.out.println("----------------------------------");

        System.out.println("Logical operators:-");
        System.out.println("(num1 > 0 && num2 > 0) = "+(num1 > 0 && num2 > 0));
        System.out.println("(num1 > 0 || num2 > 0) = "+(num1 > 0 || num2 > 0));
        System.out.println("!(num1 > num2) = "+(!(num1 > num2)));
        System.out.println("----------------------------------");

        System.out.println("Relational operators:-");
        System.out.println(num1+" == "+num2+" = "+(num1==num2));
        System.out.println(num1+" != "+num2+" = "+(num1!=num2));
        System.out.println(num1+" > "+num2+" = "+(num1>num2));
        System.out.println(num1+" < "+num2+" = "+(num1<num2));
        System.out.println(num1+" >= "+num2+" = "+(num1>=num2));
        System.out.println(num1+" <= "+num2+" = "+(num1<=num2));
        System.out.println("----------------------------------");
    }
}