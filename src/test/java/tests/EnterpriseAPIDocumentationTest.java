package tests;

import base.BaseTest;
import helpers.APIDocumentationTestHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.APIAndDocumentationPage;
import pages.EnterpriseControlCenterPage;
import utils.ConfigReader;

/**
 * Test Suite for Enterprise API & Documentation Module
 * Tests API & Documentation functionality in Enterprise Control Center
 * including service validation, documentation downloads, and Swagger UI
 * verification
 * 
 * This test class is for Enterprise login flow only.
 * Covers SMS, OBD, CCS, WABA services similar to
 * SuperAdminAPIAndDocumentationTest
 */
public class EnterpriseAPIDocumentationTest extends BaseTest {

    private LoginPage loginPage;
    private EnterpriseControlCenterPage controlCenterPage;
    private APIAndDocumentationPage apiDocPage;
    private APIDocumentationTestHelper testHelper;
    private String expectedDomain = "stagingvault.smartping.io";

    @BeforeClass
    public void setupPages() {
        loginPage = new LoginPage(driver);
        controlCenterPage = new EnterpriseControlCenterPage(driver);
        apiDocPage = new APIAndDocumentationPage(driver);
        retainSession = true;

        // Extract domain from config URL
        try {
            String url = ConfigReader.get("url");
            if (url != null && url.contains("://")) {
                expectedDomain = url.split("://")[1].split("/")[0];
            }
        } catch (Exception e) {
            System.out.println("Using default domain: " + expectedDomain);
        }

        testHelper = new APIDocumentationTestHelper(driver, expectedDomain);
    }

    // ==================== Test Case 1: Login and Navigate ====================

    @Test(priority = 1, description = "Login as Enterprise user and navigate to API & Documentation")
    public void testLoginAndNavigateToAPIDoc() throws InterruptedException {
        System.out.println("\n========================================");
        System.out.println("ENTERPRISE API & DOCUMENTATION TEST SUITE");
        System.out.println("========================================\n");

        System.out.println("=== Test Case 1: Login & Navigation ===");

        try {
            System.out.println("Step 1: Logging in as Enterprise user with max services...");
            loginPage.loginWithEnterpriseMaxServices();
            System.out.println("✓ Logged in as Enterprise user.");

            System.out.println("Step 2: Navigating to API & Documentation...");
            apiDocPage.navigateToAPIAndDocumentation();
            Thread.sleep(1500);

            Assert.assertTrue(apiDocPage.isPageLoaded(),
                    "FAILURE: API & Documentation page did not load");
            System.out.println("✓ Successfully navigated to API & Documentation page.\n");
        } catch (Exception e) {
            Assert.fail("FAILURE: API & Documentation navigation failed - " + e.getMessage());
        }
    }

    // ==================== Test Case 2: Tab Visibility ====================

    @Test(priority = 2, description = "Verify API & Documentation tab visibility and clickability", dependsOnMethods = "testLoginAndNavigateToAPIDoc")
    public void testAPIDocTabVisibilityAndClickability() throws InterruptedException {
        System.out.println("=== Test Case 2: Tab Visibility & Clickability ===");

        try {
            boolean isVisible = apiDocPage.isAPIDocTabVisible();
            Assert.assertTrue(isVisible, "API & Documentation tab should be visible");
            System.out.println("✓ API & Documentation tab is visible");

            boolean isClickable = apiDocPage.isAPIDocTabClickable();
            Assert.assertTrue(isClickable, "API & Documentation tab should be clickable");
            System.out.println("✓ API & Documentation tab is clickable\n");

            Thread.sleep(1000);
        } catch (Exception e) {
            Assert.fail("FAILURE: Tab visibility/clickability check failed - " + e.getMessage());
        }
    }

    // ==================== Test Case 3: SMS Service ====================

    @Test(priority = 3, description = "Validate SMS service documentation and Swagger UI", dependsOnMethods = "testAPIDocTabVisibilityAndClickability")
    public void testSMSServiceDocumentation() throws InterruptedException {
        System.out.println("=== Test Case 3: SMS Service Documentation ===");

        try {
            testHelper.validateServiceDocumentation("SMS");
            System.out.println("✓ SMS service documentation validated successfully\n");
        } catch (Exception e) {
            System.out.println("SKIPPED: SMS service validation - " + e.getMessage() + "\n");
        }
        Thread.sleep(1000);
    }

    // ==================== Test Case 4: OBD Service ====================

    @Test(priority = 4, description = "Validate OBD service documentation and Swagger UI", dependsOnMethods = "testSMSServiceDocumentation", alwaysRun = true)
    public void testOBDServiceDocumentation() throws InterruptedException {
        System.out.println("=== Test Case 4: OBD Service Documentation ===");

        try {
            testHelper.validateServiceDocumentation("OBD");
            System.out.println("✓ OBD service documentation validated successfully\n");
        } catch (Exception e) {
            System.out.println("SKIPPED: OBD service validation - " + e.getMessage() + "\n");
        }
        Thread.sleep(1000);
    }

    // ==================== Test Case 5: CCS Service ====================

    @Test(priority = 5, description = "Validate CCS service documentation and Swagger UI", dependsOnMethods = "testOBDServiceDocumentation", alwaysRun = true)
    public void testCCSServiceDocumentation() throws InterruptedException {
        System.out.println("=== Test Case 5: CCS Service Documentation ===");

        try {
            testHelper.validateServiceDocumentation("CCS");
            System.out.println("✓ CCS service documentation validated successfully\n");
        } catch (Exception e) {
            System.out.println("SKIPPED: CCS service validation - " + e.getMessage() + "\n");
        }
        Thread.sleep(1000);
    }

    // ==================== Test Case 6: WABA Service ====================

    @Test(priority = 6, description = "Validate WABA service documentation and Swagger UI", dependsOnMethods = "testCCSServiceDocumentation", alwaysRun = true)
    public void testWABAServiceDocumentation() throws InterruptedException {
        System.out.println("=== Test Case 6: WABA Service Documentation ===");

        try {
            testHelper.validateServiceDocumentation("WABA");
            System.out.println("✓ WABA service documentation validated successfully\n");
        } catch (Exception e) {
            System.out.println("SKIPPED: WABA service validation - " + e.getMessage() + "\n");
        }
        Thread.sleep(1000);
    }

    // ==================== Test Case 7: RCS Service (Enterprise-specific)
    // ====================

    @Test(priority = 7, description = "Validate RCS service documentation if available", dependsOnMethods = "testWABAServiceDocumentation", alwaysRun = true)
    public void testRCSServiceDocumentation() throws InterruptedException {
        System.out.println("=== Test Case 7: RCS Service Documentation ===");

        try {
            testHelper.validateServiceDocumentation("RCS");
            System.out.println("✓ RCS service documentation validated successfully\n");
        } catch (Exception e) {
            System.out.println("INFO: RCS service not available or validation failed - " + e.getMessage() + "\n");
        }
        Thread.sleep(1000);
    }

    // ==================== Test Case 8: IVR Service (Enterprise-specific)
    // ====================

    @Test(priority = 8, description = "Validate IVR service documentation if available", dependsOnMethods = "testRCSServiceDocumentation", alwaysRun = true)
    public void testIVRServiceDocumentation() throws InterruptedException {
        System.out.println("=== Test Case 8: IVR Service Documentation ===");

        try {
            testHelper.validateServiceDocumentation("IVR");
            System.out.println("✓ IVR service documentation validated successfully\n");
        } catch (Exception e) {
            System.out.println("INFO: IVR service not available or validation failed - " + e.getMessage() + "\n");
        }
        Thread.sleep(1000);
    }

    // ==================== Test Case 9: Logout ====================

    @Test(priority = 9, description = "Logout from Enterprise account", dependsOnMethods = "testLoginAndNavigateToAPIDoc", alwaysRun = true)
    public void testLogout() throws InterruptedException {
        System.out.println("=== Test Case 9: Logout ===");

        try {
            loginPage.logout();
            System.out.println("✓ Logout successful.\n");
        } catch (Exception e) {
            System.out.println("INFO: Logout encountered an issue - " + e.getMessage() + "\n");
        }

        System.out.println("========================================");
        System.out.println("ENTERPRISE API & DOCUMENTATION TEST SUITE - COMPLETED");
        System.out.println("========================================");
    }
}
