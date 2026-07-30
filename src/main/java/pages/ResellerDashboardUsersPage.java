package pages;

import locators.ResellerDashboardUsersPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the Reseller Dashboard's "Users" tab.
 */
public class ResellerDashboardUsersPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public ResellerDashboardUsersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ==================== Users Tab ====================

    public void clickUsersTab() {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardUsersPageLocators.USERS_TAB));
        try {
            tab.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
        }
    }

    public boolean isUsersTabSelected() {
        try {
            if (isElementVisible(ResellerDashboardUsersPageLocators.USERS_HEADER)) {
                return true;
            }
            return isElementVisible(ResellerDashboardUsersPageLocators.USERS_TABLE_HEADERS)
                    || isElementVisible(ResellerDashboardUsersPageLocators.NO_DATA_MESSAGE);
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageHeaderText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    ResellerDashboardUsersPageLocators.USERS_HEADER)).getText().trim();
        } catch (Exception e) {
            System.out.println("Page header not found: " + e.getMessage());
            return "";
        }
    }

    // ==================== Users Listing ====================

    public void waitForUserData() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(ResellerDashboardUsersPageLocators.USERS_TABLE_ROWS),
                    ExpectedConditions.visibilityOfElementLocated(ResellerDashboardUsersPageLocators.NO_DATA_MESSAGE)));
        } catch (Exception e) {
            System.out.println("Timed out waiting for user data: " + e.getMessage());
        }
    }

    public boolean hasUsers() {
        try {
            return !driver.findElements(ResellerDashboardUsersPageLocators.USERS_TABLE_ROWS).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNoDataMessageDisplayed() {
        return isElementVisible(ResellerDashboardUsersPageLocators.NO_DATA_MESSAGE);
    }

    public int getUserRowCount() {
        try {
            return driver.findElements(ResellerDashboardUsersPageLocators.USERS_TABLE_ROWS).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<String> getUsersTableHeaders() {
        List<String> headers = new ArrayList<>();
        try {
            List<WebElement> headerElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    ResellerDashboardUsersPageLocators.USERS_TABLE_HEADERS));
            for (WebElement header : headerElements) {
                String text = header.getText().trim();
                if (!text.isEmpty()) {
                    headers.add(text);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting users table headers: " + e.getMessage());
        }
        return headers;
    }

    /**
     * Reads a users table cell's text, waiting for it to have non-empty text
     * (not just be visible) - Angular can render the row skeleton with an
     * empty cell for a moment before async-populating it (same issue found
     * and fixed on the My Wallets tab).
     */
    public String getUserCellText(int row, int column) {
        By locator = ResellerDashboardUsersPageLocators.getUserCellByRowAndColumn(row, column);
        try {
            wait.until(d -> {
                try {
                    String text = d.findElement(locator).getText().trim();
                    // The table briefly renders a "no data available" placeholder
                    // row before Angular populates real rows - a plain non-empty
                    // check gets satisfied by that transient placeholder text, so
                    // it must be excluded explicitly (same class of issue as the
                    // empty-cell-skeleton bug found on the My Wallets tab).
                    return !text.isEmpty() && !text.toLowerCase().contains("no data available");
                } catch (Exception e) {
                    return false;
                }
            });
            return driver.findElement(locator).getText().trim();
        } catch (Exception e) {
            System.out.println("Could not read user cell (row=" + row + ", column=" + column + "): "
                    + e.getMessage());
            return "";
        }
    }

    public String getUserStatus(int row) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    ResellerDashboardUsersPageLocators.getUserStatusByRow(row))).getText().trim();
        } catch (Exception e) {
            System.out.println("Could not read user status for row " + row + ": " + e.getMessage());
            return "N/A";
        }
    }

    // ==================== Pagination ====================

    public boolean isPaginationAvailable() {
        return isElementVisible(ResellerDashboardUsersPageLocators.PAGINATION_NEXT)
                || isElementVisible(ResellerDashboardUsersPageLocators.PAGINATION_PREVIOUS);
    }

    public boolean clickNextPage() {
        try {
            WebElement next = driver.findElement(ResellerDashboardUsersPageLocators.PAGINATION_NEXT);
            if (!next.isEnabled()) {
                return false;
            }
            next.click();
            waitForUserData();
            return true;
        } catch (Exception e) {
            System.out.println("Failed to click Next page: " + e.getMessage());
            return false;
        }
    }

    public boolean clickPreviousPage() {
        try {
            WebElement prev = driver.findElement(ResellerDashboardUsersPageLocators.PAGINATION_PREVIOUS);
            if (!prev.isEnabled()) {
                return false;
            }
            prev.click();
            waitForUserData();
            return true;
        } catch (Exception e) {
            System.out.println("Failed to click Previous page: " + e.getMessage());
            return false;
        }
    }

    // ==================== Search ====================

    public void searchUser(String searchText) {
        try {
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    ResellerDashboardUsersPageLocators.SEARCH_INPUT));
            searchInput.clear();
            searchInput.sendKeys(searchText);
            System.out.println("Searched for: " + searchText);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            System.out.println("Error entering search text: " + e.getMessage());
            throw new RuntimeException("Failed to search user", e);
        }
    }

    public void clearSearch() {
        try {
            WebElement searchInput = driver.findElement(ResellerDashboardUsersPageLocators.SEARCH_INPUT);
            // WebElement.clear() doesn't fire Angular's (input) event, so the
            // component's bound model never sees the change and the filtered
            // results stay applied - simulate real keystrokes instead.
            searchInput.click();
            searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            searchInput.sendKeys(Keys.DELETE);
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Error clearing search: " + e.getMessage());
        }
    }

    // ==================== Filters ====================

    public void applyActiveStatusFilter() {
        try {
            WebElement openBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    ResellerDashboardUsersPageLocators.FILTER_BUTTON));
            openBtn.click();
            Thread.sleep(500);
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                    ResellerDashboardUsersPageLocators.STATUS_FILTER_ACTIVE));
            option.click();
            Thread.sleep(300);
            try {
                WebElement applyBtn = driver.findElement(ResellerDashboardUsersPageLocators.APPLY_FILTER_BUTTON);
                if (applyBtn.isDisplayed()) {
                    applyBtn.click();
                }
            } catch (Exception e) {
                // Some filters auto-apply without a separate Apply button
            }
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("Error applying filter: " + e.getMessage());
            throw new RuntimeException("Failed to apply filter", e);
        }
    }

    public void clickClearAllFilters() {
        try {
            WebElement clearBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    ResellerDashboardUsersPageLocators.CLEAR_ALL_FILTERS_BUTTON));
            clearBtn.click();
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("Clear All button not found, refreshing as fallback: " + e.getMessage());
            driver.navigate().refresh();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ==================== Create User ====================

    public boolean isCreateUserButtonVisible() {
        return isElementVisible(ResellerDashboardUsersPageLocators.CREATE_USER_BUTTON);
    }

    public void clickCreateUserButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardUsersPageLocators.CREATE_USER_BUTTON));
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    // ==================== Per-row Actions ====================

    public boolean isUpdatePlanButtonPresent(int row) {
        try {
            return driver.findElement(ResellerDashboardUsersPageLocators.getUpdatePlanButtonByRow(row)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickUpdatePlanButton(int row) {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardUsersPageLocators.getUpdatePlanButtonByRow(row)));
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public boolean isAddCreditButtonPresent(int row) {
        try {
            return driver.findElement(ResellerDashboardUsersPageLocators.getAddCreditButtonByRow(row)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickAddCreditButton(int row) {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardUsersPageLocators.getAddCreditButtonByRow(row)));
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public boolean isServiceNavButtonEnabled(int row) {
        try {
            WebElement btn = driver.findElement(ResellerDashboardUsersPageLocators.getServiceNavButtonByRow(row));
            return btn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickServiceNavButton(int row) {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardUsersPageLocators.getServiceNavButtonByRow(row)));
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    // ==================== Utility ====================

    private boolean isElementVisible(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500);
        } catch (Exception e) {
            // Ignore scroll errors
        }
    }
}
