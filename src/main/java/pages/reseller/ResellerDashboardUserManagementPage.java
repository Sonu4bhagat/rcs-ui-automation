package pages.reseller;

import locators.reseller.ResellerDashboardUserManagementPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the Reseller Dashboard's "User Management" sidebar section
 * (User List -> per-org wallet drill-down -> services -> organization
 * details -> profile -> roles).
 */
public class ResellerDashboardUserManagementPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public ResellerDashboardUserManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ==================== Sidebar Navigation ====================

    /**
     * Confirmed live: the submenu-expand animation after clicking "User
     * Management" occasionally isn't settled by the time "User List" is
     * clicked right after, causing that click to silently not register (same
     * class of timing issue found repeatedly elsewhere this session). Retry
     * with a fresh lookup and an explicit URL wait rather than a fixed sleep.
     */
    public void navigateToUserList() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.SIDEBAR_USER_MANAGEMENT);

        boolean navigated = false;
        for (int attempt = 1; attempt <= 3 && !navigated; attempt++) {
            try {
                WebElement userListSpan = wait.until(ExpectedConditions.elementToBeClickable(
                        ResellerDashboardUserManagementPageLocators.SIDEBAR_USER_LIST));
                try {
                    userListSpan.click();
                } catch (Exception e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", userListSpan);
                }
                new WebDriverWait(driver, Duration.ofSeconds(6))
                        .until(ExpectedConditions.urlContains("user-management"));
                navigated = true;
            } catch (Exception e) {
                System.out.println("'User List' click did not navigate on attempt " + attempt + ", retrying...");
            }
        }
        if (!navigated) {
            throw new RuntimeException("Failed to navigate to User List after 3 attempts");
        }
        waitForTableData();
    }

    private void clickAndWait(By locator) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        try {
            el.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    // ==================== User List ====================

    public void waitForTableData() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(ResellerDashboardUserManagementPageLocators.USER_LIST_TABLE_ROWS),
                    ExpectedConditions.visibilityOfElementLocated(ResellerDashboardUserManagementPageLocators.NO_DATA_MESSAGE)));
        } catch (Exception e) {
            System.out.println("Timed out waiting for table data: " + e.getMessage());
        }
    }

    public int getRowCount() {
        try {
            return driver.findElements(ResellerDashboardUserManagementPageLocators.USER_LIST_TABLE_ROWS).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isNoDataMessageDisplayed() {
        return isElementVisible(ResellerDashboardUserManagementPageLocators.NO_DATA_MESSAGE);
    }

    public List<String> getTableHeaders() {
        List<String> headers = new ArrayList<>();
        try {
            List<WebElement> headerElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    ResellerDashboardUserManagementPageLocators.USER_LIST_TABLE_HEADERS));
            for (WebElement header : headerElements) {
                String text = header.getText().trim();
                if (!text.isEmpty()) {
                    headers.add(text);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting table headers: " + e.getMessage());
        }
        return headers;
    }

    /**
     * Reads a cell's text, waiting for non-empty text that isn't the
     * transient "no data available" placeholder (Angular renders an empty
     * skeleton row before async-populating it - same class of issue found
     * repeatedly across the Users/My Wallets tabs).
     */
    public String getCellText(int row, int column) {
        By locator = ResellerDashboardUserManagementPageLocators.getUserListCellByRowAndColumn(row, column);
        try {
            wait.until(d -> {
                try {
                    String text = d.findElement(locator).getText().trim();
                    return !text.isEmpty() && !text.toLowerCase().contains("no data available");
                } catch (Exception e) {
                    return false;
                }
            });
            return driver.findElement(locator).getText().trim();
        } catch (Exception e) {
            System.out.println("Could not read cell (row=" + row + ", column=" + column + "): " + e.getMessage());
            return "";
        }
    }

    public void searchByName(String text) {
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    ResellerDashboardUserManagementPageLocators.SEARCH_INPUT));
            input.clear();
            input.sendKeys(text);
            Thread.sleep(1500);
        } catch (Exception e) {
            throw new RuntimeException("Failed to search by name", e);
        }
    }

    public void clearSearch() {
        try {
            WebElement input = driver.findElement(ResellerDashboardUserManagementPageLocators.SEARCH_INPUT);
            input.click();
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            input.sendKeys(Keys.DELETE);
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Error clearing search: " + e.getMessage());
        }
    }

    public void applyActiveStatusFilter() {
        try {
            clickAndWait(ResellerDashboardUserManagementPageLocators.FILTER_BUTTON);
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                    ResellerDashboardUserManagementPageLocators.STATUS_FILTER_ACTIVE));
            option.click();
            Thread.sleep(300);
            try {
                WebElement applyBtn = driver.findElement(ResellerDashboardUserManagementPageLocators.APPLY_FILTER_BUTTON);
                if (applyBtn.isDisplayed()) {
                    applyBtn.click();
                }
            } catch (Exception e) {
                // Some filters auto-apply without a separate Apply button
            }
            Thread.sleep(1500);
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply Active filter", e);
        }
    }

    public boolean isClearAllButtonVisible() {
        return isElementVisible(ResellerDashboardUserManagementPageLocators.CLEAR_ALL_FILTERS_BUTTON);
    }

    public void clickClearAllFilters() {
        try {
            WebElement clearBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    ResellerDashboardUserManagementPageLocators.CLEAR_ALL_FILTERS_BUTTON));
            clearBtn.click();
            Thread.sleep(1500);
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear filters", e);
        }
    }

    public boolean isCreateUserButtonVisible() {
        return isElementVisible(ResellerDashboardUserManagementPageLocators.CREATE_USER_BUTTON);
    }

    public void clickCreateUserButton() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.CREATE_USER_BUTTON);
    }

    public boolean isUpdatePlanButtonPresent(int row) {
        return isElementVisible(ResellerDashboardUserManagementPageLocators.getUpdatePlanButtonByRow(row));
    }

    public void clickUpdatePlanButton(int row) {
        clickAndWait(ResellerDashboardUserManagementPageLocators.getUpdatePlanButtonByRow(row));
    }

    public boolean isAddCreditButtonPresent(int row) {
        return isElementVisible(ResellerDashboardUserManagementPageLocators.getAddCreditButtonByRow(row));
    }

    public void clickAddCreditButton(int row) {
        clickAndWait(ResellerDashboardUserManagementPageLocators.getAddCreditButtonByRow(row));
    }

    public void clickRowArrowButton(int row) {
        clickAndWait(ResellerDashboardUserManagementPageLocators.getRowArrowButton(row));
    }

    // ==================== Customer wallets tab ====================

    public void clickCustomerWalletsTab() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.CUSTOMER_WALLETS_TAB);
    }

    public void clickOrganizationDetailsTab() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.ORGANIZATION_DETAILS_TAB);
    }

    public void clickProfileTab() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.PROFILE_TAB);
    }

    public void clickRolesTab() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.ROLES_TAB);
    }

    public boolean isAddWalletButtonVisible() {
        return isElementVisible(ResellerDashboardUserManagementPageLocators.ADD_WALLET_BUTTON);
    }

    public void clickAddWalletButton() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.ADD_WALLET_BUTTON);
    }

    public void clickWalletServicesButton(int row) {
        clickAndWait(ResellerDashboardUserManagementPageLocators.getWalletServicesButtonByRow(row));
    }

    // ==================== Wallet Services page ====================

    public boolean isAddNewServiceButtonVisible() {
        return waitUntilVisible(ResellerDashboardUserManagementPageLocators.ADD_NEW_SERVICE_BUTTON);
    }

    public void clickAddNewServiceButton() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.ADD_NEW_SERVICE_BUTTON);
    }

    /**
     * Returns the "Available Wallet Balance" text. The balance loads after the page itself, so this
     * waits (up to 15s) until it is shown with a number instead of reading it immediately.
     */
    public String getWalletBalanceValue() {
        try {
            return wait.until(d -> {
                String text = d.findElement(ResellerDashboardUserManagementPageLocators.WALLET_BALANCE_VALUE)
                        .getText().trim();
                return text.matches(".*\\d.*") ? text : null;
            });
        } catch (Exception e) {
            System.out.println("Error reading wallet balance: " + e.getMessage());
            return "";
        }
    }

    /**
     * After Refill/Deduct the balance updates asynchronously - waits (up to 15s) until it differs
     * from the previous value and returns the new balance (or the unchanged one on timeout).
     */
    public double waitForBalanceChange(double previousBalance) {
        try {
            return wait.until(d -> {
                double current = parseBalance(getWalletBalanceValue());
                return Math.abs(current - previousBalance) > 0.001 ? current : null;
            });
        } catch (TimeoutException e) {
            return parseBalance(getWalletBalanceValue());
        }
    }

    public void clickRefillButton() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.REFILL_BUTTON);
    }

    public void clickDeductButton() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.DEDUCT_BUTTON);
    }

    /**
     * The Refill/Deduct modal requires the Amount and Confirm Amount fields
     * filled with the same value, AND a required "Reason" textarea (easy to
     * miss since it isn't an <input> - a diagnostic dump limited to input
     * tags won't surface it). Confirmed live: the "amount" field auto-
     * formats on blur (e.g. "5.0" -> "5.0000" via an appdigitonly directive)
     * but "confirmAmount" only reformats once IT is blurred too - leaving
     * them as different-looking strings (and the cross-field validator
     * failing, keeping submit disabled) unless both are blurred first.
     */
    public void enterRefillDeductAmount(String amount) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardUserManagementPageLocators.REFILL_DEDUCT_AMOUNT_INPUT));
        input.clear();
        input.sendKeys(amount);

        WebElement confirmInput = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardUserManagementPageLocators.REFILL_DEDUCT_CONFIRM_AMOUNT_INPUT));
        confirmInput.clear();
        confirmInput.sendKeys(amount);
        confirmInput.sendKeys(Keys.TAB);

        WebElement reasonTextarea = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardUserManagementPageLocators.REFILL_DEDUCT_REASON_TEXTAREA));
        reasonTextarea.clear();
        reasonTextarea.sendKeys("Automated test fund adjustment");

        try {
            Thread.sleep(300);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    public void clickRefillDeductSubmit() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.REFILL_DEDUCT_SUBMIT_BUTTON);
    }

    /** Parses a balance string like "?884.00" or "884.00" into a double. */
    public double parseBalance(String balanceText) {
        try {
            String cleaned = balanceText.replaceAll("[^0-9.]", "");
            return cleaned.isEmpty() ? 0.0 : Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // ==================== Organization Details tab ====================

    public boolean isContactInformationHeaderVisible() {
        return isElementVisible(ResellerDashboardUserManagementPageLocators.CONTACT_INFORMATION_HEADER);
    }

    public String getOrganizationLevelValue() {
        try {
            return driver.findElement(ResellerDashboardUserManagementPageLocators.ORGANIZATION_LEVEL_VALUE).getText().trim();
        } catch (Exception e) {
            System.out.println("Error reading organization level: " + e.getMessage());
            return "";
        }
    }

    // ==================== Profile tab ====================

    public boolean isTeamMembersHeaderVisible() {
        return isElementVisible(ResellerDashboardUserManagementPageLocators.TEAM_MEMBERS_HEADER);
    }

    public boolean isProfileAddNewButtonVisible() {
        return isElementVisible(ResellerDashboardUserManagementPageLocators.PROFILE_ADD_NEW_BUTTON);
    }

    public void clickProfileAddNewButton() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.PROFILE_ADD_NEW_BUTTON);
    }

    public void clickProfileEditButton(int row) {
        clickAndWait(ResellerDashboardUserManagementPageLocators.getProfileEditButtonByRow(row));
    }

    public void clickProfileViewButton(int row) {
        clickAndWait(ResellerDashboardUserManagementPageLocators.getProfileViewButtonByRow(row));
    }

    // ==================== Roles tab ====================

    public boolean isRoleManagementHeaderVisible() {
        return isElementVisible(ResellerDashboardUserManagementPageLocators.ROLE_MANAGEMENT_HEADER);
    }

    public boolean isRolesAddNewButtonVisible() {
        // The Roles tab renders its toolbar after the role list loads - checking once right after
        // navigating found nothing and failed the test intermittently
        return waitUntilVisible(ResellerDashboardUserManagementPageLocators.ROLES_ADD_NEW_BUTTON);
    }

    /** Waits (up to 15s) for the URL to contain {@code expectedPart}, then returns the current URL. */
    public String waitForUrlContaining(String expectedPart) {
        try {
            wait.until(ExpectedConditions.urlContains(expectedPart));
        } catch (TimeoutException e) {
            // Returned as-is so the caller's assertion reports the URL actually reached
        }
        return driver.getCurrentUrl();
    }

    public void clickRolesAddNewButton() {
        clickAndWait(ResellerDashboardUserManagementPageLocators.ROLES_ADD_NEW_BUTTON);
    }

    public void clickRoleEditButton(int row) {
        clickAndWait(ResellerDashboardUserManagementPageLocators.getRoleEditButtonByRow(row));
    }

    public void clickRoleViewButton(int row) {
        clickAndWait(ResellerDashboardUserManagementPageLocators.getRoleViewButtonByRow(row));
    }

    public boolean isOverviewPanelVisible() {
        return isElementVisible(ResellerDashboardUserManagementPageLocators.OVERVIEW_PANEL_HEADER);
    }

    // ==================== Utility ====================

    public boolean isElementVisible(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Like {@link #isElementVisible} but gives the element up to 15s to appear. */
    public boolean waitUntilVisible(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
