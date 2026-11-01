package tests.ccs;

import base.BaseTest;
import base.DriverFactory;
import helpers.CCSTestHelper;
import locators.ccs.CCSQueuePageLocators;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.ccs.CCSLivePage;
import pages.ccs.CCSQueuePage;
import pages.ccs.CCSUsersPage;
import utils.ConfigReader;
import utils.ExtentReportManager;
import utils.TestUtil;

import java.util.Arrays;
import java.util.List;

/**
 * CCS Part C - Enterprise Call Flow: create a Queue with the agent created in Part B and Call Waiting Type "Queue".
 * Starts on the CCS-Live dashboard: the class setup logs in with the CCS credentials and SSOs into CCS-Live
 * (the flow verified in Part A, reused through CCSTestHelper - not re-tested here). No agent is created here.
 * Tests run as one chain in the written order (each depends on the previous one); no logout at the end.
 */
public class CCSEnterpriseCallFlow extends BaseTest {

    private static final String ALGORITHM = "Even Call Distribution";
    private static final String CALL_WAITING_TYPE = "Queue";
    private static final List<String> EXPECTED_LIST_COLUMNS = Arrays.asList(CCSQueuePageLocators.COL_NAME,
            CCSQueuePageLocators.COL_DESCRIPTION, CCSQueuePageLocators.COL_ASSIGNED_AGENTS,
            CCSQueuePageLocators.COL_ALGORITHM, CCSQueuePageLocators.COL_CALL_WAITING_TYPE, CCSQueuePageLocators.COL_ACTION);
    private static final List<String> EXPECTED_ALGORITHMS = Arrays.asList("Select Algorithm", "Even Call Distribution",
            "Random", "Serial Hunting", "Parallel Ringing", "Round Robin");
    private static final List<String> EXPECTED_CALL_WAITING_TYPES = Arrays.asList("Select Waiting Type", "Queue", "Wait Time");
    private static final String NAME_REQUIRED_MESSAGE = "Please enter name";

    private CCSTestHelper ccsHelper;
    private CCSLivePage ccsLivePage;
    private CCSQueuePage queuePage;

    // Agent created by Part B (null when it is missing) and the queue created by this run
    private String partBAgentName;
    private String queueName;
    private String queueDescription;
    private boolean queueCreated;

    @BeforeClass
    public void openCCSLiveDashboard() {
        ccsHelper = new CCSTestHelper(driver);
        ccsLivePage = new CCSLivePage(driver);
        queuePage = new CCSQueuePage(driver);
        retainSession = true; // stay on the CCS-Live tab between tests
        try {
            ccsHelper.openCCSLiveDashboard();
            partBAgentName = ccsHelper.getPartBAgentName(new CCSUsersPage(driver), ConfigReader.get("ccs.users.email"));
        } catch (RuntimeException | AssertionError e) {
            // TestNG skips @AfterClass when @BeforeClass fails - close the browser here (same as BaseTest)
            DriverFactory.quitDriver();
            throw e;
        }
    }

    // ==================== Call Flow and Queue navigation ====================

    @Test(priority = 1, description = "CCS-C-01: Call Flow tab opens the Call Flow section with the Queue option")
    public void testNavigateToCallFlow() {
        queuePage.openCallFlow();
        Assert.assertTrue(driver.getCurrentUrl().contains(CCSQueuePage.CALL_FLOW_PATH),
                "Call Flow not opened: " + ccsLivePage.getSafeCurrentUrl());
        Assert.assertTrue(queuePage.isDisplayed(CCSQueuePageLocators.QUEUE_MENU), "Queue option not shown under Call Flow");
        Assert.assertNull(ccsLivePage.findErrorPageText(), "Error page shown on Call Flow");
    }

    @Test(priority = 2, description = "CCS-C-02: Queue opens the Queue list with columns, search and Add Queue", dependsOnMethods = "testNavigateToCallFlow")
    public void testNavigateToQueuePage() {
        queuePage.openQueueTab();
        Assert.assertTrue(queuePage.isOnQueueList(), "Queue list not opened: " + ccsLivePage.getSafeCurrentUrl());

        List<String> headers = queuePage.getTableHeaders();
        System.out.println("Queue list columns: " + headers);
        Assert.assertTrue(headers.containsAll(EXPECTED_LIST_COLUMNS), "Queue list columns missing. Found: " + headers);
        Assert.assertTrue(queuePage.isDisplayed(CCSQueuePageLocators.ADD_QUEUE_BUTTON), "Add Queue not displayed");
        Assert.assertNull(ccsLivePage.findErrorPageText(), "Error page shown on the Queue list");
    }

    // ==================== Add Queue form ====================

    @Test(priority = 3, description = "CCS-C-03: Add Queue opens an empty form with Name, Description, Agents, Algorithm and Call Waiting Type", dependsOnMethods = "testNavigateToQueuePage")
    public void testOpenAddQueueForm() {
        queuePage.clickAddQueue();
        Assert.assertTrue(queuePage.isOnAddQueueForm(), "Add Queue form not opened");

        Assert.assertTrue(queuePage.isDisplayed(CCSQueuePageLocators.NAME_INPUT), "Name field missing");
        Assert.assertTrue(queuePage.isDisplayed(CCSQueuePageLocators.DESCRIPTION_INPUT), "Description field missing");
        Assert.assertTrue(queuePage.isDisplayed(CCSQueuePageLocators.AGENTS_DROPDOWN_BUTTON), "Agents dropdown missing");
        Assert.assertTrue(queuePage.isDisplayed(CCSQueuePageLocators.ALGORITHM_SELECT), "Queue Call Algorithm missing");
        Assert.assertTrue(queuePage.isDisplayed(CCSQueuePageLocators.CALL_WAITING_TYPE_SELECT), "Call Waiting Type missing");
        Assert.assertTrue(queuePage.getSaveButton().isEnabled(), "Save button not enabled");
        Assert.assertTrue(queuePage.getFieldValue(CCSQueuePageLocators.NAME_INPUT).isEmpty(), "Name is not empty");
        Assert.assertEquals(queuePage.getNameCounter(), "0/50", "Name character counter");
    }

    @Test(priority = 4, description = "CCS-C-04a: Save without a Queue Name is blocked with 'Please enter name'", dependsOnMethods = "testOpenAddQueueForm")
    public void testSaveWithoutNameIsBlocked() {
        queuePage.clickSave();
        String message = queuePage.waitForSaveOutcome();
        Assert.assertTrue(queuePage.isOnAddQueueForm(), "Queue saved without a name");
        Assert.assertTrue(message.contains(NAME_REQUIRED_MESSAGE), "Mandatory name message. Shown: [" + message + "]");
    }

    @Test(priority = 5, description = "CCS-C-04: Queue Name - maximum 50 characters with counter; valid name accepted and retained", dependsOnMethods = "testSaveWithoutNameIsBlocked", alwaysRun = true)
    public void testQueueNameValidation() {
        Assert.assertEquals(queuePage.getAttribute(CCSQueuePageLocators.NAME_INPUT, "maxlength"), "50", "Name max length");
        queuePage.enterName(TestUtil.getRandomName(55));
        Assert.assertEquals(queuePage.getFieldValue(CCSQueuePageLocators.NAME_INPUT).length(), 50, "Name longer than 50 characters accepted");
        Assert.assertEquals(queuePage.getNameCounter(), "50/50", "Name character counter at the limit");

        // Application rule (observed): the Name field has no character pattern - special characters are recorded only
        String pattern = queuePage.getAttribute(CCSQueuePageLocators.NAME_INPUT, "pattern");
        ExtentReportManager.logInfo("Queue Name character rule (pattern attribute): " + (pattern == null || pattern.isEmpty() ? "none" : pattern));

        queueName = "CCS Queue " + TestUtil.getRandomName(5);
        queuePage.enterName(queueName);
        Assert.assertEquals(queuePage.getFieldValue(CCSQueuePageLocators.NAME_INPUT), queueName, "Name not retained");
        Assert.assertEquals(queuePage.getNameCounter(), queueName.length() + "/50", "Name character counter");
    }

    @Test(priority = 6, description = "CCS-C-05: Description is optional free text and is retained", dependsOnMethods = "testQueueNameValidation", alwaysRun = true)
    public void testQueueDescription() {
        Assert.assertNull(queuePage.getAttribute(CCSQueuePageLocators.DESCRIPTION_INPUT, "required"), "Description should be optional");
        queueDescription = "Created by CCS automation " + TestUtil.getRandomName(5);
        queuePage.enterDescription(queueDescription);
        Assert.assertEquals(queuePage.getFieldValue(CCSQueuePageLocators.DESCRIPTION_INPUT), queueDescription, "Description not retained");
    }

    @Test(priority = 7, description = "CCS-C-06: The agent created in Part B is offered and selected", dependsOnMethods = "testQueueDescription", alwaysRun = true)
    public void testSelectPartBAgent() {
        if (partBAgentName == null) {
            throw new SkipException("Prerequisite missing: no agent created by Part B (CCSEnterpriseUsers) - run Part B first");
        }
        List<String> agents = queuePage.getAgentOptions();
        System.out.println("Agents offered: " + agents);
        Assert.assertTrue(agents.stream().anyMatch(a -> a.equalsIgnoreCase(partBAgentName)),
                "Part B agent '" + partBAgentName + "' not offered. Offered: " + agents);

        queuePage.selectAgent(partBAgentName);
        Assert.assertTrue(queuePage.isAgentSelected(partBAgentName), "Part B agent not selected: " + partBAgentName);
    }

    @Test(priority = 8, description = "CCS-C-07: Queue Call Algorithm lists the algorithms and Even Call Distribution is selected", dependsOnMethods = "testSelectPartBAgent", alwaysRun = true)
    public void testSelectAlgorithm() {
        Assert.assertEquals(queuePage.getOptions(CCSQueuePageLocators.ALGORITHM_SELECT), EXPECTED_ALGORITHMS, "Algorithm options");
        queuePage.selectByText(CCSQueuePageLocators.ALGORITHM_SELECT, ALGORITHM);
        Assert.assertEquals(queuePage.getSelectedOption(CCSQueuePageLocators.ALGORITHM_SELECT), ALGORITHM, "Algorithm not selected");
    }

    // ==================== Call Waiting ====================

    @Test(priority = 9, description = "CCS-C-08: Call Waiting Type dropdown lists Queue / Wait Time and Queue is selected", dependsOnMethods = "testSelectAlgorithm", alwaysRun = true)
    public void testSelectCallWaitingTypeQueue() {
        Assert.assertEquals(queuePage.getOptions(CCSQueuePageLocators.CALL_WAITING_TYPE_SELECT), EXPECTED_CALL_WAITING_TYPES,
                "Call Waiting Type options");
        queuePage.selectByText(CCSQueuePageLocators.CALL_WAITING_TYPE_SELECT, CALL_WAITING_TYPE);
        Assert.assertEquals(queuePage.getSelectedOption(CCSQueuePageLocators.CALL_WAITING_TYPE_SELECT), CALL_WAITING_TYPE,
                "Call Waiting Type not selected");
        // Selecting "Queue" shows the queue call waiting settings
        Assert.assertTrue(queuePage.isDisplayed(CCSQueuePageLocators.QUEUE_ATTEMPT_SELECT), "Queue Attempt not shown");
        Assert.assertTrue(queuePage.isDisplayed(CCSQueuePageLocators.REDIRECT_TO_SELECT), "After Attempt Expired Redirect To not shown");
    }

    // ==================== Save ====================

    @Test(priority = 10, description = "CCS-C-09: Save (single click) creates the Queue", dependsOnMethods = "testSelectCallWaitingTypeQueue", alwaysRun = true)
    public void testSaveQueue() {
        // Guard: only save the complete queue entered by the previous tests
        Assert.assertNotNull(partBAgentName, "Prerequisite missing: no Part B agent - queue not saved");
        Assert.assertEquals(queuePage.getFieldValue(CCSQueuePageLocators.NAME_INPUT), queueName, "Name not retained");
        Assert.assertEquals(queuePage.getFieldValue(CCSQueuePageLocators.DESCRIPTION_INPUT), queueDescription, "Description not retained");
        Assert.assertTrue(queuePage.isAgentSelected(partBAgentName), "Agent not retained");
        Assert.assertEquals(queuePage.getSelectedOption(CCSQueuePageLocators.ALGORITHM_SELECT), ALGORITHM, "Algorithm not retained");
        Assert.assertEquals(queuePage.getSelectedOption(CCSQueuePageLocators.CALL_WAITING_TYPE_SELECT), CALL_WAITING_TYPE,
                "Call Waiting Type not retained");

        queuePage.clickSave(); // single click
        String message = queuePage.waitForSaveOutcome();
        System.out.println("Queue Save message: [" + message + "]");
        Assert.assertFalse(queuePage.isOnAddQueueForm(), "Queue was not saved - still on the Add Queue form. Message: [" + message + "]");
        Assert.assertFalse(message.toLowerCase().matches(".*(error|fail|invalid|already|exist|please).*"), "Save reported a problem: " + message);
        queueCreated = true;
        ExtentReportManager.logPass("Queue saved: " + queueName + (message.isEmpty() ? "" : " - " + message));
    }

    @Test(priority = 11, description = "CCS-C-10: Created Queue is listed once with its Description, Algorithm and Call Waiting Type 'Queue'", dependsOnMethods = "testSaveQueue")
    public void testQueueListedWithDetails() {
        Assert.assertTrue(queueCreated, "No queue was created by this run");
        if (!queuePage.isOnQueueList()) {
            queuePage.openQueueTab();
        }
        Assert.assertEquals(queuePage.searchQueuesByName(queueName), 1, "Expected exactly one queue named " + queueName);
        Assert.assertEquals(queuePage.getCellText(1, CCSQueuePageLocators.COL_NAME), queueName, "Name in list");
        Assert.assertEquals(queuePage.getCellText(1, CCSQueuePageLocators.COL_DESCRIPTION), queueDescription, "Description in list");
        Assert.assertEquals(queuePage.getCellText(1, CCSQueuePageLocators.COL_ALGORITHM), ALGORITHM, "Algorithm in list");
        Assert.assertEquals(queuePage.getCellText(1, CCSQueuePageLocators.COL_CALL_WAITING_TYPE), CALL_WAITING_TYPE,
                "Call Waiting Type in list");
    }

    @Test(priority = 12, description = "CCS-C-11: Saved Queue keeps the Part B agent and Call Waiting Type 'Queue' (Edit form, not saved)", dependsOnMethods = "testQueueListedWithDetails", alwaysRun = true)
    public void testQueueSavedValuesInEditForm() {
        Assert.assertTrue(queueCreated, "No queue was created by this run");
        if (!queuePage.isOnQueueList()) {
            queuePage.openQueueTab();
        }
        Assert.assertEquals(queuePage.searchQueuesByName(queueName), 1, "Expected exactly one queue named " + queueName);
        queuePage.clickEditAtRow(1);
        try {
            Assert.assertEquals(queuePage.getFieldValue(CCSQueuePageLocators.NAME_INPUT), queueName, "Saved name");
            Assert.assertEquals(queuePage.getFieldValue(CCSQueuePageLocators.DESCRIPTION_INPUT), queueDescription, "Saved description");
            Assert.assertTrue(queuePage.getAgentOptions().stream().anyMatch(a -> a.equalsIgnoreCase(partBAgentName))
                    && queuePage.isAgentSelected(partBAgentName), "Saved queue does not have the Part B agent " + partBAgentName);
            Assert.assertEquals(queuePage.getSelectedOption(CCSQueuePageLocators.ALGORITHM_SELECT), ALGORITHM, "Saved algorithm");
            Assert.assertEquals(queuePage.getSelectedOption(CCSQueuePageLocators.CALL_WAITING_TYPE_SELECT), CALL_WAITING_TYPE,
                    "Saved Call Waiting Type");
        } finally {
            queuePage.openQueueTab(); // leave the Edit form without saving
        }
    }

    // Safety net: BaseTest closes the browser in a plain @AfterClass, which TestNG skips when a setup method
    // fails. alwaysRun closes it in every case; quitDriver does nothing when the browser is already closed.
    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        DriverFactory.quitDriver();
    }
}
