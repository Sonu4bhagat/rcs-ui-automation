package pages;

import locators.TeamManagementPageLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for Team Management Page
 * Contains methods for Super Admin Team Management operations
 */
public class TeamManagementPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public TeamManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ========== Navigation Methods ==========

    /**
     * Navigate to Team Management page from dashboard
     */
    public void navigateToTeamManagement() {
        System.out.println("Navigating to Team Management page...");
        try {
            WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(
                    TeamManagementPageLocators.TEAM_MANAGEMENT_MENU));
            menu.click();

            // Wait for URL to change
            Thread.sleep(2000);

            // Validate navigation by URL
            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.contains("team-management")) {
                throw new RuntimeException("Failed to navigate to Team Management page. Current URL: " + currentUrl);
            }

            Thread.sleep(1000);
            System.out.println("✓ Navigated to Team Management page");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted: " + e.getMessage());
            throw new RuntimeException("Navigation to Team Management failed", e);
        } catch (Exception e) {
            System.err.println("Failed to navigate to Team Management: " + e.getMessage());
            throw new RuntimeException("Navigation to Team Management failed", e);
        }
    }

    /**
     * Verify the Team Management page is loaded
     */
    public boolean isPageLoaded() {
        try {
            Thread.sleep(1000);
            String currentUrl = driver.getCurrentUrl();
            boolean loaded = currentUrl.contains("team-management");

            if (loaded) {
                System.out.println("✓ Team Management page loaded. URL: " + currentUrl);
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
                    TeamManagementPageLocators.TABLE_HEADERS));

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
            if (!actualHeaders.contains(expected)) {
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
            List<WebElement> rows = driver.findElements(TeamManagementPageLocators.TABLE_ROWS);
            // Subtract 1 for header row
            int count = Math.max(0, rows.size() - 1);
            System.out.println("Table has " + count + " data row(s)");
            return count;
        } catch (Exception e) {
            System.err.println("Failed to get row count: " + e.getMessage());
            return 0;
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
                    TeamManagementPageLocators.PAGINATION_NEXT));
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
                    TeamManagementPageLocators.PAGINATION_PREVIOUS));
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
            WebElement nextButton = driver.findElement(TeamManagementPageLocators.PAGINATION_NEXT);
            boolean enabled = nextButton.isEnabled() && !nextButton.getAttribute("class").contains("disabled");
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
            WebElement prevButton = driver.findElement(TeamManagementPageLocators.PAGINATION_PREVIOUS);
            boolean enabled = prevButton.isEnabled() && !prevButton.getAttribute("class").contains("disabled");
            System.out.println("Previous button enabled: " + enabled);
            return enabled;
        } catch (Exception e) {
            System.err.println("Failed to check Previous button status: " + e.getMessage());
            return false;
        }
    }

    // ========== Action Buttons Methods ==========

    /**
     * Check if Edit button is clickable for a specific row
     */
    public boolean isEditButtonClickable(int rowIndex) {
        System.out.println("Checking Edit button for row " + rowIndex + "...");
        try {
            WebElement editButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    TeamManagementPageLocators.getEditButtonByRow(rowIndex)));
            boolean clickable = editButton.isEnabled() && editButton.isDisplayed();
            System.out.println("✓ Edit button clickable: " + clickable);
            return clickable;
        } catch (Exception e) {
            System.err.println("Failed to check Edit button: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if first Edit button is clickable
     */
    public boolean isFirstEditButtonClickable() {
        System.out.println("Checking first Edit button...");
        try {
            WebElement editButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    TeamManagementPageLocators.EDIT_BUTTON_FIRST));
            boolean clickable = editButton.isEnabled() && editButton.isDisplayed();
            System.out.println("✓ First Edit button clickable: " + clickable);
            return clickable;
        } catch (Exception e) {
            System.err.println("Failed to check first Edit button: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if View button is clickable for a specific row
     */
    public boolean isViewButtonClickable(int rowIndex) {
        System.out.println("Checking View button for row " + rowIndex + "...");
        try {
            WebElement viewButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    TeamManagementPageLocators.getViewButtonByRow(rowIndex)));
            boolean clickable = viewButton.isEnabled() && viewButton.isDisplayed();
            System.out.println("✓ View button clickable: " + clickable);
            return clickable;
        } catch (Exception e) {
            System.err.println("Failed to check View button: " + e.getMessage());
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
                    TeamManagementPageLocators.VIEW_BUTTON_FIRST));
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
                    TeamManagementPageLocators.SEARCH_FIELD));
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
     * Get current row count after search/filter
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

    // ========== Filter Methods ==========

    /**
     * Open filter dropdown
     */
    public void openFilters() {
        System.out.println("Opening filters...");
        try {
            WebElement filterButton = wait.until(ExpectedConditions.elementToBeClickable(
                    TeamManagementPageLocators.FILTERS_BUTTON));
            filterButton.click();
            Thread.sleep(1000);
            System.out.println("✓ Filters opened");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Open filters failed", e);
        } catch (Exception e) {
            System.err.println("Failed to open filters: " + e.getMessage());
            throw new RuntimeException("Open filters failed", e);
        }
    }

    /**
     * Select role in filter dropdown
     */
    public void selectRoleFilter(String roleName) {
        System.out.println("Selecting role filter: " + roleName);
        try {
            // Open filters if not already open
            openFilters();

            // Click on role dropdown
            WebElement roleDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    TeamManagementPageLocators.FILTER_ROLE_DROPDOWN));
            roleDropdown.click();
            Thread.sleep(500);

            // Select the role
            WebElement roleOption = wait.until(ExpectedConditions.elementToBeClickable(
                    TeamManagementPageLocators.getFilterRoleOption(roleName)));
            roleOption.click();
            Thread.sleep(500);

            System.out.println("✓ Selected role: " + roleName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Select role filter failed", e);
        } catch (Exception e) {
            System.err.println("Failed to select role filter: " + e.getMessage());
            throw new RuntimeException("Select role filter failed", e);
        }
    }

    /**
     * Apply filters
     */
    public void applyFilters() {
        System.out.println("Applying filters...");
        try {
            WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(
                    TeamManagementPageLocators.FILTER_APPLY_BUTTON));
            applyButton.click();
            Thread.sleep(2000); // Wait for filtered results
            System.out.println("✓ Filters applied");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Apply filters failed", e);
        } catch (Exception e) {
            System.err.println("Failed to apply filters: " + e.getMessage());
            throw new RuntimeException("Apply filters failed", e);
        }
    }

    /**
     * Filter by role name (all-in-one method)
     */
    public void filterByRole(String roleName) {
        System.out.println("Filtering by role: " + roleName);
        try {
            selectRoleFilter(roleName);
            applyFilters();
            System.out.println("✓ Filter by role completed");
        } catch (Exception e) {
            System.err.println("Failed to filter by role: " + e.getMessage());
            throw new RuntimeException("Filter by role failed", e);
        }
    }

    /**
     * Get all role names from the current table
     * 
     * @return List of role names displayed in the Roles Name column
     */
    public List<String> getAllRoleNamesFromTable() {
        System.out.println("Retrieving all role names from table...");
        try {
            Thread.sleep(1000); // Wait for table to stabilize
            List<WebElement> roleElements = driver.findElements(TeamManagementPageLocators.TABLE_ROLE_COLUMN_VALUES);

            List<String> roleNames = new ArrayList<>();
            for (WebElement element : roleElements) {
                String roleText = element.getText().trim();
                if (!roleText.isEmpty()) {
                    roleNames.add(roleText);
                }
            }

            System.out.println("✓ Found " + roleNames.size() + " role(s): " + roleNames);
            return roleNames;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Failed to get role names: " + e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Failed to get role names: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Validate that all displayed rows match the expected role filter
     * 
     * @param expectedRole The role that was used for filtering
     * @return true if all rows contain the expected role, false otherwise
     */
    public boolean validateFilteredResults(String expectedRole) {
        System.out.println("Validating filtered results for role: " + expectedRole);
        try {
            List<String> actualRoles = getAllRoleNamesFromTable();

            if (actualRoles.isEmpty()) {
                System.err.println("✗ No results found in table");
                return false;
            }

            boolean allMatch = true;
            for (int i = 0; i < actualRoles.size(); i++) {
                String actualRole = actualRoles.get(i);
                // Check if the actual role contains the expected role (case-insensitive)
                if (!actualRole.toLowerCase().contains(expectedRole.toLowerCase())) {
                    System.err.println("✗ Row " + (i + 1) + " has role '" + actualRole +
                            "' which does NOT match expected role '" + expectedRole + "'");
                    allMatch = false;
                } else {
                    System.out.println("✓ Row " + (i + 1) + " has matching role: '" + actualRole + "'");
                }
            }

            if (allMatch) {
                System.out.println("✓ All " + actualRoles.size() + " row(s) match the expected role: " + expectedRole);
            } else {
                System.err.println("✗ Filter validation FAILED: Not all rows match the expected role");
            }

            return allMatch;
        } catch (Exception e) {
            System.err.println("Failed to validate filtered results: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the role name for a specific row
     * 
     * @param rowIndex 1-based index of the row
     * @return Role name for that row
     */
    public String getRoleNameByRow(int rowIndex) {
        System.out.println("Getting role name for row " + rowIndex + "...");
        try {
            WebElement roleElement = driver.findElement(TeamManagementPageLocators.getRoleValueByRow(rowIndex));
            String roleName = roleElement.getText().trim();
            System.out.println("✓ Role for row " + rowIndex + ": " + roleName);
            return roleName;
        } catch (Exception e) {
            System.err.println("Failed to get role for row " + rowIndex + ": " + e.getMessage());
            return "";
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
                    TeamManagementPageLocators.ADD_NEW_BUTTON));
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
                    TeamManagementPageLocators.ADD_NEW_BUTTON));
            addNewButton.click();
            Thread.sleep(1000);
            System.out.println("✓ Clicked Add New button");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Click Add New failed", e);
        } catch (Exception e) {
            System.err.println("Failed to click Add New: " + e.getMessage());
            throw new RuntimeException("Click Add New failed", e);
        }
    }

    // ========== Utility Methods ==========

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
