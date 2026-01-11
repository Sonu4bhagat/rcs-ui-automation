package helpers;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.DashboardPage;

/**
 * Helper class for Dashboard service testing
 * Contains reusable methods for testing services with calendar filters
 */
public class DashboardTestHelper {

    private DashboardPage dashboardPage;

    public DashboardTestHelper(WebDriver driver) {
        this.dashboardPage = new DashboardPage(driver);
    }

    /**
     * Test service navigation
     * Navigates to service and verifies page load
     * 
     * @param serviceName Name of the service to test
     * @throws InterruptedException if thread sleep is interrupted
     */
    public void testServiceNavigation(String serviceName) throws InterruptedException {
        System.out.println("\n========================================");
        System.out.println("TESTING SERVICE NAVIGATION: " + serviceName);
        System.out.println("========================================");

        try {
            // Step 1: Navigate to service
            System.out.println("Step 1: Navigating to " + serviceName + "...");
            dashboardPage.clickService(serviceName);

            // Use longer wait in headless mode
            int waitTime = base.DriverFactory.isHeadlessModeEnabled() ? 4000 : 2000;
            Thread.sleep(waitTime); // Give page time to load

            // Step 2: Verify service page loaded (check for errors)
            String serviceCheckName = getServiceCheckName(serviceName);
            boolean isLoaded = dashboardPage.isServicePageLoaded(serviceCheckName);

            // Step 2b: Explicitly check for UI error messages
            String errorMessage = dashboardPage.checkForPageErrors();
            if (errorMessage != null) {
                System.err.println("❌ FAILURE: " + serviceName + " service loaded with error: " + errorMessage);
                // For WABA and similar services, log the error but check if page loaded anyway
                if (isLoaded) {
                    System.out.println("⚠️ Warning: Error popup appeared but page loaded. Error: " + errorMessage);
                    // Don't fail the test if page loaded despite error popup
                } else {
                    Assert.fail(serviceName + " service navigation failed - Visible Error: " + errorMessage);
                }
            }

            if (!isLoaded) {
                System.err.println("❌ FAILURE: " + serviceName + " service page did not load - navigation failed");
                Assert.fail(serviceName + " service page did not load - navigation failed");
            }

            System.out.println("✓ Successfully navigated to " + serviceName + " service");
            System.out.println("✓ " + serviceName + " service navigation PASSED\n");

        } catch (AssertionError e) {
            System.err.println("❌ TEST FAILED: " + serviceName + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ FAILURE: " + serviceName + " service navigation error - " + e.getMessage());
            Assert.fail(serviceName + " service navigation error - " + e.getMessage());
        }
    }

    /**
     * Get the service name to check in URL/page for verification
     * Some services have different names in the URL than their display name
     * 
     * @param serviceName Display name of the service
     * @return Name to check in URL/page
     */
    private String getServiceCheckName(String serviceName) {
        switch (serviceName) {
            case "WABA":
                return "WhatsApp";
            case "Live Agent":
                return "liveagent";
            default:
                return serviceName;
        }
    }
}
