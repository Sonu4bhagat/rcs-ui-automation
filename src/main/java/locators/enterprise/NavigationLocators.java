package locators.enterprise;

import org.openqa.selenium.By;

public class NavigationLocators {

    public static final By OPEN_BTN = By.xpath("//h5[contains(text(), 'Retrol16-UW-321')]/ancestor::div[contains(@class, 'wallet-card')]//button[contains(text(), 'Open')]");
    public static final By SERVICES_TAB = By.xpath("//span[contains (text(), 'Services')]");
    // RCS card only - the previous "| //button[contains(text(), 'View Details')]" fallback matched the first
    // card on the page (SMS), which has no SSO redirection, so Assistant List / Media Library setup timed out
    public static final By VIEW_DETAIL_BTN = By.xpath("//h6[contains(text(), 'RCS')]/ancestor::div[contains(@class, 'card')]//button[contains(text(), 'View Details')]");
    // Same match as ServicesPageLocators.getSSOIconByRow (tooltip *contains* "SSO")
    public static final By SSO_ICON = By.xpath("//table//tbody//tr//button[contains(@mattooltip, 'SSO') or .//mat-icon[contains(text(), 'open_in_new')] or contains(@class, 'sso')]");
    public static final By CONTROL_CENTER_TAB = By.xpath("//span[contains (text(), 'Control Center')]");
    public static final By ASSISTANT_TAB = By.xpath("//a[@role='tab' and not(@aria-disabled='true')]//span[normalize-space(.)='Assistants']");
    public static final By OCMP_DASHBOARD_HEADER = By.xpath("//h4[text()='Dashboard']");
    public static final By CONTROL_CENTER_LANDING_HEADER = By.xpath("//h4[contains (text(), 'Manage Assistants')]");
    public static final By MANAGE_ASSISTANTS = By.xpath("//span[contains(text(), 'Manage Assistants')]");
}
