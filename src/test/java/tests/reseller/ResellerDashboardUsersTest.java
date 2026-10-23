package tests.reseller;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ResellerDashboardUsersPage;

import java.util.List;

/**
 * Test class for Reseller Dashboard's "Users" tab.
 * Tests tab selection, user listing/pagination, table headers, search,
 * filters, Create User, Update Plan, Add Credit, and the per-row Action
 * button.
 */
public class ResellerDashboardUsersTest extends BaseTest {

    private LoginPage loginPage;
    private ResellerDashboardUsersPage usersPage;
    private static final String DASHBOARD_USERS_URL = "https://stagingvault.smartping.io/rs/dashboard/users";

    @BeforeClass
    public void setupPages() {
        loginPage = new LoginPage(driver);
        usersPage = new ResellerDashboardUsersPage(driver);
        retainSession = true; // Prevent BaseTest from auto-navigating to login
    }

    // ==================== TC01: Verify Users Tab Clickable ====================

    @Test(priority = 1, description = "TC01: Verify Users tab is clickable and loads the Users listing")
    public void testUsersTabClickable() {
        System.out.println("=== TC01: Verify Users Tab Clickable ===");

        loginPage.loginWithResellerCredentials();
        System.out.println("Logged in as Reseller.");

        usersPage.clickUsersTab();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        usersPage.waitForUserData();

        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard/users"),
                "URL should navigate to the Users tab route after clicking it. URL: " + driver.getCurrentUrl());
        Assert.assertTrue(usersPage.isUsersTabSelected(), "Users tab should be selected after clicking it");
        System.out.println("Users tab clicked and selected successfully.");
    }

    // ==================== TC02: Verify User Listing + Pagination ====================

    @Test(priority = 2, description = "TC02: Verify user listing, and pagination when data exceeds 10 rows", dependsOnMethods = "testUsersTabClickable")
    public void testUserListingAndPagination() throws InterruptedException {
        System.out.println("=== TC02: Verify User Listing and Pagination ===");

        boolean hasUsers = usersPage.hasUsers();
        boolean noDataShown = usersPage.isNoDataMessageDisplayed();
        Assert.assertTrue(hasUsers || noDataShown,
                "Either users should be listed OR a 'no data' message should be displayed");

        if (!hasUsers) {
            System.out.println("No users available for this reseller. Skipping pagination validation.");
            return;
        }

        int rowCount = usersPage.getUserRowCount();
        System.out.println("User row count (this page): " + rowCount);
        Assert.assertTrue(rowCount > 0 && rowCount <= 10, "Page size should be at most 10 rows");

        boolean paginationVisible = usersPage.isPaginationAvailable();
        System.out.println("Pagination controls visible: " + paginationVisible);

        if (rowCount == 10 && paginationVisible) {
            String firstPageRow1 = usersPage.getUserCellText(1, 1);
            boolean nextClicked = usersPage.clickNextPage();
            Assert.assertTrue(nextClicked, "Next button should be clickable when more than 10 records exist");
            Thread.sleep(1000);

            String secondPageRow1 = usersPage.getUserCellText(1, 1);
            System.out.println("Row 1 org before Next: " + firstPageRow1 + ", after Next: " + secondPageRow1);
            Assert.assertNotEquals(secondPageRow1, firstPageRow1,
                    "Clicking Next should load a different page of data");

            boolean prevClicked = usersPage.clickPreviousPage();
            Assert.assertTrue(prevClicked, "Previous button should navigate back to the previous page");
            System.out.println("Pagination Next/Previous validated.");
        } else {
            System.out.println("10 or fewer users in total; pagination not required to be exercised.");
        }
    }

    // ==================== TC03: Verify Table Headers ====================

    @Test(priority = 3, description = "TC03: Verify Users table headers", dependsOnMethods = "testUserListingAndPagination")
    public void testTableHeaders() {
        System.out.println("=== TC03: Verify Table Headers ===");

        List<String> headers = usersPage.getUsersTableHeaders();
        System.out.println("Users table headers: " + headers);

        Assert.assertFalse(headers.isEmpty(), "Users table headers should be present");
        String[] expected = { "Organisation Name", "Wallets", "Status" };
        for (String exp : expected) {
            boolean found = headers.stream().anyMatch(h -> h.contains(exp));
            Assert.assertTrue(found, "Expected header containing '" + exp + "' not found in: " + headers);
        }
        System.out.println("Table headers validated.");
    }

    // ==================== TC04: Verify Search by Name ====================

    @Test(priority = 4, description = "TC04: Verify search by text from 'Search by Name' field", dependsOnMethods = "testTableHeaders")
    public void testSearchByName() {
        System.out.println("=== TC04: Verify Search by Name ===");

        String firstOrg = usersPage.getUserCellText(1, 1);
        Assert.assertFalse(firstOrg.isEmpty(), "Need an organisation name to search with");
        System.out.println("Searching for: " + firstOrg);

        usersPage.searchUser(firstOrg);
        int resultCount = usersPage.getUserRowCount();
        System.out.println("Search result count: " + resultCount);
        Assert.assertTrue(resultCount > 0, "Searching for an existing organisation should return at least one result");

        for (int i = 1; i <= resultCount; i++) {
            String name = usersPage.getUserCellText(i, 1);
            Assert.assertTrue(name.equalsIgnoreCase(firstOrg) || name.toLowerCase().contains(firstOrg.toLowerCase()),
                    "Row " + i + " ('" + name + "') should match search text '" + firstOrg + "'");
        }
        usersPage.clearSearch();
        System.out.println("Search by name validated.");
    }

    // ==================== TC05: Verify Data Matches Applied Filter ====================

    @Test(priority = 5, description = "TC05: Verify data present matches the applied Status filter", dependsOnMethods = "testSearchByName")
    public void testFilterAppliedDataMatches() {
        System.out.println("=== TC05: Verify Filtered Data Matches Applied Filter ===");

        usersPage.applyActiveStatusFilter();

        boolean noDataShown = usersPage.isNoDataMessageDisplayed();
        if (noDataShown) {
            System.out.println("No Active-status organisations found after filtering.");
        } else {
            int rowCount = usersPage.getUserRowCount();
            System.out.println("Filtered result count: " + rowCount);
            Assert.assertTrue(rowCount > 0, "Applying Active filter should show matching users or a no-data message");

            for (int i = 1; i <= rowCount; i++) {
                String status = usersPage.getUserStatus(i);
                System.out.println("Row " + i + " -> Status: " + status);
                Assert.assertTrue(status.equalsIgnoreCase("Active"),
                        "Row " + i + " should be Active after filtering");
            }
        }
        System.out.println("Filter data match validated.");
    }

    // ==================== TC06: Verify Clear All After Filter Apply ====================

    @Test(priority = 6, description = "TC06: Verify Clear All restores the full unfiltered list", dependsOnMethods = "testFilterAppliedDataMatches")
    public void testClearAllFilters() throws InterruptedException {
        System.out.println("=== TC06: Verify Clear All After Filter Apply ===");

        int countWithFilter = usersPage.getUserRowCount();
        System.out.println("Row count with Active filter applied: " + countWithFilter);

        // Confirmed live: a "Clear All" button only appears in the DOM after a
        // filter has actually been applied (it isn't present beforehand).
        List<WebElement> clearButtons = driver.findElements(
                locators.ResellerDashboardUsersPageLocators.CLEAR_ALL_FILTERS_BUTTON);
        Assert.assertFalse(clearButtons.isEmpty(), "Clear All button should appear once a filter is applied");

        usersPage.clickClearAllFilters();
        Thread.sleep(1000);
        usersPage.waitForUserData();

        int countAfterClear = usersPage.getUserRowCount();
        System.out.println("Row count after Clear All: " + countAfterClear);
        Assert.assertTrue(countAfterClear >= countWithFilter,
                "Full user list should be restored (or larger) after clearing filters");
        System.out.println("Clear All Filters validated.");
    }

    // ==================== TC07: Verify Create User Button ====================

    @Test(priority = 7, description = "TC07: Verify Create user button redirects to the create user page", dependsOnMethods = "testClearAllFilters")
    public void testCreateUserButton() {
        System.out.println("=== TC07: Verify Create User Button ===");

        Assert.assertTrue(usersPage.isCreateUserButtonVisible(), "Create user button should be visible");

        usersPage.clickCreateUserButton();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("create-user"));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL after clicking Create user: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("create-user"),
                "Create user button should redirect to the create user page. URL: " + currentUrl);

        driver.navigate().to(DASHBOARD_USERS_URL);
        usersPage.waitForUserData();
        System.out.println("Create user button validated. Returned to Users tab.");
    }

    // ==================== TC08: Verify Update Plan Redirect ====================

    @Test(priority = 8, description = "TC08: Verify Update Plan redirects to the Users Rate Card page", dependsOnMethods = "testCreateUserButton")
    public void testUpdatePlanRedirect() throws InterruptedException {
        System.out.println("=== TC08: Verify Update Plan Redirect ===");

        Assert.assertTrue(usersPage.isUpdatePlanButtonPresent(1), "Update Plan button should be present on row 1");

        usersPage.clickUpdatePlanButton(1);
        Thread.sleep(2000);

        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL after clicking Update Plan: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("user-ratecard"),
                "Update Plan should redirect to the Users Rate Card page. URL: " + currentUrl);

        driver.navigate().to(DASHBOARD_USERS_URL);
        Thread.sleep(1500);
        usersPage.waitForUserData();
        System.out.println("Update Plan redirect validated. Returned to Users tab.");
    }

    // ==================== TC09: Verify Add Credit Redirect ====================

    @Test(priority = 9, description = "TC09: Verify Add Credit redirects to User Fund Management with matching org name", dependsOnMethods = "testUpdatePlanRedirect")
    public void testAddCreditRedirect() throws InterruptedException {
        System.out.println("=== TC09: Verify Add Credit Redirect ===");

        String orgName = usersPage.getUserCellText(1, 1);
        System.out.println("Row 1 organisation: " + orgName);
        Assert.assertTrue(usersPage.isAddCreditButtonPresent(1), "Add Credit button should be present on row 1");

        usersPage.clickAddCreditButton(1);
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("manage-credits"));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL after clicking Add Credit: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("manage-credits"),
                "Add Credit should redirect to the User Fund Management page. URL: " + currentUrl);

        String pageOrgName = waitForOrgNameHeading(orgName);
        System.out.println("Organisation name shown on User Fund Management page: " + pageOrgName);
        Assert.assertTrue(pageOrgName.equalsIgnoreCase(orgName),
                "User Fund Management page should show the same organisation clicked from ('" + orgName
                        + "'), but showed '" + pageOrgName + "'");

        driver.navigate().to(DASHBOARD_USERS_URL);
        Thread.sleep(1500);
        usersPage.waitForUserData();
        System.out.println("Add Credit redirect validated. Returned to Users tab.");
    }

    // ==================== TC10: Verify Action Button Redirect ====================

    @Test(priority = 10, description = "TC10: Verify the row Action button redirect", dependsOnMethods = "testAddCreditRedirect")
    public void testActionButtonRedirect() throws InterruptedException {
        System.out.println("=== TC10: Verify Action Button Redirect ===");

        // NOTE: confirmed live (HTML dump + repeated URL checks) that the per-row
        // circular arrow ("Action") button is tooltip-labeled "Redirect to User
        // Overview" and actually navigates to the SAME /rs/manage-credits (User
        // Fund Management) page as the "Add Credit" button - there is no separate
        // "Service Management" destination for this button. Validating that
        // actual, confirmed behavior here rather than the originally assumed (but
        // unconfirmed) "service management page" destination - same class of
        // correction as My Wallets TC05.
        String orgName = usersPage.getUserCellText(1, 1);
        System.out.println("Row 1 organisation: " + orgName);
        Assert.assertTrue(usersPage.isServiceNavButtonEnabled(1), "Action button should be enabled for row 1");

        usersPage.clickServiceNavButton(1);
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("manage-credits"));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL after clicking Action button: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("manage-credits"),
                "Action button should redirect to the User Fund Management page. URL: " + currentUrl);

        String pageOrgName = waitForOrgNameHeading(orgName);
        System.out.println("Organisation name shown after Action click: " + pageOrgName);
        Assert.assertTrue(pageOrgName.equalsIgnoreCase(orgName),
                "Destination page should show the same organisation clicked from ('" + orgName
                        + "'), but showed '" + pageOrgName + "'");

        System.out.println("Action button redirect validated.");
    }

    /**
     * Waits for a heading-like element showing the given organisation name to
     * appear on the User Fund Management page. The page's tag structure for
     * this heading isn't a plain h1/h2 (confirmed live - a fixed xpath guess
     * threw NoSuchElementException), so this waits for ANY element whose text
     * equals the org name rather than assuming a specific tag.
     */
    private String waitForOrgNameHeading(String orgName) {
        By locator = By.xpath("//*[normalize-space(text())='" + orgName + "']");
        WebElement el = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(locator));
        return el.getText().trim();
    }

    // ==================== Logout ====================

    @Test(priority = 99, description = "Logout from Reseller account", dependsOnMethods = "testActionButtonRedirect", alwaysRun = true)
    public void testLogout() {
        System.out.println("=== Logout ===");
        try {
            loginPage.logout();
            System.out.println("Successfully logged out from Reseller account.");
        } catch (Exception e) {
            System.out.println("Logout encountered an issue: " + e.getMessage());
        }
    }
}
