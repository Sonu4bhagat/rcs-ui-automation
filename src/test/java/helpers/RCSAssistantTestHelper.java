package helpers;

import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import pages.ServicesPage;
import pages.RCSAssistantPage;
import utils.ExtentReportManager;

/**
 * Helper class for RCS Assistant Flow tests
 * Handles login, navigation to RCS Portal via SSO, and common operations
 */
public class RCSAssistantTestHelper {

    private WebDriver driver;
    private LoginPage loginPage;
    private ServicesPage servicesPage;
    private RCSAssistantPage rcsAssistantPage;

    private String mainWindowHandle;
    private String rcsWindowHandle;
    private int selectedServiceRow = -1;
    private String selectedServiceAccount;

    public RCSAssistantTestHelper(WebDriver driver) {
        this.driver = driver;
        this.loginPage = new LoginPage(driver);
        this.servicesPage = new ServicesPage(driver);
        this.rcsAssistantPage = new RCSAssistantPage(driver);
    }

    /**
     * Login as Enterprise user with maximum service accounts
     */
    public void loginAsEnterprise() {
        ExtentReportManager.logStep("Login as Enterprise user");
        loginPage.loginWithEnterpriseMaxServices();
        ExtentReportManager.logPass("Successfully logged in as Enterprise");
    }

    /**
     * Navigate to Services tab
     * Waits for dashboard to be fully loaded before clicking
     */
    public void navigateToServices() {
        ExtentReportManager.logStep("Navigate to Services tab");

        // Wait for dashboard to be fully loaded before clicking Services
        System.out.println("Waiting for dashboard to be fully loaded...");
        try {
            // Wait for dashboard URL or dashboard elements
            org.openqa.selenium.support.ui.WebDriverWait dashboardWait = new org.openqa.selenium.support.ui.WebDriverWait(
                    driver, java.time.Duration.ofSeconds(15));

            // Check for dashboard indicators
            dashboardWait.until(org.openqa.selenium.support.ui.ExpectedConditions.or(
                    org.openqa.selenium.support.ui.ExpectedConditions.urlContains("dashboard"),
                    org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            org.openqa.selenium.By.xpath("//h4[contains(text(),'Dashboard')]")),
                    org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            org.openqa.selenium.By.xpath("//*[contains(@class,'dashboard')]")),
                    org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            org.openqa.selenium.By.xpath("//div[contains(@class,'wallet')]"))));
            System.out.println("Dashboard loaded. Current URL: " + driver.getCurrentUrl());

            // Additional wait for all dashboard elements to render
            Thread.sleep(3000);
            System.out.println("Waited for dashboard elements to stabilize");

        } catch (Exception e) {
            System.out.println("Warning: Dashboard wait timed out, proceeding anyway. Error: " + e.getMessage());
        }

        // Now click on Services tab
        servicesPage.clickServicesTab();
        ExtentReportManager.logPass("Navigated to Services tab");
    }

    /**
     * Open RCS Service details page
     */
    public void openRCSService() {
        ExtentReportManager.logStep("Open RCS Service details");
        servicesPage.clickServiceCardViewDetails("RCS");
        servicesPage.waitForServiceDetailsPageLoad();
        ExtentReportManager.logPass("Navigated to RCS Service details");
    }

    /**
     * Find and store the first active service account
     * 
     * @return Row index of active service account, or -1 if not found
     */
    public int identifyActiveServiceAccount() {
        ExtentReportManager.logStep("Identify Active Service Account");

        // Apply active filter first
        servicesPage.applyActiveFilter();

        int rowCount = servicesPage.getRowCount();
        if (rowCount == 0) {
            // Try finding first non-failed row
            selectedServiceRow = servicesPage.findFirstNonFailedServiceRow();
        } else {
            selectedServiceRow = servicesPage.findFirstActiveServiceRow();
            if (selectedServiceRow == -1) {
                selectedServiceRow = 1; // Default to first row
            }
        }

        if (selectedServiceRow > 0) {
            selectedServiceAccount = servicesPage.getServiceName(selectedServiceRow);
            ExtentReportManager.logInfo(
                    "Found Active Service Account: " + selectedServiceAccount + " at row " + selectedServiceRow);
        } else {
            ExtentReportManager.logWarning("No active service account found");
        }

        return selectedServiceRow;
    }

    /**
     * Perform SSO to RCS Portal
     * 
     * @return True if SSO successful
     */
    public boolean performSSO() {
        ExtentReportManager.logStep("Perform SSO to RCS Portal");

        if (selectedServiceRow <= 0) {
            selectedServiceRow = 1;
        }

        mainWindowHandle = driver.getWindowHandle();
        servicesPage.clickSSOIcon(selectedServiceRow);

        try {
            Thread.sleep(2000);
            servicesPage.switchToNewTab();
            rcsWindowHandle = driver.getWindowHandle();

            // Check for page-not-found and refresh if needed
            if (driver.getCurrentUrl().contains("page-not-found")) {
                ExtentReportManager.logWarning("Landed on Page-Not-Found, refreshing...");
                driver.navigate().refresh();
                Thread.sleep(2000);
            }

            ExtentReportManager.logPass("SSO to RCS Portal successful");
            return true;
        } catch (Exception e) {
            ExtentReportManager.logWarning("SSO failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate that we landed on RCS Portal
     * 
     * @return True if on RCS Portal
     */
    public boolean validateRCSPortalRedirection() {
        ExtentReportManager.logStep("Validate RCS Portal redirection");

        boolean onRCSPortal = rcsAssistantPage.isOnRCSPortal();

        if (onRCSPortal) {
            ExtentReportManager.logPass("Successfully redirected to RCS Portal");
        } else {
            ExtentReportManager.logWarning("Not on RCS Portal");
        }

        return onRCSPortal;
    }

    /**
     * Navigate to Manage Assistants section
     */
    public void navigateToManageAssistants() {
        rcsAssistantPage.navigateToManageAssistants();
    }

    /**
     * Get the main window handle (Enterprise portal)
     */
    public String getMainWindowHandle() {
        return mainWindowHandle;
    }

    /**
     * Get the RCS Portal window handle
     */
    public String getRCSWindowHandle() {
        return rcsWindowHandle;
    }

    /**
     * Switch back to Enterprise portal window
     */
    public void switchBackToMainWindow() {
        if (mainWindowHandle != null) {
            driver.switchTo().window(mainWindowHandle);
        }
    }

    /**
     * Close RCS Portal tab and switch back to main
     */
    public void closeRCSPortalAndSwitchBack() {
        ExtentReportManager.logStep("Close RCS Portal and switch back");

        if (rcsWindowHandle != null && !rcsWindowHandle.equals(mainWindowHandle)) {
            driver.switchTo().window(rcsWindowHandle);
            driver.close();
        }

        if (mainWindowHandle != null) {
            driver.switchTo().window(mainWindowHandle);
        }
    }

    /**
     * Get selected service account name
     */
    public String getSelectedServiceAccount() {
        return selectedServiceAccount;
    }

    /**
     * Get selected service row
     */
    public int getSelectedServiceRow() {
        return selectedServiceRow;
    }

    /**
     * Get RCS Assistant Page instance
     */
    public RCSAssistantPage getRCSAssistantPage() {
        return rcsAssistantPage;
    }

    /**
     * Get Services Page instance
     */
    public ServicesPage getServicesPage() {
        return servicesPage;
    }

    /**
     * Get Login Page instance
     */
    public LoginPage getLoginPage() {
        return loginPage;
    }

    /**
     * Complete flow: Login -> Services -> RCS -> SSO -> Manage Assistants
     * 
     * @return True if entire navigation successful
     */
    public boolean completeNavigationToRCSAssistants() {
        try {
            loginAsEnterprise();
            navigateToServices();
            openRCSService();

            if (identifyActiveServiceAccount() <= 0) {
                ExtentReportManager.logWarning("No active service account found, using first row");
            }

            if (!performSSO()) {
                return false;
            }

            if (!validateRCSPortalRedirection()) {
                return false;
            }

            navigateToManageAssistants();
            return true;

        } catch (Exception e) {
            ExtentReportManager.logWarning("Navigation failed: " + e.getMessage());
            return false;
        }
    }
}
