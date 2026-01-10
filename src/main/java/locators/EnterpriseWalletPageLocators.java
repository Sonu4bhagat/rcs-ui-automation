package locators;

import org.openqa.selenium.By;

/**
 * Locators for the Enterprise Wallet Tab page.
 * Used by EnterpriseWalletPage for wallet transaction operations.
 */
public class EnterpriseWalletPageLocators {

        // ==================== Wallet Navigation ====================

        // Wallet sidebar menu item
        public static final By WALLET_MENU_ITEM = By.xpath(
                        "//span[text()='Wallet'] | " +
                                        "//a[contains(@href, 'wallet')]//span");

        // ==================== Wallet Page Header ====================

        // Wallet page header/title - "Wallet Usage"
        public static final By WALLET_PAGE_HEADER = By.xpath(
                        "//h1[contains(text(), 'Wallet Usage')] | " +
                                        "//h2[contains(text(), 'Wallet Usage')] | " +
                                        "//*[contains(@class, 'header') and contains(text(), 'Wallet')]");

        // Available Wallet Balance
        public static final By WALLET_BALANCE = By.xpath(
                        "//label[contains(text(), 'Available Wallet Balance')]/following-sibling::* | " +
                                        "//*[contains(text(), 'Available Wallet Balance')]");

        // ==================== Transaction List Table ====================

        // Transaction table container - mat-table-listing section
        public static final By TRANSACTION_TABLE = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')] | " +
                                        "//div[contains(@class, 'mat-table-listing')] | " +
                                        "//table[contains(@class, 'mat-table')]");

        // Transaction table headers
        public static final By TRANSACTION_TABLE_HEADERS = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'header')]//div | " +
                                        "//table//thead//th | " +
                                        "//mat-header-row//mat-header-cell");

        // Transaction table rows
        public static final By TRANSACTION_TABLE_ROWS = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')] | " +
                                        "//table//tbody//tr | " +
                                        "//mat-row");

        // No data message
        public static final By NO_DATA_MESSAGE = By.xpath(
                        "//*[contains(text(), 'No Data Available') or contains(text(), 'No data') or contains(text(), 'No records')] | "
                                        +
                                        "//div[contains(@class, 'empty-state') or contains(@class, 'no-data')]");

        // ==================== Transaction Type Filter ====================

        // Filter dropdown - "Select Transaction Type"
        public static final By TRANSACTION_FILTER_DROPDOWN = By.xpath(
                        "//input[@role='combobox'] | " +
                                        "//mat-select[contains(@placeholder, 'Transaction') or contains(@formcontrolname, 'type')] | "
                                        +
                                        "//*[contains(text(), 'Select Transaction Type')]/ancestor::mat-form-field");

        // Filter options - using ng-select dropdown options
        public static final By FILTER_OPTION_TRANSACTION = By.xpath(
                        "//ng-dropdown-panel//span[contains(text(), 'Transaction')] | " +
                                        "//mat-option[contains(., 'Transaction')]");

        public static final By FILTER_OPTION_RECHARGE = By.xpath(
                        "//ng-dropdown-panel//span[contains(text(), 'Recharge')] | " +
                                        "//mat-option[contains(., 'Recharge')]");

        public static final By FILTER_OPTION_DEDUCTION = By.xpath(
                        "//ng-dropdown-panel//span[contains(text(), 'Deduction')] | " +
                                        "//mat-option[contains(., 'Deduction')]");

        // Clear/Reset filter button
        public static final By CLEAR_FILTER_BUTTON = By.xpath(
                        "//button[contains(., 'Clear') or contains(., 'Reset')] | " +
                                        "//span[contains(@class, 'ng-clear-wrapper')] | " +
                                        "//*[contains(@class, 'clear')]");

        // Dynamic locator for filter option by type
        public static By getFilterOptionByType(String type) {
                return By.xpath("//ng-dropdown-panel//span[contains(text(), '" + type + "')] | " +
                                "//mat-option[contains(., '" + type + "')] | " +
                                "//div[contains(@class, 'option') and contains(text(), '" + type + "')]");
        }

        // ==================== Pagination ====================

        // Next page button
        public static final By PAGINATION_NEXT = By.xpath(
                        "//button[contains(@aria-label, 'Next') or contains(@class, 'next')] | " +
                                        "//mat-icon[normalize-space()='navigate_next']/parent::button | " +
                                        "//a[contains(@class, 'next')]");

        // Previous page button
        public static final By PAGINATION_PREVIOUS = By.xpath(
                        "//button[contains(@aria-label, 'Previous') or contains(@class, 'previous')] | " +
                                        "//mat-icon[normalize-space()='navigate_before']/parent::button | " +
                                        "//a[contains(@class, 'previous')]");

        // Pagination info (e.g., "Last 100")
        public static final By PAGINATION_INFO = By.xpath(
                        "//label[contains(text(), 'Last')] | " +
                                        "//div[contains(@class, 'paginator-range')] | " +
                                        "//span[contains(text(), 'of')]");

        // ==================== Total Amounts ====================

        // Total Credit amount display
        public static final By TOTAL_CREDIT_AMOUNT = By.xpath(
                        "//*[contains(text(), 'Total Credit')]/following-sibling::* | " +
                                        "//*[contains(text(), 'Total Credit')]/parent::*//*[contains(@class, 'amount')] | "
                                        +
                                        "//label[contains(text(), 'Total Credit')]/following-sibling::*");

        // Total Debit amount display
        public static final By TOTAL_DEBIT_AMOUNT = By.xpath(
                        "//*[contains(text(), 'Total Debit')]/following-sibling::* | " +
                                        "//*[contains(text(), 'Total Debit')]/parent::*//*[contains(@class, 'amount')] | "
                                        +
                                        "//label[contains(text(), 'Total Debit')]/following-sibling::*");

        // Credit amount cells in table (Status column with Credit indicator)
        public static final By CREDIT_AMOUNT_CELLS = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')]//div[contains(@class, 'amount') or contains(@class, 'credit')] | "
                                        +
                                        "//td[contains(@class, 'credit')]");

        // Debit amount cells in table (Status column with Debit indicator)
        public static final By DEBIT_AMOUNT_CELLS = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')]//div[contains(@class, 'amount') or contains(@class, 'debit')] | "
                                        +
                                        "//td[contains(@class, 'debit')]");

        // Status column cells (Credit/Debit indicator)
        public static final By STATUS_CELLS = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')]//div[contains(@class, 'status')] | "
                                        +
                                        "//div[contains(text(), 'Credit') or contains(text(), 'Debit')]");

        // Amount (INR) column cells
        public static final By AMOUNT_CELLS = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')]//div[last()] | "
                                        +
                                        "//td[contains(@class, 'amount')]");

        // ==================== Archive Report ====================

        // Archive Reports button (primary blue button)
        public static final By ARCHIVE_REPORT_BUTTON = By.xpath(
                        "//button[contains(text(), 'Archive Reports') or contains(text(), 'Archive Report')] | " +
                                        "//button[contains(@class, 'btn-primary') and contains(., 'Archive')]");

        // Archive Report list table
        public static final By ARCHIVE_REPORT_TABLE = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')] | " +
                                        "//table[contains(@class, 'archive') or contains(@class, 'report')]");

        // Report status column (Active/Expired)
        public static final By REPORT_STATUS_CELLS = By.xpath(
                        "//div[contains(@class, 'status')] | " +
                                        "//td[contains(@class, 'status')] | " +
                                        "//span[contains(@class, 'badge')]");

        // Download button in report row
        public static final By DOWNLOAD_BUTTON = By.xpath(
                        "//button[contains(@aria-label, 'Download') or contains(@class, 'download')] | " +
                                        "//mat-icon[contains(text(), 'download') or contains(text(), 'file_download')]/parent::button | "
                                        +
                                        "//a[contains(@class, 'download') or contains(@href, 'download')]");

        // Dynamic locator for download button by row
        public static By getDownloadButtonByRow(int row) {
                return By.xpath("(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])["
                                + row
                                + "]//button[contains(@class, 'download')] | " +
                                "(//table//tbody//tr)[" + row + "]//button[contains(@class, 'download')]");
        }

        // Dynamic locator for report status by row
        public static By getReportStatusByRow(int row) {
                return By.xpath("(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])["
                                + row
                                + "]//div[contains(@class, 'status')] | " +
                                "(//table//tbody//tr)[" + row + "]//td[contains(@class, 'status')]");
        }

        // ==================== Generate Report ====================

        // Generate Report button
        public static final By GENERATE_REPORT_BUTTON = By.xpath(
                        "//button[contains(text(), 'Generate Report') or contains(text(), 'Generate')] | " +
                                        "//button[contains(@class, 'btn') and contains(., 'Generate')]");

        // Generate Report popup/modal (supports both Material Dialog and ng-bootstrap
        // modal)
        public static final By GENERATE_REPORT_POPUP = By.xpath(
                        "//ngb-modal-window[contains(@class, 'modal')] | " +
                                        "//mat-dialog-container | " +
                                        "//div[contains(@class, 'modal') and contains(@class, 'show')] | " +
                                        "//div[contains(@class, 'popup') or contains(@class, 'dialog') or contains(@class, 'cdk-overlay')]");

        // Popup title
        public static final By POPUP_TITLE = By.xpath(
                        "//mat-dialog-container//h1 | " +
                                        "//mat-dialog-container//h2 | " +
                                        "//div[contains(@class, 'modal-header')]//h5");

        // Popup date input fields (supports ng-bootstrap modal context)
        public static final By POPUP_START_DATE = By.xpath(
                        "(//input[@placeholder='Pick Date & Time'])[1] "
                                        +
                                        "//ngb-modal-window//input[contains(@placeholder, 'From')] | " +
                                        "//ngb-modal-window//input[@type='date'][1] | " +
                                        "//mat-dialog-container//input[contains(@placeholder, 'Start')] | " +
                                        "//input[contains(@placeholder, 'Start') or contains(@formcontrolname, 'start')]");

        public static final By POPUP_END_DATE = By.xpath(
                        "(//input[@placeholder='Pick Date & Time'])[2] "
                                        +
                                        "//ngb-modal-window//input[contains(@placeholder, 'To')] | " +
                                        "//ngb-modal-window//input[@type='date'][2] | " +
                                        "//mat-dialog-container//input[contains(@placeholder, 'End')] | " +
                                        "//input[contains(@placeholder, 'End') or contains(@formcontrolname, 'end')]");

        // Popup text/value input fields
        public static final By POPUP_INPUT_FIELDS = By.xpath(
                        "//mat-dialog-container//input | " +
                                        "//div[contains(@class, 'modal-body')]//input");

        // Popup Cancel button (supports ng-bootstrap modal)
        public static final By POPUP_CANCEL_BUTTON = By.xpath(
                        "//ngb-modal-window//button[contains(., 'Cancel') or contains(., 'Close')] | " +
                                        "//ngb-modal-window//button[contains(@class, 'btn-secondary')] | " +
                                        "//ngb-modal-window//button[@aria-label='Close'] | " +
                                        "//ngb-modal-window//button[contains(@class, 'close')] | " +
                                        "//mat-dialog-container//button[contains(., 'Cancel') or contains(., 'Close')] | "
                                        +
                                        "//div[contains(@class, 'modal')]//button[contains(., 'Cancel')]");

        // Popup Submit/Generate button
        public static final By POPUP_SUBMIT_BUTTON = By.xpath(
                        "//mat-dialog-container//button[contains(., 'Generate') or contains(., 'Submit')] | " +
                                        "//div[contains(@class, 'modal')]//button[contains(., 'Generate')]");

        // ==================== Transaction Type Column ====================

        // Transaction type column in table (Status showing Credit/Debit)
        public static final By TRANSACTION_TYPE_CELLS = By.xpath(
                        "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')]//div[contains(text(), 'Credit') or contains(text(), 'Debit')] | "
                                        +
                                        "//div[contains(@class, 'status')]//span[contains(text(), 'Credit') or contains(text(), 'Debit')]");
}
