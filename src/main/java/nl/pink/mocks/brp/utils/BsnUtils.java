package nl.pink.mocks.brp.utils;

import java.util.regex.Pattern;

public class BsnUtils {

    private static final Pattern NINE_DIGITS = Pattern.compile("\\d{9}");


    public static boolean isValidBsn(String bsn) {
        if (bsn == null || !NINE_DIGITS.matcher(bsn).matches() || bsn.chars().allMatch(c -> c == '0')) {
            return false;
        }

        int length = bsn.length();
        int sum = 0;

        //Elfproef implementation
        for (int index = 0; index < length; index++) {
            int digit = Character.getNumericValue(bsn.charAt(index));
            int weight = (length - index == 9) ? 9 : length - index; // 9 for the first digit if length is 9
            sum += digit * weight;
        }
        return sum % 11 == 0;
    }

    public static boolean isNotValidBsn(String bsn) {
        return !isValidBsn(bsn);
    }

}
