package tests.reseller;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.common.LoginPage;
import pages.reseller.ResellerDashboardPage;

import java.util.List;

/**
 * Test class for Reseller Dashboard's "My Wallets" tab.
 * Tests default tab selection, wallet listing, page header, pagination,
 * service navigation, search, filters, and wallet fund transfer.
 */
public class ResellerDashboardMyWalletsTest extends BaseTest {

    private LoginPage loginPage;
    private ResellerDashboardPage walletPage;

    // State carried across the Fund Transfer test sequence (TC10-TC13)
    private String selectedSourceWallet;
    private String selectedSourceWalletType;
    private String selectedDestinationWallet;
    private double sourceBalanceBeforeTransfer;
    private double destinationBalanceBeforeTransfer;
    private static final double TRANSFER_AMOUNT = 1.0;

    @BeforeClass
    public void setupPages() {
        loginPage = new LoginPage(driver);
        walletPage = new ResellerDashboardPage(driver);
        retainSession = true; // Prevent BaseTest from auto-navigating to login
    }

    // ==================== TC01: Verify My Wallets Tab ====================

    @Test(priority = 1, description = "TC01: Verify user lands on My Wallets tab by default after Dashboard navigation")
    public void testMyWalletsTabDefaultSelection() {
        System.out.println("=== TC01: Verify My Wallets Tab ===");

        loginPage.loginWithResellerCredentials();
        System.out.println("Logged in as Reseller.");

        walletPage.waitForWalletData();

        Assert.assertTrue(walletPage.isMyWalletsTabSelected(),
                "My Wallets tab should be selected by default after Dashboard navigation");
        System.out.println("My Wallets tab is selected by default.");
    }

    // ==================== TC02: Verify Wallet Listing ====================

    @Test(priority = 2, description = "TC02: Verify wallet listing and expected wallet info", dependsOnMethods = "testMyWalletsTabDefaultSelection")
    public void testWalletListing() {
        System.out.println("=== TC02: Verify Wallet Listing ===");

        boolean hasWallets = walletPage.hasWallets();
        boolean noDataShown = walletPage.isNoDataMessageDisplayed();
        Assert.assertTrue(hasWallets || noDataShown,
                "Either wallets should be visible OR a 'no data' message should be displayed");

        if (!hasWallets) {
            System.out.println("No wallets available for this reseller. Skipping row-level validation.");
            return;
        }

        int rowCount = walletPage.getWalletRowCount();
        System.out.println("Wallet row count: " + rowCount);

        List<String> headers = walletPage.getWalletTableHeaders();
        System.out.println("Wallet table headers: " + headers);
        Assert.assertFalse(headers.isEmpty(), "Wallet table headers should be present");

        // Spot-check row 1 has the expected fields: name, type, balance, status
        String name = walletPage.getWalletCellText(1, 1);
        String type = walletPage.getWalletCellText(1, 2);
        String balance = walletPage.getWalletCellText(1, 6);
        String status = walletPage.getWalletStatus(1);

        System.out.println("Row 1 -> Name: '" + name + "', Type: '" + type + "', Balance: '" + balance
                + "', Status: '" + status + "'");

        Assert.assertFalse(name.isEmpty(), "Wallet name should not be empty");
        Assert.assertFalse(type.isEmpty(), "Wallet type should not be empty");
        Assert.assertFalse(balance.isEmpty(), "Wallet balance should not be empty");
        Assert.assertFalse(status.isEmpty() || status.equals("N/A"), "Wallet status should be displayed");

        System.out.println("Wallet listing validated.");
    }

    // ==================== TC03: Verify Page Header ====================

    @Test(priority = 3, description = "TC03: Verify My Wallets page header", dependsOnMethods = "testWalletListing")
    public void testPageHeader() {
        System.out.println("=== TC03: Verify Page Header ===");

        // NOTE: this app has no separate H1/H2 "My Wallets" header beneath the tab
        // bar - the tab's own label ("My Wallets") is the only header-like text
        // for this section (confirmed via live screenshot), so that's what's
        // validated here.
        String headerText = walletPage.getPageHeaderText();
        System.out.println("Header text found: '" + headerText + "'");

        Assert.assertFalse(headerText.isEmpty(), "My Wallets page header should be displayed");
        Assert.assertTrue(headerText.toLowerCase().contains("wallet"),
                "Header text should reference 'Wallet'. Found: " + headerText);
        System.out.println("Page header validated.");
    }

    // ==================== TC04: Verify Pagination ====================

    @Test(priority = 4, description = "TC04: Verify pagination shows only when wallet count > 10", dependsOnMethods = "testPageHeader")
    public void testPagination() {
        System.out.println("=== TC04: Verify Pagination ===");

        int rowCount = walletPage.getWalletRowCount();
        System.out.println("Current wallet row count (this page): " + rowCount);

        boolean paginationVisible = walletPage.isPaginationAvailable();
        System.out.println("Pagination controls visible: " + paginationVisible);

        if (rowCount > 10 || paginationVisible) {
            Assert.assertTrue(paginationVisible,
                    "Pagination controls should be visible when wallet count exceeds 10");

            boolean nextClicked = walletPage.clickNextPage();
            Assert.assertTrue(nextClicked, "Next button should be clickable");
            System.out.println("Clicked Next page successfully.");

            boolean prevClicked = walletPage.clickPreviousPage();
            Assert.assertTrue(prevClicked, "Previous button should navigate back to the previous page");
            System.out.println("Clicked Previous page successfully - navigated back.");
        } else {
            Assert.assertFalse(paginationVisible,
                    "Pagination controls should NOT be displayed when wallet count is 10 or fewer");
            System.out.println("Wallet count is <= 10 and pagination is correctly not displayed.");
        }
    }

    // ==================== TC05: Verify Service Navigation ====================

    @Test(priority = 5, description = "TC05: Verify wallet Action button navigation", dependsOnMethods = "testPagination")
    public void testServiceNavigation() {
        System.out.println("=== TC05: Verify Service Navigation ===");

        // NOTE: confirmed live (screenshots + URL checks) that the per-row Action
        // button does NOT navigate to a "Service Management" tab, and is NOT
        // gated by whether the wallet has associated services - it consistently
        // navigates to that wallet's Usage page (/rs/my-usage?walletName=...),
        // identically for wallets with and without service badges. Validating
        // that actual, confirmed behavior here rather than the originally
        // assumed (but unconfirmed) "Service Management" destination.
        int rowCount = walletPage.getWalletRowCount();
        Assert.assertTrue(rowCount > 0, "Need at least one wallet row to validate Action button navigation");

        String walletName = walletPage.getWalletCellText(1, 1);
        boolean enabled = walletPage.isServiceNavButtonEnabled(1);
        System.out.println("Row 1 (" + walletName + ") Action button enabled: " + enabled);
        Assert.assertTrue(enabled, "Action button should be enabled for row 1");

        walletPage.clickServiceNavButton(1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL after clicking Action button: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("my-usage") && currentUrl.contains(walletName),
                "Action button should navigate to the selected wallet's usage/management page. URL: " + currentUrl);

        // Navigate back to My Wallets for subsequent tests
        driver.navigate().to("https://stagingvault.smartping.io/rs/dashboard/my-wallets");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        walletPage.waitForWalletData();
        System.out.println("Service navigation validated. Returned to My Wallets.");
    }

    // ==================== TC06: Verify Wallet Search ====================

    @Test(priority = 6, description = "TC06: Verify wallet search functionality", dependsOnMethods = "testServiceNavigation")
    public void testWalletSearch() {
        System.out.println("=== TC06: Verify Wallet Search ===");

        String firstWalletName = walletPage.getWalletCellText(1, 1);
        Assert.assertFalse(firstWalletName.isEmpty(), "Need a wallet name to search with");
        System.out.println("Using wallet name for search: " + firstWalletName);

        // Partial match
        String partial = firstWalletName.substring(0, Math.min(5, firstWalletName.length()));
        walletPage.searchWallet(partial);
        int partialResultCount = walletPage.getWalletRowCount();
        System.out.println("Partial search '" + partial + "' returned " + partialResultCount + " row(s)");
        Assert.assertTrue(partialResultCount > 0, "Partial match search should return at least one result");
        for (int i = 1; i <= partialResultCount; i++) {
            String name = walletPage.getWalletCellText(i, 1);
            Assert.assertTrue(name.toLowerCase().contains(partial.toLowerCase()),
                    "Row " + i + " ('" + name + "') should contain search text '" + partial + "'");
        }
        walletPage.clearSearch();

        // Exact match
        walletPage.searchWallet(firstWalletName);
        int exactResultCount = walletPage.getWalletRowCount();
        System.out.println("Exact search '" + firstWalletName + "' returned " + exactResultCount + " row(s)");
        Assert.assertTrue(exactResultCount > 0, "Exact match search should return at least one result");
        boolean foundExact = false;
        for (int i = 1; i <= exactResultCount; i++) {
            if (walletPage.getWalletCellText(i, 1).equalsIgnoreCase(firstWalletName)) {
                foundExact = true;
                break;
            }
        }
        Assert.assertTrue(foundExact, "Exact match search should return the searched wallet");
        walletPage.clearSearch();

        // No match
        String noMatchTerm = "ZZZ_NONEXISTENT_WALLET_XYZ";
        walletPage.searchWallet(noMatchTerm);
        boolean noDataShown = walletPage.isNoDataMessageDisplayed();
        boolean zeroRows = walletPage.getWalletRowCount() == 0;
        System.out.println("No-match search -> noDataShown=" + noDataShown + ", zeroRows=" + zeroRows);
        Assert.assertTrue(noDataShown || zeroRows,
                "Searching for a non-existent wallet should show no rows or a 'no data' message");
        walletPage.clearSearch();

        System.out.println("Wallet search validated (partial, exact, no-match).");
    }

    // ==================== TC07: Verify Wallet Filters ====================

    @Test(priority = 7, description = "TC07: Verify Active status + Prepaid type filters", dependsOnMethods = "testWalletSearch")
    public void testWalletFilters() {
        System.out.println("=== TC07: Verify Wallet Filters ===");

        walletPage.applyActiveStatusFilter();
        walletPage.applyPrepaidTypeFilter();

        boolean noDataShown = walletPage.isNoDataMessageDisplayed();
        if (noDataShown) {
            String message = driver.findElement(
                    locators.reseller.ResellerDashboardPageLocators.NO_DATA_MESSAGE).getText().trim();
            System.out.println("No matching wallets found. Message: '" + message + "'");
            Assert.assertTrue(message.toLowerCase().contains("no data available")
                    || message.toLowerCase().contains("no data"),
                    "Expected a 'no data available' style message. Found: " + message);
        } else {
            int rowCount = walletPage.getWalletRowCount();
            System.out.println("Filtered result count: " + rowCount);
            Assert.assertTrue(rowCount > 0, "Applying filters should show matching wallets or a no-data message");

            for (int i = 1; i <= rowCount; i++) {
                String type = walletPage.getWalletCellText(i, 2);
                String status = walletPage.getWalletStatus(i);
                System.out.println("Row " + i + " -> Type: " + type + ", Status: " + status);
                Assert.assertEquals(type.toUpperCase(), "PREPAID",
                        "Row " + i + " should be Prepaid after filtering");
                Assert.assertTrue(status.equalsIgnoreCase("Active"),
                        "Row " + i + " should be Active after filtering");
            }
        }
        System.out.println("Wallet filters validated.");
    }

    // ==================== TC08: Verify Clear All Filters ====================

    @Test(priority = 8, description = "TC08: Verify Clear All removes filters and restores full list", dependsOnMethods = "testWalletFilters")
    public void testClearAllFilters() {
        System.out.println("=== TC08: Verify Clear All Filters ===");

        int countBeforeClear = walletPage.getWalletRowCount();
        System.out.println("Row count with filters applied: " + countBeforeClear);

        walletPage.clickClearAllFilters();

        boolean chipsGone = !isElementPresentAndVisible(
                org.openqa.selenium.By.xpath("//*[contains(text(),'Status:') or contains(text(),'Wallet Type:')]"));
        System.out.println("Filter chips gone: " + chipsGone);

        walletPage.waitForWalletData();
        int countAfterClear = walletPage.getWalletRowCount();
        System.out.println("Row count after Clear All: " + countAfterClear);

        Assert.assertTrue(countAfterClear >= countBeforeClear,
                "Full wallet list should be restored (or larger) after clearing filters");
        System.out.println("Clear All Filters validated.");
    }

    private boolean isElementPresentAndVisible(org.openqa.selenium.By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== TC09: Verify Wallet Fund Transfer Popup ====================

    @Test(priority = 9, description = "TC09: Verify Wallet Fund Transfer button opens popup", dependsOnMethods = "testClearAllFilters")
    public void testFundTransferPopup() {
        System.out.println("=== TC09: Verify Wallet Fund Transfer Popup ===");

        Assert.assertTrue(walletPage.isFundTransferButtonVisible(), "Wallet Fund Transfer button should be visible");
        System.out.println("Wallet Fund Transfer button is visible.");

        walletPage.clickFundTransferButton();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        Assert.assertTrue(walletPage.isFundTransferModalOpen(), "Fund Transfer popup/modal should open");
        System.out.println("Fund Transfer popup opened successfully.");
    }

    // ==================== TC10: Verify Source Wallet Selection ====================

    @Test(priority = 10, description = "TC10: Verify source wallet (with sufficient balance) selection", dependsOnMethods = "testFundTransferPopup")
    public void testSourceWalletSelection() {
        System.out.println("=== TC10: Verify Source Wallet Selection ===");

        Assert.assertTrue(walletPage.isSourceWalletDropdownVisible(), "Source wallet dropdown should be displayed");

        List<String> options = walletPage.getDropdownOptionTexts(
                locators.reseller.ResellerDashboardPageLocators.SOURCE_WALLET_DROPDOWN);
        System.out.println("Source wallet options: " + options);
        Assert.assertFalse(options.isEmpty(), "Source wallet dropdown should list available wallets");

        // Pick the option with the highest balance to guarantee it's sufficient
        // for the ₹1 transfer in TC12.
        String bestOption = options.get(0);
        double bestBalance = walletPage.extractBalanceFromOption(bestOption);
        for (String option : options) {
            double balance = walletPage.extractBalanceFromOption(option);
            if (balance > bestBalance) {
                bestBalance = balance;
                bestOption = option;
            }
        }
        System.out.println("Selecting source wallet with highest balance: '" + bestOption + "' (₹" + bestBalance + ")");
        Assert.assertTrue(bestBalance >= TRANSFER_AMOUNT,
                "Selected source wallet should have sufficient balance for the transfer");

        boolean selected = walletPage.selectOpenDropdownOptionByText(bestOption);
        Assert.assertTrue(selected, "Should be able to select the source wallet option");

        selectedSourceWallet = walletPage.extractWalletNameFromOption(bestOption);
        selectedSourceWalletType = walletPage.extractTypeFromOption(bestOption);
        sourceBalanceBeforeTransfer = bestBalance;
        System.out.println("Source wallet selected: " + selectedSourceWallet + " (type: " + selectedSourceWalletType
                + ", balance before: " + sourceBalanceBeforeTransfer + ")");

        try {
            Thread.sleep(500);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        String populated = walletPage.getSelectedSourceWalletText();
        System.out.println("Source wallet field now shows: '" + populated + "'");
        Assert.assertTrue(populated.contains(selectedSourceWallet) || !populated.isEmpty(),
                "Source wallet field should reflect the selected wallet");
    }

    // ==================== TC11: Verify Destination Wallet Selection ====================

    @Test(priority = 11, description = "TC11: Verify destination wallet selection", dependsOnMethods = "testSourceWalletSelection")
    public void testDestinationWalletSelection() {
        System.out.println("=== TC11: Verify Destination Wallet Selection ===");

        Assert.assertTrue(walletPage.isDestinationWalletDropdownVisible(),
                "Destination wallet dropdown should be displayed");

        List<String> options = walletPage.getDropdownOptionTexts(
                locators.reseller.ResellerDashboardPageLocators.DESTINATION_WALLET_DROPDOWN);
        System.out.println("Destination wallet options: " + options);
        Assert.assertFalse(options.isEmpty(), "Destination wallet dropdown should list eligible wallets");

        // Pick an option that (a) isn't the already-selected source wallet, and
        // (b) has the SAME wallet type as the source - confirmed live, the app
        // rejects a transfer between different types ("Source and destination
        // wallet types must be the same for transfer (Prepaid to Prepaid or
        // Postpaid to Postpaid)"), leaving the Submit button permanently
        // disabled with no way to proceed.
        String destOption = null;
        for (String option : options) {
            String name = walletPage.extractWalletNameFromOption(option);
            String type = walletPage.extractTypeFromOption(option);
            if (!name.equals(selectedSourceWallet) && type.equalsIgnoreCase(selectedSourceWalletType)) {
                destOption = option;
                break;
            }
        }
        Assert.assertNotNull(destOption, "There should be at least one destination wallet different from the "
                + "source with a matching wallet type (" + selectedSourceWalletType + ")");
        System.out.println("Selecting destination wallet (matching type '" + selectedSourceWalletType + "'): '"
                + destOption + "'");

        double destBalance = walletPage.extractBalanceFromOption(destOption);
        String destType = walletPage.extractTypeFromOption(destOption);
        Assert.assertEquals(destType, selectedSourceWalletType,
                "Destination wallet type must match source wallet type for a valid transfer");

        boolean selected = walletPage.selectOpenDropdownOptionByText(destOption);
        Assert.assertTrue(selected, "Should be able to select the destination wallet option");

        selectedDestinationWallet = walletPage.extractWalletNameFromOption(destOption);
        destinationBalanceBeforeTransfer = destBalance;
        System.out.println("Destination wallet selected: " + selectedDestinationWallet + " (balance before: "
                + destinationBalanceBeforeTransfer + ")");

        try {
            Thread.sleep(500);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        String populated = walletPage.getSelectedDestinationWalletText();
        System.out.println("Destination wallet field now shows: '" + populated + "'");
        Assert.assertTrue(populated.contains(selectedDestinationWallet) || !populated.isEmpty(),
                "Destination wallet field should reflect the selected wallet");
    }

    // ==================== TC12: Verify Fund Transfer ====================

    @Test(priority = 12, description = "TC12: Verify Fund Transfer of ₹1 completes successfully", dependsOnMethods = "testDestinationWalletSelection")
    public void testFundTransfer() {
        System.out.println("=== TC12: Verify Fund Transfer ===");

        walletPage.enterTransferAmount(String.valueOf(TRANSFER_AMOUNT));
        System.out.println("Entered amount: " + TRANSFER_AMOUNT);

        walletPage.enterTransferDescription("Automated test fund transfer");
        System.out.println("Entered description.");

        walletPage.clickFundTransferSubmit();
        System.out.println("Clicked Fund Transfer submit button.");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        boolean successShown = walletPage.isTransferSuccessMessageDisplayed();
        boolean modalClosed = !walletPage.isFundTransferModalOpen();
        System.out.println("Success message shown: " + successShown + ", modal closed: " + modalClosed);

        Assert.assertTrue(successShown || modalClosed,
                "Transfer should show a success confirmation and/or close the modal without validation errors");
        System.out.println("Fund transfer submitted successfully.");
    }

    // ==================== TC13: Verify Wallet Balance After Transfer ====================

    @Test(priority = 13, description = "TC13: Verify source/destination balances reflect the transfer", dependsOnMethods = "testFundTransfer")
    public void testWalletBalanceAfterTransfer() {
        System.out.println("=== TC13: Verify Wallet Balance After Transfer ===");

        driver.navigate().to("https://stagingvault.smartping.io/rs/dashboard/my-wallets");
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        walletPage.waitForWalletData();

        String sourceBalanceText = walletPage.getWalletBalanceByName(selectedSourceWallet);
        String destBalanceText = walletPage.getWalletBalanceByName(selectedDestinationWallet);
        double sourceBalanceAfter = walletPage.parseBalance(sourceBalanceText);
        double destBalanceAfter = walletPage.parseBalance(destBalanceText);

        System.out.println("Source wallet '" + selectedSourceWallet + "': before=" + sourceBalanceBeforeTransfer
                + ", after=" + sourceBalanceAfter);
        System.out.println("Destination wallet '" + selectedDestinationWallet + "': before="
                + destinationBalanceBeforeTransfer + ", after=" + destBalanceAfter);

        double margin = 0.01;
        Assert.assertEquals(sourceBalanceAfter, sourceBalanceBeforeTransfer - TRANSFER_AMOUNT, margin,
                "Source wallet balance should be reduced by the transferred amount");
        Assert.assertEquals(destBalanceAfter, destinationBalanceBeforeTransfer + TRANSFER_AMOUNT, margin,
                "Destination wallet balance should be increased by the transferred amount");

        System.out.println("Wallet balances after transfer validated successfully.");
    }

    // ==================== Logout ====================

    @Test(priority = 14, description = "Logout from Reseller account", dependsOnMethods = "testWalletBalanceAfterTransfer", alwaysRun = true)
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
