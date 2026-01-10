package locators;

import org.openqa.selenium.By;

/**
 * Locators for Enterprise Roles Management Page
 * Contains all XPath and CSS selectors for Enterprise Control Center
 * Roles Management functionality
 */
public class EnterpriseRolesManagementLocators {

        // ==================== Navigation ====================

        // Roles Management tab in Control Center (mat-mdc-tab-link element)
        public static final By ROLES_MANAGEMENT_TAB = By.xpath(
                        "//a[contains(@class, 'mat-mdc-tab-link')]//span[contains(text(), 'Roles Management')] | " +
                                        "//a[@role='tab'][.//span[text()='Roles Management']] | " +
                                        "//a[contains(@class, 'mdc-tab')]//span[text()='Roles Management']");

        // ==================== Page Verification ====================

        // Page header to verify successful navigation
        public static final By PAGE_HEADER = By.xpath(
                        "//h2[contains(text(), 'Roles')] | " +
                                        "//h3[contains(text(), 'Roles')] | " +
                                        "//span[contains(@class, 'header') and contains(text(), 'Roles')]");

        // Roles section header
        public static final By ROLES_SECTION_HEADER = By.xpath(
                        "//h5[contains(text(), 'Roles')] | " +
                                        "//div[contains(@class, 'section-header')]//span[contains(text(), 'Roles')]");

        // ==================== Search ====================

        // Search input field
        public static final By SEARCH_FIELD = By.xpath(
                        "//input[@placeholder='Search by name'] | " +
                                        "//input[@placeholder='Search by Name'] | " +
                                        "//input[contains(@placeholder, 'Search')]");

        // ==================== Add New Button ====================

        // Add New button (btn btn-primary with text 'Add New')
        public static final By ADD_NEW_BUTTON = By.xpath(
                        "//button[contains(@class, 'btn-primary') and contains(., 'Add New')] | " +
                                        "//button[.//em[contains(@class, 'bi-plus-circle')]] | " +
                                        "//button[contains(text(), 'Add New')]");

        // ==================== Table Elements ====================

        // Table wrapper/container
        public static final By TABLE_CONTAINER = By.xpath(
                        "//div[contains(@class, 'table-wrapper')] | " +
                                        "//section[contains(@class, 'mat-table-listing')]");

        // Table headers (th elements)
        public static final By TABLE_HEADERS = By.xpath(
                        "//th[contains(@class, 'mat-mdc-header-cell')] | " +
                                        "//thead//th | " +
                                        "//mat-header-row//th");

        // Table rows
        public static final By TABLE_ROWS = By.xpath(
                        "//mat-row | " +
                                        "//tr[contains(@class, 'mat-mdc-row')] | " +
                                        "//tbody//tr");

        // First role name (first column of first row)
        public static final By FIRST_ROLE_NAME = By.xpath(
                        "(//mat-row)[1]//td[1] | " +
                                        "(//tbody//tr)[1]//td[1] | " +
                                        "(//mat-row)[1]//td[contains(@class, 'mat-column-roleName')]");

        // ==================== Users Column ====================

        // Users count cells (2nd column)
        public static final By USERS_COLUMN_CELLS = By.xpath(
                        "//mat-row//td[2] | " +
                                        "//tbody//tr//td[2]");

        // Clickable Users count links
        public static final By USERS_COUNT_LINKS = By.xpath(
                        "//mat-row//td[2]//a | " +
                                        "//tbody//tr//td[2]//a");

        // Get Users count link by role name
        public static By getUsersCountByRoleName(String roleName) {
                return By.xpath(
                                "//mat-row[.//td[1][contains(., '" + roleName + "')]]//td[2]//a | " +
                                                "//tbody//tr[td[1][contains(., '" + roleName + "')]]//td[2]//a");
        }

        // ==================== Action Buttons ====================

        // Edit button (first) - using mattooltip
        public static final By EDIT_BUTTON_FIRST = By.xpath(
                        "(//button[@mattooltip='Edit'])[1] | " +
                                        "(//button[.//em[contains(@class, 'bi-pencil')]])[1]");

        // All edit buttons
        public static final By EDIT_BUTTONS_ALL = By.xpath(
                        "//button[@mattooltip='Edit'] | " +
                                        "//button[.//em[contains(@class, 'bi-pencil')]]");

        // View button (first) - using mattooltip
        public static final By VIEW_BUTTON_FIRST = By.xpath(
                        "(//button[@mattooltip='View'])[1] | " +
                                        "(//button[.//em[contains(@class, 'bi-eye')]])[1]");

        // All view buttons
        public static final By VIEW_BUTTONS_ALL = By.xpath(
                        "//button[@mattooltip='View'] | " +
                                        "//button[.//em[contains(@class, 'bi-eye')]]");

        // ==================== Pagination ====================

        // Previous button
        public static final By PAGINATION_PREVIOUS = By.xpath(
                        "//a[@aria-label='Previous'] | " +
                                        "//button[contains(@aria-label, 'Previous')]");

        // Next button
        public static final By PAGINATION_NEXT = By.xpath(
                        "//a[@aria-label='Next'] | " +
                                        "//button[contains(@aria-label, 'Next')]");

        // ==================== Add New Role Form ====================

        // Role name input
        public static final By FORM_ROLE_NAME = By.xpath(
                        "//input[@placeholder='Enter role name'] | " +
                                        "//input[contains(@formcontrolname, 'name')]");

        // Role description textarea
        public static final By FORM_ROLE_DESCRIPTION = By.xpath(
                        "//textarea[@placeholder='Enter role description'] | " +
                                        "//textarea[contains(@formcontrolname, 'description')]");

        // ==================== Permission Accordions ====================

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

        // Service Node SSO Management accordion
        public static final By ACCORDION_SERVICE_NODE_SSO = By.xpath(
                        "//button[contains(text(), 'Service nodes management role SSO')]");

        // ==================== Service Node SSO Sub-options ====================

        // All SSO sub-option labels (for duplicate check)
        public static final By SSO_ALL_OPTIONS = By.xpath(
                        "//button[contains(text(), 'Service nodes management role SSO')]/following::div[contains(@class, 'accordion-body')]//div[contains(@class, 'form-check')]//label");

        // ==================== Form Actions ====================

        // Submit button
        public static final By FORM_SUBMIT_BUTTON = By.xpath(
                        "//button[contains(text(), 'Submit')]");

        // Cancel button
        public static final By FORM_CANCEL_BUTTON = By.xpath(
                        "//button[contains(text(), 'Cancel')]");

        // Close button (X)
        public static final By FORM_CLOSE_BUTTON = By.xpath(
                        "//button[@class='btn-close'] | " +
                                        "//button[contains(@class, 'close')]");
}
