package helpers;

import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import pages.NavigationPage;

import java.util.Random;

public class AssistantTestHelper {
    WebDriver driver;

    public AssistantTestHelper(WebDriver driver) {

        this.driver = driver;
    }

    public void loginAndNavigateToAssistants() {
        LoginPage loginPage = new LoginPage(driver);
        NavigationPage navigationPage = new NavigationPage(driver);

        loginPage.loginWithEnterpriseCredentials();
        navigationPage.navigateToAssistantList(); // Handles full navigation to Control Center > Assistants
    }
}
