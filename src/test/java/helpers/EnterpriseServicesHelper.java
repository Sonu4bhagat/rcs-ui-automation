package helpers;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.ServicesPage;

public class EnterpriseServicesHelper {

    private WebDriver driver;
    private ServicesPage servicesPage;

    public EnterpriseServicesHelper(WebDriver driver) {
        this.driver = driver;
        this.servicesPage = new ServicesPage(driver);
    }

    public void validateServiceFlow(String serviceName) {
        System.out.println("\n========== Validating " + serviceName + " Service ==========");

        // 1. Navigate to specific service details from Services Home
        servicesPage.clickServicesTab();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        servicesPage.clickServiceCardViewDetails(serviceName);

        // 2. Find Active Service Account
        int activeRow = servicesPage.findFirstActiveServiceRow();
        Assert.assertTrue(activeRow != -1, "No Active Service Account found for " + serviceName);
        System.out.println("Found Active Service Account at row: " + activeRow);

        // 3. Get Name and Verify Detail View
        String listingName = servicesPage.getServiceName(activeRow);
        System.out.println("Service Account Name: " + listingName);

        servicesPage.clickViewIcon(activeRow);
        String detailsName = servicesPage.getDetailsServiceName();
        // Compare
        Assert.assertTrue(detailsName.contains(listingName) || listingName.contains(detailsName),
                "Details Verification Failed! expected: " + listingName + ", found: " + detailsName);
        System.out.println("Details View Verification Passed.");

        servicesPage.clickBackButton();

        // 4. SSO Redirection
        activeRow = servicesPage.findFirstActiveServiceRow();

        String mainWindowHandle = driver.getWindowHandle();
        servicesPage.clickSSOIcon(activeRow);
        servicesPage.switchToNewTab();

        // Validate Dashboard Header matches Service Name (or Page Source)
        Assert.assertTrue(driver.getPageSource().contains(listingName),
                "SSO Redirection Verification Failed! Name " + listingName + " not found on dashboard.");
        System.out.println("SSO Redirection Verification Passed.");

        servicesPage.closeCurrentTabAndSwitchBack(mainWindowHandle);

        // 5. Search Functionality
        String partialName = listingName.substring(0, Math.min(3, listingName.length()));
        servicesPage.searchService(partialName);
        // Verify first row contains searched text
        String firstRowName = servicesPage.getServiceName(1);
        Assert.assertTrue(firstRowName.toLowerCase().contains(partialName.toLowerCase()),
                "Search Failed! Expected " + firstRowName + " to contain " + partialName);
        System.out.println("Search Validation Passed.");

        servicesPage.searchService(""); // Clear search
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        // 6. Filter Functionality
        servicesPage.applyActiveFilter();
        // Verify first row is Active
        String status = servicesPage.getServiceStatus(1);
        Assert.assertEquals(status.toUpperCase(), "ACTIVE", "Filter Failed! Expected Active, found " + status);
        System.out.println("Active Filter Validation Passed.");
    }
}
