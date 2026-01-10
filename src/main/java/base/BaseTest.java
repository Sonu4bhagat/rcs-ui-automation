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

    protected static WebDriver driver;
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

        // Ensure we are on the login page ONLY if retainSession is false
        if (driver != null && !retainSession) {
            String currentUrl = driver.getCurrentUrl();
            String targetUrl = ConfigReader.get("url");
            try {
                // Simple logic: if not matching target, go there.
                // Enhanced logic: if executing sequential flow, we trust the previous state
                if (!currentUrl.equals(targetUrl) && !currentUrl.contains("dashboard")) {
                    // Verify if we are already logged in?
                    // For now, strict adherence to retainSession flag
                    System.out.println("Navigating to login page for test: " + method.getName());
                    driver.get(targetUrl);
                }
            } catch (Exception e) {
                System.out.println("Navigation error in BeforeMethod: " + e.getMessage());
            }
        }
    }

    @AfterMethod
    public void afterEachTest(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            // Log the failure with exception details
            test.fail("<b><font color='red'>Test Failed:</font></b> " + result.getName());
            test.fail(result.getThrowable());

            // Capture and embed screenshot as Base64 (works in Chrome without security
            // issues)
            String base64Screenshot = ScreenshotUtil.captureScreenshotAsBase64(driver);
            if (base64Screenshot != null) {
                test.addScreenCaptureFromBase64String(base64Screenshot, "Failure Screenshot");
                System.out.println("Screenshot captured for failed test: " + result.getName());
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
