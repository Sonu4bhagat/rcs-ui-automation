package tests.ccs;

import base.CCSBaseTest;
import helpers.CCSTestHelper;
import locators.ccs.CCSUsersPageLocators;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.ccs.CCSCampaignPage;
import pages.ccs.CCSLivePage;
import pages.ccs.CCSQueuePage;
import pages.ccs.CCSUsersPage;
import utils.ConfigReader;
import utils.ExtentReportManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CCS cleanup - deletes the test data created by THIS execution: the Part D campaign, the Part C queue and the
 * Part B agent, identified by the exact names / email those parts recorded (CCSTestHelper). Nothing else is deleted.
 * Runs after CCSEnterpriseAgentTab (ccs.xml order) and only when all its tests passed; set
 * -Dccs.cleanup.onFailure=true to clean up after a failed run as well (failure recovery).
 * Dependency order: campaign (uses the queue), queue (holds the agent), agent. Each deletion is verified.
 */
public class CCSCleanup extends CCSBaseTest {

    private CCSTestHelper ccsHelper;
    private CCSLivePage ccsLivePage;
    private CCSUsersPage usersPage;

    // Outcome per record, printed as the cleanup report at the end
    private final Map<String, String> report = new LinkedHashMap<>();
    private boolean campaignRemoved = true;
    private boolean queueRemoved = true;

    @BeforeClass
    public void openCCSLiveDashboard() {
        boolean failureRecovery = Boolean.parseBoolean(ConfigReader.get("ccs.cleanup.onFailure"));
        if (!CCSTestHelper.isAgentTabCompleted() && !failureRecovery) {
            report.put("Cleanup", "NOT RUN - CCSEnterpriseAgentTab did not complete successfully in this execution"
                    + " (use -Dccs.cleanup.onFailure=true to clean up anyway)");
            throw new SkipException(report.get("Cleanup"));
        }
        ccsHelper = new CCSTestHelper(driver);
        ccsLivePage = new CCSLivePage(driver);
        usersPage = new CCSUsersPage(driver);
        retainSession = true; // stay on the CCS-Live tab between tests
        String ccsTab = openCCSLiveDashboard(ccsHelper::openCCSLiveDashboard, ccsLivePage);
        closeAgentTabs(ccsTab);
    }

    // ==================== 1. Campaign (uses the queue) ====================

    @Test(priority = 1, description = "Cleanup 1/3: Delete the campaign created by Part D in this execution")
    public void testDeleteCampaign() {
        String name = CCSTestHelper.getCreatedCampaignName();
        if (name == null) {
            report.put("Campaign", "SKIPPED - none created in this execution");
            throw new SkipException("No campaign was created by Part D in this execution - nothing to delete");
        }
        campaignRemoved = false;
        CCSCampaignPage campaignPage = new CCSCampaignPage(driver);
        campaignPage.openCampaignTab();
        String messages = campaignPage.deleteCampaign(name);
        System.out.println("Delete campaign " + name + ": " + messages);
        // The Campaign list keeps showing a deleted campaign until the page is reloaded - reload before each check
        int stillListed = ccsHelper.waitUntilNotListed(() -> {
            ccsLivePage.refresh();
            campaignPage.waitForCampaignList();
            return campaignPage.searchCampaignsByName(name);
        }, 60);
        report.put("Campaign " + name, stillListed == 0 ? "DELETED" : "NOT DELETED - still listed (" + messages + ")");
        Assert.assertEquals(stillListed, 0, "Campaign still listed after delete: " + name + " (" + messages + ")");
        campaignRemoved = true;
        ExtentReportManager.logPass("Deleted campaign " + name);
    }

    // ==================== 2. Queue (holds the agent) ====================

    @Test(priority = 2, description = "Cleanup 2/3: Delete the queue created by Part C in this execution", dependsOnMethods = "testDeleteCampaign", alwaysRun = true)
    public void testDeleteQueue() {
        String name = CCSTestHelper.getCreatedQueueName();
        if (name == null) {
            report.put("Queue", "SKIPPED - none created in this execution");
            throw new SkipException("No queue was created by Part C in this execution - nothing to delete");
        }
        if (!campaignRemoved) {
            queueRemoved = false;
            report.put("Queue " + name, "NOT DELETED - this execution's campaign still exists and uses it");
            throw new SkipException("Queue " + name + " kept: the campaign using it could not be deleted");
        }
        queueRemoved = false;
        CCSQueuePage queuePage = new CCSQueuePage(driver);
        queuePage.openCallFlow();
        queuePage.openQueueTab();
        String messages = queuePage.deleteQueue(name);
        System.out.println("Delete queue " + name + ": " + messages);
        int stillListed = ccsHelper.waitUntilNotListed(() -> queuePage.searchQueuesByName(name), 20);
        report.put("Queue " + name, stillListed == 0 ? "DELETED" : "NOT DELETED - still listed (" + messages + ")");
        Assert.assertEquals(stillListed, 0, "Queue still listed after delete: " + name + " (" + messages + ")");
        queueRemoved = true;
        ExtentReportManager.logPass("Deleted queue " + name);
    }

    // ==================== 3. Agent ====================

    @Test(priority = 3, description = "Cleanup 3/3: Delete the agent created by Part B in this execution", dependsOnMethods = "testDeleteQueue", alwaysRun = true)
    public void testDeleteAgent() {
        String email = CCSTestHelper.getCreatedAgentEmail();
        if (email == null) {
            report.put("Agent", "SKIPPED - none created in this execution");
            throw new SkipException("No agent was created by Part B in this execution - nothing to delete");
        }
        if (!queueRemoved) {
            report.put("Agent " + email, "NOT DELETED - this execution's queue still exists and holds it");
            throw new SkipException("Agent " + email + " kept: the queue holding it could not be deleted");
        }
        usersPage.openUsersTab();
        int rows = usersPage.searchUsers(CCSUsersPageLocators.SEARCH_BY_USER_NAME, email);
        Assert.assertEquals(rows, 1, "Expected exactly one user " + email);
        String deleted = usersPage.deleteUserAtRow(1);
        Assert.assertEquals(deleted, email, "Deleted a different user");
        usersPage.clearSearch();
        int stillListed = ccsHelper.waitUntilNotListed(() -> usersPage.searchUsers(CCSUsersPageLocators.SEARCH_BY_USER_NAME, email), 20);
        usersPage.clearSearch();
        report.put("Agent " + email, stillListed == 0 ? "DELETED" : "NOT DELETED - still listed");
        Assert.assertEquals(stillListed, 0, "Agent still listed after delete: " + email);
        ExtentReportManager.logPass("Deleted agent " + email);
    }

    // ==================== Report ====================

    @AfterClass(alwaysRun = true)
    public void printCleanupReport() {
        System.out.println("\n========== CCS CLEANUP REPORT ==========");
        if (report.isEmpty()) {
            System.out.println("Cleanup did not run");
        }
        report.forEach((record, outcome) -> System.out.println(record + ": " + outcome));
        System.out.println("========================================\n");
    }

    /** Closes the Agent Live Dashboard tab(s) Part E opened and returns to the CCS-Live admin tab. */
    private void closeAgentTabs(String ccsTab) {
        try {
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(ccsTab) && driver.switchTo().window(handle).getCurrentUrl().contains("/agent/")) {
                    driver.close();
                }
            }
        } finally {
            driver.switchTo().window(ccsTab);
        }
    }
}
