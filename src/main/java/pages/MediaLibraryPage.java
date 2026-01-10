package pages;

import locators.MediaLibraryLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MediaLibraryPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public MediaLibraryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isMediaLibraryTabVisible() {
        try {
            WebElement tab = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(MediaLibraryLocators.MEDIA_LIBRARY_TAB));
            return tab.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickMediaLibraryTab() {
        wait.until(ExpectedConditions.elementToBeClickable(MediaLibraryLocators.MEDIA_LIBRARY_TAB)).click();
    }

    public boolean isMediaLibraryPageLoaded() {
        try {
            // Wait for some element that confirms the Media Library page is loaded
            return wait.until(ExpectedConditions.urlContains("media-library"));
        } catch (Exception e) {
            return false;
        }
    }
}
