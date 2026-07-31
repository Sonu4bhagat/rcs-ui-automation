package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailReportSender {

    public static void sendReportEmail() {
        try {
            String enableEmail = ConfigReader.get("email.enable");
            if (enableEmail != null && !Boolean.parseBoolean(enableEmail)) {
                System.out.println("[INFO] Email notifications are disabled in config.properties.");
                return;
            }

            String smtpHost = ConfigReader.get("email.smtp.host");
            String smtpPort = ConfigReader.get("email.smtp.port");
            String senderEmail = ConfigReader.get("email.sender");
            String senderPassword = ConfigReader.get("email.password");
            String recipients = ConfigReader.get("email.recipients");

            if (smtpHost == null || senderEmail == null || senderPassword == null || recipients == null) {
                System.out.println("[WARN] Email settings incomplete in config.properties. Skipping email trigger.");
                return;
            }

            System.out.println("[INFO] Preparing automated test execution email report...");

            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort != null ? smtpPort : "465");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.port", smtpPort != null ? smtpPort : "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, "RCS Automation CI/CD"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));

            // Parse ExtentReport HTML or default summary for email body
            File reportFile = new File("test-output/ExtentReport.html");
            String bodyText = generateHtmlSummary();

            MimeMultipart multipart = new MimeMultipart();

            // Body Part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(bodyText, "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);

            // Attachment Part (ExtentReport.html)
            if (reportFile.exists()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(reportFile);
                attachmentPart.setFileName("RCS_Automation_ExtentReport.html");
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);
            message.setSubject("📊 RCS Automation Test Execution Summary Report");

            Transport.send(message);
            System.out.println("[SUCCESS] Test execution summary email sent successfully to: " + recipients);

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to send execution email report: " + e.getMessage());
        }
    }

    private static String generateHtmlSummary() {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; background-color: #f4f6f9; padding: 20px;'>");
        html.append("<div style='max-width: 650px; margin: 0 auto; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); padding: 25px;'>");
        html.append("<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;'>📊 RCS Automation Execution Summary</h2>");
        html.append("<p style='color: #555;'>Hello Team,</p>");
        html.append("<p style='color: #555;'>The automated test suite execution triggered via <b>Jenkins Localhost CI/CD</b> has completed.</p>");
        
        html.append("<table style='width: 100%; border-collapse: collapse; margin: 20px 0;'>");
        html.append("<tr style='background: #34495e; color: #fff;'><th style='padding: 10px; text-align: left;'>Property</th><th style='padding: 10px; text-align: left;'>Details</th></tr>");
        html.append("<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Framework</b></td><td style='padding: 8px; border: 1px solid #ddd;'>RCS Automation Framework (TestNG + Selenium)</td></tr>");
        html.append("<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Environment</b></td><td style='padding: 8px; border: 1px solid #ddd;'>Staging (https://stagingvault.smartping.io/login)</td></tr>");
        html.append("<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Extent Report Attached</b></td><td style='padding: 8px; border: 1px solid #ddd;'>Yes (RCS_Automation_ExtentReport.html)</td></tr>");
        html.append("</table>");

        html.append("<p style='color: #555;'>Please check the attached <b>RCS_Automation_ExtentReport.html</b> for detailed step-by-step logs and screenshots.</p>");
        html.append("<hr style='border: none; border-top: 1px solid #eee; margin-top: 20px;' />");
        html.append("<p style='font-size: 12px; color: #888;'>This is an automated notification sent from RCS Automation Framework running on localhost Jenkins.</p>");
        html.append("</div></body></html>");

        return html.toString();
    }
}
