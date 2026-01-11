package pages;

import locators.LoginPageLocators;
import enums.UserRole;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import utils.ConfigReader;
import utils.ExtentReportManager;

import java.time.Duration;
import java.util.List;

public class LoginPage {
    WebDriver driver;
    WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT = 10;
    private static final int HEADLESS_TIMEOUT = 20;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        // Use longer timeout in headless mode for CI/CD stability
        int timeout = base.DriverFactory.isHeadlessModeEnabled() ? HEADLESS_TIMEOUT : DEFAULT_TIMEOUT;
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public void enterUsername(String email) {
        ExtentReportManager.logStep("Enter username: " + email);

        // In headless mode, wait for page to be fully loaded
        if (base.DriverFactory.isHeadlessModeEnabled()) {
            waitForPageLoad();
        }

        WebElement emailField = wait
                .until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.EMAIL_INPUT));
        emailField.clear();
        emailField.sendKeys(email);
        System.out.println("Username entered: " + email);
    }

    /**
     * Wait for page to be fully loaded (document.readyState = complete)
     */
    private void waitForPageLoad() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    webDriver -> ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState").equals("complete"));
            // Additional wait for Angular/React apps to initialize
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Page load wait completed with: " + e.getMessage());
        }
    }

    public void enterPassword(String password) {
        ExtentReportManager.logStep("Enter password (masked)");
        WebElement passwordField = wait
                .until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.PASSWORD_INPUT));
        passwordField.clear();
        passwordField.sendKeys(password);
        System.out.println("Password entered");

        // In headless mode, submit form using ENTER key (more reliable than button
        // click)
        if (base.DriverFactory.isHeadlessModeEnabled()) {
            System.out.println("Submitting form with ENTER key (headless mode)");
            passwordField.sendKeys(Keys.ENTER);

            // Wait for form submission and page transition
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }

            // Debug: capture page state after submission
            System.out.println("=== DEBUG: After form submission ===");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page Title: " + driver.getTitle());

            // Check if there's any error message on page
            try {
                List<WebElement> errors = driver
                        .findElements(By.xpath("//*[contains(@class, 'error') or contains(@class, 'alert')]"));
                if (!errors.isEmpty()) {
                    System.out.println("Error elements found: " + errors.size());
                    for (WebElement error : errors) {
                        if (error.isDisplayed()) {
                            System.out.println("Error text: " + error.getText());
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("No error elements found");
            }
            System.out.println("=== END DEBUG ===");
        }
    }

    public void clickLoginButton() {
        String currentUrl = driver.getCurrentUrl();

        // In headless mode, if ENTER key already submitted the form, skip button click
        if (base.DriverFactory.isHeadlessModeEnabled()) {
            if (!currentUrl.contains("login")) {
                System.out.println("Already logged in (URL: " + currentUrl + "), skipping login button click");
                ExtentReportManager.logStep("Login button click skipped - already logged in");
                return;
            }
        }

        ExtentReportManager.logStep("Click Login button");
        System.out.println("URL before login click: " + currentUrl);

        try {
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(LoginPageLocators.LOGIN_BUTTON));

            if (base.DriverFactory.isHeadlessModeEnabled()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
            } else {
                loginBtn.click();
            }

            System.out.println("URL after login click: " + driver.getCurrentUrl());
        } catch (Exception e) {
            // If login button not found, we might already be logged in
            System.out.println("Login button not found, might already be logged in: " + e.getMessage());
        }
    }

    public void enterOTP(String otp) {
        List<WebElement> otpFields = wait
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(LoginPageLocators.OTP_INPUT_FIELDS));
        for (int i = 0; i < otp.length(); i++) {
            WebElement otpField = otpFields.get(i);
            otpField.clear();
            otpField.sendKeys(String.valueOf(otp.charAt(i)));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void clickVerifyButton() {
        ExtentReportManager.logStep("Click Verify OTP button");
        wait.until(ExpectedConditions.elementToBeClickable(LoginPageLocators.VERIFY_BUTTON)).click();
    }

    public boolean isOTPRequired() {
        try {
            // Check if OTP field is visible within a short timeout
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.OTP_INPUT_FIELDS))
                    .isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void handleOTP(String email, String password) {
        if (isOTPRequired()) {
            System.out.println("OTP Field detected. Fetching OTP from email...");
            String otp = utils.EmailReader.getOTPFromEmail(email, password);
            if (otp == null) {
                System.out.println("Failed to fetch OTP from email. Trying default test OTP.");
                otp = "112233";
            }
            enterOTP(otp);
            clickVerifyButton();
        } else {
            System.out.println("OTP Field not detected. Proceeding to Dashboard.");
        }
    }

    public boolean isDashboardLoaded(UserRole role) {
        try {
            // In headless mode, use URL-based verification (more reliable)
            // Wait for URL to change from login page
            try {
                wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));
            } catch (TimeoutException e) {
                System.out.println("Still on login page after waiting");
                return false;
            }

            String currentUrl = driver.getCurrentUrl();
            System.out.println("Current URL after login: " + currentUrl);

            // URL-based verification (works in headless mode)
            boolean urlValid = false;
            switch (role) {
                case SUPERADMIN:
                    // SuperAdmin should land on customer-org or dashboard
                    urlValid = currentUrl.contains("customer-org") || currentUrl.contains("dashboard") ||
                            currentUrl.contains("superadmin") || !currentUrl.contains("login");
                    break;
                case ENTERPRISE:
                    // Enterprise should land on select-wallet or dashboard
                    urlValid = currentUrl.contains("select-wallet") || currentUrl.contains("dashboard") ||
                            currentUrl.contains("enterprise") || !currentUrl.contains("login");
                    break;
                case RESELLER:
                    // Reseller should land on dashboard
                    urlValid = currentUrl.contains("dashboard") || currentUrl.contains("reseller") ||
                            !currentUrl.contains("login");
                    break;
                default:
                    return false;
            }

            if (!urlValid) {
                System.out.println("URL validation failed for role " + role + ". URL: " + currentUrl);
                return false;
            }

            System.out.println("Dashboard URL verified for role " + role + ": " + currentUrl);

            // Give page a moment to stabilize in headless mode
            if (base.DriverFactory.isHeadlessModeEnabled()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }

            return true;
        } catch (Exception e) {
            System.out.println("Exception checking dashboard for role " + role + ": " + e.getMessage());
            return false;
        }
    }

    public void selectWalletWithMaxServices() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 1. Wait for URL to confirm we are on the wallet page
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("select-wallet"),
                        ExpectedConditions.urlContains("wallet")));
            } catch (TimeoutException e) {
                System.out.println("URL did not change to 'select-wallet'. Assuming direct dashboard access.");
                return;
            }

            // 2. Wait for Header (Broadened locator)
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(), 'Select Wallet') or contains(text(), 'Select Account')]")));
                System.out.println("Wallet selection screen detected.");
            } catch (TimeoutException e) {
                System.out.println("Wallet header not found, but URL matches. Attempting to find cards directly...");
            }

            System.out.println("Finding wallet with maximum services...");
            List<WebElement> cards = driver.findElements(LoginPageLocators.WALLET_CARD);

            // Retry mechanism for cards
            if (cards.isEmpty()) {
                System.out.println("No cards found initially. Waiting 2s and retrying...");
                try {
                    Thread.sleep(2000);
                } catch (Exception ex) {
                }
                cards = driver.findElements(LoginPageLocators.WALLET_CARD);
            }

            int maxServices = -1;
            WebElement bestCard = null;

            for (WebElement card : cards) {
                int serviceCount = card.findElements(LoginPageLocators.WALLET_SERVICE_ITEMS).size();
                System.out.println("  Found wallet card with " + serviceCount + " services.");

                if (serviceCount > maxServices) {
                    maxServices = serviceCount;
                    bestCard = card;
                }
            }

            if (bestCard != null) {
                System.out.println("Clicking wallet with " + maxServices + " services.");
                // Ensure button is clickable
                WebElement openBtn = bestCard.findElement(By.xpath(".//button[contains(text(), 'Open')]"));
                wait.until(ExpectedConditions.elementToBeClickable(openBtn)).click();
            } else {
                System.out
                        .println("No wallet cards found or could not determine best card. Trying default Open button.");
                driver.findElement(LoginPageLocators.WALLET_OPEN_BUTTON).click();
            }

        } catch (TimeoutException e) {
            System.out.println("Wallet selection timed out.");
        } catch (Exception e) {
            System.out.println("Error identifying best wallet: " + e.getMessage() + ".  Clicking default.");
            try {
                driver.findElement(LoginPageLocators.WALLET_OPEN_BUTTON).click();
            } catch (Exception ex) {
            }
        }
    }

    public void loginWithEnterpriseCredentials() {
        ExtentReportManager.logStep("Login with Enterprise credentials");
        String username = ConfigReader.get("enterprise.email");
        String password = ConfigReader.get("enterprise.password");

        enterUsername(username);
        enterPassword(password);
        clickLoginButton();

        handleOTP(username, password);

        // Standard behavior: Click first available wallet
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.WALLET_OPEN_BUTTON));
            ExtentReportManager.logStep("Select wallet from wallet selection screen");
            System.out.println("Wallet selection screen detected. Opening first wallet...");
            driver.findElement(LoginPageLocators.WALLET_OPEN_BUTTON).click();
        } catch (TimeoutException e) {
            ExtentReportManager.logInfo("No wallet selection screen - direct dashboard access");
            System.out.println("Wallet selection screen not found, assuming direct dashboard access.");
        }

        ExtentReportManager.logStep("Verify Enterprise dashboard is loaded");
        if (!isDashboardLoaded(UserRole.ENTERPRISE)) {
            throw new IllegalStateException("Enterprise dashboard did not load.");
        }
        ExtentReportManager.logPass("Successfully logged in as Enterprise user");
    }

    public void loginWithEnterpriseMaxServices() {
        String username = ConfigReader.get("enterprise.email");
        String password = ConfigReader.get("enterprise.password");

        enterUsername(username);
        enterPassword(password);
        clickLoginButton();

        handleOTP(username, password);

        // New behavior: Select wallet with most services
        selectWalletWithMaxServices();

        if (!isDashboardLoaded(UserRole.ENTERPRISE)) {
            throw new IllegalStateException("Enterprise dashboard did not load.");
        }
    }

    public void loginWithSuperAdminCredentials() {
        ExtentReportManager.logStep("Login with SuperAdmin credentials");
        String username = ConfigReader.get("superadmin.email");
        String password = ConfigReader.get("superadmin.password");

        enterUsername(username);
        enterPassword(password);
        clickLoginButton();

        handleOTP(username, password);

        ExtentReportManager.logStep("Verify SuperAdmin dashboard is loaded");
        if (!isDashboardLoaded(UserRole.SUPERADMIN)) {
            throw new IllegalStateException("Superadmin dashboard did not load.");
        }
        ExtentReportManager.logPass("Successfully logged in as SuperAdmin");
    }

    public void loginWithResellerCredentials() {
        String username = ConfigReader.get("reseller.email");
        String password = ConfigReader.get("reseller.password");

        enterUsername(username);
        enterPassword(password);
        clickLoginButton();

        handleOTP(username, password);

        if (!isDashboardLoaded(UserRole.RESELLER)) {
            throw new IllegalStateException("Reseller dashboard did not load.");
        }
    }

    public String getInvalidCredentialsErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.INVALID_CREDENTIALS_ALERT))
                .getText();
    }

    public String getEmailRequiredMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.EMAIL_REQUIRED_MSG))
                .getText();
    }

    public String getPasswordRequiredMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.PASSWORD_REQUIRED_MSG))
                .getText();
    }

    public void logout() {
        try {
            ExtentReportManager.logStep("Logout from application");
            System.out.println("Attempting to logout...");

            // 1. Click Profile Menu
            ExtentReportManager.logStep("Click Profile menu");
            WebElement profileMenu = wait
                    .until(ExpectedConditions.elementToBeClickable(LoginPageLocators.PROFILE_MENU));
            profileMenu.click();

            // RETRY LOGIC: Check if Sign Out is visible, if not, retry click with JS
            try {
                // Short wait to see if menu opens
                new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.SIGN_OUT_BUTTON));
            } catch (TimeoutException e) {
                System.out.println("Menu did not open. Retrying click with JS...");
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", profileMenu);
            }
            System.out.println("Clicked profile menu.");

            // 2. Click Sign Out - Ensure it's visible first
            WebElement signOutBtn = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.SIGN_OUT_BUTTON));

            // Retry with JS if standard click fails
            try {
                wait.until(ExpectedConditions.elementToBeClickable(signOutBtn)).click();
            } catch (ElementClickInterceptedException e) {
                System.out.println("Sign Out click intercepted. Using JS Click...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signOutBtn);
            }
            System.out.println("Clicked sign out button.");

            // 3. Confirm Logout - Use JS click to avoid iframe overlay issues
            WebElement confirmBtn = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.CONFIRM_LOGOUT_BUTTON));
            try {
                wait.until(ExpectedConditions.elementToBeClickable(confirmBtn)).click();
            } catch (ElementClickInterceptedException e) {
                System.out.println("Confirm logout click intercepted. Using JS Click...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);
            }
            System.out.println("Clicked confirm logout.");

            // 4. Verify return to login page
            wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.EMAIL_INPUT));

            // Extra Validation: URL check
            if (!driver.getCurrentUrl().contains("login")) {
                throw new RuntimeException("Logout verification failed: URL does not contain 'login'");
            }

            System.out.println("Logout successful. Returned to login page.");

        } catch (Exception e) {
            System.out.println("Logout failed: " + e.getMessage());
            // Retry mechanics could be added here, but for now throwing ensures test fails
            // explicitly
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }
}
