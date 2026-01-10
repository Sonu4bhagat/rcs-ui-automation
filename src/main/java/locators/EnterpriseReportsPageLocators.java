package locators;

import org.openqa.selenium.By;

/**
 * Locators for the Enterprise Reports Tab page.
 * Used by EnterpriseReportsPage for reports operations.
 */
public class EnterpriseReportsPageLocators {

        // ==================== Reports Navigation ====================

        // Reports sidebar menu item
        public static final By REPORTS_MENU_ITEM = By.xpath(
                        "//span[text()='Reports'] | " +
                                        "//a[contains(@href, 'report')]//span | " +
                                        "//div[contains(@class, 'menu')]//span[contains(text(), 'Reports')]");

        // ==================== Reports Page Header ====================

        // Reports page header/title
        public static final By REPORTS_PAGE_HEADER = By.xpath(
                        "//h1[contains(text(), 'Reports')] | " +
                                        "//h2[contains(text(), 'Reports')] | " +
                                        "//*[contains(@class, 'header') and contains(text(), 'Reports')]");

        // ==================== Service Cards (SMS, RCS, etc.) ====================

        // SMS View Details button
        public static final By SMS_VIEW_DETAILS_BUTTON = By.xpath(
                        "//h6[contains(text(), 'SMS')]/ancestor::div[contains(@class, 'card')]//button[contains(text(), 'View Details')] | "
                                        +
                                        "//div[contains(@class, 'card')]//h6[contains(text(), 'SMS')]/following::button[contains(text(), 'View Details')][1] | "
                                        +
                                        "//div[contains(text(), 'SMS')]/ancestor::div[contains(@class, 'card')]//button");

        // Dynamic locator for View Details button by service name
        public static By getViewDetailsButtonForService(String serviceName) {
                return By.xpath("//h6[contains(text(), '" + serviceName +
                                "')]/ancestor::div[contains(@class, 'card')]//button[contains(text(), 'View Details')]");
        }

        // ==================== SMS Reports Page ====================

        // SMS Reports page header
        public static final By SMS_REPORTS_PAGE_HEADER = By.xpath(
                        "//h1[contains(text(), 'SMS')] | " +
                                        "//h2[contains(text(), 'SMS Reports')] | " +
                                        "//h4[contains(text(), 'SMS')]");

        // Reports table container
        public static final By REPORTS_TABLE = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')] | " +
                                        "//div[contains(@class, 'mat-table-listing')] | " +
                                        "//table[contains(@class, 'mat-table')] | " +
                                        "//table | //tbody");

        // Reports table headers
        public static final By REPORTS_TABLE_HEADERS = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'header')]//div | " +
                                        "//table//thead//th | " +
                                        "//mat-header-row//mat-header-cell | " +
                                        "//thead//th");

        // Reports table rows
        public static final By REPORTS_TABLE_ROWS = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')] | " +
                                        "//table//tbody//tr | " +
                                        "//mat-row | //tbody//tr");

        // No data message
        public static final By NO_DATA_MESSAGE = By.xpath(
                        "//*[contains(text(), 'No Data Available') or contains(text(), 'No data') or contains(text(), 'No records')] | "
                                        +
                                        "//div[contains(@class, 'empty-state') or contains(@class, 'no-data')]");

        // ==================== Search ====================

        // Search input field
        public static final By SEARCH_INPUT = By.xpath(
                        "//input[@placeholder='Search' or contains(@placeholder, 'search') or contains(@placeholder, 'Search')] | "
                                        +
                                        "//input[@type='search'] | " +
                                        "//div[contains(@class, 'search')]//input | " +
                                        "//mat-form-field//input");

        // Search button (if any)
        public static final By SEARCH_BUTTON = By.xpath(
                        "//button[contains(@aria-label, 'Search') or contains(@class, 'search')] | " +
                                        "//button[.//mat-icon[contains(text(), 'search')]]");

        // ==================== Status Filter ====================

        // Status filter dropdown
        public static final By STATUS_FILTER_DROPDOWN = By.xpath(
                        "//input[@role='combobox'] | " +
                                        "//mat-select[contains(@formcontrolname, 'status')] | " +
                                        "//ng-select | " +
                                        "//*[contains(text(), 'Status')]/ancestor::mat-form-field");

        // Active filter option
        public static final By ACTIVE_FILTER_OPTION = By.xpath(
                        "//ng-dropdown-panel//span[contains(text(), 'Active')] | " +
                                        "//mat-option[contains(., 'Active')] | " +
                                        "//input[@id='statusActive'] | " +
                                        "//label[normalize-space()='Active']");

        // Apply filter button
        public static final By APPLY_FILTER_BUTTON = By.xpath(
                        "//button[contains(text(), 'Apply') or contains(text(), 'Search') or contains(text(), 'Filter')]");

        // Clear filter button
        public static final By CLEAR_FILTER_BUTTON = By.xpath(
                        "//button[contains(., 'Clear') or contains(., 'Reset')] | " +
                                        "//span[contains(@class, 'ng-clear-wrapper')]");

        // ==================== Status Column ====================

        // Status column cells
        public static final By STATUS_CELLS = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')]//div[contains(@class, 'status')] | "
                                        +
                                        "//span[contains(@class, 'badge')] | " +
                                        "//table//tbody//tr//td[contains(@class, 'status')] | " +
                                        "//tbody//tr//td//span[contains(@class, 'badge')]");

        // Dynamic locator for status by row
        public static By getStatusByRow(int row) {
                return By.xpath("(//tbody//tr)[" + row + "]//span[contains(@class, 'badge')] | " +
                                "(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])["
                                + row
                                + "]//span[contains(@class, 'badge')]");
        }

        // ==================== First Column (for search) ====================

        // First column cells (typically Name or ID for search)
        public static final By FIRST_COLUMN_CELLS = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')]//div[1] | " +
                                        "//table//tbody//tr//td[1] | " +
                                        "//tbody//tr//td[1]");

        // Dynamic locator for first column by row
        public static By getFirstColumnByRow(int row) {
                return By.xpath("(//tbody//tr)[" + row + "]//td[1] | " +
                                "(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])["
                                + row
                                + "]//div[1]");
        }

        // ==================== Redirect to Service Node ====================

        // Redirect to Service Node icon/button
        public static final By REDIRECT_TO_SERVICE_NODE_ICON = By.xpath(
                        "//button[contains(@mattooltip, 'Redirect') or contains(@mattooltip, 'Service Node') or contains(@aria-label, 'Redirect')] | "
                                        +
                                        "//mat-icon[contains(text(), 'open_in_new') or contains(text(), 'launch')]/parent::button | "
                                        +
                                        "//button[contains(@class, 'redirect') or contains(@class, 'sso')] | " +
                                        "//a[contains(@class, 'redirect')]");

        // Dynamic locator for redirect icon by row
        public static By getRedirectIconByRow(int row) {
                return By.xpath("(//tbody//tr)[" + row
                                + "]//button[contains(@mattooltip, 'Redirect') or contains(@mattooltip, 'Service')] | "
                                +
                                "(//tbody//tr)[" + row
                                + "]//mat-icon[contains(text(), 'open_in_new')]/parent::button | " +
                                "(//tbody//tr)[" + row + "]//button[last()] | " +
                                "(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])["
                                + row
                                + "]//button[last()]");
        }

        // ==================== Service Node Dashboard ====================

        // Service Account Name on Service Node dashboard (breadcrumb or header)
        public static final By SERVICE_ACCOUNT_NAME = By.xpath(
                        "//ol[contains(@class, 'breadcrumb')]//li[last()] | " +
                                        "//ol[contains(@class, 'breadcrumb')]//li[contains(@class, 'active')] | " +
                                        "//nav[@aria-label='breadcrumb']//li[last()] | " +
                                        "//div[contains(@class, 'sidenav-header')]//span[contains(@class, 'text')] | " +
                                        "//div[contains(@class, 'header')]//h4 | " +
                                        "//div[contains(@class, 'header')]//h5 | " +
                                        "//div[contains(@class, 'page-header')]//h1 | " +
                                        "//div[contains(@class, 'page-header')]//h2");

        // Current Tab indicator (should show "Reports" in sidebar)
        public static final By CURRENT_TAB_INDICATOR = By.xpath(
                        "//li[contains(@class, 'active')]//span[contains(text(), 'Report')] | " +
                                        "//a[contains(@class, 'active')]//span[contains(text(), 'Report')] | " +
                                        "//mat-list-item[contains(@class, 'active')]//span[contains(text(), 'Report')] | "
                                        +
                                        "//div[contains(@class, 'mat-tree-node') and contains(@class, 'active')]//span | "
                                        +
                                        "//span[contains(text(), 'Reports')]/ancestor::a[contains(@class, 'active')] | "
                                        +
                                        "//span[contains(text(), 'Reports')]/ancestor::li[contains(@class, 'active')]");

        // Reports tab link (should be active/selected in sidebar)
        public static final By REPORTS_TAB_LINK = By.xpath(
                        "//span[normalize-space()='Reports']/ancestor::a[contains(@class, 'active')] | " +
                                        "//span[normalize-space()='Reports']/ancestor::li[contains(@class, 'active')] | "
                                        +
                                        "//span[normalize-space()='Reports']/ancestor::mat-list-item[contains(@class, 'active')] | "
                                        +
                                        "//a[contains(@class, 'active') and .//span[contains(text(), 'Reports')]] | " +
                                        "//li[contains(@class, 'active') and .//span[contains(text(), 'Reports')]]");

        // ==================== Service Account Name in Row ====================

        // Service Account Name column (for matching after redirect)
        public static final By SERVICE_ACCOUNT_COLUMN = By.xpath(
                        "//tbody//tr//td[2] | " +
                                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')]//div[2]");

        // Dynamic locator for service account by row - Service Account Name is in
        // column 1
        public static By getServiceAccountByRow(int row) {
                return By.xpath("(//tbody//tr)[" + row + "]//td[1] | " +
                                "(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])["
                                + row
                                + "]//div[1]");
        }
}
