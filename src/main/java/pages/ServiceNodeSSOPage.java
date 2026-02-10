package pages;

import locators.ServiceNodeSSOPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for Service Node SSO functionality
 * Handles interactions with Super Admin's Service Node SSO page
 */
public class ServiceNodeSSOPage {
    WebDriver driver;
    WebDriverWait wait;

    public ServiceNodeSSOPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ==================== Navigation Methods ====================

    /**
     * Navigate to Service Node SSO page from dashboard
     */
    public void navigateToServiceNodeSSO() {
        System.out.println("Navigating to Service Node SSO page...");
        try {
            WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(
                    ServiceNodeSSOPageLocators.SERVICE_NODE_SSO_MENU));
            menu.click();

            // Wait for URL to change to SSO page
            Thread.sleep(2000);

            // Validate navigation by URL
            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.contains("service-nodes-sso")) {
                throw new RuntimeException("Failed to navigate to SSO page. Current URL: " + currentUrl);
            }

            Thread.sleep(1000);
            System.out.println("✓ Navigated to Service Node SSO page");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted: " + e.getMessage());
            throw new RuntimeException("Navigation to Service Node SSO failed", e);
        } catch (Exception e) {
            System.err.println("Failed to navigate to Service Node SSO: " + e.getMessage());
            // Retry once
            try {
                System.out.println("Retrying navigation to Service Node SSO...");
                driver.get(driver.getCurrentUrl()); // Refresh
                Thread.sleep(2000);
                WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(
                        ServiceNodeSSOPageLocators.SERVICE_NODE_SSO_MENU));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
                Thread.sleep(2000);
                if (!driver.getCurrentUrl().contains("service-nodes-sso")) {
                    throw new RuntimeException("Navigation retry failed");
                }
                System.out.println("✓ Retry successful");
            } catch (Exception retryEx) {
                throw new RuntimeException("Navigation to Service Node SSO failed after retry", retryEx);
            }
        }
    }

    /**
     * Verify the Service Node SSO page is loaded
     */
    public boolean isPageLoaded() {
        try {
            // Validate by URL since the page doesn't have a visible header
            Thread.sleep(1000);
            String currentUrl = driver.getCurrentUrl();
            boolean loaded = currentUrl.contains("service-nodes-sso");

            if (loaded) {
                System.out.println("✓ Service Nodes SSO page loaded. URL: " + currentUrl);
            }

            return loaded;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Service Discovery Methods ====================

    /**
     * Get list of all available service names
     */
    public List<String> getAvailableServices() {
        System.out.println("Fetching available services...");
        List<String> services = new ArrayList<>();

        try {
            // Wait for services to load
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    ServiceNodeSSOPageLocators.SERVICE_ROWS));
            Thread.sleep(1000);

            List<WebElement> serviceElements = driver.findElements(
                    ServiceNodeSSOPageLocators.SERVICE_NAME_CELLS);

            for (WebElement element : serviceElements) {
                String serviceName = element.getText().trim();
                if (!serviceName.isEmpty()) {
                    services.add(serviceName);
                }
            }

            System.out.println("✓ Found " + services.size() + " service(s): " + services);
        } catch (Exception e) {
            System.err.println("Error fetching services: " + e.getMessage());
        }

        return services;
    }

    /**
     * Get count of available services
     */
    public int getServiceCount() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    ServiceNodeSSOPageLocators.SERVICE_ROWS));
            return driver.findElements(ServiceNodeSSOPageLocators.SERVICE_ROWS).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if a specific service exists
     */
    public boolean isServiceAvailable(String serviceName) {
        List<String> services = getAvailableServices();
        return services.stream().anyMatch(s -> s.equalsIgnoreCase(serviceName));
    }

    // ==================== Service Login Methods ====================

    /**
     * Click Login button for a specific service
     */
    public void clickLoginForService(String serviceName) {
        System.out.println("Clicking Login button for service: " + serviceName);
        try {
            waitForLoading(); // Ensure page is settled
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    ServiceNodeSSOPageLocators.getLoginButtonForService(serviceName)));

            // Scroll into view
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", loginBtn);
            Thread.sleep(500);

            try {
                // Try Actions click first (better for hover effects/overlays)
                new org.openqa.selenium.interactions.Actions(driver)
                        .moveToElement(loginBtn)
                        .pause(Duration.ofMillis(200))
                        .click()
                        .perform();
            } catch (Exception actionEx) {
                // Fallback to standard click
                loginBtn.click();
            }

            Thread.sleep(1000);
            System.out.println("✓ Clicked Login button for: " + serviceName);
        } catch (Exception e) {
            System.out.println("Standard click failed for " + serviceName + ", trying JS click...");
            try {
                WebElement loginBtn = driver
                        .findElement(ServiceNodeSSOPageLocators.getLoginButtonForService(serviceName));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
                Thread.sleep(1000);
                System.out.println("✓ JS Clicked Login button for: " + serviceName);
            } catch (Exception jsEx) {
                System.err.println("Failed to click Login for " + serviceName + ": " + jsEx.getMessage());
                throw new RuntimeException("Login button click failed", jsEx);
            }
        }
    }

    // ==================== Role Selection Methods ====================

    /**
     * Check if role selection modal is displayed
     */
    public boolean isRoleSelectionModalDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    ServiceNodeSSOPageLocators.ROLE_SELECTION_MODAL));
            return driver.findElement(ServiceNodeSSOPageLocators.ROLE_SELECTION_MODAL).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get list of available roles in the modal
     */
    public List<String> getAvailableRoles() {
        System.out.println("Fetching available roles...");
        List<String> roles = new ArrayList<>();

        try {
            // Wait for modal to appear
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    ServiceNodeSSOPageLocators.ROLE_SELECTION_MODAL));
            Thread.sleep(1000);

            // Try to get roles from dropdown first
            try {
                WebElement dropdown = driver.findElement(ServiceNodeSSOPageLocators.ROLE_DROPDOWN);
                dropdown.click();
                Thread.sleep(500);

                List<WebElement> options = driver.findElements(ServiceNodeSSOPageLocators.ROLE_OPTIONS);
                for (WebElement option : options) {
                    String roleText = option.getText().trim();
                    if (!roleText.isEmpty()) {
                        roles.add(roleText);
                    }
                }

                // Close dropdown
                dropdown.click();
            } catch (Exception e) {
                // If dropdown doesn't exist, try radio buttons or list
                List<WebElement> roleElements = driver.findElements(
                        ServiceNodeSSOPageLocators.AVAILABLE_ROLES_LIST);

                // Filter for unique, visible roles
                java.util.Set<String> uniqueRoles = new java.util.LinkedHashSet<>();
                for (WebElement element : roleElements) {
                    if (element.isDisplayed()) {
                        String roleText = element.getText().trim();
                        if (!roleText.isEmpty()) {
                            uniqueRoles.add(roleText);
                        }
                    }
                }
                roles.addAll(uniqueRoles);
            }

            System.out.println("✓ Found " + roles.size() + " role(s): " + roles);
        } catch (Exception e) {
            System.err.println("Error fetching roles: " + e.getMessage());
        }

        return roles;
    }

    /**
     * Select a specific role from the modal
     */
    public void selectRole(String roleName) {
        System.out.println("Selecting role: " + roleName);
        try {
            waitForLoading(); // Ensure no spinners are active
            // Find the visible menu container first to avoid duplicates/hidden elements
            WebElement visibleMenu = null;
            try {
                // Wait for any menu to appear first
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        ServiceNodeSSOPageLocators.ROLE_SELECTION_MODAL));

                List<WebElement> menus = driver.findElements(ServiceNodeSSOPageLocators.ROLE_SELECTION_MODAL);
                for (WebElement menu : menus) {
                    if (menu.isDisplayed()) {
                        visibleMenu = menu;
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Wait for role menu timed out or failed: " + e.getMessage());
            }

            WebElement roleMenuItem = null;

            if (visibleMenu != null) {
                try {
                    // Use relative XPath (.) to search ONLY inside the visible menu
                    String relativeXpath = ".//button[@role='menuitem'][.//span[contains(text(), '" + roleName + "')]]";
                    roleMenuItem = visibleMenu.findElement(By.xpath(relativeXpath));
                    System.out.println("Found role '" + roleName + "' in visible menu");
                } catch (Exception e) {
                    System.out.println("Could not find role in visible menu, falling back to global search");
                }
            }

            if (roleMenuItem == null) {
                // Global fallback
                By roleLocator = ServiceNodeSSOPageLocators.getRoleOption(roleName);
                roleMenuItem = wait.until(ExpectedConditions.elementToBeClickable(roleLocator));

                // Verify this element is actually visible if global search used
                if (!roleMenuItem.isDisplayed()) {
                    System.out.println("⚠ Found role element but it's not displayed. searching for visible one...");
                    List<WebElement> options = driver.findElements(roleLocator);
                    for (WebElement opt : options) {
                        if (opt.isDisplayed()) {
                            roleMenuItem = opt;
                            break;
                        }
                    }
                }
            } else {
                // explicit wait for element found via relative path
                wait.until(ExpectedConditions.elementToBeClickable(roleMenuItem));
            }
            try {
                // Try Actions class move and click first
                new org.openqa.selenium.interactions.Actions(driver)
                        .moveToElement(roleMenuItem)
                        .pause(Duration.ofMillis(200))
                        .click()
                        .perform();

                // Verify if click worked (modal should disappear)
                Thread.sleep(1000);
                if (isRoleSelectionModalDisplayed()) {
                    System.out.println("Actions click validation failed (modal still visible). Forcing JS click...");
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", roleMenuItem);
                }
            } catch (Exception clickEx) {
                System.out.println("Standard/Actions click failed for role, trying JS click...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", roleMenuItem);
            }
            Thread.sleep(1000); // Increased wait after click

            System.out.println("✓ Selected role: " + roleName);
        } catch (

        InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted: " + e.getMessage());
            throw new RuntimeException("Role selection failed", e);
        } catch (Exception e) {
            System.err.println("Failed to select role " + roleName + ": " + e.getMessage());
            throw new RuntimeException("Role selection failed", e);
        }
    }

    /**
     * Click Submit/Continue button in role modal
     */
    public void submitRoleSelection() {
        System.out.println("Submitting role selection...");
        try {
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    ServiceNodeSSOPageLocators.ROLE_MODAL_SUBMIT_BUTTON));
            submitBtn.click();

            // Wait for modal to close and navigation to complete
            Thread.sleep(3000);
            System.out.println("✓ Role selection submitted");
        } catch (Exception e) {
            System.err.println("Failed to submit role: " + e.getMessage());
            throw new RuntimeException("Role submission failed", e);
        }
    }

    /**
     * Complete SSO login for a service with a specific role
     */
    public void performSSOLogin(String serviceName, String roleName) {
        System.out.println("Performing SSO login - Service: " + serviceName + ", Role: " + roleName);

        clickLoginForService(serviceName);

        if (isRoleSelectionModalDisplayed()) {
            selectRole(roleName);
            // Note: Clicking the role directly triggers navigation, no separate submit
            // needed
            try {
                Thread.sleep(3000); // Wait for navigation to complete
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            System.out.println("⚠ No role selection modal appeared - proceeding directly");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("✓ SSO login completed for " + serviceName + " as " + roleName);
    }

    // ==================== Validation Methods ====================

    /**
     * Verify successful redirection to service dashboard
     */
    public boolean isRedirectedToService() {
        try {
            // Wait for URL to change
            Thread.sleep(2000);

            // Check for service dashboard elements
            try {
                wait.withTimeout(Duration.ofSeconds(45))
                        .until(ExpectedConditions.visibilityOfElementLocated(
                                ServiceNodeSSOPageLocators.SERVICE_DASHBOARD));
                return true;
            } catch (Exception e) {
                // Try checking if URL changed from SSO page
                String currentUrl = driver.getCurrentUrl();
                // Also checking for 'dashboard' keyword which is common
                if ((!currentUrl.contains("service-nodes-sso") && !currentUrl.contains("sso"))
                        || currentUrl.contains("dashboard")) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            wait.withTimeout(Duration.ofSeconds(15));
        }
    }

    /**
     * Verify redirection to specific service by checking page title or header
     */
    public boolean isRedirectedToService(String expectedServiceName) {
        System.out.println("Validating redirection to: " + expectedServiceName);
        try {
            Thread.sleep(2000);

            String currentUrl = driver.getCurrentUrl();
            String pageTitle = driver.getTitle();

            System.out.println("Current URL: " + currentUrl);
            System.out.println("Page Title: " + pageTitle);

            // Check if URL or title contains service name
            boolean urlMatch = currentUrl.toLowerCase().contains(expectedServiceName.toLowerCase());
            boolean titleMatch = pageTitle.toLowerCase().contains(expectedServiceName.toLowerCase());

            // Also try to find service-specific header
            boolean headerMatch = false;
            try {
                headerMatch = driver.findElement(
                        ServiceNodeSSOPageLocators.getServiceDashboardHeader(expectedServiceName))
                        .isDisplayed();
            } catch (Exception e) {
                // Header not found
            }

            boolean redirected = urlMatch || titleMatch || headerMatch;
            System.out.println(redirected ? "✓ Successfully redirected to " + expectedServiceName
                    : "✗ Failed to redirect to " + expectedServiceName);

            return redirected;
        } catch (Exception e) {
            System.err.println("Error validating redirection: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get current URL for validation
     */
    public String getCurrentURL() {
        return driver.getCurrentUrl();
    }

    /**
     * Get current page title for validation
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Navigate back to Service Node SSO page (useful after testing a service)
     */
    /**
     * Store the main window handle before performing actions that open new windows
     */
    private String mainWindowHandle;

    public void setMainWindowHandle() {
        this.mainWindowHandle = driver.getWindowHandle();
    }

    /**
     * Navigate back to Service Node SSO page (useful after testing a service)
     */
    public void navigateBackToSSO() {
        System.out.println("Navigating back to Service Node SSO page...");
        try {
            // Check how many windows are open
            if (driver.getWindowHandles().size() > 1) {
                // If we are in a child window/tab, close it and switch back
                System.out.println("Closing child window and returning to main...");
                driver.close();
                if (mainWindowHandle != null) {
                    driver.switchTo().window(mainWindowHandle);
                } else {
                    // Fallback if main handle wasn't set (switch to the last remaining one)
                    for (String handle : driver.getWindowHandles()) {
                        driver.switchTo().window(handle);
                    }
                }
            } else {
                if (mainWindowHandle != null) {
                    try {
                        driver.switchTo().window(mainWindowHandle);
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }

            // Always ensure we are actually on the SSO page
            if (!getCurrentURL().contains("service-nodes-sso")) {
                System.out.println("URL check failed, clicking menu explicitly...");
                navigateToServiceNodeSSO();
            }

            // Wait for page to load
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(ServiceNodeSSOPageLocators.PAGE_HEADER),
                    ExpectedConditions.urlContains("service-nodes-sso")));
            Thread.sleep(1000);

            System.out.println("✓ Back to Service Node SSO page");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Navigation back failed", e);
        } catch (Exception e) {
            System.err.println("Failed to navigate back: " + e.getMessage());
            // Try emergency fallback
            try {
                navigateToServiceNodeSSO();
            } catch (Exception ex) {
                throw new RuntimeException("Navigation back failed", e);
            }
        }
    }

    /**
     * Switch to new window/tab if service opens in new window
     */
    public void switchToNewWindow() {
        try {
            setMainWindowHandle(); // Remember where we came from
            Thread.sleep(2000); // Give time for new window to open

            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(mainWindowHandle)) {
                    driver.switchTo().window(windowHandle);
                    System.out.println("✓ Switched to new window: " + windowHandle);
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Error switching windows: " + e.getMessage());
        }
    }

    /**
     * Close current window and switch back to main window
     */
    public void closeCurrentAndSwitchToMain() {
        try {
            if (driver.getWindowHandles().size() > 1) {
                driver.close();
                Thread.sleep(500);
            }
            if (mainWindowHandle != null) {
                driver.switchTo().window(mainWindowHandle);
                System.out.println("✓ Closed window and switched back to main: " + mainWindowHandle);
            } else {
                // Fallback
                for (String windowHandle : driver.getWindowHandles()) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error closing window: " + e.getMessage());
        }
    }

    // ==================== Utility Methods ====================

    /**
     * Wait for loading to complete
     */
    public void waitForLoading() {
        try {
            wait.withTimeout(Duration.ofSeconds(3))
                    .until(ExpectedConditions.invisibilityOfElementLocated(
                            ServiceNodeSSOPageLocators.LOADING_SPINNER));
        } catch (Exception e) {
            // Loading spinner might not exist or already gone
        } finally {
            wait.withTimeout(Duration.ofSeconds(15));
        }
    }

    /**
     * Search for a specific service
     */
    public void searchService(String serviceName) {
        System.out.println("Searching for service: " + serviceName);
        try {
            WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    ServiceNodeSSOPageLocators.SEARCH_FIELD));
            searchField.clear();
            searchField.sendKeys(serviceName);
            Thread.sleep(1000);
            System.out.println("✓ Search executed");
        } catch (Exception e) {
            System.err.println("Search field not found or error: " + e.getMessage());
        }
    }
}
