package locators.ccs;

import org.openqa.selenium.By;

/**
 * Locators for CCS-Live Users management (Users list and Add User form).
 */
public class CCSUsersPageLocators {

        // ==================== Users List ====================
        public static final By USERS_MENU = By.xpath("//li//span[normalize-space()='Users']");
        public static final By ADD_USER_BUTTON = By.xpath("//button[normalize-space()='Add User']");
        public static final By SEARCH_TYPE_SELECT = By.name("searchType");
        public static final By SEARCH_INPUT = By.name("search");
        public static final By TABLE_HEADERS = By.xpath("//table//thead//th");
        public static final By TABLE_ROWS = By.xpath("//table//tbody/tr");

        // Option values of SEARCH_TYPE_SELECT
        public static final String SEARCH_BY_NAME = "name";
        public static final String SEARCH_BY_USER_NAME = "user_name";

        // Users list columns (header text as shown in the app)
        public static final String COL_CONTACT_NUMBER = "Contact Number";
        public static final String COL_USER_ID = "User Id";
        public static final String COL_NAME = "Name";
        public static final String COL_USERNAME = "Username";
        public static final String COL_ROLE = "Role";
        public static final String COL_AGENT_TYPE = "Agent Type";
        public static final String COL_STATUS = "Status";
        public static final String COL_ACTION = "Action";

        public static By getRowCell(int rowIndex, int columnIndex) {
                return By.xpath("//table//tbody/tr[" + rowIndex + "]/td[" + columnIndex + "]");
        }

        // Row action menu (three dots) and its Delete entry
        public static By getRowActionMenu(int rowIndex) {
                return By.xpath("//table//tbody/tr[" + rowIndex + "]//button[contains(@class, 'three-dots')]");
        }

        public static By getRowDeleteAction(int rowIndex) {
                return By.xpath("//table//tbody/tr[" + rowIndex + "]//a[contains(@class, 'dropdown-item') and normalize-space()='Delete']");
        }

        // Confirmation dialog button after Delete (if the app asks for confirmation)
        public static final By CONFIRM_DIALOG_BUTTON = By.xpath(
                        "//*[contains(@class, 'modal') or contains(@class, 'swal2') or @role='dialog']"
                                        + "//button[normalize-space()='Yes' or normalize-space()='Delete' or normalize-space()='Confirm'"
                                        + " or normalize-space()='OK' or normalize-space()='Ok' or starts-with(normalize-space(), 'Yes')]");

        // ==================== Add User Form ====================
        public static final By NAME_INPUT = By.name("agentName");
        public static final By COUNTRY_CODE_SELECT = By.name("countryCode");
        public static final By CONTACT_NUMBER_INPUT = By.name("agentMobile");
        public static final By EMAIL_INPUT = By.name("agentEmail");
        public static final By ROLE_SELECT = By.name("role");
        public static final By CALL_MODE_WEBRTC = By.id("callModeWebrtc");
        public static final By CALL_MODE_PHONE = By.id("callModePhone");
        public static final By STATUS_SELECT = By.name("status");

        // The Single and Bulk tabs each have Save/Cancel - only the visible one is used
        public static final By SAVE_BUTTONS = By.xpath("//button[normalize-space()='Save']");
        public static final By CANCEL_BUTTONS = By.xpath("//button[normalize-space()='Cancel']");

        // Validation message of a form field, relative to the field (e.g. "Mobile number must be 10 in length")
        public static final String FIELD_ERROR_RELATIVE = "./ancestor::div[contains(@class, 'col') or contains(@class, 'form-group') or contains(@class, 'mb-')][1]"
                        + "//span[contains(@class, 'invalid-feedback')]";

        // Notifications shown after Save / Delete
        public static final By NOTIFICATIONS = By.xpath(
                        "//*[contains(@class, 'toast-message') or contains(@class, 'toast-title') or contains(@class, 'swal2-title')"
                                        + " or contains(@class, 'swal2-html-container') or contains(@class, 'mat-mdc-snack-bar-label')"
                                        + " or (contains(@class, 'alert') and contains(@class, 'alert-'))]");
}
