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

        // Check if headless mode is enabled
        if (isHeadlessMode()) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            System.out.println("[DriverFactory] Running in HEADLESS mode (CI/CD)");
        } else {
            System.out.println("[DriverFactory] Running in GUI mode");
        }

        driver.set(new ChromeDriver(options));
        driver.get().manage().window().maximize();
    }

    /**
     * Determines if headless mode should be enabled.
     * System property takes precedence over config file.
     */
    private static boolean isHeadlessMode() {
        // Priority 1: System property (for CI/CD override via -Dbrowser.headless=true)
        String systemProp = System.getProperty("browser.headless");
        if (systemProp != null) {
            return Boolean.parseBoolean(systemProp);
        }

        // Priority 2: Config file property
        try {
            String configProp = ConfigReader.get("browser.headless");
            if (configProp != null) {
                return Boolean.parseBoolean(configProp);
            }
        } catch (Exception e) {
            // Config not available, use default
        }

        // Default: GUI mode (false)
        return false;
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
