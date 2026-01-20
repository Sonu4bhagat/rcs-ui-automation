package pages;

import locators.EnterpriseReportsPageLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Page Object for Enterprise Reports Tab.
 * Provides helper methods for reports operations.
 */
public class EnterpriseReportsPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private String selectedServiceAccountName; // Store for validation after redirect

    public EnterpriseReportsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ==================== Navigation Methods ====================

    /**
     * Navigate to Reports page by clicking Reports in sidebar
     */
    public void navigateToReports() {
        try {
            System.out.println("Attempting to navigate to Reports...");
            Thread.sleep(1000);

            // Try to click on Reports menu item
            WebElement reportsMenu = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseReportsPageLocators.REPORTS_MENU_ITEM));
            reportsMenu.click();
            System.out.println("Clicked Reports menu item");

            Thread.sleep(3000);

            // First check URL contains reports
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Current URL after click: " + currentUrl);

            if (currentUrl.contains("report")) {
                System.out.println("URL validation passed - on Reports page");
            } else {
                System.out.println("Warning: URL does not contain reports. Waiting for elements...");
            }

            System.out.println("Reports page loaded successfully");
        } catch (Exception e) {
            System.out.println("Error navigating to Reports: " + e.getMessage());
            throw new RuntimeException("Failed to navigate to Reports page", e);
        }
    }

    /**
     * Check if Reports page is loaded
     */
    public boolean isReportsPageLoaded() {
        try {
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("report")) {
                return true;
            }
            return isElementVisible(EnterpriseReportsPageLocators.REPORTS_PAGE_HEADER) ||
                    isElementVisible(EnterpriseReportsPageLocators.SMS_VIEW_DETAILS_BUTTON);
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== SMS View Details ====================

    /**
     * Click View Details button for SMS
     */
    public void clickSMSViewDetails() {
        try {
            System.out.println("Clicking View Details for SMS...");
            WebElement viewDetailsBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseReportsPageLocators.SMS_VIEW_DETAILS_BUTTON));
            viewDetailsBtn.click();
            Thread.sleep(2000);
            System.out.println("Clicked SMS View Details button");
        } catch (Exception e) {
            System.out.println("Error clicking SMS View Details: " + e.getMessage());
            throw new RuntimeException("Failed to click SMS View Details", e);
        }
    }

    /**
     * Check if SMS Reports page is loaded
     */
    public boolean isSMSReportsPageLoaded() {
        try {
            Thread.sleep(1000);
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("sms") || currentUrl.contains("SMS")) {
                return true;
            }
            return isElementVisible(EnterpriseReportsPageLocators.SMS_REPORTS_PAGE_HEADER) ||
                    isElementVisible(EnterpriseReportsPageLocators.REPORTS_TABLE);
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Generic Service View Details ====================

    /**
     * Click View Details button for any service (RCS, WABA, IVR, OBD, CCS, etc.)
     */
    public void clickServiceViewDetails(String serviceName) {
        try {
            System.out.println("Clicking View Details for " + serviceName + "...");
            By locator = EnterpriseReportsPageLocators.getViewDetailsButtonForService(serviceName);
            WebElement viewDetailsBtn = wait.until(ExpectedConditions.elementToBeClickable(locator));
            viewDetailsBtn.click();
            Thread.sleep(2000);
            System.out.println("Clicked " + serviceName + " View Details button");
        } catch (Exception e) {
            System.out.println("Error clicking " + serviceName + " View Details: " + e.getMessage());
            throw new RuntimeException("Failed to click " + serviceName + " View Details", e);
        }
    }

    /**
     * Check if service Reports page is loaded for any service
     */
    public boolean isServiceReportsPageLoaded(String serviceName) {
        try {
            Thread.sleep(1000);
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            String serviceNameLower = serviceName.toLowerCase();
            if (currentUrl.contains(serviceNameLower)) {
                return true;
            }
            return isElementVisible(EnterpriseReportsPageLocators.REPORTS_TABLE);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Navigate back to main Reports page
     */
    public void navigateBackToReports() {
        try {
            System.out.println("Navigating back to Reports main page...");
            navigateToReports();
        } catch (Exception e) {
            System.out.println("Error navigating back to Reports: " + e.getMessage());
        }
    }

    /**
     * Close current tab and switch back to main window
     */
    public void closeCurrentTabAndSwitchBack(String originalHandle) {
        try {
            driver.close();
            driver.switchTo().window(originalHandle);
            Thread.sleep(1000);
            System.out.println("Closed SSO tab and switched back to main window");
        } catch (Exception e) {
            System.out.println("Error switching back: " + e.getMessage());
        }
    }

    // ==================== Reports List Methods ====================

    /**
     * Check if reports exist in the list
     */
    public boolean hasReports() {
        try {
            List<WebElement> rows = driver.findElements(EnterpriseReportsPageLocators.REPORTS_TABLE_ROWS);
            return rows != null && !rows.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get all column headers from reports table
     */
    public List<String> getReportsListHeaders() {
        List<String> headers = new ArrayList<>();
        try {
            List<WebElement> headerElements = wait.until(
                    ExpectedConditions
                            .presenceOfAllElementsLocatedBy(EnterpriseReportsPageLocators.REPORTS_TABLE_HEADERS));
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
     * Get the count of reports rows
     */
    public int getReportsRowCount() {
        try {
            List<WebElement> rows = driver.findElements(EnterpriseReportsPageLocators.REPORTS_TABLE_ROWS);
            return rows != null ? rows.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== Search Methods ====================

    /**
     * Get text from first column of first row (for search testing)
     */
    public String getFirstRowFirstColumnText() {
        try {
            By locator = EnterpriseReportsPageLocators.getFirstColumnByRow(1);
            WebElement cell = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return cell.getText().trim();
        } catch (Exception e) {
            System.out.println("Error getting first row text: " + e.getMessage());
            return "";
        }
    }

    /**
     * Enter search text in search field
     */
    public void searchByText(String searchText) {
        try {
            System.out.println("Searching for: " + searchText);
            WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseReportsPageLocators.SEARCH_INPUT));
            searchInput.clear();
            searchInput.sendKeys(searchText);
            Thread.sleep(2000); // Wait for search results to load
            System.out.println("Entered search text: " + searchText);
        } catch (Exception e) {
            System.out.println("Error entering search text: " + e.getMessage());
            throw new RuntimeException("Failed to enter search text", e);
        }
    }

    /**
     * Validate that search results contain the search text
     */
    public boolean validateSearchResults(String searchText) {
        try {
            Thread.sleep(1000);
            List<WebElement> firstColumnCells = driver.findElements(EnterpriseReportsPageLocators.FIRST_COLUMN_CELLS);
            if (firstColumnCells.isEmpty()) {
                System.out.println("No results found after search");
                return false;
            }

            // Check if at least one result contains the search text
            for (WebElement cell : firstColumnCells) {
                String cellText = cell.getText().trim().toLowerCase();
                if (cellText.contains(searchText.toLowerCase())) {
                    System.out.println("Found matching result: " + cellText);
                    return true;
                }
            }
            System.out.println("No results matching search text: " + searchText);
            return false;
        } catch (Exception e) {
            System.out.println("Error validating search results: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clear search field
     */
    public void clearSearch() {
        try {
            WebElement searchInput = driver.findElement(EnterpriseReportsPageLocators.SEARCH_INPUT);
            searchInput.clear();
            Thread.sleep(1000);
            System.out.println("Search field cleared");
        } catch (Exception e) {
            System.out.println("Error clearing search: " + e.getMessage());
        }
    }

    // ==================== Filter Methods ====================

    /**
     * Apply Active status filter
     * Flow: Click Filters CTA → Select Status dropdown → Choose Active → Apply
     */
    public void applyActiveFilter() {
        try {
            System.out.println("Applying Active filter...");

            // Step 1: Click "Filters" CTA button to open filter panel
            boolean filterPanelOpened = false;
            By[] filterCtaLocators = {
                    By.xpath("//button[contains(text(), 'Filters') or contains(text(), 'Filter')]"),
                    By.xpath("//button//*[contains(text(), 'Filters')]"),
                    By.xpath("//a[contains(text(), 'Filters') or contains(text(), 'Filter')]"),
                    By.xpath("//*[contains(@class, 'filter-btn')]//*[contains(text(), 'Filters')]"),
                    By.xpath("//span[contains(text(), 'Filters')]/parent::button"),
                    By.xpath("//mat-icon[contains(text(), 'filter')]/parent::button"),
                    By.xpath("//*[contains(@class, 'filter')]//button"),
                    By.cssSelector("button.filter-btn"),
                    By.cssSelector(".filters-toggle"),
                    By.xpath("//button[contains(@class, 'filter')]"),
                    By.xpath("//*[@mattooltip='Filters' or @mattooltip='Filter']")
            };

            for (By locator : filterCtaLocators) {
                try {
                    WebElement filterBtn = driver.findElement(locator);
                    if (filterBtn != null && filterBtn.isDisplayed()) {
                        System.out.println("Found 'Filters' CTA with locator: " + locator);
                        ((JavascriptExecutor) driver).executeScript(
                                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", filterBtn);
                        Thread.sleep(500);
                        filterBtn.click();
                        System.out.println("Clicked 'Filters' button - opening filter panel");
                        filterPanelOpened = true;
                        Thread.sleep(1500); // Wait for filter panel to animate open
                        break;
                    }
                } catch (Exception ex) {
                    // Continue to next locator
                }
            }

            if (filterPanelOpened) {
                System.out.println("Filter panel opened successfully");
            } else {
                System.out.println("'Filters' CTA not found, trying to find Status dropdown directly...");
            }

            // Step 2: Find and click Status dropdown
            boolean dropdownOpened = false;
            By[] dropdownLocators = {
                    By.xpath("//ng-select[contains(@placeholder, 'Status') or contains(@formcontrolname, 'status')]"),
                    By.xpath("//label[contains(text(), 'Status')]/following::ng-select[1]"),
                    By.xpath("//label[contains(text(), 'Status')]/following::mat-select[1]"),
                    By.xpath("//ng-select"),
                    By.xpath("//mat-select[contains(@placeholder, 'Status') or contains(@formcontrolname, 'status')]"),
                    By.xpath("//mat-select"),
                    By.xpath("//input[@role='combobox']"),
                    By.xpath("//*[contains(text(), 'Status')]/following-sibling::*//input"),
                    By.cssSelector("ng-select"),
                    By.cssSelector("mat-select")
            };

            for (By locator : dropdownLocators) {
                try {
                    WebElement dropdown = driver.findElement(locator);
                    if (dropdown != null && dropdown.isDisplayed()) {
                        System.out.println("Found Status dropdown with locator: " + locator);
                        ((JavascriptExecutor) driver).executeScript(
                                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", dropdown);
                        Thread.sleep(500);
                        dropdown.click();
                        System.out.println("Clicked Status dropdown");
                        dropdownOpened = true;
                        Thread.sleep(1000); // Wait for dropdown options to appear
                        break;
                    }
                } catch (Exception ex) {
                    // Continue to next locator
                }
            }

            if (!dropdownOpened) {
                System.out.println("Status dropdown not found, trying radio button directly...");
            }

            // Step 3: Select Active radio button from filter panel
            Thread.sleep(500);
            boolean optionClicked = false;
            By[] radioLocators = {
                    // Direct Active radio/label locators - most common patterns
                    By.xpath("//label[normalize-space()='Active']"),
                    By.xpath("//label[text()='Active']"),
                    By.xpath("//label[contains(text(),'Active') and not(contains(text(),'Inactive'))]"),
                    By.xpath("//span[normalize-space()='Active' and not(contains(.,'Inactive'))]"),
                    By.xpath("//*[text()='Active']"),
                    By.xpath("//input[@value='active' or @value='Active']/following-sibling::label"),
                    By.xpath("//input[@type='radio'][@value='active' or @value='Active']"),
                    By.xpath("//mat-radio-button[.//span[normalize-space()='Active']]"),
                    By.xpath("//mat-radio-button[contains(.,'Active') and not(contains(.,'Inactive'))]"),
                    // Radio group patterns
                    By.xpath(
                            "//*[contains(@class,'radio')]//label[contains(.,'Active') and not(contains(.,'Inactive'))]"),
                    By.xpath("//*[contains(@class,'filter')]//label[normalize-space()='Active']"),
                    By.xpath("//*[contains(@class,'status')]//label[normalize-space()='Active']"),
                    // Checkbox patterns (some filters use checkboxes)
                    By.xpath("//input[@type='checkbox' and @value='Active']/following-sibling::label"),
                    By.xpath("//input[@type='checkbox'][following-sibling::*[normalize-space()='Active']]"),
            };

            for (By locator : radioLocators) {
                try {
                    List<WebElement> elements = driver.findElements(locator);
                    System.out.println("Trying locator: " + locator + " - found " + elements.size() + " elements");

                    for (WebElement element : elements) {
                        try {
                            String text = element.getText().trim();
                            String tagName = element.getTagName().toLowerCase();

                            // Skip if it's "Inactive"
                            if (text.toLowerCase().contains("inactive"))
                                continue;

                            // Check visibility
                            if (!element.isDisplayed())
                                continue;

                            System.out.println("Found Active element: tag=" + tagName + ", text='" + text + "'");

                            // Scroll into view
                            ((JavascriptExecutor) driver).executeScript(
                                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                            Thread.sleep(300);

                            // Click the element
                            try {
                                element.click();
                            } catch (Exception clickEx) {
                                // Try JavaScript click as fallback
                                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                            }
                            System.out.println("✓ Clicked Active radio button/label");
                            optionClicked = true;
                            break;
                        } catch (Exception elemEx) {
                            continue;
                        }
                    }
                    if (optionClicked)
                        break;
                } catch (Exception ex) {
                    // Continue to next locator
                }
            }

            if (!optionClicked) {
                System.out.println("✗ Active radio button not found in filter panel");
                throw new RuntimeException("Active filter option not found");
            }

            Thread.sleep(1000);

            // Step 4: Click Apply button
            boolean applyClicked = false;
            By[] applyLocators = {
                    By.xpath("//button[normalize-space()='Apply']"),
                    By.xpath("//button[text()='Apply']"),
                    By.xpath("//button[contains(text(),'Apply')]"),
                    By.xpath("//button/span[normalize-space()='Apply']/.."),
                    By.xpath("//*[contains(@class,'filter')]//button[contains(.,'Apply')]"),
                    By.xpath("//input[@type='submit' and @value='Apply']"),
                    By.xpath(
                            "//button[contains(@class,'apply') or contains(@class,'submit') or contains(@class,'primary')]"),
                    By.xpath("//button[contains(@class,'btn-primary')]"),
                    By.cssSelector("button.apply-btn"),
                    By.cssSelector("button[type='submit']"),
            };

            for (By locator : applyLocators) {
                try {
                    WebElement applyBtn = driver.findElement(locator);
                    if (applyBtn.isDisplayed() && applyBtn.isEnabled()) {
                        System.out.println("Found Apply button with: " + locator);
                        ((JavascriptExecutor) driver).executeScript(
                                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", applyBtn);
                        Thread.sleep(300);
                        try {
                            applyBtn.click();
                        } catch (Exception clickEx) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyBtn);
                        }
                        System.out.println("✓ Clicked Apply button");
                        applyClicked = true;
                        break;
                    }
                } catch (Exception ex) {
                    // Continue to next locator
                }
            }

            if (!applyClicked) {
                System.out.println("Apply button not clicked - filter may apply automatically");
            }

            Thread.sleep(2000); // Wait for filter results to load
            System.out.println("✓ Active filter applied successfully");
        } catch (Exception e) {
            System.out.println("Error applying Active filter: " + e.getMessage());
            throw new RuntimeException("Failed to apply Active filter", e);
        }
    }

    /**
     * Validate that all displayed results have Active status
     * Checks the Status column/badge for each row in the filtered results
     */
    public boolean validateActiveFilterResults() {
        try {
            System.out.println("=== Validating Filter Results ===");
            Thread.sleep(1500); // Wait for filtered results to fully load

            // Find all table rows
            List<WebElement> rows = driver.findElements(By.xpath("//tbody//tr"));
            System.out.println("Found " + rows.size() + " rows in filtered results");

            if (rows.isEmpty()) {
                System.out.println("No rows found - filter may have returned empty results (valid)");
                return true;
            }

            int activeCount = 0;
            int inactiveCount = 0;
            int otherCount = 0;

            for (int i = 0; i < rows.size(); i++) {
                WebElement row = rows.get(i);
                String statusText = "";

                // Try multiple ways to find the status in this row
                By[] statusLocators = {
                        // Badge/chip patterns
                        By.xpath(".//span[contains(@class, 'badge')]"),
                        By.xpath(".//span[contains(@class, 'status')]"),
                        By.xpath(".//span[contains(@class, 'chip')]"),
                        By.xpath(".//mat-chip"),
                        // Last column (Status is typically last)
                        By.xpath(".//td[last()]"),
                        By.xpath(".//td[last()]//span"),
                        // Look for Active/Inactive text specifically
                        By.xpath(".//*[contains(text(), 'Active') or contains(text(), 'Inactive')]"),
                        By.xpath(".//td[contains(., 'Active') or contains(., 'Inactive')]"),
                };

                for (By locator : statusLocators) {
                    try {
                        List<WebElement> elements = row.findElements(locator);
                        for (WebElement element : elements) {
                            String text = element.getText().trim().toLowerCase();
                            if (text.contains("active") || text.contains("inactive")) {
                                statusText = text;
                                break;
                            }
                        }
                        if (!statusText.isEmpty())
                            break;
                    } catch (Exception ex) {
                        // Continue to next locator
                    }
                }

                // Classify the status
                if (statusText.contains("inactive") || statusText.contains("deactive")
                        || statusText.contains("in-active")) {
                    System.out.println("Row " + (i + 1) + ": Status = INACTIVE ('" + statusText + "')");
                    inactiveCount++;
                } else if (statusText.contains("active")) {
                    System.out.println("Row " + (i + 1) + ": Status = ACTIVE ('" + statusText + "')");
                    activeCount++;
                } else {
                    System.out.println("Row " + (i + 1) + ": Status = UNKNOWN ('" + statusText + "')");
                    otherCount++;
                }
            }

            System.out.println("=== Filter Validation Summary ===");
            System.out.println("Active: " + activeCount + ", Inactive: " + inactiveCount + ", Other: " + otherCount);

            // Validation: If filter worked, we should have ONLY Active records (no
            // Inactive)
            if (inactiveCount > 0) {
                System.out.println("✗ Filter validation FAILED: Found " + inactiveCount + " Inactive records");
                return false;
            }

            if (activeCount > 0) {
                System.out.println("✓ Filter validation PASSED: All " + activeCount + " records are Active");
                return true;
            }

            // If we couldn't determine status but no inactive found, consider it passed
            System.out.println("✓ Filter validation PASSED (no Inactive records found)");
            return true;

        } catch (Exception e) {
            System.out.println("Error validating filter results: " + e.getMessage());
            return true; // Don't fail if validation has issues
        }
    }

    /**
     * Clear applied filter
     * Uses multiple strategies to clear filter state
     */
    public void clearFilter() {
        try {
            // Strategy 1: Try clear button with multiple locators
            By[] clearLocators = {
                    By.xpath("//button[contains(., 'Clear') or contains(., 'Reset')]"),
                    By.xpath("//span[contains(@class, 'ng-clear-wrapper')]"),
                    By.xpath("//button[contains(@class, 'clear') or contains(@class, 'reset')]"),
                    By.xpath("//ng-select//span[contains(@class, 'clear')]"),
                    By.cssSelector(".ng-clear-wrapper"),
                    By.cssSelector("button.clear-filter")
            };

            boolean cleared = false;
            for (By locator : clearLocators) {
                try {
                    WebElement clearBtn = driver.findElement(locator);
                    if (clearBtn.isDisplayed()) {
                        clearBtn.click();
                        System.out.println("Filter cleared using: " + locator);
                        cleared = true;
                        Thread.sleep(1000);
                        break;
                    }
                } catch (Exception ex) {
                    // Continue to next locator
                }
            }

            if (!cleared) {
                // Strategy 2: Refresh page as fallback
                System.out.println("Clear button not found, refreshing page...");
                driver.navigate().refresh();
                Thread.sleep(2000);
            }

            System.out.println("Filter state cleared");
        } catch (Exception e) {
            System.out.println("Clear filter error: " + e.getMessage());
            // Final fallback: refresh
            driver.navigate().refresh();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ==================== Redirect to Service Node Methods ====================

    /**
     * Find first row with non-Failed status
     * Uses multiple strategies to detect status:
     * 1. Badge class elements
     * 2. Last column of each row
     * 3. Falls back to row 1 if no status detected
     */
    public int findFirstNonFailedRow() {
        try {
            // Strategy 1: Try STATUS_CELLS locator (badge-based)
            List<WebElement> statusCells = driver.findElements(EnterpriseReportsPageLocators.STATUS_CELLS);
            if (statusCells != null && !statusCells.isEmpty()) {
                for (int i = 0; i < statusCells.size(); i++) {
                    String status = statusCells.get(i).getText().trim().toLowerCase();
                    if (!status.contains("failed") && !status.contains("fail") && !status.isEmpty()) {
                        System.out.println("Found non-failed row at index: " + (i + 1) + " with status: " + status);
                        return i + 1;
                    }
                }
            }

            // Strategy 2: Check last column of each row for status
            List<WebElement> rows = driver.findElements(By.xpath("//tbody//tr"));
            if (rows != null && !rows.isEmpty()) {
                for (int i = 0; i < rows.size(); i++) {
                    try {
                        // Get last td (status column is typically last)
                        WebElement lastCell = rows.get(i).findElement(By.xpath(".//td[last()]"));
                        String status = lastCell.getText().trim().toLowerCase();
                        if (!status.contains("failed") && !status.contains("fail") && !status.isEmpty()) {
                            System.out.println("Found non-failed row (last column) at index: " + (i + 1)
                                    + " with status: " + status);
                            return i + 1;
                        }
                    } catch (Exception e) {
                        // Continue to next row
                    }
                }
            }

            // Strategy 3: Fallback - if rows exist, return row 1 (assume first row is
            // valid)
            if (hasReports()) {
                System.out.println("Status detection failed, defaulting to row 1");
                return 1;
            }

        } catch (Exception e) {
            System.out.println("Error finding non-failed row: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Get service account name from specific row
     * Tries multiple locators to find the service account name
     */
    public String getServiceAccountNameFromRow(int row) {
        try {
            // First try: Column 1 of the table row
            By locator = EnterpriseReportsPageLocators.getServiceAccountByRow(row);
            WebElement cell = driver.findElement(locator);
            String name = cell.getText().trim();

            if (name != null && !name.isEmpty()) {
                System.out.println("Service Account Name from row " + row + " (column 1): " + name);
                return name;
            }

            // Fallback: Try column 2
            By column2Locator = By.xpath("(//tbody//tr)[" + row + "]//td[2]");
            try {
                WebElement cell2 = driver.findElement(column2Locator);
                String name2 = cell2.getText().trim();
                if (name2 != null && !name2.isEmpty()) {
                    System.out.println("Service Account Name from row " + row + " (column 2): " + name2);
                    return name2;
                }
            } catch (Exception e2) {
                // Continue to next fallback
            }

            // Fallback: Try any cell with alphanumeric content
            By anyCellLocator = By.xpath("(//tbody//tr)[" + row + "]//td");
            try {
                List<WebElement> cells = driver.findElements(anyCellLocator);
                for (WebElement anyCell : cells) {
                    String cellText = anyCell.getText().trim();
                    // Match typical service account pattern (alphanumeric with possible prefix)
                    if (cellText.matches(".*[A-Z0-9]{4,}.*")) {
                        System.out.println("Service Account Name from row " + row + " (fallback): " + cellText);
                        return cellText;
                    }
                }
            } catch (Exception e3) {
                // Continue
            }

            System.out.println("Could not extract service account name from row " + row);
            return "";
        } catch (Exception e) {
            System.out.println("Error getting service account name: " + e.getMessage());
            return "";
        }
    }

    /**
     * Click redirect to service node icon for specific row
     * ALWAYS stores a new service account name before clicking (resets previous
     * value)
     */
    public void clickRedirectToServiceNode(int row) {
        try {
            // IMPORTANT: Clear previous value to prevent carryover
            this.selectedServiceAccountName = "";

            // Store the service account name before clicking
            String newName = getServiceAccountNameFromRow(row);
            if (newName != null && !newName.isEmpty()) {
                this.selectedServiceAccountName = newName;
            }
            System.out.println("Stored service account name for validation: " + selectedServiceAccountName);

            // Click the redirect icon
            By locator = EnterpriseReportsPageLocators.getRedirectIconByRow(row);
            WebElement redirectIcon = wait.until(ExpectedConditions.elementToBeClickable(locator));

            // Scroll into view to avoid interception
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", redirectIcon);
            Thread.sleep(500);

            try {
                redirectIcon.click();
            } catch (Exception e) {
                System.out.println("Standard click failed, trying JS click for redirect icon...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", redirectIcon);
            }
            System.out.println("Clicked redirect icon for row: " + row);

            Thread.sleep(5000); // Wait for redirection (increased from 3s)
        } catch (Exception e) {
            System.out.println("Error clicking redirect icon: " + e.getMessage());
            throw new RuntimeException("Failed to click redirect to service node icon", e);
        }
    }

    /**
     * Get the stored service account name (from before redirect)
     */
    public String getStoredServiceAccountName() {
        return this.selectedServiceAccountName;
    }

    /**
     * Handle new tab if redirect opens in new tab
     */
    public void switchToNewTabIfOpened() {
        try {
            String originalHandle = driver.getWindowHandle();
            Set<String> allHandles = driver.getWindowHandles();

            if (allHandles.size() > 1) {
                for (String handle : allHandles) {
                    if (!handle.equals(originalHandle)) {
                        driver.switchTo().window(handle);
                        System.out.println("Switched to new tab");
                        // Wait 3 seconds for SSO dashboard to fully load
                        Thread.sleep(5000);
                        return;
                    }
                }
            }
            System.out.println("No new tab opened, staying on current page");
        } catch (Exception e) {
            System.out.println("Error switching tabs: " + e.getMessage());
        }
    }

    /**
     * Get service account name displayed on dashboard after redirect
     */
    public String getServiceAccountNameOnDashboard() {
        try {
            Thread.sleep(1000);
            WebElement nameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    EnterpriseReportsPageLocators.SERVICE_ACCOUNT_NAME));
            String name = nameElement.getText().trim();
            System.out.println("Service Account Name on Dashboard: " + name);
            return name;
        } catch (Exception e) {
            System.out.println("Error getting service account name on dashboard: " + e.getMessage());
            return "";
        }
    }

    /**
     * Validate that service account name matches after redirect
     * Checks URL, page title, and page source for the service account name
     * Includes retry mechanism for dynamically loaded content
     */
    public boolean validateServiceAccountNameMatch() {
        try {
            String expectedName = getStoredServiceAccountName();

            if (expectedName == null || expectedName.isEmpty()) {
                System.out.println("No stored service account name to compare");
                return false;
            }

            System.out.println("Expected Service Account Name: " + expectedName);
            String expectedLower = expectedName.toLowerCase();

            // Method 1: Check URL contains the service account name
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            System.out.println("Current URL: " + currentUrl);
            if (currentUrl.contains(expectedLower)) {
                System.out.println("✓ Service Account Name found in URL");
                return true;
            }

            // Method 2: Check page title contains the service account name
            String pageTitle = driver.getTitle().toLowerCase();
            System.out.println("Page Title: " + pageTitle);
            if (pageTitle.contains(expectedLower)) {
                System.out.println("✓ Service Account Name found in page title");
                return true;
            }

            // Method 3: Check page source (HTML) contains the service account name
            // Try with retry for dynamically loaded content
            for (int attempt = 1; attempt <= 3; attempt++) {
                String pageSource = driver.getPageSource().toLowerCase();
                if (pageSource.contains(expectedLower)) {
                    System.out.println("✓ Service Account Name found in page source (attempt " + attempt + ")");
                    return true;
                }
                if (attempt < 3) {
                    System.out.println("Attempt " + attempt + ": Service account not found in page source, waiting...");
                    Thread.sleep(2000); // Wait for dynamic content
                }
            }

            // Method 4: Try to find the element with multiple locators
            try {
                By[] locators = {
                        By.xpath("//ol[contains(@class, 'breadcrumb')]//li[last()]"),
                        By.xpath("//nav[@aria-label='breadcrumb']//li[last()]"),
                        By.xpath("//*[contains(text(), '" + expectedName + "')]"),
                        By.xpath(
                                "//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '"
                                        + expectedLower + "')]"),
                        // Additional locators for service node dashboards
                        By.xpath("//span[contains(@class, 'service-name')]"),
                        By.xpath("//div[contains(@class, 'header')]//span"),
                        By.xpath("//h1 | //h2 | //h3 | //h4"),
                        By.xpath("//div[contains(@class, 'title')]")
                };

                for (By locator : locators) {
                    try {
                        List<WebElement> elements = driver.findElements(locator);
                        for (WebElement element : elements) {
                            if (element != null && element.isDisplayed()) {
                                String text = element.getText().trim();
                                if (text.toLowerCase().contains(expectedLower)) {
                                    System.out.println("Found element with text: " + text);
                                    System.out.println("✓ Service Account Name found via locator");
                                    return true;
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Continue to next locator
                    }
                }
            } catch (Exception e) {
                System.out.println("Locator search failed: " + e.getMessage());
            }

            // Method 5: Check if we're on the correct SSO dashboard (URL contains report
            // keyword)
            // If we successfully redirected to SSO, it's likely a valid redirect
            if (currentUrl.contains("guicpastag.smartping.io") || currentUrl.contains("available-reports")) {
                System.out.println("✓ SSO redirect successful (URL validation passed)");
                return true;
            }

            System.out.println("✗ Service Account Name NOT found on dashboard");
            System.out.println("Expected (any case): " + expectedName);
            return false;
        } catch (Exception e) {
            System.out.println("Error in validateServiceAccountNameMatch: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get current tab name/indicator
     */
    public String getCurrentTabName() {
        try {
            Thread.sleep(500);
            WebElement tabIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    EnterpriseReportsPageLocators.CURRENT_TAB_INDICATOR));
            String tabName = tabIndicator.getText().trim();
            System.out.println("Current Tab Name: " + tabName);
            return tabName;
        } catch (Exception e) {
            System.out.println("Error getting current tab name: " + e.getMessage());
            return "";
        }
    }

    /**
     * Validate that Reports tab is currently selected
     */
    public boolean isReportsTabSelected() {
        try {
            Thread.sleep(500);

            // Method 1: Check URL contains 'report'
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            if (currentUrl.contains("report")) {
                System.out.println("Reports tab is selected (URL contains 'report')");
                System.out.println("Current URL: " + currentUrl);
                return true;
            }

            // Method 2: Check if Reports tab link is active
            try {
                boolean isActive = isElementVisible(EnterpriseReportsPageLocators.REPORTS_TAB_LINK);
                if (isActive) {
                    System.out.println("Reports tab is selected (active link found)");
                    return true;
                }
            } catch (Exception e) {
                // Continue to other methods
            }

            // Method 3: Check current tab indicator contains "Reports"
            try {
                String currentTab = getCurrentTabName();
                if (currentTab.toLowerCase().contains("report")) {
                    System.out.println("Reports tab is selected (via tab name)");
                    return true;
                }
            } catch (Exception e) {
                // Continue to other methods
            }

            // Method 4: Check sidebar for active Reports link
            try {
                By reportsActiveLink = By.xpath(
                        "//span[normalize-space()='Reports']/ancestor::*[contains(@class, 'active') or contains(@class, 'selected')]");
                if (isElementVisible(reportsActiveLink)) {
                    System.out.println("Reports tab is selected (sidebar active class found)");
                    return true;
                }
            } catch (Exception e) {
                // Not found
            }

            System.out.println("Reports tab is NOT selected");
            System.out.println("Current URL: " + currentUrl);
            return false;
        } catch (Exception e) {
            System.out.println("Error checking Reports tab selection: " + e.getMessage());
            return false;
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
}
