package locators;

import org.openqa.selenium.By;

public class NavigationLocators {

    public static final By OPEN_BTN = By.xpath("//h5[contains(text(), 'Retrol16-UW-321')]/ancestor::div[contains(@class, 'wallet-card')]//button[contains(text(), 'Open')]");
    public static final By SERVICES_TAB = By.xpath("//span[contains (text(), 'Services')]");
    public static final By VIEW_DETAIL_BTN = By.xpath("//h6[contains(text(), 'RCS')]/ancestor::div[contains(@class, 'card')]//button[contains(text(), 'View Details')] | //button[contains(text(), 'View Details')]");
    public static final By SSO_ICON = By.xpath("//button[@mattooltip='SSO redirection'] | //button[contains(@class, 'sso')] | //mat-icon[contains(text(), 'open_in_new')]/parent::button");
    public static final By CONTROL_CENTER_TAB = By.xpath("//span[contains (text(), 'Control Center')]");
    public static final By ASSISTANT_TAB = By.xpath("//a[@role='tab' and not(@aria-disabled='true')]//span[normalize-space(.)='Assistants']");
    public static final By OCMP_DASHBOARD_HEADER = By.xpath("//h4[text()='Dashboard']");
    public static final By CONTROL_CENTER_LANDING_HEADER = By.xpath("//h4[contains (text(), 'Manage Assistants')]");
    public static final By MANAGE_ASSISTANTS = By.xpath("//span[contains(text(), 'Manage Assistants')]");
}
