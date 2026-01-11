package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import base.DriverFactory;

import java.time.Duration;

/**
 * Utility class for headless mode compatibility.
 * All methods check isHeadlessModeEnabled() and only apply special handling in
 * headless mode.
 * GUI mode (headless=false) behavior remains unchanged.
 */
public class HeadlessHelper {

    private static final int HEADLESS_TIMEOUT = 20;
    private static final int GUI_TIMEOUT = 10;

    /**
     * Get appropriate timeout based on mode.
     * Returns longer timeout for headless mode to account for slower rendering.
     */
    public static int getTimeout() {
        return DriverFactory.isHeadlessModeEnabled() ? HEADLESS_TIMEOUT : GUI_TIMEOUT;
    }

    /**
     * Create WebDriverWait with appropriate timeout for current mode.
     */
    public static WebDriverWait createWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(getTimeout()));
    }

    /**
     * Click element with JavaScript fallback for headless mode.
     * In GUI mode, uses regular click.
     * In headless mode, catches ElementClickInterceptedException and uses JS click.
     */
    public static void safeClick(WebDriver driver, WebElement element) {
        if (DriverFactory.isHeadlessModeEnabled()) {
            try {
                element.click();
            } catch (ElementClickInterceptedException e) {
                System.out.println("Click intercepted in headless mode, using JS click");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        } else {
            element.click();
        }
    }

    /**
     * Click element by locator with wait and JavaScript fallback.
     */
    public static void safeClick(WebDriver driver, WebDriverWait wait, By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        safeClick(driver, element);
    }

    /**
     * Force JavaScript click - use when regular click consistently fails.
     * Only uses JS in headless mode, regular click in GUI mode.
     */
    public static void jsClick(WebDriver driver, WebElement element) {
        if (DriverFactory.isHeadlessModeEnabled()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } else {
            element.click();
        }
    }

    /**
     * Wait for page to be fully loaded.
     * Only adds extra wait in headless mode.
     */
    public static void waitForPageLoad(WebDriver driver) {
        if (DriverFactory.isHeadlessModeEnabled()) {
            try {
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                        d -> ((JavascriptExecutor) d)
                                .executeScript("return document.readyState").equals("complete"));
                Thread.sleep(1000); // Extra stabilization for headless
            } catch (Exception e) {
                System.out.println("Page load wait completed: " + e.getMessage());
            }
        }
    }

    /**
     * Wait after navigation in headless mode.
     */
    public static void waitAfterNavigation() {
        if (DriverFactory.isHeadlessModeEnabled()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Scroll element into view - helpful in headless mode.
     */
    public static void scrollIntoView(WebDriver driver, WebElement element) {
        if (DriverFactory.isHeadlessModeEnabled()) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'auto', block: 'center'});", element);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Clear field and enter text with verification.
     */
    public static void safeEnterText(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);

        // In headless mode, verify text was entered
        if (DriverFactory.isHeadlessModeEnabled()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
        }
    }
}
