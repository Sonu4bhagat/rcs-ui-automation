package base;

import utils.ConfigReader;
import base.DriverFactory;
import enums.UserRole;
import pages.common.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.ExtentReportManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import utils.ScreenshotUtil;

import java.lang.reflect.Method;

public class BaseTest {

    protected WebDriver driver;
    protected ExtentReports extent;
    protected ExtentTest test;
    protected boolean retainSession = false; // Flag to skip auto-navigation to login
    // Optional (use with retainSession): role this class logs in as. When set, a test that unexpectedly
    // lands on the login page (browser crash, session ended elsewhere) logs in again as this role and
    // returns to the last page, instead of every remaining test in the class failing.
    protected UserRole sessionRole = null;
    private String lastAppUrl; // last non-login page seen after a test, used for session recovery

    @BeforeSuite
    public void setupReport() {
        extent = ExtentReportManager.getExtentReports();
    }

    @BeforeClass
    public void setUpDriverOnce() {
        DriverFactory.initializeDriver();
        driver = DriverFactory.getDriver();
        // Initial navigation
        driver.get(ConfigReader.get("url"));
    }

    @BeforeMethod
    public void beforeEachTest(Method method, ITestResult result) {
        String description = result.getMethod().getDescription();
        test = (description == null || description.isEmpty())
                ? ExtentReportManager.createTest(ExtentReportManager.displayName(result))
                : ExtentReportManager.createTest(ExtentReportManager.displayName(result), description);
        test.assignCategory(ExtentReportManager.roleOf(getClass()), getClass().getSimpleName());
        // Lets ExtentReportManager.reconcileWithTestNG drop this entry if the attempt is later retried
        result.setAttribute(ExtentReportManager.RESULT_ATTRIBUTE, test);
        ExtentReportManager.resetStepCounter(); // Reset step counter for each new test

        // Robust Session Check & Self-Healing
        try {
            if (driver == null) {
                System.out.println("[WARN] Driver is null. Re-initializing for test: " + method.getName());
                DriverFactory.initializeDriver();
                driver = DriverFactory.getDriver();
                driver.get(ConfigReader.get("url"));
            } else {
                // Check if session is alive (triggers exception if session is lost)
                driver.getCurrentUrl();
            }
        } catch (Exception e) {
            System.err.println(
                    "[CRITICAL] Driver session lost/crashed for test " + method.getName() + ". Restarting driver...");
            try {
                DriverFactory.quitDriver();
            } catch (Exception ex) {
                /* Ignore cleanup errors */ }
            DriverFactory.initializeDriver();
            driver = DriverFactory.getDriver();
            driver.get(ConfigReader.get("url"));
        }

        if (driver != null) {
            String currentUrl = driver.getCurrentUrl();
            String targetUrl = ConfigReader.get("url"); // Login URL

            // Extract hosts to ensure we only redirect if we are on the WRONG page of the
            // SAME system
            String currentHost = "";
            String targetHost = "";
            try {
                currentHost = new java.net.URL(currentUrl).getHost();
                targetHost = new java.net.URL(targetUrl).getHost();
            } catch (Exception e) {
                // Ignore parsing errors
            }

            boolean isSameHost = currentHost.equals(targetHost);
            boolean isOnLoginPage = (currentUrl.contains("login") || currentUrl.equals(targetUrl)) && isSameHost;

            System.out.println(
                    "[DEBUG] Test=" + method.getName() + " URL=" + currentUrl + " HostMatch=" + isSameHost
                            + " RetainSession=" + retainSession);

            if (retainSession) {
                // If we want to retain session, but we are back on login page of the SAME host
                if (isOnLoginPage && sessionRole != null && lastAppUrl != null && !LoginPage.wasLoggedOutByTest()) {
                    restoreSession(method.getName(), currentUrl, targetUrl);
                } else if (isOnLoginPage) {
                    System.out.println(
                            "[WARN] retainSession=true but on Login page of " + targetHost
                                    + ". Forcing navigation to Login to allow re-login.");

                    if (!currentUrl.equals(targetUrl)) {
                        driver.get(targetUrl);
                    }
                }
            } else {
                // !retainSession => Ensure we are at Login Page of the target host
                if (!isOnLoginPage) {
                    System.out.println("[INFO] Navigating to login page for test: " + method.getName());
                    driver.get(targetUrl);
                }
            }
        }
    }

    /**
     * Logs in again as {@link #sessionRole} and returns to the last page the class was on.
     * Only used when the session was lost unexpectedly (not after a test's own logout).
     */
    private void restoreSession(String testName, String currentUrl, String loginUrl) {
        System.out.println("[RECOVERY] Session lost before " + testName + ". Logging in again as " + sessionRole
                + " and returning to " + lastAppUrl);
        test.warning("Session was lost (browser crash or session ended). Logged in again as " + sessionRole
                + " and returned to the previous page.");
        try {
            if (!currentUrl.equals(loginUrl)) {
                driver.get(loginUrl);
            }
            loginAgain(new LoginPage(driver));
            driver.get(lastAppUrl);
        } catch (Exception e) {
            System.err.println("[RECOVERY] Re-login failed: " + e.getMessage());
        }
    }

    /**
     * Logs in as {@link #sessionRole} during session recovery. Override when the class logs in
     * differently (e.g. a specific wallet) or keeps page objects that must use the current driver.
     */
    protected void loginAgain(LoginPage loginPage) {
        switch (sessionRole) {
            case SUPERADMIN:
                loginPage.loginWithSuperAdminCredentials();
                break;
            case ENTERPRISE:
                loginPage.loginWithEnterpriseCredentials();
                break;
            case RESELLER:
                loginPage.loginWithResellerCredentials();
                break;
        }
    }

    /** Remembers the current page if it is inside the app (not the login page). */
    private void rememberLastAppUrl() {
        try {
            String url = driver.getCurrentUrl();
            String appHost = new java.net.URL(ConfigReader.get("url")).getHost();
            // Only pages of the app itself (not external SSO/service-node sites opened from it)
            if (new java.net.URL(url).getHost().equals(appHost)
                    && !url.contains("login") && !url.equals(ConfigReader.get("url"))) {
                lastAppUrl = url;
                LoginPage.clearLoggedOutByTest(); // logged in again since any earlier deliberate logout
            }
        } catch (Exception e) {
            // Browser crashed during the test - keep the previous page for recovery
        }
    }

    @AfterMethod
    public void afterEachTest(ITestResult result) {
        rememberLastAppUrl();
        if (result.getStatus() == ITestResult.FAILURE) {
            // Log the failure with exception details
            test.fail("<b><font color='red'>Test Failed:</font></b> " + result.getName());
            test.fail(result.getThrowable());

            // Capture and embed screenshot as Base64
            String base64Screenshot = ScreenshotUtil.captureScreenshotAsBase64(driver);
            if (base64Screenshot != null) {
                test.addScreenCaptureFromBase64String(base64Screenshot, "Failure Screenshot");
                System.out.println("[INFO] Screenshot captured for failed test: " + result.getName());
            } else {
                test.info("Could not capture screenshot for this failure");
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("<b><font color='green'>Test Passed:</font></b> " + result.getName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.skip("<b><font color='orange'>Test Skipped:</font></b> " + result.getName());
            if (result.getThrowable() != null) {
                test.skip(result.getThrowable());
            }
        }

        ExtentReportManager.removeTest();
    }

    @AfterClass
    public void tearDownDriverOnce() {
        DriverFactory.quitDriver();
    }

    @AfterSuite
    public void tearDownReport() {
        // Email summary is sent by utils.EmailReportSender (TestNG reporter) after the suite completes
        ExtentReportManager.getExtentReports().flush();
    }
}
