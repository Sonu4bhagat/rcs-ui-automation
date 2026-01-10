package pages;

import locators.AssistantsPageLocators;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.TestUtil;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Random;

public class AssistantsPage {

    WebDriver driver;
    WebDriverWait wait;

    public AssistantsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public WebElement getSearchBox() {
        return driver.findElement(AssistantsPageLocators.SEARCH_BOX);
    }

    public List<WebElement> getAssistantNameCells() {
        return driver.findElements(AssistantsPageLocators.ASSISTANT_NAME_CELLS);
    }

    public WebElement getFirstAssistantName() {
        return driver.findElement(AssistantsPageLocators.FIRST_ASSISTANT_NAME);
    }

    public List<WebElement> getNoAssistantMessage() {
        return driver.findElements(AssistantsPageLocators.NO_ASSISTANT_MESSAGE);
    }

    public void createNewAssistant() throws InterruptedException {
        driver.findElement(AssistantsPageLocators.ADD_NEW_ASSISTANT_BUTTON).click();

        String randomName = TestUtil.getRandomName(6);
        driver.findElement(AssistantsPageLocators.ASSISTANT_NAME_INPUT).sendKeys(randomName);

        driver.findElement(AssistantsPageLocators.ASSISTANT_USERNAME_FIELD).click();

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                AssistantsPageLocators.ASSISTANT_USERNAME_DROPDOWN_OPTIONS));
        if (!options.isEmpty()) {
            options.get(new Random().nextInt(options.size())).click();
        }
        driver.findElement(AssistantsPageLocators.TEMPLATE_CATEGORY).click();

        driver.findElement(AssistantsPageLocators.ASSISTANT_DESCRIPTION_INPUT)
                .sendKeys(TestUtil.getRandomName(20));
        driver.findElement(AssistantsPageLocators.NEXT_BUTTON1).click();

        // ---------------------------------------------------------------------------------------------------------
        // Step 1: Build dynamic absolute path for the image
        String coverImagePath = new File("src/main/resources/image/Cover.jpg").getAbsolutePath();

        // Step 2: Locate the file input element
        WebElement coverImageUpload = driver.findElement(AssistantsPageLocators.COVER_IMAGE_UPLOAD);

        // Step 3: Scroll into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", coverImageUpload);
        Thread.sleep(500); // Optional: wait to ensure scroll has completed

        // Step 4: Send file path to input field (upload file)
        coverImageUpload.sendKeys(coverImagePath);

        // Step 1: Build dynamic absolute path for the icon image
        String iconImagePath = new File("src/main/resources/image/icon.png").getAbsolutePath();

        // Step 2: Locate the file input element for icon upload
        WebElement iconImageUpload = driver.findElement(AssistantsPageLocators.ICON_IMAGE_UPLOAD);

        // Step 3: Scroll into view for better visibility/stability
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", iconImageUpload);
        Thread.sleep(500); // Optional pause

        // Step 4: Upload the icon file using sendKeys
        iconImageUpload.sendKeys(iconImagePath);

        // ---------------------------------------------------------------------------------------------------------

        driver.findElement(AssistantsPageLocators.NEXT_BUTTON2).click();

        driver.findElement(AssistantsPageLocators.DISPLAY_NAME_BUSINESS_PHONE)
                .sendKeys(TestUtil.getRandomName(5));
        driver.findElement(AssistantsPageLocators.MOBILE_NUMBER_FIELD)
                .sendKeys(TestUtil.getRandomPhoneNumber());

        driver.findElement(AssistantsPageLocators.DISPLAY_NAME_WEBSITE)
                .sendKeys(TestUtil.getRandomName(5));
        driver.findElement(AssistantsPageLocators.WEBSITE_URL_FIELD)
                .sendKeys(TestUtil.getRandomUrl());

        driver.findElement(AssistantsPageLocators.DISPLAY_NAME_EMAIL)
                .sendKeys(TestUtil.getRandomName(5));
        driver.findElement(AssistantsPageLocators.EMAIL_ADDRESS_FIELD)
                .sendKeys(TestUtil.getRandomEmail());

        driver.findElement(AssistantsPageLocators.PRIVACY_URL_FIELD)
                .sendKeys(TestUtil.getRandomUrl());
        driver.findElement(AssistantsPageLocators.TERMS_URL_FIELD)
                .sendKeys(TestUtil.getRandomUrl());

        driver.findElement(AssistantsPageLocators.SUBMIT_BUTTON).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(AssistantsPageLocators.SUCCESS_MESSAGE));
    }
}
