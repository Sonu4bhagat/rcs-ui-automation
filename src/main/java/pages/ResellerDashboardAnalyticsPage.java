package pages;

import locators.ResellerDashboardAnalyticsPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the Reseller Dashboard's "Analytics" tab.
 */
public class ResellerDashboardAnalyticsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public ResellerDashboardAnalyticsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void clickAnalyticsTab() {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardAnalyticsPageLocators.ANALYTICS_TAB));
        try {
            tab.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
        }
    }

    public boolean isElementVisible(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getServiceTabTexts() {
        List<String> texts = new ArrayList<>();
        try {
            List<WebElement> tabs = driver.findElements(ResellerDashboardAnalyticsPageLocators.SERVICE_TABS);
            for (WebElement t : tabs) {
                String text = t.getText().trim();
                if (!text.isEmpty()) {
                    texts.add(text);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting service tab texts: " + e.getMessage());
        }
        return texts;
    }

    public boolean isErrorMessageDisplayed() {
        return isElementVisible(ResellerDashboardAnalyticsPageLocators.ERROR_MESSAGE);
    }

    public boolean isAccessNotAllowedMessageDisplayed() {
        return isElementVisible(ResellerDashboardAnalyticsPageLocators.ACCESS_NOT_ALLOWED_MESSAGE);
    }

    public boolean isCustomerFilterVisible() {
        return isElementVisible(ResellerDashboardAnalyticsPageLocators.CUSTOMER_FILTER_DROPDOWN);
    }

    /**
     * The "Select Customer" filter is an ng-select combobox (not a native
     * select) - clicking it opens a floating ng-option list.
     */
    public List<String> getCustomerFilterOptionTexts() {
        List<String> texts = new ArrayList<>();
        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    ResellerDashboardAnalyticsPageLocators.CUSTOMER_FILTER_DROPDOWN));
            dropdown.click();
            Thread.sleep(800);
            List<WebElement> options = driver.findElements(By.xpath("//div[contains(@class,'ng-option')]"));
            for (WebElement o : options) {
                String t = o.getText().trim();
                if (!t.isEmpty()) {
                    texts.add(t);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading customer filter options: " + e.getMessage());
        }
        return texts;
    }

    /** Selects the ng-option whose visible text matches optionText. Assumes the dropdown is already open. */
    public boolean selectOpenCustomerOptionByText(String optionText) {
        try {
            List<WebElement> options = driver.findElements(By.xpath("//div[contains(@class,'ng-option')]"));
            for (WebElement o : options) {
                if (o.getText().trim().equals(optionText)) {
                    try {
                        o.click();
                    } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", o);
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error selecting customer option '" + optionText + "': " + e.getMessage());
        }
        return false;
    }

    public String getSelectedCustomerText() {
        try {
            return driver.findElement(By.xpath(
                    "//ng-select[@labelforid='select-customer']//div[contains(@class,'ng-value')]")).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Clears the selected customer via the ng-select's built-in clear ("x") icon. */
    public void clearCustomerFilter() {
        try {
            WebElement clearIcon = wait.until(ExpectedConditions.elementToBeClickable(
                    ResellerDashboardAnalyticsPageLocators.CUSTOMER_FILTER_CLEAR));
            clearIcon.click();
        } catch (Exception e) {
            System.out.println("Error clearing customer filter: " + e.getMessage());
        }
    }

    public void clickServiceTab(String serviceName) {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardAnalyticsPageLocators.getServiceTabByName(serviceName)));
        try {
            tab.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
        }
    }

    public boolean isServiceTabActive(String serviceName) {
        try {
            WebElement tab = driver.findElement(ResellerDashboardAnalyticsPageLocators.getServiceTabByName(serviceName));
            String selected = tab.getDomAttribute("aria-selected");
            return "true".equals(selected);
        } catch (Exception e) {
            System.out.println("Error checking active state for tab '" + serviceName + "': " + e.getMessage());
            return false;
        }
    }

    public boolean hasAllServiceTabs() {
        List<String> tabs = getServiceTabTexts();
        for (String service : ResellerDashboardAnalyticsPageLocators.ALL_SERVICE_NAMES) {
            if (!tabs.contains(service)) {
                return false;
            }
        }
        return true;
    }

    public String getDateRangeLabel() {
        try {
            return driver.findElement(ResellerDashboardAnalyticsPageLocators.CALENDAR_FILTER_INPUT)
                    .getDomAttribute("ng-reflect-model");
        } catch (Exception e) {
            System.out.println("Error reading date range label: " + e.getMessage());
            return "";
        }
    }

    /**
     * Opens the date-range picker. Confirmed live: neither the calendar icon
     * nor the visible label text is the real click target - the readonly
     * <input class="datepicker-dropdown-input"> itself is.
     */
    public void openCalendarFilter() {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardAnalyticsPageLocators.CALENDAR_FILTER_INPUT));
        try {
            input.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input);
        }
    }

    /** Selects a preset range (e.g. "Last 7 Days", "Last 30 Days") from the open calendar panel. */
    public boolean selectCalendarPreset(String presetText) {
        try {
            List<WebElement> presets = driver.findElements(ResellerDashboardAnalyticsPageLocators.CALENDAR_PRESET_OPTIONS);
            for (WebElement p : presets) {
                if (p.getText().trim().equals(presetText)) {
                    p.click();
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error selecting calendar preset '" + presetText + "': " + e.getMessage());
        }
        return false;
    }
}
