package locators.ccs;

import org.openqa.selenium.By;

/**
 * Locators for CCS-Live Call Flow: Queue list and the Add / Edit Queue form.
 * The Queue list uses the same table and search controls as the Users list (see CCSUsersPageLocators).
 */
public class CCSQueuePageLocators {

        // ==================== Navigation ====================
        public static final By CALL_FLOW_MENU = By.xpath("//li//span[normalize-space()='Call Flow']");
        public static final By QUEUE_MENU = By.xpath(
                        "//li[.//span[normalize-space()='Call Flow']]//ul//*[normalize-space()='Queue']");

        // ==================== Queue List ====================
        public static final By ADD_QUEUE_BUTTON = By.xpath("//button[normalize-space()='Add Queue']");

        // Option value of the search type dropdown (name="searchType") on the Queue list
        public static final String SEARCH_BY_QUEUE_NAME = "queue_name";

        // Queue list columns (header text as shown in the app)
        public static final String COL_NAME = "Name";
        public static final String COL_DESCRIPTION = "Description";
        public static final String COL_ASSIGNED_AGENTS = "Assigned Agents";
        public static final String COL_ALGORITHM = "Algorithm";
        public static final String COL_CALL_WAITING_TYPE = "Call Waiting Type";
        public static final String COL_ACTION = "Action";

        public static By getRowEditButton(int rowIndex) {
                return By.xpath("//table//tbody/tr[" + rowIndex + "]//button[.//i[@title='Edit']]");
        }

        // ==================== Add / Edit Queue Form ====================
        public static final By NAME_INPUT = By.name("name");
        public static final By NAME_COUNTER = By.xpath("//input[@name='name']/following-sibling::div//small");
        public static final By DESCRIPTION_INPUT = By.name("description");
        public static final By ALGORITHM_SELECT = By.name("algorithm");
        public static final By CALL_WAITING_TYPE_SELECT = By.name("attempt_type");
        // Shown when Call Waiting Type is "Queue"
        public static final By QUEUE_ATTEMPT_SELECT = By.name("retry");
        public static final By REDIRECT_TO_SELECT = By.name("redirect_to");

        // Agents multi-select (ng-multiselect-dropdown): each option is a checkbox labelled with the agent name
        public static final By AGENTS_DROPDOWN_BUTTON = By.xpath(
                        "//ng-multiselect-dropdown[@name='selectedAgents']//span[contains(@class, 'dropdown-btn')]");
        public static final By AGENT_OPTIONS = By.xpath(
                        "//ng-multiselect-dropdown[@name='selectedAgents']//ul[contains(@class, 'item2')]/li");

        public static final By SAVE_BUTTONS = By.xpath("//button[normalize-space()='Save']");
}
