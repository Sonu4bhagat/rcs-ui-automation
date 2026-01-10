package helpers;

import org.openqa.selenium.WebDriver;
import pages.EnterpriseReportsPage;

import java.util.Set;

/**
 * Helper class for Enterprise Reports Tab tests.
 * Provides utility methods for managing browser tabs and navigation.
 */
public class EnterpriseReportsHelper {

    private WebDriver driver;
    private EnterpriseReportsPage reportsPage;
    private String mainWindowHandle;

    public EnterpriseReportsHelper(WebDriver driver, EnterpriseReportsPage reportsPage) {
        this.driver = driver;
        this.reportsPage = reportsPage;
    }

    /**
     * Store the main window handle for later use
     */
    public void storeMainWindowHandle() {
        this.mainWindowHandle = driver.getWindowHandle();
        System.out.println("Stored main window handle: " + mainWindowHandle);
    }

    /**
     * Get the stored main window handle
     */
    public String getMainWindowHandle() {
        return mainWindowHandle;
    }

    /**
     * Close extra tabs and navigate back to main Reports page.
     * This is used between service tests to clean up SSO tabs.
     */
    public void closeExtraTabsAndNavigateToReports() {
        try {
            Set<String> handles = driver.getWindowHandles();
            if (handles.size() > 1 && mainWindowHandle != null) {
                for (String handle : handles) {
                    if (!handle.equals(mainWindowHandle)) {
                        driver.switchTo().window(handle);
                        driver.close();
                    }
                }
                driver.switchTo().window(mainWindowHandle);
            }
            Thread.sleep(1000);
            reportsPage.navigateBackToReports();
        } catch (Exception e) {
            System.out.println("Error closing tabs: " + e.getMessage());
        }
    }
}
