package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.CustomerOrgPage;
import pages.LoginPage;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SuperAdminCustomerOrgTest extends BaseTest {

    @BeforeClass
    public void configureSession() {
        // Retain session for multi-priority regression steps
        retainSession = true;
    }

    @Test(priority = 1, description = "Login as SuperAdmin and Navigate to Customer Org")
    public void testLoginAndNavigation() throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("Step 1: Logging in as SuperAdmin...");
        loginPage.loginWithSuperAdminCredentials();
        Thread.sleep(1500); // Wait for dashboard to fully load

        System.out.println("Step 2: Navigating to Customer Org...");
        customerOrgPage.navigateToCustomerOrg();
        Thread.sleep(1000); // Wait for page transition

        Assert.assertTrue(customerOrgPage.isPageLoaded(), "Customer Org page failed to load");
        Thread.sleep(500); // Stabilize before next test
    }

    @Test(priority = 2, description = "Comprehensive Table Header Validation")
    public void testTableHeaders() throws InterruptedException {
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("Validating Table Header regression cases...");

        // Wait for table to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table")));
        Thread.sleep(500);

        java.util.List<String> detectedHeaders = customerOrgPage.getTableHeaderTexts();
        System.out.println("Detected Headers: " + detectedHeaders);

        java.util.List<String> expectedHeaders = java.util.Arrays.asList(
                "Organisation Name", "Type", "Status", "Wallets");

        Assert.assertFalse(detectedHeaders.isEmpty(), "Table headers should not be empty");

        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();
        for (String expected : expectedHeaders) {
            boolean found = detectedHeaders.stream().anyMatch(h -> h.toLowerCase().contains(expected.toLowerCase()));
            softAssert.assertTrue(found, "Header '" + expected + "' not found");
        }
        softAssert.assertAll();
        System.out.println("✓ All table headers validated");
    }

    @Test(priority = 3, description = "Filter Enterprise and Redirect to Overview with Wallet Check", dependsOnMethods = "testTableHeaders")
    public void testEnterpriseDrillDown() throws InterruptedException {
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("Step 3: Filtering Enterprise and drilling down...");

        Thread.sleep(500); // Wait before applying filter
        customerOrgPage.filterByEnterprise();
        Thread.sleep(1000); // Wait for filter to apply

        int walletCount = customerOrgPage.redirectToFirstEnterpriseWithWallets();
        System.out.println("Detected " + walletCount + " wallet(s) for the selected Enterprise.");
        Thread.sleep(1500); // Wait for page to fully load after drill-down

        // Verify we actually drilled down (URL should contain customer ID)
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/customer-org/") && !currentUrl.endsWith("/customer-org"),
                "Should have drilled down into a customer. Current URL: " + currentUrl);
        System.out.println("✓ Successfully drilled down. URL: " + currentUrl);

        String balance = customerOrgPage.getWalletValue();
        Assert.assertNotNull(balance, "Wallet balance should be available");
        System.out.println("✓ Wallet balance: " + balance);
        Thread.sleep(500); // Stabilize before next test
    }

    @Test(priority = 4, description = "Verify Wallet Table and Search", dependsOnMethods = "testEnterpriseDrillDown")
    public void testWalletTableAndSearch() throws InterruptedException {
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("Step 4: Verifying Wallet Table and search...");

        WebElement walletSearchField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[contains(@placeholder, 'Search') or @type='search']")));
        Thread.sleep(500);
        Assert.assertTrue(walletSearchField.isDisplayed(), "Search field should be visible");
        customerOrgPage.searchWallet("Test Search");
        Thread.sleep(1000); // Wait for search results
        System.out.println("✓ Wallet search executed");
    }

    @Test(priority = 6, description = "Verify Organization Details Tab - Customer ID", dependsOnMethods = "testWalletTableAndSearch")
    public void testOrganizationDetailsTab() throws InterruptedException {
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);

        System.out.println("Step 5: Navigating to Organization Details tab...");
        Thread.sleep(500); // Wait before navigation
        customerOrgPage.navigateToOrgDetailsTab();
        Thread.sleep(1500); // Wait for tab content to load

        String customerId = customerOrgPage.getCustomerId();
        System.out.println("Customer ID: " + customerId);

        Assert.assertNotNull(customerId, "Customer ID should not be null");
        Assert.assertFalse(customerId.equals("N/A"), "Customer ID should be found on the page");
        System.out.println("✓ Organization Details validation: PASSED. Customer ID = " + customerId);
        Thread.sleep(500); // Stabilize before next test
    }

    @Test(priority = 7, description = "Navigate to Profile Tab", dependsOnMethods = "testOrganizationDetailsTab", alwaysRun = true)
    public void testNavigateToProfileTab() throws InterruptedException {
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("Navigating to Profile tab...");

        // Clear any previous search or filters from Organization Details tab
        try {
            WebElement searchBox = driver.findElement(By.xpath("//input[contains(@placeholder, 'Search')]"));
            if (searchBox.isDisplayed()) {
                searchBox.clear();
                Thread.sleep(500);
            }
        } catch (Exception e) {
            // No search box, continue
        }

        Thread.sleep(500); // Wait before navigation
        customerOrgPage.navigateToProfileTab();
        Thread.sleep(1500); // Wait for Profile tab to fully load

        Assert.assertTrue(customerOrgPage.isProfileTabLoaded(), "Profile tab failed to load");
        System.out.println("✓ Profile tab loaded successfully");
        Thread.sleep(500); // Stabilize before next test
    }

    @Test(priority = 8, description = "Verify Profile Table Headers", dependsOnMethods = "testNavigateToProfileTab", alwaysRun = true)
    public void testProfileTableHeaders() throws InterruptedException {
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("Verifying Profile table headers...");
        Thread.sleep(500); // Wait for table to be ready
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table")));

        List<String> headers = customerOrgPage.getProfileTableHeaders();
        System.out.println("Detected Headers: " + headers);

        Assert.assertFalse(headers.isEmpty(), "Profile table should have headers");

        List<String> expectedHeaders = Arrays.asList("Role Name", "Created On", "Assigned Services", "Status");

        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();
        for (String expected : expectedHeaders) {
            boolean found = headers.stream().anyMatch(h -> h.toLowerCase().contains(expected.toLowerCase()));
            softAssert.assertTrue(found, "Header missing: " + expected + ". Found headers: " + headers);
        }
        softAssert.assertAll();
        System.out.println("✓ All profile table headers verified");
    }

    @Test(priority = 9, description = "Verify Edit Button Clickable", dependsOnMethods = "testNavigateToProfileTab", alwaysRun = true)
    public void testEditButtonClickable() {
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);

        System.out.println("Verifying Edit button is clickable...");

        // Check if table has rows first
        try {
            List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
            if (rows.isEmpty()) {
                System.out.println("⚠ No rows in Profile table, skipping Edit button test");
                return;
            }
        } catch (Exception e) {
            System.err.println("Could not check table rows: " + e.getMessage());
        }

        Assert.assertTrue(customerOrgPage.isEditButtonClickable(), "Edit button should be clickable");
        System.out.println("✓ Edit button is clickable");
    }

    @Test(priority = 10, description = "Verify View Icon and Content", dependsOnMethods = "testNavigateToProfileTab", alwaysRun = true)
    public void testViewIconAndContent() {
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);

        System.out.println("Clicking View icon and verifying content...");

        // Check if table has rows first
        try {
            List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
            if (rows.isEmpty()) {
                System.out.println("⚠ No rows in Profile table, skipping View icon test");
                return;
            }
        } catch (Exception e) {
            System.err.println("Could not check table rows: " + e.getMessage());
        }

        boolean contentVisible = customerOrgPage.clickViewIconAndVerifyContent();

        if (!contentVisible) {
            System.err.println("⚠ View icon did not show content or element not found");
            // Take screenshot for debugging
            try {
                System.err.println("Current URL: " + driver.getCurrentUrl());
            } catch (Exception e) {
            }
        }

        Assert.assertTrue(contentVisible, "At least one text element should be visible after clicking view icon");
        System.out.println("✓ View icon functional and content verified");

        // Close modal if needed (press ESC or click close button)
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            Thread.sleep(500);
        } catch (Exception e) {
            // Modal might close automatically or no modal present
        }
    }

    @Test(priority = 11, description = "Verify Search Field", dependsOnMethods = "testNavigateToProfileTab", alwaysRun = true)
    public void testSearchField() {
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);

        System.out.println("Verifying Search field functionality...");
        Assert.assertTrue(customerOrgPage.isSearchFieldFunctional(), "Search field should be functional");
        System.out.println("✓ Search field is functional");
    }

    @Test(priority = 12, description = "Verify Filter Selection and Apply", dependsOnMethods = "testNavigateToProfileTab", alwaysRun = true)
    public void testFilterSelectionAndApply() {
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);

        System.out.println("Verifying Filter selection and apply...");
        customerOrgPage.applyProfileFilters();
        System.out.println("✓ Filters applied successfully");
    }

    @Test(priority = 13, description = "Verify Add New Button Clickable", dependsOnMethods = "testNavigateToProfileTab", alwaysRun = true)
    public void testAddNewButtonClickable() {
        CustomerOrgPage customerOrgPage = new CustomerOrgPage(driver);

        System.out.println("Verifying Add New button is clickable...");
        Assert.assertTrue(customerOrgPage.isAddNewButtonClickable(), "Add New button should be clickable");
        System.out.println("✓ Add New button is clickable");
    }

    // ========== ROLES TAB TESTS (Priority 15-23) ==========

    @Test(priority = 14, description = "Verify Roles tab is visible and accessible", dependsOnMethods = "testNavigateToProfileTab", alwaysRun = true)
    public void testRolesTabVisibility() throws InterruptedException {
        CustomerOrgPage page = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        Thread.sleep(1000); // Wait before starting roles tab tests

        // Debug: Print current state
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL before navigation: " + currentUrl);

        // Verify we're inside a customer details page (not on the main list)
        Assert.assertTrue(currentUrl.contains("/customer-org/") && !currentUrl.endsWith("/customer-org"),
                "Should be inside a customer's details page. Current URL: " + currentUrl);

        System.out.println("Navigating to Roles tab...");
        page.navigateToRolesTab();
        Thread.sleep(1500); // Wait for Roles tab to fully load

        // Verify navigation
        String newUrl = driver.getCurrentUrl();
        System.out.println("Current URL after navigation: " + newUrl);

        Assert.assertTrue(newUrl.contains("roles"), "URL should contain 'roles'. Current URL: " + newUrl);
        Assert.assertTrue(page.isRolesTabLoaded(), "Roles tab should load successfully");
        System.out.println("✓ Roles tab is visible and accessible");
        Thread.sleep(500); // Stabilize before next test
    }

    @Test(priority = 15, description = "Validate Roles table headers and structure", dependsOnMethods = "testRolesTabVisibility", alwaysRun = true)
    public void testRolesTableHeaders() throws InterruptedException {
        CustomerOrgPage page = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("Validating Roles table headers...");
        Thread.sleep(500); // Wait for table to be ready
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table")));

        List<String> headers = page.getRolesTableHeaders();
        System.out.println("Detected Headers: " + headers);

        Assert.assertFalse(headers.isEmpty(), "Roles table should have headers");

        List<String> expectedHeaders = Arrays.asList("Role Name", "Users", "Permissions", "Status");

        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();
        for (String expected : expectedHeaders) {
            boolean found = headers.stream()
                    .anyMatch(h -> h.toLowerCase().contains(expected.toLowerCase()));
            softAssert.assertTrue(found, "Header missing: " + expected + ". Found: " + headers);
        }
        softAssert.assertAll();
        System.out.println("✓ All Roles table headers validated");
    }

    @Test(priority = 16, description = "Verify Edit icon clickability for roles", dependsOnMethods = "testRolesTabVisibility", alwaysRun = true)
    public void testRolesEditIconClickable() throws InterruptedException {
        CustomerOrgPage page = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("Verifying Roles Edit icon clickability...");
        Thread.sleep(500); // Wait for page to be ready

        // Check if table has rows
        if (page.getRolesTableRowCount() == 0) {
            System.out.println("⚠ No roles in table, skipping Edit icon test");
            return;
        }

        Assert.assertTrue(page.isRolesEditIconClickable(), "Edit icon should be clickable");
        Thread.sleep(300);

        page.clickRolesEditIcon();
        Thread.sleep(1500); // Wait for navigation to complete

        // Verify navigation to edit page
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("edit") || currentUrl.contains("roles/"),
                "Should navigate to edit page. Current URL: " + currentUrl);
        System.out.println("✓ Edit icon is clickable and navigates correctly");

        // Navigate back to roles list
        driver.navigate().back();
        Thread.sleep(2000); // Wait for navigation back to complete
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table")));
    }

    @Test(priority = 17, description = "Verify View icon clickability for roles", dependsOnMethods = {
            "testRolesTabVisibility", "testRolesEditIconClickable" }, alwaysRun = true)
    public void testRolesViewIconClickable() {
        CustomerOrgPage page = new CustomerOrgPage(driver);

        System.out.println("Verifying Roles View icon clickability...");

        // Check if table has rows
        if (page.getRolesTableRowCount() == 0) {
            System.out.println("⚠ No roles in table, skipping View icon test");
            return;
        }

        Assert.assertTrue(page.isRolesViewIconClickable(), "View icon should be clickable");

        page.clickRolesViewIcon();

        // Verify modal/view page displayed
        try {
            Thread.sleep(1000);
            boolean viewDisplayed = page.isRolesViewModalDisplayed() || driver.getCurrentUrl().contains("view");
            Assert.assertTrue(viewDisplayed, "View modal or page should be displayed");
            System.out.println("✓ View icon is clickable and displays content in read-only mode");
        } catch (Exception e) {
            System.out.println("⚠ View verification skipped: " + e.getMessage());
        }

        // Close modal or navigate back
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            Thread.sleep(500);
        } catch (Exception e) {
            driver.navigate().back();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
            }
        }
    }

    @Test(priority = 18, description = "Search with non-existent role name (negative test)", dependsOnMethods = "testRolesTabVisibility", alwaysRun = true)
    public void testRolesSearchNoResults() throws InterruptedException {
        CustomerOrgPage page = new CustomerOrgPage(driver);

        System.out.println("Testing search with non-existent role name...");
        Thread.sleep(500); // Wait before search
        page.searchRole("NONEXISTENT_ROLE_XYZ123");
        Thread.sleep(1500); // Wait for search results

        boolean noData = page.isNoDataMessageDisplayed() || page.getRolesTableRowCount() == 0;
        Assert.assertTrue(noData, "Should show 'No data found' message or empty table for non-existent role");
        System.out.println("✓ Search correctly shows no results for non-existent role");
    }

    @Test(priority = 19, description = "Search with partial role name (positive test)", dependsOnMethods = "testRolesSearchNoResults", alwaysRun = true)
    public void testRolesSearchWithResults() {
        CustomerOrgPage page = new CustomerOrgPage(driver);

        System.out.println("Testing search with partial role name...");

        // Clear previous search
        page.searchRole("");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // Get first role name and search with first 4 characters
        String firstRoleName = page.getFirstRoleName();
        if (firstRoleName.isEmpty()) {
            System.out.println("⚠ No roles found, skipping positive search test");
            return;
        }

        String searchTerm = firstRoleName.substring(0, Math.min(4, firstRoleName.length()));
        System.out.println("Searching with term: " + searchTerm);

        page.searchRole(searchTerm);

        int rowCount = page.getRolesTableRowCount();
        Assert.assertTrue(rowCount > 0, "Should show matching results for partial role name search");
        System.out.println("✓ Search correctly returns " + rowCount + " result(s) for partial match");
    }

    @Test(priority = 20, description = "Apply Active status filter", dependsOnMethods = "testRolesTabVisibility", alwaysRun = true)
    public void testRolesFilterActive() throws InterruptedException {
        CustomerOrgPage page = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("Testing Active status filter...");

        // Clear any search first
        Thread.sleep(500);
        page.searchRole("");
        Thread.sleep(1500); // Wait for search to clear

        page.applyRolesStatusFilter("Active");
        Thread.sleep(1500); // Wait for filter to apply

        // Verify all visible roles have Active status
        List<String> statusValues = page.getRoleStatusValues();
        if (!statusValues.isEmpty()) {
            org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();
            for (String status : statusValues) {
                softAssert.assertTrue(status.equalsIgnoreCase("Active"),
                        "All roles should have Active status. Found: " + status);
            }
            softAssert.assertAll();
            System.out
                    .println("✓ Active filter applied successfully. " + statusValues.size() + " active role(s) shown");
        } else {
            System.out.println("⚠ No roles in table after filtering, verification skipped");
        }
    }

    @Test(priority = 21, description = "Verify Add New button functionality", dependsOnMethods = "testRolesTabVisibility", alwaysRun = true)
    public void testRolesAddNewButton() throws InterruptedException {
        CustomerOrgPage page = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("Verifying Add New Role button...");
        Thread.sleep(500); // Wait before interaction
        Assert.assertTrue(page.isAddNewRoleButtonClickable(), "Add New button should be clickable");

        page.clickAddNewRole();
        Thread.sleep(2000); // Wait for page to load

        Assert.assertTrue(page.isOnAddRolePage(), "Should navigate to Add New Role page");
        System.out.println("✓ Add New button is functional and navigates to role creation page");

        // Navigate back to roles list
        driver.navigate().back();
        Thread.sleep(2000); // Wait for navigation back
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table")));
    }

    @Test(priority = 22, description = "Validate accordion structure in Add/Edit role form", dependsOnMethods = "testRolesAddNewButton", alwaysRun = true)
    public void testRoleFormAccordions() throws InterruptedException {
        CustomerOrgPage page = new CustomerOrgPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("Validating role form accordion structure...");

        // Navigate to Add Role page
        Thread.sleep(500); // Wait before navigation
        page.clickAddNewRole();
        Thread.sleep(2000); // Wait for form to load

        List<String> accordions = page.getAccordionLabels();
        System.out.println("Found accordions: " + accordions);

        Assert.assertFalse(accordions.isEmpty(), "Form should have accordion sections");

        // Verify no duplicates
        Set<String> uniqueAccordions = new HashSet<>(accordions);
        Assert.assertEquals(uniqueAccordions.size(), accordions.size(),
                "Accordions should have no duplicates. Found: " + accordions);

        // Expected minimum accordions: Dashboard, Own services, Wallet usage, Settings
        Assert.assertTrue(accordions.size() >= 4,
                "Should have at least 4 accordion sections. Found: " + accordions.size());

        System.out.println("✓ Form accordion structure validated. No duplicates found.");

        // Close any offcanvas/modal that might be open
        try {
            // Try pressing ESC to close
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            Thread.sleep(500);

            // Try clicking close button if exists
            List<WebElement> closeButtons = driver.findElements(By.xpath(
                    "//button[contains(@class, 'close') or contains(@class, 'btn-close') or @aria-label='Close']"));
            for (WebElement closeBtn : closeButtons) {
                if (closeBtn.isDisplayed()) {
                    closeBtn.click();
                    Thread.sleep(300);
                    break;
                }
            }
        } catch (Exception e) {
            // Ignore if no modal to close
        }

        // Navigate back
        driver.navigate().back();
        Thread.sleep(2000); // Wait for navigation back
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table")));

        // Ensure any backdrop is removed
        Thread.sleep(500);
    }

    @Test(priority = 23, description = "Logout from SuperAdmin session", dependsOnMethods = "testAddNewButtonClickable", alwaysRun = true)
    public void testLogout() throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);
        System.out.println("Step 6: Logging out...");

        // Close any remaining modals/offcanvas before logout
        try {
            // Check for and close offcanvas backdrop
            List<WebElement> backdrops = driver.findElements(By.cssSelector(".offcanvas-backdrop, .modal-backdrop"));
            if (!backdrops.isEmpty()) {
                System.out.println("Found " + backdrops.size() + " backdrop(s), attempting to close...");

                // Press ESC multiple times
                for (int i = 0; i < 3; i++) {
                    driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
                    Thread.sleep(300);
                }

                // Try clicking close buttons
                List<WebElement> closeButtons = driver.findElements(By.xpath(
                        "//button[contains(@class, 'close') or contains(@class, 'btn-close') or @aria-label='Close']"));
                for (WebElement closeBtn : closeButtons) {
                    try {
                        if (closeBtn.isDisplayed()) {
                            closeBtn.click();
                            Thread.sleep(300);
                        }
                    } catch (Exception e) {
                        // Continue if click fails
                    }
                }

                // Force remove backdrops with JavaScript
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "document.querySelectorAll('.offcanvas-backdrop, .modal-backdrop').forEach(el => el.remove());");
                Thread.sleep(500);
                System.out.println("✓ Backdrops removed");
            }
        } catch (Exception e) {
            System.out.println("No backdrops found or already closed");
        }

        // Scroll to top to ensure profile menu is visible
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        Thread.sleep(500);

        loginPage.logout();
    }
}
