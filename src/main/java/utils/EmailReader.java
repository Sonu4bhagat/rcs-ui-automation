package utils;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.Properties;

public class EmailReader {

    public static String getOTPFromEmail(String email, String password) {
        // Placeholder configuration - User needs to update config.properties
        String host = ConfigReader.get("email.host");
        String storeType = "imaps";

        if (host == null) {
            System.out.println("Email host not configured. Skipping email fetch.");
            return null;
        }

        try {
            Properties properties = new Properties();
            properties.put("mail.imaps.host", host);
            properties.put("mail.imaps.port", "993");
            properties.put("mail.imaps.starttls.enable", "true");

            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore(storeType);
            store.connect(host, email, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Fetch unseen messages
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            String otp = null;
            // Iterate from latest
            for (int i = messages.length - 1; i >= 0; i--) {
                Message message = messages[i];
                String subject = message.getSubject();
                // Adjust subject match as per actual email
                if (subject != null && subject.contains("OTP")) {
                    String content = getTextFromMessage(message);
                    otp = extractOTP(content);
                    if (otp != null) {
                        message.setFlag(Flags.Flag.SEEN, true); // Mark as read
                        break;
                    }
                }
            }

            inbox.close(false);
            store.close();
            return otp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        }
        // Handle other MimeTypes if needed
        return "";
    }

    private static String extractOTP(String content) {
        // Simple regex to find 6 digit code - Adjust based on actual email format
        // Example: "Your OTP is 123456"
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\b\\d{6}\\b");
        java.util.regex.Matcher m = p.matcher(content);
        if (m.find()) {
            return m.group();
        }
        return null;
    }
}
