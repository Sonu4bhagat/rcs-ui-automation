package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import utils.ConfigReader;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    /**
     * Initializes the WebDriver with configurable headless mode.
     * 
     * Headless mode priority:
     * 1. System property: -Dbrowser.headless=true (for CI/CD override)
     * 2. Config file: browser.headless property
     * 3. Default: false (GUI mode for manual testing)
     */
    public static void initializeDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Common options for both modes
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });

        // Check if headless mode is enabled
        if (isHeadlessMode()) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-notifications");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--remote-allow-origins=*");

            // Stability & Performance options for CI/CD
            options.addArguments("--disable-dev-shm-usage"); // Overcome limited resource problems
            options.addArguments("--disable-features=VizDisplayCompositor"); // Disable compositor for stability
            options.addArguments("--dns-prefetch-disable");
            options.addArguments("--disable-gpu");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--remote-allow-origins=*");
            // User agent to avoid bot detection
            options.addArguments(
                    "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            System.out.println("[DriverFactory] Running in HEADLESS mode (CI/CD)");
        } else {
            System.out.println("[DriverFactory] Running in GUI mode");
        }

        driver.set(new ChromeDriver(options));
        driver.get().manage().window().maximize();
    }

    // Store headless mode state for access by other classes
    private static volatile boolean headlessModeEnabled = false;

    /**
     * Determines if headless mode should be enabled.
     * System property takes precedence over config file.
     */
    private static boolean isHeadlessMode() {
        // Priority 1: System property (for CI/CD override via -Dbrowser.headless=true)
        String systemProp = System.getProperty("browser.headless");
        if (systemProp != null) {
            headlessModeEnabled = Boolean.parseBoolean(systemProp);
            return headlessModeEnabled;
        }

        // Priority 2: Config file property
        try {
            String configProp = ConfigReader.get("browser.headless");
            if (configProp != null) {
                headlessModeEnabled = Boolean.parseBoolean(configProp);
                return headlessModeEnabled;
            }
        } catch (Exception e) {
            // Config not available, use default
        }

        // Default: GUI mode (false)
        headlessModeEnabled = false;
        return false;
    }

    /**
     * Public method to check if running in headless mode.
     * Useful for tests that need to adjust wait times for CI/CD.
     */
    public static boolean isHeadlessModeEnabled() {
        return headlessModeEnabled;
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
