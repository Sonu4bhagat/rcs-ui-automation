package locators;

import org.openqa.selenium.By;

/**
 * Locators for API & Documentation Page
 * Contains all XPath and CSS selectors for Super Admin API & Documentation
 * functionality
 */
public class APIAndDocumentationPageLocators {

        // Navigation and Page Load
        public static final By API_DOC_MENU = By.xpath(
                        "//span[normalize-space()='API & Documentation']");

        public static final By PAGE_TITLE = By.xpath(
                        "//h5[contains(text(), 'API Documentation')]");

        // Service Cards/Sections - Generic locators for all services
        public static final By SERVICE_CARDS_CONTAINER = By.xpath(
                        "//div[contains(@class, 'card') or contains(@class, 'service')]");

        // Service-specific cards
        public static final By SMS_SERVICE_CARD = By.xpath(
                        "//h6[contains(text(), 'SMS')]/ancestor::div[contains(@class, 'card')]");

        public static final By OBD_SERVICE_CARD = By.xpath(
                        "//h6[contains(text(), 'OBD')]/ancestor::div[contains(@class, 'card')]");

        public static final By CCS_SERVICE_CARD = By.xpath(
                        "//h6[contains(text(), 'CCS')]/ancestor::div[contains(@class, 'card')]");

        public static final By WABA_SERVICE_CARD = By.xpath(
                        "//h6[contains(text(), 'WABA') or contains(text(), 'WhatsApp')]/ancestor::div[contains(@class, 'card')]");

        // Dynamic locator method for View Details button by service name
        public static By getViewDetailsButton(String serviceName) {
                return By.xpath(
                                "//h6[contains(text(), '" + serviceName
                                                + "')]/ancestor::div[contains(@class, 'card')]//button[contains(text(), 'View Details')]");
        }

        // API Documentation Download Button - Generic
        public static final By API_DOC_DOWNLOAD_BUTTON = By.xpath(
                        "//button[contains(text(), 'Download') or contains(@aria-label, 'Download') or .//mat-icon[contains(text(), 'download')]]");

        // Dynamic locator for Download button by service
        public static By getDownloadButton(String serviceName) {
                return By.xpath(
                                "//h6[contains(text(), '" + serviceName
                                                + "')]/ancestor::div[contains(@class, 'card') or contains(@class, 'modal') or contains(@class, 'dialog')]//button[contains(text(), 'Download') or .//mat-icon[contains(text(), 'download')]]");
        }

        // Explore Swagger UI Button - Generic
        public static final By EXPLORE_SWAGGER_BUTTON = By.xpath(
                        "//button[contains(text(), 'Explore Swagger') or contains(text(), 'Swagger UI') or contains(@aria-label, 'Swagger')]");

        // Dynamic locator for Swagger button by service
        public static By getSwaggerButton(String serviceName) {
                return By.xpath(
                                "//h6[contains(text(), '" + serviceName
                                                + "')]/ancestor::div[contains(@class, 'card') or contains(@class, 'modal') or contains(@class, 'dialog')]//button[contains(text(), 'Swagger') or contains(text(), 'Explore')]");
        }

        // Modal/Dialog elements (if View Details opens a modal)
        public static final By MODAL_DIALOG = By.xpath(
                        "//div[contains(@class, 'modal') or contains(@class, 'dialog') or contains(@role, 'dialog')]");

        public static final By MODAL_CLOSE_BUTTON = By.xpath(
                        "//button[contains(@class, 'close') or contains(@aria-label, 'Close') or .//mat-icon[contains(text(), 'close')]]");

        // Back/Return to list button (from detail view)
        public static final By BACK_TO_LIST_BUTTON = By.xpath(
                        "//button[contains(text(), 'Back') or .//mat-icon[contains(text(), 'arrow_back')] or contains(@aria-label, 'Back')]");

        // Documentation content area (if displayed on page)
        public static final By DOCUMENTATION_CONTENT = By.xpath(
                        "//div[contains(@class, 'documentation') or contains(@class, 'api-docs')]");

        // Service-specific documentation sections
        public static By getServiceDocSection(String serviceName) {
                return By.xpath(
                                "//h6[contains(text(), '" + serviceName
                                                + "')]/following-sibling::div[contains(@class, 'documentation') or contains(@class, 'content')]");
        }

        // URL/Domain display elements (if shown in UI)
        public static final By API_URL_DISPLAY = By.xpath(
                        "//span[contains(@class, 'url') or contains(@class, 'endpoint')] | //code[contains(@class, 'url')]");

        public static final By SWAGGER_URL_DISPLAY = By.xpath(
                        "//a[contains(@href, 'swagger') or contains(text(), 'swagger')] | //span[contains(text(), 'swagger')]");

        // Swagger UI page elements (when opened in new tab)
        public static final By SWAGGER_UI_TITLE = By.xpath(
                        "//title[contains(text(), 'Swagger')] | //h2[contains(text(), 'Swagger')] | //div[contains(@class, 'swagger-ui')]");

        public static final By SWAGGER_UI_HEADER = By.xpath(
                        "//div[contains(@class, 'swagger-ui')] | //div[@id='swagger-ui']");

        // Service title in Swagger UI
        public static By getSwaggerServiceTitle(String serviceName) {
                return By.xpath(
                                "//h2[contains(text(), '" + serviceName + "')] | //title[contains(text(), '"
                                                + serviceName + "')]");
        }

        // Loading indicators
        public static final By LOADING_SPINNER = By.xpath(
                        "//mat-spinner | //div[contains(@class, 'spinner')] | //div[contains(@class, 'loading')]");
}
