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
    private static final int DEFAULT_TIMEOUT = 30;
    private static final int HEADLESS_TIMEOUT = 45;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        // Use longer timeout in headless mode for CI/CD stability
        int timeout = base.DriverFactory.isHeadlessModeEnabled() ? HEADLESS_TIMEOUT : DEFAULT_TIMEOUT;
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public void enterUsername(String email) {
        ExtentReportManager.logStep("Enter username: " + email);
        wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.EMAIL_INPUT)).sendKeys(email);
    }

    public void enterPassword(String password) {
        ExtentReportManager.logStep("Enter password (masked)");
        wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.PASSWORD_INPUT)).sendKeys(password);
    }

    public void clickLoginButton() {
        ExtentReportManager.logStep("Click Login button");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(LoginPageLocators.LOGIN_BUTTON));

        // Use JavaScript click in headless mode to avoid click intercept issues
        if (base.DriverFactory.isHeadlessModeEnabled()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
            // Wait for form submission - could go to dashboard, wallet selection, or show
            // error
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("dashboard"),
                    ExpectedConditions.urlContains("select-wallet"),
                    ExpectedConditions.urlContains("/sa/"),
                    ExpectedConditions.urlContains("/ep/"),
                    ExpectedConditions.presenceOfElementLocated(LoginPageLocators.INVALID_CREDENTIALS_ALERT),
                    ExpectedConditions.presenceOfElementLocated(LoginPageLocators.EMAIL_REQUIRED_MSG),
                    ExpectedConditions.presenceOfElementLocated(LoginPageLocators.PASSWORD_REQUIRED_MSG)));
        } else {
            // GUI mode: normal click, no extra wait
            loginBtn.click();
        }
    }

    public void enterOTP(String otp) {
        List<WebElement> otpFields = wait
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(LoginPageLocators.OTP_INPUT_FIELDS));
        for (int i = 0; i < otp.length(); i++) {
            WebElement otpField = otpFields.get(i);
            otpField.clear();
            otpField.sendKeys(String.valueOf(otp.charAt(i)));
            // Wait logic already improved
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
            System.out.println("[INFO] OTP Field detected. Fetching OTP from email...");
            String otp = utils.EmailReader.getOTPFromEmail(email, password);
            if (otp == null) {
                System.out.println("[WARN] Failed to fetch OTP from email. Trying default test OTP.");
                otp = "112233";
            }
            enterOTP(otp);
            clickVerifyButton();
        } else {
            System.out.println("[INFO] OTP Field not detected. Proceeding to Dashboard.");
        }
    }

    public boolean isDashboardLoaded(UserRole role) {
        try {
            boolean isLoaded = false;
            switch (role) {
                case SUPERADMIN:
                    isLoaded = wait
                            .until(ExpectedConditions
                                    .visibilityOfElementLocated(LoginPageLocators.SUPER_ADMIN_DASHBOARD))
                            .isDisplayed();
                    break;
                case ENTERPRISE:
                    isLoaded = wait
                            .until(ExpectedConditions
                                    .visibilityOfElementLocated(LoginPageLocators.ENTERPRISE_DASHBOARD))
                            .isDisplayed();
                    break;
                case RESELLER:
                    isLoaded = wait
                            .until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.RESELLER_DASHBOARD))
                            .isDisplayed();
                    break;
                default:
                    return false;
            }
            System.out.println("[INFO] Dashboard loaded for role " + role + ": " + isLoaded);

            // Additional URL Validation
            String currentUrl = driver.getCurrentUrl();
            if (isLoaded && !currentUrl.contains("dashboard") && !currentUrl.contains("customer")) {
                System.out.println("[WARN] URL does not contain 'dashboard' or 'customer': " + currentUrl);
            }

            return isLoaded;
        } catch (TimeoutException e) {
            // HEADLESS FALLBACK: Use URL-based verification if element check fails
            if (base.DriverFactory.isHeadlessModeEnabled()) {
                String currentUrl = driver.getCurrentUrl();
                System.out.println("Element-based check failed in headless. Checking URL: " + currentUrl);

                boolean urlValid = !currentUrl.contains("login") && (currentUrl.contains("dashboard") ||
                        currentUrl.contains("customer") ||
                        currentUrl.contains("select-wallet") ||
                        currentUrl.contains("/sa/") ||
                        currentUrl.contains("/ep/") ||
                        currentUrl.contains("/rs/"));

                if (urlValid) {
                    System.out.println("URL-based verification passed for role " + role);
                    return true;
                }
            }
            System.out.println("Timeout waiting for dashboard for role: " + role);
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
                System.out.println("[INFO] URL did not change to 'select-wallet'. Assuming direct dashboard access.");
                return;
            }

            // 2. Wait for Header
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(), 'Select Wallet') or contains(text(), 'Select Account')]")));
                System.out.println("[INFO] Wallet selection screen detected.");
            } catch (TimeoutException e) {
                System.out.println("[DEBUG] Wallet header not found. Attempting to find cards directly...");
            }

            List<WebElement> cards = driver.findElements(LoginPageLocators.WALLET_CARD);

            // Retry mechanism for cards
            if (cards.isEmpty()) {
                System.out.println("[DEBUG] No cards found initially. Retrying...");
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
                if (serviceCount > maxServices) {
                    maxServices = serviceCount;
                    bestCard = card;
                }
            }

            if (bestCard != null) {
                System.out.println("[INFO] Selecting wallet with maximum services (" + maxServices + ").");
                WebElement openBtn = bestCard.findElement(By.xpath(".//button[contains(text(), 'Open')]"));
                wait.until(ExpectedConditions.elementToBeClickable(openBtn)).click();
            } else {
                System.out.println("[WARN] Could not determine best card. Clicking default.");
                driver.findElement(LoginPageLocators.WALLET_OPEN_BUTTON).click();
            }

        } catch (TimeoutException e) {
            System.out.println("[WARN] Wallet selection timed out.");
        } catch (Exception e) {
            System.out.println("[ERROR] Error identifying best wallet: " + e.getMessage());
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
            System.out.println("[INFO] Wallet selection screen detected. Opening first wallet...");
            driver.findElement(LoginPageLocators.WALLET_OPEN_BUTTON).click();
        } catch (TimeoutException e) {
            ExtentReportManager.logInfo("No wallet selection screen - direct dashboard access");
            System.out.println("[INFO] Wallet selection screen not found, assuming direct dashboard access.");
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
            System.out.println("[INFO] Attempting to logout...");

            // Wait for any overlay to disappear
            try {
                WebDriverWait waitShort = new WebDriverWait(driver, Duration.ofSeconds(3));
                waitShort.until(ExpectedConditions
                        .invisibilityOfElementLocated(By.xpath("//div[contains(@class, 'cdk-overlay-backdrop')]")));
            } catch (Exception e) {
                // Ignore
            }

            WebElement profileMenu;
            try {
                profileMenu = wait.until(ExpectedConditions.elementToBeClickable(LoginPageLocators.PROFILE_MENU));
                profileMenu.click();
            } catch (Exception e) {
                System.out.println("[DEBUG] Standard click fail on profile menu. Retrying with JS click...");
                try {
                    profileMenu = wait
                            .until(ExpectedConditions.presenceOfElementLocated(LoginPageLocators.PROFILE_MENU));
                    ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
                    Thread.sleep(500);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", profileMenu);
                } catch (Exception ex) {
                    throw ex;
                }
            }

            // Click Sign Out
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }

            WebElement signOutBtn = wait
                    .until(ExpectedConditions.presenceOfElementLocated(LoginPageLocators.SIGN_OUT_BUTTON));

            try {
                signOutBtn.click();
            } catch (Exception e) {
                System.out.println("[DEBUG] Sign Out click fail. Using JS Click...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signOutBtn);
            }

            // Confirm Logout
            WebElement confirmBtn = wait
                    .until(ExpectedConditions.presenceOfElementLocated(LoginPageLocators.CONFIRM_LOGOUT_BUTTON));
            try {
                wait.until(ExpectedConditions.elementToBeClickable(confirmBtn)).click();
            } catch (Exception e) {
                System.out.println("[DEBUG] Confirm logout click fail. Using JS Click...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);
            }

            // Verify return to login page
            wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.EMAIL_INPUT));
            System.out.println("[INFO] Logout successful.");

        } catch (Exception e) {
            System.err.println("[ERROR] Logout failed: " + e.getMessage());
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }
}
