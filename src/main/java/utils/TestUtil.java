package utils;

import java.util.Random;

public class TestUtil {

    public static String getRandomName(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String getRandomPhoneNumber() {
        Random random = new Random();
        long number = 1000000000L + (long)(random.nextDouble() * 8999999999L);
        return "9" + String.valueOf(number).substring(1);  // Ensure 12-digit
    }

    public static String getRandomEmail() {
        return getRandomName(5) + "@example.com";
    }

    public static String getRandomUrl() {
        return "https://" + getRandomName(5).toLowerCase() + ".com";
    }
}
