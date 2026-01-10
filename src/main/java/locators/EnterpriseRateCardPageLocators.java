package locators;

import org.openqa.selenium.By;

/**
 * Locators for the Enterprise Rate Card Tab page.
 * Used by EnterpriseRateCardPage for rate card operations.
 */
public class EnterpriseRateCardPageLocators {

    // ==================== Rate Card Navigation ====================

    // Rate Card sidebar menu item
    public static final By RATE_CARD_MENU_ITEM = By.xpath(
            "//span[text()='Rate Card'] | " +
                    "//a[contains(@href, 'rate-card')]//span | " +
                    "//div[contains(@class, 'menu')]//span[contains(text(), 'Rate Card')]");

    // ==================== Rate Card Page Header ====================

    // Rate Card page header/title
    public static final By RATE_CARD_PAGE_HEADER = By.xpath(
            "//h1[contains(text(), 'Rate Card')] | " +
                    "//h2[contains(text(), 'Rate Card')] | " +
                    "//*[contains(@class, 'header') and contains(text(), 'Rate Card')]");

    // ==================== Rate Card List Table ====================

    // Rate Card table container
    public static final By RATE_CARD_TABLE = By.xpath(
            "//section[contains(@class, 'mat-table-listing')] | " +
                    "//div[contains(@class, 'mat-table-listing')] | " +
                    "//table[contains(@class, 'mat-table')]");

    // Rate Card table headers
    public static final By RATE_CARD_TABLE_HEADERS = By.xpath(
            "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'header')]//div | " +
                    "//table//thead//th | " +
                    "//mat-header-row//mat-header-cell");

    // Rate Card table rows
    public static final By RATE_CARD_TABLE_ROWS = By.xpath(
            "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')] | " +
                    "//table//tbody//tr | " +
                    "//mat-row");

    // No data message
    public static final By NO_DATA_MESSAGE = By.xpath(
            "//*[contains(text(), 'No Data Available') or contains(text(), 'No data') or contains(text(), 'No records')] | "
                    +
                    "//div[contains(@class, 'empty-state') or contains(@class, 'no-data')]");

    // ==================== Serial Number Column ====================

    // Serial Number column cells
    public static final By SERIAL_NUMBER_CELLS = By.xpath(
            "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')]//div[1] | " +
                    "//table//tbody//tr//td[1] | " +
                    "//mat-row//mat-cell[1]");

    // Dynamic locator for serial number by row
    public static By getSerialNumberByRow(int row) {
        return By.xpath("(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])[" + row
                + "]//div[1] | " +
                "(//table//tbody//tr)[" + row + "]//td[1]");
    }

    // ==================== Date/Created Column ====================

    // Date column cells (for ordering validation)
    public static final By DATE_CELLS = By.xpath(
            "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')]//div[contains(@class, 'date')] | "
                    +
                    "//table//tbody//tr//td[contains(@class, 'date')]");

    // First row date (for latest card validation)
    public static final By FIRST_ROW_DATE = By.xpath(
            "(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])[1]//div[contains(@class, 'date')] | "
                    +
                    "(//table//tbody//tr)[1]//td[contains(@class, 'date')]");

    // ==================== Status Column ====================

    // Status column cells (Active/Inactive)
    public static final By STATUS_CELLS = By.xpath(
            "//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')]//div[contains(@class, 'status')] | "
                    +
                    "//span[contains(@class, 'badge')] | " +
                    "//table//tbody//tr//td[contains(@class, 'status')]");

    // Dynamic locator for status by row
    public static By getStatusByRow(int row) {
        return By.xpath("(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])[" + row
                + "]//div[contains(@class, 'status')] | " +
                "(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])[" + row
                + "]//span[contains(@class, 'badge')] | " +
                "(//table//tbody//tr)[" + row + "]//td[contains(@class, 'status')]");
    }

    // ==================== View Icon ====================

    // View icon buttons
    public static final By VIEW_ICON_BUTTONS = By.xpath(
            "//button[contains(@aria-label, 'View') or contains(@mattooltip, 'View')] | " +
                    "//mat-icon[contains(text(), 'visibility') or contains(text(), 'remove_red_eye')]/parent::button | "
                    +
                    "//i[contains(@class, 'fa-eye')]/parent::button | " +
                    "//button[contains(@class, 'view')]");

    // Dynamic locator for view icon by row
    public static By getViewIconByRow(int row) {
        return By.xpath("(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])[" + row
                + "]//button[contains(@aria-label, 'View') or contains(@mattooltip, 'View')] | " +
                "(//section[contains(@class, 'mat-table-listing')]//div[contains(@class, 'row')])[" + row
                + "]//mat-icon[contains(text(), 'visibility') or contains(text(), 'remove_red_eye')]/parent::button | "
                +
                "(//table//tbody//tr)[" + row + "]//button[contains(@class, 'view')]");
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

    // Pagination info
    public static final By PAGINATION_INFO = By.xpath(
            "//div[contains(@class, 'paginator-range')] | " +
                    "//span[contains(text(), 'of')] | " +
                    "//label[contains(text(), 'Page')]");

    // ==================== Rate Card Details Page ====================

    // Rate Card Details page header
    public static final By RATE_CARD_DETAILS_HEADER = By.xpath(
            "//h1[contains(text(), 'Rate Card Details')] | " +
                    "//h2[contains(text(), 'Rate Card Details')] | " +
                    "//*[contains(@class, 'header') and contains(text(), 'Details')]");

    // Services section on details page
    public static final By SERVICES_SECTION = By.xpath(
            "//div[contains(@class, 'services')] | " +
                    "//section[contains(@class, 'service')] | " +
                    "//*[contains(text(), 'Services')]/parent::*");

    // Service name elements on details page
    public static final By SERVICE_NAMES_ON_DETAILS = By.xpath(
            "//div[contains(@class, 'service-name')] | " +
                    "//span[contains(@class, 'service')] | " +
                    "//td[contains(@class, 'service')] | " +
                    "//*[contains(text(), 'SMS') or contains(text(), 'RCS') or contains(text(), 'WABA') or contains(text(), 'IVR') or contains(text(), 'OBD') or contains(text(), 'CCS')]");

    // Edit button (should NOT be present)
    public static final By EDIT_BUTTON = By.xpath(
            "//button[contains(text(), 'Edit') or contains(@aria-label, 'Edit')] | " +
                    "//mat-icon[contains(text(), 'edit')]/parent::button | " +
                    "//button[contains(@class, 'edit')]");

    // Back button
    public static final By BACK_BUTTON = By.xpath(
            "//button[contains(text(), 'Back') or contains(@aria-label, 'Back')] | " +
                    "//mat-icon[contains(text(), 'arrow_back')]/parent::button | " +
                    "//a[contains(@class, 'back')]");
}
