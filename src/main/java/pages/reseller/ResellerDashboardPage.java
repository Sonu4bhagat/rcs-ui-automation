package pages.reseller;

import locators.reseller.ResellerDashboardPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the Reseller Dashboard's "My Wallets" tab and Wallet Fund
 * Transfer flow.
 */
public class ResellerDashboardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public ResellerDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ==================== My Wallets Tab ====================

    /**
     * True if the "My Wallets" tab/section is the currently active/selected view
     * on the dashboard (checked via an active-class match, falling back to
     * whether the wallet table/header is simply visible - some dashboards don't
     * expose an explicit "active tab" class for a default-selected tab).
     */
    public boolean isMyWalletsTabSelected() {
        try {
            if (isElementVisible(ResellerDashboardPageLocators.MY_WALLETS_HEADER)) {
                return true;
            }
            return isElementVisible(ResellerDashboardPageLocators.WALLET_TABLE_HEADERS)
                    || isElementVisible(ResellerDashboardPageLocators.NO_DATA_MESSAGE);
        } catch (Exception e) {
            return false;
        }
    }

    public void clickMyWalletsTab() {
        try {
            WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
                    ResellerDashboardPageLocators.MY_WALLETS_TAB));
            tab.click();
        } catch (Exception e) {
            System.out.println("My Wallets tab not clickable/not found (may already be the default view): "
                    + e.getMessage());
        }
    }

    public String getPageHeaderText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    ResellerDashboardPageLocators.MY_WALLETS_HEADER)).getText().trim();
        } catch (Exception e) {
            System.out.println("Page header not found: " + e.getMessage());
            return "";
        }
    }

    // ==================== Wallet Listing ====================

    public void waitForWalletData() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(ResellerDashboardPageLocators.WALLET_TABLE_ROWS),
                    ExpectedConditions.visibilityOfElementLocated(ResellerDashboardPageLocators.NO_DATA_MESSAGE)));
        } catch (Exception e) {
            System.out.println("Timed out waiting for wallet data: " + e.getMessage());
        }
    }

    public boolean hasWallets() {
        try {
            return !driver.findElements(ResellerDashboardPageLocators.WALLET_TABLE_ROWS).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNoDataMessageDisplayed() {
        return isElementVisible(ResellerDashboardPageLocators.NO_DATA_MESSAGE);
    }

    public int getWalletRowCount() {
        try {
            return driver.findElements(ResellerDashboardPageLocators.WALLET_TABLE_ROWS).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<String> getWalletTableHeaders() {
        List<String> headers = new ArrayList<>();
        try {
            List<WebElement> headerElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    ResellerDashboardPageLocators.WALLET_TABLE_HEADERS));
            for (WebElement header : headerElements) {
                String text = header.getText().trim();
                if (!text.isEmpty()) {
                    headers.add(text);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting wallet table headers: " + e.getMessage());
        }
        return headers;
    }

    /**
     * Reads a wallet table cell's text, waiting for it to have non-empty text
     * (not just be visible). Angular renders the row skeleton (empty &lt;td&gt;)
     * before async-populating its text, so waiting only for visibility can
     * return "" immediately if this runs right after a navigation/refresh -
     * e.g. straight after returning from the wallet's Usage page in TC05.
     */
    public String getWalletCellText(int row, int column) {
        By locator = ResellerDashboardPageLocators.getWalletCellByRowAndColumn(row, column);
        try {
            wait.until(d -> {
                try {
                    return !d.findElement(locator).getText().trim().isEmpty();
                } catch (Exception e) {
                    return false;
                }
            });
            return driver.findElement(locator).getText().trim();
        } catch (Exception e) {
            System.out.println("Could not read wallet cell (row=" + row + ", column=" + column + "): "
                    + e.getMessage());
            return "";
        }
    }

    public String getWalletStatus(int row) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    ResellerDashboardPageLocators.getWalletStatusByRow(row))).getText().trim();
        } catch (Exception e) {
            System.out.println("Could not read wallet status for row " + row + ": " + e.getMessage());
            return "N/A";
        }
    }

    // ==================== Pagination ====================

    public boolean isPaginationAvailable() {
        return isElementVisible(ResellerDashboardPageLocators.PAGINATION_NEXT)
                || isElementVisible(ResellerDashboardPageLocators.PAGINATION_PREVIOUS);
    }

    public boolean clickNextPage() {
        try {
            WebElement next = driver.findElement(ResellerDashboardPageLocators.PAGINATION_NEXT);
            if (!next.isEnabled()) {
                return false;
            }
            next.click();
            waitForWalletData();
            return true;
        } catch (Exception e) {
            System.out.println("Failed to click Next page: " + e.getMessage());
            return false;
        }
    }

    public boolean clickPreviousPage() {
        try {
            WebElement prev = driver.findElement(ResellerDashboardPageLocators.PAGINATION_PREVIOUS);
            if (!prev.isEnabled()) {
                return false;
            }
            prev.click();
            waitForWalletData();
            return true;
        } catch (Exception e) {
            System.out.println("Failed to click Previous page: " + e.getMessage());
            return false;
        }
    }

    // ==================== Service Navigation ====================

    public boolean isServiceNavButtonEnabled(int row) {
        try {
            WebElement btn = driver.findElement(ResellerDashboardPageLocators.getServiceNavButtonByRow(row));
            return btn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickServiceNavButton(int row) {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardPageLocators.getServiceNavButtonByRow(row)));
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    // ==================== Search ====================

    public void searchWallet(String searchText) {
        try {
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    ResellerDashboardPageLocators.SEARCH_INPUT));
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
            throw new RuntimeException("Failed to search wallet", e);
        }
    }

    public void clearSearch() {
        try {
            WebElement searchInput = driver.findElement(ResellerDashboardPageLocators.SEARCH_INPUT);
            searchInput.clear();
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Error clearing search: " + e.getMessage());
        }
    }

    // ==================== Filters ====================

    public void applyActiveStatusFilter() {
        clickAndSelect(ResellerDashboardPageLocators.FILTER_BUTTON, ResellerDashboardPageLocators.STATUS_FILTER_ACTIVE);
    }

    public void applyPrepaidTypeFilter() {
        clickAndSelect(ResellerDashboardPageLocators.FILTER_BUTTON,
                ResellerDashboardPageLocators.WALLET_TYPE_FILTER_PREPAID);
    }

    private void clickAndSelect(By openLocator, By optionLocator) {
        try {
            WebElement openBtn = wait.until(ExpectedConditions.elementToBeClickable(openLocator));
            openBtn.click();
            Thread.sleep(500);
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
            option.click();
            Thread.sleep(300);
            try {
                WebElement applyBtn = driver.findElement(ResellerDashboardPageLocators.APPLY_FILTER_BUTTON);
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
                    ResellerDashboardPageLocators.CLEAR_ALL_FILTERS_BUTTON));
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

    // ==================== Wallet Fund Transfer ====================

    public boolean isFundTransferButtonVisible() {
        return isElementVisible(ResellerDashboardPageLocators.FUND_TRANSFER_BUTTON);
    }

    public void clickFundTransferButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                ResellerDashboardPageLocators.FUND_TRANSFER_BUTTON));
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public boolean isFundTransferModalOpen() {
        return isElementVisible(ResellerDashboardPageLocators.FUND_TRANSFER_MODAL);
    }

    public boolean isSourceWalletDropdownVisible() {
        return isElementVisible(ResellerDashboardPageLocators.SOURCE_WALLET_DROPDOWN);
    }

    public boolean isDestinationWalletDropdownVisible() {
        return isElementVisible(ResellerDashboardPageLocators.DESTINATION_WALLET_DROPDOWN);
    }

    /**
     * Confirmed live: both wallet dropdowns are searchable comboboxes (not a
     * native select/mat-select/ng-select tag) that, once clicked, show a list
     * of options formatted as "WalletName - <balance> - Type" (e.g.
     * "PssTest11-UW-339 - ₹899 - Prepaid") via generic option-role elements.
     */
    public List<String> getDropdownOptionTexts(By dropdownLocator) {
        List<String> texts = new ArrayList<>();
        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            dropdown.click();
            Thread.sleep(800);
            List<WebElement> options = driver.findElements(By.xpath(
                    "//mat-option | //ng-option | //div[@role='option'] | //li[@role='option']"));
            for (WebElement o : options) {
                String t = o.getText().trim();
                if (!t.isEmpty()) {
                    texts.add(t);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading dropdown options: " + e.getMessage());
        }
        return texts;
    }

    /** Selects the option whose visible text exactly matches optionText. Assumes the dropdown is already open. */
    public boolean selectOpenDropdownOptionByText(String optionText) {
        try {
            List<WebElement> options = driver.findElements(By.xpath(
                    "//mat-option | //ng-option | //div[@role='option'] | //li[@role='option']"));
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
            System.out.println("Error selecting option '" + optionText + "': " + e.getMessage());
        }
        return false;
    }

    /** Extracts the wallet name from an option formatted "WalletName - <balance> - Type". */
    public String extractWalletNameFromOption(String optionText) {
        String[] parts = optionText.split(" - ");
        return parts.length > 0 ? parts[0].trim() : optionText.trim();
    }

    /** Extracts the balance from an option formatted "WalletName - <balance> - Type". */
    public double extractBalanceFromOption(String optionText) {
        String[] parts = optionText.split(" - ");
        return parts.length > 1 ? parseBalance(parts[1]) : 0.0;
    }

    /**
     * Extracts the wallet type (Prepaid/Postpaid) from an option formatted
     * "WalletName - <balance> - Type". Confirmed live: the app rejects a
     * transfer where source and destination types differ ("Source and
     * destination wallet types must be the same for transfer"), so callers
     * must match on this when picking a destination.
     */
    public String extractTypeFromOption(String optionText) {
        String[] parts = optionText.split(" - ");
        return parts.length > 2 ? parts[2].trim() : "";
    }

    public String getSelectedSourceWalletText() {
        return getSelectedDropdownText(ResellerDashboardPageLocators.SOURCE_WALLET_DROPDOWN);
    }

    public String getSelectedDestinationWalletText() {
        return getSelectedDropdownText(ResellerDashboardPageLocators.DESTINATION_WALLET_DROPDOWN);
    }

    private String getSelectedDropdownText(By dropdownLocator) {
        try {
            WebElement dropdown = driver.findElement(dropdownLocator);
            String tag = dropdown.getTagName().toLowerCase();
            if (tag.equals("select")) {
                return new org.openqa.selenium.support.ui.Select(dropdown).getFirstSelectedOption().getText().trim();
            }
            return dropdown.getAttribute("value") != null && !dropdown.getAttribute("value").isEmpty()
                    ? dropdown.getAttribute("value")
                    : dropdown.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void enterTransferAmount(String amount) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                ResellerDashboardPageLocators.TRANSFER_AMOUNT_INPUT));
        input.clear();
        input.sendKeys(amount);
    }

    public void enterTransferDescription(String description) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                ResellerDashboardPageLocators.TRANSFER_DESCRIPTION_INPUT));
        input.clear();
        input.sendKeys(description);
    }

    public void clickFundTransferSubmit() {
        // The submit button's async validation has occasionally needed more than
        // 15s to settle (observed once in a real run, though it typically enables
        // within a couple seconds) - retry once with a longer wait before giving
        // up, same class of fix as the RCS Assistant form's validation lag.
        WebElement btn = null;
        for (int attempt = 1; attempt <= 2 && btn == null; attempt++) {
            try {
                int timeoutSeconds = attempt == 1 ? 15 : 30;
                btn = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(
                        ExpectedConditions.elementToBeClickable(
                                ResellerDashboardPageLocators.FUND_TRANSFER_SUBMIT_BUTTON));
            } catch (Exception e) {
                if (attempt == 2) {
                    System.out.println("Fund Transfer submit button never became clickable - dumping modal state:");
                    dumpFundTransferModalState();
                    String shot = utils.ScreenshotUtil.captureScreenshot(driver, "fundTransferSubmitStuckDisabled");
                    System.out.println("Diagnostic screenshot saved to: " + shot);
                    throw e;
                }
                System.out.println("Submit button not yet clickable after attempt " + attempt
                        + ", retrying with a longer wait...");
            }
        }
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /** Diagnostic dump of the Fund Transfer modal's field values when Submit stays disabled. */
    private void dumpFundTransferModalState() {
        try {
            System.out.println("  Source wallet field: '" + getSelectedSourceWalletText() + "'");
            System.out.println("  Destination wallet field: '" + getSelectedDestinationWalletText() + "'");
            try {
                WebElement amountField = driver.findElement(ResellerDashboardPageLocators.TRANSFER_AMOUNT_INPUT);
                System.out.println("  Amount field value: '" + amountField.getAttribute("value") + "'");
            } catch (Exception e) {
                System.out.println("  Amount field not found: " + e.getMessage());
            }
            try {
                WebElement descField = driver.findElement(ResellerDashboardPageLocators.TRANSFER_DESCRIPTION_INPUT);
                System.out.println("  Description field value: '" + descField.getAttribute("value") + "'");
            } catch (Exception e) {
                System.out.println("  Description field not found: " + e.getMessage());
            }
            List<WebElement> invalidFields = driver.findElements(By.xpath("//*[contains(@class,'ng-invalid')]"));
            System.out.println("  Found " + invalidFields.size() + " ng-invalid element(s):");
            for (WebElement f : invalidFields) {
                System.out.println("    tag=" + f.getTagName() + " formcontrolname="
                        + f.getAttribute("formcontrolname") + " class=" + f.getAttribute("class"));
            }
            List<WebElement> errorTexts = driver.findElements(By.xpath(
                    "//*[contains(@class,'error') or contains(@class,'invalid-feedback')]"));
            System.out.println("  Found " + errorTexts.size() + " error-styled element(s):");
            for (WebElement e : errorTexts) {
                String text = e.getText().trim();
                if (!text.isEmpty()) {
                    System.out.println("    '" + text + "'");
                }
            }
        } catch (Exception e) {
            System.out.println("  Error dumping modal state: " + e.getMessage());
        }
    }

    public boolean isTransferSuccessMessageDisplayed() {
        return isElementVisible(ResellerDashboardPageLocators.TRANSFER_SUCCESS_MESSAGE);
    }

    /**
     * Reads a wallet's displayed balance (as a raw string, e.g. "₹1,234.00")
     * from the wallet listing table by matching its name in column 1 and
     * reading column 6 ("Current Balance (₹)" - confirmed live table order:
     * Wallet Name, Wallet Type, Wallet Mode, Created On, Service, Current
     * Balance, Usage, Status, Action).
     * <p>
     * Polls (via wait.until) until the matching row's balance cell has
     * non-empty text, not just until the row exists - like getWalletCellText,
     * Angular can render the row skeleton with an empty balance cell for a
     * moment (especially right after a transfer, while the balance recalculates)
     * before the real value populates. A stale element reference during any
     * single poll just fails that poll silently; wait.until retries on its own.
     */
    public String getWalletBalanceByName(String walletName) {
        final String[] result = new String[1];
        try {
            waitForWalletData();
            wait.until(d -> {
                try {
                    List<WebElement> rows = d.findElements(ResellerDashboardPageLocators.WALLET_TABLE_ROWS);
                    for (WebElement row : rows) {
                        List<WebElement> cells = row.findElements(By.xpath(".//td"));
                        if (cells.size() < 6) {
                            continue;
                        }
                        String name = cells.get(0).getText().trim();
                        if (name.equalsIgnoreCase(walletName)) {
                            String balance = cells.get(5).getText().trim();
                            if (!balance.isEmpty()) {
                                result[0] = balance;
                                return true;
                            }
                            return false;
                        }
                    }
                    return false;
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            });
        } catch (Exception e) {
            System.out.println("Error reading wallet balance for '" + walletName + "': " + e.getMessage());
        }
        return result[0] != null ? result[0] : "";
    }

    public double parseBalance(String balanceText) {
        try {
            String cleaned = balanceText.replaceAll("[^0-9.]", "");
            return cleaned.isEmpty() ? 0.0 : Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
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
