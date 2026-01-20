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
    private WebDriver driver;

    public DashboardTestHelper(WebDriver driver) {
        this.driver = driver;
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
            // FIRST: Ensure we're on the dashboard
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Current URL before navigation: " + currentUrl);

            if (currentUrl.contains("/select-wallet") || currentUrl.contains("/login")) {
                System.out.println("⚠️ Not on dashboard, navigating to dashboard first...");
                String baseUrl = currentUrl.split("/select-wallet")[0];
                if (currentUrl.contains("/login")) {
                    baseUrl = currentUrl.split("/login")[0];
                }
                String dashboardUrl = baseUrl + "/dashboard";
                if (currentUrl.contains("/customer/")) {
                    String[] parts = currentUrl.split("/customer/");
                    if (parts.length > 1) {
                        String customerId = parts[1].split("/")[0];
                        dashboardUrl = baseUrl + "/customer/" + customerId + "/dashboard";
                    }
                }
                System.out.println("Navigating to: " + dashboardUrl);
                driver.get(dashboardUrl);
                Thread.sleep(3000);
            }

            // Step 1: Navigate to service with retry logic
            System.out.println("Step 1: Navigating to " + serviceName + "...");
            boolean clicked = false;
            try {
                dashboardPage.clickService(serviceName);
                clicked = true;
            } catch (Exception e) {
                System.out.println("Standard click failed for " + serviceName + ". Retrying...");
                // Retry is handled inside clickService usually, but if it throws, we catch here
            }

            // Use significantly longer wait in headless mode or if click was flaky
            int waitTime = base.DriverFactory.isHeadlessModeEnabled() ? 8000 : 3000;
            System.out.println("Waiting " + waitTime + "ms for page load...");
            Thread.sleep(waitTime);

            // Step 2: Verify service page loaded
            String serviceCheckName = getServiceCheckName(serviceName);
            boolean isLoaded = dashboardPage.isServicePageLoaded(serviceCheckName);

            // Retry Verification if failed
            if (!isLoaded) {
                System.out.println("First verification failed. waiting 2s more...");
                Thread.sleep(2000);
                isLoaded = dashboardPage.isServicePageLoaded(serviceCheckName);
            }

            // Step 2b: Explicitly check for UI error messages
            String errorMessage = dashboardPage.checkForPageErrors();
            if (errorMessage != null) {
                System.err.println("❌ FAILURE: " + serviceName + " service loaded with error: " + errorMessage);
                if (isLoaded) {
                    System.out.println("⚠️ Warning: Error popup appeared but page loaded. Error: " + errorMessage);
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
