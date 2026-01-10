package locators;

import org.openqa.selenium.By;

public class SuperadminNavLocator {
    public static final By SERVICE_NODE_SSO = By.xpath("//span[contains(text(),'Service Nodes SSO')]");
    public static final  By RCS_LOGIN = By.xpath("//h6[contains(text(),'RCS')]/ancestor::div[contains(@class,'card')]/descendant::button[contains(text(),'Login')]");
    public static final By LOGIN_AS_SUPERADMIN = By.xpath("//span[contains(text(), 'SUPER ADMIN')]");
    public static final By SUPERADMIN_DASHBOARD = By.xpath("//h5[@class='title' and normalize-space(text())='Overall Performance Trends']\n");
    public static final By ASSISTANT01 = By.xpath("//span[normalize-space(text())='Assistants']");
    public static final By ASSISTANT_3DOT = By.xpath("(//em[@class = 'bi bi-three-dots-vertical'])[1]");
    public static final By ASSISTANT_3DOT_APPROVE = By.xpath("//a[@class='dropdown-item' and contains(text(), 'Approve')]");
    public static final By APPROVE_ASSISTANT_ID1 = By.xpath("//input[@formcontrolname = 'assistantId']");
    public static final By Approve_ASSISTANT_KEY1 = By.xpath("//input[@formcontrolname = 'service_key']");
    public static final By UPDATE_APPROVE = By.xpath("//button[contains(text(),'Update & Approve')]");
}
