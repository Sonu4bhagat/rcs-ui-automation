package pages.ccs;

import locators.ccs.CCSCampaignPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ExtentReportManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Page Object for CCS-Live Campaign: Campaign list, Add Campaign form and the Schedule Campaign (Calendar) dialog.
 * The Campaign list has the same table, search controls and toast notifications as the Users list, so those
 * parts are delegated to CCSUsersPage instead of being duplicated.
 */
public class CCSCampaignPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final CCSUsersPage listPage;
    private static final int DEFAULT_TIMEOUT = 30;
    private static final int HEADLESS_TIMEOUT = 45;

    public static final String CAMPAIGN_LIST_PATH = "/client/campaign";
    public static final String ADD_CAMPAIGN_PATH = "/client/campaign/create-campaign";

    public CCSCampaignPage(WebDriver driver) {
        this.driver = driver;
        this.listPage = new CCSUsersPage(driver);
        int timeout = base.DriverFactory.isHeadlessModeEnabled() ? HEADLESS_TIMEOUT : DEFAULT_TIMEOUT;
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    // ==================== Campaign List ====================

    public void openCampaignTab() {
        ExtentReportManager.logStep("Click Campaign in the CCS-Live sidebar");
        jsClick(wait.until(ExpectedConditions.presenceOfElementLocated(CCSCampaignPageLocators.CAMPAIGN_MENU)));
        waitForCampaignList();
    }

    public void waitForCampaignList() {
        wait.until(d -> isOnCampaignList());
        wait.until(ExpectedConditions.visibilityOfElementLocated(CCSCampaignPageLocators.ADD_CAMPAIGN_BUTTON));
        System.out.println("[INFO] CCS Campaign list loaded");
    }

    public boolean isOnCampaignList() {
        return CCSLivePage.stripQuery(driver.getCurrentUrl()).endsWith(CAMPAIGN_LIST_PATH);
    }

    public boolean isOnAddCampaignForm() {
        return driver.getCurrentUrl().contains(ADD_CAMPAIGN_PATH);
    }

    public boolean isDisplayed(By locator) {
        return listPage.isDisplayed(locator);
    }

    public List<String> getTableHeaders() {
        return listPage.getTableHeaders();
    }

    /** Searches the Campaign list by name and returns the number of rows shown. */
    public int searchCampaignsByName(String name) {
        return listPage.searchUsers(CCSCampaignPageLocators.SEARCH_BY_CAMPAIGN_NAME, name);
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

    /**
     * Deletes the campaign named {@code name} (exactly one match expected) and confirms the dialog.
     * Returns the messages shown (confirmation dialog | result).
     */
    public String deleteCampaign(String name) {
        ExtentReportManager.logStep("Delete campaign " + name);
        if (searchCampaignsByName(name) != 1) {
            throw new IllegalStateException("Expected exactly one campaign named " + name);
        }
        clickRowButton(CCSCampaignPageLocators.getRowDeleteButton(1));
        String confirmText = listPage.confirmDialog(10);
        String result = listPage.waitForNotification(10);
        listPage.closeDialogIfShown();
        return confirmText + " | " + result;
    }

    // ==================== Add Campaign Form ====================

    public void clickAddCampaign() {
        ExtentReportManager.logStep("Click Add Campaign");
        wait.until(ExpectedConditions.elementToBeClickable(CCSCampaignPageLocators.ADD_CAMPAIGN_BUTTON)).click();
        wait.until(ExpectedConditions.urlContains(ADD_CAMPAIGN_PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CCSCampaignPageLocators.NAME_INPUT));
    }

    /** Clears the field, types {@code value} and tabs out so the field is validated. */
    public void enterText(By field, String value) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(field));
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);
    }

    public void enterName(String name) {
        ExtentReportManager.logStep("Enter Campaign Name: " + name);
        enterText(CCSCampaignPageLocators.NAME_INPUT, name);
    }

    public void enterDescription(String description) {
        ExtentReportManager.logStep("Enter Campaign Description: " + description);
        enterText(CCSCampaignPageLocators.DESCRIPTION_INPUT, description);
    }

    public String getFieldValue(By field) {
        String value = driver.findElement(field).getAttribute("value");
        return value == null ? "" : value;
    }

    public String getAttribute(By field, String attribute) {
        return driver.findElement(field).getAttribute(attribute);
    }

    /** Character counter under Campaign Name, e.g. "12/50". */
    public String getNameCounter() {
        return driver.findElement(CCSCampaignPageLocators.NAME_COUNTER).getText().trim();
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

    /** Virtual numbers assigned to the account, as offered by the Virtual Number multi-select. */
    public List<String> getVirtualNumberOptions() {
        openVirtualNumberDropdown();
        List<String> numbers = new ArrayList<>();
        for (WebElement option : driver.findElements(CCSCampaignPageLocators.VIRTUAL_NUMBER_OPTIONS)) {
            numbers.add(option.getText().trim());
        }
        closeVirtualNumberDropdown();
        return numbers;
    }

    public void selectVirtualNumber(String number) {
        ExtentReportManager.logStep("Select Virtual Number: " + number);
        openVirtualNumberDropdown();
        WebElement option = findVirtualNumberOption(number);
        if (option == null) {
            closeVirtualNumberDropdown();
            throw new NoSuchElementException("Virtual number not offered: " + number);
        }
        if (!option.findElement(By.tagName("input")).isSelected()) {
            jsClick(option);
        }
        closeVirtualNumberDropdown();
    }

    public boolean isVirtualNumberSelected(String number) {
        WebElement option = findVirtualNumberOption(number);
        return option != null && option.findElement(By.tagName("input")).isSelected();
    }

    private WebElement findVirtualNumberOption(String number) {
        for (WebElement option : driver.findElements(CCSCampaignPageLocators.VIRTUAL_NUMBER_OPTIONS)) {
            if (option.getAttribute("textContent").trim().equals(number)) {
                return option;
            }
        }
        return null;
    }

    private void openVirtualNumberDropdown() {
        jsClick(wait.until(ExpectedConditions.presenceOfElementLocated(CCSCampaignPageLocators.VIRTUAL_NUMBER_DROPDOWN_BUTTON)));
        wait.until(d -> d.findElements(CCSCampaignPageLocators.VIRTUAL_NUMBER_OPTIONS).stream().anyMatch(WebElement::isDisplayed));
    }

    private void closeVirtualNumberDropdown() {
        // Clicking the dropdown button again closes the list
        jsClick(driver.findElement(CCSCampaignPageLocators.VIRTUAL_NUMBER_DROPDOWN_BUTTON));
    }

    public WebElement getSaveButton() {
        return wait.until(d -> d.findElements(CCSCampaignPageLocators.SAVE_BUTTONS).stream()
                .filter(WebElement::isDisplayed).findFirst().orElse(null));
    }

    /** Clicks the visible Save button once (after any earlier toast is gone). */
    public void clickSave() {
        waitForNotificationsToClear();
        ExtentReportManager.logStep("Click Save");
        jsClick(getSaveButton());
    }

    /**
     * Waits after Save until the Add Campaign form is left or a notification is shown.
     * Returns the notification text, or "" when there was none.
     */
    public String waitForSaveOutcome() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(d -> !isOnAddCampaignForm() || !getNotifications().isEmpty());
        } catch (TimeoutException e) {
            System.out.println("[WARN] No navigation or notification after Save");
        }
        String message = getNotifications();
        System.out.println("[INFO] After Save - URL: " + CCSLivePage.stripQuery(driver.getCurrentUrl()) + " Message: " + message);
        // "Campaign created successfully" is a SweetAlert popup that covers the page until it is confirmed
        listPage.closeDialogIfShown();
        return message;
    }

    // ==================== Schedule Campaign (Calendar) dialog ====================

    public void clickCalendarAtRow(int rowIndex) {
        ExtentReportManager.logStep("Click the Calendar icon of campaign row " + rowIndex);
        listPage.closeDialogIfShown(); // a leftover success popup would cover the schedule dialog
        clickRowButton(CCSCampaignPageLocators.getRowCalendarButton(rowIndex));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CCSCampaignPageLocators.SCHEDULE_DATE_RANGE_INPUT));
        // The dialog animates in and then loads the campaign's schedule (the date range is pre-filled); a click
        // before that is lost, so wait until the date range is filled in
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> !getDateRangeValue().isEmpty());
        } catch (TimeoutException e) {
            System.out.println("[WARN] Schedule dialog shows no date range yet");
        }
    }

    public boolean isScheduleDialogOpen() {
        return !driver.findElements(CCSCampaignPageLocators.SCHEDULE_DIALOG).isEmpty();
    }

    /** Picks {@code from} to {@code to} in the date range picker (the field does not accept typed dates). */
    public void selectDateRange(LocalDate from, LocalDate to) {
        ExtentReportManager.logStep("Select date range " + from + " to " + to);
        // A new campaign's schedule opens with today's date pre-filled - nothing to pick then
        if (getDateRangeValue().equals(from + " - " + to)) {
            System.out.println("[INFO] Date range already " + getDateRangeValue());
            return;
        }
        WebElement dateInput = driver.findElement(CCSCampaignPageLocators.SCHEDULE_DATE_RANGE_INPUT);
        try {
            dateInput.click();
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(CCSCampaignPageLocators.DATE_PICKER));
        } catch (TimeoutException | ElementClickInterceptedException e) {
            new Actions(driver).moveToElement(dateInput).click().perform();
            wait.until(ExpectedConditions.visibilityOfElementLocated(CCSCampaignPageLocators.DATE_PICKER));
        }
        clickDatePickerDay(from);
        clickDatePickerDay(to);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(CCSCampaignPageLocators.DATE_PICKER));
    }

    private void clickDatePickerDay(LocalDate date) {
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String year = String.valueOf(date.getYear());
        for (int i = 0; i < 24; i++) {
            String shownMonth = driver.findElement(CCSCampaignPageLocators.DATE_PICKER_MONTH).getText().trim();
            String shownYear = driver.findElement(CCSCampaignPageLocators.DATE_PICKER_YEAR).getText().trim();
            if (shownMonth.equalsIgnoreCase(month) && shownYear.equals(year)) {
                break;
            }
            LocalDate shown = LocalDate.parse(shownYear + "-" + String.format("%02d",
                    java.time.Month.valueOf(shownMonth.toUpperCase()).getValue()) + "-01");
            jsClick(driver.findElement(shown.isBefore(date.withDayOfMonth(1))
                    ? CCSCampaignPageLocators.DATE_PICKER_NEXT : CCSCampaignPageLocators.DATE_PICKER_PREVIOUS));
        }
        jsClick(wait.until(ExpectedConditions.presenceOfElementLocated(CCSCampaignPageLocators.getDatePickerDay(date.getDayOfMonth()))));
    }

    public String getDateRangeValue() {
        return getFieldValue(CCSCampaignPageLocators.SCHEDULE_DATE_RANGE_INPUT).trim().replaceAll("\\s+", " ");
    }

    /** Turns the weekday on (Mon = 0 ... Sun = 6) and sets its start and end time, e.g. "12:00 AM" / "11:59 PM". */
    public void setWeekdaySchedule(int weekdayIndex, String startTime, String endTime) {
        ExtentReportManager.logStep("Schedule weekday " + weekdayIndex + ": " + startTime + " - " + endTime);
        WebElement toggle = driver.findElement(CCSCampaignPageLocators.getWeekdayToggle(weekdayIndex));
        if (!toggle.isSelected()) {
            jsClick(toggle);
            wait.until(d -> d.findElement(CCSCampaignPageLocators.getWeekdayToggle(weekdayIndex)).isSelected());
        }
        List<WebElement> times = driver.findElements(CCSCampaignPageLocators.getWeekdayTimeSelects(weekdayIndex));
        selectNgOption(times.get(0), startTime);
        selectNgOption(times.get(1), endTime);
    }

    public boolean isWeekdayEnabled(int weekdayIndex) {
        return driver.findElement(CCSCampaignPageLocators.getWeekdayToggle(weekdayIndex)).isSelected();
    }

    /** Selected start and end time of the weekday, e.g. ["12:00 AM", "11:59 PM"]. */
    public List<String> getWeekdayTimes(int weekdayIndex) {
        List<String> values = new ArrayList<>();
        for (WebElement select : driver.findElements(CCSCampaignPageLocators.getWeekdayTimeSelects(weekdayIndex))) {
            List<WebElement> label = select.findElements(By.xpath(".//span[contains(@class, 'ng-value-label')]"));
            values.add(label.isEmpty() ? "" : label.get(0).getText().trim());
        }
        return values;
    }

    // ng-select opens on a real mouse-down (a JavaScript click does not open it); typing filters the options
    private void selectNgOption(WebElement ngSelect, String optionText) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", ngSelect);
        new Actions(driver).moveToElement(ngSelect).click().perform();
        WebElement search = ngSelect.findElement(By.xpath(".//input"));
        search.sendKeys(optionText);
        WebElement option = wait.until(d -> d.findElements(CCSCampaignPageLocators.NG_SELECT_OPTIONS).stream()
                .filter(o -> o.getText().trim().equals(optionText)).findFirst().orElse(null));
        option.click();
        // The options read "12:00 AM" but the selected value may be shown as "00:00" - compare as times
        java.time.LocalTime wanted = parseTime(optionText);
        wait.until(d -> ngSelect.findElements(By.xpath(".//span[contains(@class, 'ng-value-label')]")).stream()
                .anyMatch(l -> wanted.equals(parseTime(l.getText().trim()))));
    }

    /** Parses "12:00 AM" or "00:00" style times; null when the text is not a time. */
    public static java.time.LocalTime parseTime(String text) {
        for (String pattern : new String[] { "h:mm a", "hh:mm a", "H:mm", "HH:mm" }) {
            try {
                return java.time.LocalTime.parse(text.trim().toUpperCase(Locale.ENGLISH),
                        java.time.format.DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
            } catch (java.time.format.DateTimeParseException ignored) {
                // try the next pattern
            }
        }
        return null;
    }

    /** Saves the schedule and returns the notification shown (the dialog closes on success). */
    public String saveSchedule() {
        waitForNotificationsToClear();
        ExtentReportManager.logStep("Click Save in Schedule Campaign");
        jsClick(driver.findElement(CCSCampaignPageLocators.SCHEDULE_SAVE_BUTTON));
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20)).until(d -> !isScheduleDialogOpen() || !getNotifications().isEmpty());
        } catch (TimeoutException e) {
            System.out.println("[WARN] Schedule dialog still open and no notification after Save");
        }
        String message = getNotifications();
        System.out.println("[INFO] After schedule Save - dialog open: " + isScheduleDialogOpen() + " Message: " + message);
        listPage.closeDialogIfShown();
        return message;
    }

    // Validation toasts on this page do not always fade on their own: close any shown toast with its close button
    // and wait until it is gone, so the next message read is only the response to the next action
    private void waitForNotificationsToClear() {
        for (WebElement close : driver.findElements(CCSCampaignPageLocators.TOAST_CLOSE_BUTTONS)) {
            try {
                jsClick(close);
            } catch (StaleElementReferenceException ignored) {
                // Toast closed on its own meanwhile
            }
        }
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> getNotifications().isEmpty());
        } catch (TimeoutException e) {
            System.out.println("[WARN] Earlier notification still shown: " + getNotifications());
        }
    }

    // The list re-renders after a search, so a row button found a moment ago can be stale: find it again and retry
    private void clickRowButton(By button) {
        wait.ignoring(StaleElementReferenceException.class).until(d -> {
            jsClick(d.findElement(button));
            return true;
        });
    }

    // A sticky footer covers the lower part of CCS-Live pages, so a normal click is intercepted
    private void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();", element);
    }
}
