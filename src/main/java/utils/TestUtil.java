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

    /**
     * Builds a plus-alias of {@code email} with a 6-digit random tag, e.g.
     * sonu.bhagat+123@altiquence.com -> sonu.bhagat+009212@altiquence.com (any existing +tag is replaced).
     */
    public static String getUniqueAliasEmail(String email) {
        String[] parts = email.split("@", 2);
        String localPart = parts[0].split("\\+", 2)[0];
        String tag = String.format("%06d", java.util.concurrent.ThreadLocalRandom.current().nextInt(1_000_000));
        return localPart + "+" + tag + "@" + parts[1];
    }

    public static String getRandomUrl() {
        return "https://" + getRandomName(5).toLowerCase() + ".com";
    }
}
