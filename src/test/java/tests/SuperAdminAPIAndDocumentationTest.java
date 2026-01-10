package tests;

import base.BaseTest;
import helpers.APIDocumentationTestHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.APIAndDocumentationPage;
import utils.ConfigReader;

/**
 * Test Suite for Super Admin API & Documentation Module
 * Tests API & Documentation functionality including service validation,
 * documentation downloads, and Swagger UI verification for SMS, OBD, CCS, and
 * WABA services
 */
public class SuperAdminAPIAndDocumentationTest extends BaseTest {

    private APIAndDocumentationPage apiDocPage;
    private APIDocumentationTestHelper testHelper;
    private String expectedDomain = "stagingvault.smartping.io";

    @BeforeClass
    public void configureSession() {
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
    }

    @Test(priority = 1, description = "Login as Super Admin and navigate to API & Documentation")
    public void testLoginAndNavigateToAPIDoc() throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);
        apiDocPage = new APIAndDocumentationPage(driver);
        testHelper = new APIDocumentationTestHelper(driver, expectedDomain);

        System.out.println("========================================");
        System.out.println("API & DOCUMENTATION TEST SUITE");
        System.out.println("========================================\n");

        System.out.println("Step 1: Logging in as Super Admin...");
        loginPage.loginWithSuperAdminCredentials();
        Thread.sleep(2000);

        System.out.println("Step 2: Navigating to API & Documentation...");
        apiDocPage.navigateToAPIAndDocumentation();
        Thread.sleep(1500);

        Assert.assertTrue(apiDocPage.isPageLoaded(), "API & Documentation page should load successfully");
        System.out.println("✓ Successfully navigated to API & Documentation page\n");
    }

    @Test(priority = 2, description = "Verify API & Documentation tab visibility and clickability", dependsOnMethods = "testLoginAndNavigateToAPIDoc")
    public void testAPIDocTabVisibilityAndClickability() throws InterruptedException {
        System.out.println("\n========================================");
        System.out.println("TEST: API & Documentation Tab Validation");
        System.out.println("========================================");

        boolean isVisible = apiDocPage.isAPIDocTabVisible();
        Assert.assertTrue(isVisible, "API & Documentation tab should be visible");
        System.out.println("✓ API & Documentation tab is visible");

        boolean isClickable = apiDocPage.isAPIDocTabClickable();
        Assert.assertTrue(isClickable, "API & Documentation tab should be clickable");
        System.out.println("✓ API & Documentation tab is clickable\n");

        Thread.sleep(1000);
    }

    @Test(priority = 3, description = "Validate SMS service documentation and Swagger UI", dependsOnMethods = "testAPIDocTabVisibilityAndClickability")
    public void testSMSServiceDocumentation() throws InterruptedException {
        System.out.println("\n========================================");
        System.out.println("TEST: SMS Service Documentation");
        System.out.println("========================================");

        testHelper.validateServiceDocumentation("SMS");

        System.out.println("✓ SMS service documentation validated successfully\n");
        Thread.sleep(1000);
    }

    @Test(priority = 4, description = "Validate OBD service documentation and Swagger UI", dependsOnMethods = "testSMSServiceDocumentation")
    public void testOBDServiceDocumentation() throws InterruptedException {
        System.out.println("\n========================================");
        System.out.println("TEST: OBD Service Documentation");
        System.out.println("========================================");

        testHelper.validateServiceDocumentation("OBD");

        System.out.println("✓ OBD service documentation validated successfully\n");
        Thread.sleep(1000);
    }

    @Test(priority = 5, description = "Validate CCS service documentation and Swagger UI", dependsOnMethods = "testOBDServiceDocumentation")
    public void testCCSServiceDocumentation() throws InterruptedException {
        System.out.println("\n========================================");
        System.out.println("TEST: CCS Service Documentation");
        System.out.println("========================================");

        testHelper.validateServiceDocumentation("CCS");

        System.out.println("✓ CCS service documentation validated successfully\n");
        Thread.sleep(1000);
    }

    @Test(priority = 6, description = "Validate WABA service documentation and Swagger UI", dependsOnMethods = "testCCSServiceDocumentation")
    public void testWABAServiceDocumentation() throws InterruptedException {
        System.out.println("\n========================================");
        System.out.println("TEST: WABA Service Documentation");
        System.out.println("========================================");

        testHelper.validateServiceDocumentation("WABA");

        System.out.println("✓ WABA service documentation validated successfully\n");
        Thread.sleep(1000);
    }

    @Test(priority = 7, description = "Logout from Super Admin", dependsOnMethods = "testWABAServiceDocumentation", alwaysRun = true)
    public void testLogout() throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);

        System.out.println("\n========================================");
        System.out.println("LOGGING OUT");
        System.out.println("========================================");

        loginPage.logout();
        System.out.println("✓ Logout successful\n");

        System.out.println("========================================");
        System.out.println("API & DOCUMENTATION TEST SUITE - COMPLETED");
        System.out.println("========================================");
    }

}
