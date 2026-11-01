package locators.ccs;

import org.openqa.selenium.By;

/**
 * Locators for the CCS-Live app, opened in a new tab by the CCS SSO icon on
 * Enterprise Services > CCS.
 */
public class CCSLivePageLocators {

        // ==================== Live Dashboard ====================
        public static final By SIDEBAR_LOGO = By.xpath("//img[contains(@class, 'logo_full')]");
        public static final By LIVE_DASHBOARD_MENU = By.xpath("//li//span[normalize-space()='Live Dashboard']");
        public static final By LIVE_CALLS_CARD = By.xpath("//h6[contains(@class, 'title') and normalize-space()='Live Calls']");
        public static final By LOGGED_IN_USERS_CARD = By
                        .xpath("//h6[contains(@class, 'title') and normalize-space()='Logged in Users']");
        // Header greeting: "Welcome, <wallet name> <service account name>"
        public static final By WELCOME_GREETING = By
                        .xpath("//span[normalize-space()='Welcome,']/parent::span");
        public static final By LOADING_SPINNER = By.id("loading");

        // Sidebar modules of the CCS-Live app
        public static final String[] SIDEBAR_MODULES = { "Live Dashboard", "Insights", "Call Flow", "Users",
                        "Reports" };

        public static By getSidebarModule(String moduleName) {
                return By.xpath("//li//span[normalize-space()='" + moduleName + "']");
        }

        // ==================== CCS-Live Login Page ====================
        // Shown when CCS-Live is opened without a valid SSO session
        public static final By LOGIN_HEADING = By.xpath("//*[normalize-space()='Login to your account']");
        public static final By LOGIN_PASSWORD_INPUT = By.xpath("//input[@type='password']");
}
