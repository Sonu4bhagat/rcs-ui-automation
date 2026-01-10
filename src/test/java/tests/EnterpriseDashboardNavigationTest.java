package tests;

import base.BaseTest;
import helpers.DashboardTestHelper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.LoginPage;

public class EnterpriseDashboardNavigationTest extends BaseTest {

    private LoginPage loginPage;
    private DashboardTestHelper dashboardHelper;

    @BeforeClass
    public void setupPages() {
        loginPage = new LoginPage(driver);
        dashboardHelper = new DashboardTestHelper(driver);
    }

    @Test(priority = 1, description = "Login with Enterprise User and Select Max Services Wallet")
    public void testEnterpriseLoginAndWalletSelection() {
        System.out.println("Starting Enterprise Login Test...");
        loginPage.loginWithEnterpriseMaxServices();
        System.out.println("Enterprise Dashboard loaded successfully.");
    }

    @Test(priority = 2, description = "Test RCS service navigation", dependsOnMethods = "testEnterpriseLoginAndWalletSelection", alwaysRun = true)
    public void testRCSService() throws InterruptedException {
        dashboardHelper.testServiceNavigation("RCS");
    }

    @Test(priority = 3, description = "Test CCS service navigation", dependsOnMethods = "testRCSService", alwaysRun = true)
    public void testCCSService() throws InterruptedException {
        dashboardHelper.testServiceNavigation("CCS");
    }

    @Test(priority = 4, description = "Test SMS service navigation", dependsOnMethods = "testCCSService", alwaysRun = true)
    public void testSMSService() throws InterruptedException {
        dashboardHelper.testServiceNavigation("SMS");
    }

    @Test(priority = 5, description = "Test IVR service navigation", dependsOnMethods = "testSMSService", alwaysRun = true)
    public void testIVRService() throws InterruptedException {
        dashboardHelper.testServiceNavigation("IVR");
    }

    @Test(priority = 6, description = "Test OBD service navigation", dependsOnMethods = "testIVRService", alwaysRun = true)
    public void testOBDService() throws InterruptedException {
        dashboardHelper.testServiceNavigation("OBD");
    }

    @Test(priority = 7, description = "Test WABA service navigation", dependsOnMethods = "testOBDService", alwaysRun = true)
    public void testWABAService() throws InterruptedException {
        dashboardHelper.testServiceNavigation("WABA");
    }

    @Test(priority = 8, description = "Test Live Agent service navigation", dependsOnMethods = "testWABAService", alwaysRun = true)
    public void testLiveAgentService() throws InterruptedException {
        dashboardHelper.testServiceNavigation("Live Agent");
    }

    @Test(priority = 9, description = "Logout from Enterprise", dependsOnMethods = "testLiveAgentService", alwaysRun = true)
    public void testLogout() {
        loginPage.logout();
        System.out.println("Successfully logged out from Enterprise account.");
    }
}
