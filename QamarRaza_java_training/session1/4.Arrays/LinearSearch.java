import java.util.Scanner;
class LinearSearch
{
    static void linearSearch(int a[],int target)
    {
        for(int i=0;i<a.length;i++)
        {
            if(target==a[i])
            {
                System.out.println("Element found at a["+i+"]");
                return;
            }
        }
        System.out.println("Element not found !!");
    }

    public static void main(String args[])
    {
        Scanner sc = new Scanner(System.in);
        int arr[] = {10,46,23,6,37};
        System.out.println("Enter an element to search: ");
        int num = sc.nextInt();
        linearSearch(arr,num);
    }
}