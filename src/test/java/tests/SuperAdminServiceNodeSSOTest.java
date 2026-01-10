package tests;

import base.BaseTest;
import helpers.SSOTestHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ServiceNodeSSOPage;

/**
 * Test Suite for Service Node SSO Module
 * Tests SSO login for each service with all available roles
 */
public class SuperAdminServiceNodeSSOTest extends BaseTest {

    private ServiceNodeSSOPage ssoPage;
    private SSOTestHelper ssoHelper;

    @BeforeClass
    public void configureSession() {
        retainSession = true;
    }

    @Test(priority = 1, description = "Login as Super Admin and navigate to Service Node SSO")
    public void testLoginAndNavigateToSSO() throws InterruptedException {
        try {
            LoginPage loginPage = new LoginPage(driver);
            ssoPage = new ServiceNodeSSOPage(driver);
            ssoHelper = new SSOTestHelper(driver);

            System.out.println("========================================");
            System.out.println("SERVICE NODE SSO TEST SUITE");
            System.out.println("========================================\n");

            System.out.println("Step 1: Logging in as Super Admin...");
            loginPage.loginWithSuperAdminCredentials();
            Thread.sleep(2000);

            System.out.println("Step 2: Navigating to Service Node SSO...");
            ssoPage.navigateToServiceNodeSSO();
            Thread.sleep(1500);

            Assert.assertTrue(ssoPage.isPageLoaded(),
                    "❌ FAILURE: Service Node SSO page failed to load - Check navigation or page locators");
            System.out.println("✓ Successfully navigated to Service Node SSO page\n");
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: SSO Login/Navigation - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: SSO Login/Navigation - Unexpected error: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 2, description = "Test SSO login for SMS service with all roles", dependsOnMethods = "testLoginAndNavigateToSSO", alwaysRun = true)
    public void testSMSServiceSSO() throws InterruptedException {
        try {
            ssoHelper.testServiceSSO("SMS");
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: SMS Service SSO - " + e.getMessage());
            throw new AssertionError("❌ FAILURE: SMS service SSO failed - " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: SMS Service SSO - Error: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 3, description = "Test SSO login for RCS service with all roles", dependsOnMethods = "testSMSServiceSSO", alwaysRun = true)
    public void testRCSServiceSSO() throws InterruptedException {
        try {
            ssoHelper.testServiceSSO("RCS");
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: RCS Service SSO - " + e.getMessage());
            throw new AssertionError("❌ FAILURE: RCS service SSO failed - " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: RCS Service SSO - Error: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4, description = "Test SSO login for CCS service with all roles", dependsOnMethods = "testRCSServiceSSO", alwaysRun = true)
    public void testCCSServiceSSO() throws InterruptedException {
        try {
            ssoHelper.testServiceSSO("CCS");
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: CCS Service SSO - " + e.getMessage());
            throw new AssertionError("❌ FAILURE: CCS service SSO failed - " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: CCS Service SSO - Error: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 5, description = "Test SSO login for OBD service with all roles", dependsOnMethods = "testCCSServiceSSO", alwaysRun = true)
    public void testOBDServiceSSO() throws InterruptedException {
        try {
            ssoHelper.testServiceSSO("OBD");
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: OBD Service SSO - " + e.getMessage());
            throw new AssertionError("❌ FAILURE: OBD service SSO failed - " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: OBD Service SSO - Error: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 6, description = "Test SSO login for IVR service with all roles", dependsOnMethods = "testOBDServiceSSO", alwaysRun = true)
    public void testIVRServiceSSO() throws InterruptedException {
        try {
            ssoHelper.testServiceSSO("IVR");
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: IVR Service SSO - " + e.getMessage());
            throw new AssertionError("❌ FAILURE: IVR service SSO failed - " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: IVR Service SSO - Error: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 7, description = "Test SSO login for WABA service with all roles", dependsOnMethods = "testIVRServiceSSO", alwaysRun = true)
    public void testWABAServiceSSO() throws InterruptedException {
        try {
            ssoHelper.testServiceSSO("WABA");
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: WABA Service SSO - " + e.getMessage());
            throw new AssertionError("❌ FAILURE: WABA service SSO failed - " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: WABA Service SSO - Error: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 8, description = "Test SSO login for Live Agent service with all roles", dependsOnMethods = "testWABAServiceSSO", alwaysRun = true)
    public void testLiveAgentServiceSSO() throws InterruptedException {
        try {
            ssoHelper.testServiceSSO("Live Agent");
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: Live Agent Service SSO - " + e.getMessage());
            throw new AssertionError("❌ FAILURE: Live Agent service SSO failed - " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: Live Agent Service SSO - Error: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 9, description = "Logout from Super Admin", dependsOnMethods = "testLiveAgentServiceSSO", alwaysRun = true)
    public void testLogout() throws InterruptedException {
        try {
            LoginPage loginPage = new LoginPage(driver);

            System.out.println("\n========================================");
            System.out.println("LOGGING OUT");
            System.out.println("========================================");

            // Ensure we're back on SSO page or main application
            try {
                if (!driver.getCurrentUrl().contains("service-nodes-sso")) {
                    driver.navigate().back();
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                // Continue with logout
            }

            loginPage.logout();
            System.out.println("✓ Logout successful\n");

            System.out.println("========================================");
            System.out.println("SSO TEST SUITE - COMPLETED");
            System.out.println("========================================");
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: SSO Logout - Error during logout: " + e.getMessage());
            throw e;
        }
    }
}
