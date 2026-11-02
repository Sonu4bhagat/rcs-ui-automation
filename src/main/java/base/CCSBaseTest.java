package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import pages.ccs.CCSLivePage;

import java.util.function.Supplier;

/**
 * Base class of the CCS test classes: one browser and one login for the whole CCS suite.
 * BaseTest opens and closes a browser per class, so every CCS part used to log in and SSO again. Here the browser
 * is kept across the CCS classes: Part A logs in and SSOs into CCS-Live once, and the later parts continue on that
 * CCS-Live tab. The browser is closed once, at the end of the suite. A CCS class run on its own still logs in once.
 */
public abstract class CCSBaseTest extends BaseTest {

    private static WebDriver sharedDriver;
    private static String sharedCCSLiveTab;

    @Override
    @BeforeClass
    public void setUpDriverOnce() {
        if (isAlive(sharedDriver)) {
            driver = sharedDriver;
            System.out.println("[CCS] Reusing the signed-in CCS browser - no new login");
            return;
        }
        super.setUpDriverOnce();
        sharedDriver = driver;
        sharedCCSLiveTab = null;
    }

    @Override
    @AfterClass(alwaysRun = true)
    public void tearDownDriverOnce() {
        // Keep the browser (and its CCS session) for the next CCS class - it is closed in closeCCSBrowser()
        if (driver != null && driver != sharedDriver) {
            driver.quit(); // a browser BaseTest restarted after a crash is not the shared one
        }
    }

    @AfterSuite(alwaysRun = true)
    public void closeCCSBrowser() {
        if (sharedDriver != null) {
            try {
                sharedDriver.quit();
            } catch (Exception ignored) {
                // already closed
            }
            sharedDriver = null;
            sharedCCSLiveTab = null;
        }
    }

    /** Records the signed-in CCS-Live tab so the later CCS classes continue there. */
    protected static void rememberCCSLiveTab(String handle) {
        sharedCCSLiveTab = handle;
    }

    /**
     * Brings the driver to the CCS-Live dashboard and returns its tab: reuses the signed-in CCS-Live tab when there
     * is one (no login), otherwise runs {@code signIn} once - logs in (if needed) and SSOs into CCS-Live, returning
     * the CCS-Live tab (the test classes pass CCSTestHelper::openCCSLiveDashboard).
     */
    protected String openCCSLiveDashboard(Supplier<String> signIn, CCSLivePage livePage) {
        if (sharedCCSLiveTab != null && driver.getWindowHandles().contains(sharedCCSLiveTab)) {
            driver.switchTo().window(sharedCCSLiveTab);
            driver.get(CCSLivePage.getConfiguredUrl());
            if (livePage.waitForLiveDashboard()) {
                System.out.println("[CCS] Continuing on the signed-in CCS-Live tab");
                return sharedCCSLiveTab;
            }
            System.out.println("[CCS] CCS-Live session has ended - signing in again");
        }
        // Start from the first (Vault) tab: the sign-in step logs in only when the Vault login form is shown
        driver.switchTo().window(driver.getWindowHandles().iterator().next());
        driver.get(utils.ConfigReader.get("url"));
        String tab = signIn.get();
        rememberCCSLiveTab(tab);
        return tab;
    }

    private static boolean isAlive(WebDriver webDriver) {
        if (webDriver == null) {
            return false;
        }
        try {
            webDriver.getWindowHandles();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
