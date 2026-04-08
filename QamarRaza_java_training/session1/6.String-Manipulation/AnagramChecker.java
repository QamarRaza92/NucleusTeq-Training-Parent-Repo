import java.util.Arrays;
import java.util.Scanner;

public class AnagramChecker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter first string: ");
        String str1 = scanner.nextLine();
        System.out.println("Enter second string: ");
        String str2 = scanner.nextLine();
        
        System.out.println(areAnagrams(str1, str2));
        
        scanner.close();
    }

    public static boolean areAnagrams(String str1, String str2) {
        String cleanStr1 = str1.replaceAll("\\s", "").toLowerCase();
        String cleanStr2 = str2.replaceAll("\\s", "").toLowerCase();

        if (cleanStr1.length() != cleanStr2.length()) {
            return false;
        }

        char[] charArray1 = cleanStr1.toCharArray();
        char[] charArray2 = cleanStr2.toCharArray();

        Arrays.sort(charArray1);
        Arrays.sort(charArray2);

        return Arrays.equals(charArray1, charArray2);
    }
}