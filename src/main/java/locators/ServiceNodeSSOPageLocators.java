package locators;

import org.openqa.selenium.By;

/**
 * Locators for Service Node SSO Page
 * This class contains all element locators for Super Admin's Service Node SSO
 * functionality
 */
public class ServiceNodeSSOPageLocators {

    // ========== Navigation ==========
    // Main menu navigation to Service Node SSO
    public static final By SERVICE_NODE_SSO_MENU = By.xpath(
            "//span[contains(text(), 'Service Nodes SSO')]");

    // Fallback locators if needed
    public static final By SERVICE_NODE_SSO_MENU_ALTERNATIVE = By.xpath(
            "//li[.//span[contains(text(), 'Service Nodes SSO')]] | " +
                    "//a[contains(@href, 'service-nodes-sso')] | " +
                    "//span[contains(text(), 'SSO')]");

    // Page header to confirm we're on the right page
    public static final By PAGE_HEADER = By.xpath(
            "//h1[contains(text(), 'Service Node SSO')] | " +
                    "//h2[contains(text(), 'Service Node SSO')] | " +
                    "//h1[contains(text(), 'SSO')] | " +
                    "//*[contains(@class, 'page-title') and contains(text(), 'SSO')]");

    // ========== Service List/Table Elements ==========
    // Service cards container (cards layout, not table)
    public static final By SERVICES_CONTAINER = By.xpath(
            "//div[contains(@class, 'service-cards')]");

    // All service cards
    public static final By SERVICE_CARDS = By.xpath(
            "//div[contains(@class, 'service-cards')]");

    // Service rows (kept for compatibility, points to cards)
    public static final By SERVICE_ROWS = By.xpath(
            "//div[contains(@class, 'service-cards')]");

    // Service name elements (h6 tags within cards)
    public static final By SERVICE_NAME_CELLS = By.xpath(
            "//div[contains(@class, 'service-cards')]//h6");

    // Service status/icon (if needed)
    public static final By SERVICE_STATUS_CELLS = By.xpath(
            "//div[contains(@class, 'service-cards')]//div[contains(@class, 'service_icon')]");

    // ========== Login Action Buttons ==========
    // All login buttons across all service cards
    public static final By SERVICE_LOGIN_BUTTON = By.xpath(
            "//div[contains(@class, 'service-cards')]//button[contains(text(), 'Login')]");

    // Login button for a specific service by service name
    public static By getLoginButtonForService(String serviceName) {
        return By.xpath(
                "//div[contains(@class, 'service-cards')][.//h6[contains(text(), '" + serviceName + "')]]" +
                        "//button[contains(text(), 'Login')]");
    }

    // Alternative: Get the entire card for a specific service
    public static By getServiceCard(String serviceName) {
        return By.xpath(
                "//div[contains(@class, 'service-cards')][.//h6[contains(text(), '" + serviceName + "')]]");
    }

    // ========== Role Selection Modal/Dropdown ==========
    // Role menu container (Angular Material menu that appears after clicking Login)
    public static final By ROLE_SELECTION_MODAL = By.xpath(
            "//div[@role='menu']");

    // Modal title (if exists)
    public static final By ROLE_MODAL_TITLE = By.xpath(
            "//h1[contains(@class, 'mat-dialog-title')] | " +
                    "//h2[contains(text(), 'Select Role')] | " +
                    "//div[@role='menu']//span[contains(@class, 'title')]");

    // Role dropdown/selector (not applicable for menu-based selection)
    public static final By ROLE_DROPDOWN = By.xpath(
            "//mat-select[contains(@placeholder, 'Role')] | " +
                    "//select[contains(@name, 'role')]");

    // Role options in the menu (menuitem buttons)
    public static final By ROLE_OPTIONS = By.xpath(
            "//button[@role='menuitem']");

    // Specific role option by role name
    public static By getRoleOption(String roleName) {
        return By.xpath(
                "//button[@role='menuitem'][.//span[contains(text(), '" + roleName + "')]]");
    }

    // List of available roles (menu items)
    public static final By AVAILABLE_ROLES_LIST = By.xpath(
            "//button[@role='menuitem']");

    // Role button (for menu-based selection)
    public static By getRoleRadioButton(String roleName) {
        return By.xpath(
                "//button[@role='menuitem'][.//span[contains(text(), '" + roleName + "')]]");
    }

    // ========== Modal Action Buttons ==========
    // Submit/Confirm button in role selection modal
    public static final By ROLE_MODAL_SUBMIT_BUTTON = By.xpath(
            "//button[contains(text(), 'Submit') or contains(text(), 'Continue') or contains(text(), 'Login')] | " +
                    "//button[@type='submit']");

    // Cancel button in role selection modal
    public static final By ROLE_MODAL_CANCEL_BUTTON = By.xpath(
            "//button[contains(text(), 'Cancel') or contains(text(), 'Close')] | " +
                    "//button[contains(@class, 'btn-cancel')]");

    // ========== Search and Filter ==========
    // Search field for services
    public static final By SEARCH_FIELD = By.xpath(
            "//input[@type='search' or contains(@placeholder, 'Search')] | " +
                    "//input[contains(@placeholder, 'service')]");

    // Filter dropdown
    public static final By FILTER_DROPDOWN = By.xpath(
            "//button[contains(., 'Filter')] | " +
                    "//mat-select[contains(@placeholder, 'Filter')]");

    // ========== Validation Elements ==========
    // Success message after SSO login
    public static final By SUCCESS_MESSAGE = By.xpath(
            "//*[contains(@class, 'success') and contains(., 'Success')] | " +
                    "//*[contains(@class, 'alert-success')] | " +
                    "//div[contains(@class, 'toast-success')]");

    // Error message
    public static final By ERROR_MESSAGE = By.xpath(
            "//*[contains(@class, 'error')] | " +
                    "//*[contains(@class, 'alert-danger')] | " +
                    "//div[contains(@class, 'toast-error')]");

    // Loading spinner
    public static final By LOADING_SPINNER = By.xpath(
            "//mat-spinner | " +
                    "//div[contains(@class, 'spinner')] | " +
                    "//mat-progress-spinner");

    // ========== Redirected Service Page Validation ==========
    // Generic service dashboard/home indicator
    public static final By SERVICE_DASHBOARD = By.xpath(
            "//h1[contains(@class, 'dashboard')] | " +
                    "//*[contains(@class, 'dashboard-header')] | " +
                    "//div[contains(@class, 'service-home')]");

    // Service-specific validation by checking unique elements
    public static By getServiceDashboardHeader(String serviceName) {
        return By.xpath(
                "//h1[contains(text(), '" + serviceName + "')] | " +
                        "//h2[contains(text(), '" + serviceName + "')] | " +
                        "//*[contains(@class, 'page-title') and contains(text(), '" + serviceName + "')]");
    }

    // ========== Table Headers ==========
    public static final By TABLE_HEADERS = By.xpath("//table//th");

    // ========== No Data Message ==========
    public static final By NO_SERVICES_MESSAGE = By.xpath(
            "//*[contains(text(), 'No services found')] | " +
                    "//*[contains(text(), 'No data found')] | " +
                    "//div[contains(@class, 'no-data')]");
}
