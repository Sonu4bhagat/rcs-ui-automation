package tests.ccs;

import base.BaseTest;
import enums.UserRole;
import helpers.CCSTestHelper;
import locators.ccs.CCSLivePageLocators;
import locators.common.LoginPageLocators;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.ccs.CCSLivePage;
import pages.common.LoginPage;
import pages.enterprise.ServicesPage;
import utils.ConfigReader;
import utils.ExtentReportManager;

import java.net.URI;
import java.util.List;

/**
 * CCS Part A - Enterprise login (CCS account) and SSO into the CCS-Live app.
 * Flow: Vault login -> Services -> CCS -> SSO icon -> new tab -> CCS-Live live dashboard.
 * Valid CCS credentials only - login negatives are covered by LoginTest.
 */
public class CCSEnterpriseSSOTest extends BaseTest {

    private LoginPage loginPage;
    private ServicesPage servicesPage;
    private CCSLivePage ccsLivePage;
    private CCSTestHelper ccsHelper;

    private String vaultWindowHandle;
    private String ccsWindowHandle;
    private String selectedCCSServiceAccount;
    private int selectedCCSServiceRow;

    @BeforeClass
    public void setupPages() {
        initPages();
        retainSession = true; // Keep the Vault session (and the CCS-Live tab) across the tests of this class
        sessionRole = UserRole.ENTERPRISE;
    }

    private void initPages() {
        loginPage = new LoginPage(driver);
        servicesPage = new ServicesPage(driver);
        ccsLivePage = new CCSLivePage(driver);
        ccsHelper = new CCSTestHelper(driver);
    }

    @Override
    protected void loginAgain(LoginPage loginPage) {
        // Session recovery must use the CCS account, not the default Enterprise account
        initPages();
        loginPage.loginWithCCSEnterpriseCredentials();
    }

    // ==================== Security ====================

    // Runs first, before any login, so the browser has no CCS session yet - no cookies need clearing
    @Test(priority = 1, description = "CCS-A-10: Direct CCS-Live URL without authentication redirects to the CCS login page")
    public void testDirectCCSLiveUrlWithoutAuthentication() {
        ccsLivePage.open();
        Assert.assertTrue(ccsLivePage.isCCSLoginPageDisplayed(),
                "Unauthenticated user was not sent to the CCS login page. URL: " + ccsLivePage.getSafeCurrentUrl());
        Assert.assertFalse(ccsLivePage.getSafeCurrentUrl().contains(URI.create(CCSLivePage.getConfiguredUrl()).getPath()),
                "Unauthenticated user stayed on the live dashboard URL");
        Assert.assertTrue(driver.findElements(CCSLivePageLocators.LIVE_CALLS_CARD).isEmpty(),
                "Live dashboard data visible without authentication");
    }

    // ==================== Scenario 1: Enterprise Login ====================

    @Test(priority = 2, description = "CCS-A-01: CCS Enterprise user logs in and lands on the Enterprise dashboard")
    public void testCCSEnterpriseLogin() {
        if (!driver.findElements(LoginPageLocators.ENTERPRISE_DASHBOARD).isEmpty()) {
            // Retry of this test after a failed assertion: still signed in from the previous attempt
            loginPage.logout();
        }
        loginPage.openFreshLoginForm(ConfigReader.get("url"));
        loginPage.loginWithCCSEnterpriseCredentials();

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Post-login URL: " + currentUrl);
        Assert.assertFalse(currentUrl.contains("login"), "User is still on the login page after login");
        Assert.assertTrue(currentUrl.contains("/customer/") && currentUrl.contains("/dashboard"),
                "Unexpected post-login landing page: " + currentUrl);

        // Role: Enterprise sidebar and role badge are shown, Super Admin sidebar is not
        Assert.assertTrue(loginPage.isDashboardLoaded(UserRole.ENTERPRISE), "Enterprise dashboard not loaded");
        Assert.assertTrue(driver.findElements(LoginPageLocators.SUPER_ADMIN_DASHBOARD).isEmpty(),
                "Super Admin navigation must not be shown to an Enterprise user");
        String roleBadge = ccsHelper.getUserRoleBadge();
        System.out.println("Header role badge: " + roleBadge);
        Assert.assertEquals(roleBadge.toLowerCase(), "enterprise", "Logged-in user role is not Enterprise");

        List<String> missingNavItems = ccsHelper.getMissingEnterpriseNavItems();
        Assert.assertTrue(missingNavItems.isEmpty(), "Enterprise navigation items missing: " + missingNavItems);
        ExtentReportManager.logPass("Enterprise dashboard and navigation verified");
    }

    // ==================== Scenario 2: CCS SSO Navigation ====================

    @Test(priority = 3, description = "CCS-A-02: CCS service and an SSO-enabled CCS service account are available", dependsOnMethods = "testCCSEnterpriseLogin")
    public void testCCSServiceAccountSSOOptionVisible() {
        ccsHelper.openCCSServiceAccountList();
        Assert.assertTrue(driver.getCurrentUrl().contains("/services/CCS"),
                "Not on the CCS service account list: " + driver.getCurrentUrl());
        Assert.assertTrue(servicesPage.getRowCount() > 0, "No CCS service accounts listed");

        selectedCCSServiceRow = servicesPage.findFirstNonFailedServiceRow();
        Assert.assertTrue(selectedCCSServiceRow != -1, "No non-failed CCS service account found");
        selectedCCSServiceAccount = servicesPage.getServiceName(selectedCCSServiceRow);
        System.out.println("CCS service account: " + selectedCCSServiceAccount + " (row " + selectedCCSServiceRow + ")");
        Assert.assertFalse(selectedCCSServiceAccount == null || selectedCCSServiceAccount.isEmpty(),
                "CCS service account name is empty");

        // Unlike the generic Services tab test, a missing SSO icon is a failure for the CCS account
        Assert.assertTrue(servicesPage.isSSOIconAvailable(selectedCCSServiceRow),
                "CCS SSO icon is not available for service account " + selectedCCSServiceAccount);
    }

    @Test(priority = 4, description = "CCS-A-03: Clicking the CCS SSO icon opens CCS-Live in a new tab through SSO", dependsOnMethods = "testCCSServiceAccountSSOOptionVisible")
    public void testCCSSSORedirection() {
        vaultWindowHandle = driver.getWindowHandle();
        List<String> newTabs = ccsHelper.clickSSOAndWaitForNewTab(selectedCCSServiceRow);
        Assert.assertEquals(newTabs.size(), 1, "One CCS SSO click should open exactly one tab");

        ccsWindowHandle = newTabs.get(0);
        ccsHelper.switchToTab(ccsWindowHandle);
        Assert.assertTrue(ccsLivePage.waitForLiveDashboard(),
                "SSO did not reach the CCS-Live dashboard. URL: " + ccsLivePage.getSafeCurrentUrl());

        String expectedHost = CCSLivePage.hostOf(CCSLivePage.getConfiguredUrl());
        Assert.assertEquals(CCSLivePage.hostOf(driver.getCurrentUrl()), expectedHost, "SSO opened the wrong application");
        Assert.assertFalse(driver.getCurrentUrl().contains("jwtToken"), "SSO token is still in the dashboard URL");
    }

    // ==================== Scenario 3: CCS-Live Dashboard ====================

    @Test(priority = 5, description = "CCS-A-04: CCS-Live URL, title and key dashboard elements are correct", dependsOnMethods = "testCCSSSORedirection")
    public void testCCSLiveDashboardElements() {
        ccsHelper.switchToTab(ccsWindowHandle);
        Assert.assertEquals(ccsLivePage.getSafeCurrentUrl(), CCSLivePage.getConfiguredUrl(), "CCS-Live URL mismatch");
        Assert.assertEquals(ccsLivePage.getPageTitle(), CCSLivePage.getExpectedTitle(), "CCS-Live page title mismatch");

        Assert.assertTrue(ccsLivePage.isSidebarLogoDisplayed(), "CCS-Live logo not displayed");
        Assert.assertTrue(ccsLivePage.isLiveCallsCardDisplayed(), "Live Calls card not displayed");
        Assert.assertTrue(ccsLivePage.isLoggedInUsersCardDisplayed(), "Logged in Users card not displayed");
        for (String module : CCSLivePageLocators.SIDEBAR_MODULES) {
            Assert.assertTrue(ccsLivePage.isSidebarModuleDisplayed(module), "CCS-Live module missing: " + module);
        }

        // The greeting names the service account the SSO was started for - confirms the right CCS account opened
        String greeting = ccsLivePage.getWelcomeGreeting();
        System.out.println("CCS-Live greeting: " + greeting);
        Assert.assertTrue(greeting.toLowerCase().contains(selectedCCSServiceAccount.toLowerCase()),
                "CCS-Live opened for a different account. Expected: " + selectedCCSServiceAccount + ", Greeting: " + greeting);
    }

    @Test(priority = 6, description = "CCS-A-05: CCS-Live shows no login, 404, 500 or access-denied page after SSO", dependsOnMethods = "testCCSSSORedirection")
    public void testCCSLiveNoErrorOrLoginPage() {
        ccsHelper.switchToTab(ccsWindowHandle);
        Assert.assertNull(ccsLivePage.findErrorPageText(), "Error page shown on CCS-Live");
        Assert.assertTrue(driver.findElements(CCSLivePageLocators.LOGIN_PASSWORD_INPUT).isEmpty(),
                "Unexpected CCS-Live login page after SSO");
    }

    @Test(priority = 7, description = "CCS-A-06: CCS-Live session persists after a page refresh", dependsOnMethods = "testCCSSSORedirection")
    public void testCCSLiveSessionPersistsAfterRefresh() {
        ccsHelper.switchToTab(ccsWindowHandle);
        ccsLivePage.refresh();
        Assert.assertTrue(ccsLivePage.waitForLiveDashboard(),
                "CCS-Live dashboard lost after refresh. URL: " + ccsLivePage.getSafeCurrentUrl());
        Assert.assertNull(ccsLivePage.findErrorPageText(), "Error page shown after refresh");
    }

    @Test(priority = 8, description = "CCS-A-07: Enterprise Vault session stays active after SSO into CCS-Live", dependsOnMethods = "testCCSSSORedirection")
    public void testVaultSessionActiveAfterSSO() {
        driver.switchTo().window(vaultWindowHandle);
        driver.navigate().refresh();
        Assert.assertTrue(loginPage.isDashboardLoaded(UserRole.ENTERPRISE),
                "Enterprise session in Vault was lost after SSO into CCS-Live");
        Assert.assertFalse(driver.getCurrentUrl().contains("login"), "Vault returned to login after SSO");

        // Finish on the CCS-Live dashboard - no logout; the browser is closed by BaseTest after the class
        ccsHelper.switchToTab(ccsWindowHandle);
        Assert.assertTrue(ccsLivePage.waitForLiveDashboard(),
                "CCS-Live dashboard not shown at the end of the flow. URL: " + ccsLivePage.getSafeCurrentUrl());
    }
}
