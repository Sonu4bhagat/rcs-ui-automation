package helpers;

import locators.ccs.CCSUsersPageLocators;
import locators.common.DashboardPageLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.ccs.CCSLivePage;
import pages.ccs.CCSUsersPage;
import pages.enterprise.ServicesPage;
import utils.ExtentReportManager;
import utils.TestUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class for CCS project testing
 * Contains reusable methods for the Enterprise -> CCS SSO -> CCS-Live flow, tab handling and CCS-Live Users management
 */
public class CCSTestHelper {

    // Enterprise sidebar items expected for the CCS Enterprise user
    public static final String[] ENTERPRISE_NAV_ITEMS = { "Dashboard", "Services", "Wallet", "Rate Card",
            "Reports", "Control Center" };

    private WebDriver driver;
    private ServicesPage servicesPage;
    private CCSLivePage ccsLivePage;
    // Set once an agent was deleted to free a slot - a retried test must not delete a second agent
    private boolean agentDeleted;

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

    /**
     * Setup for the CCS parts after Part A: logs in with the CCS Enterprise credentials (CCSUser), opens
     * Services -> CCS and SSOs into CCS-Live with the first non-failed service account - the same flow Part A
     * verifies. Leaves the driver on the CCS-Live dashboard tab and returns its handle.
     */
    public String openCCSLiveDashboard() {
        new pages.common.LoginPage(driver).loginWithCCSEnterpriseCredentials();
        openCCSServiceAccountList();
        int row = servicesPage.findFirstNonFailedServiceRow();
        Assert.assertTrue(row != -1, "No non-failed CCS service account found");
        Assert.assertTrue(servicesPage.isSSOIconAvailable(row), "CCS SSO icon is not available");
        String ccsTab = clickSSOAndWaitForNewTab(row).get(0);
        driver.switchTo().window(ccsTab);
        Assert.assertTrue(ccsLivePage.waitForLiveDashboard(),
                "SSO did not reach the CCS-Live dashboard. URL: " + ccsLivePage.getSafeCurrentUrl());
        return ccsTab;
    }

    /** True when a user with this email (CCS-Live Username) is listed. Must be called on the Users list. */
    public boolean isEmailRegistered(CCSUsersPage usersPage, String email) {
        int rows = usersPage.searchUsers(CCSUsersPageLocators.SEARCH_BY_USER_NAME, email);
        boolean registered = false;
        for (int i = 1; i <= rows; i++) {
            if (usersPage.getCellText(i, CCSUsersPageLocators.COL_USERNAME).equalsIgnoreCase(email)) {
                registered = true;
                break;
            }
        }
        usersPage.clearSearch();
        System.out.println("Email " + email + " registered: " + registered);
        return registered;
    }

    /**
     * Returns {@code preferredEmail} when it is not registered; otherwise a unique plus-alias of it
     * (e.g. sonu.bhagat+NNNNNN@altiquence.com) that is not registered either. Must be called on the Users list.
     */
    public String resolveAvailableEmail(CCSUsersPage usersPage, String preferredEmail) {
        String email = preferredEmail;
        for (int attempt = 1; attempt <= 5 && isEmailRegistered(usersPage, email); attempt++) {
            email = TestUtil.getUniqueAliasEmail(preferredEmail);
            System.out.println("Email already registered - generated unique email: " + email);
        }
        return email;
    }

    /** Opens the CCS-Live Users list unless it is already shown. */
    public void openUsersListIfNeeded(CCSUsersPage usersPage) {
        if (!usersPage.isOnUsersList()) {
            usersPage.openUsersTab();
        }
    }

    /** Fills the Add User form with an Agent user using WebRTC. */
    public void fillAgentForm(CCSUsersPage usersPage, String name, String contact, String email) {
        usersPage.enterName(name);
        usersPage.enterContactNumber(contact);
        usersPage.enterEmail(email);
        usersPage.selectRole("Agent");
        usersPage.setCallMode(true, false);
    }

    /** Asserts the Add User form still holds the given Agent / WebRTC user. */
    public void assertFormHoldsUser(CCSUsersPage usersPage, String name, String contact, String email) {
        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.NAME_INPUT), name, "Name not retained");
        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.CONTACT_NUMBER_INPUT), contact, "Contact not retained");
        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.EMAIL_INPUT), email, "Email not retained");
        Assert.assertEquals(usersPage.getSelectedOption(CCSUsersPageLocators.ROLE_SELECT), "Agent", "Role not retained");
        Assert.assertTrue(usersPage.isChecked(CCSUsersPageLocators.CALL_MODE_WEBRTC), "WebRTC not retained");
        Assert.assertFalse(usersPage.isChecked(CCSUsersPageLocators.CALL_MODE_PHONE), "Phone selected");
    }

    /**
     * Agent limit reached (the account allows 2 agents): leaves the Add User form and deletes the oldest agent.
     * Only once per helper instance (one test class run) - a second limit message means deleting did not free
     * a slot, so the test fails instead of deleting more agents.
     */
    public void freeAgentSlot(CCSUsersPage usersPage, String limitMessage) {
        Assert.assertFalse(agentDeleted, "Limit still reached after deleting an agent - deleting does not free a slot: " + limitMessage);
        ExtentReportManager.logWarning("Agent limit reached ('" + limitMessage + "') - deleting the oldest agent and retrying");
        usersPage.clickCancel();
        usersPage.waitForUsersList();
        int oldestRow = usersPage.findOldestAgentRow();
        Assert.assertTrue(oldestRow != -1, "Agent limit reached but no agent found to delete");
        String deleted = usersPage.deleteUserAtRow(oldestRow);
        agentDeleted = true;
        ExtentReportManager.logWarning("Deleted agent " + deleted + " to free an agent slot");
    }

    /** Asserts exactly one user with {@code email} is listed with the given Name, Contact, Role Agent and WebRTC. */
    public void verifyUserListedOnce(CCSUsersPage usersPage, String name, String contact, String email) {
        openUsersListIfNeeded(usersPage);
        int rows = usersPage.searchUsers(CCSUsersPageLocators.SEARCH_BY_USER_NAME, email);
        Assert.assertEquals(rows, 1, "Expected exactly one user with email " + email);

        String contactCell = usersPage.getCellText(1, CCSUsersPageLocators.COL_CONTACT_NUMBER);
        String agentType = usersPage.getCellText(1, CCSUsersPageLocators.COL_AGENT_TYPE);
        // The Users list shows names in Title Case (e.g. "CCS Agent abcDe" -> "Ccs Agent Abcde")
        String nameCell = usersPage.getCellText(1, CCSUsersPageLocators.COL_NAME);
        Assert.assertTrue(nameCell.equalsIgnoreCase(name), "Name in list: " + nameCell + ", expected: " + name);
        Assert.assertTrue(contactCell.endsWith(contact), "Contact Number in list: " + contactCell);
        Assert.assertEquals(usersPage.getCellText(1, CCSUsersPageLocators.COL_USERNAME), email, "Email in list");
        Assert.assertEquals(usersPage.getCellText(1, CCSUsersPageLocators.COL_ROLE), "Agent", "Role in list");
        Assert.assertTrue(agentType.equalsIgnoreCase("Webrtc"), "Call Mode (Agent Type) in list: " + agentType);
        usersPage.clearSearch();
    }
}
