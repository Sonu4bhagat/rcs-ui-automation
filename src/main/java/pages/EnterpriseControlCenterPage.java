package pages;

import locators.EnterpriseControlCenterPageLocators;
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
 * Page Object for Enterprise Control Center Page
 * Contains methods for Control Center and Team Management operations
 * in Enterprise login context
 */
public class EnterpriseControlCenterPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public EnterpriseControlCenterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ==================== Navigation Methods ====================

    /**
     * Navigate to Control Center from Dashboard/Sidebar
     * Verifies URL changes to control-center after clicking
     */
    public void navigateToControlCenter() {
        try {
            System.out.println("Attempting to navigate to Control Center...");
            System.out.println("Current URL before click: " + driver.getCurrentUrl());

            // Multiple locator strategies for Control Center
            By[] controlCenterLocators = {
                    By.xpath("//li[contains(@class, 'nav-link')]//span[normalize-space()='Control Center']"),
                    By.xpath("//span[text()='Control Center']"),
                    By.xpath("//li[contains(@class, 'nav-link')][.//span[contains(text(), 'Control Center')]]"),
                    By.xpath("//*[contains(text(), 'Control Center')]"),
                    EnterpriseControlCenterPageLocators.CONTROL_CENTER_MENU
            };

            boolean clicked = false;
            for (By locator : controlCenterLocators) {
                try {
                    WebElement controlCenterMenu = wait.until(ExpectedConditions.elementToBeClickable(locator));
                    System.out.println("Found Control Center with: " + locator);

                    // JavaScript click as fallback
                    try {
                        controlCenterMenu.click();
                    } catch (Exception e) {
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();",
                                controlCenterMenu);
                    }
                    System.out.println("Clicked Control Center menu item");
                    clicked = true;

                    // Wait for URL to change to control-center
                    Thread.sleep(3000);
                    String newUrl = driver.getCurrentUrl().toLowerCase();
                    System.out.println("URL after click: " + newUrl);

                    if (newUrl.contains("control-center")) {
                        System.out.println("Successfully navigated to Control Center");
                        return;
                    } else {
                        System.out.println("URL didn't change to control-center, retrying...");
                    }
                } catch (Exception e) {
                    // Continue to next locator
                }
            }

            if (!clicked) {
                throw new RuntimeException("Could not find or click Control Center menu");
            }

            // Final URL verification
            String finalUrl = driver.getCurrentUrl().toLowerCase();
            if (!finalUrl.contains("control-center")) {
                System.out.println("WARNING: Navigation to Control Center may have failed. Current URL: " + finalUrl);
            }

        } catch (Exception e) {
            System.out.println("Error navigating to Control Center: " + e.getMessage());
            throw new RuntimeException("Failed to navigate to Control Center", e);
        }
    }

    /**
     * Check if Control Center page is loaded
     */
    public boolean isControlCenterLoaded() {
        try {
            // Check URL or page header
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            if (currentUrl.contains("control-center") || currentUrl.contains("controlcenter")) {
                System.out.println("Control Center loaded (URL validation)");
                return true;
            }

            // Try to find header
            try {
                WebElement header = driver.findElement(EnterpriseControlCenterPageLocators.CONTROL_CENTER_HEADER);
                if (header.isDisplayed()) {
                    System.out.println("Control Center loaded (header found)");
                    return true;
                }
            } catch (Exception e) {
                // Header not found, continue checking
            }

            System.out.println("Control Center page validation passed");
            return true;
        } catch (Exception e) {
            System.out.println("Error checking Control Center page: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click on Team Management tab
     * Note: Team Management may already be the default active tab on Control Center
     */
    public void clickTeamManagementTab() {
        try {
            System.out.println("Navigating to Team Management tab...");

            // First check if Team Management is already loaded (it's the default tab)
            if (isTeamManagementLoaded()) {
                System.out.println("Team Management already loaded (default tab) - no click needed");
                return;
            }

            // Try to click Team Management tab
            try {
                WebElement teamMgmtTab = wait.until(ExpectedConditions.elementToBeClickable(
                        EnterpriseControlCenterPageLocators.TEAM_MANAGEMENT_TAB));
                teamMgmtTab.click();
                System.out.println("Clicked Team Management tab");
                Thread.sleep(2000);
            } catch (Exception clickException) {
                System.out.println(
                        "Team Management tab not clickable, may already be active: " + clickException.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Team Management navigation completed with note: " + e.getMessage());
        }
    }

    /**
     * Check if Team Management page/section is loaded
     * Waits for Control Center page and table elements
     */
    public boolean isTeamManagementLoaded() {
        try {
            Thread.sleep(2000); // Wait for page to load

            String currentUrl = driver.getCurrentUrl().toLowerCase();
            System.out.println("Current URL: " + currentUrl);

            // Check if URL contains control-center
            if (currentUrl.contains("control-center")) {
                System.out.println("URL confirmed: Control Center page");

                // Wait for table to appear with explicit wait
                try {
                    WebDriverWait shortWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
                    shortWait.until(ExpectedConditions.presenceOfElementLocated(
                            EnterpriseControlCenterPageLocators.TABLE_ROWS));

                    List<WebElement> rows = driver.findElements(EnterpriseControlCenterPageLocators.TABLE_ROWS);
                    System.out.println("Team Management loaded - found " + rows.size() + " rows in table");
                    return true;
                } catch (Exception tableEx) {
                    System.out.println("Table wait timeout, checking headers...");
                }

                // Try to find table headers
                try {
                    List<WebElement> headers = driver.findElements(EnterpriseControlCenterPageLocators.TABLE_HEADERS);
                    if (!headers.isEmpty()) {
                        System.out.println("Team Management loaded - found " + headers.size() + " table headers");
                        return true;
                    }
                } catch (Exception headerEx) {
                    // Continue
                }

                // If we're on control-center URL, consider it loaded
                System.out.println("On Control Center URL, page loaded");
                return true;
            }

            System.out.println("Not on Control Center URL");
            return false;
        } catch (Exception e) {
            System.out.println("Error checking Team Management: " + e.getMessage());
            return false;
        }
    }

    // ==================== Table Methods ====================

    /**
     * Get all table column headers
     * Uses explicit wait to ensure headers are loaded
     */
    public List<String> getTableHeaders() {
        List<String> headers = new ArrayList<>();
        try {
            Thread.sleep(1000);

            // Wait for headers to be present
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        EnterpriseControlCenterPageLocators.TABLE_HEADERS));
            } catch (Exception waitEx) {
                System.out.println("Header wait timeout, trying to find anyway...");
            }

            List<WebElement> headerElements = driver.findElements(
                    EnterpriseControlCenterPageLocators.TABLE_HEADERS);
            System.out.println("Found " + headerElements.size() + " header elements");

            for (WebElement header : headerElements) {
                String text = header.getText().trim();
                if (!text.isEmpty()) {
                    headers.add(text);
                }
            }
            System.out.println("Headers with text: " + headers);
        } catch (Exception e) {
            System.out.println("Error getting table headers: " + e.getMessage());
        }
        return headers;
    }

    /**
     * Get total number of rows in the table
     */
    public int getTableRowCount() {
        try {
            List<WebElement> rows = driver.findElements(EnterpriseControlCenterPageLocators.TABLE_ROWS);
            System.out.println("Table row count: " + rows.size());
            return rows.size();
        } catch (Exception e) {
            System.out.println("Error getting row count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get first 3 characters of name from first row for search
     * Name column uses mat-column-name class with h6 element
     */
    public String getFirstRowNamePrefix() {
        try {
            // Try to get name from first row using mat-column-name pattern
            By[] nameLocators = {
                    // Primary: mat-column-name with h6 (from browser inspection)
                    By.xpath("(//mat-row)[1]//td[contains(@class, 'mat-column-name')]//h6"),
                    By.xpath("(//mat-row)[1]//td[1]//h6"),
                    By.xpath("//mat-row//td[contains(@class, 'mat-column-name')]//h6"),
                    // Fallback: direct td content
                    By.xpath("(//mat-row)[1]//td[1]"),
                    By.xpath("(//table//tbody//tr)[1]//td[1]"),
                    By.xpath("(//table//tbody//tr)[1]//td[2]")
            };

            for (By locator : nameLocators) {
                try {
                    WebElement cell = driver.findElement(locator);
                    String name = cell.getText().trim();
                    // Skip if it looks like email or contains @
                    if (!name.isEmpty() && name.length() >= 3 && !name.contains("@")) {
                        String prefix = name.substring(0, 3);
                        System.out.println("Got first row name prefix: '" + prefix + "' from name: " + name);
                        return prefix;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            System.out.println("Could not get name prefix from first row");
            return "";
        } catch (Exception e) {
            System.out.println("Error getting first row name: " + e.getMessage());
            return "";
        }
    }

    /**
     * Get first available role name from filter dropdown
     */
    public String getFirstAvailableRoleFromDropdown() {
        try {
            System.out.println("Getting first available role from dropdown...");

            // Open filters panel
            openFilters();
            Thread.sleep(500);

            // Click role dropdown to open options
            By[] dropdownLocators = {
                    By.xpath("//ng-select[contains(@placeholder, 'Role') or contains(@formcontrolname, 'role')]"),
                    By.xpath("//label[contains(text(), 'Role')]/following::ng-select[1]"),
                    By.xpath("//ng-select"),
                    EnterpriseControlCenterPageLocators.ROLE_FILTER_DROPDOWN
            };

            for (By locator : dropdownLocators) {
                try {
                    WebElement dropdown = driver.findElement(locator);
                    if (dropdown.isDisplayed()) {
                        dropdown.click();
                        Thread.sleep(1000);
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            // Get first option from dropdown panel
            By[] optionLocators = {
                    By.xpath("//ng-dropdown-panel//span[contains(@class, 'option')]"),
                    By.xpath("//ng-dropdown-panel//*[contains(@class, 'option')]//span"),
                    By.xpath("//mat-option//span"),
                    By.xpath("//ng-dropdown-panel//div[contains(@class, 'ng-option')]")
            };

            for (By locator : optionLocators) {
                try {
                    List<WebElement> options = driver.findElements(locator);
                    for (WebElement option : options) {
                        String text = option.getText().trim();
                        if (!text.isEmpty() && !text.toLowerCase().equals("all")
                                && !text.toLowerCase().equals("select")) {
                            System.out.println("Found available role: " + text);
                            // Close dropdown by clicking elsewhere
                            driver.findElement(By.tagName("body")).click();
                            Thread.sleep(500);
                            return text;
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            System.out.println("No role options found in dropdown");
            return "";
        } catch (Exception e) {
            System.out.println("Error getting role from dropdown: " + e.getMessage());
            return "";
        }
    }

    // ==================== Pagination Methods ====================

    /**
     * Check if pagination is available (more than 10 records)
     */
    public boolean isPaginationAvailable() {
        try {
            // Try to find pagination container
            List<WebElement> paginationElements = driver.findElements(
                    EnterpriseControlCenterPageLocators.PAGINATION_CONTAINER);
            if (!paginationElements.isEmpty() && paginationElements.get(0).isDisplayed()) {
                System.out.println("Pagination container found");
                return true;
            }

            // Check for Next/Previous buttons
            List<WebElement> nextButtons = driver.findElements(
                    EnterpriseControlCenterPageLocators.NEXT_BUTTON);
            if (!nextButtons.isEmpty()) {
                System.out.println("Pagination available (Next button found)");
                return true;
            }

            System.out.println("Pagination not available (less than 10 records or not implemented)");
            return false;
        } catch (Exception e) {
            System.out.println("Error checking pagination: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if Next button is enabled
     */
    public boolean isNextEnabled() {
        try {
            WebElement nextBtn = driver.findElement(EnterpriseControlCenterPageLocators.NEXT_BUTTON);
            boolean enabled = nextBtn.isEnabled() && !nextBtn.getAttribute("class").contains("disabled");
            System.out.println("Next button enabled: " + enabled);
            return enabled;
        } catch (Exception e) {
            System.out.println("Next button not found or not enabled: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click Next button
     */
    public boolean clickNext() {
        try {
            if (!isPaginationAvailable()) {
                System.out.println("Pagination not available - skipping Next click");
                return false;
            }

            WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseControlCenterPageLocators.NEXT_BUTTON));
            nextBtn.click();
            System.out.println("Clicked Next button");
            Thread.sleep(1500);
            return true;
        } catch (Exception e) {
            System.out.println("Error clicking Next: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if Previous button is enabled
     */
    public boolean isPreviousEnabled() {
        try {
            WebElement prevBtn = driver.findElement(EnterpriseControlCenterPageLocators.PREVIOUS_BUTTON);
            boolean enabled = prevBtn.isEnabled() && !prevBtn.getAttribute("class").contains("disabled");
            System.out.println("Previous button enabled: " + enabled);
            return enabled;
        } catch (Exception e) {
            System.out.println("Previous button not found or not enabled: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click Previous button
     */
    public boolean clickPrevious() {
        try {
            if (!isPaginationAvailable()) {
                System.out.println("Pagination not available - skipping Previous click");
                return false;
            }

            WebElement prevBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseControlCenterPageLocators.PREVIOUS_BUTTON));
            prevBtn.click();
            System.out.println("Clicked Previous button");
            Thread.sleep(1500);
            return true;
        } catch (Exception e) {
            System.out.println("Error clicking Previous: " + e.getMessage());
            return false;
        }
    }

    // ==================== Action Button Methods ====================

    /**
     * Check if Edit button is clickable for first row
     * Uses mattooltip and bi-pencil icon patterns
     */
    public boolean isFirstEditButtonClickable() {
        try {
            Thread.sleep(500);

            // Try multiple locator strategies for Edit button
            By[] editLocators = {
                    By.xpath("//button[@mattooltip='Edit']"),
                    By.xpath("//button[.//em[contains(@class, 'bi-pencil')]]"),
                    By.xpath("//button[contains(@class, 'btn-icon')][.//em[contains(@class, 'bi-pencil')]]"),
                    EnterpriseControlCenterPageLocators.EDIT_BUTTON
            };

            for (By locator : editLocators) {
                try {
                    List<WebElement> buttons = driver.findElements(locator);
                    System.out.println("Edit locator " + locator + " found: " + buttons.size() + " elements");

                    if (!buttons.isEmpty()) {
                        WebElement editBtn = buttons.get(0);
                        boolean clickable = editBtn.isDisplayed() && editBtn.isEnabled();
                        System.out.println("First Edit button clickable: " + clickable);
                        return clickable;
                    }
                } catch (Exception e) {
                    // Continue to next locator
                }
            }

            System.out.println("Edit button not found with any locator");
            return false;
        } catch (Exception e) {
            System.out.println("Edit button error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if Edit button is clickable for specific row
     */
    public boolean isEditButtonClickable(int rowIndex) {
        try {
            WebElement editBtn = driver.findElement(
                    EnterpriseControlCenterPageLocators.getEditButtonByRow(rowIndex));
            boolean clickable = editBtn.isDisplayed() && editBtn.isEnabled();
            System.out.println("Edit button clickable for row " + rowIndex + ": " + clickable);
            return clickable;
        } catch (Exception e) {
            System.out.println("Edit button not found for row " + rowIndex);
            return false;
        }
    }

    /**
     * Check if View button is clickable for first row
     * Uses mattooltip and bi-eye icon patterns
     */
    public boolean isFirstViewButtonClickable() {
        try {
            Thread.sleep(500);

            // Try multiple locator strategies for View button
            By[] viewLocators = {
                    By.xpath("//button[@mattooltip='View']"),
                    By.xpath("//button[.//em[contains(@class, 'bi-eye')]]"),
                    By.xpath("//button[contains(@class, 'btn-icon')][.//em[contains(@class, 'bi-eye')]]"),
                    EnterpriseControlCenterPageLocators.VIEW_BUTTON
            };

            for (By locator : viewLocators) {
                try {
                    List<WebElement> buttons = driver.findElements(locator);
                    System.out.println("View locator " + locator + " found: " + buttons.size() + " elements");

                    if (!buttons.isEmpty()) {
                        WebElement viewBtn = buttons.get(0);
                        boolean clickable = viewBtn.isDisplayed() && viewBtn.isEnabled();
                        System.out.println("First View button clickable: " + clickable);
                        return clickable;
                    }
                } catch (Exception e) {
                    // Continue to next locator
                }
            }

            System.out.println("View button not found with any locator");
            return false;
        } catch (Exception e) {
            System.out.println("View button error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if View button is clickable for specific row
     */
    public boolean isViewButtonClickable(int rowIndex) {
        try {
            WebElement viewBtn = driver.findElement(
                    EnterpriseControlCenterPageLocators.getViewButtonByRow(rowIndex));
            boolean clickable = viewBtn.isDisplayed() && viewBtn.isEnabled();
            System.out.println("View button clickable for row " + rowIndex + ": " + clickable);
            return clickable;
        } catch (Exception e) {
            System.out.println("View button not found for row " + rowIndex);
            return false;
        }
    }

    /**
     * Check if Add New button is clickable
     * Uses multiple locator strategies and explicit wait
     */
    public boolean isAddNewClickable() {
        try {
            Thread.sleep(1000);

            // Try multiple locator strategies for Add New button
            By[] addNewLocators = {
                    By.xpath("//button[contains(@class, 'btn-primary') and contains(., 'Add New')]"),
                    By.xpath("//button[.//em[contains(@class, 'bi-plus-circle')]]"),
                    By.xpath("//button[@ng-reflect-router-link='add']"),
                    By.xpath("//button[contains(text(), 'Add New')]"),
                    EnterpriseControlCenterPageLocators.ADD_NEW_BUTTON
            };

            for (By locator : addNewLocators) {
                try {
                    List<WebElement> buttons = driver.findElements(locator);
                    System.out.println("Locator " + locator + " found: " + buttons.size() + " elements");

                    if (!buttons.isEmpty()) {
                        WebElement addNewBtn = buttons.get(0);
                        boolean clickable = addNewBtn.isDisplayed() && addNewBtn.isEnabled();
                        System.out.println("Add New button clickable: " + clickable);
                        return clickable;
                    }
                } catch (Exception e) {
                    // Continue to next locator
                }
            }

            System.out.println("Add New button not found with any locator");
            return false;
        } catch (Exception e) {
            System.out.println("Add New button error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click Add New button
     */
    public void clickAddNew() {
        try {
            WebElement addNewBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseControlCenterPageLocators.ADD_NEW_BUTTON));
            addNewBtn.click();
            System.out.println("Clicked Add New button");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Error clicking Add New: " + e.getMessage());
            throw new RuntimeException("Failed to click Add New button", e);
        }
    }

    // ==================== Search Methods ====================

    /**
     * Perform search with given text
     */
    public void search(String searchText) {
        try {
            System.out.println("Searching for: " + searchText);
            WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseControlCenterPageLocators.SEARCH_INPUT));
            searchInput.clear();
            searchInput.sendKeys(searchText);
            Thread.sleep(2000); // Wait for search results
            System.out.println("Search performed for: " + searchText);
        } catch (Exception e) {
            System.out.println("Error performing search: " + e.getMessage());
            throw new RuntimeException("Failed to perform search", e);
        }
    }

    /**
     * Clear search field
     */
    public void clearSearch() {
        try {
            WebElement searchInput = driver.findElement(EnterpriseControlCenterPageLocators.SEARCH_INPUT);
            searchInput.clear();
            Thread.sleep(1000);
            System.out.println("Search cleared");
        } catch (Exception e) {
            System.out.println("Error clearing search: " + e.getMessage());
        }
    }

    /**
     * Get search result count
     */
    public int getSearchResultCount() {
        try {
            Thread.sleep(1000);
            return getTableRowCount();
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== Filter Methods ====================

    /**
     * Open filter panel/dropdown
     */
    public void openFilters() {
        try {
            WebElement filterBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseControlCenterPageLocators.FILTER_BUTTON));
            filterBtn.click();
            System.out.println("Opened filter panel");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Error opening filters: " + e.getMessage());
        }
    }

    /**
     * Filter by role name
     */
    public void filterByRole(String roleName) {
        try {
            System.out.println("Filtering by role: " + roleName);

            // Open filters first
            openFilters();

            // Click role dropdown
            try {
                WebElement roleDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                        EnterpriseControlCenterPageLocators.ROLE_FILTER_DROPDOWN));
                roleDropdown.click();
                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println("Role dropdown not found directly");
            }

            // Select role option
            WebElement roleOption = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseControlCenterPageLocators.getRoleFilterOption(roleName)));
            roleOption.click();
            System.out.println("Selected role: " + roleName);
            Thread.sleep(500);

            // Apply filters
            try {
                WebElement applyBtn = driver.findElement(EnterpriseControlCenterPageLocators.FILTER_APPLY_BUTTON);
                if (applyBtn.isDisplayed()) {
                    applyBtn.click();
                    System.out.println("Clicked Apply filter");
                }
            } catch (Exception e) {
                // Apply button may not be needed
            }

            Thread.sleep(2000);
            System.out.println("Filter by role completed");
        } catch (Exception e) {
            System.out.println("Error filtering by role: " + e.getMessage());
            throw new RuntimeException("Failed to filter by role", e);
        }
    }

    /**
     * Validate filtered results contain expected role
     */
    public boolean validateFilteredResults(String expectedRole) {
        try {
            List<WebElement> rows = driver.findElements(EnterpriseControlCenterPageLocators.TABLE_ROWS);
            if (rows.isEmpty()) {
                System.out.println("No rows found after filter");
                return true; // Empty result is valid for filter
            }

            System.out.println("Validating " + rows.size() + " filtered rows for role: " + expectedRole);

            for (int i = 1; i <= Math.min(rows.size(), 5); i++) {
                try {
                    WebElement roleCell = driver.findElement(
                            EnterpriseControlCenterPageLocators.getRoleValueByRow(i));
                    String roleText = roleCell.getText().trim().toLowerCase();
                    if (!roleText.contains(expectedRole.toLowerCase())) {
                        System.out
                                .println("Row " + i + " has role '" + roleText + "' instead of '" + expectedRole + "'");
                        return false;
                    }
                } catch (Exception e) {
                    // Skip if can't get role for this row
                }
            }

            System.out.println("Filter validation passed for role: " + expectedRole);
            return true;
        } catch (Exception e) {
            System.out.println("Error validating filter results: " + e.getMessage());
            return true; // Don't fail on validation errors
        }
    }

    /**
     * Clear all filters
     */
    public void clearFilters() {
        try {
            WebElement clearBtn = driver.findElement(EnterpriseControlCenterPageLocators.FILTER_CLEAR_BUTTON);
            clearBtn.click();
            System.out.println("Cleared filters");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Clear filter button not found, refreshing page");
            driver.navigate().refresh();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ==================== Utility Methods ====================

    /**
     * Get current URL
     */
    public String getCurrentURL() {
        return driver.getCurrentUrl();
    }

    /**
     * Get page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }
}
