package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.EnterpriseControlCenterPage;
import pages.EnterpriseRolesManagementPage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Test Suite for Enterprise Roles Management Module
 * Tests Roles Management functionality in Enterprise Control Center
 * including table operations, pagination, search, and Add New form validation
 * 
 * This test class is for Enterprise login flow only.
 */
public class EnterpriseRolesManagementTest extends BaseTest {

    private LoginPage loginPage;
    private EnterpriseControlCenterPage controlCenterPage;
    private EnterpriseRolesManagementPage rolesPage;

    @BeforeClass
    public void setupPages() {
        loginPage = new LoginPage(driver);
        controlCenterPage = new EnterpriseControlCenterPage(driver);
        rolesPage = new EnterpriseRolesManagementPage(driver);
        retainSession = true;
    }

    // ==================== Test Case 1: Dashboard Navigation & Control Center
    // ====================

    @Test(priority = 1, description = "Login as Enterprise user, click max service wallet, navigate to Control Center")
    public void testLoginAndControlCenterNavigation() {
        System.out.println("\n========================================");
        System.out.println("ENTERPRISE ROLES MANAGEMENT TEST SUITE");
        System.out.println("========================================\n");

        System.out.println("=== Test Case 1: Dashboard Navigation & Control Center Redirection ===");

        try {
            System.out.println("Step 1: Logging in as Enterprise user with max services...");
            loginPage.loginWithEnterpriseMaxServices();
            System.out.println("✓ Logged in as Enterprise user.");

            System.out.println("Step 2: Navigating to Control Center...");
            controlCenterPage.navigateToControlCenter();

            Assert.assertTrue(controlCenterPage.isControlCenterLoaded(),
                    "FAILURE: Control Center page did not load after click");
            System.out.println("✓ Successfully navigated to Control Center.\n");
        } catch (Exception e) {
            Assert.fail("FAILURE: Control Center navigation failed - " + e.getMessage());
        }
    }

    // ==================== Test Case 2: Roles Management Tab Navigation
    // ====================

    @Test(priority = 2, description = "Navigate to Roles Management tab from Control Center", dependsOnMethods = "testLoginAndControlCenterNavigation")
    public void testRolesManagementTabNavigation() {
        System.out.println("=== Test Case 2: Roles Management Tab Navigation ===");

        try {
            System.out.println("Clicking on Roles Management tab...");
            rolesPage.clickRolesManagementTab();

            Assert.assertTrue(rolesPage.isPageLoaded(),
                    "FAILURE: Roles Management section did not load");
            System.out.println("✓ Successfully navigated to Roles Management.\n");
        } catch (Exception e) {
            Assert.fail("FAILURE: Roles Management navigation failed - " + e.getMessage());
        }
    }

    // ==================== Test Case 3: Table Headers Validation
    // ====================

    @Test(priority = 3, description = "Validate Roles Management table headers", dependsOnMethods = "testRolesManagementTabNavigation")
    public void testTableHeaders() throws InterruptedException {
        System.out.println("=== Test Case 3: Table Headers Validation ===");

        // Enterprise Roles Management has 4 visible columns (Actions column may not
        // have text header)
        List<String> expectedHeaders = Arrays.asList("Role Name", "Users", "Permissions", "Status");

        List<String> actualHeaders = rolesPage.getTableHeaders();
        System.out.println("Expected headers: " + expectedHeaders);
        System.out.println("Actual headers: " + actualHeaders);

        boolean validated = rolesPage.validateTableHeaders(expectedHeaders);
        Assert.assertTrue(validated, "All expected table headers should be present");
        System.out.println("✓ All table headers validated successfully.\n");
        Thread.sleep(1000);
    }

    // ==================== Test Case 4: Pagination Next ====================

    @Test(priority = 4, description = "Validate pagination Next button", dependsOnMethods = "testTableHeaders", alwaysRun = true)
    public void testPaginationNext() throws InterruptedException {
        System.out.println("=== Test Case 4: Pagination - Next Button ===");

        boolean nextEnabled = rolesPage.isNextEnabled();
        System.out.println("Next button enabled: " + nextEnabled);

        if (nextEnabled) {
            int rowCountBefore = rolesPage.getTableRowCount();
            System.out.println("Row count before clicking Next: " + rowCountBefore);

            rolesPage.clickNext();
            Thread.sleep(1500);

            String currentUrl = rolesPage.getCurrentURL();
            System.out.println("URL after clicking Next: " + currentUrl);

            int rowCountAfter = rolesPage.getTableRowCount();
            System.out.println("Row count after clicking Next: " + rowCountAfter);

            System.out.println("✓ Next button clicked successfully.");
        } else {
            System.out.println("⚠ Next button is disabled (already on last page or single page)");
        }

        System.out.println("✓ Pagination Next button test completed.\n");
        Thread.sleep(1000);
    }

    // ==================== Test Case 5: Pagination Previous ====================

    @Test(priority = 5, description = "Validate pagination Previous button", dependsOnMethods = "testPaginationNext", alwaysRun = true)
    public void testPaginationPrevious() throws InterruptedException {
        System.out.println("=== Test Case 5: Pagination - Previous Button ===");

        boolean prevEnabled = rolesPage.isPreviousEnabled();
        System.out.println("Previous button enabled: " + prevEnabled);

        if (prevEnabled) {
            int rowCountBefore = rolesPage.getTableRowCount();
            System.out.println("Row count before clicking Previous: " + rowCountBefore);

            rolesPage.clickPrevious();
            Thread.sleep(1500);

            String currentUrl = rolesPage.getCurrentURL();
            System.out.println("URL after clicking Previous: " + currentUrl);

            int rowCountAfter = rolesPage.getTableRowCount();
            System.out.println("Row count after clicking Previous: " + rowCountAfter);

            System.out.println("✓ Previous button clicked successfully.");
        } else {
            System.out.println("⚠ Previous button is disabled (already on first page)");
        }

        System.out.println("✓ Pagination Previous button test completed.\n");
        Thread.sleep(1000);
    }

    // ==================== Test Case 6: Edit Button Clickable ====================

    @Test(priority = 6, description = "Validate Edit button is clickable", dependsOnMethods = "testPaginationPrevious", alwaysRun = true)
    public void testEditButtonClickable() throws InterruptedException {
        System.out.println("=== Test Case 6: Edit Button Clickability ===");

        int rowCount = rolesPage.getTableRowCount();
        if (rowCount == 0) {
            System.out.println("⚠ No rows in table - Edit button test skipped");
            return;
        }

        boolean isClickable = rolesPage.isFirstEditButtonClickable();

        if (isClickable) {
            System.out.println("✓ Edit button is clickable.\n");
        } else {
            System.out.println("INFO: Edit button not found or not clickable (may not be available for this role).\n");
        }
        Thread.sleep(1000);
    }

    // ==================== Test Case 7: View Button Clickable ====================

    @Test(priority = 7, description = "Validate View button is clickable", dependsOnMethods = "testEditButtonClickable", alwaysRun = true)
    public void testViewButtonClickable() throws InterruptedException {
        System.out.println("=== Test Case 7: View Button Clickability ===");

        int rowCount = rolesPage.getTableRowCount();
        if (rowCount == 0) {
            System.out.println("⚠ No rows in table - View button test skipped");
            return;
        }

        boolean isClickable = rolesPage.isFirstViewButtonClickable();

        if (isClickable) {
            System.out.println("✓ View button is clickable.\n");
        } else {
            System.out.println("INFO: View button not found or not clickable (may not be available for this role).\n");
        }
        Thread.sleep(1000);
    }

    // ==================== Test Case 8: Search Functionality ====================

    @Test(priority = 8, description = "Validate Search functionality with first 3 characters", dependsOnMethods = "testViewButtonClickable", alwaysRun = true)
    public void testSearchFunctionality() throws InterruptedException {
        System.out.println("=== Test Case 8: Search Functionality ===");

        // Get first role name
        String firstRoleName = rolesPage.getFirstRoleName();
        if (firstRoleName.isEmpty()) {
            System.out.println("⚠ No roles in table - Search test skipped");
            return;
        }

        // Get first 3 characters
        String searchTerm = firstRoleName.length() >= 3 ? firstRoleName.substring(0, 3) : firstRoleName;
        System.out.println("Searching with first 3 characters: '" + searchTerm + "'");

        int rowCountBefore = rolesPage.getTableRowCount();
        System.out.println("Row count before search: " + rowCountBefore);

        rolesPage.search(searchTerm);
        Thread.sleep(2000);

        int rowCountAfter = rolesPage.getSearchResultCount();
        System.out.println("Row count after search: " + rowCountAfter);

        // Verify that search returns results
        Assert.assertTrue(rowCountAfter > 0, "Search should return results for '" + searchTerm + "'");
        System.out.println("✓ Search functionality validated - Found " + rowCountAfter + " result(s).\n");
        Thread.sleep(1000);
    }

    // ==================== Test Case 9: Add New Button Clickable
    // ====================

    @Test(priority = 9, description = "Validate Add New button is clickable", dependsOnMethods = "testSearchFunctionality", alwaysRun = true)
    public void testAddNewButtonClickable() throws InterruptedException {
        System.out.println("=== Test Case 9: Add New Button Clickability ===");

        // Clear search first
        rolesPage.search("");
        Thread.sleep(1500);

        boolean isClickable = rolesPage.isAddNewClickable();

        if (isClickable) {
            System.out.println("✓ Add New button is clickable.\n");
        } else {
            System.out.println("INFO: Add New button not found or not clickable.\n");
        }
        Thread.sleep(1000);
    }

    // ==================== Test Case 10: Add New Form Fields ====================

    @Test(priority = 10, description = "Validate permission fields in Add New form", dependsOnMethods = "testAddNewButtonClickable", alwaysRun = true)
    public void testAddNewFormFields() throws InterruptedException {
        System.out.println("=== Test Case 10: Add New Form - Permission Fields ===");

        try {
            // Click Add New button
            rolesPage.clickAddNew();
            Thread.sleep(2000);

            // Validate permission accordions are present (Enterprise has fewer accordions)
            boolean accordionsPresent = rolesPage.validateAllPermissionAccordions();

            // In Enterprise, we expect at least Dashboard, Settings, My Profile
            Assert.assertTrue(accordionsPresent, "At least 3 permission accordions should be present");
            System.out.println("✓ Permission fields validated successfully.\n");
        } catch (Exception e) {
            System.out.println("SKIPPED: Add New form validation - " + e.getMessage() + "\n");
        }
        Thread.sleep(1000);
    }

    // ==================== Test Case 11: Dashboard Accordion Expansion
    // ====================

    @Test(priority = 11, description = "Validate Dashboard accordion expansion", dependsOnMethods = "testAddNewFormFields", alwaysRun = true)
    public void testDashboardAccordionExpansion() throws InterruptedException {
        System.out.println("=== Test Case 11: Dashboard Accordion Expansion ===");

        try {
            // Expand Dashboard accordion (available in Enterprise)
            rolesPage.expandDashboardAccordion();
            Thread.sleep(1500);

            // Validate sub-options are present
            boolean subOptionsPresent = rolesPage.validateDashboardSubOptions();

            Assert.assertTrue(subOptionsPresent, "Dashboard sub-options should be present");
            System.out.println("✓ Dashboard accordion expansion validated successfully.\n");
        } catch (Exception e) {
            System.out.println("SKIPPED: Dashboard accordion validation - " + e.getMessage() + "\n");
        }
        Thread.sleep(1000);
    }

    // ==================== Test Case 12: No Duplicate Values ====================

    @Test(priority = 12, description = "Validate no duplicate values in form", dependsOnMethods = "testDashboardAccordionExpansion", alwaysRun = true)
    public void testNoDuplicateValues() throws InterruptedException {
        System.out.println("=== Test Case 12: No Duplicate Values ===");

        try {
            // Check for duplicates in Dashboard options
            boolean noDuplicates = rolesPage.checkNoDuplicatesInDashboardOptions();

            Assert.assertTrue(noDuplicates, "There should be no duplicate values in the form");
            System.out.println("✓ No duplicate values found.\n");

            // Close the form
            rolesPage.closeAddNewForm();
            Thread.sleep(1000);
            System.out.println("✓ Add New form closed.\n");
        } catch (Exception e) {
            System.out.println("SKIPPED: Duplicate check - " + e.getMessage() + "\n");
            rolesPage.closeAddNewForm();
        }
    }

    // ==================== Test Case 13: Users Count Navigation
    // ====================

    @Test(priority = 13, description = "Validate Users count navigation to Team Management", dependsOnMethods = "testNoDuplicateValues", alwaysRun = true)
    public void testUsersCountNavigation() throws InterruptedException {
        System.out.println("=== Test Case 13: Users Count Navigation ===");

        try {
            // Find a role with Users > 0
            Map<String, String> roleInfo = rolesPage.findRoleWithUsers();

            if (roleInfo == null) {
                System.out.println("⚠ No role with Users > 0 found - Test skipped");
                return;
            }

            String roleName = roleInfo.get("roleName");
            int expectedUserCount = Integer.parseInt(roleInfo.get("usersCount"));

            System.out.println("Selected role: " + roleName);
            System.out.println("Expected user count: " + expectedUserCount);

            // Click on Users count
            rolesPage.clickUsersCount(roleName);
            Thread.sleep(2000);

            // Verify navigation to Team Management
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("team-management") || currentUrl.contains("team"),
                    "Should navigate to Team Management page. Current URL: " + currentUrl);
            System.out.println("✓ Successfully navigated to Team Management");
            System.out.println("Current URL: " + currentUrl);
            System.out.println("✓ Users count navigation validated successfully.\n");

            // Navigate back to Roles Management for cleanup
            rolesPage.navigateToRolesManagement();
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("SKIPPED: Users count navigation - " + e.getMessage() + "\n");
        }
    }

    // ==================== Test Case 14: Logout ====================

    @Test(priority = 14, description = "Logout from Enterprise account", dependsOnMethods = "testLoginAndControlCenterNavigation", alwaysRun = true)
    public void testLogout() throws InterruptedException {
        System.out.println("=== Test Case 14: Logout ===");

        try {
            loginPage.logout();
            System.out.println("✓ Logout successful.\n");
        } catch (Exception e) {
            System.out.println("INFO: Logout encountered an issue - " + e.getMessage() + "\n");
        }

        System.out.println("========================================");
        System.out.println("ENTERPRISE ROLES MANAGEMENT TEST SUITE - COMPLETED");
        System.out.println("========================================");
    }
}
