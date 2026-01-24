package nl.pink.mocks.brp.utils;

public class BsnUtils {

    public static boolean isValidBsn(String bsn) {
        if (bsn == null || !bsn.matches("\\d{8,9}")) {
            return false;
        }

        int length = bsn.length();
        int sum = 0;

        //Elfproef implementation
        for (int i = 0; i < length; i++) {
            int digit = Character.getNumericValue(bsn.charAt(i));
            int weight = (length - i == 9) ? 9 : length - i; // 9 for the first digit if length is 9
            sum += digit * weight;
        }
        return sum % 11 == 0;
    }
}
