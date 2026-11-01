package pages.ccs;

import locators.ccs.CCSQueuePageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ExtentReportManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for CCS-Live Call Flow: Queue list and the Add / Edit Queue form.
 * The Queue list has the same table, search controls and toast notifications as the Users list, so those
 * parts are delegated to CCSUsersPage instead of being duplicated.
 */
public class CCSQueuePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final CCSUsersPage listPage;
    private static final int DEFAULT_TIMEOUT = 30;
    private static final int HEADLESS_TIMEOUT = 45;

    public static final String CALL_FLOW_PATH = "/client/ivr";
    public static final String QUEUE_LIST_PATH = "/client/queue/list-queue";
    public static final String ADD_QUEUE_PATH = "/client/queue/add-queue";

    public CCSQueuePage(WebDriver driver) {
        this.driver = driver;
        this.listPage = new CCSUsersPage(driver);
        int timeout = base.DriverFactory.isHeadlessModeEnabled() ? HEADLESS_TIMEOUT : DEFAULT_TIMEOUT;
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    // ==================== Navigation ====================

    public void openCallFlow() {
        ExtentReportManager.logStep("Click Call Flow in the CCS-Live sidebar");
        jsClick(wait.until(ExpectedConditions.presenceOfElementLocated(CCSQueuePageLocators.CALL_FLOW_MENU)));
        wait.until(ExpectedConditions.urlContains(CALL_FLOW_PATH));
    }

    public void openQueueTab() {
        ExtentReportManager.logStep("Click Queue under Call Flow");
        jsClick(wait.until(ExpectedConditions.presenceOfElementLocated(CCSQueuePageLocators.QUEUE_MENU)));
        waitForQueueList();
    }

    public void waitForQueueList() {
        wait.until(ExpectedConditions.urlContains(QUEUE_LIST_PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CCSQueuePageLocators.ADD_QUEUE_BUTTON));
        System.out.println("[INFO] CCS Queue list loaded");
    }

    public boolean isOnQueueList() {
        return driver.getCurrentUrl().contains(QUEUE_LIST_PATH);
    }

    public boolean isOnAddQueueForm() {
        return driver.getCurrentUrl().contains(ADD_QUEUE_PATH);
    }

    public boolean isDisplayed(By locator) {
        return listPage.isDisplayed(locator);
    }

    // ==================== Queue List (shared list controls) ====================

    public List<String> getTableHeaders() {
        return listPage.getTableHeaders();
    }

    /** Searches the Queue list by name and returns the number of rows shown. */
    public int searchQueuesByName(String name) {
        return listPage.searchUsers(CCSQueuePageLocators.SEARCH_BY_QUEUE_NAME, name);
    }

    public void clearSearch() {
        listPage.clearSearch();
    }

    public String getCellText(int rowIndex, String header) {
        return listPage.getCellText(rowIndex, header);
    }

    public String getNotifications() {
        return listPage.getNotifications();
    }

    public void clickEditAtRow(int rowIndex) {
        ExtentReportManager.logStep("Click Edit on queue row " + rowIndex);
        jsClick(wait.until(ExpectedConditions.presenceOfElementLocated(CCSQueuePageLocators.getRowEditButton(rowIndex))));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CCSQueuePageLocators.NAME_INPUT));
        // The edit form loads the saved values asynchronously
        wait.until(d -> !d.findElement(CCSQueuePageLocators.NAME_INPUT).getAttribute("value").isEmpty());
    }

    // ==================== Add / Edit Queue Form ====================

    public void clickAddQueue() {
        ExtentReportManager.logStep("Click Add Queue");
        wait.until(ExpectedConditions.elementToBeClickable(CCSQueuePageLocators.ADD_QUEUE_BUTTON)).click();
        wait.until(ExpectedConditions.urlContains(ADD_QUEUE_PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CCSQueuePageLocators.NAME_INPUT));
    }

    /** Clears the field, types {@code value} and tabs out so the field is validated. */
    public void enterText(By field, String value) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(field));
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);
    }

    public void enterName(String name) {
        ExtentReportManager.logStep("Enter Queue Name: " + name);
        enterText(CCSQueuePageLocators.NAME_INPUT, name);
    }

    public void enterDescription(String description) {
        ExtentReportManager.logStep("Enter Description: " + description);
        enterText(CCSQueuePageLocators.DESCRIPTION_INPUT, description);
    }

    public String getFieldValue(By field) {
        String value = driver.findElement(field).getAttribute("value");
        return value == null ? "" : value;
    }

    public String getAttribute(By field, String attribute) {
        return driver.findElement(field).getAttribute(attribute);
    }

    /** Character counter under Queue Name, e.g. "12/50". */
    public String getNameCounter() {
        return driver.findElement(CCSQueuePageLocators.NAME_COUNTER).getText().trim();
    }

    public List<String> getOptions(By select) {
        List<String> options = new ArrayList<>();
        for (WebElement option : new Select(driver.findElement(select)).getOptions()) {
            options.add(option.getText().trim());
        }
        return options;
    }

    public String getSelectedOption(By select) {
        return new Select(driver.findElement(select)).getFirstSelectedOption().getText().trim();
    }

    public void selectByText(By select, String text) {
        ExtentReportManager.logStep("Select '" + text + "'");
        new Select(wait.until(ExpectedConditions.elementToBeClickable(select))).selectByVisibleText(text);
    }

    /** Agent names offered by the Agents multi-select. */
    public List<String> getAgentOptions() {
        openAgentsDropdown();
        List<String> agents = new ArrayList<>();
        for (WebElement option : driver.findElements(CCSQueuePageLocators.AGENT_OPTIONS)) {
            agents.add(option.getText().trim());
        }
        closeAgentsDropdown();
        return agents;
    }

    /** Ticks the agent whose name matches {@code agentName} (case-insensitive - the Users list shows Title Case). */
    public void selectAgent(String agentName) {
        ExtentReportManager.logStep("Select Agent: " + agentName);
        openAgentsDropdown();
        WebElement option = findAgentOption(agentName);
        if (option == null) {
            closeAgentsDropdown();
            throw new NoSuchElementException("Agent not offered in the Agents dropdown: " + agentName);
        }
        if (!option.findElement(By.tagName("input")).isSelected()) {
            jsClick(option);
        }
        closeAgentsDropdown();
    }

    public boolean isAgentSelected(String agentName) {
        WebElement option = findAgentOption(agentName);
        return option != null && option.findElement(By.tagName("input")).isSelected();
    }

    private WebElement findAgentOption(String agentName) {
        for (WebElement option : driver.findElements(CCSQueuePageLocators.AGENT_OPTIONS)) {
            if (option.getAttribute("textContent").trim().equalsIgnoreCase(agentName.trim())) {
                return option;
            }
        }
        return null;
    }

    private void openAgentsDropdown() {
        jsClick(wait.until(ExpectedConditions.presenceOfElementLocated(CCSQueuePageLocators.AGENTS_DROPDOWN_BUTTON)));
        wait.until(d -> d.findElements(CCSQueuePageLocators.AGENT_OPTIONS).stream().anyMatch(WebElement::isDisplayed));
    }

    private void closeAgentsDropdown() {
        // Clicking the dropdown button again closes the list
        jsClick(driver.findElement(CCSQueuePageLocators.AGENTS_DROPDOWN_BUTTON));
    }

    public WebElement getSaveButton() {
        return wait.until(d -> d.findElements(CCSQueuePageLocators.SAVE_BUTTONS).stream()
                .filter(WebElement::isDisplayed).findFirst().orElse(null));
    }

    /** Clicks the visible Save button once. */
    public void clickSave() {
        waitForNotificationsToClear();
        ExtentReportManager.logStep("Click Save");
        jsClick(getSaveButton());
    }

    // Toasts stay for a few seconds: wait until an earlier one (e.g. "Please enter name") is gone, so the
    // message read after this Save is only the response to this Save
    private void waitForNotificationsToClear() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> getNotifications().isEmpty());
        } catch (TimeoutException e) {
            System.out.println("[WARN] Earlier notification still shown: " + getNotifications());
        }
    }

    /**
     * Waits after Save until the form is left (queue saved) or a notification is shown.
     * Returns the notification text, or "" when there was none.
     */
    public String waitForSaveOutcome() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(d -> !isOnAddQueueForm() || !getNotifications().isEmpty());
        } catch (TimeoutException e) {
            System.out.println("[WARN] No navigation or notification after Save");
        }
        String message = getNotifications();
        System.out.println("[INFO] After Save - URL: " + CCSLivePage.stripQuery(driver.getCurrentUrl()) + " Message: " + message);
        return message;
    }

    // A sticky footer covers the lower part of CCS-Live pages, so a normal click is intercepted
    private void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();", element);
    }
}
