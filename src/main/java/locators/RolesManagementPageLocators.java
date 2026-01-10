package locators;

import org.openqa.selenium.By;

/**
 * Locators for Roles Management Page
 * Contains all XPath and CSS selectors for Super Admin Roles Management
 * functionality
 */
public class RolesManagementPageLocators {

    // ========== Navigation ==========
    // Roles Management menu item in sidebar
    public static final By ROLES_MANAGEMENT_MENU = By.xpath(
            "//span[text()='Roles Management']");

    // Alternative locator
    public static final By ROLES_MANAGEMENT_MENU_ALT = By.xpath(
            "//li[.//span[text()='Roles Management']]");

    // ========== Page Verification ==========
    // Page header to verify successful navigation
    public static final By PAGE_HEADER = By.xpath(
            "//h2[contains(text(), 'Roles')]");

    // ========== Search ==========
    // Search input field
    public static final By SEARCH_FIELD = By.xpath(
            "//input[@placeholder='Search by name']");

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

    // All table headers (th elements in thead)
    public static final By TABLE_HEADERS = By.xpath(
            "//div[contains(@class, 'table-wrapper')]//thead//th");

    // Alternative: thead with class
    public static final By TABLE_HEADERS_ALT = By.xpath(
            "//thead[@class='table-head']//th");

    // Specific table headers
    public static final By TABLE_HEADER_ROLE_NAME = By.xpath(
            "//thead//th[contains(text(), 'Role Name')]");

    public static final By TABLE_HEADER_USERS = By.xpath(
            "//thead//th[contains(text(), 'Users')]");

    public static final By TABLE_HEADER_PERMISSIONS = By.xpath(
            "//thead//th[contains(text(), 'Permissions')]");

    public static final By TABLE_HEADER_STATUS = By.xpath(
            "//thead//th[contains(text(), 'Status')]");

    public static final By TABLE_HEADER_ACTIONS = By.xpath(
            "//thead//th[contains(text(), 'Actions')]");

    // Table body
    public static final By TABLE_BODY = By.xpath(
            "//div[contains(@class, 'table-wrapper')]//tbody");

    // Table rows (in tbody)
    public static final By TABLE_ROWS = By.xpath(
            "//div[contains(@class, 'table-wrapper')]//tbody//tr");

    // First row's role name (first td in first tr)
    public static final By FIRST_ROLE_NAME = By.xpath(
            "//div[contains(@class, 'table-wrapper')]//tbody//tr[1]//td[1]");

    // ========== Users Column Elements ==========
    // All Users count cells (2nd column)
    public static final By USERS_COLUMN_CELLS = By.xpath(
            "//div[contains(@class, 'table-wrapper')]//tbody//tr//td[2]");

    // All clickable Users count links
    public static final By USERS_COUNT_LINKS = By.xpath(
            "//div[contains(@class, 'table-wrapper')]//tbody//tr//td[2]//a");

    // Users count value (strong tag inside link)
    public static final By USERS_COUNT_VALUES = By.xpath(
            "//div[contains(@class, 'table-wrapper')]//tbody//tr//td[2]//a//strong");

    // Get Users count link by role name
    public static By getUsersCountByRoleName(String roleName) {
        return By.xpath(
                "//div[contains(@class, 'table-wrapper')]//tbody//tr[td[1][normalize-space()='" + roleName
                        + "']]//td[2]//a");
    }

    // Get Users count value by role name
    public static By getUsersCountValueByRoleName(String roleName) {
        return By.xpath(
                "//div[contains(@class, 'table-wrapper')]//tbody//tr[td[1][normalize-space()='" + roleName
                        + "']]//td[2]//a//strong");
    }

    // ========== Action Buttons ==========
    // Edit button (first in list) - using mattooltip
    public static final By EDIT_BUTTON_FIRST = By.xpath(
            "(//button[@mattooltip='Edit'])[1]");

    // All edit buttons
    public static final By EDIT_BUTTONS_ALL = By.xpath(
            "//button[@mattooltip='Edit']");

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

    // View button by row index (1-indexed)
    public static By getViewButtonByRow(int rowIndex) {
        return By.xpath(
                "(//button[@mattooltip='View'])[" + rowIndex + "]");
    }

    // ========== Pagination ==========
    // Previous button
    public static final By PAGINATION_PREVIOUS = By.xpath(
            "//a[@aria-label='Previous']");

    // Next button
    public static final By PAGINATION_NEXT = By.xpath(
            "//a[@aria-label='Next']");

    // Active page number
    public static final By PAGINATION_ACTIVE_PAGE = By.xpath(
            "//li[contains(@class, 'active')]//a");

    // ========== Add New Role Form ==========
    // Role name input
    public static final By FORM_ROLE_NAME = By.xpath(
            "//input[@placeholder='Enter role name']");

    // Role description textarea
    public static final By FORM_ROLE_DESCRIPTION = By.xpath(
            "//textarea[@placeholder='Enter role description']");

    // ========== Permission Accordions ==========
    // Dashboard accordion
    public static final By ACCORDION_DASHBOARD = By.xpath(
            "//button[contains(text(), 'Dashboard')]");

    // Customer Organisation accordion
    public static final By ACCORDION_CUSTOMER_ORG = By.xpath(
            "//button[contains(text(), 'Customer org')]");

    // Own Team Management accordion
    public static final By ACCORDION_OWN_TEAM = By.xpath(
            "//button[contains(text(), 'Own team management')]");

    // Roles Management accordion
    public static final By ACCORDION_ROLES_MGMT = By.xpath(
            "//button[contains(text(), 'Role management')]");

    // Settings accordion
    public static final By ACCORDION_SETTINGS = By.xpath(
            "//button[contains(text(), 'Settings')]");

    // My Profile accordion
    public static final By ACCORDION_MY_PROFILE = By.xpath(
            "//button[contains(text(), 'My profile')]");

    // API & Documentation accordion
    public static final By ACCORDION_API_DOCS = By.xpath(
            "//button[contains(text(), 'API & Documentation')]");

    // Service Node SSO Management accordion (dropdown)
    public static final By ACCORDION_SERVICE_NODE_SSO = By.xpath(
            "//button[contains(text(), 'Service nodes management role SSO')]");

    // ========== Service Node SSO Sub-options ==========
    // Select All checkbox
    public static final By SSO_SELECT_ALL = By.xpath(
            "//span[contains(text(), 'Select All')]/preceding-sibling::div//input");

    // Service nodes SSO
    public static final By SSO_SERVICE_NODES = By.xpath(
            "//div[contains(text(), 'Service nodes SSO')]/following-sibling::div//input");

    // Only for SMS
    public static final By SSO_ONLY_SMS = By.xpath(
            "//div[contains(text(), 'Only for SMS')]/following-sibling::div//input");

    // Only for OBD
    public static final By SSO_ONLY_OBD = By.xpath(
            "//div[contains(text(), 'Only for OBD')]/following-sibling::div//input");

    // Only for IVR
    public static final By SSO_ONLY_IVR = By.xpath(
            "//div[contains(text(), 'Only for IVR')]/following-sibling::div//input");

    // Only for CCS
    public static final By SSO_ONLY_CCS = By.xpath(
            "//div[contains(text(), 'Only for CCS')]/following-sibling::div//input");

    // Only for WABA
    public static final By SSO_ONLY_WABA = By.xpath(
            "//div[contains(text(), 'Only for WABA')]/following-sibling::div//input");

    // Only for RCS
    public static final By SSO_ONLY_RCS = By.xpath(
            "//div[contains(text(), 'Only for RCS')]/following-sibling::div//input");

    // Only for Live Agent
    public static final By SSO_ONLY_LIVE_AGENT = By.xpath(
            "//div[contains(text(), 'Only for Live Agent')]/following-sibling::div//input");

    // All SSO sub-option labels (for duplicate check)
    public static final By SSO_ALL_OPTIONS = By.xpath(
            "//button[contains(text(), 'Service nodes management role SSO')]/following::div[contains(@class, 'accordion-body')]//div[contains(@class, 'form-check')]//label");

    // ========== Form Actions ==========
    // Submit button
    public static final By FORM_SUBMIT_BUTTON = By.xpath(
            "//button[contains(text(), 'Submit')]");

    // Cancel button
    public static final By FORM_CANCEL_BUTTON = By.xpath(
            "//button[contains(text(), 'Cancel')]");

    // Close button (X)
    public static final By FORM_CLOSE_BUTTON = By.xpath(
            "//button[@class='btn-close']");

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
}
