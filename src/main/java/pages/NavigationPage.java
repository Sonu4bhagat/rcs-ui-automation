package pages;

import locators.AssistantsPageLocators;
import locators.MediaLibraryLocators;
import locators.NavigationLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Set;

public class NavigationPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public NavigationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void clickOpenButton() {
        wait.until(ExpectedConditions.elementToBeClickable(NavigationLocators.OPEN_BTN)).click();
        System.out.println("Clicked on 'Open' button.");
    }

    public void clickServicesTab() {
        wait.until(ExpectedConditions.elementToBeClickable(NavigationLocators.SERVICES_TAB)).click();
        System.out.println("Clicked on 'Services' tab.");
    }

    public void clickViewDetails() {
        wait.until(ExpectedConditions.elementToBeClickable(NavigationLocators.VIEW_DETAIL_BTN)).click();
        System.out.println("Clicked on 'View Details' button.");
    }

    public void clickSSORedirectionIcon() {
        String mainWindow = driver.getWindowHandle();
        wait.until(ExpectedConditions.elementToBeClickable(NavigationLocators.SSO_ICON)).click();
        System.out.println("Clicked on 'SSO Redirection' icon.");

        // Wait and switch to new tab
        Set<String> handles;
        int attempts = 0;
        do {
            handles = driver.getWindowHandles();
            attempts++;
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        } while (handles.size() == 1 && attempts < 10);

        for (String handle : handles) {
            if (!handle.equals(mainWindow)) {
                driver.switchTo().window(handle);
                System.out.println("Switched to new SSO tab.");
                break;
            }
        }

        // Check if Page-Not-Found is opened, refresh once
        if (driver.getCurrentUrl().contains("page-not-found")) {
            System.out.println("Landed on Page-Not-Found, refreshing...");
            driver.navigate().refresh();
        }

        // Final wait: confirm Dashboard is loaded
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(NavigationLocators.OCMP_DASHBOARD_HEADER),
                    ExpectedConditions.visibilityOfElementLocated(NavigationLocators.MANAGE_ASSISTANTS)
                   // ExpectedConditions.presenceOfElementLocated(NavigationLocators.CONTROL_CENTER_TAB)
            ));
            System.out.println("SSO redirect completed, Dashboard/Control Center is visible.");
        } catch (Exception e) {
            throw new RuntimeException("SSO did not complete properly, still stuck or 404 error.");
        }
    }

    public void clickControlCenter() {
        // Wait for Control Center tab and click it
        //wait.until(ExpectedConditions.presenceOfElementLocated(NavigationLocators.CONTROL_CENTER_TAB));
        wait.until(ExpectedConditions.presenceOfElementLocated(NavigationLocators.MANAGE_ASSISTANTS));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
       // WebElement controlCenterTab = wait.until(ExpectedConditions.elementToBeClickable(NavigationLocators.CONTROL_CENTER_TAB));
        WebElement controlCenterTab = wait.until(ExpectedConditions.elementToBeClickable(NavigationLocators.MANAGE_ASSISTANTS));
        controlCenterTab.click();

        //wait.until(ExpectedConditions.elementToBeClickable(NavigationLocators.CONTROL_CENTER_TAB)).click();
        System.out.println("Clicked on 'Control Center' tab.");

        // Verify Control Center landing page (e.g. look for heading, unique element)
        wait.until(ExpectedConditions.visibilityOfElementLocated(NavigationLocators.CONTROL_CENTER_LANDING_HEADER));
        System.out.println("Control Center landing page loaded successfully.");
    }

    public void clickAssistantTab() {
        // Click Assistants tab (ensure visible + clickable)
        WebElement assistantTab = wait.until(ExpectedConditions
                .visibilityOfElementLocated(NavigationLocators.ASSISTANT_TAB));
        wait.until(ExpectedConditions.elementToBeClickable(assistantTab)).click();
        System.out.println("Clicked on 'Assistant' tab.");

        // Wait until URL updates (if routing changes)
        wait.until(ExpectedConditions.urlContains("/assistants"));

        // Now wait for Assistants page to load (header or no data message)
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(AssistantsPageLocators.NO_ASSISTANT_MESSAGE),
                ExpectedConditions.presenceOfElementLocated(AssistantsPageLocators.HEADER_ASSISTANT_NAME)
        ));

        System.out.println("Assistants page loaded successfully.");
    }
    public void navigateToAssistantList() {
        clickOpenButton();
        clickServicesTab();
        clickViewDetails();
        clickSSORedirectionIcon(); // now waits for SSO
       // wait.until(ExpectedConditions.elementToBeClickable(NavigationLocators.CONTROL_CENTER_TAB)).click();
        clickControlCenter();
       // wait.until(ExpectedConditions.elementToBeClickable(NavigationLocators.ASSISTANT_TAB)).click();
        clickAssistantTab();
        System.out.println("Navigation to Assistant list completed.");
    }


    public void navigateToMediaLibrary() {
        clickOpenButton();
        clickServicesTab();
        clickViewDetails();
        clickSSORedirectionIcon();
        wait.until(ExpectedConditions.elementToBeClickable(MediaLibraryLocators.MEDIA_LIBRARY_TAB)).click();
        System.out.println("Navigation to Media Library page");
    }
}
