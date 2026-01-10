package locators;

import org.openqa.selenium.By;

/**
 * Locators for RCS Assistant Portal - used after SSO from Enterprise Services
 * Covers: Manage Assistants, Add/Edit Assistant forms, Search, Filter, Download
 */
public class RCSAssistantPageLocators {

        // ==================== Navigation & Menu ====================
        // Multiple fallback locators for Manage Assistants menu on RCS Portal
        public static final By MANAGE_ASSISTANTS_MENU = By.xpath(
                        "//span[contains(text(),'Manage Assistants')] | " +
                                        "//a[contains(text(),'Manage Assistants')] | " +
                                        "//span[contains(text(),'Assistants')] | " +
                                        "//a[contains(@href,'assistants')] | " +
                                        "//div[contains(@class,'nav')]//span[contains(text(),'Assistants')] | " +
                                        "//li[contains(@class,'nav')]//a[contains(text(),'Assistants')] | " +
                                        "//button[contains(text(),'Assistants')]");

        // Control Center menu (parent menu that may contain Assistants)
        public static final By CONTROL_CENTER_MENU = By.xpath(
                        "//span[contains(text(),'Control Center')] | " +
                                        "//a[contains(text(),'Control Center')] | " +
                                        "//div[contains(@class,'nav')]//span[contains(text(),'Control')]");

        public static final By ASSISTANTS_PAGE_HEADER = By.xpath(
                        "//h4[contains(text(),'Assistants')] | " +
                                        "//h5[contains(text(),'Assistants')] | " +
                                        "//h4[contains(text(),'Manage Assistants')] | " +
                                        "//div[contains(@class,'header')]//span[contains(text(),'Assistants')]");
        public static final By RCS_PORTAL_LOGO = By.xpath(
                        "//img[contains(@src,'logo') or contains(@alt,'SPARC')] | " +
                                        "//img[contains(@src,'smartping')] | " +
                                        "//div[contains(@class,'logo')]//img");

        // ==================== Assistants Table ====================
        public static final By ASSISTANTS_TABLE = By.xpath("//table[contains(@class,'table')]");
        public static final By ASSISTANT_TABLE_ROWS = By.xpath("//table//tbody/tr");
        public static final By FIRST_ASSISTANT_NAME = By
                        .xpath("//table//tbody/tr[1]//td[1]//p | //table//tbody/tr[1]//td[1]");
        public static final By ASSISTANT_NAME_CELLS = By
                        .xpath("//td[contains(@class,'assistantName')] | //table//tbody/tr/td[1]");
        public static final By ASSISTANT_STATUS_CELLS = By
                        .xpath("//td[contains(@class,'status')] | //table//tbody/tr/td[5]");
        public static final By NO_ASSISTANT_MESSAGE = By
                        .xpath("//p[contains(text(),'No assistant') or contains(text(),'no data')]");

        // ==================== Add New Assistant ====================
        public static final By ADD_NEW_BUTTON = By
                        .xpath("//button[contains(text(),'Add New') or contains(@ng-reflect-router-link,'add')]");

        // Basic Details Section
        public static final By ASSISTANT_NAME_INPUT = By
                        .xpath("//input[@formcontrolname='assistantName' or @placeholder='Enter Assistant Name']");
        public static final By ASSISTANT_DESCRIPTION_INPUT = By
                        .xpath("//textarea[@formcontrolname='description' or @placeholder='Enter Description']");
        public static final By TEMPLATE_CATEGORY_DROPDOWN = By
                        .xpath("//ng-select[@placeholder='Select Template Category'] | //div[contains(@class,'ng-select')]");
        public static final By TEMPLATE_CATEGORY_OPTIONS = By
                        .xpath("//span[@class='ng-option-label'] | //div[@role='option']");
        public static final By NEXT_BUTTON_STEP1 = By.xpath("(//button[contains(text(),'Next')])[1]");

        // Branding Section
        public static final By COVER_IMAGE_UPLOAD = By.id("coverImg");
        public static final By ICON_IMAGE_UPLOAD = By.id("iconImg");
        public static final By COVER_IMAGE_PREVIEW = By
                        .xpath("//img[contains(@class,'cover-preview') or contains(@alt,'cover')]");
        public static final By ICON_IMAGE_PREVIEW = By
                        .xpath("//img[contains(@class,'icon-preview') or contains(@alt,'icon')]");
        public static final By NEXT_BUTTON_STEP2 = By
                        .xpath("(//button[contains(text(),'Next')])[2] | //button[contains(text(),'Next') and not(@disabled)]");

        // Contact Section
        public static final By PHONE_DISPLAY_NAME = By
                        .xpath("//div[@formarrayname='phone_numbers']//input[@placeholder='Enter Display Name']");
        public static final By PHONE_NUMBER_INPUT = By
                        .xpath("//input[@placeholder='Enter Phone Number' or @formcontrolname='phoneNumber']");
        public static final By WEBSITE_DISPLAY_NAME = By
                        .xpath("//div[@formarrayname='websites']//input[@placeholder='Enter Display Name']");
        public static final By WEBSITE_URL_INPUT = By
                        .xpath("//input[@placeholder='Enter Website URL' or @formcontrolname='uri']");
        public static final By EMAIL_DISPLAY_NAME = By
                        .xpath("//div[@formarrayname='emails']//input[@placeholder='Enter Display Name']");
        public static final By EMAIL_ADDRESS_INPUT = By
                        .xpath("//input[@placeholder='Enter Email Address' or @formcontrolname='email']");
        public static final By PRIVACY_URL_INPUT = By.xpath("//input[@placeholder='Enter Privacy Policy URL']");
        public static final By TERMS_URL_INPUT = By.xpath("//input[@placeholder='Enter Terms & Conditions URL']");
        public static final By SUBMIT_BUTTON = By.xpath("//button[contains(text(),'Submit')]");

        // Success Messages
        public static final By SUCCESS_MESSAGE = By
                        .xpath("//p[contains(text(),'successfully')] | //div[contains(@class,'success')]");
        public static final By GO_TO_ASSISTANT_BUTTON = By.xpath("//button[contains(text(),'Go to Assistant')]");

        // ==================== Search & Filter ====================
        public static final By SEARCH_BOX = By.xpath("//input[@type='search' or @placeholder='Search']");
        public static final By FILTER_BUTTON = By
                        .xpath("//button[@id='serviceAccountsDropDown' or contains(@class,'filter')]");
        public static final By STATUS_DROPDOWN = By
                        .xpath("//div[@class='ng-select-container'] | //ng-select[contains(@placeholder,'Status')]");
        public static final By PENDING_STATUS_OPTION = By
                        .xpath("//span[contains(text(),'Pending')] | //div[@role='option' and contains(text(),'Pending')]");
        public static final By APPLY_FILTER_BUTTON = By
                        .xpath("//button[contains(@class,'btn-primary') and contains(text(),'Apply')]");
        public static final By CLEAR_FILTER_BUTTON = By
                        .xpath("//em[contains(@class,'bi-x-circle')] | //button[contains(text(),'Clear')]");

        // ==================== Download ====================
        public static final By DOWNLOAD_ICON = By.xpath(
                        "//button[contains(@class,'download') or contains(@title,'Download')] | //em[contains(@class,'bi-download')]");

        // ==================== View/Edit Actions ====================
        public static final By THREE_DOT_MENU = By
                        .xpath("//button[@aria-label='Assistants Action Options'] | //button[contains(@class,'dropdown-toggle')]");
        public static final By VIEW_BUTTON = By
                        .xpath("//a[contains(@class,'dropdown-item') and contains(text(),'View')]");
        public static final By EDIT_BUTTON = By
                        .xpath("//a[contains(@class,'dropdown-item') and contains(text(),'Edit')]");
        public static final By EDIT_PAGE_HEADER = By.xpath("//h4[contains(text(),'Edit Assistant')]");
        public static final By VIEW_DETAILS_ASSISTANT_NAME = By
                        .xpath("//h4[contains(@class,'assistant-name')] | //div[contains(@class,'detail')]//h4");

        // Update Button for Edit
        public static final By UPDATE_ASSISTANT_BUTTON = By.xpath("//button[contains(text(),'Update Assistant')]");

        // ==================== Calendar Filter ====================
        public static final By CALENDAR_ICON = By
                        .xpath("//input[@name='datePickerInput'] | //em[contains(@class,'bi-calendar')]");
        public static final By LAST_7_DAYS = By.xpath("//li[contains(text(),'Last 7 Days')]");
        public static final By LAST_30_DAYS = By.xpath("//li[contains(text(),'Last 30 Days')]");

        // ==================== Status Badge ====================
        public static final By PENDING_STATUS_BADGE = By
                        .xpath("//span[contains(@class,'badge') and contains(text(),'Pending')]");
        public static final By APPROVED_STATUS_BADGE = By
                        .xpath("//span[contains(@class,'badge') and contains(text(),'Approved')]");

        // ==================== Logout from RCS Portal ====================
        public static final By RCS_PROFILE_MENU = By
                        .xpath("//div[contains(@class,'profile')] | //button[contains(@class,'user-profile')]");
        public static final By RCS_LOGOUT_BUTTON = By.xpath(
                        "//a[contains(text(),'Logout') or contains(text(),'Sign Out')] | //button[contains(text(),'Logout')]");
        public static final By RCS_LOGOUT_CONFIRM = By
                        .xpath("//button[contains(text(),'Yes') or contains(text(),'Confirm')]");
        public static final By RCS_LOGIN_PAGE = By.xpath("//input[@type='email'] | //h1[contains(text(),'Login')]");

        // ==================== Validation Messages ====================
        public static final By NAME_REQUIRED_ERROR = By
                        .xpath("//span[contains(text(),'required') and contains(@class,'error')]");
        public static final By VALID_INPUT_INDICATOR = By
                        .xpath("//div[contains(@class,'valid-feedback') or contains(@class,'success')]");
}
