package utils;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Sends the execution summary email once all suites have finished.
 *
 * Registered as a TestNG reporter through META-INF/services/org.testng.ITestNGListener so it
 * runs for every execution mode (suite XML or -Dtest=ClassName). Reporters run after
 * @AfterSuite, so results are read from TestNG's in-memory model instead of
 * testng-results.xml (which does not exist yet at @AfterSuite time).
 */
public class EmailReportSender implements IReporter {

    private static final AtomicBoolean SENT = new AtomicBoolean(false);
    private static final String EXTENT_REPORT = "test-output/ExtentReport.html";
    private static final String EXTENT_ASSETS = "test-output/spark";
    private static final long MAX_ATTACHMENT_BYTES = 20L * 1024 * 1024; // Gmail limit is 25 MB
    private static final int MAX_ERROR_LENGTH = 250;

    private static final String EMOJI_PASSED = "✅";
    private static final String EMOJI_FAILED = "❌";
    private static final String EMOJI_SKIPPED = "⏭";
    private static final String LINE = "==========================================\n";
    private static final String THIN = "------------------------------------------\n";

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        if (!SENT.compareAndSet(false, true)) {
            return;
        }
        try {
            ExtentReportManager.reconcileWithTestNG(suites);
            ExtentReportManager.getExtentReports().flush();
        } catch (Exception e) {
            System.err.println("[WARN] Could not finalize Extent report before emailing: " + e.getMessage());
        }
        sendReportEmail(Summary.from(suites));
    }

    // ------------------------------------------------------------------ results

    static final class Row {
        final String role;
        final String className;
        final String methodName;
        final String detail;

        Row(ITestResult r) {
            this.role = r.getTestClass() != null ? ExtentReportManager.roleOf(r.getTestClass().getRealClass()) : "";
            this.className = r.getTestClass() != null ? simpleName(r.getTestClass().getName()) : "";
            this.methodName = r.getMethod().getMethodName();
            this.detail = errorMessage(r.getThrowable());
        }
    }

    static final class ClassStats {
        int passed, failed, skipped, setupFailures;

        int total() {
            return passed + failed + skipped;
        }
    }

    static final class Summary {
        final List<Row> failed = new ArrayList<>();
        final List<Row> skipped = new ArrayList<>();
        final List<Row> configFailures = new ArrayList<>();
        final Map<String, ClassStats> byClass = new TreeMap<>();
        // Per portal (role folder), in a fixed order so every email reads the same way
        final Map<String, ClassStats> byRole = new LinkedHashMap<>();
        int passed;
        Date start;
        Date end;

        int total() {
            return passed + failed.size() + skipped.size();
        }

        static Summary from(List<ISuite> suites) {
            Summary s = new Summary();
            for (String role : new String[] { "Common", "Enterprise", "Reseller", "Super Admin" }) {
                s.byRole.put(role, new ClassStats());
            }
            for (ISuite suite : suites) {
                for (ISuiteResult sr : suite.getResults().values()) {
                    ITestContext ctx = sr.getTestContext();
                    if (ctx.getStartDate() != null && (s.start == null || ctx.getStartDate().before(s.start))) {
                        s.start = ctx.getStartDate();
                    }
                    if (ctx.getEndDate() != null && (s.end == null || ctx.getEndDate().after(s.end))) {
                        s.end = ctx.getEndDate();
                    }
                    for (ITestResult r : ctx.getPassedTests().getAllResults()) {
                        s.passed++;
                        s.stats(r).passed++;
                        s.roleStats(r).passed++;
                    }
                    List<ITestResult> failures = new ArrayList<>(ctx.getFailedTests().getAllResults());
                    failures.addAll(ctx.getFailedButWithinSuccessPercentageTests().getAllResults());
                    for (ITestResult r : failures) {
                        s.failed.add(new Row(r));
                        s.stats(r).failed++;
                        s.roleStats(r).failed++;
                    }
                    for (ITestResult r : ctx.getSkippedTests().getAllResults()) {
                        // Attempts that RetryAnalyzer re-ran are reported as SKIP; the final attempt is counted instead.
                        if (r.wasRetried()) continue;
                        s.skipped.add(new Row(r));
                        s.stats(r).skipped++;
                        s.roleStats(r).skipped++;
                    }
                    for (ITestResult r : ctx.getFailedConfigurations().getAllResults()) {
                        s.configFailures.add(new Row(r));
                        s.roleStats(r).setupFailures++;
                    }
                }
            }
            Comparator<Row> order = Comparator.comparing((Row r) -> r.className).thenComparing(r -> r.methodName);
            s.failed.sort(order);
            s.skipped.sort(order);
            s.configFailures.sort(order);
            return s;
        }

        private ClassStats stats(ITestResult r) {
            String name = r.getTestClass() != null
                    ? "[" + ExtentReportManager.roleOf(r.getTestClass().getRealClass()) + "] " + simpleName(r.getTestClass().getName())
                    : "Unknown";
            return byClass.computeIfAbsent(name, k -> new ClassStats());
        }

        private ClassStats roleStats(ITestResult r) {
            String role = r.getTestClass() != null ? ExtentReportManager.roleOf(r.getTestClass().getRealClass()) : "Other";
            return byRole.computeIfAbsent(role, k -> new ClassStats());
        }
    }

    static String status(Summary s) {
        if (s.total() == 0) return "NO TESTS EXECUTED";
        return (!s.failed.isEmpty() || !s.configFailures.isEmpty()) ? "FAILURE" : "SUCCESS";
    }

    static String passRate(Summary s) {
        return s.total() == 0 ? "0.00%" : String.format("%.2f%%", s.passed * 100.0 / s.total());
    }

    // ------------------------------------------------------------------ email

    public static void sendReportEmail(Summary summary) {
        try {
            if (!BuildInfo.isJenkinsBuild()) {
                System.out.println("[INFO] Not running inside a Jenkins build (JOB_NAME/BUILD_NUMBER not set). Email not sent.");
                return;
            }

            String enableEmail = ConfigReader.get("email.enable");
            if (enableEmail != null && !Boolean.parseBoolean(enableEmail)) {
                System.out.println("[INFO] Email notifications are disabled (email.enable=false).");
                return;
            }

            String smtpHost = ConfigReader.get("email.smtp.host");
            String smtpPort = Optional.ofNullable(ConfigReader.get("email.smtp.port")).orElse("465");
            String senderEmail = ConfigReader.get("email.sender");
            String senderPassword = ConfigReader.get("email.password");
            String recipients = ConfigReader.get("email.recipients");

            if (smtpHost == null || senderEmail == null || senderPassword == null || recipients == null) {
                System.out.println("[WARN] Email settings incomplete (host/sender/password/recipients). Skipping email.");
                return;
            }

            System.out.println("[INFO] Preparing automated test execution email report...");

            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
            props.put("mail.smtp.connectiontimeout", "30000");
            props.put("mail.smtp.timeout", "30000");
            props.put("mail.smtp.writetimeout", "60000");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            BuildInfo build = BuildInfo.fromEnvironment();
            String status = status(summary);
            String statusEmoji = "SUCCESS".equals(status) ? EMOJI_PASSED : EMOJI_FAILED;

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, "Jenkins"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            message.setSubject(statusEmoji + " Jenkins: " + build.jobName + " #" + build.number + " - " + status
                    + " | Passed " + summary.passed + "/" + summary.total() + " (" + passRate(summary) + ")", "UTF-8");
            message.setSentDate(new Date());

            MimeMultipart multipart = new MimeMultipart("mixed");
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(buildBody(summary, build, status, statusEmoji), "UTF-8");
            multipart.addBodyPart(bodyPart);

            File reportZip = zipExtentReport();
            if (reportZip != null) {
                MimeBodyPart attachment = new MimeBodyPart();
                attachment.attachFile(reportZip);
                attachment.setFileName("RCS_Automation_ExtentReport_Build_" + build.number + ".zip");
                multipart.addBodyPart(attachment);
            }

            message.setContent(multipart);
            Transport.send(message);
            System.out.println("[SUCCESS] Test execution summary email sent to: " + recipients);

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to send execution email report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------ build info

    static final class BuildInfo {
        String jobName;
        String number;
        String buildUrl;
        String suite;

        static BuildInfo fromEnvironment() {
            BuildInfo b = new BuildInfo();
            b.jobName = Optional.ofNullable(env("JOB_NAME")).orElse("RCS_Automation_Pipeline");
            b.number = Optional.ofNullable(env("BUILD_NUMBER")).orElse("local");
            String url = env("BUILD_URL");
            b.buildUrl = url == null ? null : (url.endsWith("/") ? url : url + "/");
            // Jenkins exposes the TEST_TO_RUN build parameter as an environment variable
            b.suite = describeSuite(Optional.ofNullable(env("TEST_TO_RUN"))
                    .orElse(System.getProperty("suiteXmlFile", System.getProperty("test", "testng.xml"))));
            return b;
        }

        private static String describeSuite(String suite) {
            switch (suite) {
                case "testng.xml": return "Full regression - all portals (testng.xml)";
                case "enterprise.xml": return "Enterprise portal (enterprise.xml)";
                case "reseller.xml": return "Reseller portal (reseller.xml)";
                case "superadmin.xml": return "Super Admin portal (superadmin.xml)";
                default: return "Single test class (" + suite + ")";
            }
        }

        /** Jenkins sets JOB_NAME and BUILD_NUMBER for every build step; local IDE/Maven runs do not. */
        static boolean isJenkinsBuild() {
            return env("JOB_NAME") != null && env("BUILD_NUMBER") != null;
        }

        private static String env(String name) {
            String v = System.getenv(name);
            return v == null || v.trim().isEmpty() ? null : v.trim();
        }
    }

    // ------------------------------------------------------------------ body

    private static String buildBody(Summary s, BuildInfo b, String status, String statusEmoji) {
        StringBuilder t = new StringBuilder();
        t.append(LINE).append("   RCS Automation Test Report ").append(statusEmoji).append("\n").append(LINE).append("\n");

        // Plain-language summary first, for readers outside QA (management, developers)
        t.append("OVERALL RESULT\n").append(THIN);
        t.append("Result       : ").append(statusEmoji).append(" ").append(verdict(s)).append("\n");
        t.append("Pass Rate    : ").append(passRate(s)).append("\n");
        t.append("Tested On    : ").append(appHost()).append("\n");
        t.append("Test Scope   : ").append(b.suite).append("\n\n");

        t.append("RESULTS BY PORTAL\n").append(THIN);
        for (Map.Entry<String, ClassStats> e : s.byRole.entrySet()) {
            ClassStats c = e.getValue();
            if (c.total() == 0) continue; // portal not part of this run
            String mark = c.failed > 0 || c.setupFailures > 0 ? EMOJI_FAILED
                    : c.skipped > 0 ? EMOJI_SKIPPED : EMOJI_PASSED;
            t.append(mark).append(" ").append(e.getKey()).append(" : ").append(c.passed).append(" / ")
                    .append(c.total()).append(" passed (").append(String.format("%.2f%%", c.passed * 100.0 / c.total()))
                    .append(")");
            if (c.failed > 0 || c.skipped > 0) {
                t.append(" - ").append(c.failed).append(" failed, ").append(c.skipped).append(" skipped");
            }
            if (c.setupFailures > 0) {
                t.append(", ").append(c.setupFailures).append(" setup step(s) failed");
            }
            t.append("\n");
        }
        t.append("\n");

        t.append("BUILD INFORMATION\n").append(THIN);
        t.append("Job Name     : ").append(b.jobName).append("\n");
        t.append("Build Number : #").append(b.number).append("\n");
        t.append("Status       : ").append(status).append("\n");
        t.append("Started      : ").append(startTime(s)).append("\n");
        t.append("Duration     : ").append(duration(s)).append("\n");
        t.append("Build URL    : ").append(b.buildUrl != null ? b.buildUrl : "N/A (local run)").append("\n\n");

        t.append(LINE).append("         TEST EXECUTION SUMMARY\n").append(LINE);
        t.append("Total Tests  : ").append(s.total()).append("\n");
        t.append("Passed       : ").append(s.passed).append(" ").append(EMOJI_PASSED).append("\n");
        t.append("Failed       : ").append(s.failed.size()).append(" ").append(EMOJI_FAILED).append("\n");
        t.append("Skipped      : ").append(s.skipped.size()).append(" ").append(EMOJI_SKIPPED).append("\n");
        t.append("Pass Rate    : ").append(passRate(s)).append("\n");

        if (!s.byClass.isEmpty()) {
            t.append("\nRESULTS BY TEST CLASS\n").append(THIN);
            for (Map.Entry<String, ClassStats> e : s.byClass.entrySet()) {
                ClassStats c = e.getValue();
                String mark = c.failed > 0 ? EMOJI_FAILED : c.skipped > 0 ? EMOJI_SKIPPED : EMOJI_PASSED;
                t.append(mark).append(" ").append(e.getKey())
                        .append("  (Passed: ").append(c.passed)
                        .append(", Failed: ").append(c.failed)
                        .append(", Skipped: ").append(c.skipped).append(")\n");
            }
        }
        appendRows(t, "FAILED TESTS", s.failed);
        appendRows(t, "SETUP / TEARDOWN FAILURES", s.configFailures);
        appendRows(t, "SKIPPED TESTS", s.skipped);
        t.append("\n");

        t.append("HOW TO READ THIS REPORT\n").append(THIN);
        t.append("Passed  : the feature behaved as expected.\n");
        t.append("Failed  : the application did not behave as the test expected - needs investigation\n");
        t.append("          (a product defect, or a test that needs updating after a UI change).\n");
        t.append("Skipped : the test did not run because an earlier step it depends on failed,\n");
        t.append("          or the required test data was not available.\n");
        t.append("Each failure in the detailed report includes the error and a screenshot.\n\n");

        t.append(LINE).append("             VIEW REPORTS\n").append(LINE);
        t.append("Extent Report (Detailed):\n");
        t.append("Attached to this email - extract the ZIP and open ExtentReport.html\n");
        if (b.buildUrl != null) {
            String artifacts = b.buildUrl + "artifact/";
            t.append(artifacts).append("test-output/ExtentReport.html\n\n");
            t.append("TestNG Index Report:\n").append(artifacts).append("target/surefire-reports/index.html\n\n");
            t.append("Console Output:\n").append(b.buildUrl).append("console\n\n");
        } else {
            t.append("\n");
        }
        t.append(LINE);
        t.append("This is an automated email from Jenkins\n");
        t.append(THIN);
        return t.toString();
    }

    private static void appendRows(StringBuilder t, String title, List<Row> rows) {
        if (rows.isEmpty()) return;
        t.append("\n").append(title).append(" (").append(rows.size()).append("):\n").append(THIN);
        int i = 1;
        for (Row r : rows) {
            t.append(i++).append(". [").append(r.role).append("] ").append(r.className).append(" -> ")
                    .append(r.methodName).append("\n");
            if (!r.detail.isEmpty()) t.append("   Reason: ").append(r.detail).append("\n");
        }
    }

    // ------------------------------------------------------------------ attachment

    /** Zips ExtentReport.html with its spark/ assets so the report renders correctly when opened from the mail. */
    private static File zipExtentReport() {
        File report = new File(EXTENT_REPORT);
        if (!report.exists()) {
            System.out.println("[WARN] Extent report not found at " + EXTENT_REPORT + "; sending email without attachment.");
            return null;
        }
        File zip = new File("target/RCS_Automation_ExtentReport.zip");
        zip.getParentFile().mkdirs();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip))) {
            addToZip(zos, report.toPath(), "ExtentReport.html");
            Path assets = new File(EXTENT_ASSETS).toPath();
            if (Files.isDirectory(assets)) {
                try (Stream<Path> files = Files.walk(assets)) {
                    for (Path p : (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                        addToZip(zos, p, "spark/" + assets.relativize(p).toString().replace('\\', '/'));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[WARN] Could not zip Extent report: " + e.getMessage());
            return null;
        }
        if (zip.length() > MAX_ATTACHMENT_BYTES) {
            System.out.println("[WARN] Extent report ZIP is " + (zip.length() / (1024 * 1024))
                    + " MB (over the mail limit); sending without attachment. Use the Jenkins artifact link instead.");
            return null;
        }
        return zip;
    }

    private static void addToZip(ZipOutputStream zos, Path file, String entryName) throws IOException {
        zos.putNextEntry(new ZipEntry(entryName));
        Files.copy(file, zos);
        zos.closeEntry();
    }

    // ------------------------------------------------------------------ helpers

    private static String simpleName(String fqcn) {
        int i = fqcn.lastIndexOf('.');
        return i >= 0 ? fqcn.substring(i + 1) : fqcn;
    }

    private static String errorMessage(Throwable t) {
        if (t == null) return "";
        String msg = t.getMessage();
        if (msg == null || msg.trim().isEmpty()) msg = t.getClass().getSimpleName();
        // Selenium appends multi-line build/session info; the first line is the meaningful part
        msg = msg.split("\\R", 2)[0].trim();
        // TestNG dependency skips name the object instance ("on instance pkg.Class@1a2b3c") - noise in an email
        msg = msg.replaceAll(" on instance \\S+", "");
        return msg.length() > MAX_ERROR_LENGTH ? msg.substring(0, MAX_ERROR_LENGTH) + "..." : msg;
    }

    /** One plain-language sentence describing the outcome, e.g. for management readers. */
    static String verdict(Summary s) {
        if (s.total() == 0) {
            return "NO TESTS RAN - check the Jenkins console output.";
        }
        if (s.failed.isEmpty() && s.configFailures.isEmpty()) {
            return s.skipped.isEmpty()
                    ? "PASSED - all " + s.total() + " tests passed."
                    : "PASSED - no failures; " + s.skipped.size() + " of " + s.total()
                            + " tests were skipped (see list below).";
        }
        return "FAILED - " + s.failed.size() + " of " + s.total() + " tests failed"
                + (s.configFailures.isEmpty() ? "" : ", " + s.configFailures.size() + " setup step(s) failed")
                + (s.skipped.isEmpty() ? "" : ", " + s.skipped.size() + " skipped")
                + ". Details below.";
    }

    private static String appHost() {
        try {
            return new java.net.URL(ConfigReader.get("url")).getHost();
        } catch (Exception e) {
            return String.valueOf(ConfigReader.get("url"));
        }
    }

    private static String startTime(Summary s) {
        return s.start == null ? "N/A" : new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a z").format(s.start);
    }

    /** Same style as Jenkins' durationString, e.g. "1 hr 5 min", "1 min 58 sec". */
    private static String duration(Summary s) {
        if (s.start == null || s.end == null) return "N/A";
        long sec = Math.max(0, (s.end.getTime() - s.start.getTime()) / 1000);
        long h = sec / 3600, m = (sec % 3600) / 60, ss = sec % 60;
        if (h > 0) return h + " hr " + m + " min";
        if (m > 0) return m + " min " + ss + " sec";
        return ss + " sec";
    }
}
