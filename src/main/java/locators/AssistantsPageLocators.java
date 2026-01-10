package locators;

import org.openqa.selenium.By;

public class AssistantsPageLocators {

    // Table headers
    public static final By HEADER_ASSISTANT_NAME = By.xpath("//th[text()='Assistant Name']");
    public static final By HEADER_CREATED_BY = By.xpath("//th[text()='Created By']");
    public static final By HEADER_CREATED_ON = By.xpath("//th[text()='Created On']");
    public static final By HEADER_UPDATED_ON = By.xpath("//th[text()='Updated On']");
    public static final By HEADER_STATUS = By.xpath("//th[text()='Status']");
    public static final By HEADER_SERVICE_ACCOUNT = By.xpath("//th[text()='Service Account']");

    // Message when no assistant exists
    public static final By NO_ASSISTANT_MESSAGE = By
            .xpath("//p[contains (text(),'Check your search criteria or create a new assistant')]");

    // Edit Assistant
    public static final By THREE_DOT_MENU = By.xpath("//button[@aria-label='Assistants Action Options']");
    public static final By EDIT_BUTTON = By.xpath("//a[contains(@class,'dropdown-item') and normalize-space()='Edit']");
    public static final By EDIT_PAGE_HEADER = By.xpath("//h4[normalize-space(text())='Edit Assistant']");

    // Assistant search
    public static final By FIRST_ASSISTANT_NAME = By.xpath("//p[@class='mat-mdc-tooltip-trigger text-truncate']");
    public static final By SEARCH_BOX = By.xpath("//input[@type='search']");
    public static final By ASSISTANT_NAME_CELLS = By.xpath("//td[contains(@class, 'cdk-column-assistantName')]");

    // Assistant table header locator
    // public static final By ASSISTANT_TABLE_HEADER = By.xpath("//h4[contains
    // (text(),'Edit Assistant')]");

    public static final By EDIT_ASSISTANT_FORM = By.xpath("//h4[contains(text(),'Edit Assistant')]");

    public static final By ASSISTANT_NAME_INPUT = By.xpath("//input[@formcontrolname='assistantName']");
    public static final By NEXT_BUTTON1 = By
            .xpath("//button[contains(text(), 'Next') and contains(@ng-reflect-type, 'button')]");
    public static final By NEXT_BUTTON2 = By.xpath("(//button[contains(text(), 'Next')])[2]");
    public static final By MOBILE_NUMBER_INPUT = By.xpath("//input[@formcontrolname='phoneNumber']");
    public static final By BUSINESS_WEBSITE_INPUT = By.xpath("//input[@formcontrolname='uri']");
    public static final By MOBILE_PREVIEW_SECTION = By
            .xpath("//*[contains(@class, 'small') and (contains(text(), '9') or contains(text(), 'http'))]");
    public static final By UPDATE_ASSISTANT_BUTTON = By.xpath("//button[contains (text(),'Update Assistant ')]");
    public static final By GOT_IT_BUTTON = By.xpath("//button[contains (text(), 'Go to Assistant')]");
    // filter
    public static final By CALENDAR_ICON = By.xpath("//input[@name='datePickerInput']");
    public static final By LAST_7_DAYS = By
            .xpath("//ul[@class='list-group list-group-flush ps-2']//li[contains (text(),'Last 7 Days')]");
    public static final By LAST_30_DAYS = By
            .xpath("//ul[@class='list-group list-group-flush ps-2']//li[contains (text(),'Last 30 Days')]");
    public static final By FILTER_BUTTON = By.xpath("//button[@id='serviceAccountsDropDown']");
    public static final By STATUS_DROPDOWN = By.xpath("//div[@class='ng-select-container']");
    public static final By STATUS_OPTIONS = By.xpath("//div[@role='listbox']"); // or update based on actual list
                                                                                // structure
    public static final By APPLY_FILTER_BUTTON = By.xpath("//button[@class='btn btn-block btn-primary']");

    public static final By ADD_NEW_ASSISTANT_BUTTON = By.xpath("//button[@ng-reflect-router-link='add']");
    public static final By ASSISTANT_USERNAME_FIELD = By
            .xpath("//div[@class='form-block mb-2']//input[@placeholder='Enter Assistant Name']");
    public static final By ASSISTANT_USERNAME_DROPDOWN_OPTIONS = By
            .xpath("//div[@class='form-group mb-2']//ng-select[@placeholder='Select Template Category']");
    public static final By ASSISTANT_DESCRIPTION_INPUT = By
            .xpath("//div[@class='col-12']//textarea[@placeholder='Enter Description']");

    public static final By COVER_IMAGE_UPLOAD = By.id("coverImg"); // Upload input for cover image
    public static final By ICON_IMAGE_UPLOAD = By.id("iconImg"); // Assumed id for icon image (confirm this in DOM)

    public static final By DISPLAY_NAME_BUSINESS_PHONE = By
            .xpath("//div[@formarrayname='phone_numbers']//input[@placeholder='Enter Display Name']");
    public static final By MOBILE_NUMBER_FIELD = By.xpath("//input[@placeholder='Enter Phone Number']");
    public static final By DISPLAY_NAME_WEBSITE = By
            .xpath("//div[@formarrayname='websites']//input[@placeholder='Enter Display Name']");
    public static final By WEBSITE_URL_FIELD = By.xpath("//input[@placeholder='Enter Website URL']");
    public static final By DISPLAY_NAME_EMAIL = By
            .xpath("//div[@formarrayname='emails']//input[@placeholder='Enter Display Name']");
    public static final By EMAIL_ADDRESS_FIELD = By.xpath("//input[@placeholder='Enter Email Address']");
    public static final By PRIVACY_URL_FIELD = By.xpath("//input[@placeholder='Enter Privacy Policy URL']");
    public static final By TERMS_URL_FIELD = By.xpath("//input[@placeholder='Enter Terms & Conditions URL']");
    public static final By SUBMIT_BUTTON = By.xpath("//button[contains (text(), 'Submit')]");
    public static final By TEMPLATE_CATEGORY = By.xpath("//span[@class='ng-option-label']");

    // Success message
    public static final By SUCCESS_MESSAGE = By
            .xpath("//p[contains (text(),'Assistant Approval initiated successfully.')]");
    // public static final By FILTER_CLEAR = By.xpath("//em[@class = 'bi
    // bi-x-circle']");

}
