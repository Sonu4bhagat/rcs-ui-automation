package tests.ccs;

import helpers.CCSTestHelper;
import locators.ccs.CCSAgentLoginPageLocators;
import locators.ccs.CCSUsersPageLocators;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.ccs.CCSAgentLoginPage;
import pages.ccs.CCSCampaignPage;
import pages.ccs.CCSLivePage;
import pages.ccs.CCSQueuePage;
import pages.ccs.CCSUsersPage;
import utils.ConfigReader;
import utils.ExtentReportManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CCS Part E - Agent tab: log the Part B agent in from the Users list (row action "Login") with WebRTC and the
 * Part D campaign, and verify the Agent Live Dashboard. Then clean up the test data created by this execution
 * (Part D campaign, Part C queue, Part B agent).
 * Starts on the CCS-Live dashboard: the class setup logs in with the CCS credentials and SSOs into CCS-Live
 * (the flow verified in Part A, reused through CCSTestHelper - not re-tested here). No user, queue or campaign is
 * created here. Tests run as one chain in the written order; the cleanup tests run even when an earlier test fails.
 */
public class CCSEnterpriseAgentTab extends CCSBaseTest {

    private static final String LOGIN_TYPE = "Webrtc";
    private static final List<String> EXPECTED_DASHBOARD_SECTIONS = List.of("Incoming Calls", "Outgoing Calls",
            "Agent Status Summary", "Call History");

    private CCSTestHelper ccsHelper;
    private CCSLivePage ccsLivePage;
    private CCSUsersPage usersPage;
    private CCSAgentLoginPage agentLoginPage;

    // Agent from Part B and campaign from Part D (null when missing)
    private String agentName;
    private String agentEmail;
    private String campaignName;
    private String adminTab;
    private int agentRow = -1;
    private boolean agentLoginOpened;

    @BeforeClass
    public void openCCSLiveDashboard() {
        ccsHelper = new CCSTestHelper(driver);
        ccsLivePage = new CCSLivePage(driver);
        usersPage = new CCSUsersPage(driver);
        agentLoginPage = new CCSAgentLoginPage(driver);
        retainSession = true; // stay on the CCS-Live tab between tests
        adminTab = openCCSLiveDashboard(ccsHelper, ccsLivePage);
        agentName = ccsHelper.getPartBAgentName(usersPage, ConfigReader.get("ccs.users.email"));
        agentEmail = CCSTestHelper.getCreatedAgentEmail(); // known only when Part B ran in this execution
        campaignName = ccsHelper.getPartDCampaignName(new CCSCampaignPage(driver), "CCS Campaign");
    }

    // ==================== TC-28 / TC-29: Users list and the Part B agent ====================

    @Test(priority = 1, description = "TC-28: Users tab opens the Users listing")
    public void testNavigateToUsers() {
        driver.switchTo().window(adminTab);
        usersPage.openUsersTab();
        Assert.assertTrue(usersPage.isOnUsersList(), "Users list not opened: " + ccsLivePage.getSafeCurrentUrl());
        List<String> headers = usersPage.getTableHeaders();
        Assert.assertTrue(headers.containsAll(List.of(CCSUsersPageLocators.COL_NAME, CCSUsersPageLocators.COL_USERNAME,
                CCSUsersPageLocators.COL_ROLE, CCSUsersPageLocators.COL_ACTION)), "Users list columns missing. Found: " + headers);
        Assert.assertTrue(usersPage.getRowCount() > 0, "Users list is empty");
    }

    @Test(priority = 2, description = "TC-29: The agent created in Part B is found with its details", dependsOnMethods = "testNavigateToUsers")
    public void testFindPartBAgent() {
        if (agentName == null) {
            throw new SkipException("Prerequisite missing: no agent created by Part B (CCSEnterpriseUsers) - run Part B first");
        }
        int rows = usersPage.searchUsers(CCSUsersPageLocators.SEARCH_BY_NAME, agentName);
        for (int row = 1; row <= rows; row++) {
            if (usersPage.getCellText(row, CCSUsersPageLocators.COL_NAME).equalsIgnoreCase(agentName)) {
                agentRow = row;
                break;
            }
        }
        Assert.assertTrue(agentRow != -1, "Part B agent not listed: " + agentName);
        System.out.println("Part B agent " + agentName + " at row " + agentRow + ": "
                + usersPage.getCellText(agentRow, CCSUsersPageLocators.COL_USERNAME));
        Assert.assertEquals(usersPage.getCellText(agentRow, CCSUsersPageLocators.COL_ROLE), "Agent", "Role");
        Assert.assertTrue(usersPage.getCellText(agentRow, CCSUsersPageLocators.COL_AGENT_TYPE).equalsIgnoreCase("Webrtc"), "Agent Type");
        Assert.assertEquals(usersPage.getCellText(agentRow, CCSUsersPageLocators.COL_STATUS), "Active", "Status");
        if (agentEmail != null) {
            Assert.assertEquals(usersPage.getCellText(agentRow, CCSUsersPageLocators.COL_USERNAME), agentEmail, "Username");
        }
    }

    // ==================== TC-30 / TC-31 / TC-32: Agent Login ====================

    @Test(priority = 3, description = "TC-30: The agent's three-dot action menu opens with the Login action", dependsOnMethods = "testFindPartBAgent")
    public void testOpenAgentActions() {
        // The Action column is at the far right: the menu button is scrolled into view horizontally before clicking
        List<String> actions = usersPage.openRowActions(agentRow);
        System.out.println("Agent actions: " + actions);
        Assert.assertTrue(actions.contains("Login"), "Login action missing. Actions: " + actions);
    }

    @Test(priority = 4, description = "TC-31: Login opens the agent login page in a new tab ('Agent Already Logged In' is confirmed with 'Yes, proceed!')", dependsOnMethods = "testOpenAgentActions")
    public void testClickLoginCta() {
        Set<String> tabsBefore = new HashSet<>(driver.getWindowHandles());
        usersPage.clickRowAction(agentRow, "Login");
        // When the agent still has a session, the app asks "Agent Already Logged In ... proceed with SSO login?"
        String dialog = usersPage.confirmDialog(5);
        if (!dialog.isEmpty()) {
            ExtentReportManager.logInfo("Confirmed: " + dialog);
        }
        List<String> newTabs = ccsLivePage.waitForNewTabs(tabsBefore);
        Assert.assertEquals(newTabs.size(), 1, "Login should open exactly one tab");
        driver.switchTo().window(newTabs.get(0));
        agentLoginOpened = true;
    }

    @Test(priority = 5, description = "TC-32: The agent login page shows the login configuration form", dependsOnMethods = "testClickLoginCta")
    public void testAgentLoginPage() {
        Assert.assertTrue(agentLoginPage.waitForLoginForm(), "Agent login form not shown. URL: " + ccsLivePage.getSafeCurrentUrl());
        Assert.assertEquals(CCSLivePage.hostOf(driver.getCurrentUrl()), CCSLivePage.hostOf(CCSLivePage.getConfiguredUrl()),
                "Agent login opened on a different application");
        Assert.assertTrue(agentLoginPage.getFormLabels().containsAll(List.of("Select Type", "Select Campaign")),
                "Form labels: " + agentLoginPage.getFormLabels());
        Assert.assertTrue(agentLoginPage.isDisplayed(CCSAgentLoginPageLocators.SUBMIT_BUTTON), "Submit not displayed");
    }

    // ==================== TC-33 / TC-34 / TC-35: Configuration ====================

    @Test(priority = 6, description = "TC-33: Select Type WebRTC is selected", dependsOnMethods = "testAgentLoginPage")
    public void testSelectWebRTC() {
        List<String> types = agentLoginPage.getLoginTypeOptions();
        Assert.assertTrue(types.stream().anyMatch(t -> t.equalsIgnoreCase(LOGIN_TYPE)), "WebRTC not offered. Offered: " + types);
        agentLoginPage.selectLoginType(LOGIN_TYPE);
        Assert.assertTrue(agentLoginPage.getSelectedLoginType().equalsIgnoreCase(LOGIN_TYPE),
                "Selected type: " + agentLoginPage.getSelectedLoginType());
    }

    @Test(priority = 7, description = "TC-34: The campaign created in Part D is selected", dependsOnMethods = "testSelectWebRTC")
    public void testSelectPartDCampaign() {
        if (campaignName == null) {
            throw new SkipException("Prerequisite missing: no Running campaign created by Part D (CCSEnterpriseCampaign) - run Part D first");
        }
        List<String> campaigns = agentLoginPage.getCampaignOptions();
        System.out.println("Campaigns offered to the agent: " + campaigns);
        Assert.assertTrue(campaigns.stream().anyMatch(c -> c.equalsIgnoreCase(campaignName)),
                "Part D campaign '" + campaignName + "' not offered to the agent. Offered: " + campaigns);
        agentLoginPage.selectCampaign(campaignName);
        Assert.assertTrue(agentLoginPage.getSelectedCampaign().equalsIgnoreCase(campaignName),
                "Selected campaign: " + agentLoginPage.getSelectedCampaign());
    }

    @Test(priority = 8, description = "TC-35: Submit is accepted", dependsOnMethods = "testSelectPartDCampaign")
    public void testSubmitConfiguration() {
        Assert.assertTrue(agentLoginPage.getSelectedLoginType().equalsIgnoreCase(LOGIN_TYPE), "WebRTC not retained");
        Assert.assertTrue(agentLoginPage.getSelectedCampaign().equalsIgnoreCase(campaignName), "Campaign not retained");
        agentLoginPage.clickSubmit();
        Assert.assertTrue(agentLoginPage.waitForAgentDashboard(), "Submit was not accepted. URL: " + ccsLivePage.getSafeCurrentUrl()
                + " Message: [" + usersPage.getNotifications() + "]");
        String message = usersPage.getNotifications();
        System.out.println("Submit message: [" + message + "]");
        Assert.assertFalse(message.toLowerCase().matches(".*(error|fail|invalid).*"), "Submit reported a problem: " + message);
    }

    // ==================== TC-36: Agent Live Dashboard ====================

    @Test(priority = 9, description = "TC-36: The Agent Live Dashboard is loaded with its sections", dependsOnMethods = "testSubmitConfiguration")
    public void testAgentLiveDashboard() {
        Assert.assertTrue(driver.getCurrentUrl().contains(CCSAgentLoginPage.AGENT_DASHBOARD_PATH), "Not on the Agent Live Dashboard: " + ccsLivePage.getSafeCurrentUrl());
        List<String> headings = agentLoginPage.getDashboardHeadings();
        System.out.println("Agent Live Dashboard sections: " + headings);
        Assert.assertTrue(headings.containsAll(EXPECTED_DASHBOARD_SECTIONS), "Agent dashboard sections missing. Found: " + headings);
        Assert.assertTrue(agentLoginPage.isDisplayed(CCSAgentLoginPageLocators.SWITCH_CAMPAIGN_BUTTON), "Switch Campaign not displayed");
        Assert.assertNull(ccsLivePage.findErrorPageText(), "Error page shown on the Agent Live Dashboard");
    }

    // ==================== Cleanup (runs after all tests above, even when one failed) ====================
    // Deletes only what Parts B, C and D created in THIS execution, in dependency order: campaign (uses the queue),
    // queue (holds the agent), agent.

    @Test(priority = 10, description = "Cleanup 1/3: Delete the campaign created by Part D in this execution", dependsOnMethods = "testAgentLiveDashboard", alwaysRun = true)
    public void testDeletePartDCampaign() {
        String name = CCSTestHelper.getCreatedCampaignName();
        if (name == null) {
            throw new SkipException("No campaign was created by Part D in this execution - nothing to delete");
        }
        backToAdminTab();
        CCSCampaignPage campaignPage = new CCSCampaignPage(driver);
        campaignPage.openCampaignTab();
        String messages = campaignPage.deleteCampaign(name);
        System.out.println("Delete campaign " + name + ": " + messages);
        // The Campaign list keeps showing a deleted campaign until the page is reloaded - reload before each check
        Assert.assertEquals(ccsHelper.waitUntilNotListed(() -> {
            ccsLivePage.refresh();
            campaignPage.waitForCampaignList();
            return campaignPage.searchCampaignsByName(name);
        }, 60), 0, "Campaign still listed after delete: " + name + " (" + messages + ")");
        ExtentReportManager.logPass("Deleted campaign " + name);
    }

    @Test(priority = 11, description = "Cleanup 2/3: Delete the queue created by Part C in this execution", dependsOnMethods = "testDeletePartDCampaign", alwaysRun = true)
    public void testDeletePartCQueue() {
        String name = CCSTestHelper.getCreatedQueueName();
        if (name == null) {
            throw new SkipException("No queue was created by Part C in this execution - nothing to delete");
        }
        backToAdminTab();
        CCSQueuePage queuePage = new CCSQueuePage(driver);
        queuePage.openCallFlow();
        queuePage.openQueueTab();
        String messages = queuePage.deleteQueue(name);
        System.out.println("Delete queue " + name + ": " + messages);
        Assert.assertEquals(ccsHelper.waitUntilNotListed(() -> queuePage.searchQueuesByName(name), 20), 0,
                "Queue still listed after delete: " + name + " (" + messages + ")");
        ExtentReportManager.logPass("Deleted queue " + name);
    }

    @Test(priority = 12, description = "Cleanup 3/3: Delete the agent created by Part B in this execution", dependsOnMethods = "testDeletePartCQueue", alwaysRun = true)
    public void testDeletePartBAgent() {
        String email = CCSTestHelper.getCreatedAgentEmail();
        if (email == null) {
            throw new SkipException("No agent was created by Part B in this execution - nothing to delete");
        }
        backToAdminTab();
        if (!usersPage.isOnUsersList()) {
            usersPage.openUsersTab();
        }
        int rows = usersPage.searchUsers(CCSUsersPageLocators.SEARCH_BY_USER_NAME, email);
        Assert.assertEquals(rows, 1, "Expected exactly one user " + email);
        String deleted = usersPage.deleteUserAtRow(1);
        Assert.assertEquals(deleted, email, "Deleted a different user");
        usersPage.clearSearch();
        Assert.assertEquals(ccsHelper.waitUntilNotListed(() -> usersPage.searchUsers(CCSUsersPageLocators.SEARCH_BY_USER_NAME, email), 20), 0,
                "Agent still listed after delete: " + email);
        usersPage.clearSearch();
        ExtentReportManager.logPass("Deleted agent " + email);
    }

    /** Closes the agent tab (if open) and returns to the admin's CCS-Live tab. */
    private void backToAdminTab() {
        Assert.assertNotNull(adminTab, "CCS-Live admin tab not opened");
        try {
            if (agentLoginOpened) {
                for (String handle : driver.getWindowHandles()) {
                    if (!handle.equals(adminTab) && driver.switchTo().window(handle).getCurrentUrl().contains("/agent/")) {
                        driver.close();
                    }
                }
                agentLoginOpened = false;
            }
        } finally {
            // Always end on the admin tab - never leave the driver pointing at a closed tab
            driver.switchTo().window(adminTab);
        }
    }
}
