package pages.ccs;

import locators.ccs.CCSLivePageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;
import utils.ExtentReportManager;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Page Object for the CCS-Live app reached through the Enterprise CCS SSO icon.
 * The SSO tab first opens /sso-login?jwtToken=... and then moves to the live dashboard, so URLs are
 * only ever logged without their query string (the token must not reach logs or reports).
 */
public class CCSLivePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT = 30;
    private static final int HEADLESS_TIMEOUT = 45;

    // Page texts that mean the SSO landed on an error page instead of the dashboard
    private static final String[] ERROR_PAGE_TEXTS = { "page not found", "404 not found", "internal server error",
            "500 internal", "access denied", "403 forbidden", "unauthorized", "bad gateway", "service unavailable" };

    public CCSLivePage(WebDriver driver) {
        this.driver = driver;
        int timeout = base.DriverFactory.isHeadlessModeEnabled() ? HEADLESS_TIMEOUT : DEFAULT_TIMEOUT;
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    /** Configured CCS-Live dashboard URL, e.g. https://test.smartping.ai/client/live-dashboard */
    public static String getConfiguredUrl() {
        return ConfigReader.get("ccs.live.url");
    }

    public static String getExpectedTitle() {
        return ConfigReader.get("ccs.live.title");
    }

    /** Current URL without query string / fragment - safe to log. */
    public String getSafeCurrentUrl() {
        return stripQuery(driver.getCurrentUrl());
    }

    public static String stripQuery(String url) {
        return url == null ? "" : url.split("[?#]")[0];
    }

    public static String hostOf(String url) {
        try {
            return new URI(url).getHost();
        } catch (Exception e) {
            return "";
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Waits until a tab not in {@code handlesBefore} is open and returns the new handles,
     * in the order the browser lists them.
     */
    public List<String> waitForNewTabs(Set<String> handlesBefore) {
        ExtentReportManager.logStep("Wait for the CCS-Live tab to open");
        wait.until(d -> d.getWindowHandles().size() > handlesBefore.size());
        List<String> newHandles = new ArrayList<>();
        for (String handle : driver.getWindowHandles()) {
            if (!handlesBefore.contains(handle)) {
                newHandles.add(handle);
            }
        }
        System.out.println("[INFO] New tab(s) opened by CCS SSO: " + newHandles.size());
        return newHandles;
    }

    /**
     * Waits for the SSO token exchange to finish and the live dashboard to show its cards.
     * Returns false (instead of throwing) when the dashboard does not appear, so the caller asserts.
     */
    public boolean waitForLiveDashboard() {
        ExtentReportManager.logStep("Wait for the CCS-Live dashboard to load");
        String dashboardPath = URI.create(getConfiguredUrl()).getPath();
        try {
            wait.until(ExpectedConditions.urlContains(dashboardPath));
            wait.until(ExpectedConditions.visibilityOfElementLocated(CCSLivePageLocators.LIVE_CALLS_CARD));
            System.out.println("[INFO] CCS-Live dashboard loaded: " + getSafeCurrentUrl());
            return true;
        } catch (TimeoutException e) {
            System.out.println("[WARN] CCS-Live dashboard did not load. URL: " + getSafeCurrentUrl()
                    + " Title: " + driver.getTitle());
            return false;
        }
    }

    public boolean isElementDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isSidebarLogoDisplayed() {
        return isElementDisplayed(CCSLivePageLocators.SIDEBAR_LOGO);
    }

    public boolean isLiveCallsCardDisplayed() {
        return isElementDisplayed(CCSLivePageLocators.LIVE_CALLS_CARD);
    }

    public boolean isLoggedInUsersCardDisplayed() {
        return isElementDisplayed(CCSLivePageLocators.LOGGED_IN_USERS_CARD);
    }

    public boolean isSidebarModuleDisplayed(String moduleName) {
        return isElementDisplayed(CCSLivePageLocators.getSidebarModule(moduleName));
    }

    /** Header greeting text, e.g. "Welcome, acpltest67 ccs982". */
    public String getWelcomeGreeting() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(CCSLivePageLocators.WELCOME_GREETING))
                    .getText().trim().replaceAll("\\s+", " ");
        } catch (TimeoutException e) {
            return "";
        }
    }

    /** True when the CCS-Live login form is shown (no valid session). */
    public boolean isCCSLoginPageDisplayed() {
        try {
            WebDriverWait loginWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            loginWait.until(ExpectedConditions.visibilityOfElementLocated(CCSLivePageLocators.LOGIN_PASSWORD_INPUT));
            return !driver.findElements(CCSLivePageLocators.LOGIN_HEADING).isEmpty();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Returns the error text found on the page (404/500/access denied...), or null when there is none. */
    public String findErrorPageText() {
        String pageText = (driver.getTitle() + " " + driver.findElement(By.tagName("body")).getText()).toLowerCase();
        for (String errorText : ERROR_PAGE_TEXTS) {
            if (pageText.contains(errorText)) {
                return errorText;
            }
        }
        return null;
    }

    public void refresh() {
        ExtentReportManager.logStep("Refresh the CCS-Live page");
        driver.navigate().refresh();
    }

    /** Opens the configured CCS-Live URL directly (address bar), without going through SSO. */
    public void open() {
        ExtentReportManager.logStep("Open CCS-Live URL directly");
        driver.get(getConfiguredUrl());
    }
}
