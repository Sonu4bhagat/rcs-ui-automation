package tests;

import base.BaseTest;
import helpers.MediaLibraryTestHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.MediaLibraryPage;

public class MediaLibraryTest extends BaseTest {

    MediaLibraryTestHelper helper;
    MediaLibraryPage mediaLibraryPage;

    @BeforeClass
    public void setupOnceForAllTests() {
        helper = new MediaLibraryTestHelper(driver);
        mediaLibraryPage = new MediaLibraryPage(driver);

        // Login and navigate to Media Library
        helper.loginAndNavigateToMediaLibrary();
    }

    @Test(priority = 1)
    public void verifyMediaLibraryPageLoaded() {
        Assert.assertTrue(mediaLibraryPage.isMediaLibraryPageLoaded(),
                "Media Library page did not load correctly");
        System.out.println("Media Library page loaded successfully");
    }

    @Test(priority = 2)
    public void verifyMediaLibraryTabVisible() {
        Assert.assertTrue(mediaLibraryPage.isMediaLibraryTabVisible(),
                "Media Library tab is not visible");
        System.out.println("Media Library tab is visible");
    }
}
