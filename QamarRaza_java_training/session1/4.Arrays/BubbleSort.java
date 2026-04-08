import java.util.Scanner;
class BubbleSort
{
    static int[] bubbleSort(int a[])
    {
        int temp;
        for(int i=0;i<a.length-1;i++)
        {
            for(int j=0;j<a.length-1-i;j++)
            {
                if(a[j]>a[j+1])
                {
                    temp = a[j];
                    a[j]=a[j+1];
                    a[j+1]= temp;
                }
            }
        }
        return a;
    }

    public static void main(String args[])
    {
        Scanner sc = new Scanner(System.in);
        int a[] = {10,6,3,8,23};
        System.out.println("Array Before Sorting: ");
        for(int i=0;i<a.length;i++)
        {
            System.out.println("a["+i+"] = "+a[i]);
        }
        
        a = bubbleSort(a);

        System.out.println("Array After Sorting: ");
        for(int i=0;i<a.length;i++)
        {
            System.out.println("a["+i+"] = "+a[i]);
        }
    }
}