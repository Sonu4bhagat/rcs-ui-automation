package locators.ccs;

import org.openqa.selenium.By;

/**
 * Locators for the CCS-Live agent login configuration form (opened by the Users row action "Login") and the
 * Agent Live Dashboard it leads to.
 */
public class CCSAgentLoginPageLocators {

        // ==================== Agent login configuration form ====================
        public static final By LOGIN_TYPE_SELECT = By.id("login_type");
        public static final By CAMPAIGN_SELECT = By.id("select_campaign");
        public static final By CAMPAIGN_SELECTED_LABEL = By.xpath("//ng-select[@id='select_campaign']//span[contains(@class, 'ng-value-label')]");
        public static final By CAMPAIGN_OPTIONS = By.xpath("//ng-dropdown-panel//div[contains(@class, 'ng-option')]");
        public static final By SUBMIT_BUTTON = By.xpath("//button[normalize-space()='Submit']");
        public static final By FORM_LABELS = By.xpath("//label");

        // ==================== Agent Live Dashboard ====================
        public static final By DASHBOARD_HEADINGS = By.xpath("//h1|//h2|//h3|//h4|//h5|//h6");
        public static final By SWITCH_CAMPAIGN_BUTTON = By.xpath("//button[normalize-space()='Switch Campaign']");
}
