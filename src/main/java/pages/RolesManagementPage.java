package pages;

import locators.RolesManagementPageLocators;
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
import java.util.Set;

/**
 * Page Object for Roles Management Page
 * Contains methods for Super Admin Roles Management operations
 */
public class RolesManagementPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public RolesManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ========== Navigation Methods ==========

    /**
     * Navigate to Roles Management page from dashboard
     */
    public void navigateToRolesManagement() {
        System.out.println("Navigating to Roles Management page...");
        try {
            WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(
                    RolesManagementPageLocators.ROLES_MANAGEMENT_MENU));
            menu.click();

            // Wait for URL to change
            Thread.sleep(2000);

            // Validate navigation by URL
            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.contains("roles-management")) {
                throw new RuntimeException("Failed to navigate to Roles Management page. Current URL: " + currentUrl);
            }

            Thread.sleep(1000);
            System.out.println("✓ Navigated to Roles Management page");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted: " + e.getMessage());
            throw new RuntimeException("Navigation to Roles Management failed", e);
        } catch (Exception e) {
            System.err.println("Failed to navigate to Roles Management: " + e.getMessage());
            throw new RuntimeException("Navigation to Roles Management failed", e);
        }
    }

    /**
     * Verify the Roles Management page is loaded
     */
    public boolean isPageLoaded() {
        try {
            Thread.sleep(1000);
            String currentUrl = driver.getCurrentUrl();
            boolean loaded = currentUrl.contains("roles-management");

            if (loaded) {
                System.out.println("✓ Roles Management page loaded. URL: " + currentUrl);
            }

            return loaded;
        } catch (Exception e) {
            return false;
        }
    }

    // ========== Table Methods ==========

    /**
     * Get all table column headers
     */
    public List<String> getTableHeaders() {
        System.out.println("Retrieving table headers...");
        try {
            List<WebElement> headers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    RolesManagementPageLocators.TABLE_HEADERS));

            List<String> headerTexts = new ArrayList<>();
            for (WebElement header : headers) {
                String text = header.getText().trim();
                if (!text.isEmpty()) {
                    headerTexts.add(text);
                }
            }

            System.out.println("✓ Found " + headerTexts.size() + " headers: " + headerTexts);
            return headerTexts;
        } catch (Exception e) {
            System.err.println("Failed to get table headers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Validate table headers contain expected values
     */
    public boolean validateTableHeaders(List<String> expectedHeaders) {
        System.out.println("Validating table headers...");
        List<String> actualHeaders = getTableHeaders();

        for (String expected : expectedHeaders) {
            boolean found = false;
            for (String actual : actualHeaders) {
                if (actual.contains(expected)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.err.println("✗ Expected header '" + expected + "' not found");
                return false;
            }
        }

        System.out.println("✓ All expected headers found");
        return true;
    }

    /**
     * Get total number of rows in the table
     */
    public int getTableRowCount() {
        try {
            List<WebElement> rows = driver.findElements(RolesManagementPageLocators.TABLE_ROWS);
            int count = rows.size();
            System.out.println("Table has " + count + " row(s)");
            return count;
        } catch (Exception e) {
            System.err.println("Failed to get row count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get first role name from the table
     */
    public String getFirstRoleName() {
        System.out.println("Retrieving first role name...");
        try {
            WebElement firstRole = wait.until(ExpectedConditions.presenceOfElementLocated(
                    RolesManagementPageLocators.FIRST_ROLE_NAME));
            String roleName = firstRole.getText().trim();
            System.out.println("✓ First role name: " + roleName);
            return roleName;
        } catch (Exception e) {
            System.err.println("Failed to get first role name: " + e.getMessage());
            return "";
        }
    }

    // ========== Users Count Navigation Methods ==========

    /**
     * Find first role with Users count > 0
     * 
     * @return Map with "roleName" and "usersCount" keys, or null if none found
     */
    public java.util.Map<String, String> findRoleWithUsers() {
        System.out.println("Finding role with Users > 0...");
        try {
            List<WebElement> rows = driver.findElements(RolesManagementPageLocators.TABLE_ROWS);

            for (WebElement row : rows) {
                try {
                    // Get role name (1st column)
                    WebElement roleNameCell = row.findElement(By.xpath(".//td[1]"));
                    String roleName = roleNameCell.getText().trim();

                    // Get Users count cell (2nd column)
                    WebElement usersCell = row.findElement(By.xpath(".//td[2]"));

                    // Check if there's a clickable link
                    List<WebElement> links = usersCell.findElements(By.xpath(".//a"));
                    if (links.size() > 0) {
                        String countText = links.get(0).getText().trim();
                        int count = Integer.parseInt(countText);

                        if (count > 0) {
                            java.util.Map<String, String> result = new java.util.HashMap<>();
                            result.put("roleName", roleName);
                            result.put("usersCount", String.valueOf(count));
                            System.out.println("✓ Found role: " + roleName + " with " + count + " user(s)");
                            return result;
                        }
                    }
                } catch (Exception e) {
                    // Skip this row if parsing fails
                    continue;
                }
            }

            System.err.println("✗ No role found with Users > 0");
            return null;
        } catch (Exception e) {
            System.err.println("Failed to find role with users: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get Users count for a specific role
     * 
     * @param roleName Name of the role
     * @return Users count as integer, or -1 if not found
     */
    public int getUsersCountForRole(String roleName) {
        System.out.println("Getting Users count for role: " + roleName);
        try {
            WebElement countElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    RolesManagementPageLocators.getUsersCountValueByRoleName(roleName)));
            String countText = countElement.getText().trim();
            int count = Integer.parseInt(countText);
            System.out.println("✓ Users count for '" + roleName + "': " + count);
            return count;
        } catch (Exception e) {
            System.err.println("Failed to get Users count for role '" + roleName + "': " + e.getMessage());
            return -1;
        }
    }

    /**
     * Click on Users count link for a specific role
     * 
     * @param roleName Name of the role
     */
    public void clickUsersCount(String roleName) {
        System.out.println("Clicking Users count for role: " + roleName);
        try {
            WebElement usersLink = wait.until(ExpectedConditions.elementToBeClickable(
                    RolesManagementPageLocators.getUsersCountByRoleName(roleName)));
            usersLink.click();
            Thread.sleep(2000); // Wait for navigation
            System.out.println("✓ Clicked Users count for '" + roleName + "'");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Click Users count failed", e);
        } catch (Exception e) {
            System.err.println("Failed to click Users count for role '" + roleName + "': " + e.getMessage());
            throw new RuntimeException("Click Users count failed", e);
        }
    }

    // ========== Pagination Methods ==========

    /**
     * Click the Next button in pagination
     */
    public void clickNext() {
        System.out.println("Clicking Next button...");
        try {
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                    RolesManagementPageLocators.PAGINATION_NEXT));
            nextButton.click();
            Thread.sleep(2000); // Wait for page to load
            System.out.println("✓ Clicked Next button");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Click Next failed", e);
        } catch (Exception e) {
            System.err.println("Failed to click Next: " + e.getMessage());
            throw new RuntimeException("Click Next failed", e);
        }
    }

    /**
     * Click the Previous button in pagination
     */
    public void clickPrevious() {
        System.out.println("Clicking Previous button...");
        try {
            WebElement prevButton = wait.until(ExpectedConditions.elementToBeClickable(
                    RolesManagementPageLocators.PAGINATION_PREVIOUS));
            prevButton.click();
            Thread.sleep(2000); // Wait for page to load
            System.out.println("✓ Clicked Previous button");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Click Previous failed", e);
        } catch (Exception e) {
            System.err.println("Failed to click Previous: " + e.getMessage());
            throw new RuntimeException("Click Previous failed", e);
        }
    }

    /**
     * Check if Next button is enabled/clickable
     */
    public boolean isNextEnabled() {
        try {
            WebElement nextButton = driver.findElement(RolesManagementPageLocators.PAGINATION_NEXT);
            boolean enabled = nextButton.isEnabled() &&
                    !nextButton.getDomAttribute("class").contains("disabled");
            System.out.println("Next button enabled: " + enabled);
            return enabled;
        } catch (Exception e) {
            System.err.println("Failed to check Next button status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if Previous button is enabled/clickable
     */
    public boolean isPreviousEnabled() {
        try {
            WebElement prevButton = driver.findElement(RolesManagementPageLocators.PAGINATION_PREVIOUS);
            boolean enabled = prevButton.isEnabled() &&
                    !prevButton.getDomAttribute("class").contains("disabled");
            System.out.println("Previous button enabled: " + enabled);
            return enabled;
        } catch (Exception e) {
            System.err.println("Failed to check Previous button status: " + e.getMessage());
            return false;
        }
    }

    // ========== Action Buttons Methods ==========

    /**
     * Check if first Edit button is clickable
     */
    public boolean isFirstEditButtonClickable() {
        System.out.println("Checking first Edit button...");
        try {
            WebElement editButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    RolesManagementPageLocators.EDIT_BUTTON_FIRST));
            boolean clickable = editButton.isEnabled() && editButton.isDisplayed();
            System.out.println("✓ First Edit button clickable: " + clickable);
            return clickable;
        } catch (Exception e) {
            System.err.println("Failed to check first Edit button: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if first View button is clickable
     */
    public boolean isFirstViewButtonClickable() {
        System.out.println("Checking first View button...");
        try {
            WebElement viewButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    RolesManagementPageLocators.VIEW_BUTTON_FIRST));
            boolean clickable = viewButton.isEnabled() && viewButton.isDisplayed();
            System.out.println("✓ First View button clickable: " + clickable);
            return clickable;
        } catch (Exception e) {
            System.err.println("Failed to check first View button: " + e.getMessage());
            return false;
        }
    }

    // ========== Search Methods ==========

    /**
     * Perform search with given text
     */
    public void search(String searchText) {
        System.out.println("Searching for: " + searchText);
        try {
            WebElement searchField = wait.until(ExpectedConditions.presenceOfElementLocated(
                    RolesManagementPageLocators.SEARCH_FIELD));
            searchField.clear();
            searchField.sendKeys(searchText);
            Thread.sleep(2000); // Wait for search results
            System.out.println("✓ Search performed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Search failed", e);
        } catch (Exception e) {
            System.err.println("Failed to perform search: " + e.getMessage());
            throw new RuntimeException("Search failed", e);
        }
    }

    /**
     * Get current row count after search
     */
    public int getSearchResultCount() {
        try {
            Thread.sleep(1000);
            return getTableRowCount();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        }
    }

    // ========== Add New Button Methods ==========

    /**
     * Check if Add New button is clickable
     */
    public boolean isAddNewClickable() {
        System.out.println("Checking Add New button...");
        try {
            WebElement addNewButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    RolesManagementPageLocators.ADD_NEW_BUTTON));
            boolean clickable = addNewButton.isEnabled() && addNewButton.isDisplayed();
            System.out.println("✓ Add New button clickable: " + clickable);
            return clickable;
        } catch (Exception e) {
            System.err.println("Failed to check Add New button: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click Add New button
     */
    public void clickAddNew() {
        System.out.println("Clicking Add New button...");
        try {
            WebElement addNewButton = wait.until(ExpectedConditions.elementToBeClickable(
                    RolesManagementPageLocators.ADD_NEW_BUTTON));
            addNewButton.click();
            Thread.sleep(2000); // Wait for form to load
            System.out.println("✓ Clicked Add New button - Form opened");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Click Add New failed", e);
        } catch (Exception e) {
            System.err.println("Failed to click Add New: " + e.getMessage());
            throw new RuntimeException("Click Add New failed", e);
        }
    }

    // ========== Add New Form Validation Methods ==========

    /**
     * Validate all permission accordions are present
     */
    public boolean validateAllPermissionAccordions() {
        System.out.println("Validating all permission accordions...");
        boolean allPresent = true;

        try {
            // Check Dashboard
            if (!isElementPresent(RolesManagementPageLocators.ACCORDION_DASHBOARD)) {
                System.err.println("✗ Dashboard accordion not found");
                allPresent = false;
            } else {
                System.out.println("✓ Dashboard accordion present");
            }

            // Check Customer Organisation
            if (!isElementPresent(RolesManagementPageLocators.ACCORDION_CUSTOMER_ORG)) {
                System.err.println("✗ Customer Organisation accordion not found");
                allPresent = false;
            } else {
                System.out.println("✓ Customer Organisation accordion present");
            }

            // Check Own Team Management
            if (!isElementPresent(RolesManagementPageLocators.ACCORDION_OWN_TEAM)) {
                System.err.println("✗ Own Team Management accordion not found");
                allPresent = false;
            } else {
                System.out.println("✓ Own Team Management accordion present");
            }

            // Check Roles Management
            if (!isElementPresent(RolesManagementPageLocators.ACCORDION_ROLES_MGMT)) {
                System.err.println("✗ Roles Management accordion not found");
                allPresent = false;
            } else {
                System.out.println("✓ Roles Management accordion present");
            }

            // Check Settings
            if (!isElementPresent(RolesManagementPageLocators.ACCORDION_SETTINGS)) {
                System.err.println("✗ Settings accordion not found");
                allPresent = false;
            } else {
                System.out.println("✓ Settings accordion present");
            }

            // Check My Profile
            if (!isElementPresent(RolesManagementPageLocators.ACCORDION_MY_PROFILE)) {
                System.err.println("✗ My Profile accordion not found");
                allPresent = false;
            } else {
                System.out.println("✓ My Profile accordion present");
            }

            // Check API & Documentation
            if (!isElementPresent(RolesManagementPageLocators.ACCORDION_API_DOCS)) {
                System.err.println("✗ API & Documentation accordion not found");
                allPresent = false;
            } else {
                System.out.println("✓ API & Documentation accordion present");
            }

            // Check Service Node SSO Management
            if (!isElementPresent(RolesManagementPageLocators.ACCORDION_SERVICE_NODE_SSO)) {
                System.err.println("✗ Service Node SSO Management accordion not found");
                allPresent = false;
            } else {
                System.out.println("✓ Service Node SSO Management accordion present");
            }

        } catch (Exception e) {
            System.err.println("Error validating accordions: " + e.getMessage());
            return false;
        }

        if (allPresent) {
            System.out.println("✓ All 8 permission accordions are present");
        }

        return allPresent;
    }

    /**
     * Expand Service Node SSO accordion
     */
    public void expandServiceNodeSSOAccordion() {
        System.out.println("Expanding Service Node SSO accordion...");
        try {
            WebElement accordion = wait.until(ExpectedConditions.elementToBeClickable(
                    RolesManagementPageLocators.ACCORDION_SERVICE_NODE_SSO));

            // Check if it's already expanded
            String ariaExpanded = accordion.getDomAttribute("aria-expanded");
            if (!"true".equals(ariaExpanded)) {
                accordion.click();
                Thread.sleep(1000);
                System.out.println("✓ Service Node SSO accordion expanded");
            } else {
                System.out.println("✓ Service Node SSO accordion already expanded");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Expand accordion failed", e);
        } catch (Exception e) {
            System.err.println("Failed to expand Service Node SSO accordion: " + e.getMessage());
            throw new RuntimeException("Expand accordion failed", e);
        }
    }

    /**
     * Validate Service Node SSO sub-options are present
     */
    public boolean validateServiceNodeSSOSubOptions() {
        System.out.println("Validating Service Node SSO sub-options...");
        boolean allPresent = true;

        String[] expectedOptions = {
                "Service nodes SSO",
                "Only for SMS",
                "Only for OBD",
                "Only for IVR",
                "Only for CCS",
                "Only for WABA",
                "Only for RCS",
                "Only for Live Agent"
        };

        try {
            for (String option : expectedOptions) {
                // Use p tags with normalize-space() to match labels
                boolean found = driver.findElements(By.xpath(
                        "//p[normalize-space()='" + option + "']")).size() > 0;

                if (found) {
                    System.out.println("✓ " + option + " - present");
                } else {
                    System.err.println("✗ " + option + " - NOT FOUND");
                    allPresent = false;
                }
            }
        } catch (Exception e) {
            System.err.println("Error validating sub-options: " + e.getMessage());
            return false;
        }

        if (allPresent) {
            System.out.println("✓ All 8 Service Node SSO sub-options are present");
        }

        return allPresent;
    }

    /**
     * Check for duplicate values in Service Node SSO options
     */
    public boolean checkNoDuplicatesInSSOOptions() {
        System.out.println("Checking for duplicate values in SSO options...");
        try {
            List<WebElement> allOptions = driver.findElements(
                    RolesManagementPageLocators.SSO_ALL_OPTIONS);

            List<String> optionTexts = new ArrayList<>();
            Set<String> uniqueOptions = new HashSet<>();

            for (WebElement option : allOptions) {
                String text = option.getText().trim();
                if (!text.isEmpty()) {
                    optionTexts.add(text);
                    uniqueOptions.add(text);
                }
            }

            if (optionTexts.size() != uniqueOptions.size()) {
                System.err.println("✗ Duplicates found in SSO options!");
                System.err.println("Total options: " + optionTexts.size());
                System.err.println("Unique options: " + uniqueOptions.size());
                return false;
            }

            System.out.println("✓ No duplicates found. All " + optionTexts.size() + " options are unique");
            return true;
        } catch (Exception e) {
            System.err.println("Failed to check for duplicates: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close Add New form
     */
    public void closeAddNewForm() {
        System.out.println("Closing Add New form...");
        try {
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    RolesManagementPageLocators.FORM_CLOSE_BUTTON));
            closeButton.click();
            Thread.sleep(1000);
            System.out.println("✓ Add New form closed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Close form failed", e);
        } catch (Exception e) {
            System.err.println("Failed to close form: " + e.getMessage());
            // Try alternative - cancel button
            try {
                WebElement cancelButton = driver.findElement(
                        RolesManagementPageLocators.FORM_CANCEL_BUTTON);
                cancelButton.click();
                Thread.sleep(1000);
                System.out.println("✓ Add New form closed via Cancel");
            } catch (Exception ex) {
                System.err.println("Failed to close form via Cancel: " + ex.getMessage());
            }
        }
    }

    // ========== Utility Methods ==========

    /**
     * Check if element is present
     */
    private boolean isElementPresent(By locator) {
        try {
            return driver.findElements(locator).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

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
