package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.TeamManagementPage;

import java.util.Arrays;
import java.util.List;

/**
 * Test Suite for Super Admin Team Management Module
 * Tests Team Management functionality with table operations, pagination,
 * search, and filters
 */
public class SuperAdminTeamManagementTest extends BaseTest {

    private TeamManagementPage teamPage;

    @BeforeClass
    public void configureSession() {
        retainSession = true;
    }

    @Test(priority = 1, description = "Login as Super Admin and navigate to Team Management")
    public void testLoginAndNavigateToTeamManagement() throws InterruptedException {
        try {
            LoginPage loginPage = new LoginPage(driver);
            teamPage = new TeamManagementPage(driver);

            System.out.println("========================================");
            System.out.println("TEAM MANAGEMENT TEST SUITE");
            System.out.println("========================================\n");

            System.out.println("Step 1: Logging in as Super Admin...");
            loginPage.loginWithSuperAdminCredentials();
            Thread.sleep(2000);

            System.out.println("Step 2: Navigating to Team Management...");
            teamPage.navigateToTeamManagement();
            Thread.sleep(1500);

            Assert.assertTrue(teamPage.isPageLoaded(),
                    "❌ FAILURE: Team Management page failed to load - Check navigation or page locators");
            System.out.println("✓ Successfully navigated to Team Management page\n");
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: Login/Navigation - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: Login/Navigation - Unexpected error: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 2, description = "Validate table headers are displayed correctly", dependsOnMethods = "testLoginAndNavigateToTeamManagement", alwaysRun = true)
    public void testTableHeaders() throws InterruptedException {
        try {
            System.out.println("\n========================================");
            System.out.println("TEST: Table Headers Validation");
            System.out.println("========================================");

            List<String> expectedHeaders = Arrays.asList("Name", "Roles Name", "Created On", "Status");

            List<String> actualHeaders = teamPage.getTableHeaders();
            System.out.println("Expected headers: " + expectedHeaders);
            System.out.println("Actual headers: " + actualHeaders);

            // Validate that all expected headers are present
            boolean allHeadersPresent = true;
            StringBuilder missingHeaders = new StringBuilder();
            for (String expected : expectedHeaders) {
                if (!actualHeaders.contains(expected)) {
                    System.err.println("✗ Missing header: " + expected);
                    missingHeaders.append(expected).append(", ");
                    allHeadersPresent = false;
                }
            }

            Assert.assertTrue(allHeadersPresent,
                    "❌ FAILURE: Table headers missing - " + missingHeaders.toString() + "Check table structure");
            System.out.println("✓ All table headers validated successfully\n");
            Thread.sleep(1000);
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: Table Headers - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: Table Headers - Error retrieving headers: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 3, description = "Validate pagination Next button", dependsOnMethods = "testTableHeaders", alwaysRun = true)
    public void testPaginationNext() throws InterruptedException {
        try {
            System.out.println("\n========================================");
            System.out.println("TEST: Pagination - Next Button");
            System.out.println("========================================");

            // Check if Next button is enabled
            boolean nextEnabled = teamPage.isNextEnabled();
            System.out.println("Next button enabled: " + nextEnabled);

            if (nextEnabled) {
                int rowCountBefore = teamPage.getTableRowCount();
                System.out.println("Row count before clicking Next: " + rowCountBefore);

                teamPage.clickNext();
                Thread.sleep(1500);

                String currentUrl = teamPage.getCurrentURL();
                System.out.println("URL after clicking Next: " + currentUrl);

                int rowCountAfter = teamPage.getTableRowCount();
                System.out.println("Row count after clicking Next: " + rowCountAfter);

                System.out.println("✓ Next button clicked successfully");
            } else {
                System.out.println("⚠ Next button is disabled (already on last page or single page)");
            }

            System.out.println("✓ Pagination Next button test completed\n");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: Pagination Next - Error clicking Next button: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4, description = "Validate pagination Previous button", dependsOnMethods = "testPaginationNext", alwaysRun = true)
    public void testPaginationPrevious() throws InterruptedException {
        try {
            System.out.println("\n========================================");
            System.out.println("TEST: Pagination - Previous Button");
            System.out.println("========================================");

            // Check if Previous button is enabled
            boolean prevEnabled = teamPage.isPreviousEnabled();
            System.out.println("Previous button enabled: " + prevEnabled);

            if (prevEnabled) {
                int rowCountBefore = teamPage.getTableRowCount();
                System.out.println("Row count before clicking Previous: " + rowCountBefore);

                teamPage.clickPrevious();
                Thread.sleep(1500);

                String currentUrl = teamPage.getCurrentURL();
                System.out.println("URL after clicking Previous: " + currentUrl);

                int rowCountAfter = teamPage.getTableRowCount();
                System.out.println("Row count after clicking Previous: " + rowCountAfter);

                System.out.println("✓ Previous button clicked successfully");
            } else {
                System.out.println("⚠ Previous button is disabled (already on first page)");
            }

            System.out.println("✓ Pagination Previous button test completed\n");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.err.println(
                    "\n❌ TEST FAILED: Pagination Previous - Error clicking Previous button: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 5, description = "Validate Edit button is clickable", dependsOnMethods = "testPaginationPrevious", alwaysRun = true)
    public void testEditButtonClickable() throws InterruptedException {
        try {
            System.out.println("\n========================================");
            System.out.println("TEST: Edit Button Clickability");
            System.out.println("========================================");

            boolean isClickable = teamPage.isFirstEditButtonClickable();

            Assert.assertTrue(isClickable,
                    "❌ FAILURE: Edit button not clickable - Check button locator or permissions");
            System.out.println("✓ Edit button is clickable\n");
            Thread.sleep(1000);
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: Edit Button - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: Edit Button - Error finding Edit button: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 6, description = "Validate View button is clickable", dependsOnMethods = "testEditButtonClickable", alwaysRun = true)
    public void testViewButtonClickable() throws InterruptedException {
        try {
            System.out.println("\n========================================");
            System.out.println("TEST: View Button Clickability");
            System.out.println("========================================");

            boolean isClickable = teamPage.isFirstViewButtonClickable();

            Assert.assertTrue(isClickable,
                    "❌ FAILURE: View button not clickable - Check button locator or permissions");
            System.out.println("✓ View button is clickable\n");
            Thread.sleep(1000);
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: View Button - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: View Button - Error finding View button: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 7, description = "Validate Search functionality with 'Super Admin'", dependsOnMethods = "testViewButtonClickable", alwaysRun = true)
    public void testSearchFunctionality() throws InterruptedException {
        try {
            System.out.println("\n========================================");
            System.out.println("TEST: Search Functionality");
            System.out.println("========================================");

            String searchTerm = "Super Admin";
            System.out.println("Searching for: " + searchTerm);

            int rowCountBefore = teamPage.getTableRowCount();
            System.out.println("Row count before search: " + rowCountBefore);

            teamPage.search(searchTerm);
            Thread.sleep(2000);

            int rowCountAfter = teamPage.getSearchResultCount();
            System.out.println("Row count after search: " + rowCountAfter);

            // Verify that search returns results (should be > 0 since Super Admin exists)
            Assert.assertTrue(rowCountAfter > 0,
                    "❌ FAILURE: Search returned no results for '" + searchTerm
                            + "' - Check search functionality or data");

            System.out.println("✓ Search functionality validated - Found " + rowCountAfter + " result(s)");
            System.out.println("✓ Search returned expected results for: " + searchTerm + "\n");
            Thread.sleep(1000);
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: Search - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: Search - Error performing search: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 8, description = "Validate Filter by role 'Reporting Manager'", dependsOnMethods = "testSearchFunctionality", alwaysRun = true)
    public void testFilterByRole() throws InterruptedException {
        try {
            System.out.println("\n========================================");
            System.out.println("TEST: Filter by Role");
            System.out.println("========================================");

            // First, clear search if any
            teamPage.search("");
            Thread.sleep(1500);

            int rowCountBefore = teamPage.getTableRowCount();
            System.out.println("Row count before filter: " + rowCountBefore);

            String roleName = "Reporting Manager";
            System.out.println("Filtering by role: " + roleName);

            teamPage.filterByRole(roleName);
            Thread.sleep(2000);

            int rowCountAfter = teamPage.getTableRowCount();
            System.out.println("Row count after filter: " + rowCountAfter);

            // CRITICAL: Validate that all displayed rows actually match the filter
            System.out.println("\nValidating filtered results...");
            boolean filterValidation = teamPage.validateFilteredResults(roleName);

            // Assert that the filter validation passed
            Assert.assertTrue(filterValidation,
                    "❌ FAILURE: Filter not working - Displayed rows don't match role '" + roleName + "'");

            System.out.println("✓ Filter applied successfully");
            System.out.println("✓ All results validated - All rows match expected role: " + roleName + "\n");
            Thread.sleep(1000);
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: Filter by Role - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: Filter by Role - Error applying filter: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 9, description = "Validate Add New button is clickable", dependsOnMethods = "testFilterByRole", alwaysRun = true)
    public void testAddNewButtonClickable() throws InterruptedException {
        try {
            System.out.println("\n========================================");
            System.out.println("TEST: Add New Button Clickability");
            System.out.println("========================================");

            boolean isClickable = teamPage.isAddNewClickable();

            Assert.assertTrue(isClickable,
                    "❌ FAILURE: Add New button not clickable - Check button locator or permissions");
            System.out.println("✓ Add New button is clickable\n");
            Thread.sleep(1000);
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: Add New Button - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: Add New Button - Error finding Add New button: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 10, description = "Logout from Super Admin", dependsOnMethods = "testAddNewButtonClickable", alwaysRun = true)
    public void testLogout() throws InterruptedException {
        try {
            LoginPage loginPage = new LoginPage(driver);

            System.out.println("\n========================================");
            System.out.println("LOGGING OUT");
            System.out.println("========================================");

            loginPage.logout();
            System.out.println("✓ Logout successful\n");

            System.out.println("========================================");
            System.out.println("TEAM MANAGEMENT TEST SUITE - COMPLETED");
            System.out.println("========================================");
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: Logout - Error during logout: " + e.getMessage());
            throw e;
        }
    }
}
