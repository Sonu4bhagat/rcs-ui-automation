package locators;

import org.openqa.selenium.By;

public class CustomerOrgPageLocators {

        // Main navigation link
        public static final By CUSTOMER_ORG_LINK = By.xpath(
                        "//span[contains(text(), 'Customer Org')] | //a[contains(@href, 'customer-org')]");

        // Page elements
        public static final By TABLE_HEADERS = By.xpath("//table//th | //mat-header-cell");
        public static final By FILTERS_BUTTON = By.xpath("//button[contains(text(), 'Filters')]");
        public static final By FILTER_ORG_TYPE = By.xpath("//label[contains(., 'Enterprise')]");
        public static final By FILTER_APPLY_BUTTON = By.xpath("//button[contains(text(), 'Apply')]");

        // Drill down elements - updated with multiple strategies
        public static final By DRILL_DOWN_ARROW = By.xpath(
                        "//button[contains(@mattooltip, 'Redirect to Customer Overview')] | " +
                                        "//button[.//mat-icon[text()='arrow_forward' or text()='chevron_right']] | " +
                                        "//button[contains(@class, 'arrow') or contains(@class, 'redirect')]");

        // ========== Wallet Tab ==========
        public static final By WALLET_TABLE = By.xpath("//table");
        public static final By WALLET_SEARCH_FIELD = By.xpath("//input[contains(@placeholder, 'Search')]");

        // ========== Tabs Navigation ==========
        // Simple exact text match for Organizations details tab
        public static final By ORG_DETAIL_TAB = By.xpath(
                        "//*[contains(@class, 'tab')][normalize-space()='Organizations details']");
        // Profile/Profiles tab - browser inspection shows it's an <a> tag with text
        // "Profiles" (plural)
        public static final By PROFILE_TAB = By.xpath(
                        "//a[contains(text(), 'Profile')] | //div[@role='tablist']//div[contains(text(), 'Profile')] | //span[contains(text(), 'Profile')]");

        // Profile Tab - Table Elements
        public static final By PROFILE_TABLE = By.xpath("//table");
        public static final By PROFILE_TABLE_HEADERS = By.xpath(
                        "//table//th | //mat-header-cell");
        public static final By PROFILE_TABLE_ROWS = By.xpath(
                        "//table//tbody//tr");

        // Profile Tab - Action Buttons
        public static final By PROFILE_ADD_NEW_BUTTON = By.xpath(
                        "//button[contains(., 'Add New') or contains(., 'Add Profile') or contains(., 'Create')]");
        public static final By PROFILE_EDIT_BUTTON = By.xpath(
                        "//button[contains(@mattooltip, 'Edit') or .//mat-icon[text()='edit']] | " +
                                        "//table//tbody//tr[1]//button[.//mat-icon[text()='edit']]");
        public static final By PROFILE_VIEW_ICON = By.cssSelector("button[mattooltip='View']");

        // Profile Tab - Search Field
        public static final By PROFILE_SEARCH_FIELD = By.xpath(
                        "//input[contains(@placeholder, 'Search') or contains(@placeholder, 'search')]");

        // Filter button has ID="serviceAccountsDropDown" and text "Filters"
        public static final By PROFILE_FILTER_BUTTON = By.id("serviceAccountsDropDown");
        public static final By PROFILE_FILTER_DROPDOWN = By.xpath(
                        "//mat-select | //select");
        public static final By PROFILE_FILTER_APPLY_BUTTON = By.xpath(
                        "//button[contains(text(), 'Apply')]");

        // Profile View/Details Modal or Page
        public static final By PROFILE_VIEW_MODAL = By.xpath(
                        "//mat-dialog-container | //div[contains(@class, 'modal')]");

        // Tab navigation - loosened to allow "Roles", "Role", "Roles (2)", "Profiles",
        // "Profile"
        public static final By ROLES_TAB = By.xpath(
                        "//div[contains(@class,'tab') and (normalize-space(.)='Roles' or normalize-space(.)='Role')] | "
                                        +
                                        "//a[(normalize-space(.)='Roles' or normalize-space(.)='Role')] | " +
                                        "//button[(normalize-space(.)='Roles' or normalize-space(.)='Role')] | " +
                                        "//div[contains(@class,'tab') and (normalize-space(.)='Profiles' or normalize-space(.)='Profile')] | "
                                        +
                                        "//a[(normalize-space(.)='Profiles' or normalize-space(.)='Profile')] | " +
                                        "//button[(normalize-space(.)='Profiles' or normalize-space(.)='Profile')]");

        // Table elements
        public static final By ROLES_TABLE = By.xpath("//table");
        public static final By ROLES_TABLE_HEADERS = By.xpath("//table//th");
        public static final By ROLES_TABLE_ROWS = By.xpath("//table//tbody//tr");

        // Action buttons - using mattooltip for specificity
        public static final By ROLES_EDIT_BUTTON = By.cssSelector("button[mattooltip='Edit']");
        public static final By ROLES_VIEW_BUTTON = By.cssSelector("button[mattooltip='View']");

        // Search field
        public static final By ROLES_SEARCH_FIELD = By.xpath(
                        "//input[@type='search' or contains(@placeholder, 'Search by name')]");

        // Filter elements - broadened to catch any filter button if ID changes
        public static final By ROLES_FILTER_BUTTON = By.xpath(
                        "//*[@id='CampaignfiltersDropDown'] | //*[@id='serviceAccountsDropDown'] | //button[contains(., 'Filter')]");
        public static final By ROLES_FILTER_OPTION_ALL = By.xpath("//label[contains(., 'All')]");
        public static final By ROLES_FILTER_OPTION_ACTIVE = By.xpath("//label[contains(., 'Active')]");
        public static final By ROLES_FILTER_OPTION_INACTIVE = By.xpath("//label[contains(., 'Inactive')]");
        public static final By ROLES_FILTER_APPLY_BUTTON = By.xpath("//button[contains(text(), 'Apply')]");

        // Add New button
        public static final By ROLES_ADD_NEW_BUTTON = By.xpath(
                        "//button[contains(text(), 'Add New') or contains(text(), 'Create Role') or contains(text(), 'Add Role')]");

        // No data message
        public static final By ROLES_NO_DATA_MESSAGE = By.xpath(
                        "//*[contains(text(), 'No data found') or contains(text(), 'No roles found') or contains(text(), 'No Data Found') or contains(text(), 'No matching records found') or contains(text(), 'No Results Found')]");

        // Form elements - accordions for permissions
        public static final By ROLE_FORM_ACCORDIONS = By.xpath(
                        "//button[contains(@class, 'accordion') or contains(@class, 'mat-expansion')]");

        // Role details/view page elements
        public static final By ROLES_VIEW_MODAL = By.xpath(
                        "//mat-dialog-container | //div[contains(@class, 'modal')] | //div[contains(@class, 'role-details')]");

        // ========== Customer Information Card ==========
        // Dynamic locators for customer overview
        public static final By CUSTOMER_OVERVIEW_SECTION = By.xpath("//div[contains(@class, 'customer-overview')]");

        // Organization Details Tab Elements
        // Customer ID is in <p><strong>Customer Id:</strong> VALUE</p> format, NOT in
        // label tags
        public static final By ORG_DETAILS_CUSTOMER_ID = By.xpath(
                        "//p[strong[contains(text(), 'Customer Id')]] | " +
                                        "//p[contains(., 'Customer Id:')] | " +
                                        "//strong[contains(text(), 'Customer Id')]/parent::p | " +
                                        "//label[contains(text(), 'Customer ID')]/following-sibling::* | " +
                                        "//label[contains(text(), 'Customer ID')]/..//h5");

        // ========== MISSING LOCATORS FOR EXISTING TESTS ==========

        // Page Elements
        public static final By PAGE_HEADER = By.xpath(
                        "//h1 | //h2[contains(@class, 'page-title')] | //*[contains(@class, 'page-header')]");
        public static final By FILTER_DROPDOWN = By.id("serviceAccountsDropDown");
        public static final By ENTERPRISE_FILTER_OPTION = By.id("orgEnterprise");
        public static final By APPLY_FILTER_BUTTON = By.xpath("//button[contains(text(), 'Apply')]");

        // Table Elements
        public static final By TABLE_ROWS = By.xpath("//table//tbody//tr | //mat-row");
        public static final By TABLE_DATA = By.xpath("//table//tbody//tr//td | //mat-cell");
        public static final By ENTERPRISE_ROW_LINK = By.xpath("//table//tr[.//td[contains(text(), 'Enterprise')]]//a");
        public static final By RESELLER_ROW_LINK = By.xpath("//table//tr[.//td[contains(text(), 'Reseller')]]//a");
        public static final By REDIRECT_TO_OVERVIEW_BUTTON = By
                        .cssSelector("button[mattooltip='Redirect to Customer Overview']");

        // Wallet Elements
        public static final By WALLET_BALANCE = By.xpath(
                        "//*[contains(text(), 'Total Available Balance')]/following-sibling::* | " +
                                        "//*[contains(@class, 'balance')]//span");
        public static final By OVERVIEW_WALLET_BALANCE = By.xpath(
                        "//*[contains(text(), 'Available Balance')]/following-sibling::* | " +
                                        "//*[contains(text(), 'Balance')]/parent::*/following-sibling::*");
        public static final By WALLET_SEARCH_INPUT = By.xpath("//input[contains(@placeholder, 'Search')]");
        public static final By WALLET_USAGE_TAB = By
                        .xpath("//div[contains(text(), 'Wallet Usage')] | //a[contains(text(), 'Wallet Usage')]");
        public static final By PREPAID_BALANCE = By.xpath("//*[contains(text(), 'Prepaid')]/following-sibling::*");
        public static final By POSTPAID_BALANCE = By.xpath("//*[contains(text(), 'Postpaid')]/following-sibling::*");

        // Services Elements
        public static final By REDIRECT_TO_SERVICES_BUTTON = By.xpath(
                        "//button[contains(text(), 'Redirect to Services') or contains(@mattooltip, 'Services')]");
        public static final By SERVICES_TABLE_HEADER = By.xpath(
                        "//h1[contains(text(), 'Services')] | //h2[contains(text(), 'Services')]");
        public static final By SERVICES_TABLE_ROWS = By.xpath("//table//tbody//tr");
        public static final By SERVICES_NAME_CELLS = By.xpath("//table//tbody//tr//td[1]");
        public static final By SERVICES_STATUS_CELLS = By.xpath("//table//tbody//tr//td[position()=last()]");

        // Customer Management Elements
        public static final By ADD_CUSTOMER_BUTTON = By.xpath("//button[contains(text(), 'Add Customer')]");
        public static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Search' or @type='search']");
        public static final By SEARCH_BUTTON = By.xpath("//button[contains(., 'Search')] | //button[@type='submit']");
        public static final By CUSTOMER_TABLE = By.xpath("//table");

        // Profile Elements (additional missing ones)
        public static final By PROFILE_SEARCH_INPUT = By.xpath("//input[contains(@placeholder, 'Search')]");
        public static final By PROFILE_VIEW_MODAL_TEXT = By.xpath(
                        "//mat-dialog-container//*[contains(@class, 'mat-dialog-content')] | " +
                                        "//div[contains(@class, 'modal-body')]//*");

        // Transaction/Wallet Recharge Elements
        public static final By WALLET_RECHARGE_BTN = By.xpath(
                        "//button[contains(text(), 'Recharge') or contains(text(), 'Adjustment')]");
        public static final By TXN_MODAL_TITLE = By.xpath(
                        "//h1[contains(@class, 'mat-dialog-title')] | //mat-dialog-container//h2");
        public static final By TXN_TYPE_CREDIT_RADIO = By.xpath(
                        "//input[@type='radio' and contains(@value, 'Credit')] | " +
                                        "//label[contains(text(), 'Credit')]/preceding-sibling::input");
        public static final By TXN_TYPE_DEBIT_RADIO = By.xpath(
                        "//input[@type='radio' and contains(@value, 'Debit')] | " +
                                        "//label[contains(text(), 'Debit')]/preceding-sibling::input");
        public static final By TXN_AMOUNT_INPUT = By.xpath(
                        "//input[@formcontrolname='amount' or @placeholder='Amount']");
        public static final By TXN_DESC_INPUT = By.xpath(
                        "//input[@formcontrolname='description' or @placeholder='Description'] | " +
                                        "//textarea[@formcontrolname='description']");
        public static final By TXN_SUBMIT_BUTTON = By.xpath(
                        "//button[@type='submit' or contains(text(), 'Submit')]");
        public static final By TXN_SUCCESS_MSG = By.xpath(
                        "//*[contains(@class, 'success')] | //*[contains(text(), 'Success') or contains(text(), 'successful')]");
}
