package locators;

import org.openqa.selenium.By;

/**
 * Locators for the Reseller Dashboard's "My Wallets" tab and its Wallet Fund
 * Transfer flow.
 *
 * NOTE: this is a first pass seeded from the (unverified/unused) "Wallet Fund
 * Transfer Page" stubs in EnterpriseWalletPageLocators, then corrected against
 * the live Reseller UI. Locators here have been exercised against the real
 * app - see ResellerDashboardMyWalletsTest for confirmation of which ones.
 */
public class ResellerDashboardPageLocators {

        // ========== My Wallets Tab ==========
        public static final By MY_WALLETS_TAB = By.xpath(
                        "//a[contains(., 'My Wallets')] | //span[contains(., 'My Wallets')] | " +
                                        "//li[contains(., 'My Wallets')] | //div[contains(@class,'nav')][contains(., 'My Wallets')]");

        public static final By MY_WALLETS_HEADER = By.xpath(
                        "//h1[contains(., 'My Wallets')] | //h2[contains(., 'My Wallets')] | //h3[contains(., 'My Wallets')] | "
                                        +
                                        "//*[contains(@class, 'header') and contains(., 'My Wallets')] | //*[contains(@class,'page-title') and contains(., 'My Wallets')]");

        // ========== Wallet Listing Table ==========
        public static final By WALLET_TABLE_HEADERS = By.xpath(
                        "//table//thead//th | //div[contains(@class, 'table-header')]//div");
        public static final By WALLET_TABLE_ROWS = By.xpath(
                        "//table//tbody//tr | //div[contains(@class, 'table-row')]");
        public static final By NO_DATA_MESSAGE = By.xpath(
                        "//*[contains(text(), 'There is no data available') or contains(text(), 'No data available') or contains(text(), 'No Data Found')]");

        public static By getWalletCellByRowAndColumn(int row, int column) {
                return By.xpath("(//table//tbody//tr)[" + row + "]//td[" + column + "]");
        }

        public static By getWalletStatusByRow(int row) {
                return By.xpath("(//table//tbody//tr)[" + row
                                + "]//*[contains(text(), 'Active') or contains(text(), 'Inactive')]");
        }

        // ========== Pagination ==========
        public static final By PAGINATION_NEXT = By.xpath(
                        "//a[@aria-label='Next'] | //button[contains(@class,'next')] | //li[contains(@class,'next')]//a");
        public static final By PAGINATION_PREVIOUS = By.xpath(
                        "//a[@aria-label='Previous'] | //button[contains(@class,'prev')] | //li[contains(@class,'previous')]//a");

        // ========== Search ==========
        public static final By SEARCH_INPUT = By.xpath(
                        "//input[contains(@placeholder, 'Search') or contains(@placeholder, 'search')]");

        // ========== Filters ==========
        public static final By FILTER_BUTTON = By.xpath(
                        "//button[contains(., 'Filter')] | //*[@mattooltip='Filters' or @mattooltip='Filter']");
        public static final By STATUS_FILTER_ACTIVE = By.xpath(
                        "//label[normalize-space()='Active'] | //input[@value='active' or @value='Active']");
        public static final By WALLET_TYPE_FILTER_PREPAID = By.xpath(
                        "//label[normalize-space()='Prepaid'] | //input[@value='prepaid' or @value='Prepaid']");
        public static final By APPLY_FILTER_BUTTON = By.xpath("//button[contains(text(), 'Apply')]");
        public static final By CLEAR_ALL_FILTERS_BUTTON = By.xpath(
                        "//button[contains(., 'Clear All') or contains(., 'Clear all')]");

        // ========== Service Navigation (per-wallet "Next"/manage button) ==========
        public static By getServiceNavButtonByRow(int row) {
                return By.xpath("(//table//tbody//tr)[" + row
                                + "]//button[contains(@mattooltip, 'Service') or contains(@mattooltip, 'Manage')] | " +
                                "(//table//tbody//tr)[" + row + "]//button[last()]");
        }

        // ========== Wallet Fund Transfer ==========
        // Confirmed live: trigger button says "Wallet Fund Transfer"; the modal's
        // own submit button says exactly "Fund Transfer" (no "Wallet" prefix),
        // which is what disambiguates the two from each other.
        public static final By FUND_TRANSFER_BUTTON = By.xpath(
                        "//button[contains(., 'Wallet Fund Transfer')] | " +
                                        "//a[contains(., 'Wallet Fund Transfer') and contains(@class, 'btn')]");
        public static final By FUND_TRANSFER_MODAL = By.xpath(
                        "//*[contains(@class, 'modal')][.//*[normalize-space()='Fund Transfer']] | " +
                                        "//mat-dialog-container[.//*[normalize-space()='Fund Transfer']]");
        // Labels are literally "Wallet From (Source Wallet)*" / "Wallet To
        // (Destination Wallet)*"; the control itself is a searchable combobox
        // (placeholder "Search or select wallet by name"), not a native
        // select/mat-select/ng-select tag.
        public static final By SOURCE_WALLET_DROPDOWN = By.xpath(
                        "//label[contains(., 'Wallet From') or contains(., 'Source Wallet')]/following::*[contains(@placeholder, 'Search or select wallet')][1]");
        public static final By DESTINATION_WALLET_DROPDOWN = By.xpath(
                        "//label[contains(., 'Wallet To') or contains(., 'Destination Wallet')]/following::*[contains(@placeholder, 'Search or select wallet')][1]");
        // Amount input's own placeholder is "ex: 1000", not the word "Amount" -
        // locate by proximity to its "Amount" label instead.
        public static final By TRANSFER_AMOUNT_INPUT = By.xpath(
                        "//label[contains(., 'Amount')]/following::input[1]");
        // Description is a <textarea> with placeholder "Enter message", not
        // "Description" - locate by proximity to its "Description" label.
        public static final By TRANSFER_DESCRIPTION_INPUT = By.xpath(
                        "//label[contains(., 'Description')]/following::textarea[1]");
        // Exact match "Fund Transfer" (not "contains") to avoid matching the
        // "Wallet Fund Transfer" trigger button that opened this modal.
        public static final By FUND_TRANSFER_SUBMIT_BUTTON = By.xpath(
                        "//button[normalize-space()='Fund Transfer']");
        public static final By TRANSFER_SUCCESS_MESSAGE = By.xpath(
                        "//*[contains(@class, 'success') or contains(@class, 'toast')][contains(., 'success') or contains(., 'Success') or contains(., 'successful')]");
}
