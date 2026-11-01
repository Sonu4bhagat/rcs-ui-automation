package tests.ccs;

import base.BaseTest;
import base.DriverFactory;
import helpers.CCSTestHelper;
import locators.ccs.CCSUsersPageLocators;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.ccs.CCSLivePage;
import pages.ccs.CCSUsersPage;
import utils.ConfigReader;
import utils.ExtentReportManager;
import utils.TestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CCS Part B - Enterprise Users management in CCS-Live.
 * Starts on the CCS-Live dashboard: the class setup logs in with the CCS credentials and SSOs into CCS-Live
 * (the flow verified in Part A, reused through CCSTestHelper - not re-tested here). No logout at the end.
 */
public class CCSEnterpriseUsers extends BaseTest {

    private static final List<String> EXPECTED_ROLES = Arrays.asList("Agent", "Ai Agent", "Team Lead", "Operator",
            "Voice Logger", "Campaign Supervisor");
    private static final List<String> EXPECTED_LIST_COLUMNS = Arrays.asList(CCSUsersPageLocators.COL_CONTACT_NUMBER,
            CCSUsersPageLocators.COL_USER_ID, CCSUsersPageLocators.COL_NAME, CCSUsersPageLocators.COL_USERNAME,
            CCSUsersPageLocators.COL_ROLE, CCSUsersPageLocators.COL_AGENT_TYPE, CCSUsersPageLocators.COL_STATUS,
            CCSUsersPageLocators.COL_ACTION);
    private static final String NAME_MIN_LENGTH_MESSAGE = "Name should be at least 3 characters in length";
    private static final String MOBILE_LENGTH_MESSAGE = "Mobile number must be 10 in length";

    private CCSTestHelper ccsHelper;
    private CCSLivePage ccsLivePage;
    private CCSUsersPage usersPage;

    // Data of the user created by this run
    private String newUserName;
    private String newUserContact;
    private String newUserEmail;
    private boolean userCreated;

    @BeforeClass
    public void openCCSLiveDashboard() {
        ccsHelper = new CCSTestHelper(driver);
        ccsLivePage = new CCSLivePage(driver);
        usersPage = new CCSUsersPage(driver);
        retainSession = true; // stay on the CCS-Live tab between tests
        try {
            ccsHelper.openCCSLiveDashboard();
        } catch (RuntimeException | AssertionError e) {
            // TestNG skips @AfterClass when @BeforeClass fails - close the browser here (same as BaseTest)
            DriverFactory.quitDriver();
            throw e;
        }
    }

    // ==================== Scenario 4: Users Tab ====================

    @Test(priority = 1, description = "CCS-B-01: Users tab is visible and enabled on the CCS-Live dashboard")
    public void testUsersTabVisibleOnDashboard() {
        Assert.assertTrue(ccsLivePage.isLiveCallsCardDisplayed(), "Not on the CCS-Live dashboard");
        Assert.assertTrue(usersPage.isDisplayed(CCSUsersPageLocators.USERS_MENU), "Users tab not visible");
        Assert.assertTrue(usersPage.isEnabled(CCSUsersPageLocators.USERS_MENU), "Users tab not enabled");
    }

    @Test(priority = 2, description = "CCS-B-02: Users tab opens the Users management page with list, search and Add User", dependsOnMethods = "testUsersTabVisibleOnDashboard")
    public void testNavigateToUsersPage() {
        usersPage.openUsersTab();
        Assert.assertTrue(usersPage.isOnUsersList(), "Users page not opened: " + ccsLivePage.getSafeCurrentUrl());

        List<String> headers = usersPage.getTableHeaders();
        System.out.println("Users list columns: " + headers);
        Assert.assertTrue(headers.containsAll(EXPECTED_LIST_COLUMNS), "Users list columns missing. Found: " + headers);
        Assert.assertTrue(usersPage.getRowCount() > 0, "Users list is empty");
        Assert.assertTrue(usersPage.isDisplayed(CCSUsersPageLocators.ADD_USER_BUTTON), "Add User option not displayed");
        Assert.assertTrue(usersPage.isDisplayed(CCSUsersPageLocators.SEARCH_INPUT), "User search not displayed");
        Assert.assertNull(ccsLivePage.findErrorPageText(), "Error page shown on the Users page");
    }

    // ==================== Scenario 8 (part 1): email availability ====================

    // Duplicate check without a Save: the supplied email is looked up in the Users list (only one Save per run,
    // the one that creates the user)
    @Test(priority = 4, description = "CCS-B-10a: Supplied email is used when available, else a unique sonu.bhagat+NNNNNN email is generated", dependsOnMethods = "testNavigateToUsersPage")
    public void testResolveEmailForNewUser() {
        String suppliedEmail = ConfigReader.get("ccs.users.email");
        newUserEmail = ccsHelper.resolveAvailableEmail(usersPage, suppliedEmail);
        System.out.println("Email for the new user: " + newUserEmail);

        if (newUserEmail.equals(suppliedEmail)) {
            ExtentReportManager.logInfo(suppliedEmail + " is not registered - it is used as is");
        } else {
            Assert.assertTrue(newUserEmail.matches("sonu\\.bhagat\\+\\d{6}@altiquence\\.com"),
                    "Generated email does not follow sonu.bhagat+<6 digits>@altiquence.com: " + newUserEmail);
            ExtentReportManager.logInfo(suppliedEmail + " is already registered - generated " + newUserEmail);
        }
        Assert.assertFalse(ccsHelper.isEmailRegistered(usersPage, newUserEmail), "Chosen email is already registered");
    }

    // ==================== Scenario 5: Add User ====================

    @Test(priority = 5, description = "CCS-B-03: Add User opens an empty form with all fields and dropdowns", dependsOnMethods = "testResolveEmailForNewUser", alwaysRun = true)
    public void testOpenAddUserForm() {
        usersPage.clickAddUser();
        Assert.assertTrue(usersPage.isOnAddUserForm(), "Add User form not opened");

        Assert.assertTrue(usersPage.isDisplayed(CCSUsersPageLocators.NAME_INPUT), "Name field missing");
        Assert.assertTrue(usersPage.isDisplayed(CCSUsersPageLocators.CONTACT_NUMBER_INPUT), "Contact Number field missing");
        Assert.assertEquals(usersPage.getSelectedOption(CCSUsersPageLocators.COUNTRY_CODE_SELECT), "91", "Country code");
        Assert.assertTrue(usersPage.isDisplayed(CCSUsersPageLocators.EMAIL_INPUT), "Email field missing");
        Assert.assertTrue(usersPage.isDisplayed(CCSUsersPageLocators.ROLE_SELECT), "Role dropdown missing");
        Assert.assertTrue(usersPage.isDisplayed(CCSUsersPageLocators.STATUS_SELECT), "Status dropdown missing");
        Assert.assertTrue(usersPage.getSaveButton().isEnabled(), "Save button not enabled");

        Assert.assertTrue(usersPage.getFieldValue(CCSUsersPageLocators.NAME_INPUT).isEmpty(), "Name is not empty");
        Assert.assertTrue(usersPage.getFieldValue(CCSUsersPageLocators.CONTACT_NUMBER_INPUT).isEmpty(), "Contact is not empty");
        Assert.assertTrue(usersPage.getFieldValue(CCSUsersPageLocators.EMAIL_INPUT).isEmpty(), "Email is not empty");
    }

    @Test(priority = 6, description = "CCS-B-04: Save on an empty Add User form is blocked by the mandatory fields", dependsOnMethods = "testOpenAddUserForm")
    public void testSaveEmptyFormIsBlocked() {
        usersPage.clickSave();
        String message = usersPage.waitForNotification(5);
        System.out.println("Empty form Save - notification: [" + message + "]");

        Assert.assertTrue(usersPage.isOnAddUserForm(), "Empty form was submitted");
        Assert.assertTrue(usersPage.isFieldInvalid(CCSUsersPageLocators.NAME_INPUT), "Empty Name accepted");
        Assert.assertTrue(usersPage.isFieldInvalid(CCSUsersPageLocators.CONTACT_NUMBER_INPUT), "Empty Contact Number accepted");
        Assert.assertTrue(usersPage.isFieldInvalid(CCSUsersPageLocators.EMAIL_INPUT), "Empty Email accepted");
    }

    // ==================== Scenario 6: Name ====================

    @Test(priority = 7, description = "CCS-B-06: Name - at least 3 characters, maximum 50, valid name accepted and retained", dependsOnMethods = "testSaveEmptyFormIsBlocked", alwaysRun = true)
    public void testNameFieldValidation() {
        for (String invalidName : new String[] { "A", "Ab", "   " }) {
            usersPage.enterName(invalidName);
            Assert.assertTrue(usersPage.isFieldInvalid(CCSUsersPageLocators.NAME_INPUT), "Invalid name accepted: [" + invalidName + "]");
            Assert.assertTrue(usersPage.getFieldError(CCSUsersPageLocators.NAME_INPUT).contains(NAME_MIN_LENGTH_MESSAGE),
                    "Name error message for [" + invalidName + "]: " + usersPage.getFieldError(CCSUsersPageLocators.NAME_INPUT));
        }

        Assert.assertEquals(usersPage.getMaxLength(CCSUsersPageLocators.NAME_INPUT), "50", "Name max length");
        usersPage.enterName(TestUtil.getRandomName(55));
        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.NAME_INPUT).length(), 50,
                "Name longer than 50 characters accepted");

        // Application rule (observed): only the length is validated - digits and special characters are allowed
        for (String allowedName : new String[] { "123", "@#$%" }) {
            usersPage.enterName(allowedName);
            Assert.assertFalse(usersPage.isFieldInvalid(CCSUsersPageLocators.NAME_INPUT),
                    "Name [" + allowedName + "] rejected although only the length is validated");
        }

        newUserName = "CCS Agent " + TestUtil.getRandomName(5);
        usersPage.enterName(newUserName);
        Assert.assertFalse(usersPage.isFieldInvalid(CCSUsersPageLocators.NAME_INPUT), "Valid name rejected: " + newUserName);
        Assert.assertEquals(usersPage.getFieldError(CCSUsersPageLocators.NAME_INPUT), "", "Error shown for a valid name");
        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.NAME_INPUT), newUserName, "Name not retained");
    }

    // ==================== Scenario 7: Contact Number ====================

    @Test(priority = 8, description = "CCS-B-07: Supplied contact number 790319091 is entered and rejected (10 digits required)", dependsOnMethods = "testNameFieldValidation", alwaysRun = true)
    public void testSuppliedContactNumber() {
        String suppliedNumber = ConfigReader.get("ccs.users.contact");
        usersPage.enterContactNumber(suppliedNumber);

        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.CONTACT_NUMBER_INPUT), suppliedNumber,
                "Supplied contact number could not be entered");
        Assert.assertTrue(usersPage.isFieldInvalid(CCSUsersPageLocators.CONTACT_NUMBER_INPUT),
                "Contact number " + suppliedNumber + " (" + suppliedNumber.length() + " digits) accepted - the field requires 10 digits");
        Assert.assertEquals(usersPage.getFieldError(CCSUsersPageLocators.CONTACT_NUMBER_INPUT), MOBILE_LENGTH_MESSAGE,
                "Contact number error message");
    }

    @Test(priority = 9, description = "CCS-B-05: Save with partially completed / invalid details is blocked and entered data is retained", dependsOnMethods = "testSuppliedContactNumber", alwaysRun = true)
    public void testSavePartialFormIsBlockedAndDataRetained() {
        // Valid name, invalid contact number (from CCS-B-07), no email. Guard: never click Save on a form that
        // could be valid - that would create a user
        String contactBefore = usersPage.getFieldValue(CCSUsersPageLocators.CONTACT_NUMBER_INPUT);
        Assert.assertTrue(usersPage.getFieldValue(CCSUsersPageLocators.EMAIL_INPUT).isEmpty()
                && usersPage.isFieldInvalid(CCSUsersPageLocators.CONTACT_NUMBER_INPUT),
                "Precondition not met (email must be empty, contact invalid) - Save not clicked to avoid creating a user");
        usersPage.clickSave();
        String message = usersPage.waitForNotification(5);
        System.out.println("Partial form Save - notification: [" + message + "]");

        Assert.assertTrue(usersPage.isOnAddUserForm(), "Partially completed form was submitted");
        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.NAME_INPUT), newUserName, "Name lost after blocked Save");
        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.CONTACT_NUMBER_INPUT), contactBefore,
                "Contact number lost after blocked Save");
    }

    @Test(priority = 10, description = "CCS-B-08: Contact Number - digits only, maximum 10 digits, valid number accepted and retained", dependsOnMethods = "testSavePartialFormIsBlockedAndDataRetained", alwaysRun = true)
    public void testContactNumberValidation() {
        usersPage.enterContactNumber("abc!@#");
        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.CONTACT_NUMBER_INPUT), "",
                "Letters/special characters accepted in Contact Number");
        Assert.assertTrue(usersPage.isFieldInvalid(CCSUsersPageLocators.CONTACT_NUMBER_INPUT), "Empty Contact Number accepted");

        Assert.assertEquals(usersPage.getMaxLength(CCSUsersPageLocators.CONTACT_NUMBER_INPUT), "10", "Contact max length");
        usersPage.enterContactNumber("12345678901");
        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.CONTACT_NUMBER_INPUT).length(), 10,
                "More than 10 digits accepted");

        newUserContact = TestUtil.getRandomPhoneNumber();
        usersPage.enterContactNumber(newUserContact);
        Assert.assertFalse(usersPage.isFieldInvalid(CCSUsersPageLocators.CONTACT_NUMBER_INPUT),
                "Valid 10-digit contact number rejected: " + newUserContact);
        Assert.assertEquals(usersPage.getFieldError(CCSUsersPageLocators.CONTACT_NUMBER_INPUT), "", "Error shown for a valid number");
        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.CONTACT_NUMBER_INPUT), newUserContact, "Contact not retained");
    }

    // ==================== Scenario 8 (part 2): Email ====================

    @Test(priority = 11, description = "CCS-B-09: Email - mandatory, maximum 50 characters; invalid-format handling recorded", dependsOnMethods = "testContactNumberValidation", alwaysRun = true)
    public void testEmailFieldValidation() {
        List<String> acceptedInvalidEmails = new ArrayList<>();
        try {
            usersPage.enterEmail("x");
            usersPage.enterEmail("");
            Assert.assertTrue(usersPage.isFieldInvalid(CCSUsersPageLocators.EMAIL_INPUT), "Empty Email accepted");

            Assert.assertEquals(usersPage.getMaxLength(CCSUsersPageLocators.EMAIL_INPUT), "50", "Email max length");
            usersPage.enterEmail(TestUtil.getRandomName(45).toLowerCase() + "@altiquence.com"); // 60 characters
            Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.EMAIL_INPUT).length(), 50,
                    "Email longer than 50 characters accepted");

            // Application rule (observed): the form validates only mandatory and length, not the email format
            for (String invalidEmail : new String[] { "abc", "abc@", "abc@x", "abc@@altiquence.com" }) {
                usersPage.enterEmail(invalidEmail);
                if (!usersPage.isFieldInvalid(CCSUsersPageLocators.EMAIL_INPUT)) {
                    acceptedInvalidEmails.add(invalidEmail);
                }
            }
        } finally {
            usersPage.enterEmail(""); // leave the field empty for the next test
        }
        if (!acceptedInvalidEmails.isEmpty()) {
            String issue = "Known application issue - email format is not validated, accepted: " + acceptedInvalidEmails;
            System.out.println("[WARN] " + issue);
            ExtentReportManager.logWarning(issue);
        }
    }

    @Test(priority = 12, description = "CCS-B-10b: Available email is accepted and retained", dependsOnMethods = "testEmailFieldValidation", alwaysRun = true)
    public void testEnterAvailableEmail() {
        usersPage.enterEmail(newUserEmail);
        Assert.assertFalse(usersPage.isFieldInvalid(CCSUsersPageLocators.EMAIL_INPUT), "Available email rejected: " + newUserEmail);
        Assert.assertEquals(usersPage.getFieldValue(CCSUsersPageLocators.EMAIL_INPUT), newUserEmail, "Email not retained");
    }

    // ==================== Scenario 9 / 10: Role and Call Mode ====================

    @Test(priority = 13, description = "CCS-B-11: Role dropdown lists the roles, defaults to Agent, and Agent is selected", dependsOnMethods = "testEnterAvailableEmail", alwaysRun = true)
    public void testSelectRoleAgent() {
        Assert.assertTrue(usersPage.isEnabled(CCSUsersPageLocators.ROLE_SELECT), "Role dropdown not enabled");
        List<String> roles = usersPage.getOptions(CCSUsersPageLocators.ROLE_SELECT);
        System.out.println("Roles: " + roles);
        Assert.assertEquals(roles, EXPECTED_ROLES, "Role options");
        Assert.assertEquals(usersPage.getSelectedOption(CCSUsersPageLocators.ROLE_SELECT), "Agent", "Default role");

        usersPage.selectRole("Agent");
        Assert.assertEquals(usersPage.getSelectedOption(CCSUsersPageLocators.ROLE_SELECT), "Agent", "Agent not selected");
    }

    @Test(priority = 14, description = "CCS-B-12: Call Mode offers WebRTC and Phone; WebRTC is selected and Phone is not", dependsOnMethods = "testSelectRoleAgent", alwaysRun = true)
    public void testSelectCallModeWebRTC() {
        // Call Mode is a pair of checkboxes in this application, not a dropdown
        Assert.assertTrue(usersPage.isDisplayed(CCSUsersPageLocators.CALL_MODE_WEBRTC), "WebRTC call mode not displayed");
        Assert.assertTrue(usersPage.isDisplayed(CCSUsersPageLocators.CALL_MODE_PHONE), "Phone call mode not displayed");

        usersPage.setCallMode(true, false);
        Assert.assertTrue(usersPage.isChecked(CCSUsersPageLocators.CALL_MODE_WEBRTC), "WebRTC not selected");
        Assert.assertFalse(usersPage.isChecked(CCSUsersPageLocators.CALL_MODE_PHONE), "Phone should not be selected");
    }

    // ==================== Scenario 11: Submit ====================

    @Test(priority = 15, description = "CCS-B-13: Save (single click) creates the Agent user", dependsOnMethods = "testSelectCallModeWebRTC", alwaysRun = true)
    public void testSaveCreatesUser() {
        ccsHelper.assertFormHoldsUser(usersPage, newUserName, newUserContact, newUserEmail);
        Assert.assertTrue(usersPage.getSaveButton().isEnabled(), "Save button not enabled");

        usersPage.clickSave(); // single click
        String message = usersPage.waitForSaveOutcome();

        // Up to two recoveries: agent limit (delete the oldest agent ONCE per run, fill the form again) or email
        // registered elsewhere. A second limit message after a deletion fails the test instead of deleting more agents.
        for (int attempt = 1; attempt <= 2 && usersPage.isOnAddUserForm(); attempt++) {
            if (CCSUsersPage.isAgentLimitMessage(message)) {
                ccsHelper.freeAgentSlot(usersPage, message);
                usersPage.clickAddUser();
                ccsHelper.fillAgentForm(usersPage, newUserName, newUserContact, newUserEmail);
                ccsHelper.assertFormHoldsUser(usersPage, newUserName, newUserContact, newUserEmail);
            } else if (CCSUsersPage.isDuplicateEmailMessage(message)) {
                ExtentReportManager.logInfo("Save reported '" + message + "' for " + newUserEmail + " - generating a new email");
                newUserEmail = TestUtil.getUniqueAliasEmail(newUserEmail);
                usersPage.enterEmail(newUserEmail);
            } else {
                break;
            }
            usersPage.clickSave();
            message = usersPage.waitForSaveOutcome();
        }

        System.out.println("Save message: [" + message + "]");
        Assert.assertFalse(usersPage.isOnAddUserForm(), "User was not saved - still on the Add User form. Message: [" + message + "]");
        Assert.assertFalse(message.toLowerCase().matches(".*(error|fail|invalid|already|limit).*"), "Save reported a problem: " + message);
        userCreated = true;
        ExtentReportManager.logPass("User saved: " + newUserEmail + (message.isEmpty() ? "" : " - " + message));
    }

    @Test(priority = 16, description = "CCS-B-14: Created user is listed exactly once with the submitted Name, Contact, Email, Role and Call Mode", dependsOnMethods = "testSaveCreatesUser")
    public void testCreatedUserListedWithDetails() {
        Assert.assertTrue(userCreated, "No user was created by this run");
        ccsHelper.verifyUserListedOnce(usersPage, newUserName, newUserContact, newUserEmail);
    }

    @Test(priority = 17, description = "CCS-B-15: Created user is still listed after refreshing the Users page", dependsOnMethods = "testCreatedUserListedWithDetails", alwaysRun = true)
    public void testCreatedUserPersistsAfterRefresh() {
        Assert.assertTrue(userCreated, "No user was created by this run");
        ccsLivePage.refresh();
        usersPage.waitForUsersList();
        ccsHelper.verifyUserListedOnce(usersPage, newUserName, newUserContact, newUserEmail);
    }

    // Safety net: BaseTest closes the browser in a plain @AfterClass, which TestNG skips when a setup method
    // fails. alwaysRun closes it in every case; quitDriver does nothing when the browser is already closed.
    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        DriverFactory.quitDriver();
    }
}
