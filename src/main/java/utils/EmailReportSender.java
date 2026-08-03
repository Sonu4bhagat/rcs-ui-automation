package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

            // Parse environment variables for Jenkins build information
            String jobName = System.getenv("JOB_NAME");
            if (jobName == null || jobName.isEmpty()) jobName = "RCS_Automation_Pipeline";

            String buildNumber = System.getenv("BUILD_NUMBER");
            if (buildNumber == null || buildNumber.isEmpty()) buildNumber = "1";

            String buildUrl = System.getenv("BUILD_URL");
            if (buildUrl == null || buildUrl.isEmpty()) {
                buildUrl = "http://localhost:8080/job/" + jobName + "/" + buildNumber + "/";
            }

            // Parse test results from surefire report
            int totalTests = 0;
            int passedTests = 0;
            int failedTests = 0;
            int skippedTests = 0;
            List<String> failedTestNames = new ArrayList<>();

            File resultsFile = new File("target/surefire-reports/testng-results.xml");
            if (resultsFile.exists()) {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(resultsFile.getAbsolutePath())));
                    Matcher totalMatch = Pattern.compile("total=\"(\\d+)\"").matcher(content);
                    Matcher passedMatch = Pattern.compile("passed=\"(\\d+)\"").matcher(content);
                    Matcher failedMatch = Pattern.compile("failed=\"(\\d+)\"").matcher(content);
                    Matcher skippedMatch = Pattern.compile("skipped=\"(\\d+)\"").matcher(content);

                    if (totalMatch.find()) totalTests = Integer.parseInt(totalMatch.group(1));
                    if (passedMatch.find()) passedTests = Integer.parseInt(passedMatch.group(1));
                    if (failedMatch.find()) failedTests = Integer.parseInt(failedMatch.group(1));
                    if (skippedMatch.find()) skippedTests = Integer.parseInt(skippedMatch.group(1));

                    Matcher failMethodMatch = Pattern.compile("test-method status=\"FAIL\" name=\"(\\w+)\"").matcher(content);
                    while (failMethodMatch.find()) {
                        failedTestNames.add(failMethodMatch.group(1));
                    }
                } catch (Exception e) {
                    System.err.println("[WARN] Could not parse testng-results.xml: " + e.getMessage());
                }
            }

            String calculatedStatus = (failedTests > 0) ? "FAILURE" : (totalTests > 0 ? "SUCCESS" : "SUCCESS");
            String statusEmoji = (failedTests > 0) ? "❌" : "✅";
            String emojiPassed = "✅";
            String emojiFailed = "❌";
            String emojiSkipped = "⏩";

            String passRateStr = "0.00%";
            if (totalTests > 0) {
                double pr = (passedTests * 100.0) / totalTests;
                passRateStr = String.format("%.2f", pr) + "%";
            }

            StringBuilder failureSummary = new StringBuilder();
            if (!failedTestNames.isEmpty()) {
                failureSummary.append("\nFAILED TESTS:\n------------------------------------------\n");
                for (String name : failedTestNames) {
                    failureSummary.append(name).append("\n");
                }
            }

            String reportBaseUrl = buildUrl.endsWith("/") ? buildUrl + "artifact/" : buildUrl + "/artifact/";

            StringBuilder bodyText = new StringBuilder();
            bodyText.append("==========================================\n");
            bodyText.append("   RCS Automation Test Report ").append(statusEmoji).append("\n");
            bodyText.append("==========================================\n\n");
            bodyText.append("BUILD INFORMATION\n");
            bodyText.append("------------------------------------------\n");
            bodyText.append("Job Name     : ").append(jobName).append("\n");
            bodyText.append("Build Number : #").append(buildNumber).append("\n");
            bodyText.append("Status       : ").append(calculatedStatus).append("\n");
            bodyText.append("Build URL    : ").append(buildUrl).append("\n\n");
            bodyText.append("==========================================\n");
            bodyText.append("          TEST EXECUTION SUMMARY\n");
            bodyText.append("==========================================\n");
            bodyText.append("Total Tests  : ").append(totalTests).append("\n");
            bodyText.append("Passed       : ").append(passedTests).append(" ").append(emojiPassed).append("\n");
            bodyText.append("Failed       : ").append(failedTests).append(" ").append(emojiFailed).append("\n");
            bodyText.append("Skipped      : ").append(skippedTests).append(" ").append(emojiSkipped).append("\n");
            bodyText.append("Pass Rate    : ").append(passRateStr).append("\n");
            bodyText.append(failureSummary).append("\n");
            bodyText.append("==========================================\n");
            bodyText.append("             VIEW REPORTS\n");
            bodyText.append("==========================================\n");
            bodyText.append("Extent Report (Detailed):\n").append(reportBaseUrl).append("test-output/ExtentReport.html\n\n");
            bodyText.append("TestNG Index Report:\n").append(reportBaseUrl).append("target/surefire-reports/index.html\n\n");
            bodyText.append("Console Output:\n").append(buildUrl.endsWith("/") ? buildUrl + "console" : buildUrl + "/console").append("\n\n");
            bodyText.append("==========================================\n");
            bodyText.append("This is an automated email from Jenkins\n");
            bodyText.append("------------------------------------------\n");

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, "Jenkins"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            message.setSubject(statusEmoji + " Jenkins: " + jobName + " #" + buildNumber + " - " + calculatedStatus, "UTF-8");

            MimeMultipart multipart = new MimeMultipart();

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(bodyText.toString(), "UTF-8");
            multipart.addBodyPart(messageBodyPart);

            File reportFile = new File("test-output/ExtentReport.html");
            if (reportFile.exists()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(reportFile);
                attachmentPart.setFileName("RCS_Automation_ExtentReport.html");
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("[SUCCESS] Test execution summary email sent successfully to: " + recipients);

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to send execution email report: " + e.getMessage());
        }
    }
}
