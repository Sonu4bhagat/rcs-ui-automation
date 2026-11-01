package pages.ccs;

import locators.ccs.CCSUsersPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ExtentReportManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Page Object for CCS-Live Users management: Users list, search, row actions and the Add User form.
 */
public class CCSUsersPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT = 30;
    private static final int HEADLESS_TIMEOUT = 45;

    public static final String USERS_PATH = "/client/users";
    public static final String ADD_USER_PATH = "/client/users/add-user";

    public CCSUsersPage(WebDriver driver) {
        this.driver = driver;
        int timeout = base.DriverFactory.isHeadlessModeEnabled() ? HEADLESS_TIMEOUT : DEFAULT_TIMEOUT;
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    // ==================== Users List ====================

    public void openUsersTab() {
        ExtentReportManager.logStep("Click Users in the CCS-Live sidebar");
        wait.until(ExpectedConditions.elementToBeClickable(CCSUsersPageLocators.USERS_MENU)).click();
        waitForUsersList();
    }

    public void waitForUsersList() {
        wait.until(d -> CCSLivePage.stripQuery(d.getCurrentUrl()).endsWith(USERS_PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CCSUsersPageLocators.TABLE_HEADERS));
        System.out.println("[INFO] CCS Users list loaded");
    }

    public boolean isOnUsersList() {
        return CCSLivePage.stripQuery(driver.getCurrentUrl()).endsWith(USERS_PATH);
    }

    public boolean isDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isEnabled(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator)).isEnabled();
    }

    public List<String> getTableHeaders() {
        List<String> headers = new ArrayList<>();
        for (WebElement th : driver.findElements(CCSUsersPageLocators.TABLE_HEADERS)) {
            headers.add(th.getText().trim());
        }
        return headers;
    }

    /**
     * Searches the Users list by {@code searchType} (an option value such as
     * {@link CCSUsersPageLocators#SEARCH_BY_USER_NAME}) and returns the number of rows shown.
     */
    public int searchUsers(String searchType, String text) {
        ExtentReportManager.logStep("Search users by " + searchType + ": " + text);
        new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(CCSUsersPageLocators.SEARCH_TYPE_SELECT)))
                .selectByValue(searchType);
        WebElement search = driver.findElement(CCSUsersPageLocators.SEARCH_INPUT);
        search.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        search.sendKeys(text, Keys.ENTER);
        // The list reloads asynchronously: wait until every row matches the search, or no row is left
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
                List<WebElement> rows = d.findElements(CCSUsersPageLocators.TABLE_ROWS);
                return rows.isEmpty() || rows.stream().allMatch(r -> r.getText().toLowerCase().contains(text.toLowerCase()));
            });
        } catch (TimeoutException e) {
            System.out.println("[WARN] Users list did not settle on search '" + text + "'");
        }
        return getRowCount();
    }

    public void clearSearch() {
        WebElement search = driver.findElement(CCSUsersPageLocators.SEARCH_INPUT);
        search.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE, Keys.ENTER);
    }

    public int getRowCount() {
        return driver.findElements(CCSUsersPageLocators.TABLE_ROWS).size();
    }

    /** Text of the cell in {@code rowIndex} (1-based) under the column titled {@code header}. */
    public String getCellText(int rowIndex, String header) {
        List<String> headers = getTableHeaders();
        int column = headers.indexOf(header);
        if (column == -1) {
            throw new IllegalArgumentException("Column not found: " + header + " in " + headers);
        }
        return driver.findElement(CCSUsersPageLocators.getRowCell(rowIndex, column + 1)).getText().trim();
    }

    /** Row (1-based) of the Agent with the lowest User Id - the oldest agent - or -1 when there is none. */
    public int findOldestAgentRow() {
        int oldestRow = -1;
        long lowestId = Long.MAX_VALUE;
        for (int row = 1; row <= getRowCount(); row++) {
            if (!getCellText(row, CCSUsersPageLocators.COL_ROLE).equalsIgnoreCase("Agent")) {
                continue;
            }
            try {
                long id = Long.parseLong(getCellText(row, CCSUsersPageLocators.COL_USER_ID));
                if (id < lowestId) {
                    lowestId = id;
                    oldestRow = row;
                }
            } catch (NumberFormatException e) {
                // Row without a numeric User Id - not considered
            }
        }
        return oldestRow;
    }

    /**
     * Deletes the user in {@code rowIndex} through its action menu, confirming the dialog when one is shown,
     * and waits until that user is no longer listed. Returns the deleted user's Username (email).
     */
    public String deleteUserAtRow(int rowIndex) {
        String username = getCellText(rowIndex, CCSUsersPageLocators.COL_USERNAME);
        ExtentReportManager.logStep("Delete user " + username);
        jsClick(wait.until(ExpectedConditions.presenceOfElementLocated(CCSUsersPageLocators.getRowActionMenu(rowIndex))));
        jsClick(wait.until(ExpectedConditions.presenceOfElementLocated(CCSUsersPageLocators.getRowDeleteAction(rowIndex))));
        try {
            WebElement confirm = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    d -> d.findElements(CCSUsersPageLocators.CONFIRM_DIALOG_BUTTON).stream()
                            .filter(WebElement::isDisplayed).findFirst().orElse(null));
            System.out.println("[INFO] Confirming delete: " + confirm.getText().trim());
            jsClick(confirm);
        } catch (TimeoutException e) {
            System.out.println("[INFO] No delete confirmation dialog shown");
        }
        wait.until(d -> d.findElements(CCSUsersPageLocators.TABLE_ROWS).stream()
                .noneMatch(r -> r.getText().toLowerCase().contains(username.toLowerCase())));
        System.out.println("[INFO] Deleted user " + username + ". Message: " + getNotifications());
        closeDialogIfShown();
        return username;
    }

    /** Closes a success/confirmation dialog (e.g. "Successfully Deleted") that would cover the page. */
    public void closeDialogIfShown() {
        for (WebElement button : driver.findElements(CCSUsersPageLocators.CONFIRM_DIALOG_BUTTON)) {
            try {
                if (button.isDisplayed()) {
                    jsClick(button);
                }
            } catch (StaleElementReferenceException ignored) {
                // Dialog closed on its own
            }
        }
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> d.findElements(CCSUsersPageLocators.CONFIRM_DIALOG_BUTTON)
                    .stream().noneMatch(WebElement::isDisplayed));
        } catch (TimeoutException e) {
            System.out.println("[WARN] Dialog still shown");
        }
    }

    // ==================== Add User Form ====================

    public void clickAddUser() {
        ExtentReportManager.logStep("Click Add User");
        wait.until(ExpectedConditions.elementToBeClickable(CCSUsersPageLocators.ADD_USER_BUTTON)).click();
        wait.until(ExpectedConditions.urlContains(ADD_USER_PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CCSUsersPageLocators.NAME_INPUT));
    }

    public boolean isOnAddUserForm() {
        return driver.getCurrentUrl().contains(ADD_USER_PATH);
    }

    /** Clears the field, types {@code value} and tabs out so the field is validated. */
    public void enterText(By field, String value) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(field));
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);
    }

    public void enterName(String name) {
        ExtentReportManager.logStep("Enter Name: " + name);
        enterText(CCSUsersPageLocators.NAME_INPUT, name);
    }

    public void enterContactNumber(String number) {
        ExtentReportManager.logStep("Enter Contact Number: " + number);
        enterText(CCSUsersPageLocators.CONTACT_NUMBER_INPUT, number);
    }

    public void enterEmail(String email) {
        ExtentReportManager.logStep("Enter Email: " + email);
        enterText(CCSUsersPageLocators.EMAIL_INPUT, email);
    }

    public String getFieldValue(By field) {
        String value = driver.findElement(field).getAttribute("value");
        return value == null ? "" : value;
    }

    /** Angular marks a field that fails its validators with the ng-invalid class. */
    public boolean isFieldInvalid(By field) {
        return driver.findElement(field).getAttribute("class").contains("ng-invalid");
    }

    /** Validation message shown under the field, or "" when there is none. */
    public String getFieldError(By field) {
        Set<String> messages = new LinkedHashSet<>();
        for (WebElement e : driver.findElement(field).findElements(By.xpath(CCSUsersPageLocators.FIELD_ERROR_RELATIVE))) {
            try {
                if (e.isDisplayed() && !e.getText().trim().isEmpty()) {
                    messages.add(e.getText().trim());
                }
            } catch (StaleElementReferenceException ignored) {
                // Message re-rendered meanwhile
            }
        }
        return String.join(" | ", messages);
    }

    public String getMaxLength(By field) {
        return driver.findElement(field).getAttribute("maxlength");
    }

    public String getSelectedOption(By select) {
        return new Select(driver.findElement(select)).getFirstSelectedOption().getText().trim();
    }

    public List<String> getOptions(By select) {
        List<String> options = new ArrayList<>();
        for (WebElement option : new Select(driver.findElement(select)).getOptions()) {
            options.add(option.getText().trim());
        }
        return options;
    }

    public void selectRole(String role) {
        ExtentReportManager.logStep("Select Role: " + role);
        new Select(wait.until(ExpectedConditions.elementToBeClickable(CCSUsersPageLocators.ROLE_SELECT)))
                .selectByVisibleText(role);
    }

    public boolean isChecked(By checkbox) {
        return driver.findElement(checkbox).isSelected();
    }

    /** Call Mode is a pair of checkboxes (WebRTC / Phone); ticks or unticks each as requested. */
    public void setCallMode(boolean webrtc, boolean phone) {
        ExtentReportManager.logStep("Set Call Mode - WebRTC: " + webrtc + ", Phone: " + phone);
        setChecked(CCSUsersPageLocators.CALL_MODE_WEBRTC, webrtc);
        setChecked(CCSUsersPageLocators.CALL_MODE_PHONE, phone);
    }

    private void setChecked(By checkbox, boolean checked) {
        WebElement box = wait.until(ExpectedConditions.presenceOfElementLocated(checkbox));
        if (box.isSelected() != checked) {
            jsClick(box);
            wait.until(d -> d.findElement(checkbox).isSelected() == checked);
        }
    }

    public WebElement getSaveButton() {
        return getVisibleButton(CCSUsersPageLocators.SAVE_BUTTONS);
    }

    /** Clicks the visible Save button once. */
    public void clickSave() {
        ExtentReportManager.logStep("Click Save");
        jsClick(getSaveButton());
    }

    public void clickCancel() {
        ExtentReportManager.logStep("Click Cancel");
        jsClick(getVisibleButton(CCSUsersPageLocators.CANCEL_BUTTONS));
    }

    private WebElement getVisibleButton(By buttons) {
        return wait.until(d -> d.findElements(buttons).stream().filter(WebElement::isDisplayed).findFirst().orElse(null));
    }

    // A sticky footer covers the lower part of the page, so a normal click on Save/Cancel is intercepted
    private void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();", element);
    }

    /**
     * Waits after Save until the form is left (user created) or a notification is shown.
     * Returns the notification text, or "" when there was none.
     */
    public String waitForSaveOutcome() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(d -> !isOnAddUserForm() || !getNotifications().isEmpty());
        } catch (TimeoutException e) {
            System.out.println("[WARN] No navigation or notification after Save");
        }
        String message = getNotifications();
        System.out.println("[INFO] After Save - URL: " + CCSLivePage.stripQuery(driver.getCurrentUrl()) + " Message: " + message);
        return message;
    }

    /** Short wait for a notification (e.g. right after Save on an invalid form); "" when none appears. */
    public String waitForNotification(int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds)).until(d -> !getNotifications().isEmpty());
        } catch (TimeoutException e) {
            // No notification
        }
        return getNotifications();
    }

    public String getNotifications() {
        Set<String> messages = new LinkedHashSet<>();
        for (WebElement e : driver.findElements(CCSUsersPageLocators.NOTIFICATIONS)) {
            try {
                if (e.isDisplayed() && !e.getText().trim().isEmpty()) {
                    messages.add(e.getText().trim().replaceAll("\\s+", " "));
                }
            } catch (StaleElementReferenceException ignored) {
                // Notification closed meanwhile
            }
        }
        return String.join(" | ", messages);
    }

    /** True when the message says the agent/user limit of the account is reached. */
    public static boolean isAgentLimitMessage(String message) {
        return message != null && message.toLowerCase().matches(".*((agent|user)s? limit|limit (reached|exceeded)|maximum.*(agent|user)).*");
    }

    /** True when the message says the email is already registered. */
    public static boolean isDuplicateEmailMessage(String message) {
        return message != null && message.toLowerCase().matches(".*(already|exist|registered|duplicate|in use).*");
    }
}
