package locators;

import org.openqa.selenium.By;

/**
 * Locators for Enterprise Control Center Page
 * Contains all XPath and CSS selectors for Enterprise Control Center
 * and Team Management functionality
 */
public class EnterpriseControlCenterPageLocators {

        // ==================== Control Center Navigation ====================

        // Control Center menu item in sidebar (li.nav-link with span text)
        public static final By CONTROL_CENTER_MENU = By.xpath(
                        "//li[contains(@class, 'nav-link')]//span[normalize-space()='Control Center'] | " +
                                        "//li[contains(@class, 'nav-link')][.//span[text()='Control Center']] | " +
                                        "//span[normalize-space()='Control Center']/parent::li | " +
                                        "//span[text()='Control Center']");

        // Control Center page header/title
        public static final By CONTROL_CENTER_HEADER = By.xpath(
                        "//h1[contains(text(), 'Control Center')] | " +
                                        "//h2[contains(text(), 'Control Center')] | " +
                                        "//div[contains(@class, 'header')]//span[contains(text(), 'Control Center')] | "
                                        +
                                        "//span[contains(text(), 'Team Management')]");

        // ==================== Team Management Tab ====================

        // Team Management tab/link (appears as tab on Control Center page)
        public static final By TEAM_MANAGEMENT_TAB = By.xpath(
                        "//li[contains(@class, 'nav-link')]//span[normalize-space()='Team Management'] | " +
                                        "//li[contains(@class, 'nav-link')][.//span[text()='Team Management']] | " +
                                        "//span[normalize-space()='Team Management'] | " +
                                        "//a[contains(text(), 'Team Management')] | " +
                                        "//button[contains(text(), 'Team Management')] | " +
                                        "//div[contains(@class, 'tab')]//span[contains(text(), 'Team')]");

        // Team Management page header
        public static final By TEAM_MANAGEMENT_HEADER = By.xpath(
                        "//h1[contains(text(), 'Team Management')] | " +
                                        "//h2[contains(text(), 'Team Management')] | " +
                                        "//div[contains(@class, 'header')]//span[contains(text(), 'Team Management')] | "
                                        +
                                        "//span[contains(text(), 'Team Management')]//ancestor::div[contains(@class, 'header')]");

        // ==================== Table Headers ====================

        // Table header row
        public static final By TABLE_HEADER_ROW = By.xpath(
                        "//table//thead//tr | " +
                                        "//div[contains(@class, 'header-row')] | " +
                                        "//mat-header-row");

        // Individual table headers (mat-mdc-header-cell in mat-header-row)
        public static final By TABLE_HEADERS = By.xpath(
                        "//th[contains(@class, 'mat-mdc-header-cell')] | " +
                                        "//mat-header-row//th | " +
                                        "//table//thead//tr//th | " +
                                        "//mat-header-cell");

        // ==================== Table Body ====================

        // Table rows (mat-row in mat-table)
        public static final By TABLE_ROWS = By.xpath(
                        "//mat-row | " +
                                        "//tr[contains(@class, 'mat-mdc-row')] | " +
                                        "//table//tbody//tr");

        // Table cells
        public static final By TABLE_CELLS = By.xpath(
                        "//table//tbody//tr//td | " +
                                        "//mat-cell");

        // ==================== Pagination ====================

        // Pagination container
        public static final By PAGINATION_CONTAINER = By.xpath(
                        "//div[contains(@class, 'pagination')] | " +
                                        "//mat-paginator | " +
                                        "//nav[contains(@class, 'pagination')]");

        // Next button
        public static final By NEXT_BUTTON = By.xpath(
                        "//button[contains(@aria-label, 'Next')] | " +
                                        "//button[contains(text(), 'Next')] | " +
                                        "//mat-paginator//button[contains(@class, 'next')] | " +
                                        "//button[.//mat-icon[contains(text(), 'chevron_right')]] | " +
                                        "//a[contains(@class, 'page-link') and contains(., 'Next')]");

        // Previous button
        public static final By PREVIOUS_BUTTON = By.xpath(
                        "//button[contains(@aria-label, 'Previous')] | " +
                                        "//button[contains(text(), 'Previous')] | " +
                                        "//mat-paginator//button[contains(@class, 'previous')] | " +
                                        "//button[.//mat-icon[contains(text(), 'chevron_left')]] | " +
                                        "//a[contains(@class, 'page-link') and contains(., 'Previous')]");

        // Page size selector
        public static final By PAGE_SIZE_SELECTOR = By.xpath(
                        "//mat-select[contains(@class, 'page-size')] | " +
                                        "//select[contains(@class, 'page-size')]");

        // Total count display
        public static final By TOTAL_COUNT = By.xpath(
                        "//div[contains(@class, 'paginator-range')] | " +
                                        "//span[contains(@class, 'total')] | " +
                                        "//div[contains(text(), 'of')]");

        // ==================== Search ====================

        // Search input field (placeholder 'Search by Name')
        public static final By SEARCH_INPUT = By.xpath(
                        "//input[@placeholder='Search by Name'] | " +
                                        "//input[@placeholder='Search' or contains(@placeholder, 'Search')] | " +
                                        "//input[@type='search'] | " +
                                        "//div[contains(@class, 'search')]//input");

        // Search button
        public static final By SEARCH_BUTTON = By.xpath(
                        "//button[contains(@aria-label, 'Search')] | " +
                                        "//button[.//mat-icon[contains(text(), 'search')]] | " +
                                        "//button[contains(@class, 'search')]");

        // ==================== Filters ====================

        // Filter button/toggle
        public static final By FILTER_BUTTON = By.xpath(
                        "//button[contains(text(), 'Filter') or contains(text(), 'Filters')] | " +
                                        "//button[.//span[contains(text(), 'Filter')]] | " +
                                        "//button[.//mat-icon[contains(text(), 'filter')]]");

        // Role filter dropdown
        public static final By ROLE_FILTER_DROPDOWN = By.xpath(
                        "//ng-select[contains(@placeholder, 'Role')] | " +
                                        "//mat-select[contains(@placeholder, 'Role')] | " +
                                        "//label[contains(text(), 'Role')]/following::ng-select[1] | " +
                                        "//label[contains(text(), 'Role')]/following::mat-select[1]");

        // Filter apply button
        public static final By FILTER_APPLY_BUTTON = By.xpath(
                        "//button[normalize-space()='Apply'] | " +
                                        "//button[contains(text(), 'Apply')]");

        // Filter clear button
        public static final By FILTER_CLEAR_BUTTON = By.xpath(
                        "//button[normalize-space()='Clear'] | " +
                                        "//button[contains(text(), 'Clear')] | " +
                                        "//button[contains(text(), 'Reset')]");

        // Dynamic role filter option
        public static By getRoleFilterOption(String roleName) {
                return By.xpath(
                                "//ng-dropdown-panel//span[contains(text(), '" + roleName + "')] | " +
                                                "//mat-option[contains(., '" + roleName + "')] | " +
                                                "//li[contains(@class, 'option') and contains(., '" + roleName + "')]");
        }

        // ==================== Action Buttons ====================

        // Add New button (btn btn-primary with 'Add New' text and bi-plus-circle icon)
        public static final By ADD_NEW_BUTTON = By.xpath(
                        "//button[contains(@class, 'btn-primary') and contains(., 'Add New')] | " +
                                        "//button[contains(@class, 'btn-primary')][.//em[contains(@class, 'bi-plus-circle')]] | "
                                        +
                                        "//button[@ng-reflect-router-link='add'] | " +
                                        "//button[contains(text(), 'Add New')] | " +
                                        "//button[.//em[contains(@class, 'bi-plus')]]");

        // Edit button (mattooltip='Edit' with bi-pencil icon)
        public static final By EDIT_BUTTON = By.xpath(
                        "(//button[@mattooltip='Edit'] | " +
                                        "//button[.//em[contains(@class, 'bi-pencil')]] | " +
                                        "//button[contains(@class, 'btn-icon')][.//em[contains(@class, 'bi-pencil')]])[1]");

        // View button (mattooltip='View' with bi-eye icon)
        public static final By VIEW_BUTTON = By.xpath(
                        "(//button[@mattooltip='View'] | " +
                                        "//button[.//em[contains(@class, 'bi-eye')]] | " +
                                        "//button[contains(@class, 'btn-icon')][.//em[contains(@class, 'bi-eye')]])[1]");

        // Edit button by row index
        public static By getEditButtonByRow(int rowIndex) {
                return By.xpath(
                                "(//table//tbody//tr)[" + rowIndex
                                                + "]//button[contains(@mattooltip, 'Edit') or .//mat-icon[contains(text(), 'edit')]] | "
                                                +
                                                "(//mat-row)[" + rowIndex + "]//button[contains(@mattooltip, 'Edit')]");
        }

        // View button by row index
        public static By getViewButtonByRow(int rowIndex) {
                return By.xpath(
                                "(//table//tbody//tr)[" + rowIndex
                                                + "]//button[contains(@mattooltip, 'View') or .//mat-icon[contains(text(), 'visibility')]] | "
                                                +
                                                "(//mat-row)[" + rowIndex + "]//button[contains(@mattooltip, 'View')]");
        }

        // ==================== Table Column Values ====================

        // Role column values
        public static final By ROLE_COLUMN_VALUES = By.xpath(
                        "//table//tbody//tr//td[contains(@class, 'role')] | " +
                                        "//mat-cell[contains(@class, 'role')]");

        // Get role value by row
        public static By getRoleValueByRow(int rowIndex) {
                return By.xpath(
                                "(//table//tbody//tr)[" + rowIndex + "]//td[3] | " +
                                                "(//mat-row)[" + rowIndex + "]//mat-cell[3]");
        }

        // ==================== Loading and Messages ====================

        // Loading spinner
        public static final By LOADING_SPINNER = By.xpath(
                        "//div[contains(@class, 'spinner')] | " +
                                        "//mat-spinner | " +
                                        "//div[contains(@class, 'loading')]");

        // No data message
        public static final By NO_DATA_MESSAGE = By.xpath(
                        "//div[contains(text(), 'No data') or contains(text(), 'No records')] | " +
                                        "//td[contains(@class, 'no-data')] | " +
                                        "//p[contains(text(), 'No results')]");

        // Error message
        public static final By ERROR_MESSAGE = By.xpath(
                        "//div[contains(@class, 'alert-error') or contains(@class, 'error')] | " +
                                        "//mat-error");
}
