package pages;

import locators.EnterpriseWalletPageLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for Enterprise Wallet Tab.
 * Provides helper methods for wallet transaction operations.
 */
public class EnterpriseWalletPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public EnterpriseWalletPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ==================== Navigation Methods ====================

    /**
     * Navigate to Wallet page by clicking Wallet in sidebar
     */
    public void navigateToWallet() {
        try {
            System.out.println("Attempting to navigate to Wallet...");
            Thread.sleep(1000);

            // Try to click on Wallet menu item
            WebElement walletMenu = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseWalletPageLocators.WALLET_MENU_ITEM));
            walletMenu.click();
            System.out.println("Clicked Wallet menu item");

            Thread.sleep(3000);

            // First check URL contains wallet-management
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Current URL after click: " + currentUrl);

            if (currentUrl.contains("wallet-management") || currentUrl.contains("wallet")) {
                System.out.println("URL validation passed - on Wallet page");
            } else {
                System.out.println("Warning: URL does not contain wallet. Waiting for elements...");
            }

            // Wait for wallet page elements to load
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(EnterpriseWalletPageLocators.WALLET_BALANCE),
                        ExpectedConditions.visibilityOfElementLocated(EnterpriseWalletPageLocators.TRANSACTION_TABLE),
                        ExpectedConditions
                                .visibilityOfElementLocated(EnterpriseWalletPageLocators.ARCHIVE_REPORT_BUTTON)));
                System.out.println("Wallet page elements found");
            } catch (Exception e) {
                System.out.println("Primary elements not found, checking for URL validation...");
                if (currentUrl.contains("wallet")) {
                    System.out.println("URL contains 'wallet' - considering page loaded");
                } else {
                    throw e;
                }
            }

            System.out.println("Wallet page loaded successfully");
        } catch (Exception e) {
            System.out.println("Error navigating to Wallet: " + e.getMessage());
            throw new RuntimeException("Failed to navigate to Wallet page", e);
        }
    }

    /**
     * Check if Wallet page is loaded
     */
    public boolean isWalletPageLoaded() {
        try {
            // First check URL
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("wallet-management") || currentUrl.contains("wallet")) {
                return true;
            }
            // Then check elements
            return isElementVisible(EnterpriseWalletPageLocators.WALLET_PAGE_HEADER) ||
                    isElementVisible(EnterpriseWalletPageLocators.WALLET_BALANCE) ||
                    isElementVisible(EnterpriseWalletPageLocators.TRANSACTION_TABLE) ||
                    isElementVisible(EnterpriseWalletPageLocators.ARCHIVE_REPORT_BUTTON);
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Transaction List Methods ====================

    /**
     * Wait for either transaction rows OR the "No Data" message to appear.
     * This ensures the page has finished dynamic loading.
     */
    public void waitForTransactionData() {
        try {
            System.out.println("Waiting for transaction data or empty state...");
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(EnterpriseWalletPageLocators.TRANSACTION_TABLE_ROWS),
                    ExpectedConditions.visibilityOfElementLocated(EnterpriseWalletPageLocators.NO_DATA_MESSAGE)));
            System.out.println("Transaction state detected.");
        } catch (Exception e) {
            System.out.println("Wait for transaction data timed out: " + e.getMessage());
        }
    }

    /**
     * Check if transactions exist in the list
     */
    public boolean hasTransactions() {
        try {
            List<WebElement> rows = driver.findElements(EnterpriseWalletPageLocators.TRANSACTION_TABLE_ROWS);
            return rows != null && !rows.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if "No Data Available" message is displayed
     */
    public boolean isNoDataMessageDisplayed() {
        try {
            return isElementVisible(EnterpriseWalletPageLocators.NO_DATA_MESSAGE);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get all column headers from transaction table
     */
    public List<String> getTransactionListHeaders() {
        List<String> headers = new ArrayList<>();
        try {
            List<WebElement> headerElements = wait.until(
                    ExpectedConditions
                            .presenceOfAllElementsLocatedBy(EnterpriseWalletPageLocators.TRANSACTION_TABLE_HEADERS));
            for (WebElement header : headerElements) {
                String text = header.getText().trim();
                if (!text.isEmpty()) {
                    headers.add(text);
                }
            }
            System.out.println("Found headers: " + headers);
        } catch (Exception e) {
            System.out.println("Error getting headers: " + e.getMessage());
        }
        return headers;
    }

    /**
     * Validate that all expected headers are present
     */
    public boolean validateTransactionHeaders(List<String> expectedHeaders) {
        List<String> actualHeaders = getTransactionListHeaders();
        if (actualHeaders.isEmpty()) {
            System.out.println("No headers found");
            return false;
        }

        for (String expected : expectedHeaders) {
            boolean found = actualHeaders.stream()
                    .anyMatch(h -> h.toLowerCase().contains(expected.toLowerCase()));
            if (!found) {
                System.out.println("Missing expected header: " + expected);
                return false;
            }
        }
        return true;
    }

    /**
     * Get the count of transaction rows
     */
    public int getTransactionRowCount() {
        try {
            List<WebElement> rows = driver.findElements(EnterpriseWalletPageLocators.TRANSACTION_TABLE_ROWS);
            return rows != null ? rows.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== Filter Methods ====================

    /**
     * Select a transaction type filter (Transaction, Recharge, Deduction)
     */
    public void selectTransactionTypeFilter(String type) {
        try {
            System.out.println("Selecting filter: " + type);

            // Click the filter dropdown
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseWalletPageLocators.TRANSACTION_FILTER_DROPDOWN));
            dropdown.click();
            Thread.sleep(500);

            // Select the option
            By optionLocator = EnterpriseWalletPageLocators.getFilterOptionByType(type);
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
            option.click();

            Thread.sleep(1000);
            System.out.println("Filter applied: " + type);
        } catch (Exception e) {
            System.out.println("Error applying filter: " + e.getMessage());
            throw new RuntimeException("Failed to apply filter: " + type, e);
        }
    }

    /**
     * Clear the applied filter
     */
    public void clearFilter() {
        try {
            WebElement clearBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseWalletPageLocators.CLEAR_FILTER_BUTTON));
            clearBtn.click();
            Thread.sleep(1000);
            System.out.println("Filter cleared");
        } catch (Exception e) {
            System.out.println("Clear filter button not found or not clickable: " + e.getMessage());
            // Try refreshing the page as fallback
            driver.navigate().refresh();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Get transaction types from filtered results
     */
    public List<String> getFilteredTransactionTypes() {
        List<String> types = new ArrayList<>();
        try {
            List<WebElement> typeCells = driver.findElements(EnterpriseWalletPageLocators.TRANSACTION_TYPE_CELLS);
            for (WebElement cell : typeCells) {
                String type = cell.getText().trim();
                if (!type.isEmpty()) {
                    types.add(type);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting transaction types: " + e.getMessage());
        }
        return types;
    }

    // ==================== Pagination Methods ====================

    /**
     * Check if pagination is available
     */
    public boolean isPaginationAvailable() {
        try {
            return isElementVisible(EnterpriseWalletPageLocators.PAGINATION_NEXT) ||
                    isElementVisible(EnterpriseWalletPageLocators.PAGINATION_PREVIOUS);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click Next page button
     */
    public boolean clickNextPage() {
        try {
            WebElement nextBtn = driver.findElement(EnterpriseWalletPageLocators.PAGINATION_NEXT);
            if (nextBtn.isEnabled()) {
                nextBtn.click();
                Thread.sleep(1000);
                System.out.println("Clicked Next page");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Next page button not available: " + e.getMessage());
        }
        return false;
    }

    /**
     * Click Previous page button
     */
    public boolean clickPreviousPage() {
        try {
            WebElement prevBtn = driver.findElement(EnterpriseWalletPageLocators.PAGINATION_PREVIOUS);
            if (prevBtn.isEnabled()) {
                prevBtn.click();
                Thread.sleep(1000);
                System.out.println("Clicked Previous page");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Previous page button not available: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get pagination info text
     */
    public String getPaginationInfo() {
        try {
            WebElement info = driver.findElement(EnterpriseWalletPageLocators.PAGINATION_INFO);
            return info.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    // ==================== Amount Methods ====================

    /**
     * Get the displayed Total Credit amount
     */
    public double getTotalCreditAmount() {
        try {
            WebElement creditElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    EnterpriseWalletPageLocators.TOTAL_CREDIT_AMOUNT));
            String text = creditElement.getText().replaceAll("[^0-9.]", "");
            return text.isEmpty() ? 0.0 : Double.parseDouble(text);
        } catch (Exception e) {
            System.out.println("Error getting total credit: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Get the displayed Total Debit amount
     */
    public double getTotalDebitAmount() {
        try {
            WebElement debitElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    EnterpriseWalletPageLocators.TOTAL_DEBIT_AMOUNT));
            String text = debitElement.getText().replaceAll("[^0-9.]", "");
            return text.isEmpty() ? 0.0 : Double.parseDouble(text);
        } catch (Exception e) {
            System.out.println("Error getting total debit: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Calculate sum of all Credit values from the complete list (handles
     * pagination)
     */
    public double calculateCreditSum() {
        double sum = 0.0;
        try {
            // Go to first page
            while (clickPreviousPage()) {
                // Keep clicking until we're at the first page
            }

            do {
                List<WebElement> creditCells = driver.findElements(EnterpriseWalletPageLocators.CREDIT_AMOUNT_CELLS);
                for (WebElement cell : creditCells) {
                    String text = cell.getText().replaceAll("[^0-9.]", "");
                    if (!text.isEmpty()) {
                        sum += Double.parseDouble(text);
                    }
                }
            } while (clickNextPage());

            System.out.println("Calculated credit sum: " + sum);
        } catch (Exception e) {
            System.out.println("Error calculating credit sum: " + e.getMessage());
        }
        return sum;
    }

    /**
     * Calculate sum of all Debit values from the complete list (handles pagination)
     */
    public double calculateDebitSum() {
        double sum = 0.0;
        try {
            // Go to first page
            while (clickPreviousPage()) {
                // Keep clicking until we're at the first page
            }

            do {
                List<WebElement> debitCells = driver.findElements(EnterpriseWalletPageLocators.DEBIT_AMOUNT_CELLS);
                for (WebElement cell : debitCells) {
                    String text = cell.getText().replaceAll("[^0-9.]", "");
                    if (!text.isEmpty()) {
                        sum += Double.parseDouble(text);
                    }
                }
            } while (clickNextPage());

            System.out.println("Calculated debit sum: " + sum);
        } catch (Exception e) {
            System.out.println("Error calculating debit sum: " + e.getMessage());
        }
        return sum;
    }

    // ==================== Archive Report Methods ====================

    /**
     * Click Archive Report button
     */
    public void clickArchiveReportButton() {
        try {
            WebElement archiveBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseWalletPageLocators.ARCHIVE_REPORT_BUTTON));
            archiveBtn.click();
            Thread.sleep(2000);
            System.out.println("Clicked Archive Report button");
        } catch (Exception e) {
            System.out.println("Error clicking Archive Report: " + e.getMessage());
            throw new RuntimeException("Failed to click Archive Report button", e);
        }
    }

    /**
     * Check if Archive Report page is loaded
     */
    public boolean isArchiveReportPageLoaded() {
        try {
            return isElementVisible(EnterpriseWalletPageLocators.ARCHIVE_REPORT_TABLE);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get report status for a specific row
     */
    public String getReportStatus(int row) {
        try {
            By locator = EnterpriseWalletPageLocators.getReportStatusByRow(row);
            WebElement statusCell = driver.findElement(locator);
            return statusCell.getText().trim();
        } catch (Exception e) {
            System.out.println("Error getting report status: " + e.getMessage());
            return "";
        }
    }

    /**
     * Click download for a specific report row
     */
    public void clickDownloadReport(int row) {
        try {
            By locator = EnterpriseWalletPageLocators.getDownloadButtonByRow(row);
            WebElement downloadBtn = wait.until(ExpectedConditions.elementToBeClickable(locator));
            downloadBtn.click();
            System.out.println("Clicked download for row: " + row);
        } catch (Exception e) {
            System.out.println("Error clicking download: " + e.getMessage());
            throw new RuntimeException("Failed to click download for row " + row, e);
        }
    }

    /**
     * Check if report is downloadable (Active status)
     */
    public boolean isDownloadable(int row) {
        try {
            String status = getReportStatus(row);
            return status.equalsIgnoreCase("Active");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Find first report with Active status
     */
    public int findFirstActiveReport() {
        try {
            List<WebElement> statusCells = driver.findElements(EnterpriseWalletPageLocators.REPORT_STATUS_CELLS);
            for (int i = 0; i < statusCells.size(); i++) {
                if (statusCells.get(i).getText().trim().equalsIgnoreCase("Active")) {
                    return i + 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding active report: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Find first report with Expired status
     */
    public int findFirstExpiredReport() {
        try {
            List<WebElement> statusCells = driver.findElements(EnterpriseWalletPageLocators.REPORT_STATUS_CELLS);
            for (int i = 0; i < statusCells.size(); i++) {
                if (statusCells.get(i).getText().trim().equalsIgnoreCase("Expired")) {
                    return i + 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding expired report: " + e.getMessage());
        }
        return -1;
    }

    // ==================== Generate Report Methods ====================

    /**
     * Click Generate Report button
     */
    public void clickGenerateReportButton() {
        try {
            WebElement generateBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseWalletPageLocators.GENERATE_REPORT_BUTTON));
            generateBtn.click();
            Thread.sleep(1000);
            System.out.println("Clicked Generate Report button");
        } catch (Exception e) {
            System.out.println("Error clicking Generate Report: " + e.getMessage());
            throw new RuntimeException("Failed to click Generate Report button", e);
        }
    }

    /**
     * Check if Generate Report popup is open
     */
    public boolean isGenerateReportPopupOpen() {
        try {
            return isElementVisible(EnterpriseWalletPageLocators.GENERATE_REPORT_POPUP);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Enter values in the Generate Report popup
     */
    public void enterReportValues(String startDate, String endDate) {
        try {
            // Enter start date
            WebElement startInput = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseWalletPageLocators.POPUP_START_DATE));
            startInput.clear();
            startInput.sendKeys(startDate);

            // Enter end date
            WebElement endInput = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseWalletPageLocators.POPUP_END_DATE));
            endInput.clear();
            endInput.sendKeys(endDate);

            Thread.sleep(500);
            System.out.println("Entered report values - Start: " + startDate + ", End: " + endDate);
        } catch (Exception e) {
            System.out.println("Error entering report values: " + e.getMessage());
        }
    }

    /**
     * Click Cancel button on popup
     */
    public void clickCancelOnPopup() {
        try {
            WebElement cancelBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseWalletPageLocators.POPUP_CANCEL_BUTTON));
            cancelBtn.click();
            Thread.sleep(1000);
            System.out.println("Clicked Cancel on popup");
        } catch (Exception e) {
            System.out.println("Error clicking Cancel: " + e.getMessage());
            // Try multiple fallback methods to close the modal
            closeModalFallback();
        }
    }

    /**
     * Fallback methods to close modal if Cancel button not found
     */
    private void closeModalFallback() {
        try {
            // Fallback 1: Try clicking X close button in ng-bootstrap modal
            try {
                WebElement closeX = driver.findElement(By.xpath(
                        "//ngb-modal-window//button[@aria-label='Close'] | " +
                                "//ngb-modal-window//button[contains(@class, 'close')] | " +
                                "//button[contains(@class, 'btn-close')]"));
                closeX.click();
                Thread.sleep(500);
                System.out.println("Closed modal using X button");
                return;
            } catch (Exception ex) {
                // Continue to next fallback
            }

            // Fallback 2: Try pressing Escape key
            try {
                driver.switchTo().activeElement().sendKeys(org.openqa.selenium.Keys.ESCAPE);
                Thread.sleep(500);
                System.out.println("Pressed Escape to close modal");
                // Check if modal closed
                if (!isElementVisible(EnterpriseWalletPageLocators.GENERATE_REPORT_POPUP)) {
                    return;
                }
            } catch (Exception ex) {
                // Continue to next fallback
            }

            // Fallback 3: Try clicking the modal backdrop
            try {
                WebElement backdrop = driver.findElement(By.xpath(
                        "//div[contains(@class, 'modal-backdrop')] | " +
                                "//ngb-modal-window"));
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].dispatchEvent(new Event('click'));", backdrop);
                Thread.sleep(500);
                System.out.println("Clicked modal backdrop");
            } catch (Exception ex) {
                // Continue to next fallback
            }

            // Fallback 4: Use JavaScript to find and click any close/cancel button
            try {
                ((JavascriptExecutor) driver).executeScript(
                        "var buttons = document.querySelectorAll('ngb-modal-window button');" +
                                "for(var btn of buttons) {" +
                                "  var text = btn.innerText.toLowerCase();" +
                                "  if(text.includes('cancel') || text.includes('close') || btn.classList.contains('close') || btn.classList.contains('btn-close')) {"
                                +
                                "    btn.click(); break;" +
                                "  }" +
                                "}");
                Thread.sleep(500);
                System.out.println("Used JavaScript to close modal");
            } catch (Exception ex) {
                System.out.println("All fallback methods failed: " + ex.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error in closeModalFallback: " + e.getMessage());
        }
    }

    /**
     * Check if popup is closed
     */
    public boolean isPopupClosed() {
        try {
            Thread.sleep(500);
            return !isElementVisible(EnterpriseWalletPageLocators.GENERATE_REPORT_POPUP);
        } catch (Exception e) {
            return true;
        }
    }

    // ==================== Utility Methods ====================

    /**
     * Check if element is visible
     */
    private boolean isElementVisible(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Scroll element into view
     */
    public void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500);
        } catch (Exception e) {
            // Ignore scroll errors
        }
    }

    /**
     * Navigate back to wallet from archive report
     */
    public void navigateBackToWallet() {
        try {
            driver.navigate().back();
            Thread.sleep(2000);
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(EnterpriseWalletPageLocators.TRANSACTION_TABLE),
                    ExpectedConditions.visibilityOfElementLocated(EnterpriseWalletPageLocators.NO_DATA_MESSAGE)));
        } catch (Exception e) {
            System.out.println("Error navigating back: " + e.getMessage());
        }
    }
}
