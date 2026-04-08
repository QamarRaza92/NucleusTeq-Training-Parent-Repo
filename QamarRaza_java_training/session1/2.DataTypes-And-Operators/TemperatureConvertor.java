import java.util.Scanner;
class TemperatureConvertor
{
    static void celciusToFahrenheit(float C)
    {
        float F = (C * 9.0f/5.0f) + 32;
        System.out.println(C+ " Celcius(C) = "+F+" Fahrenheit(F)");
    }

    static void fahrenheitToCelcius(float F)
    {
        float C = (F - 32) * 5.0f/9.0f;
        System.out.println(F+ " Fahrenheit(F) = "+C+" Celcius(C)");
    }

    public static void main(String args[])
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter temperature in celcius: ");
        float c = sc.nextFloat();
        celciusToFahrenheit(c);
        System.out.println("Enter temperature in fahrenheit: ");
        float f = sc.nextFloat();
        fahrenheitToCelcius(f);
    }
}