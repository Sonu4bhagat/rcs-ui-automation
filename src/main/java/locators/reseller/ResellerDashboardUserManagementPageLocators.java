package locators.reseller;

import org.openqa.selenium.By;

/**
 * Locators for the Reseller Dashboard's "User Management" sidebar section.
 * Confirmed live navigation structure:
 *   Sidebar "User Management" -> "User List" -> /rs/user-management
 *   Row arrow -> /rs/user-management/{svCustomerId}/customer-wallets
 *     (4 tabs: Customer wallets / Organizations details / Profile / Roles)
 *   Wallet row's services-action button (ng-reflect-app-is-authorized=
 *     "rsServicesMgmt") -> .../customer-wallets/{walletName}/services
 */
public class ResellerDashboardUserManagementPageLocators {

    // ========== Sidebar Navigation ==========
    public static final By SIDEBAR_USER_MANAGEMENT = By.xpath("//span[normalize-space()='User Management']");
    public static final By SIDEBAR_USER_LIST = By.xpath("//span[normalize-space()='User List']");
    public static final By SIDEBAR_CREATE_USER = By.xpath("//span[normalize-space()='Create User']");

    // ========== User List page ==========
    // NOTE: within this "User Management" section, some tables (User List,
    // Customer wallets) render plain HTML <tr> rows, while others (Profile,
    // Roles) are Angular Material CDK tables using a custom <mat-row> tag for
    // data rows (cells are still real <td mat-cell> though) - confirmed live
    // that //table//tbody//tr alone matches zero rows on those. These row/
    // cell locators are shared across all of them, so they union both
    // patterns; the row-scoped action-button locators further down are only
    // ever used on the plain-<tr> tables and are left as-is.
    public static final By USER_LIST_TABLE_HEADERS = By.xpath("//table//thead//th");
    public static final By USER_LIST_TABLE_ROWS = By.xpath("//table//tbody//tr | //mat-row");
    public static final By NO_DATA_MESSAGE = By.xpath(
            "//*[contains(text(), 'There is no data available') or contains(text(), 'No data available') or contains(text(), 'No Data Found') or contains(text(), 'No Results Found')]");

    public static By getUserListCellByRowAndColumn(int row, int column) {
        return By.xpath("(//table//tbody//tr | //mat-row)[" + row + "]//td[" + column + "]");
    }

    public static final By SEARCH_INPUT = By.xpath(
            "//input[contains(@placeholder, 'Search by') or contains(@placeholder, 'Search') or contains(@placeholder, 'search')]");
    public static final By FILTER_BUTTON = By.xpath("//button[contains(., 'Filter')] | //*[@mattooltip='Filters']");
    public static final By STATUS_FILTER_ACTIVE = By.xpath(
            "//label[normalize-space()='Active'] | //input[@value='active' or @value='Active']");
    public static final By APPLY_FILTER_BUTTON = By.xpath("//button[contains(text(), 'Apply')]");
    public static final By CLEAR_ALL_FILTERS_BUTTON = By.xpath(
            "//button[contains(., 'Clear All') or contains(., 'Clear all')]");

    public static final By CREATE_USER_BUTTON = By.xpath(
            "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'create user')]");

    public static By getUpdatePlanButtonByRow(int row) {
        return By.xpath("(//table//tbody//tr)[" + row + "]//button[contains(., 'Update Plan')]");
    }

    public static By getAddCreditButtonByRow(int row) {
        return By.xpath("(//table//tbody//tr)[" + row + "]//button[contains(., 'Add Credit')]");
    }

    public static By getRowArrowButton(int row) {
        return By.xpath("(//table//tbody//tr)[" + row + "]//button[last()]");
    }

    public static final By PAGINATION_NEXT = By.xpath("//a[normalize-space()='Next' or @aria-label='Next']");
    public static final By PAGINATION_PREVIOUS = By.xpath("//a[normalize-space()='Previous' or @aria-label='Previous']");

    // ========== Customer wallets tab (per-org drill-down) ==========
    public static final By CUSTOMER_WALLETS_TAB = By.xpath("//span[normalize-space()='Customer wallets']");
    public static final By ORGANIZATION_DETAILS_TAB = By.xpath("//span[normalize-space()='Organizations details']");
    public static final By PROFILE_TAB = By.xpath("//span[normalize-space()='Profile']");
    public static final By ROLES_TAB = By.xpath("//span[normalize-space()='Roles']");

    public static final By ADD_WALLET_BUTTON = By.xpath(
            "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add wallet')]");

    public static By getWalletServicesButtonByRow(int row) {
        return By.xpath("(//table//tbody//tr)[" + row + "]//button[contains(@ng-reflect-app-is-authorized,'rsServicesMgmt')]");
    }

    // ========== Wallet Services page ==========
    // Confirmed live: "Add New" is a <span>Add New</span> inside a clickable card (not a <button>) and
    // opens .../services/create/service-account. Match the element's own text - the previous
    // contains(., 'add new') also matched every ancestor, so the first match was <html>.
    public static final By ADD_NEW_SERVICE_BUTTON = By.xpath(
            "//*[self::span or self::button or self::a][normalize-space(text())='Add New']");
    public static final By REFILL_BUTTON = By.xpath("//button[normalize-space()='Refill']");
    public static final By DEDUCT_BUTTON = By.xpath("//button[normalize-space()='Deduct']");
    public static final By WALLET_BALANCE_VALUE = By.xpath(
            "//*[contains(text(),'Available Wallet Balance')]/following-sibling::*[1]");
    public static By getServiceCardViewDetailsButton(String serviceName) {
        return By.xpath("//*[self::h2 or self::h3 or self::h4 or self::span][normalize-space()='" + serviceName
                + "']/ancestor::div[1]/following::button[normalize-space()='View Details'][1] | "
                + "//*[normalize-space()='" + serviceName + "']/parent::*/parent::*//button[normalize-space()='View Details']");
    }

    // Refill/Deduct amount modal - confirmed live: two separate required
    // fields (Amount + Confirm Amount), and the real submit button is
    // labeled "Recharge Amount" (Refill) / "Deduct Amount" (Deduct) - not
    // "Refill"/"Deduct"/"Submit"/"Confirm" as originally assumed.
    public static final By REFILL_DEDUCT_AMOUNT_INPUT = By.id("amount");
    public static final By REFILL_DEDUCT_CONFIRM_AMOUNT_INPUT = By.id("confirmAmount");
    // Confirmed live: a required "Reason" textarea (formcontrolname="reason")
    // also gates form validity - easy to miss since it isn't an <input>.
    public static final By REFILL_DEDUCT_REASON_TEXTAREA = By.id("reason");
    public static final By REFILL_DEDUCT_SUBMIT_BUTTON = By.xpath(
            "//button[normalize-space()='Recharge Amount' or normalize-space()='Deduct Amount']");

    // ========== Organization Details tab ==========
    public static final By CONTACT_INFORMATION_HEADER = By.xpath("//*[normalize-space()='Contact Information']");
    public static final By ORGANIZATION_LEVEL_VALUE = By.xpath(
            "//*[contains(text(),'Organization Level')]/following-sibling::*[1] | //*[contains(text(),'Organization Level')]/parent::*/following-sibling::*[1]");

    // ========== Profile tab ==========
    public static final By TEAM_MEMBERS_HEADER = By.xpath("//*[normalize-space()='Team Members']");
    public static final By PROFILE_ADD_NEW_BUTTON = By.xpath(
            "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add new')]");

    public static By getProfileEditButtonByRow(int row) {
        return By.xpath("(//table//tbody//tr | //mat-row)[" + row + "]//button[@mattooltip='Edit']");
    }

    public static By getProfileViewButtonByRow(int row) {
        return By.xpath("(//table//tbody//tr | //mat-row)[" + row + "]//button[@mattooltip='View']");
    }

    // ========== Roles tab ==========
    public static final By ROLE_MANAGEMENT_HEADER = By.xpath("//*[normalize-space()='Role Management']");
    public static final By ROLES_ADD_NEW_BUTTON = By.xpath(
            "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add new')]");

    public static By getRoleEditButtonByRow(int row) {
        return By.xpath("(//table//tbody//tr | //mat-row)[" + row + "]//button[@mattooltip='Edit']");
    }

    public static By getRoleViewButtonByRow(int row) {
        return By.xpath("(//table//tbody//tr | //mat-row)[" + row + "]//button[@mattooltip='View']");
    }

    public static final By OVERVIEW_PANEL_HEADER = By.xpath("//*[normalize-space()='Overview']");
}
