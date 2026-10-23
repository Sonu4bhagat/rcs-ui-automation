package locators.reseller;

import org.openqa.selenium.By;

/**
 * Locators for the Reseller Dashboard's "Analytics" tab.
 *
 * NOTE: first-pass locators seeded from the same conventions confirmed
 * working on the "My Wallets"/"Users" tabs, then corrected against the live
 * Reseller UI - see ResellerDashboardAnalyticsTest for confirmation of which
 * ones actually matched.
 */
public class ResellerDashboardAnalyticsPageLocators {

    // ========== Analytics Tab ==========
    public static final By ANALYTICS_TAB = By.xpath(
            "//a[contains(., 'Analytics')] | //span[contains(., 'Analytics')] | " +
                    "//li[contains(., 'Analytics')] | //div[contains(@class,'nav')][contains(., 'Analytics')]");

    // Confirmed live: the page header under the Analytics tab reads
    // "Performance Overview", not literally "Analytics".
    public static final By ANALYTICS_HEADER = By.xpath("//*[contains(., 'Performance Overview')]");

    // ========== Service Sub-tabs (SMS/IVR/OBD/CCS/WABA/RCS etc.) ==========
    // NOTE: this generic locator also picks up the outer My Wallets/Users/
    // Analytics tabs (confirmed live) - callers filter/assert against the
    // known service names rather than relying on this being scoped only to
    // the service sub-tab bar.
    public static final By SERVICE_TABS = By.xpath(
            "//ul[contains(@class,'nav')]//a | //div[contains(@class,'tab')]//a | //*[@role='tab']");
    public static final String[] ALL_SERVICE_NAMES = { "SMS", "IVR", "OBD", "CCS", "WABA", "RCS" };

    // Confirmed live: each service sub-tab is an Angular Material tab-link
    // (<a mat-tab-link role="tab">). Clicking one sets aria-selected="true"
    // and adds the "mdc-tab--active" class.
    public static By getServiceTabByName(String name) {
        return By.xpath("//a[@role='tab'][normalize-space()='" + name + "']");
    }

    // ========== Customer Filter ==========
    // Confirmed live: this is an ng-select (Angular custom combobox), not a
    // native <select> - the two native <select> elements on this page are the
    // date-range picker's month/year pickers.
    public static final By CUSTOMER_FILTER_DROPDOWN = By.xpath("//ng-select[@labelforid='select-customer']");
    public static final By CUSTOMER_FILTER_CLEAR = By.xpath(
            "//ng-select[@labelforid='select-customer']//span[contains(@class,'ng-clear')]");
    public static final By ACCESS_NOT_ALLOWED_MESSAGE = By.xpath(
            "//*[contains(text(), 'Access to Service Tabs is not allowed')]");

    // ========== Calendar Filter ==========
    // Confirmed live: the visible "<start date> - <end date>" trigger is a
    // readonly <input class="datepicker-dropdown-input"> with an ngbDropdownToggle
    // directive - clicking a calendar-icon ancestor or a text match on the
    // label both failed to open the picker; the input itself is the real target.
    public static final By CALENDAR_FILTER_INPUT = By.xpath("//input[contains(@class,'datepicker-dropdown-input')]");
    public static final By CALENDAR_PRESET_OPTIONS = By.cssSelector(".list-group-item");

    // ========== Error Messages ==========
    // Class-based (not blind text search) to avoid false-positive matches on
    // unrelated CSS class names or hidden validation spans.
    public static final By ERROR_MESSAGE = By.xpath(
            "//*[contains(@class, 'toast-error')] | //*[contains(@class, 'alert-danger')] | " +
                    "//*[contains(@class, 'error-message')]");
}
