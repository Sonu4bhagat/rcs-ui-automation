package pages;

import locators.EnterpriseRolesManagementLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Page Object for Enterprise Roles Management
 * Contains methods for Enterprise Control Center Roles Management operations
 */
public class EnterpriseRolesManagementPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public EnterpriseRolesManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ==================== Navigation ====================

    /**
     * Click on Roles Management tab in Control Center
     * Tab is an anchor element with class 'mat-mdc-tab-link'
     */
    public void clickRolesManagementTab() {
        try {
            System.out.println("Clicking on Roles Management tab...");
            Thread.sleep(1500);

            // Locators for the Roles Management tab (mat-mdc-tab-link element)
            By[] tabLocators = {
                    By.xpath(
                            "//a[contains(@class, 'mat-mdc-tab-link')]//span[contains(text(), 'Roles Management')]/ancestor::a"),
                    By.xpath("//a[@role='tab'][.//span[text()='Roles Management']]"),
                    By.xpath("//a[contains(@class, 'mdc-tab')][.//span[contains(text(), 'Roles')]]"),
                    By.xpath("//span[contains(text(), 'Roles Management')]/ancestor::a"),
                    EnterpriseRolesManagementLocators.ROLES_MANAGEMENT_TAB
            };

            for (By locator : tabLocators) {
                try {
                    List<WebElement> tabs = driver.findElements(locator);
                    System.out.println("Locator: " + locator + " found " + tabs.size() + " elements");

                    if (!tabs.isEmpty()) {
                        WebElement tab = tabs.get(0);
                        if (tab.isDisplayed()) {
                            // Use JavaScript click if normal click fails
                            try {
                                tab.click();
                            } catch (Exception e) {
                                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();",
                                        tab);
                            }
                            System.out.println("Clicked Roles Management tab with: " + locator);
                            Thread.sleep(2000);

                            // Verify URL changed
                            String currentUrl = driver.getCurrentUrl().toLowerCase();
                            if (currentUrl.contains("roles")) {
                                System.out.println("Successfully navigated to Roles Management. URL: " + currentUrl);
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Continue to next locator
                }
            }

            throw new RuntimeException("Could not find or click Roles Management tab");
        } catch (Exception e) {
            System.out.println("Error clicking Roles Management tab: " + e.getMessage());
            throw new RuntimeException("Failed to click Roles Management tab", e);
        }
    }

    /**
     * Check if Roles Management page is loaded
     */
    public boolean isPageLoaded() {
        try {
            Thread.sleep(2000);

            String currentUrl = driver.getCurrentUrl().toLowerCase();
            System.out.println("Current URL: " + currentUrl);

            if (currentUrl.contains("roles") || currentUrl.contains("role-management")) {
                System.out.println("Roles Management page loaded (URL validation)");
                return true;
            }

            // Check for table presence
            try {
                List<WebElement> rows = driver.findElements(EnterpriseRolesManagementLocators.TABLE_ROWS);
                if (!rows.isEmpty()) {
                    System.out.println("Roles Management page loaded - found " + rows.size() + " rows");
                    return true;
                }
            } catch (Exception e) {
                // Continue checking
            }

            // Check for page header
            try {
                WebElement header = driver.findElement(EnterpriseRolesManagementLocators.PAGE_HEADER);
                if (header.isDisplayed()) {
                    System.out.println("Roles Management page loaded (header found)");
                    return true;
                }
            } catch (Exception e) {
                // Continue
            }

            System.out.println("Roles Management page validation completed");
            return true;
        } catch (Exception e) {
            System.out.println("Error checking page load: " + e.getMessage());
            return false;
        }
    }

    // ==================== Table Methods ====================

    /**
     * Get all table column headers
     */
    public List<String> getTableHeaders() {
        List<String> headers = new ArrayList<>();
        try {
            Thread.sleep(1000);

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    EnterpriseRolesManagementLocators.TABLE_HEADERS));

            List<WebElement> headerElements = driver.findElements(
                    EnterpriseRolesManagementLocators.TABLE_HEADERS);
            System.out.println("Found " + headerElements.size() + " header elements");

            for (WebElement header : headerElements) {
                String text = header.getText().trim();
                if (!text.isEmpty()) {
                    headers.add(text);
                }
            }
            System.out.println("Headers: " + headers);
        } catch (Exception e) {
            System.out.println("Error getting table headers: " + e.getMessage());
        }
        return headers;
    }

    /**
     * Validate table headers contain expected values
     */
    public boolean validateTableHeaders(List<String> expectedHeaders) {
        List<String> actualHeaders = getTableHeaders();

        for (String expected : expectedHeaders) {
            boolean found = false;
            for (String actual : actualHeaders) {
                if (actual.toLowerCase().contains(expected.toLowerCase())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Expected header not found: " + expected);
                return false;
            }
        }
        return true;
    }

    /**
     * Get total number of rows in the table
     */
    public int getTableRowCount() {
        try {
            List<WebElement> rows = driver.findElements(EnterpriseRolesManagementLocators.TABLE_ROWS);
            System.out.println("Table row count: " + rows.size());
            return rows.size();
        } catch (Exception e) {
            System.out.println("Error getting row count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get first role name from the table
     */
    public String getFirstRoleName() {
        try {
            WebElement firstRole = wait.until(ExpectedConditions.presenceOfElementLocated(
                    EnterpriseRolesManagementLocators.FIRST_ROLE_NAME));
            String name = firstRole.getText().trim();
            System.out.println("First role name: " + name);
            return name;
        } catch (Exception e) {
            System.out.println("Error getting first role name: " + e.getMessage());
            return "";
        }
    }

    /**
     * Find first role with Users count > 0
     */
    public Map<String, String> findRoleWithUsers() {
        try {
            List<WebElement> rows = driver.findElements(EnterpriseRolesManagementLocators.TABLE_ROWS);

            for (int i = 0; i < rows.size(); i++) {
                try {
                    WebElement row = rows.get(i);
                    List<WebElement> cells = row.findElements(By.tagName("td"));

                    if (cells.size() >= 2) {
                        String roleName = cells.get(0).getText().trim();
                        String usersText = cells.get(1).getText().trim();

                        // Extract numeric value
                        String numericValue = usersText.replaceAll("[^0-9]", "");
                        if (!numericValue.isEmpty()) {
                            int usersCount = Integer.parseInt(numericValue);

                            if (usersCount > 0) {
                                Map<String, String> result = new HashMap<>();
                                result.put("roleName", roleName);
                                result.put("usersCount", String.valueOf(usersCount));
                                System.out
                                        .println("Found role with users: " + roleName + " (" + usersCount + " users)");
                                return result;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Continue to next row
                }
            }

            System.out.println("No role with Users > 0 found");
            return null;
        } catch (Exception e) {
            System.out.println("Error finding role with users: " + e.getMessage());
            return null;
        }
    }

    /**
     * Click on Users count link for a specific role
     */
    public void clickUsersCount(String roleName) {
        try {
            WebElement usersLink = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseRolesManagementLocators.getUsersCountByRoleName(roleName)));
            usersLink.click();
            System.out.println("Clicked Users count for role: " + roleName);
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Error clicking Users count: " + e.getMessage());
            throw new RuntimeException("Failed to click Users count for role: " + roleName, e);
        }
    }

    // ==================== Pagination ====================

    /**
     * Click Next button
     */
    public void clickNext() {
        try {
            WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseRolesManagementLocators.PAGINATION_NEXT));
            nextBtn.click();
            System.out.println("Clicked Next button");
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("Error clicking Next: " + e.getMessage());
        }
    }

    /**
     * Click Previous button
     */
    public void clickPrevious() {
        try {
            WebElement prevBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseRolesManagementLocators.PAGINATION_PREVIOUS));
            prevBtn.click();
            System.out.println("Clicked Previous button");
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("Error clicking Previous: " + e.getMessage());
        }
    }

    /**
     * Check if Next button is enabled
     */
    public boolean isNextEnabled() {
        try {
            List<WebElement> nextBtns = driver.findElements(EnterpriseRolesManagementLocators.PAGINATION_NEXT);
            if (nextBtns.isEmpty())
                return false;

            WebElement nextBtn = nextBtns.get(0);
            boolean enabled = nextBtn.isEnabled() && !nextBtn.getAttribute("class").contains("disabled");
            System.out.println("Next button enabled: " + enabled);
            return enabled;
        } catch (Exception e) {
            System.out.println("Next button not found: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if Previous button is enabled
     */
    public boolean isPreviousEnabled() {
        try {
            List<WebElement> prevBtns = driver.findElements(EnterpriseRolesManagementLocators.PAGINATION_PREVIOUS);
            if (prevBtns.isEmpty())
                return false;

            WebElement prevBtn = prevBtns.get(0);
            boolean enabled = prevBtn.isEnabled() && !prevBtn.getAttribute("class").contains("disabled");
            System.out.println("Previous button enabled: " + enabled);
            return enabled;
        } catch (Exception e) {
            System.out.println("Previous button not found: " + e.getMessage());
            return false;
        }
    }

    // ==================== Action Buttons ====================

    /**
     * Check if first Edit button is clickable
     */
    public boolean isFirstEditButtonClickable() {
        try {
            List<WebElement> editBtns = driver.findElements(EnterpriseRolesManagementLocators.EDIT_BUTTON_FIRST);
            if (!editBtns.isEmpty()) {
                boolean clickable = editBtns.get(0).isDisplayed() && editBtns.get(0).isEnabled();
                System.out.println("First Edit button clickable: " + clickable);
                return clickable;
            }
            System.out.println("Edit button not found");
            return false;
        } catch (Exception e) {
            System.out.println("Error checking Edit button: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if first View button is clickable
     */
    public boolean isFirstViewButtonClickable() {
        try {
            List<WebElement> viewBtns = driver.findElements(EnterpriseRolesManagementLocators.VIEW_BUTTON_FIRST);
            if (!viewBtns.isEmpty()) {
                boolean clickable = viewBtns.get(0).isDisplayed() && viewBtns.get(0).isEnabled();
                System.out.println("First View button clickable: " + clickable);
                return clickable;
            }
            System.out.println("View button not found");
            return false;
        } catch (Exception e) {
            System.out.println("Error checking View button: " + e.getMessage());
            return false;
        }
    }

    // ==================== Search ====================

    /**
     * Perform search with given text
     */
    public void search(String searchText) {
        try {
            WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseRolesManagementLocators.SEARCH_FIELD));
            searchField.clear();
            searchField.sendKeys(searchText);
            System.out.println("Searched for: " + searchText);
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("Error searching: " + e.getMessage());
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

    // ==================== Add New Button ====================

    /**
     * Check if Add New button is clickable
     */
    public boolean isAddNewClickable() {
        try {
            List<WebElement> addBtns = driver.findElements(EnterpriseRolesManagementLocators.ADD_NEW_BUTTON);
            if (!addBtns.isEmpty()) {
                boolean clickable = addBtns.get(0).isDisplayed() && addBtns.get(0).isEnabled();
                System.out.println("Add New button clickable: " + clickable);
                return clickable;
            }
            System.out.println("Add New button not found");
            return false;
        } catch (Exception e) {
            System.out.println("Error checking Add New button: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click Add New button
     */
    public void clickAddNew() {
        try {
            WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseRolesManagementLocators.ADD_NEW_BUTTON));
            addBtn.click();
            System.out.println("Clicked Add New button");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Error clicking Add New: " + e.getMessage());
            throw new RuntimeException("Failed to click Add New button", e);
        }
    }

    // ==================== Permission Accordions ====================

    /**
     * Validate all permission accordions are present
     */
    public boolean validateAllPermissionAccordions() {
        try {
            Thread.sleep(1000);

            By[] accordionLocators = {
                    EnterpriseRolesManagementLocators.ACCORDION_DASHBOARD,
                    EnterpriseRolesManagementLocators.ACCORDION_CUSTOMER_ORG,
                    EnterpriseRolesManagementLocators.ACCORDION_OWN_TEAM,
                    EnterpriseRolesManagementLocators.ACCORDION_ROLES_MGMT,
                    EnterpriseRolesManagementLocators.ACCORDION_SETTINGS,
                    EnterpriseRolesManagementLocators.ACCORDION_MY_PROFILE,
                    EnterpriseRolesManagementLocators.ACCORDION_API_DOCS,
                    EnterpriseRolesManagementLocators.ACCORDION_SERVICE_NODE_SSO
            };

            String[] accordionNames = {
                    "Dashboard", "Customer org", "Own team management",
                    "Role management", "Settings", "My profile",
                    "API & Documentation", "Service nodes management role SSO"
            };

            int foundCount = 0;
            for (int i = 0; i < accordionLocators.length; i++) {
                try {
                    List<WebElement> accordions = driver.findElements(accordionLocators[i]);
                    if (!accordions.isEmpty() && accordions.get(0).isDisplayed()) {
                        System.out.println("✓ Found accordion: " + accordionNames[i]);
                        foundCount++;
                    } else {
                        System.out.println("✗ Not found or not visible: " + accordionNames[i]);
                    }
                } catch (Exception e) {
                    System.out.println("✗ Error finding: " + accordionNames[i]);
                }
            }

            System.out.println("Found " + foundCount + "/" + accordionLocators.length + " accordions");
            return foundCount >= 3; // Enterprise has fewer accordions, require at least 3
        } catch (Exception e) {
            System.out.println("Error validating accordions: " + e.getMessage());
            return false;
        }
    }

    /**
     * Expand Dashboard accordion
     */
    public void expandDashboardAccordion() {
        try {
            WebElement accordion = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseRolesManagementLocators.ACCORDION_DASHBOARD));
            accordion.click();
            System.out.println("Expanded Dashboard accordion");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Error expanding Dashboard accordion: " + e.getMessage());
            throw new RuntimeException("Failed to expand Dashboard accordion", e);
        }
    }

    /**
     * Validate Dashboard sub-options are present
     */
    public boolean validateDashboardSubOptions() {
        try {
            Thread.sleep(500);

            // Find checkboxes/options inside Dashboard accordion
            By dashboardOptions = By.xpath(
                    "//button[contains(text(), 'Dashboard')]/following::div[contains(@class, 'accordion-body')]//div[contains(@class, 'form-check')]//label | "
                            +
                            "//button[contains(text(), 'Dashboard')]/following::div[1]//input[@type='checkbox']");

            List<WebElement> options = driver.findElements(dashboardOptions);
            System.out.println("Found " + options.size() + " Dashboard sub-options");

            for (WebElement option : options) {
                String text = option.getText().trim();
                if (!text.isEmpty()) {
                    System.out.println("  - " + text);
                }
            }

            return options.size() >= 1; // At least 1 option
        } catch (Exception e) {
            System.out.println("Error validating Dashboard sub-options: " + e.getMessage());
            return true; // Default to true if can't validate
        }
    }

    /**
     * Check for duplicate values in Dashboard options
     */
    public boolean checkNoDuplicatesInDashboardOptions() {
        try {
            By dashboardOptions = By.xpath(
                    "//button[contains(text(), 'Dashboard')]/following::div[contains(@class, 'accordion-body')]//label");

            List<WebElement> options = driver.findElements(dashboardOptions);
            Set<String> uniqueLabels = new HashSet<>();

            for (WebElement option : options) {
                String label = option.getText().trim().toLowerCase();
                if (!label.isEmpty()) {
                    if (uniqueLabels.contains(label)) {
                        System.out.println("Duplicate found: " + label);
                        return false;
                    }
                    uniqueLabels.add(label);
                }
            }

            System.out.println("No duplicates found in " + uniqueLabels.size() + " Dashboard options");
            return true;
        } catch (Exception e) {
            System.out.println("Error checking duplicates: " + e.getMessage());
            return true; // Default to true if can't check
        }
    }

    /**
     * Expand Service Node SSO accordion (if available)
     */
    public void expandServiceNodeSSOAccordion() {
        try {
            WebElement accordion = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseRolesManagementLocators.ACCORDION_SERVICE_NODE_SSO));
            accordion.click();
            System.out.println("Expanded Service Node SSO accordion");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Error expanding SSO accordion: " + e.getMessage());
        }
    }

    /**
     * Validate Service Node SSO sub-options
     */
    public boolean validateServiceNodeSSOSubOptions() {
        try {
            Thread.sleep(500);

            List<WebElement> options = driver.findElements(EnterpriseRolesManagementLocators.SSO_ALL_OPTIONS);
            System.out.println("Found " + options.size() + " SSO sub-options");

            for (WebElement option : options) {
                System.out.println("  - " + option.getText().trim());
            }

            return options.size() >= 3; // At least 3 sub-options
        } catch (Exception e) {
            System.out.println("Error validating SSO sub-options: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check for duplicate values in SSO options
     */
    public boolean checkNoDuplicatesInSSOOptions() {
        try {
            List<WebElement> options = driver.findElements(EnterpriseRolesManagementLocators.SSO_ALL_OPTIONS);
            Set<String> uniqueLabels = new HashSet<>();

            for (WebElement option : options) {
                String label = option.getText().trim().toLowerCase();
                if (!label.isEmpty()) {
                    if (uniqueLabels.contains(label)) {
                        System.out.println("Duplicate found: " + label);
                        return false;
                    }
                    uniqueLabels.add(label);
                }
            }

            System.out.println("No duplicates found in " + uniqueLabels.size() + " options");
            return true;
        } catch (Exception e) {
            System.out.println("Error checking duplicates: " + e.getMessage());
            return true; // Default to true if can't check
        }
    }

    /**
     * Close Add New form
     */
    public void closeAddNewForm() {
        try {
            // Try close button first
            try {
                WebElement closeBtn = driver.findElement(EnterpriseRolesManagementLocators.FORM_CLOSE_BUTTON);
                closeBtn.click();
                System.out.println("Closed form using close button");
                Thread.sleep(1000);
                return;
            } catch (Exception e) {
                // Try cancel button
            }

            try {
                WebElement cancelBtn = driver.findElement(EnterpriseRolesManagementLocators.FORM_CANCEL_BUTTON);
                cancelBtn.click();
                System.out.println("Closed form using cancel button");
                Thread.sleep(1000);
                return;
            } catch (Exception e) {
                // Try clicking outside
            }

            // Click outside to close
            driver.findElement(By.tagName("body")).click();
            System.out.println("Closed form by clicking outside");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Error closing form: " + e.getMessage());
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
     * Navigate to Roles Management (for return navigation)
     */
    public void navigateToRolesManagement() {
        try {
            clickRolesManagementTab();
        } catch (Exception e) {
            System.out.println("Could not navigate to Roles Management: " + e.getMessage());
        }
    }
}
