package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ScreenshotUtil {

    /**
     * Captures a screenshot and saves it to a file.
     * 
     * @param driver   WebDriver instance
     * @param testName Name of the test for filename
     * @return Absolute path to the saved screenshot file
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String destPath = "test-output/screenshots/" + testName + "_" + System.currentTimeMillis() + ".png";

        try {
            File destFile = new File(destPath);
            destFile.getParentFile().mkdirs(); // Ensure directory exists
            Files.copy(screenshot.toPath(), destFile.toPath());
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Captures a screenshot as Base64 encoded string.
     * This method is preferred for ExtentReports as it embeds the image directly in
     * HTML,
     * avoiding Chrome's security restrictions on local file access.
     * 
     * @param driver WebDriver instance
     * @return Base64 encoded string of the screenshot, or null if capture fails
     */
    public static String captureScreenshotAsBase64(WebDriver driver) {
        try {
            if (driver == null) {
                System.err.println("ScreenshotUtil: WebDriver is null, cannot capture screenshot");
                return null;
            }
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            System.err.println("ScreenshotUtil: Failed to capture Base64 screenshot - " + e.getMessage());
            return null;
        }
    }
}
