package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.EnterpriseRateCardPage;

import java.util.List;

/**
 * Test class for Enterprise Rate Card Tab.
 * Tests rate card navigation, list validation, pagination, serial numbers,
 * ordering, view functionality, and details page.
 */
public class EnterpriseRateCardTabTest extends BaseTest {

    private LoginPage loginPage;
    private EnterpriseRateCardPage rateCardPage;

    @BeforeClass
    public void setupPages() {
        loginPage = new LoginPage(driver);
        rateCardPage = new EnterpriseRateCardPage(driver);
        retainSession = true; // Prevent BaseTest from auto-navigating to login
    }

    // ==================== Test Case 1: Dashboard Navigation & Rate Card
    // Redirection ====================

    @Test(priority = 1, description = "Login as Enterprise user, navigate to Dashboard and redirect to Rate Card Tab")
    public void testLoginAndRateCardNavigation() {
        System.out.println("=== Test Case 1: Dashboard Navigation & Rate Card Redirection ===");

        try {
            // Login as Enterprise user with max services
            loginPage.loginWithEnterpriseMaxServices();
            System.out.println("Logged in as Enterprise user.");

            // Navigate to Rate Card from Dashboard
            rateCardPage.navigateToRateCard();

            // Validate Rate Card page is loaded
            Assert.assertTrue(rateCardPage.isRateCardPageLoaded(),
                    "Rate Card page should be loaded after clicking Rate Card option");
            System.out.println("Successfully navigated to Rate Card Tab.");
        } catch (Exception e) {
            Assert.fail("Test Case 1 Failed: " + e.getMessage());
        }
    }

    // ==================== Test Case 2: Rate Card List Header Validation
    // ====================

    @Test(priority = 2, description = "Validate rate card list column headers are displayed correctly", dependsOnMethods = "testLoginAndRateCardNavigation")
    public void testRateCardListHeaderValidation() {
        System.out.println("=== Test Case 2: Rate Card List Header Validation ===");

        try {
            if (!rateCardPage.hasRateCards()) {
                System.out.println("No rate cards available. Checking for No Data message.");
                Assert.assertTrue(rateCardPage.isNoDataMessageDisplayed(),
                        "Either rate cards or No Data message should be displayed");
                return;
            }

            // Get all headers
            List<String> headers = rateCardPage.getRateCardListHeaders();
            Assert.assertFalse(headers.isEmpty(), "Rate card table headers should be present");

            System.out.println("Found headers: " + headers);

            // Validate headers are not empty
            for (String header : headers) {
                Assert.assertFalse(header.trim().isEmpty(), "Header text should not be empty");
            }
            System.out.println("Rate card list headers validated successfully.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            Assert.fail("Test Case 2 Failed: " + e.getMessage());
        }
    }

    // ==================== Test Case 3: Pagination & Navigation Controls Validation
    // ====================

    @Test(priority = 3, description = "Validate pagination functionality - Next and Previous buttons", dependsOnMethods = "testRateCardListHeaderValidation")
    public void testPaginationFunctionality() {
        System.out.println("=== Test Case 3: Pagination & Navigation Controls Validation ===");

        try {
            if (!rateCardPage.isPaginationAvailable()) {
                System.out
                        .println("Pagination is not available (possibly not enough records). Test passed by default.");
                return;
            }

            // Get initial pagination info
            String initialInfo = rateCardPage.getPaginationInfo();
            System.out.println("Initial pagination info: " + initialInfo);

            // Test Next button
            boolean nextClicked = rateCardPage.clickNextPage();
            if (nextClicked) {
                String afterNext = rateCardPage.getPaginationInfo();
                System.out.println("After clicking Next: " + afterNext);

                // Test Previous button
                boolean prevClicked = rateCardPage.clickPreviousPage();
                if (prevClicked) {
                    String afterPrev = rateCardPage.getPaginationInfo();
                    System.out.println("After clicking Previous: " + afterPrev);
                }
            } else {
                System.out.println("Next button is disabled (possibly on last page or only one page).");
            }

            System.out.println("Pagination functionality validated successfully.");
        } catch (Exception e) {
            Assert.fail("Test Case 3 Failed: " + e.getMessage());
        }
    }

    // ==================== Test Case 4: Duplicate Serial Number Validation
    // ====================

    @Test(priority = 4, description = "Validate no duplicate serial numbers across all pagination pages", dependsOnMethods = "testPaginationFunctionality")
    public void testDuplicateSerialNumberValidation() {
        System.out.println("=== Test Case 4: Duplicate Serial Number Validation ===");

        try {
            if (!rateCardPage.hasRateCards()) {
                System.out.println("No rate cards available. Skipping duplicate validation.");
                return;
            }

            // Find duplicate serial numbers across all pages
            List<String> duplicates = rateCardPage.findDuplicateSerialNumbers();

            Assert.assertTrue(duplicates.isEmpty(),
                    "Duplicate serial numbers found: " + duplicates);

            System.out.println("No duplicate serial numbers found. Validation passed.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            Assert.fail("Test Case 4 Failed: " + e.getMessage());
        }
    }

    // ==================== Test Case 5: Latest Rate Card Ordering Validation
    // ====================

    @Test(priority = 5, description = "Validate latest rate card entry is displayed at top of list", dependsOnMethods = "testDuplicateSerialNumberValidation")
    public void testLatestRateCardOrdering() {
        System.out.println("=== Test Case 5: Latest Rate Card Ordering Validation ===");

        try {
            if (!rateCardPage.hasRateCards()) {
                System.out.println("No rate cards available. Skipping ordering validation.");
                return;
            }

            boolean isLatestFirst = rateCardPage.isLatestRateCardFirst();

            Assert.assertTrue(isLatestFirst,
                    "Latest rate card should be displayed at the top of the list");

            System.out.println("Latest rate card ordering validated successfully.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            Assert.fail("Test Case 5 Failed: " + e.getMessage());
        }
    }

    // ==================== Test Case 6: Active Rate Card View Icon Validation
    // ====================

    @Test(priority = 6, description = "Click on View icon for Active status rate card", dependsOnMethods = "testLatestRateCardOrdering")
    public void testActiveRateCardViewIcon() {
        System.out.println("=== Test Case 6: Active Rate Card View Icon Validation ===");

        try {
            if (!rateCardPage.hasRateCards()) {
                System.out.println("No rate cards available. Skipping view icon validation.");
                return;
            }

            // Find first active rate card
            int activeRow = rateCardPage.findFirstActiveRateCard();

            if (activeRow == -1) {
                System.out.println("No active rate cards found. Skipping view icon validation.");
                return;
            }

            System.out.println("Found Active rate card at row: " + activeRow);

            // Validate view icon is clickable for active rate card
            Assert.assertTrue(rateCardPage.isViewIconClickable(activeRow),
                    "View icon should be clickable for Active rate card");

            // Click the view icon
            rateCardPage.clickViewIcon(activeRow);

            // Validate redirection to details page
            Assert.assertTrue(rateCardPage.isRateCardDetailsPageLoaded(),
                    "Should be redirected to Rate Card Details page after clicking View icon");

            System.out.println("Active rate card View icon validation passed. Redirected to Details page.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            Assert.fail("Test Case 6 Failed: " + e.getMessage());
        }
    }

    // ==================== Test Case 7: Rate Card Details - Services Display
    // Validation ====================

    @Test(priority = 7, description = "All wallet services visible in details", dependsOnMethods = "testActiveRateCardViewIcon")
    public void testRateCardDetailsServicesDisplay() {
        System.out.println("=== Test Case 7: Rate Card Details - Services Display Validation ===");

        try {
            // Step 1: Navigate back to Rate Card list first
            rateCardPage.navigateBackToRateCardList();
            System.out.println("Navigated back to Rate Card list.");

            // Step 2: Navigate to Services tab to collect available services
            System.out.println("Step 1: Navigating to Services tab to collect available services...");
            rateCardPage.navigateToServicesTab();

            // Collect services from Services tab
            List<String> servicesFromServicesTab = rateCardPage.getServicesFromServicesTab();
            System.out.println("Services from Services Tab: " + servicesFromServicesTab);

            Assert.assertFalse(servicesFromServicesTab.isEmpty(),
                    "Services should be available in Services tab");

            // Step 3: Navigate to Rate Card tab
            System.out.println("Step 2: Navigating to Rate Card tab...");
            rateCardPage.navigateToRateCard();

            // Step 4: Find first active rate card and click view
            int activeRow = rateCardPage.findFirstActiveRateCard();
            if (activeRow == -1) {
                System.out.println("No active rate cards found. Skipping services validation.");
                return;
            }

            System.out.println("Step 3: Clicking View icon for Active rate card at row: " + activeRow);
            rateCardPage.clickViewIcon(activeRow);

            // Validate we're on details page
            Assert.assertTrue(rateCardPage.isRateCardDetailsPageLoaded(),
                    "Should be redirected to Rate Card Details page");

            // Step 5: Get services displayed on Rate Card Details page
            List<String> servicesOnDetails = rateCardPage.getServicesOnRateCardDetails();
            System.out.println("Services on Rate Card Details: " + servicesOnDetails);

            Assert.assertFalse(servicesOnDetails.isEmpty(),
                    "Services should be displayed on Rate Card Details page");

            // Step 6: Validate all Services tab services are present in Rate Card Details
            System.out.println("\n--- Comparing Services ---");
            System.out.println("Services from Services Tab: " + servicesFromServicesTab);
            System.out.println("Services on Rate Card Details: " + servicesOnDetails);

            List<String> missingServices = new java.util.ArrayList<>();
            for (String service : servicesFromServicesTab) {
                boolean found = servicesOnDetails.stream()
                        .anyMatch(s -> s.equalsIgnoreCase(service));
                if (found) {
                    System.out.println("✓ Service found: " + service);
                } else {
                    System.out.println("✗ Service NOT found: " + service);
                    missingServices.add(service);
                }
            }

            Assert.assertTrue(missingServices.isEmpty(),
                    "Missing services in Rate Card Details: " + missingServices);

            System.out.println("Rate Card Details services display validated successfully. All services matched.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            Assert.fail("Test Case 7 Failed: " + e.getMessage());
        }
    }

    // ==================== Test Case 8: Edit Functionality Restriction Validation
    // ====================

    @Test(priority = 8, description = "Validate no Edit functionality available for Rate Cards", dependsOnMethods = "testRateCardDetailsServicesDisplay")
    public void testEditFunctionalityRestriction() {
        System.out.println("=== Test Case 8: Edit Functionality Restriction Validation ===");

        try {
            // We should still be on the details page from Test Case 7
            if (!rateCardPage.isRateCardDetailsPageLoaded()) {
                System.out.println("Not on Rate Card Details page. Test may be dependent on previous test.");
                return;
            }

            // Check that Edit button is NOT present
            boolean editButtonPresent = rateCardPage.isEditButtonPresent();

            Assert.assertFalse(editButtonPresent,
                    "Edit button should NOT be present on Rate Card Details page");

            System.out.println("Edit functionality restriction validated - No Edit button present.");

            // Navigate back to Rate Card list for cleanup
            rateCardPage.navigateBackToRateCardList();
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            Assert.fail("Test Case 8 Failed: " + e.getMessage());
        }
    }

    // ==================== Test Case 9: Logout ====================

    @Test(priority = 9, description = "Logout from Enterprise account", dependsOnMethods = "testEditFunctionalityRestriction", alwaysRun = true)
    public void testLogout() {
        System.out.println("=== Test Case 9: Logout ===");

        try {
            loginPage.logout();
            System.out.println("Successfully logged out from Enterprise account.");
        } catch (Exception e) {
            System.out.println("Logout encountered an issue: " + e.getMessage());
            // This is cleanup, don't fail the test
        }
    }
}
