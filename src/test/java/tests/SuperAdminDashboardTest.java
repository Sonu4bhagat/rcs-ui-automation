package tests;

import base.BaseTest;
import enums.UserRole;
import helpers.DashboardTestHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.LoginPage;

public class SuperAdminDashboardTest extends BaseTest {

    private DashboardTestHelper dashboardHelper;

    @BeforeClass
    public void configureSession() {
        // Retain session for sequential execution
        retainSession = true;
    }

    @Test(priority = 1, description = "Login as Super Admin and verify dashboard")
    public void testLoginAndDashboard() {
        try {
            LoginPage loginPage = new LoginPage(driver);
            DashboardPage dashboardPage = new DashboardPage(driver);
            dashboardHelper = new DashboardTestHelper(driver);

            System.out.println("========================================");
            System.out.println("DASHBOARD TEST SUITE");
            System.out.println("========================================\n");

            System.out.println("Step 1: Logging in as SuperAdmin...");
            loginPage.loginWithSuperAdminCredentials();
            Thread.sleep(2000);

            System.out.println("Step 2: Verifying dashboard loaded...");
            boolean dashboardLoaded = dashboardPage.isDashboardLoaded(UserRole.SUPERADMIN);
            Assert.assertTrue(dashboardLoaded, "SuperAdmin dashboard failed to load");

            System.out.println("✓ Dashboard loaded successfully\n");

        } catch (Exception e) {
            System.err.println("❌ FAILURE: Login and dashboard verification failed - " + e.getMessage());
            Assert.fail("Login and dashboard verification failed - " + e.getMessage());
        }
    }

    @Test(priority = 2, description = "Test RCS service navigation", dependsOnMethods = "testLoginAndDashboard", alwaysRun = true)
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

    @Test(priority = 9, description = "Logout from Super Admin", dependsOnMethods = "testLiveAgentService", alwaysRun = true)
    public void testLogout() {
        try {
            System.out.println("\n========================================");
            System.out.println("LOGGING OUT");
            System.out.println("========================================");

            LoginPage loginPage = new LoginPage(driver);
            loginPage.logout();

            System.out.println("✓ Logout successful\n");
            System.out.println("========================================");
            System.out.println("DASHBOARD TEST SUITE - COMPLETED");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("❌ FAILURE: Logout error - " + e.getMessage());
            Assert.fail("Logout error - " + e.getMessage());
        }
    }
}
