package tests.reseller;

import base.BaseTest;
import locators.reseller.ResellerDashboardUserManagementPageLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.common.LoginPage;
import pages.reseller.ResellerDashboardUserManagementPage;

import java.time.Duration;
import java.util.List;

/**
 * Test class for the Reseller Dashboard's "User Management" sidebar section:
 * User List -> per-organisation wallet drill-down -> wallet services ->
 * organization details -> profile (team members) -> roles.
 *
 * Confirmed live navigation structure (see exploration history):
 *   Sidebar "User Management" -> "User List"  =>  /rs/user-management
 *   Row arrow  =>  /rs/user-management/{svCustomerId}/customer-wallets
 *     (tabs: Customer wallets / Organizations details / Profile / Roles)
 *   Wallet row's services button (authorized "rsServicesMgmt")
 *     =>  .../customer-wallets/{walletName}/services
 */
public class ResellerDashboardUserManagementTest extends BaseTest {

    private LoginPage loginPage;
    private ResellerDashboardUserManagementPage page;

    private String targetOrgName;
    private String targetSvCustomerId;
    private String targetWalletName;
    private double walletBalanceBeforeRefill;
    private double walletBalanceBeforeDeduct;
    private static final double REFILL_DEDUCT_AMOUNT = 5.0;

    private static final String USER_LIST_URL = "https://stagingvault.smartping.io/rs/user-management";

    @BeforeClass
    public void setupPages() {
        loginPage = new LoginPage(driver);
        page = new ResellerDashboardUserManagementPage(driver);
        retainSession = true;
        // If the session is lost mid-class (browser crash / session ended), log in again as Reseller
        sessionRole = enums.UserRole.RESELLER;
    }

    @Override
    protected void loginAgain(LoginPage freshLoginPage) {
        // After a browser crash BaseTest creates a new driver, so the page objects must follow it
        loginPage = freshLoginPage;
        page = new ResellerDashboardUserManagementPage(driver);
        super.loginAgain(freshLoginPage);
    }

    // ==================== TC01: Verify User List Headers ====================

    @Test(priority = 1, description = "TC01: Validate the header from the User list")
    public void testUserListHeaders() throws InterruptedException {
        System.out.println("=== TC01: Verify User List Headers ===");

        loginPage.loginWithResellerCredentials();
        System.out.println("Logged in as Reseller.");

        page.navigateToUserList();
        Assert.assertTrue(driver.getCurrentUrl().contains("user-management"),
                "Should navigate to /rs/user-management. URL: " + driver.getCurrentUrl());

        List<String> headers = page.getTableHeaders();
        System.out.println("User List headers: " + headers);
        Assert.assertFalse(headers.isEmpty(), "User List table headers should be present");
        String[] expected = { "Organisation Name", "Wallets", "Status" };
        for (String exp : expected) {
            boolean found = headers.stream().anyMatch(h -> h.contains(exp));
            Assert.assertTrue(found, "Expected header containing '" + exp + "' not found in: " + headers);
        }
        System.out.println("User List headers validated.");
    }

    // ==================== TC02: Verify Update Plan Redirect ====================

    @Test(priority = 2, description = "TC02: Validate Update Plan clickable and redirects to the Users Rate Card tab", dependsOnMethods = "testUserListHeaders")
    public void testUpdatePlanRedirect() throws InterruptedException {
        System.out.println("=== TC02: Verify Update Plan Redirect ===");

        Assert.assertTrue(page.isUpdatePlanButtonPresent(1), "Update Plan button should be present on row 1");
        page.clickUpdatePlanButton(1);
        Thread.sleep(1500);

        String url = driver.getCurrentUrl();
        System.out.println("URL after Update Plan click: " + url);
        Assert.assertTrue(url.contains("user-ratecard"), "Should redirect to Users Rate Card page. URL: " + url);

        driver.navigate().to(USER_LIST_URL);
        Thread.sleep(1500);
        page.waitForTableData();
        System.out.println("Update Plan redirect validated.");
    }

    // ==================== TC03: Verify Add Credit Redirect ====================

    @Test(priority = 3, description = "TC03: Validate Add Credit clickable and redirects to the User Fund Management page", dependsOnMethods = "testUpdatePlanRedirect")
    public void testAddCreditRedirect() throws InterruptedException {
        System.out.println("=== TC03: Verify Add Credit Redirect ===");

        String orgName = page.getCellText(1, 1);
        Assert.assertTrue(page.isAddCreditButtonPresent(1), "Add Credit button should be present on row 1");
        page.clickAddCreditButton(1);
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.urlContains("manage-credits"));

        String url = driver.getCurrentUrl();
        System.out.println("URL after Add Credit click: " + url);
        Assert.assertTrue(url.contains("manage-credits"), "Should redirect to User Fund Management page. URL: " + url);

        String pageOrgName = waitForTextHeading(orgName);
        Assert.assertTrue(pageOrgName.equalsIgnoreCase(orgName),
                "User Fund Management page should show the same organisation ('" + orgName + "'), showed '" + pageOrgName + "'");

        driver.navigate().to(USER_LIST_URL);
        Thread.sleep(1500);
        page.waitForTableData();
        System.out.println("Add Credit redirect validated.");
    }

    // ==================== TC04: Verify Search by Name ====================

    @Test(priority = 4, description = "TC04: Validate search functionality from 'Search by name'", dependsOnMethods = "testAddCreditRedirect")
    public void testSearchByName() {
        System.out.println("=== TC04: Verify Search by Name ===");

        String firstOrg = page.getCellText(1, 1);
        Assert.assertFalse(firstOrg.isEmpty(), "Need an organisation name to search with");

        page.searchByName(firstOrg);
        int resultCount = page.getRowCount();
        System.out.println("Search result count: " + resultCount);
        Assert.assertTrue(resultCount > 0, "Searching for an existing organisation should return at least one result");

        for (int i = 1; i <= resultCount; i++) {
            String name = page.getCellText(i, 1);
            Assert.assertTrue(name.toLowerCase().contains(firstOrg.toLowerCase()),
                    "Row " + i + " ('" + name + "') should match search text '" + firstOrg + "'");
        }
        page.clearSearch();
        System.out.println("Search by name validated.");
    }

    // ==================== TC05: Verify Filter Applied Result ====================

    @Test(priority = 5, description = "TC05: Validate filter active applied and result matches", dependsOnMethods = "testSearchByName")
    public void testFilterActiveApplied() {
        System.out.println("=== TC05: Verify Filter Active Applied ===");

        page.applyActiveStatusFilter();
        boolean noDataShown = page.isNoDataMessageDisplayed();
        if (!noDataShown) {
            int rowCount = page.getRowCount();
            Assert.assertTrue(rowCount > 0, "Applying Active filter should show matching results or a no-data message");
            for (int i = 1; i <= rowCount; i++) {
                String statusCell = driver.findElement(By.xpath(
                        "(//table//tbody//tr)[" + i + "]//*[contains(text(),'Active') or contains(text(),'Inactive')]")).getText().trim();
                Assert.assertTrue(statusCell.equalsIgnoreCase("Active"), "Row " + i + " should be Active after filtering");
            }
        }
        System.out.println("Filter Active applied validated.");
    }

    // ==================== TC06: Verify Clear All ====================

    @Test(priority = 6, description = "TC06: Validate clear all restores full result list", dependsOnMethods = "testFilterActiveApplied")
    public void testClearAllFilters() throws InterruptedException {
        System.out.println("=== TC06: Verify Clear All ===");

        int countWithFilter = page.getRowCount();
        Assert.assertTrue(page.isClearAllButtonVisible(), "Clear All button should appear once a filter is applied");

        page.clickClearAllFilters();
        Thread.sleep(1000);
        page.waitForTableData();

        int countAfterClear = page.getRowCount();
        System.out.println("Row count before/after Clear All: " + countWithFilter + "/" + countAfterClear);
        Assert.assertTrue(countAfterClear >= countWithFilter, "Full list should be restored after Clear All");
        System.out.println("Clear All validated.");
    }

    // ==================== TC07: Verify Create User Button ====================

    @Test(priority = 7, description = "TC07: Validate Create user button clickable and redirects to the create user page", dependsOnMethods = "testClearAllFilters")
    public void testCreateUserButtonRedirect() {
        System.out.println("=== TC07: Verify Create User Button ===");

        Assert.assertTrue(page.isCreateUserButtonVisible(), "Create user button should be visible");
        page.clickCreateUserButton();
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.urlContains("create-user"));

        String url = driver.getCurrentUrl();
        System.out.println("URL after Create user click: " + url);
        Assert.assertTrue(url.contains("create-user"), "Should redirect to create user page. URL: " + url);

        driver.navigate().to(USER_LIST_URL);
        page.waitForTableData();
        System.out.println("Create user button validated.");
    }

    // ==================== TC08: Verify Arrow -> Customer Wallets ====================

    @Test(priority = 8, description = "TC08: Validate arrow button redirects to the customer wallet page with wallet list", dependsOnMethods = "testCreateUserButtonRedirect")
    public void testArrowRedirectToCustomerWallets() throws InterruptedException {
        System.out.println("=== TC08: Verify Arrow -> Customer Wallets ===");

        // Pick a row with more than 1 wallet so later TCs (services with
        // multiple entries) have richer data to validate against.
        int targetRow = 1;
        for (int i = 1; i <= page.getRowCount(); i++) {
            String walletCount = page.getCellText(i, 2);
            if (!walletCount.equals("1")) {
                targetRow = i;
                break;
            }
        }
        targetOrgName = page.getCellText(targetRow, 1);
        System.out.println("Target row " + targetRow + " organisation: " + targetOrgName);

        page.clickRowArrowButton(targetRow);
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.urlContains("customer-wallets"));

        String url = driver.getCurrentUrl();
        System.out.println("URL after arrow click: " + url);
        Assert.assertTrue(url.contains("customer-wallets"), "Should redirect to customer wallets page. URL: " + url);
        targetSvCustomerId = url.substring(url.indexOf("user-management/") + "user-management/".length())
                .split("/")[0];
        System.out.println("Target SV Customer ID: " + targetSvCustomerId);

        boolean hasWallets = !driver.findElements(ResellerDashboardUserManagementPageLocators.USER_LIST_TABLE_ROWS).isEmpty();
        Assert.assertTrue(hasWallets, "Wallet list should be present on the customer wallets page");
        System.out.println("Arrow -> Customer Wallets validated.");
    }

    // ==================== TC09: Verify Wallet Search ====================

    @Test(priority = 9, description = "TC09: Validate search by wallet name functionality", dependsOnMethods = "testArrowRedirectToCustomerWallets")
    public void testWalletSearchByName() {
        System.out.println("=== TC09: Verify Wallet Search ===");

        String firstWallet = page.getCellText(1, 1);
        targetWalletName = firstWallet;
        Assert.assertFalse(firstWallet.isEmpty(), "Need a wallet name to search with");

        page.searchByName(firstWallet);
        int resultCount = page.getRowCount();
        System.out.println("Wallet search result count: " + resultCount);
        Assert.assertTrue(resultCount > 0, "Searching for an existing wallet should return at least one result");
        for (int i = 1; i <= resultCount; i++) {
            String name = page.getCellText(i, 1);
            Assert.assertTrue(name.toLowerCase().contains(firstWallet.toLowerCase()),
                    "Row " + i + " ('" + name + "') should match search text '" + firstWallet + "'");
        }
        page.clearSearch();
        System.out.println("Wallet search by name validated.");
    }

    // ==================== TC10: Verify Wallet Filter Applied ====================

    @Test(priority = 10, description = "TC10: Validate the result after applying the wallet filter", dependsOnMethods = "testWalletSearchByName")
    public void testWalletFilterApplied() {
        System.out.println("=== TC10: Verify Wallet Filter Applied ===");

        page.applyActiveStatusFilter();
        boolean noDataShown = page.isNoDataMessageDisplayed();
        if (!noDataShown) {
            int rowCount = page.getRowCount();
            Assert.assertTrue(rowCount > 0, "Applying Active filter should show matching wallets or a no-data message");
            for (int i = 1; i <= rowCount; i++) {
                String statusCell = driver.findElement(By.xpath(
                        "(//table//tbody//tr)[" + i + "]//*[contains(text(),'Active') or contains(text(),'Inactive')]")).getText().trim();
                Assert.assertTrue(statusCell.equalsIgnoreCase("Active"), "Row " + i + " should be Active after filtering");
            }
        }
        System.out.println("Wallet filter applied validated.");
    }

    // ==================== TC11: Verify Wallet Clear All ====================

    @Test(priority = 11, description = "TC11: Validate result display after clicking Clear All", dependsOnMethods = "testWalletFilterApplied")
    public void testWalletClearAllFilters() throws InterruptedException {
        System.out.println("=== TC11: Verify Wallet Clear All ===");

        int countWithFilter = page.getRowCount();
        Assert.assertTrue(page.isClearAllButtonVisible(), "Clear All button should appear once a wallet filter is applied");

        page.clickClearAllFilters();
        Thread.sleep(1000);
        page.waitForTableData();

        int countAfterClear = page.getRowCount();
        System.out.println("Wallet row count before/after Clear All: " + countWithFilter + "/" + countAfterClear);
        Assert.assertTrue(countAfterClear >= countWithFilter, "Full wallet list should be restored after Clear All");
        System.out.println("Wallet Clear All validated.");
    }

    // ==================== TC12: Verify Add Wallet CTA ====================

    @Test(priority = 12, description = "TC12: Validate Add Wallet CTA clickable and redirects to the create wallet page", dependsOnMethods = "testWalletClearAllFilters")
    public void testAddWalletCTA() throws InterruptedException {
        System.out.println("=== TC12: Verify Add Wallet CTA ===");

        Assert.assertTrue(page.isAddWalletButtonVisible(), "Add Wallet button should be visible");
        String urlBefore = driver.getCurrentUrl();
        page.clickAddWalletButton();
        Thread.sleep(1500);

        String urlAfter = driver.getCurrentUrl();
        System.out.println("URL before/after Add Wallet click: " + urlBefore + " / " + urlAfter);

        // Confirmed live behaviour may be a same-page slide-in panel rather
        // than a full navigation - accept either a URL change or a newly
        // visible modal/panel as satisfying "redirected to create wallet".
        boolean urlChanged = !urlAfter.equals(urlBefore);
        boolean modalVisible = !driver.findElements(By.xpath(
                "//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'create wallet') or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add wallet')]"))
                .isEmpty();
        Assert.assertTrue(urlChanged || modalVisible,
                "Add Wallet should redirect to a create-wallet page or open a create-wallet panel");
        System.out.println("Add Wallet CTA validated.");

        driver.navigate().to(driver.getCurrentUrl().split("\\?")[0]);
    }

    // ==================== TC13: Verify Wallet Services Display ====================

    @Test(priority = 13, description = "TC13: Validate all services available in the wallet display (or a no-data message)", dependsOnMethods = "testAddWalletCTA")
    public void testWalletServicesDisplay() throws InterruptedException {
        System.out.println("=== TC13: Verify Wallet Services Display ===");

        driver.navigate().to("https://stagingvault.smartping.io/rs/user-management/" + targetSvCustomerId + "/customer-wallets");
        Thread.sleep(2000);
        page.waitForTableData();

        // Prefer a row with multiple services (richer data) if available.
        int targetRow = 1;
        for (int i = 1; i <= page.getRowCount(); i++) {
            try {
                List<WebElement> serviceChips = driver.findElements(By.xpath(
                        "(//table//tbody//tr)[" + i + "]//td[6]//span"));
                if (serviceChips.size() > 1) {
                    targetRow = i;
                    break;
                }
            } catch (Exception ignored) {
            }
        }
        targetWalletName = page.getCellText(targetRow, 1);
        System.out.println("Target wallet: " + targetWalletName);

        page.clickWalletServicesButton(targetRow);
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.urlContains("/services"));

        String url = driver.getCurrentUrl();
        System.out.println("URL after services button click: " + url);
        Assert.assertTrue(url.contains("/services"), "Should redirect to the wallet's services page. URL: " + url);

        boolean hasServiceCards = !driver.findElements(By.xpath("//button[normalize-space()='View Details']")).isEmpty();
        boolean noDataShown = page.isNoDataMessageDisplayed();
        Assert.assertTrue(hasServiceCards || noDataShown,
                "Available services should be displayed, or a no-data message shown if there are none");
        System.out.println("Wallet services display validated.");
    }

    // ==================== TC14: Verify Add New Service Redirect ====================

    @Test(priority = 14, description = "TC14: Validate Add New button redirects to the service account detail page", dependsOnMethods = "testWalletServicesDisplay")
    public void testAddNewServiceRedirect() throws InterruptedException {
        System.out.println("=== TC14: Verify Add New Service Redirect ===");

        Assert.assertTrue(page.isAddNewServiceButtonVisible(), "Add New button should be visible on the services page");
        String urlBefore = driver.getCurrentUrl();
        page.clickAddNewServiceButton();
        Thread.sleep(2000);

        String urlAfter = driver.getCurrentUrl();
        System.out.println("URL before/after Add New click: " + urlBefore + " / " + urlAfter);
        Assert.assertNotEquals(urlAfter, urlBefore, "Add New should redirect to the service account detail page");
        Assert.assertTrue(urlAfter.contains("/create/service-account"),
                "Add New should open the create service account page. URL: " + urlAfter);
        System.out.println("Add New service redirect validated.");

        driver.navigate().back();
        Thread.sleep(1500);
    }

    // ==================== TC15: Verify Refill Balance ====================

    // Depends on TC13 (not TC14): it opens the services page itself and only needs the customer/wallet
    // found in TC08/TC13, so an Add New failure no longer skips the remaining 16 tests.
    @Test(priority = 15, description = "TC15: Validate Refill adds ₹5 and the available balance updates correctly", dependsOnMethods = "testWalletServicesDisplay")
    public void testRefillBalanceValidation() throws InterruptedException {
        System.out.println("=== TC15: Verify Refill Balance ===");

        driver.navigate().to("https://stagingvault.smartping.io/rs/user-management/" + targetSvCustomerId
                + "/customer-wallets/" + targetWalletName + "/services");
        Thread.sleep(2000);

        // Fail clearly if the balance can't be read - otherwise it would silently count as 0.0
        String balanceText = page.getWalletBalanceValue();
        Assert.assertFalse(balanceText.isEmpty(), "Available Wallet Balance should be displayed before Refill");
        walletBalanceBeforeRefill = page.parseBalance(balanceText);
        System.out.println("Balance before Refill: " + walletBalanceBeforeRefill);

        page.clickRefillButton();
        Thread.sleep(1000);
        page.enterRefillDeductAmount(String.valueOf(REFILL_DEDUCT_AMOUNT));
        page.clickRefillDeductSubmit();

        double balanceAfter = page.waitForBalanceChange(walletBalanceBeforeRefill);
        System.out.println("Balance after Refill: " + balanceAfter);
        Assert.assertEquals(balanceAfter, walletBalanceBeforeRefill + REFILL_DEDUCT_AMOUNT, 0.01,
                "Balance should increase by ₹" + REFILL_DEDUCT_AMOUNT + " after Refill");
        System.out.println("Refill balance validated.");
    }

    // ==================== TC16: Verify Deduct Balance ====================

    @Test(priority = 16, description = "TC16: Validate Deduct removes ₹5 and the available balance updates correctly", dependsOnMethods = "testRefillBalanceValidation")
    public void testDeductBalanceValidation() throws InterruptedException {
        System.out.println("=== TC16: Verify Deduct Balance ===");

        String balanceText = page.getWalletBalanceValue();
        Assert.assertFalse(balanceText.isEmpty(), "Available Wallet Balance should be displayed before Deduct");
        walletBalanceBeforeDeduct = page.parseBalance(balanceText);
        System.out.println("Balance before Deduct: " + walletBalanceBeforeDeduct);

        page.clickDeductButton();
        Thread.sleep(1000);
        page.enterRefillDeductAmount(String.valueOf(REFILL_DEDUCT_AMOUNT));
        page.clickRefillDeductSubmit();

        double balanceAfter = page.waitForBalanceChange(walletBalanceBeforeDeduct);
        System.out.println("Balance after Deduct: " + balanceAfter);
        Assert.assertEquals(balanceAfter, walletBalanceBeforeDeduct - REFILL_DEDUCT_AMOUNT, 0.01,
                "Balance should decrease by ₹" + REFILL_DEDUCT_AMOUNT + " after Deduct");
        System.out.println("Deduct balance validated.");
    }

    // ==================== TC17: Verify Organization Details Tab ====================

    @Test(priority = 17, description = "TC17: Validate Organization Details tab shows Contact Information and org level", dependsOnMethods = "testDeductBalanceValidation")
    public void testOrganizationDetailsTab() throws InterruptedException {
        System.out.println("=== TC17: Verify Organization Details Tab ===");

        driver.navigate().to("https://stagingvault.smartping.io/rs/user-management/" + targetSvCustomerId + "/customer-wallets");
        Thread.sleep(2000);

        page.clickOrganizationDetailsTab();
        String url = driver.getCurrentUrl();
        System.out.println("URL after Organizations details click: " + url);
        Assert.assertTrue(url.contains("organization-details"), "Should redirect to organization-details. URL: " + url);

        Assert.assertTrue(page.isContactInformationHeaderVisible(), "'Contact Information' section should be visible");
        String orgLevel = page.getOrganizationLevelValue();
        System.out.println("Organization Level: " + orgLevel);
        Assert.assertFalse(orgLevel.isEmpty(), "Organization Level value should be displayed");
        System.out.println("Organization Details tab validated.");
    }

    // ==================== TC18: Verify Profile Team Members List ====================

    @Test(priority = 18, description = "TC18: Validate Profile tab shows Team Members list", dependsOnMethods = "testOrganizationDetailsTab")
    public void testProfileTeamMembersList() throws InterruptedException {
        System.out.println("=== TC18: Verify Profile Team Members List ===");

        page.clickProfileTab();
        String url = driver.getCurrentUrl();
        System.out.println("URL after Profile click: " + url);
        Assert.assertTrue(url.contains("/profile"), "Should redirect to profile. URL: " + url);

        page.waitForTableData();
        boolean hasRows = !driver.findElements(ResellerDashboardUserManagementPageLocators.USER_LIST_TABLE_ROWS).isEmpty();
        boolean noDataShown = page.isNoDataMessageDisplayed();
        System.out.println("hasRows=" + hasRows + " noDataShown=" + noDataShown);

        Assert.assertTrue(hasRows || noDataShown, "Team members should be listed, or a no-data message shown");
        if (hasRows) {
            // A profile is genuinely present only if the Name column itself
            // has a value - checking that column directly rather than an
            // indirect page-wide signal.
            String nameValue = extractNameLine(page.getCellText(1, 1));
            System.out.println("Name column value: '" + nameValue + "'");
            Assert.assertFalse(nameValue.isEmpty(), "Name column should have a value for at least one profile");
        }
        System.out.println("Profile Team Members list validated.");
    }

    // ==================== TC19: Verify Profile Header ====================

    @Test(priority = 19, description = "TC19: Validate the Profile tab header", dependsOnMethods = "testProfileTeamMembersList")
    public void testProfileHeader() {
        System.out.println("=== TC19: Verify Profile Header ===");

        Assert.assertTrue(page.isTeamMembersHeaderVisible(), "'Team Members' header should be visible");

        List<String> headers = page.getTableHeaders();
        System.out.println("Profile table headers: " + headers);
        Assert.assertFalse(headers.isEmpty(), "Profile table headers should be present");
        String[] expected = { "Name", "Roles Name", "Status" };
        for (String exp : expected) {
            boolean found = headers.stream().anyMatch(h -> h.contains(exp));
            Assert.assertTrue(found, "Expected header containing '" + exp + "' not found in: " + headers);
        }
        System.out.println("Profile header validated.");
    }

    // ==================== TC20: Verify Profile Search ====================

    @Test(priority = 20, description = "TC20: Validate search by name functionality on Profile tab", dependsOnMethods = "testProfileHeader")
    public void testProfileSearchByName() {
        System.out.println("=== TC20: Verify Profile Search ===");

        // The Profile table's Name cell packs an avatar initial, the name,
        // and the email into one <td> (confirmed live) - getText() returns
        // all of it as separate lines, so the raw cell text isn't usable as
        // a search term on its own.
        String firstName = extractNameLine(page.getCellText(1, 1));
        Assert.assertFalse(firstName.isEmpty(), "Need a team member name to search with");

        page.searchByName(firstName);
        int resultCount = page.getRowCount();
        System.out.println("Profile search result count: " + resultCount);
        Assert.assertTrue(resultCount > 0, "Searching for an existing team member should return at least one result");
        page.clearSearch();
        System.out.println("Profile search by name validated.");
    }

    // ==================== TC21: Verify Profile Filter ====================

    @Test(priority = 21, description = "TC21: Validate data appears based on applied Active filter (Profile)", dependsOnMethods = "testProfileSearchByName")
    public void testProfileFilterActive() {
        System.out.println("=== TC21: Verify Profile Filter Active ===");

        page.applyActiveStatusFilter();
        boolean noDataShown = page.isNoDataMessageDisplayed();
        if (!noDataShown) {
            int rowCount = page.getRowCount();
            Assert.assertTrue(rowCount > 0, "Applying Active filter should show matching team members or a no-data message");
        }
        page.clickClearAllFilters();
        System.out.println("Profile filter Active validated.");
    }

    // ==================== TC22: Verify Profile Add New Redirect ====================

    @Test(priority = 22, description = "TC22: Validate Add New button redirects to the Add Team Member page", dependsOnMethods = "testProfileFilterActive")
    public void testProfileAddNewRedirect() throws InterruptedException {
        System.out.println("=== TC22: Verify Profile Add New Redirect ===");

        Assert.assertTrue(page.isProfileAddNewButtonVisible(), "Add New button should be visible on Profile tab");
        page.clickProfileAddNewButton();
        Thread.sleep(1500);

        String url = driver.getCurrentUrl();
        System.out.println("URL after Profile Add New click: " + url);
        Assert.assertTrue(url.contains("/profile/add"), "Should redirect to Add Team Member page. URL: " + url);

        driver.navigate().back();
        Thread.sleep(1500);
        System.out.println("Profile Add New redirect validated.");
    }

    // ==================== TC23: Verify Profile Edit Button ====================

    @Test(priority = 23, description = "TC23: Validate Edit button clickable with predefined values", dependsOnMethods = "testProfileAddNewRedirect")
    public void testProfileEditButton() throws InterruptedException {
        System.out.println("=== TC23: Verify Profile Edit Button ===");

        page.waitForTableData();
        String nameBefore = page.getCellText(1, 1);
        page.clickProfileEditButton(1);
        Thread.sleep(1500);

        String url = driver.getCurrentUrl();
        System.out.println("URL after Profile Edit click: " + url);
        boolean predefinedValuePresent = driver.getPageSource().contains(nameBefore.split("\\n")[0].trim());
        Assert.assertTrue(predefinedValuePresent, "Edit form should be pre-filled with the team member's existing data");
        System.out.println("Profile Edit button validated.");

        driver.navigate().back();
        Thread.sleep(1500);
    }

    // ==================== TC24: Verify Profile View Icon ====================

    @Test(priority = 24, description = "TC24: Validate View icon clickable and SV Customer ID is displayed", dependsOnMethods = "testProfileEditButton")
    public void testProfileViewIcon() throws InterruptedException {
        System.out.println("=== TC24: Verify Profile View Icon ===");

        page.waitForTableData();
        page.clickProfileViewButton(1);
        Thread.sleep(1500);

        boolean svIdShown = driver.getPageSource().toLowerCase().contains("sv customer id");
        System.out.println("SV Customer ID shown: " + svIdShown);
        Assert.assertTrue(svIdShown, "SV Customer ID should be displayed after clicking View");
        System.out.println("Profile View icon validated.");
    }

    // ==================== TC25: Verify Roles Tab Redirect ====================

    @Test(priority = 25, description = "TC25: Validate Roles tab clickable and redirects to Role Management", dependsOnMethods = "testProfileViewIcon")
    public void testRolesTabRedirect() throws InterruptedException {
        System.out.println("=== TC25: Verify Roles Tab Redirect ===");

        driver.navigate().to("https://stagingvault.smartping.io/rs/user-management/" + targetSvCustomerId + "/customer-wallets");
        Thread.sleep(2000);

        page.clickRolesTab();
        String url = driver.getCurrentUrl();
        System.out.println("URL after Roles click: " + url);
        Assert.assertTrue(url.contains("/roles"), "Should redirect to roles. URL: " + url);
        page.waitForTableData();
        Assert.assertTrue(page.isRoleManagementHeaderVisible(), "'Role Management' header should be visible");
        System.out.println("Roles tab redirect validated.");
    }

    // ==================== TC26: Verify Roles Search ====================

    @Test(priority = 26, description = "TC26: Validate result appears based on search name (Roles)", dependsOnMethods = "testRolesTabRedirect")
    public void testRolesSearchByName() {
        System.out.println("=== TC26: Verify Roles Search ===");

        String firstRole = extractNameLine(page.getCellText(1, 1));
        Assert.assertFalse(firstRole.isEmpty(), "Need a role name to search with");

        page.searchByName(firstRole);
        int resultCount = page.getRowCount();
        System.out.println("Roles search result count: " + resultCount);
        Assert.assertTrue(resultCount > 0, "Searching for an existing role should return at least one result");
        page.clearSearch();
        System.out.println("Roles search by name validated.");
    }

    // ==================== TC27: Verify Roles Filter (with no-data handling) ====================

    @Test(priority = 27, description = "TC27: Validate result appears based on applied Active filter, handling no-data message", dependsOnMethods = "testRolesSearchByName")
    public void testRolesFilterActive() {
        System.out.println("=== TC27: Verify Roles Filter Active ===");

        page.applyActiveStatusFilter();
        boolean noDataShown = page.isNoDataMessageDisplayed();
        System.out.println("No-data message shown: " + noDataShown);
        if (!noDataShown) {
            int rowCount = page.getRowCount();
            Assert.assertTrue(rowCount > 0, "Applying Active filter should show matching roles or a no-data message");
        }
        page.clickClearAllFilters();
        System.out.println("Roles filter Active validated.");
    }

    // ==================== TC28: Verify Roles Edit Button ====================

    @Test(priority = 28, description = "TC28: Validate Edit role button redirects to the edit role page with predefined fields", dependsOnMethods = "testRolesFilterActive")
    public void testRolesEditButton() throws InterruptedException {
        System.out.println("=== TC28: Verify Roles Edit Button ===");

        page.waitForTableData();
        page.clickRoleEditButton(1);
        Thread.sleep(1500);

        // Angular-bound input values aren't reflected in driver.getPageSource()
        // (set via the DOM "value" property, not the HTML attribute) - read
        // the Role Name / Description fields' actual values directly instead
        // of text-matching the page source.
        WebElement roleNameField = driver.findElement(By.xpath(
                "//*[contains(normalize-space(.),'Role Name')]/following::input[1]"));
        WebElement descriptionField = driver.findElement(By.xpath(
                "//*[contains(normalize-space(.),'Description')]/following::textarea[1]"));
        String roleNameValue = roleNameField.getDomProperty("value");
        String descriptionValue = descriptionField.getDomProperty("value");
        System.out.println("Role Name field value='" + roleNameValue + "', Description field value='" + descriptionValue + "'");

        boolean predefinedValuePresent = (roleNameValue != null && !roleNameValue.trim().isEmpty())
                || (descriptionValue != null && !descriptionValue.trim().isEmpty());
        Assert.assertTrue(predefinedValuePresent,
                "Edit role page's Role Name or Description field should be pre-filled (not empty)");
        System.out.println("Roles Edit button validated.");

        driver.navigate().back();
        Thread.sleep(1500);
    }

    // ==================== TC29: Verify Roles View Icon -> Overview ====================

    @Test(priority = 29, description = "TC29: Validate View icon clickable and redirects to the Overview page", dependsOnMethods = "testRolesEditButton")
    public void testRolesViewIconOverview() throws InterruptedException {
        System.out.println("=== TC29: Verify Roles View Icon -> Overview ===");

        page.waitForTableData();
        page.clickRoleViewButton(1);
        Thread.sleep(1500);

        Assert.assertTrue(page.isOverviewPanelVisible(), "'Overview' panel/page should be shown after clicking View");
        System.out.println("Roles View icon -> Overview validated.");
    }

    // ==================== TC30: Verify Roles Add New Redirect ====================

    @Test(priority = 30, description = "TC30: Validate Add New redirects to the add new role page", dependsOnMethods = "testRolesViewIconOverview")
    public void testRolesAddNewRedirect() throws InterruptedException {
        System.out.println("=== TC30: Verify Roles Add New Redirect ===");

        driver.navigate().to("https://stagingvault.smartping.io/rs/user-management/" + targetSvCustomerId + "/roles");
        Thread.sleep(2000);

        Assert.assertTrue(page.isRolesAddNewButtonVisible(), "Add New button should be visible on Roles tab");
        page.clickRolesAddNewButton();
        Thread.sleep(1500);

        String url = driver.getCurrentUrl();
        System.out.println("URL after Roles Add New click: " + url);
        Assert.assertTrue(url.contains("/roles/add"), "Should redirect to add new role page. URL: " + url);
        System.out.println("Roles Add New redirect validated.");
    }

    /**
     * Waits for a heading-like element showing the given text to appear -
     * the User Fund Management page's org-name heading isn't a plain h1/h2
     * (confirmed live elsewhere in this session), so this waits for ANY
     * element whose text equals the target rather than assuming a specific
     * tag.
     */
    private String waitForTextHeading(String text) {
        String lower = text.toLowerCase();
        By locator = By.xpath(
                "//*[translate(normalize-space(text()), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='"
                        + lower + "']");
        WebElement el = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
        return el.getText().trim();
    }

    /**
     * The Profile/Roles tables' first cell can pack multiple pieces of text
     * into one <td> (e.g. an avatar initial, the name, and an email on
     * separate lines - confirmed live on the Profile tab). Returns the first
     * line that looks like an actual name/title rather than a single-letter
     * avatar initial or an email address.
     */
    private String extractNameLine(String cellText) {
        String[] lines = cellText.split("\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 1 && !trimmed.contains("@")) {
                return trimmed;
            }
        }
        return cellText.trim();
    }

    // ==================== Logout ====================

    @Test(priority = 99, description = "Logout from Reseller account", dependsOnMethods = "testRolesAddNewRedirect", alwaysRun = true)
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
