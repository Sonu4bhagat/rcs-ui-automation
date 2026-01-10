package locators;

import org.openqa.selenium.By;

public class MediaLibraryLocators {
    public static final By MEDIA_LIBRARY_TAB = By.xpath("//span[contains (text(), 'Media Library')]");
    public static final By ALL_FILES_TAB = By.xpath("//span[contains (text(), 'All Files')]");
    public static final By UPLOAD_MEDIA_BUTTON = By.xpath("//span[@class='button_text']");
    public static final By FILE_INPUT_IN_POPUP = By.xpath("//input[@type='file']");
    public static final By FIRST_MEDIA_NAME = By.xpath("(//div[contains(@class,'card card-sm')])[1]//h6[contains(@class,'card-title')]"); // Adjust locator based on UI
    //public static final By MEDIA_UPLOAD = By.xpath("//input[@type='file']");
}
