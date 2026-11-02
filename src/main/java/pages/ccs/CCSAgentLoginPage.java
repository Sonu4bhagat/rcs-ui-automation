package pages.ccs;

import locators.ccs.CCSAgentLoginPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ExtentReportManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the CCS-Live agent login configuration form (Select Type, Select Campaign, Submit), opened in a
 * new tab by the Users row action "Login", and the Agent Live Dashboard (/agent/live-new) it leads to.
 */
public class CCSAgentLoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT = 30;
    private static final int HEADLESS_TIMEOUT = 45;

    public static final String AGENT_DASHBOARD_PATH = "/agent/live-new";

    public CCSAgentLoginPage(WebDriver driver) {
        this.driver = driver;
        int timeout = base.DriverFactory.isHeadlessModeEnabled() ? HEADLESS_TIMEOUT : DEFAULT_TIMEOUT;
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    /** Waits until the agent login configuration form is shown (it loads after the tab opens). */
    public boolean waitForLoginForm() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(CCSAgentLoginPageLocators.LOGIN_TYPE_SELECT));
            wait.until(ExpectedConditions.visibilityOfElementLocated(CCSAgentLoginPageLocators.CAMPAIGN_SELECT));
            System.out.println("[INFO] Agent login form loaded: " + CCSLivePage.stripQuery(driver.getCurrentUrl()));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isDisplayed(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() && elements.get(0).isDisplayed();
    }

    public List<String> getFormLabels() {
        List<String> labels = new ArrayList<>();
        for (WebElement label : driver.findElements(CCSAgentLoginPageLocators.FORM_LABELS)) {
            if (label.isDisplayed() && !label.getText().trim().isEmpty()) {
                labels.add(label.getText().trim());
            }
        }
        return labels;
    }

    // ==================== Select Type ====================

    public List<String> getLoginTypeOptions() {
        List<String> options = new ArrayList<>();
        for (WebElement option : new Select(driver.findElement(CCSAgentLoginPageLocators.LOGIN_TYPE_SELECT)).getOptions()) {
            options.add(option.getText().trim());
        }
        return options;
    }

    /** Selects the login type whose text matches {@code type} case-insensitively (the option reads "Webrtc"). */
    public void selectLoginType(String type) {
        ExtentReportManager.logStep("Select Type: " + type);
        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(CCSAgentLoginPageLocators.LOGIN_TYPE_SELECT)));
        for (WebElement option : select.getOptions()) {
            if (option.getText().trim().equalsIgnoreCase(type)) {
                select.selectByVisibleText(option.getText());
                return;
            }
        }
        throw new NoSuchElementException("Login type not offered: " + type);
    }

    public String getSelectedLoginType() {
        return new Select(driver.findElement(CCSAgentLoginPageLocators.LOGIN_TYPE_SELECT)).getFirstSelectedOption().getText().trim();
    }

    // ==================== Select Campaign (ng-select) ====================

    /** Campaign names offered to the agent (group headers such as "click_to_call_campaign" are left out). */
    public List<String> getCampaignOptions() {
        openCampaignDropdown();
        List<String> campaigns = new ArrayList<>();
        for (WebElement option : driver.findElements(CCSAgentLoginPageLocators.CAMPAIGN_OPTIONS)) {
            String cls = option.getAttribute("class");
            if (!cls.contains("ng-optgroup") && !cls.contains("ng-option-disabled")) {
                campaigns.add(option.getText().trim());
            }
        }
        closeCampaignDropdown();
        return campaigns;
    }

    /** Selects the campaign (case-insensitive - lists show names in Title Case). */
    public void selectCampaign(String campaignName) {
        ExtentReportManager.logStep("Select Campaign: " + campaignName);
        openCampaignDropdown();
        WebElement option = driver.findElements(CCSAgentLoginPageLocators.CAMPAIGN_OPTIONS).stream()
                .filter(o -> o.getText().trim().equalsIgnoreCase(campaignName)).findFirst().orElse(null);
        if (option == null) {
            closeCampaignDropdown();
            throw new NoSuchElementException("Campaign not offered to the agent: " + campaignName);
        }
        option.click();
        wait.until(d -> getSelectedCampaign().equalsIgnoreCase(campaignName));
    }

    public String getSelectedCampaign() {
        List<WebElement> label = driver.findElements(CCSAgentLoginPageLocators.CAMPAIGN_SELECTED_LABEL);
        return label.isEmpty() ? "" : label.get(0).getText().trim();
    }

    // ng-select opens on a real mouse-down (a JavaScript click does not open it)
    private void openCampaignDropdown() {
        WebElement select = wait.until(ExpectedConditions.visibilityOfElementLocated(CCSAgentLoginPageLocators.CAMPAIGN_SELECT));
        new Actions(driver).moveToElement(select).click().perform();
        wait.until(d -> d.findElements(CCSAgentLoginPageLocators.CAMPAIGN_OPTIONS).stream().anyMatch(WebElement::isDisplayed));
    }

    private void closeCampaignDropdown() {
        driver.findElement(CCSAgentLoginPageLocators.CAMPAIGN_SELECT).findElement(By.xpath(".//input")).sendKeys(Keys.ESCAPE);
    }

    // ==================== Submit / Agent Live Dashboard ====================

    public void clickSubmit() {
        ExtentReportManager.logStep("Click Submit");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();",
                wait.until(ExpectedConditions.presenceOfElementLocated(CCSAgentLoginPageLocators.SUBMIT_BUTTON)));
    }

    /** Waits for the Agent Live Dashboard and its sections; returns false when it does not load. */
    public boolean waitForAgentDashboard() {
        try {
            wait.until(ExpectedConditions.urlContains(AGENT_DASHBOARD_PATH));
            wait.until(ExpectedConditions.visibilityOfElementLocated(CCSAgentLoginPageLocators.SWITCH_CAMPAIGN_BUTTON));
            System.out.println("[INFO] Agent Live Dashboard loaded: " + CCSLivePage.stripQuery(driver.getCurrentUrl()));
            return true;
        } catch (TimeoutException e) {
            System.out.println("[WARN] Agent Live Dashboard not loaded. URL: " + CCSLivePage.stripQuery(driver.getCurrentUrl()));
            return false;
        }
    }

    public List<String> getDashboardHeadings() {
        List<String> headings = new ArrayList<>();
        for (WebElement heading : driver.findElements(CCSAgentLoginPageLocators.DASHBOARD_HEADINGS)) {
            if (heading.isDisplayed() && !heading.getText().trim().isEmpty()) {
                headings.add(heading.getText().trim());
            }
        }
        return headings;
    }
}
