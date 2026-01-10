package helpers;

import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import pages.NavigationPage;

public class MediaLibraryTestHelper {
    WebDriver driver;

    public MediaLibraryTestHelper(WebDriver driver) {
        this.driver = driver;
    }

    public void loginAndNavigateToMediaLibrary() {
        LoginPage loginPage = new LoginPage(driver);
        NavigationPage navigationPage = new NavigationPage(driver);

        loginPage.loginWithEnterpriseCredentials();
        navigationPage.navigateToMediaLibrary();
    }
}
