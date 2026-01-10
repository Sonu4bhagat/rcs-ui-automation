package locators;

import org.openqa.selenium.By;

public class LoginPageLocators {
        public static final By EMAIL_INPUT = By.id("loginEmail");
        public static final By PASSWORD_INPUT = By.id("cst_login_pwd");
        public static final By LOGIN_BUTTON = By.xpath("//button[@value='Login']");
        public static final By OTP_INPUT_FIELDS = By.xpath("//div[@id='verify_otp_sec']//input[@type='text']");
        public static final By VERIFY_BUTTON = By.xpath("//span[contains(text(), 'Verify')]");

        // Dashboard checks
        // Dashboard checks - STRICT LOCATORS
        public static final By SUPER_ADMIN_DASHBOARD = By.xpath("//span[normalize-space()='Customer Org']"); // Unique
                                                                                                             // to SA
                                                                                                             // Sidebar
        public static final By ENTERPRISE_DASHBOARD = By.xpath("//span[normalize-space()='Rate Card']"); // Unique to
                                                                                                         // Enterprise
                                                                                                         // Sidebar
        public static final By RESELLER_DASHBOARD = By.xpath("//span[normalize-space()='User Management']"); // Unique
                                                                                                             // to
                                                                                                             // Reseller
                                                                                                             // Sidebar

        // Enterprise Wallet Selection
        public static final By WALLET_SELECTION_HEADER = By.xpath("//h5[contains(text(), 'Select Wallet')]");
        public static final By WALLET_OPEN_BUTTON = By.xpath("//button[contains(text(), 'Open')]");

        // Smart Wallet Selection Locators
        public static final By WALLET_CARD = By
                        .xpath("//div[contains(@class, 'card') and .//button[contains(text(), 'Open')]]");
        // Looking for list items, chips, or icons that represent services inside a card
        public static final By WALLET_SERVICE_ITEMS = By
                        .xpath(".//li | .//mat-chip | .//div[contains(@class, 'service-item')] | .//img[@alt]");

        // Error Messages
        public static final By INVALID_CREDENTIALS_ALERT = By
                        .xpath("//div[contains(@class, 'alert-error')]//p[@class='alert-sub-title']");
        public static final By EMAIL_REQUIRED_MSG = By.xpath(
                        "//div[contains(@class, 'form-block') and .//input[@id='loginEmail']]//span[@class='invalid-feedback']");
        public static final By PASSWORD_REQUIRED_MSG = By.xpath(
                        "//div[contains(@class, 'form-block') and .//input[@id='cst_login_pwd']]//span[@class='invalid-feedback']");

        // Logout
        public static final By PROFILE_MENU = By.xpath(
                        "//div[contains(@class, 'sidenav-header')]//button[contains(@class, 'mat-mdc-menu-trigger')]");
        public static final By SIGN_OUT_BUTTON = By.xpath(
                        "//div[contains(@class, 'cdk-overlay-pane')]//button[@role='menuitem']//div[normalize-space(text())='Sign out']");
        public static final By CONFIRM_LOGOUT_BUTTON = By.xpath("//button[contains(., 'Yes, Logout')]");
}
