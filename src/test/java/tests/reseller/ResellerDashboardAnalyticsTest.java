package tests.reseller;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.common.LoginPage;
import pages.reseller.ResellerDashboardAnalyticsPage;

import java.util.List;

/**
 * Test class for Reseller Dashboard's "Analytics" tab.
 * Tests tab selection, service sub-tab navigation, the customer filter
 * (including the "no services available" case), clearing the customer
 * filter, and the calendar/date-range filter.
 */
public class ResellerDashboardAnalyticsTest extends BaseTest {

    private LoginPage loginPage;
    private ResellerDashboardAnalyticsPage analyticsPage;
    private static final String ANALYTICS_SMS_URL = "https://stagingvault.smartping.io/rs/dashboard/analytics/sms";

    @BeforeClass
    public void setupPages() {
        loginPage = new LoginPage(driver);
        analyticsPage = new ResellerDashboardAnalyticsPage(driver);
        retainSession = true;
    }

    // ==================== TC01: Verify Analytics Tab Clickable ====================

    @Test(priority = 1, description = "TC01: Verify Analytics tab is clickable and loads the Performance Overview")
    public void testAnalyticsTabClickable() throws InterruptedException {
        System.out.println("=== TC01: Verify Analytics Tab Clickable ===");

        loginPage.loginWithResellerCredentials();
        System.out.println("Logged in as Reseller.");

        analyticsPage.clickAnalyticsTab();
        Thread.sleep(2500);

        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard/analytics"),
                "URL should navigate to the Analytics tab route. URL: " + driver.getCurrentUrl());
        Assert.assertTrue(analyticsPage.isElementVisible(
                locators.reseller.ResellerDashboardAnalyticsPageLocators.ANALYTICS_HEADER),
                "Performance Overview header should be displayed");
        System.out.println("Analytics tab clicked and Performance Overview loaded.");
    }

    // ==================== TC02: Verify All Service Sub-tabs ====================

    @Test(priority = 2, description = "TC02: Verify navigation across all service tabs shows no error", dependsOnMethods = "testAnalyticsTabClickable")
    public void testAllServiceTabsNoError() throws InterruptedException {
        System.out.println("=== TC02: Verify All Service Sub-tabs ===");

        Assert.assertTrue(analyticsPage.hasAllServiceTabs(),
                "All service tabs (SMS/IVR/OBD/CCS/WABA/RCS) should be available by default. Found: "
                        + analyticsPage.getServiceTabTexts());

        for (String service : locators.reseller.ResellerDashboardAnalyticsPageLocators.ALL_SERVICE_NAMES) {
            analyticsPage.clickServiceTab(service);
            Thread.sleep(1800);

            boolean tabActive = analyticsPage.isServiceTabActive(service);
            boolean errorShown = analyticsPage.isErrorMessageDisplayed();
            System.out.println("Service=" + service + " tabActive=" + tabActive + " errorShown=" + errorShown);

            Assert.assertTrue(tabActive, "Clicking the '" + service + "' tab should mark it as selected/active");
            Assert.assertFalse(errorShown, "No error message should be shown on the '" + service + "' service tab");
        }
        System.out.println("All service tabs navigated without errors.");
    }

    // ==================== TC03: Verify Customer Filter ====================

    @Test(priority = 3, description = "TC03: Verify Customer filter shows matching services, or the access-denied message when none exist", dependsOnMethods = "testAllServiceTabsNoError")
    public void testCustomerFilter() throws InterruptedException {
        System.out.println("=== TC03: Verify Customer Filter ===");

        driver.navigate().to(ANALYTICS_SMS_URL);
        Thread.sleep(2000);

        Assert.assertTrue(analyticsPage.isCustomerFilterVisible(), "Select Customer filter should be visible");

        List<String> options = analyticsPage.getCustomerFilterOptionTexts();
        System.out.println("Customer options: " + options);
        Assert.assertFalse(options.isEmpty(), "Customer filter should list at least one organisation");

        // Pick a customer known to have at least one service, and verify the
        // service tab bar narrows down accordingly (confirmed live: selecting
        // a customer filters tabs to only that customer's provisioned
        // services, e.g. 'acpltest783' -> only the OBD tab remains).
        String customerWithService = "acpltest783";
        Assert.assertTrue(options.contains(customerWithService),
                "Expected test customer '" + customerWithService + "' not found in options: " + options);

        boolean selected = analyticsPage.selectOpenCustomerOptionByText(customerWithService);
        Assert.assertTrue(selected, "Should be able to select customer '" + customerWithService + "'");
        Thread.sleep(2000);

        List<String> tabsAfterSelect = analyticsPage.getServiceTabTexts();
        System.out.println("Service tabs after selecting '" + customerWithService + "': " + tabsAfterSelect);
        boolean hasAnyService = tabsAfterSelect.stream().anyMatch(t ->
                java.util.Arrays.asList(locators.reseller.ResellerDashboardAnalyticsPageLocators.ALL_SERVICE_NAMES).contains(t));
        Assert.assertTrue(hasAnyService,
                "Customer '" + customerWithService + "' should have at least one service tab available");
        Assert.assertFalse(analyticsPage.isAccessNotAllowedMessageDisplayed(),
                "Access-denied message should NOT show when the customer has available services");

        analyticsPage.clearCustomerFilter();
        Thread.sleep(1500);

        // Pick a customer confirmed live to have zero provisioned services.
        String customerWithNoService = "testorg76";
        analyticsPage.getCustomerFilterOptionTexts(); // re-opens the dropdown
        boolean selectedNone = analyticsPage.selectOpenCustomerOptionByText(customerWithNoService);
        Assert.assertTrue(selectedNone, "Should be able to select customer '" + customerWithNoService + "'");
        Thread.sleep(2000);

        List<String> tabsForNoService = analyticsPage.getServiceTabTexts();
        System.out.println("Service tabs after selecting '" + customerWithNoService + "': " + tabsForNoService);
        boolean anyServiceForNoServiceCustomer = tabsForNoService.stream().anyMatch(t ->
                java.util.Arrays.asList(locators.reseller.ResellerDashboardAnalyticsPageLocators.ALL_SERVICE_NAMES).contains(t));
        Assert.assertFalse(anyServiceForNoServiceCustomer,
                "Customer '" + customerWithNoService + "' should have no service tabs available");
        Assert.assertTrue(analyticsPage.isAccessNotAllowedMessageDisplayed(),
                "'Access to Service Tabs is not allowed' message should be shown when no services are available");
        System.out.println("Customer filter validated for both with-service and no-service cases.");
    }

    // ==================== TC04: Verify Cancel Customer Filter ====================

    @Test(priority = 4, description = "TC04: Verify cancelling the selected customer restores all previous service tabs", dependsOnMethods = "testCustomerFilter")
    public void testCancelCustomerFilter() throws InterruptedException {
        System.out.println("=== TC04: Verify Cancel Customer Filter ===");

        // TC03 leaves the zero-service customer selected - clear it here.
        analyticsPage.clearCustomerFilter();
        Thread.sleep(2000);

        String selectedText = analyticsPage.getSelectedCustomerText();
        System.out.println("Selected customer field after clearing: '" + selectedText + "'");
        Assert.assertTrue(selectedText.isEmpty() || selectedText.equalsIgnoreCase("-- Select --"),
                "Customer filter field should be empty/reset after clearing");

        Assert.assertTrue(analyticsPage.hasAllServiceTabs(),
                "All service tabs (SMS/IVR/OBD/CCS/WABA/RCS) should be restored after clearing the customer filter");
        System.out.println("Cancel customer filter validated - full service tab list restored.");
    }

    // ==================== TC05: Verify Calendar Filter ====================

    @Test(priority = 5, description = "TC05: Verify applying the calendar/date-range filter", dependsOnMethods = "testCancelCustomerFilter")
    public void testCalendarFilter() throws InterruptedException {
        System.out.println("=== TC05: Verify Calendar Filter ===");

        String labelBefore = analyticsPage.getDateRangeLabel();
        System.out.println("Date range before: " + labelBefore);
        Assert.assertFalse(labelBefore.isEmpty(), "Date range label should be populated by default");

        analyticsPage.openCalendarFilter();
        Thread.sleep(1000);

        boolean presetSelected = analyticsPage.selectCalendarPreset("Last 30 Days");
        Assert.assertTrue(presetSelected, "'Last 30 Days' preset option should be selectable");
        Thread.sleep(1500);

        String labelAfter = analyticsPage.getDateRangeLabel();
        System.out.println("Date range after applying 'Last 30 Days': " + labelAfter);
        Assert.assertNotEquals(labelAfter, labelBefore,
                "Date range label should change after applying a different preset");
        Assert.assertFalse(analyticsPage.isErrorMessageDisplayed(),
                "No error message should be shown after applying the calendar filter");
        System.out.println("Calendar filter validated.");
    }

    // ==================== Logout ====================

    @Test(priority = 99, description = "Logout from Reseller account", dependsOnMethods = "testCalendarFilter", alwaysRun = true)
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
