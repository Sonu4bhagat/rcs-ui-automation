package pages;

import locators.AssistantsPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class AssistantPage {

    private WebDriver driver;

    public AssistantPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean verifyAssistantHeaders() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        return wait.until(ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.HEADER_ASSISTANT_NAME))
                .isDisplayed()
                && wait.until(ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.HEADER_CREATED_BY))
                        .isDisplayed()
                && wait.until(ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.HEADER_CREATED_ON))
                        .isDisplayed()
                && wait.until(ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.HEADER_UPDATED_ON))
                        .isDisplayed()
                && wait.until(ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.HEADER_STATUS))
                        .isDisplayed()
                && wait.until(
                        ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.HEADER_SERVICE_ACCOUNT))
                        .isDisplayed();
    }

    public boolean isNoAssistantMessageDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> message = driver.findElements(AssistantsPageLocators.NO_ASSISTANT_MESSAGE);
        return message.size() > 0 && wait.until(ExpectedConditions.visibilityOf(message.get(0))).isDisplayed();
    }

    public void openEditAssistantForm() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        // wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.FILTER_CLEAR)).click();
        wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.THREE_DOT_MENU)).click();
        wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.EDIT_BUTTON)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.EDIT_ASSISTANT_FORM));
    }

    public String updateAssistantNameAndNext() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        String randomName = "Test Assistant " + new Random().nextInt(1000);
        WebElement nameInput = wait
                .until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.ASSISTANT_NAME_INPUT));
        nameInput.clear();
        nameInput.sendKeys(randomName);

        wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.NEXT_BUTTON1)).click();
        wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.NEXT_BUTTON2)).click();

        return randomName;
    }

    public void updateMobileAndWebsiteAndVerifyPreview(String randomMobile, String randomWebsite) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement mobileInput = wait
                .until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.MOBILE_NUMBER_INPUT));
        mobileInput.clear();
        mobileInput.sendKeys(randomMobile);

        WebElement websiteInput = wait
                .until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.BUSINESS_WEBSITE_INPUT));
        websiteInput.clear();
        websiteInput.sendKeys(randomWebsite);

        wait.until(ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.MOBILE_PREVIEW_SECTION));
        List<WebElement> previewElements = driver.findElements(AssistantsPageLocators.MOBILE_PREVIEW_SECTION);

        boolean isMobileVisible = previewElements.stream().anyMatch(el -> el.getText().contains(randomMobile));
        boolean isWebsiteVisible = previewElements.stream().anyMatch(el -> el.getText().contains(randomWebsite));

        Assert.assertTrue(isMobileVisible, "Mobile number not visible in preview");
        Assert.assertTrue(isWebsiteVisible, "Business website not visible in preview");
    }

    public void saveUpdatedAssistant() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.UPDATE_ASSISTANT_BUTTON)).click();
        wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.GOT_IT_BUTTON)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.FIRST_ASSISTANT_NAME));
    }

    public String waitForUpdatedAssistantName(String expectedName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // wait.until(ExpectedConditions.elementToBeClickable(AssistantsPageLocators.FILTER_CLEAR)).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                AssistantsPageLocators.FIRST_ASSISTANT_NAME, expectedName));
        return driver.findElement(AssistantsPageLocators.FIRST_ASSISTANT_NAME).getText().trim();
    }

    public List<WebElement> getNoAssistantMessage() {
        return driver.findElements(AssistantsPageLocators.NO_ASSISTANT_MESSAGE);
    }

    public List<WebElement> getAssistantNameCells() {
        return driver.findElements(AssistantsPageLocators.ASSISTANT_NAME_CELLS);
    }
}
