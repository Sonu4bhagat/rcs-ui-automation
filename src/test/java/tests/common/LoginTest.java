package tests.common;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.LoginPage;
import utils.ConfigReader;

public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "Login with SuperAdmin")
    public void testSuperAdminLogin() {
        LoginPage login = new LoginPage(driver);
        System.out.println("Step 1: Login with SuperAdmin");
        login.loginWithSuperAdminCredentials();
        System.out.println("Logging out...");
        login.logout();
    }

    @Test(priority = 2, description = "Login with Enterprise User")
    public void testEnterpriseLogin() {
        LoginPage login = new LoginPage(driver);
        System.out.println("Step 2: Login with Enterprise");
        login.loginWithEnterpriseCredentials();
        System.out.println("Logging out...");
        login.logout();
    }

    @Test(priority = 3, description = "Login with Reseller User")
    public void testResellerLogin() {
        LoginPage login = new LoginPage(driver);
        System.out.println("Step 3: Login with Reseller");
        login.loginWithResellerCredentials();
        System.out.println("Logging out...");
        login.logout();
    }

    @Test(priority = 4, description = "Login with Invalid Credentials")
    public void testLoginWithInvalidCredentials() {
        LoginPage login = new LoginPage(driver);
        System.out.println("Step 4: Login with Invalid Credentials");
        login.openFreshLoginForm(ConfigReader.get("url"));

        login.enterUsername("invalid@test.com");
        login.enterPassword("WrongPass123");
        login.clickLoginButton();

        String actualError = login.getInvalidCredentialsErrorMessage("Invalid credentials");
        Assert.assertEquals(actualError, "Invalid credentials", "Error message mismatch");
    }

    @Test(priority = 5, description = "Validate Error: Username present, Password blank")
    public void testLoginWithBlankPassword() {
        LoginPage login = new LoginPage(driver);
        System.out.println("Step 5: Login with Username only");
        login.openFreshLoginForm(ConfigReader.get("url"));

        login.enterUsername("sonu.bhagat+3@altiquence.com");
        login.clickLoginButton();

        String passwordError = login.getPasswordRequiredMessage();
        Assert.assertTrue(passwordError.contains("required"), "Password required message missing");
    }

    @Test(priority = 6, description = "Validate Error: Username blank, Password present")
    public void testLoginWithBlankUsername() {
        LoginPage login = new LoginPage(driver);
        System.out.println("Step 6: Login with Password only");
        login.openFreshLoginForm(ConfigReader.get("url"));

        login.enterPassword("Test@2036");
        login.clickLoginButton();

        String emailError = login.getEmailRequiredMessage();
        Assert.assertTrue(emailError.contains("required"), "Email required message missing");
    }
}
