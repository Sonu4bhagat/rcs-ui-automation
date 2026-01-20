package helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.ServiceNodeSSOPage;

import java.util.List;
import java.util.Set;

/**
 * Helper class for Service Node SSO testing
 * Contains reusable methods for SSO role-based login testing
 */
public class SSOTestHelper {

    private WebDriver driver;
    private ServiceNodeSSOPage ssoPage;

    public SSOTestHelper(WebDriver driver) {
        this.driver = driver;
        this.ssoPage = new ServiceNodeSSOPage(driver);
    }

    /**
     * Test SSO login for a specific service with all its available roles
     * 
     * @param serviceName Name of the service to test (e.g., "SMS", "RCS", "CCS")
     * @throws InterruptedException if thread sleep is interrupted
     */
    public void testServiceSSO(String serviceName) throws InterruptedException {
        System.out.println("\n========================================");
        System.out.println("TESTING SERVICE: " + serviceName);
        System.out.println("========================================");

        // Ensure we're on SSO page
        if (!ssoPage.getCurrentURL().contains("service-nodes-sso")) {
            System.out.println("Navigating back to SSO page...");
            ssoPage.navigateBackToSSO();
            Thread.sleep(1500);
        }

        // Store the original window handle
        String originalWindow = driver.getWindowHandle();

        try {
            // Click Login button for this service
            System.out.println("\nStep 1: Clicking Login button for " + serviceName + "...");
            ssoPage.clickLoginForService(serviceName);
            Thread.sleep(2000);

            // Check if role selection menu appears
            if (ssoPage.isRoleSelectionModalDisplayed()) {
                System.out.println("✓ Role selection menu appeared");

                // Get all available roles
                System.out.println("\nStep 2: Discovering available roles...");
                List<String> roles = ssoPage.getAvailableRoles();

                if (roles.isEmpty()) {
                    System.out.println("⚠ No roles found for " + serviceName);
                    driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
                    Thread.sleep(500);
                    Assert.fail("No roles available for " + serviceName);
                    return;
                }

                System.out.println("✓ Found " + roles.size() + " role(s): " + roles);

                // Test each role
                System.out.println("\nStep 3: Testing all roles for " + serviceName + "...");
                int passed = 0;
                int failed = 0;

                for (int i = 0; i < roles.size(); i++) {
                    String roleName = roles.get(i);

                    System.out.println("\n  ----------------------------------------");
                    System.out.println("  Testing Role " + (i + 1) + "/" + roles.size() + ": " + roleName);
                    System.out.println("  ----------------------------------------");

                    try {
                        // For subsequent roles, need to click Login again
                        if (i > 0) {
                            System.out.println("  Switching back to SSO page...");

                            // Close any extra windows first
                            for (String windowHandle : driver.getWindowHandles()) {
                                if (!windowHandle.equals(originalWindow)) {
                                    driver.switchTo().window(windowHandle);
                                    driver.close();
                                }
                            }

                            // Switch back to original window
                            driver.switchTo().window(originalWindow);
                            Thread.sleep(1000);

                            // Navigate back if needed
                            if (!ssoPage.getCurrentURL().contains("service-nodes-sso")) {
                                boolean backNavigated = false;
                                for (int retry = 0; retry < 3; retry++) {
                                    try {
                                        ssoPage.navigateBackToSSO();
                                        Thread.sleep(1500);
                                        if (ssoPage.getCurrentURL().contains("service-nodes-sso")) {
                                            backNavigated = true;
                                            break;
                                        }
                                    } catch (Exception ex) {
                                        System.out.println("Back navigation retry " + (retry + 1) + " failed");
                                    }
                                }
                                if (!backNavigated) {
                                    throw new RuntimeException("Failed to navigate back to SSO page after retries");
                                }
                            }

                            ssoPage.clickLoginForService(serviceName);
                            Thread.sleep(2000);
                        }

                        // Select the role (opens new window)
                        System.out.println("  Selecting role: " + roleName);
                        ssoPage.selectRole(roleName);
                        Thread.sleep(3000);

                        // Check for new window/tab
                        Set<String> allWindows = driver.getWindowHandles();
                        System.out.println("  Total windows open: " + allWindows.size());

                        boolean redirectedToDashboard = false;
                        String newWindowUrl = "";
                        String newWindowTitle = "";

                        if (allWindows.size() > 1) {
                            // New window was opened - switch to it
                            for (String windowHandle : allWindows) {
                                if (!windowHandle.equals(originalWindow)) {
                                    driver.switchTo().window(windowHandle);
                                    Thread.sleep(1500); // Give page time to load

                                    newWindowUrl = driver.getCurrentUrl();
                                    newWindowTitle = driver.getTitle();

                                    System.out.println("  New Window URL: " + newWindowUrl);
                                    System.out.println("  New Window Title: " + newWindowTitle);

                                    // Verify dashboard loaded by checking for SPARC logo
                                    pages.DashboardPage dashboardPage = new pages.DashboardPage(driver);
                                    redirectedToDashboard = dashboardPage.verifyDashboardWithRole(roleName);

                                    break;
                                }
                            }
                        } else {
                            // No new window - check if current window changed
                            newWindowUrl = ssoPage.getCurrentURL();
                            newWindowTitle = ssoPage.getPageTitle();

                            System.out.println("  Current URL: " + newWindowUrl);
                            System.out.println("  Page Title: " + newWindowTitle);

                            // Verify dashboard loaded by checking for SPARC logo
                            if (!newWindowUrl.contains("service-nodes-sso")) {
                                pages.DashboardPage dashboardPage = new pages.DashboardPage(driver);
                                redirectedToDashboard = dashboardPage.verifyDashboardWithRole(roleName);
                            }
                        }

                        if (redirectedToDashboard) {
                            System.out.println(
                                    "  ✓ PASS: Successfully redirected to dashboard for role '" + roleName + "'");
                            passed++;
                        } else {
                            System.out.println("  ✗ FAIL: Did not redirect to dashboard for role '" + roleName + "'");
                            System.err.println("  ⚠️ FAILURE RECORDED: Using " + roleName + " in " + serviceName
                                    + " account not redirecting to dashboard");
                            failed++;
                        }

                        // Don't throw assertion here - continue testing other roles

                    } catch (Exception e) {
                        System.err.println("  ✗ ERROR testing role '" + roleName + "': " + e.getMessage());
                        failed++;
                    }

                    Thread.sleep(1000);
                }

                // Print service summary
                System.out.println("\n  ========================================");
                System.out.println("  " + serviceName + " SERVICE SUMMARY");
                System.out.println("  ========================================");
                System.out.println("  Total Roles Tested: " + roles.size());
                System.out.println("  Passed: " + passed);
                System.out.println("  Failed: " + failed);
                System.out.println("  Success Rate: " + (roles.size() > 0 ? (passed * 100 / roles.size()) : 0) + "%");
                System.out.println("  ========================================");

                // Now assert - FAIL the test if any role failed
                if (failed > 0) {
                    String failureMessage = serviceName + " service - " + failed
                            + " role(s) failed to redirect to dashboard. " +
                            "Check logs above for details on which roles failed.";
                    System.err.println("\n❌ TEST ASSERTION: " + failureMessage);
                    Assert.fail(failureMessage);
                }

            } else {
                // No role selection - direct SSO
                System.out.println("⚠ No role selection required - direct SSO");
                Thread.sleep(3000);

                boolean redirected = !ssoPage.getCurrentURL().contains("service-nodes-sso");

                if (redirected) {
                    System.out.println("✓ PASS: Direct SSO successful for " + serviceName);
                } else {
                    System.out.println("✗ FAIL: Direct SSO failed for " + serviceName);
                    Assert.fail("Direct SSO failed for " + serviceName);
                }

                // No need to navigate back as no new window
            }

        } catch (Exception e) {
            System.err.println("✗ ERROR testing service '" + serviceName + "': " + e.getMessage());
            e.printStackTrace();

            // Try to recover
            try {
                // Close all extra windows
                for (String windowHandle : driver.getWindowHandles()) {
                    if (!windowHandle.equals(originalWindow)) {
                        driver.switchTo().window(windowHandle);
                        driver.close();
                    }
                }

                // Switch back to original
                driver.switchTo().window(originalWindow);
                Thread.sleep(1000);

                if (!ssoPage.getCurrentURL().contains("service-nodes-sso")) {
                    ssoPage.navigateToServiceNodeSSO();
                }
            } catch (Exception ex) {
                System.err.println("Failed to recover: " + ex.getMessage());
            }

            Assert.fail("Failed to test " + serviceName + " service: " + e.getMessage());
        } finally {
            // Always clean up and return to original window
            try {
                for (String windowHandle : driver.getWindowHandles()) {
                    if (!windowHandle.equals(originalWindow)) {
                        driver.switchTo().window(windowHandle);
                        driver.close();
                    }
                }
                driver.switchTo().window(originalWindow);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }

        System.out.println("\n✓ Completed testing " + serviceName + " service\n");
    }
}
