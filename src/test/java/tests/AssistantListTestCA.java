package tests;

import base.BaseTest;
import helpers.AssistantTestHelper;
import locators.AssistantsPageLocators;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.AssistantPage;
import pages.AssistantsPage;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class AssistantListTestCA extends BaseTest {

    AssistantTestHelper helper;
    AssistantPage assistantPage;

    @BeforeClass
    public void setupOnceForAllTests() {
        helper = new AssistantTestHelper(driver);
        assistantPage = new AssistantPage(driver);

        // Login and navigate to Assistants ONLY ONCE
        helper.loginAndNavigateToAssistants();
    }

    @Test(priority = 1)
    public void verifyAssistantHeadersOrNoData() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(AssistantsPageLocators.NO_ASSISTANT_MESSAGE),
                ExpectedConditions.presenceOfElementLocated(AssistantsPageLocators.HEADER_ASSISTANT_NAME)));

        if (assistantPage.isNoAssistantMessageDisplayed()) {
            System.out.println("No assistants found. Message is visible.");
            Assert.assertTrue(true);
        } else {
            System.out.println("Assistants exist. Verifying table headers.");
            Assert.assertTrue(assistantPage.verifyAssistantHeaders(), "All assistant table headers are displayed.");
        }
    }

    @Test(priority = 2)
    public void verifyAssistantSearchFunctionality() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Correct object creation
        AssistantsPage assistantsPage = new AssistantsPage(driver);

        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.FIRST_ASSISTANT_NAME),
                    ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.NO_ASSISTANT_MESSAGE)));

            List<WebElement> noAssistantMessage = assistantsPage.getNoAssistantMessage();
            if (!noAssistantMessage.isEmpty() && noAssistantMessage.get(0).isDisplayed()) {
                System.out.println("No assistant available to perform search.");
                return;
            }

            List<WebElement> assistantNames = assistantsPage.getAssistantNameCells();
            if (assistantNames.isEmpty()) {
                System.out.println("Assistant rows are not visible but no message also.");
                return;
            }

            String assistantName = assistantNames.get(0).getText().trim();
            System.out.println("Searching for assistant: " + assistantName);

            WebElement searchBox = assistantsPage.getSearchBox();
            searchBox.clear();
            searchBox.sendKeys(assistantName);

            Thread.sleep(3000); // loader wait

            List<WebElement> filteredNames = assistantsPage.getAssistantNameCells();
            for (WebElement nameCell : filteredNames) {
                String visibleName = nameCell.getText().trim();
                Assert.assertTrue(
                        visibleName.toLowerCase().contains(assistantName.toLowerCase()),
                        "Search mismatch: expected to contain '" + assistantName + "', but found: " + visibleName);
            }

            System.out.println("Search functionality verified for: " + assistantName);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    public void verifyEditAssistantFunctionality() {
        assistantPage.openEditAssistantForm();

        String randomName = assistantPage.updateAssistantNameAndNext();

        // Generate and update mobile/website, verify in preview
        String randomMobile = "9" + (long) (Math.random() * 1000000000L);
        String randomWebsite = "https://www.example" + new Random().nextInt(1000) + ".com";
        assistantPage.updateMobileAndWebsiteAndVerifyPreview(randomMobile, randomWebsite);

        assistantPage.saveUpdatedAssistant();

        // Validate updated name in list
        String updatedName = assistantPage.waitForUpdatedAssistantName(randomName);
        Assert.assertEquals(updatedName, randomName, "Assistant name was not updated correctly!");
    }

    @Test(priority = 4)
    public void verifyAssistantFilterWithCalendarAndStatus() {
        AssistantPage assistantsPage = new AssistantPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.CALENDAR_ICON)).click();
            wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.LAST_30_DAYS)).click();
            wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.FILTER_BUTTON)).click();
            wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.STATUS_DROPDOWN)).click();

            List<WebElement> statusOptions = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(AssistantsPageLocators.STATUS_OPTIONS));
            if (statusOptions.isEmpty()) {
                Assert.fail("No status options found.");
            }

            WebElement randomStatus = statusOptions.get(new Random().nextInt(statusOptions.size()));
            String selectedStatus = randomStatus.getText().trim();
            System.out.println("Selected status: " + selectedStatus);
            randomStatus.click();

            wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.APPLY_FILTER_BUTTON)).click();

            Thread.sleep(3000); // Replace with loader wait if available

            List<WebElement> noDataMsg = assistantsPage.getNoAssistantMessage();
            if (!noDataMsg.isEmpty() && noDataMsg.get(0).isDisplayed()) {
                System.out.println("No data available for selected filters.");
            } else {
                List<WebElement> filteredRows = assistantsPage.getAssistantNameCells();
                Assert.assertTrue(!filteredRows.isEmpty(), "Filtered rows should not be empty.");
                System.out.println("Filtered rows found for status: " + selectedStatus);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void verifyAddNewAssistantFunctionality() throws InterruptedException {
        AssistantsPage assistantsPage = new AssistantsPage(driver);
        assistantsPage.createNewAssistant();
    }
}
