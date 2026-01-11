package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static final String REPORT_PATH = "test-output/ExtentReport.html";

    public static ExtentReports getExtentReports() {
        if (extent == null) {
            // Ensure report directory exists
            new File("test-output").mkdirs();

            // Create Spark Reporter (HTML file)
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH);

            // Configure theme and appearance for professional management-friendly reports
            sparkReporter.config().setDocumentTitle("RCS Automation Test Report");
            sparkReporter.config().setReportName("RCS Automation Execution Summary");
            sparkReporter.config().setTheme(Theme.DARK); // Dark theme for premium appearance
            sparkReporter.config().setTimeStampFormat("MMM dd, yyyy hh:mm:ss a");
            sparkReporter.config().setEncoding("UTF-8");

            // Enable offline mode - report works without internet
            sparkReporter.config().setOfflineMode(true);

            // Configure view order for better management visibility
            // Dashboard first, then tests, then exceptions
            // Configure view order - TEST first ensures lists are visible even if Graphs
            // (JS) are blocked
            sparkReporter.viewConfigurer()
                    .viewOrder()
                    .as(new ViewName[] {
                            ViewName.TEST,
                            ViewName.DASHBOARD,
                            ViewName.CATEGORY,
                            ViewName.EXCEPTION
                    })
                    .apply();

            // Custom CSS for prominent badges and cleaner look
            String customCSS =
                    // Dashboard charts - make them larger and more visible
                    ".dashboard-view .card { min-height: 250px !important; margin-bottom: 20px; }" +
                            ".dashboard-view canvas { min-height: 220px !important; }" +
                            ".dashboard-view .card-header { font-size: 16px; font-weight: bold; }" +

                            // Test list items
                            ".test-list .test-item { padding: 12px !important; margin: 4px 0; border-radius: 6px; }" +

                            // Screenshots - visible and clickable
                            ".test-content img, .media img { max-width: 100%; height: auto; border: 1px solid #555; margin: 10px 0; }"
                            +

                            // Pass/Fail badges
                            ".badge-success, .pass-bg { background-color: #28a745 !important; }" +
                            ".badge-danger, .fail-bg { background-color: #dc3545 !important; }" +
                            ".badge-warning, .skip-bg { background-color: #ffc107 !important; }" +

                            // System info table
                            ".sysenv-container .table td { font-size: 14px; padding: 10px !important; }";

            sparkReporter.config().setCss(customCSS);

            // Removed custom JS to avoid Content Security Policy (CSP) blocking in Jenkins

            // Attach reporter to ExtentReports instance
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            // System/environment info for management context
            extent.setSystemInfo("Project", "RCS Automation Framework");
            extent.setSystemInfo("Application", "RCS Platform");
            extent.setSystemInfo("Environment", getEnvironment());
            extent.setSystemInfo("Execution Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            extent.setSystemInfo("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Browser", getBrowserInfo());
            extent.setSystemInfo("Tester", System.getProperty("user.name"));
        }
        return extent;
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest extentTest = getExtentReports().createTest(testName);
        test.set(extentTest);
        return extentTest;
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest extentTest = getExtentReports().createTest(testName, description);
        test.set(extentTest);
        return extentTest;
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void removeTest() {
        test.remove();
    }

    /**
     * Flushes the report - should be called at the end of test suite
     */
    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }

    /**
     * Get environment from config or default to Staging
     */
    private static String getEnvironment() {
        try {
            return ConfigReader.get("environment") != null ? ConfigReader.get("environment") : "Staging";
        } catch (Exception e) {
            return "Staging";
        }
    }

    /**
     * Get browser info from config or default
     */
    private static String getBrowserInfo() {
        try {
            String browser = ConfigReader.get("browser");
            return browser != null ? browser : "Chrome (Default)";
        } catch (Exception e) {
            return "Chrome (Default)";
        }
    }

    // ==================== STEP LOGGING METHODS ====================
    // These methods can be called from page objects to log test steps
    // They do NOT require any changes to existing test cases

    private static int stepCounter = 0;

    /**
     * Logs a test step with automatic numbering.
     * Call this from page object methods to track what actions are being performed.
     * 
     * @param stepDescription Description of the step being executed
     */
    public static void logStep(String stepDescription) {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            stepCounter++;
            currentTest.info("<b>Step " + stepCounter + ":</b> " + stepDescription);
        }
    }

    /**
     * Logs an informational message in the report.
     * 
     * @param message The message to log
     */
    public static void logInfo(String message) {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.info(message);
        }
    }

    /**
     * Logs a warning message in the report.
     * 
     * @param message The warning message
     */
    public static void logWarning(String message) {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.warning("<font color='orange'>" + message + "</font>");
        }
    }

    /**
     * Logs a successful action/verification in the report.
     * 
     * @param message The success message
     */
    public static void logPass(String message) {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.pass("<font color='green'>✓ " + message + "</font>");
        }
    }

    /**
     * Resets the step counter. Called automatically at the start of each test.
     */
    public static void resetStepCounter() {
        stepCounter = 0;
    }
}
