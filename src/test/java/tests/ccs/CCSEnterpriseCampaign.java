package tests.ccs;

import helpers.CCSTestHelper;
import locators.ccs.CCSCampaignPageLocators;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.ccs.CCSCampaignPage;
import pages.ccs.CCSLivePage;
import pages.ccs.CCSQueuePage;
import utils.ExtentReportManager;
import utils.TestUtil;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * CCS Part D - Enterprise Campaign: create a "Click to Call - Manual" campaign, schedule it from its Calendar icon
 * and verify it is Running.
 * Starts on the CCS-Live dashboard: the class setup logs in with the CCS credentials and SSOs into CCS-Live
 * (the flow verified in Part A, reused through CCSTestHelper - not re-tested here). The campaign uses the queue
 * created in Part C (it carries the Part B agent); no agent or queue is created here.
 * Tests run as one chain in the written order (each depends on the previous one); no logout at the end.
 */
public class CCSEnterpriseCampaign extends CCSBaseTest {

    private static final String CAMPAIGN_TYPE = "Click to Call - Manual";
    private static final String RUNNING = "Running";
    // Whole-day schedule for today, so the campaign is inside its schedule whenever the suite runs
    private static final String SCHEDULE_START = "12:00 AM";
    private static final String SCHEDULE_END = "11:59 PM";
    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Kolkata");
    private static final int RUNNING_WAIT_SECONDS = 180;

    private CCSTestHelper ccsHelper;
    private CCSLivePage ccsLivePage;
    private CCSCampaignPage campaignPage;

    // Campaign created by this run, reused by the later tests
    private String campaignName;
    private String campaignDescription;
    private String virtualNumber;
    private boolean campaignScheduled;
    // Queue created by Part C (null when it is missing) - a Click to Call - Manual campaign needs a queue
    private String partCQueueName;

    @BeforeClass
    public void openCCSLiveDashboard() {
        ccsHelper = new CCSTestHelper(driver);
        ccsLivePage = new CCSLivePage(driver);
        campaignPage = new CCSCampaignPage(driver);
        retainSession = true; // stay on the CCS-Live tab between tests
        openCCSLiveDashboard(ccsHelper, ccsLivePage);
        partCQueueName = ccsHelper.getPartCQueueName(new CCSQueuePage(driver), "CCS Queue", "Created by CCS automation");
    }

    // ==================== TC-19 / TC-20: Campaign list and Add Campaign ====================

    @Test(priority = 1, description = "TC-19: Campaign tab opens the Campaign list with columns, search and Add Campaign")
    public void testNavigateToCampaignPage() {
        campaignPage.openCampaignTab();
        Assert.assertTrue(campaignPage.isOnCampaignList(), "Campaign list not opened: " + ccsLivePage.getSafeCurrentUrl());

        List<String> headers = campaignPage.getTableHeaders();
        System.out.println("Campaign list columns: " + headers);
        Assert.assertTrue(headers.containsAll(List.of(CCSCampaignPageLocators.COL_CAMPAIGN_NAME, CCSCampaignPageLocators.COL_CAMPAIGN_TYPE,
                CCSCampaignPageLocators.COL_STATUS, CCSCampaignPageLocators.COL_ACTION)), "Campaign list columns missing. Found: " + headers);
        Assert.assertTrue(campaignPage.isDisplayed(CCSCampaignPageLocators.ADD_CAMPAIGN_BUTTON), "Add Campaign not displayed");
        Assert.assertNull(ccsLivePage.findErrorPageText(), "Error page shown on the Campaign list");
    }

    @Test(priority = 2, description = "TC-20: Add Campaign opens an empty form with Name, Description, Campaign Type and Virtual Number", dependsOnMethods = "testNavigateToCampaignPage")
    public void testOpenAddCampaignForm() {
        campaignPage.clickAddCampaign();
        Assert.assertTrue(campaignPage.isOnAddCampaignForm(), "Add Campaign form not opened");

        Assert.assertTrue(campaignPage.isDisplayed(CCSCampaignPageLocators.NAME_INPUT), "Campaign Name missing");
        Assert.assertTrue(campaignPage.isDisplayed(CCSCampaignPageLocators.DESCRIPTION_INPUT), "Campaign Description missing");
        Assert.assertTrue(campaignPage.isDisplayed(CCSCampaignPageLocators.CAMPAIGN_TYPE_SELECT), "Campaign Type missing");
        Assert.assertTrue(campaignPage.isDisplayed(CCSCampaignPageLocators.VIRTUAL_NUMBER_DROPDOWN_BUTTON), "Virtual Number missing");
        Assert.assertTrue(campaignPage.getSaveButton().isEnabled(), "Save button not enabled");
        Assert.assertTrue(campaignPage.getFieldValue(CCSCampaignPageLocators.NAME_INPUT).isEmpty(), "Name is not empty");
        Assert.assertEquals(campaignPage.getNameCounter(), "0/50", "Name character counter");
    }

    // ==================== TC-21 / TC-22: Name and Description ====================

    @Test(priority = 3, description = "TC-21a: Save without a Campaign Name is blocked", dependsOnMethods = "testOpenAddCampaignForm")
    public void testSaveWithoutNameIsBlocked() {
        campaignPage.clickSave();
        String message = campaignPage.waitForSaveOutcome();
        System.out.println("Save without name - message: [" + message + "]");
        Assert.assertTrue(campaignPage.isOnAddCampaignForm(), "Campaign saved without a name");
        Assert.assertFalse(message.isEmpty(), "No message shown for the missing Campaign Name");
    }

    @Test(priority = 4, description = "TC-21: Campaign Name - mandatory, maximum 50 characters with counter; valid name accepted and retained", dependsOnMethods = "testSaveWithoutNameIsBlocked", alwaysRun = true)
    public void testCampaignNameValidation() {
        Assert.assertNotNull(campaignPage.getAttribute(CCSCampaignPageLocators.NAME_INPUT, "required"), "Campaign Name should be mandatory");
        Assert.assertEquals(campaignPage.getAttribute(CCSCampaignPageLocators.NAME_INPUT, "maxlength"), "50", "Name max length");
        campaignPage.enterName(TestUtil.getRandomName(55));
        Assert.assertEquals(campaignPage.getFieldValue(CCSCampaignPageLocators.NAME_INPUT).length(), 50, "Name longer than 50 characters accepted");
        Assert.assertEquals(campaignPage.getNameCounter(), "50/50", "Name character counter at the limit");

        // Application rule (observed): the Name field has no character pattern - special characters are recorded only
        String pattern = campaignPage.getAttribute(CCSCampaignPageLocators.NAME_INPUT, "pattern");
        ExtentReportManager.logInfo("Campaign Name character rule (pattern attribute): " + (pattern == null || pattern.isEmpty() ? "none" : pattern));

        campaignName = "CCS Campaign " + TestUtil.getRandomName(5);
        campaignPage.enterName(campaignName);
        Assert.assertEquals(campaignPage.getFieldValue(CCSCampaignPageLocators.NAME_INPUT), campaignName, "Name not retained");
        Assert.assertEquals(campaignPage.getNameCounter(), campaignName.length() + "/50", "Name character counter");
    }

    @Test(priority = 5, description = "TC-22: Campaign Description is optional free text and is retained", dependsOnMethods = "testCampaignNameValidation", alwaysRun = true)
    public void testCampaignDescription() {
        Assert.assertNull(campaignPage.getAttribute(CCSCampaignPageLocators.DESCRIPTION_INPUT, "required"), "Description should be optional");
        campaignDescription = "Created by CCS automation " + TestUtil.getRandomName(5);
        campaignPage.enterDescription(campaignDescription);
        Assert.assertEquals(campaignPage.getFieldValue(CCSCampaignPageLocators.DESCRIPTION_INPUT), campaignDescription, "Description not retained");
    }

    // ==================== TC-23 / TC-24: Campaign Type and Virtual Number ====================

    @Test(priority = 6, description = "TC-23: Campaign Type 'Click to Call - Manual' is selected", dependsOnMethods = "testCampaignDescription", alwaysRun = true)
    public void testSelectCampaignTypeClickToCallManual() {
        List<String> types = campaignPage.getOptions(CCSCampaignPageLocators.CAMPAIGN_TYPE_SELECT);
        System.out.println("Campaign types: " + types);
        Assert.assertTrue(types.contains(CAMPAIGN_TYPE), "Campaign Type '" + CAMPAIGN_TYPE + "' not offered. Offered: " + types);

        campaignPage.selectByText(CCSCampaignPageLocators.CAMPAIGN_TYPE_SELECT, CAMPAIGN_TYPE);
        Assert.assertEquals(campaignPage.getSelectedOption(CCSCampaignPageLocators.CAMPAIGN_TYPE_SELECT), CAMPAIGN_TYPE, "Campaign Type not selected");
    }

    @Test(priority = 7, description = "TC-24: An assigned Virtual Number is selected", dependsOnMethods = "testSelectCampaignTypeClickToCallManual", alwaysRun = true)
    public void testSelectVirtualNumber() {
        List<String> numbers = campaignPage.getVirtualNumberOptions();
        System.out.println("Virtual numbers offered: " + numbers);
        Assert.assertFalse(numbers.isEmpty(), "No virtual number assigned to the account");

        virtualNumber = numbers.get(0);
        campaignPage.selectVirtualNumber(virtualNumber);
        Assert.assertTrue(campaignPage.isVirtualNumberSelected(virtualNumber), "Virtual number not selected: " + virtualNumber);
    }

    @Test(priority = 8, description = "TC-24b: The mandatory Queue of a Click to Call - Manual campaign is set to the queue created in Part C", dependsOnMethods = "testSelectVirtualNumber", alwaysRun = true)
    public void testSelectPartCQueue() {
        if (partCQueueName == null) {
            throw new SkipException("Prerequisite missing: no queue created by Part C (CCSEnterpriseCallFlow) - run Part C first");
        }
        Assert.assertTrue(campaignPage.isDisplayed(CCSCampaignPageLocators.QUEUE_SELECT), "Select Queue not shown for " + CAMPAIGN_TYPE);
        List<String> queues = campaignPage.getOptions(CCSCampaignPageLocators.QUEUE_SELECT);
        Assert.assertTrue(queues.contains(partCQueueName), "Part C queue '" + partCQueueName + "' not offered. Offered: " + queues);

        campaignPage.selectByText(CCSCampaignPageLocators.QUEUE_SELECT, partCQueueName);
        Assert.assertEquals(campaignPage.getSelectedOption(CCSCampaignPageLocators.QUEUE_SELECT), partCQueueName, "Part C queue not selected");
    }

    // ==================== TC-25: Save and list ====================

    @Test(priority = 9, description = "TC-25: Save (single click) creates the Campaign and it is listed", dependsOnMethods = "testSelectPartCQueue", alwaysRun = true)
    public void testSaveCampaignListed() {
        // Guard: only save the complete campaign entered by the previous tests
        Assert.assertNotNull(partCQueueName, "Prerequisite missing: no Part C queue - campaign not saved");
        Assert.assertEquals(campaignPage.getSelectedOption(CCSCampaignPageLocators.QUEUE_SELECT), partCQueueName, "Queue not retained");
        Assert.assertEquals(campaignPage.getFieldValue(CCSCampaignPageLocators.NAME_INPUT), campaignName, "Name not retained");
        Assert.assertEquals(campaignPage.getFieldValue(CCSCampaignPageLocators.DESCRIPTION_INPUT), campaignDescription, "Description not retained");
        Assert.assertEquals(campaignPage.getSelectedOption(CCSCampaignPageLocators.CAMPAIGN_TYPE_SELECT), CAMPAIGN_TYPE, "Campaign Type not retained");
        Assert.assertTrue(campaignPage.isVirtualNumberSelected(virtualNumber), "Virtual number not retained");

        campaignPage.clickSave(); // single click
        String message = campaignPage.waitForSaveOutcome();
        System.out.println("Campaign Save message: [" + message + "]");
        Assert.assertFalse(campaignPage.isOnAddCampaignForm(), "Campaign was not saved - still on the Add Campaign form. Message: [" + message + "]");
        Assert.assertFalse(message.toLowerCase().matches(".*(error|fail|invalid|already|exist|please).*"), "Save reported a problem: " + message);

        if (!campaignPage.isOnCampaignList()) {
            campaignPage.openCampaignTab();
        }
        Assert.assertEquals(campaignPage.searchCampaignsByName(campaignName), 1, "Expected exactly one campaign named " + campaignName);
        // The Campaign list shows names in Title Case (e.g. "CCS Campaign abcDe" -> "Ccs Campaign Abcde")
        String nameCell = campaignPage.getCellText(1, CCSCampaignPageLocators.COL_CAMPAIGN_NAME);
        Assert.assertTrue(nameCell.equalsIgnoreCase(campaignName), "Name in list: " + nameCell + ", expected: " + campaignName);
        String type = campaignPage.getCellText(1, CCSCampaignPageLocators.COL_CAMPAIGN_TYPE);
        Assert.assertTrue(type.toLowerCase().replaceAll("[^a-z]", "").contains("clicktocall"), "Campaign Type in list: " + type);
        System.out.println("Status after creation: " + campaignPage.getCellText(1, CCSCampaignPageLocators.COL_STATUS));
        CCSTestHelper.rememberCreatedCampaign(campaignName); // reused by Part E (CCSEnterpriseAgentTab)
        ExtentReportManager.logPass("Campaign created: " + campaignName + (message.isEmpty() ? "" : " - " + message));
    }

    // ==================== TC-26: Calendar ====================

    @Test(priority = 10, description = "TC-26: Calendar of the created Campaign is scheduled for today (whole day) and saved", dependsOnMethods = "testSaveCampaignListed")
    public void testScheduleCampaign() {
        Assert.assertEquals(campaignPage.searchCampaignsByName(campaignName), 1, "Expected exactly one campaign named " + campaignName);
        campaignPage.clickCalendarAtRow(1);

        LocalDate today = LocalDate.now(APP_ZONE);
        int weekday = today.getDayOfWeek().getValue() - 1; // Mon = 0 ... Sun = 6, as in the dialog
        campaignPage.selectDateRange(today, today);
        Assert.assertTrue(campaignPage.getDateRangeValue().contains(today.toString()), "Date range: " + campaignPage.getDateRangeValue());

        campaignPage.setWeekdaySchedule(weekday, SCHEDULE_START, SCHEDULE_END);
        Assert.assertTrue(campaignPage.isWeekdayEnabled(weekday), "Today's weekday not enabled");
        List<String> times = campaignPage.getWeekdayTimes(weekday); // shown as "00:00" / "23:59" or "12:00 AM" / "11:59 PM"
        Assert.assertEquals(CCSCampaignPage.parseTime(times.get(0)), CCSCampaignPage.parseTime(SCHEDULE_START), "Today's start time: " + times);
        Assert.assertEquals(CCSCampaignPage.parseTime(times.get(1)), CCSCampaignPage.parseTime(SCHEDULE_END), "Today's end time: " + times);

        String message = campaignPage.saveSchedule();
        System.out.println("Schedule Save message: [" + message + "]");
        Assert.assertFalse(campaignPage.isScheduleDialogOpen(), "Schedule was not saved - dialog still open. Message: [" + message + "]");
        Assert.assertFalse(message.toLowerCase().matches(".*(error|fail|invalid|please).*"), "Schedule Save reported a problem: " + message);
        campaignScheduled = true;
    }

    // ==================== TC-27: Running ====================

    @Test(priority = 11, description = "TC-27: Campaign status becomes Running and stays Running after a page refresh", dependsOnMethods = "testScheduleCampaign")
    public void testCampaignRunning() {
        Assert.assertTrue(campaignScheduled, "Campaign was not scheduled");
        // The status can take a moment to change after scheduling: refresh the list until it shows Running
        String status;
        try {
            status = new WebDriverWait(driver, Duration.ofSeconds(RUNNING_WAIT_SECONDS), Duration.ofSeconds(15)).until(d -> {
                String current = readStatusAfterRefresh();
                System.out.println("Campaign status: " + current);
                return RUNNING.equalsIgnoreCase(current) ? current : null;
            });
        } catch (org.openqa.selenium.TimeoutException e) {
            status = readStatusAfterRefresh();
        }
        Assert.assertEquals(status, RUNNING, "Campaign status after scheduling");

        String afterRefresh = readStatusAfterRefresh();
        Assert.assertEquals(afterRefresh, RUNNING, "Campaign status after refreshing the page");
        ExtentReportManager.logPass("Campaign " + campaignName + " is Running and stays Running after refresh");
    }

    // ==================== Helpers ====================

    private String readStatusAfterRefresh() {
        ccsLivePage.refresh();
        campaignPage.waitForCampaignList();
        Assert.assertEquals(campaignPage.searchCampaignsByName(campaignName), 1, "Expected exactly one campaign named " + campaignName);
        return campaignPage.getCellText(1, CCSCampaignPageLocators.COL_STATUS).trim();
    }
}
