package locators;

import org.openqa.selenium.By;

public class NavigationLocators {

    public static final By OPEN_BTN = By.xpath("//h5[contains(text(), 'Retrol16-UW-321')]/ancestor::div[contains(@class, 'wallet-card')]//button[contains(text(), 'Open')]");
    public static final By SERVICES_TAB = By.xpath("//span[contains (text(), 'Services')]");
    public static final By VIEW_DETAIL_BTN = By.xpath("//h6[contains(text(), 'RCS')]/ancestor::div[contains(@class, 'card') and contains(@class, 'service-cards')]//button[contains(text(), 'View Details')]");
    public static final By SSO_ICON = By.xpath("//td[contains(text(), 'RCSSERV16')]/parent::tr//button[@mattooltip='SSO redirection']");
    public static final By CONTROL_CENTER_TAB = By.xpath("//span[contains (text(), 'Control Center')]");
    public static final By ASSISTANT_TAB = By.xpath("//a[@role='tab' and not(@aria-disabled='true')]//span[normalize-space(.)='Assistants']");
    public static final By OCMP_DASHBOARD_HEADER = By.xpath("//h4[text()='Dashboard']");
    public static final By CONTROL_CENTER_LANDING_HEADER = By.xpath("//h4[contains (text(), 'Manage Assistants')]");
    public static final By MANAGE_ASSISTANTS = By.xpath("//span[contains(text(), 'Manage Assistants')]");
}
