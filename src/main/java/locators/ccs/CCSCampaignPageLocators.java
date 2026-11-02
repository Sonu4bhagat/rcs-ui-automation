package locators.ccs;

import org.openqa.selenium.By;

/**
 * Locators for CCS-Live Campaign: Campaign list, Add Campaign form and the Schedule Campaign (Calendar) dialog.
 * The Campaign list uses the same table and search controls as the Users list (see CCSUsersPageLocators).
 */
public class CCSCampaignPageLocators {

        // ==================== Navigation / Campaign List ====================
        public static final By CAMPAIGN_MENU = By.xpath("//li//span[normalize-space()='Campaign']");
        public static final By ADD_CAMPAIGN_BUTTON = By.xpath("//button[normalize-space()='Add Campaign']");

        // Option value of the search type dropdown (name="searchType") on the Campaign list
        public static final String SEARCH_BY_CAMPAIGN_NAME = "campaign_name";

        // Campaign list columns (header text as shown in the app)
        public static final String COL_CAMPAIGN_NAME = "Campaign Name";
        public static final String COL_CAMPAIGN_TYPE = "Campaign Type";
        public static final String COL_STATUS = "Status";
        public static final String COL_CREATED = "Created Date Time";
        public static final String COL_ACTION = "Action";

        public static By getRowDeleteButton(int rowIndex) {
                return By.xpath("//table//tbody/tr[" + rowIndex + "]//button[.//i[@title='Delete']]");
        }

        public static By getRowCalendarButton(int rowIndex) {
                return By.xpath("//table//tbody/tr[" + rowIndex + "]//button[.//i[@title='Calendar']]");
        }

        // ==================== Add Campaign Form ====================
        public static final By NAME_INPUT = By.name("campaign_name");
        public static final By NAME_COUNTER = By.xpath("//input[@name='campaign_name']/following-sibling::div//small");
        public static final By DESCRIPTION_INPUT = By.name("campaign_description");
        public static final By CAMPAIGN_TYPE_SELECT = By.name("campaign_type");
        // Mandatory "Select Queue" - shown for Click to Call - Manual campaigns
        public static final By QUEUE_SELECT = By.name("queue");

        // Virtual Number multi-select (ng-multiselect-dropdown): each option is a checkbox labelled with the number
        public static final By VIRTUAL_NUMBER_DROPDOWN_BUTTON = By.xpath(
                        "//ng-multiselect-dropdown[@name='virtual_number']//span[contains(@class, 'dropdown-btn')]");
        public static final By VIRTUAL_NUMBER_OPTIONS = By.xpath(
                        "//ng-multiselect-dropdown[@name='virtual_number']//ul[contains(@class, 'item2')]/li");

        public static final By SAVE_BUTTONS = By.xpath("//button[normalize-space()='Save']");

        // Close (x) button of the ngx-toastr notifications
        public static final By TOAST_CLOSE_BUTTONS = By.xpath("//div[@id='toast-container']//button[contains(@class, 'toast-close-button')]");

        // ==================== Schedule Campaign (Calendar) dialog ====================
        public static final By SCHEDULE_DIALOG = By.xpath("//schedule-campaign");
        public static final By SCHEDULE_DATE_RANGE_INPUT = By.xpath("//schedule-campaign//input[@name='date']");
        public static final By SCHEDULE_SAVE_BUTTON = By.xpath("//schedule-campaign//button[normalize-space()='Save']");

        // ngx-bootstrap date range picker
        public static final By DATE_PICKER = By.xpath("//bs-daterangepicker-container");
        public static final By DATE_PICKER_MONTH = By.xpath("//bs-daterangepicker-container//bs-datepicker-navigation-view/button[contains(@class, 'current')][1]");
        public static final By DATE_PICKER_YEAR = By.xpath("//bs-daterangepicker-container//bs-datepicker-navigation-view/button[contains(@class, 'current')][2]");
        public static final By DATE_PICKER_NEXT = By.xpath("//bs-daterangepicker-container//button[contains(@class, 'next')]");
        public static final By DATE_PICKER_PREVIOUS = By.xpath("//bs-daterangepicker-container//button[contains(@class, 'previous')]");

        public static By getDatePickerDay(int day) {
                return By.xpath("//bs-daterangepicker-container//td[@role='gridcell']/span[normalize-space()='" + day
                                + "' and not(contains(@class, 'is-other-month')) and not(contains(@class, 'disabled'))]");
        }

        // Weekday rows: week_id_0 = Mon ... week_id_6 = Sun; each row has a Start Time and an End Time ng-select
        public static By getWeekdayToggle(int weekdayIndex) {
                return By.id("week_id_" + weekdayIndex);
        }

        public static By getWeekdayTimeSelects(int weekdayIndex) {
                return By.xpath("//input[@id='week_id_" + weekdayIndex + "']/ancestor::div[contains(@class, 'row')][1]//ng-select");
        }

        public static final By NG_SELECT_OPTIONS = By.xpath("//ng-dropdown-panel//div[contains(@class, 'ng-option')]");
}
