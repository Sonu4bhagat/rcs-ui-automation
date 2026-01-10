package locators;

import org.openqa.selenium.By;

/**
 * Locators for Team Management Page
 * Contains all XPath and CSS selectors for Super Admin Team Management
 * functionality
 */
public class TeamManagementPageLocators {

        // ========== Navigation ==========
        // Team Management menu item in sidebar
        public static final By TEAM_MANAGEMENT_MENU = By.xpath(
                        "//li[.//span[text()='Team Management']]");

        // Alternative locator for menu
        public static final By TEAM_MANAGEMENT_MENU_ALT = By.xpath(
                        "//span[text()='Team Management']");

        // ========== Page Verification ==========
        // Page header to verify successful navigation
        public static final By PAGE_HEADER = By.xpath(
                        "//h2[contains(text(), 'Team Members')]");

        // Alternative: Page title
        public static final By PAGE_TITLE = By.xpath(
                        "//h2");

        // ========== Search ==========
        // Search input field
        public static final By SEARCH_FIELD = By.xpath(
                        "//input[@placeholder='Search by name, role']");

        // Alternative search field locator
        public static final By SEARCH_FIELD_ALT = By.cssSelector(
                        "input[placeholder*='Search']");

        // ========== Filters ==========
        // Filters dropdown button
        public static final By FILTERS_BUTTON = By.xpath(
                        "//button[@id='serviceAccountsDropDown']");

        // Alternative filters button
        public static final By FILTERS_BUTTON_ALT = By.id("serviceAccountsDropDown");

        // Filter Status - All
        public static final By FILTER_STATUS_ALL = By.xpath(
                        "//input[@id='statusAll']");

        // Filter Status - Active
        public static final By FILTER_STATUS_ACTIVE = By.xpath(
                        "//input[@id='statusActive']");

        // Filter Status - Inactive
        public static final By FILTER_STATUS_INACTIVE = By.xpath(
                        "//input[@id='statusInactive']");

        // Role name dropdown (combobox)
        public static final By FILTER_ROLE_DROPDOWN = By.xpath(
                        "//input[@role='combobox']");

        // Apply filters button
        public static final By FILTER_APPLY_BUTTON = By.xpath(
                        "//button[contains(text(), 'Apply')]");

        // Specific role option in dropdown
        public static By getFilterRoleOption(String roleName) {
                return By.xpath(
                                "//span[contains(text(), '" + roleName + "')]");
        }

        // ========== Add New Button ==========
        // Add New button
        public static final By ADD_NEW_BUTTON = By.xpath(
                        "//button[contains(text(), 'Add New')]");

        // Alternative: By class
        public static final By ADD_NEW_BUTTON_ALT = By.cssSelector(
                        "button.btn-primary");

        // ========== Table Elements ==========
        // Table wrapper/container
        public static final By TABLE_CONTAINER = By.xpath(
                        "//div[contains(@class, 'table-wrapper')]");

        // All table headers (using th//span)
        public static final By TABLE_HEADERS = By.xpath(
                        "//thead[@class='table-head']//th//span");

        // Alternative: All th elements
        public static final By TABLE_HEADERS_TH = By.xpath(
                        "//thead[@class='table-head']//th");

        // Specific table headers
        public static final By TABLE_HEADER_NAME = By.xpath(
                        "//thead[@class='table-head']//th//span[text()='Name']");

        public static final By TABLE_HEADER_ROLES = By.xpath(
                        "//thead[@class='table-head']//th//span[text()='Roles Name']");

        public static final By TABLE_HEADER_CREATED_ON = By.xpath(
                        "//thead[@class='table-head']//th//span[text()='Created On']");

        public static final By TABLE_HEADER_STATUS = By.xpath(
                        "//thead[@class='table-head']//th//span[text()='Status']");

        // Table body
        public static final By TABLE_BODY = By.xpath(
                        "//tbody[@class='table-body']");

        // Table rows (in tbody)
        public static final By TABLE_ROWS = By.xpath(
                        "//tbody[@class='table-body']//tr[contains(@class, 'table-row')]");

        // Table cells (td elements)
        public static final By TABLE_CELLS = By.xpath(
                        "//tbody[@class='table-body']//tr//td[contains(@class, 'table-data')]");

        // Role Name column values (second column - index 2)
        public static final By TABLE_ROLE_COLUMN_VALUES = By.xpath(
                        "//tbody[@class='table-body']//tr[contains(@class, 'table-row')]//td[2]//div");

        // Get role value for specific row
        public static By getRoleValueByRow(int rowIndex) {
                return By.xpath(
                                "(//tbody[@class='table-body']//tr[contains(@class, 'table-row')])[" + rowIndex
                                                + "]//td[2]//div");
        }

        // ========== Action Buttons ==========
        // Edit button (first in list) - using mattooltip
        public static final By EDIT_BUTTON_FIRST = By.xpath(
                        "(//button[@mattooltip='Edit'])[1]");

        // All edit buttons
        public static final By EDIT_BUTTONS_ALL = By.xpath(
                        "//button[@mattooltip='Edit']");

        // Alternative using icon
        public static final By EDIT_BUTTONS_BY_ICON = By.xpath(
                        "//button[.//em[contains(@class, 'icon-edit')]]");

        // Edit button by row index (1-indexed)
        public static By getEditButtonByRow(int rowIndex) {
                return By.xpath(
                                "(//button[@mattooltip='Edit'])[" + rowIndex + "]");
        }

        // View button (first in list) - using mattooltip
        public static final By VIEW_BUTTON_FIRST = By.xpath(
                        "(//button[@mattooltip='View'])[1]");

        // All view buttons
        public static final By VIEW_BUTTONS_ALL = By.xpath(
                        "//button[@mattooltip='View']");

        // Alternative using icon
        public static final By VIEW_BUTTONS_BY_ICON = By.xpath(
                        "//button[.//em[contains(@class, 'icon-eye')]]");

        // View button by row index (1-indexed)
        public static By getViewButtonByRow(int rowIndex) {
                return By.xpath(
                                "(//button[@mattooltip='View'])[" + rowIndex + "]");
        }

        // ========== Pagination ==========
        // Pagination container
        public static final By PAGINATION_CONTAINER = By.cssSelector(
                        ".pagination");

        // Previous button
        public static final By PAGINATION_PREVIOUS = By.xpath(
                        "//li[contains(@class, 'pagination-prev')]");

        // Next button
        public static final By PAGINATION_NEXT = By.xpath(
                        "//li[contains(@class, 'pagination-next')]");

        // Active page number
        public static final By PAGINATION_ACTIVE_PAGE = By.xpath(
                        "//li[contains(@class, 'active')]//a");

        // All page numbers
        public static final By PAGINATION_PAGE_NUMBERS = By.xpath(
                        "//ul[contains(@class, 'pagination')]//li[@role='presentation']//a");

        // Pagination info (e.g., "Showing 1 to 10 of 50")
        public static final By PAGINATION_INFO = By.cssSelector(
                        ".pagination-info, .showing-info");

        // ========== Validation Elements ==========
        // Success message
        public static final By SUCCESS_MESSAGE = By.xpath(
                        "//div[contains(@class, 'alert-success')]");

        // Error message
        public static final By ERROR_MESSAGE = By.xpath(
                        "//div[contains(@class, 'alert-error')]");

        // Loading spinner
        public static final By LOADING_SPINNER = By.xpath(
                        "//div[contains(@class, 'spinner')]");

        // No data message
        public static final By NO_DATA_MESSAGE = By.xpath(
                        "//div[contains(text(), 'No data')]");
}
