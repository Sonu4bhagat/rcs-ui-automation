package helpers;

import locators.common.DashboardPageLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.ccs.CCSLivePage;
import pages.enterprise.ServicesPage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class for CCS project testing
 * Contains reusable methods for the Enterprise -> CCS SSO -> CCS-Live flow and tab handling
 */
public class CCSTestHelper {

    // Enterprise sidebar items expected for the CCS Enterprise user
    public static final String[] ENTERPRISE_NAV_ITEMS = { "Dashboard", "Services", "Wallet", "Rate Card",
            "Reports", "Control Center" };

    private WebDriver driver;
    private ServicesPage servicesPage;
    private CCSLivePage ccsLivePage;

    public CCSTestHelper(WebDriver driver) {
        this.driver = driver;
        this.servicesPage = new ServicesPage(driver);
        this.ccsLivePage = new CCSLivePage(driver);
    }

    /** Role badge text under the user name in the Vault header (e.g. "enterprise"). */
    public String getUserRoleBadge() {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DashboardPageLocators.USER_ROLE_BADGE))
                .getText().trim();
    }

    /** Returns the Enterprise sidebar items from {@link #ENTERPRISE_NAV_ITEMS} that are not displayed. */
    public List<String> getMissingEnterpriseNavItems() {
        List<String> missing = new ArrayList<>();
        for (String item : ENTERPRISE_NAV_ITEMS) {
            if (!isNavItemDisplayed(item)) {
                missing.add(item);
            }
        }
        return missing;
    }

    public boolean isNavItemDisplayed(String itemText) {
        try {
            By item = By.xpath("//span[normalize-space()='" + itemText + "'] | //a[normalize-space()='" + itemText + "']");
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> d.findElements(item).stream().anyMatch(e -> e.isDisplayed()));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Services -> CCS card -> CCS service account list (skips navigation when already there). */
    public void openCCSServiceAccountList() {
        if (driver.getCurrentUrl().contains("/services/CCS")) {
            return;
        }
        servicesPage.clickServicesTab();
        servicesPage.clickServiceCardViewDetails("CCS");
        servicesPage.waitForServiceDetailsPageLoad();
        System.out.println("Navigated to CCS service account list");
    }

    /**
     * Clicks the CCS SSO icon of {@code row} once and waits for the CCS-Live tab to open.
     * Stays on the current (Vault) tab and returns the handles of the newly opened tab(s).
     */
    public List<String> clickSSOAndWaitForNewTab(int row) {
        Set<String> handlesBefore = new HashSet<>(driver.getWindowHandles());
        servicesPage.clickSSOIcon(row);
        return ccsLivePage.waitForNewTabs(handlesBefore);
    }

    /** Switches to a tab opened by an earlier test, failing clearly if it was never opened or is closed. */
    public void switchToTab(String handle) {
        Assert.assertNotNull(handle, "CCS-Live tab was not opened by an earlier test");
        Assert.assertTrue(driver.getWindowHandles().contains(handle), "CCS-Live tab is no longer open");
        driver.switchTo().window(handle);
    }
}
