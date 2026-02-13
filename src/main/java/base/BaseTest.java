package base;

import utils.ConfigReader;
import base.DriverFactory;
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
    public void beforeEachTest(Method method) {
        test = ExtentReportManager.createTest(method.getName());
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
            boolean isOnLoginPage = currentUrl.contains("login") || currentUrl.equals(targetUrl);

            System.out.println(
                    "[DEBUG] Test=" + method.getName() + " URL=" + currentUrl + " RetainSession=" + retainSession);

            if (retainSession) {
                // If we want to retain session, but we are back on login page (e.g. prev test
                // logged out or crashed)
                if (isOnLoginPage) {
                    System.out.println(
                            "[WARN] retainSession=true but on Login page. Forcing navigation to Login to allow re-login.");

                    // Most tests call `loginPage.loginWith...`. If that method sees dashboard, it
                    // skips login.
                    // If it sees login page, it logs in. So ensuring we are purely on login page is
                    // good.
                    if (!currentUrl.equals(targetUrl)) {
                        driver.get(targetUrl);
                    }
                }
            } else {
                // !retainSession => Ensure we are at Login Page
                if (!isOnLoginPage) {
                    System.out.println("[INFO] Navigating to login page for test: " + method.getName());
                    driver.get(targetUrl);
                }
            }
        }
    }

    @AfterMethod
    public void afterEachTest(ITestResult result) {
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
        extent.flush();
    }
}
