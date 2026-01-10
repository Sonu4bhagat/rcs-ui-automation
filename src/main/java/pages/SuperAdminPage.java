package pages;

import locators.MediaLibraryLocators;
import locators.SuperadminNavLocator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.TestUtil;

import java.time.Duration;

public class SuperAdminPage {
    WebDriver driver;
    WebDriverWait wait;

    public SuperAdminPage (WebDriver driver){
       this.driver = driver;
       this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    public void loginAsSuperAdmin() {
        wait.until(ExpectedConditions.elementToBeClickable(SuperadminNavLocator.SERVICE_NODE_SSO));
        driver.findElement(SuperadminNavLocator.SERVICE_NODE_SSO).click();

        wait.until(ExpectedConditions.elementToBeClickable(SuperadminNavLocator.RCS_LOGIN));
        driver.findElement(SuperadminNavLocator.RCS_LOGIN).click();

        wait.until(ExpectedConditions.elementToBeClickable(SuperadminNavLocator.LOGIN_AS_SUPERADMIN));
        driver.findElement(SuperadminNavLocator.LOGIN_AS_SUPERADMIN).click();
    }
    public void navigateToSuperAdminDashboard() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(SuperadminNavLocator.SUPERADMIN_DASHBOARD));
    }
    public void navigateToAssistantsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(SuperadminNavLocator.ASSISTANT01));
        driver.findElement(SuperadminNavLocator.ASSISTANT01).click();
    }
    public void click3DotMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(SuperadminNavLocator.ASSISTANT_3DOT));
        driver.findElement(SuperadminNavLocator.ASSISTANT_3DOT).click();
    }
    public void clickApproveTab() {
        wait.until(ExpectedConditions.elementToBeClickable(SuperadminNavLocator.ASSISTANT_3DOT_APPROVE));
        driver.findElement(SuperadminNavLocator.ASSISTANT_3DOT_APPROVE).click();
    }
    public void enterAssistantDetails() {
        String randomName = TestUtil.getRandomName(6);
        wait.until(ExpectedConditions.visibilityOfElementLocated(SuperadminNavLocator.APPROVE_ASSISTANT_ID1));
        driver.findElement(SuperadminNavLocator.APPROVE_ASSISTANT_ID1).sendKeys(randomName);
        driver.findElement(SuperadminNavLocator.Approve_ASSISTANT_KEY1).sendKeys(randomName);
    }
    public void clickUpdateAndApprove() {
        wait.until(ExpectedConditions.elementToBeClickable(SuperadminNavLocator.UPDATE_APPROVE));
        driver.findElement(SuperadminNavLocator.UPDATE_APPROVE).click();
    }
    public void approveAssistant() {
        loginAsSuperAdmin();
        navigateToSuperAdminDashboard();
        navigateToAssistantsTab();
        click3DotMenu();
        clickApproveTab();
        enterAssistantDetails();
        clickUpdateAndApprove();
    }

}
