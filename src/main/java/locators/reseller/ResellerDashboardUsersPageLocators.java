package locators.reseller;

import org.openqa.selenium.By;

/**
 * Locators for the Reseller Dashboard's "Users" tab.
 *
 * NOTE: first-pass locators seeded from the same conventions confirmed
 * working on the "My Wallets" tab (ResellerDashboardPageLocators), then
 * corrected against the live Reseller UI - see ResellerDashboardUsersTest for
 * confirmation of which ones actually matched.
 */
public class ResellerDashboardUsersPageLocators {

        // ========== Users Tab ==========
        public static final By USERS_TAB = By.xpath(
                        "//a[contains(., 'Users')] | //span[contains(., 'Users')] | " +
                                        "//li[contains(., 'Users')] | //div[contains(@class,'nav')][contains(., 'Users')]");

        public static final By USERS_HEADER = By.xpath(
                        "//h1[contains(., 'Users')] | //h2[contains(., 'Users')] | //h3[contains(., 'Users')] | " +
                                        "//*[contains(@class, 'header') and contains(., 'Users')] | //*[contains(@class,'page-title') and contains(., 'Users')]");

        // ========== Users Listing Table ==========
        public static final By USERS_TABLE_HEADERS = By.xpath(
                        "//table//thead//th | //div[contains(@class, 'table-header')]//div");
        public static final By USERS_TABLE_ROWS = By.xpath(
                        "//table//tbody//tr | //div[contains(@class, 'table-row')]");
        public static final By NO_DATA_MESSAGE = By.xpath(
                        "//*[contains(text(), 'There is no data available') or contains(text(), 'No data available') or contains(text(), 'No Data Found')]");

        public static By getUserCellByRowAndColumn(int row, int column) {
                return By.xpath("(//table//tbody//tr)[" + row + "]//td[" + column + "]");
        }

        public static By getUserStatusByRow(int row) {
                return By.xpath("(//table//tbody//tr)[" + row
                                + "]//*[contains(text(), 'Active') or contains(text(), 'Inactive')]");
        }

        // ========== Pagination ==========
        public static final By PAGINATION_NEXT = By.xpath(
                        "//a[@aria-label='Next'] | //button[contains(@class,'next')] | //li[contains(@class,'next')]//a");
        public static final By PAGINATION_PREVIOUS = By.xpath(
                        "//a[@aria-label='Previous'] | //button[contains(@class,'prev')] | //li[contains(@class,'previous')]//a");

        // ========== Search ==========
        public static final By SEARCH_INPUT = By.xpath(
                        "//input[contains(@placeholder, 'Search by name') or contains(@placeholder, 'Search') or contains(@placeholder, 'search')]");

        // ========== Filters ==========
        public static final By FILTER_BUTTON = By.xpath(
                        "//button[contains(., 'Filter')] | //*[@mattooltip='Filters' or @mattooltip='Filter']");
        public static final By STATUS_FILTER_ACTIVE = By.xpath(
                        "//label[normalize-space()='Active'] | //input[@value='active' or @value='Active']");
        public static final By APPLY_FILTER_BUTTON = By.xpath("//button[contains(text(), 'Apply')]");
        public static final By CLEAR_ALL_FILTERS_BUTTON = By.xpath(
                        "//button[contains(., 'Clear All') or contains(., 'Clear all')]");

        // ========== Create User ==========
        // Confirmed live: button text is "Create user" (lowercase "u"), so match
        // case-insensitively rather than assuming "Create User".
        public static final By CREATE_USER_BUTTON = By.xpath(
                        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'create user')] | "
                                        +
                                        "//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'create user') and contains(@class, 'btn')]");

        // ========== Per-row Actions ==========
        public static By getUpdatePlanButtonByRow(int row) {
                return By.xpath("(//table//tbody//tr)[" + row + "]//button[contains(., 'Update Plan')] | " +
                                "(//table//tbody//tr)[" + row + "]//*[contains(@mattooltip, 'Update Plan')]");
        }

        public static By getAddCreditButtonByRow(int row) {
                return By.xpath("(//table//tbody//tr)[" + row + "]//button[contains(., 'Add Credit')] | " +
                                "(//table//tbody//tr)[" + row + "]//*[contains(@mattooltip, 'Add Credit')]");
        }

        public static By getServiceNavButtonByRow(int row) {
                return By.xpath("(//table//tbody//tr)[" + row
                                + "]//button[contains(@mattooltip, 'Service') or contains(@mattooltip, 'Manage')] | " +
                                "(//table//tbody//tr)[" + row + "]//button[last()]");
        }
}
