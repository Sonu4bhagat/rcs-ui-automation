package locators;

import org.openqa.selenium.By;

public class ServicesPageLocators {

        // Navigation
        // Assuming Services is a top-level item or under Open menu
        // Based on NavigationLocators, it might be: //span[contains(text(),
        // 'Services')]
        public static final By SERVICES_TAB = By
                        .xpath("//a[contains(@href, 'services')] | //span[normalize-space()='Services']");

        // Page Header
        public static final By PAGE_HEADER = By
                        .xpath("//h4[contains(text(), 'Services')] | //h2[contains(text(), 'Services')]");

        // Search - flexible locator to match various search input patterns
        public static final By SEARCH_INPUT = By
                        .xpath("//input[@placeholder='Search' or contains(@placeholder, 'search') or contains(@placeholder, 'Search')] | "
                                        +
                                        "//mat-form-field//input | " +
                                        "//input[@type='text' or @type='search'] | " +
                                        "//div[contains(@class, 'search')]//input");

        // Loaders/Spinners
        public static final By LOADING_SPINNER = By
                        .xpath("//div[contains(@class, 'spinner')] | //mat-spinner | //*[contains(text(), 'Loading')]");

        // Filters - flexible locators for various filter UI patterns
        public static final By FILTER_BUTTON = By
                        .xpath("//button[contains(., 'Filter') or .//mat-icon[text()='filter_list']]");
        // Active filter is a radio input with id="statusActive"
        public static final By STATUS_FILTER_ACTIVE = By
                        .xpath("//input[@id='statusActive'] | " +
                                        "//input[@formcontrolname='isAccountActive' and @value='true'] | " +
                                        "//label[normalize-space()='Active']/preceding-sibling::input[1]");
        public static final By APPLY_FILTER_BTN = By
                        .xpath("//button[contains(text(), 'Apply') or contains(text(), 'Search')] | " +
                                        "//button[contains(@class, 'apply') or contains(@class, 'submit')] | " +
                                        "//button[@type='submit'] | " +
                                        "//div[contains(@class, 'filter')]//button[last()] | " +
                                        "//mat-menu//button[contains(text(), 'Apply')] | " +
                                        "//div[contains(@class, 'cdk-overlay')]//button[contains(text(), 'Apply')]");

        // Table Elements
        public static final By TABLE_ROWS = By.xpath("//tbody//tr");

        // Dynamic Locator for Service Name in a specific row
        // Assuming Name is in the first column (td[1])
        public static By getServiceNameByRow(int rowIndex) {
                return By.xpath("(//tbody//tr)[" + rowIndex + "]//td[1]");
        }

        public static By getStatusBadgeByRow(int rowIndex) {
                // Only match elements containing "Active" or "Inactive" status text
                // Avoid matching service type badges like "Promotional" or "Transactional"
                return By.xpath("(//tbody//tr)[" + rowIndex
                                + "]//span[contains(text(), 'Active') or contains(text(), 'Inactive')] | " +
                                "(//tbody//tr)[" + rowIndex
                                + "]//*[@class='status' or contains(@class, 'status-badge')] | " +
                                "(//tbody//tr)[" + rowIndex + "]//td[contains(@class, 'status')] | " +
                                "(//tbody//tr)[" + rowIndex
                                + "]//td[contains(., 'Active') or contains(., 'Inactive')]");
        }

        // Action Icons per Row
        // View Icon (eye) - extremely flexible to match any action button
        public static By getViewIconByRow(int rowIndex) {
                // Try to match ANY button or link in the row's action column
                // This is the most flexible approach - match by position (last td) or any
                // button
                return By.xpath("(//tbody//tr)[" + rowIndex + "]//button[1] | (//tbody//tr)[" + rowIndex
                                + "]//a[1] | (//tbody//tr)[" + rowIndex + "]//td[last()]//button | (//tbody//tr)["
                                + rowIndex + "]//td[last()]//a");
        }

        // SSO Redirect Icon (external-link-alt or similar) - simplified
        public static By getSSOIconByRow(int rowIndex) {
                // Match buttons with SSO tooltip, open_in_new icon, or external link icon
                return By.xpath("(//tbody//tr)[" + rowIndex
                                + "]//button[contains(@mattooltip, 'SSO') or .//mat-icon[contains(text(), 'open_in_new')] or contains(@class, 'sso')]");
        }

        // Back Button (in Details View) - flexible to match various back button
        // patterns
        public static final By BACK_BUTTON = By
                        .xpath("//button[contains(text(), 'Back') or .//mat-icon[text()='arrow_back']] | " +
                                        "//a[contains(text(), 'Back')] | " +
                                        "//button[contains(@class, 'back')] | " +
                                        "//*[contains(@class, 'back-button')] | " +
                                        "//mat-icon[contains(text(), 'arrow_back')]/parent::button");

        // Details View Name verification - targets breadcrumb, headers, and API header
        // on details page
        public static final By DETAILS_SERVICE_NAME = By
                        .xpath("//ol[contains(@class, 'breadcrumb')]//li[contains(@class, 'active')] | " +
                                        "//nav//li[last()] | " +
                                        "//*[contains(text(), 'Service Account API Credential')] | " +
                                        "//*[contains(text(), 'User ID:')] | " +
                                        "//div[contains(@class, 'card-header')]//h4 | " +
                                        "//div[contains(@class, 'card-header')]//h5 | " +
                                        "//div[contains(@class, 'details')]//h4 | " +
                                        "//*[@class='title' or contains(@class, 'header-title')] | " +
                                        "//div[contains(@class, 'content')]//h4 | " +
                                        "//div[contains(@class, 'content')]//h5");

        // SSO Dashboard Service Name verification - targets service name on SSO
        // dashboard
        public static final By SSO_DASHBOARD_SERVICE_NAME = By
                        .xpath("//div[contains(@class, 'header')]//h1 | " +
                                        "//div[contains(@class, 'header')]//h2 | " +
                                        "//div[contains(@class, 'dashboard')]//h3 | " +
                                        "//*[contains(@class, 'service-name')] | " +
                                        "//*[contains(@class, 'account-name')] | " +
                                        "//ol[contains(@class, 'breadcrumb')]//li[contains(@class, 'active')] | " +
                                        "//nav//li[last()]");

        // Dynamic Locator for 'View Details' button on a specific Service Card (e.g.,
        // RCS Card, SMS Card) - Enhanced with multiple fallback patterns
        public static By getViewDetailsButtonForService(String serviceName) {
                // Multiple approaches to find the View Details button for a service card
                return By.xpath(
                                // Pattern 1: h6 with service name, ancestor card, button with "View Details"
                                "//h6[contains(text(), '" + serviceName
                                                + "')]/ancestor::div[contains(@class, 'card')]//button[contains(text(), 'View Details')] | "
                                                +
                                                // Pattern 2: Any element with service name, ancestor card, any button
                                                "//*[contains(text(), '" + serviceName
                                                + "')]/ancestor::div[contains(@class, 'card')]//button[contains(text(), 'View')] | "
                                                +
                                                // Pattern 3: Card with service name in title/header
                                                "//div[contains(@class, 'card')][.//*[contains(text(), '" + serviceName
                                                + "')]]//button[contains(text(), 'View')] | " +
                                                // Pattern 4: Card with service name, any button
                                                "//div[contains(@class, 'card') and .//*[contains(text(), '"
                                                + serviceName + "')]]//button | " +
                                                // Pattern 5: Service card by class or ID containing service name
                                                // (case-insensitive)
                                                "//div[contains(@class, '" + serviceName.toLowerCase()
                                                + "') or contains(@id, '" + serviceName.toLowerCase()
                                                + "')]//button[contains(text(), 'View')] | " +
                                                // Pattern 6: Link with View Details text after service name
                                                "//*[contains(text(), '" + serviceName
                                                + "')]/following::button[contains(text(), 'View')][1] | " +
                                                // Pattern 7: Span or strong with service name followed by View Details
                                                "//span[contains(text(), '" + serviceName
                                                + "')]/ancestor::*[contains(@class, 'card')]//button | " +
                                                "//strong[contains(text(), '" + serviceName
                                                + "')]/ancestor::*[contains(@class, 'card')]//button");
        }
}
