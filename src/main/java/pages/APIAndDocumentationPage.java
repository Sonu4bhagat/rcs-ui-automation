package pages;

import locators.APIAndDocumentationPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import utils.ConfigReader;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Set;

/**
 * Page Object for API & Documentation Module
 * Handles all interactions with the API & Documentation page in Super Admin
 * panel
 */
public class APIAndDocumentationPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public APIAndDocumentationPage(WebDriver driver) {
        this.driver = driver;
        // Use longer timeout in headless mode
        int timeout = base.DriverFactory.isHeadlessModeEnabled() ? 20 : 10;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    /**
     * Navigate to API & Documentation module from sidebar
     */
    public void navigateToAPIAndDocumentation() throws InterruptedException {
        try {
            System.out.println("Navigating to API & Documentation...");

            // Wait for page ready and remove blocking iframes in headless mode
            if (base.DriverFactory.isHeadlessModeEnabled()) {
                waitForPageReady();
                removeBlockingIframes();
            }

            WebElement apiDocMenu = wait.until(ExpectedConditions.elementToBeClickable(
                    APIAndDocumentationPageLocators.API_DOC_MENU));

            // Use JavaScript click in headless mode
            if (base.DriverFactory.isHeadlessModeEnabled()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", apiDocMenu);
                Thread.sleep(500);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", apiDocMenu);
                Thread.sleep(3000); // Longer wait for headless
            } else {
                apiDocMenu.click();
                Thread.sleep(1500); // Allow page to load
            }
            System.out.println("Successfully clicked API & Documentation menu");
        } catch (Exception e) {
            System.out.println("Error navigating to API & Documentation: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Check if the API & Documentation page is loaded
     */
    public boolean isPageLoaded() {
        try {
            // Use longer wait in headless mode
            int waitTime = base.DriverFactory.isHeadlessModeEnabled() ? 15 : 10;
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));

            customWait.until(ExpectedConditions.visibilityOfElementLocated(
                    APIAndDocumentationPageLocators.PAGE_TITLE));
            System.out.println("API & Documentation page loaded successfully");
            return true;
        } catch (Exception e) {
            // Try URL-based verification as fallback
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("api-documentation") || currentUrl.contains("api-doc")) {
                System.out.println("API & Documentation page loaded (verified by URL: " + currentUrl + ")");
                return true;
            }
            System.out.println("API & Documentation page did not load: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if API & Documentation tab is visible
     */
    public boolean isAPIDocTabVisible() {
        try {
            WebElement menu = driver.findElement(APIAndDocumentationPageLocators.API_DOC_MENU);
            boolean visible = menu.isDisplayed();
            System.out.println("API & Documentation tab visible: " + visible);
            return visible;
        } catch (Exception e) {
            System.out.println("API & Documentation tab not visible: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if API & Documentation tab is clickable
     */
    public boolean isAPIDocTabClickable() {
        try {
            WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(
                    APIAndDocumentationPageLocators.API_DOC_MENU));
            boolean clickable = menu.isEnabled();
            System.out.println("API & Documentation tab clickable: " + clickable);
            return clickable;
        } catch (Exception e) {
            System.out.println("API & Documentation tab not clickable: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click View Details button for a specific service
     * 
     * @param serviceName - Name of the service (SMS, OBD, CCS, WABA)
     */
    public void clickViewDetails(String serviceName) throws InterruptedException {
        try {
            System.out.println("Clicking View Details for " + serviceName + " service...");

            // First, ensure any previous modal is closed
            closeModalWithRetry();
            Thread.sleep(500);

            // Scroll to the service card to ensure it's visible
            WebElement viewDetailsBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                    APIAndDocumentationPageLocators.getViewDetailsButton(serviceName)));

            // Scroll element into view
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", viewDetailsBtn);
            Thread.sleep(500);

            // Wait for element to be clickable and click
            viewDetailsBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    APIAndDocumentationPageLocators.getViewDetailsButton(serviceName)));

            // Try JavaScript click if regular click fails
            try {
                viewDetailsBtn.click();
            } catch (Exception clickEx) {
                System.out.println("Regular click failed, trying JavaScript click...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewDetailsBtn);
            }

            Thread.sleep(1500); // Allow modal/section to load
            System.out.println("Successfully clicked View Details for " + serviceName);
        } catch (Exception e) {
            System.out.println("Error clicking View Details for " + serviceName + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Check if API Documentation Download button is clickable for a service
     * 
     * @param serviceName - Name of the service
     */
    public boolean isAPIDownloadButtonClickable(String serviceName) {
        try {
            WebElement downloadBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    APIAndDocumentationPageLocators.getDownloadButton(serviceName)));
            boolean clickable = downloadBtn.isEnabled() && downloadBtn.isDisplayed();
            System.out.println("API Download button clickable for " + serviceName + ": " + clickable);
            return clickable;
        } catch (Exception e) {
            // Try generic download button if service-specific not found
            try {
                WebElement genericBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        APIAndDocumentationPageLocators.API_DOC_DOWNLOAD_BUTTON));
                boolean clickable = genericBtn.isEnabled() && genericBtn.isDisplayed();
                System.out.println("Generic API Download button clickable: " + clickable);
                return clickable;
            } catch (Exception ex) {
                System.out.println("API Download button not clickable for " + serviceName + ": " + ex.getMessage());
                return false;
            }
        }
    }

    /**
     * Download API documentation for a service
     * 
     * @param serviceName - Name of the service
     */
    public void downloadAPIDocumentation(String serviceName) throws InterruptedException {
        try {
            System.out.println("Downloading API documentation for " + serviceName + "...");
            WebElement downloadBtn;
            try {
                downloadBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        APIAndDocumentationPageLocators.getDownloadButton(serviceName)));
            } catch (Exception e) {
                // Fallback to generic download button
                downloadBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        APIAndDocumentationPageLocators.API_DOC_DOWNLOAD_BUTTON));
            }
            downloadBtn.click();
            Thread.sleep(3000); // Allow download to complete
            System.out.println("Successfully triggered download for " + serviceName);
        } catch (Exception e) {
            System.out.println("Error downloading documentation for " + serviceName + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get the URL displayed for API documentation (if visible on page)
     */
    public String getAPIDocumentationURL() {
        try {
            WebElement urlElement = driver.findElement(APIAndDocumentationPageLocators.API_URL_DISPLAY);
            String url = urlElement.getText();
            System.out.println("API Documentation URL: " + url);
            return url;
        } catch (Exception e) {
            System.out.println("Could not find API URL display: " + e.getMessage());
            return "";
        }
    }

    /**
     * Verify domain in documentation URL or downloaded file
     * 
     * @param expectedDomain - Expected domain to verify
     */
    public boolean verifyDomainInDocumentation(String expectedDomain) {
        try {
            String url = getAPIDocumentationURL();
            if (!url.isEmpty()) {
                boolean contains = url.contains(expectedDomain);
                System.out.println(
                        "Domain verification: " + contains + " (Expected: " + expectedDomain + ", Found: " + url + ")");
                return contains;
            }

            // If URL not visible on page, check current page URL
            String currentUrl = driver.getCurrentUrl();
            boolean contains = currentUrl.contains(expectedDomain);
            System.out.println("Current URL domain verification: " + contains + " (Expected: " + expectedDomain
                    + ", Current: " + currentUrl + ")");
            return contains;
        } catch (Exception e) {
            System.out.println("Error verifying domain: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click Explore Swagger UI button for a specific service
     * 
     * @param serviceName - Name of the service
     */
    public void clickExploreSwaggerUI(String serviceName) throws InterruptedException {
        try {
            System.out.println("Clicking Explore Swagger UI for " + serviceName + "...");
            WebElement swaggerBtn;
            try {
                swaggerBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        APIAndDocumentationPageLocators.getSwaggerButton(serviceName)));
            } catch (Exception e) {
                // Fallback to generic swagger button
                swaggerBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        APIAndDocumentationPageLocators.EXPLORE_SWAGGER_BUTTON));
            }
            swaggerBtn.click();
            Thread.sleep(2000); // Allow new tab to open
            System.out.println("Successfully clicked Explore Swagger UI for " + serviceName);
        } catch (Exception e) {
            System.out.println("Error clicking Swagger UI for " + serviceName + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Switch to newly opened tab (Swagger UI)
     */
    public void switchToNewTab() throws InterruptedException {
        try {
            System.out.println("Switching to new tab...");
            Set<String> windows = driver.getWindowHandles();
            ArrayList<String> tabs = new ArrayList<>(windows);

            if (tabs.size() > 1) {
                driver.switchTo().window(tabs.get(1));
                Thread.sleep(1500); // Allow tab to load
                System.out.println("Switched to new tab. Current URL: " + driver.getCurrentUrl());
            } else {
                System.out.println("No new tab found");
            }
        } catch (Exception e) {
            System.out.println("Error switching to new tab: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Verify Swagger UI domain is correct
     * 
     * @param serviceName    - Name of the service
     * @param expectedDomain - Expected domain
     */
    public boolean verifySwaggerUIDomain(String serviceName, String expectedDomain) {
        try {
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Current Swagger UI URL: " + currentUrl);

            boolean domainMatches = currentUrl.contains(expectedDomain);
            boolean isSwagger = currentUrl.toLowerCase().contains("swagger") ||
                    currentUrl.toLowerCase().contains("api-docs");

            // Also check for Swagger UI elements on page
            try {
                WebElement swaggerUI = driver.findElement(APIAndDocumentationPageLocators.SWAGGER_UI_HEADER);
                System.out.println("Swagger UI element found on page");
            } catch (Exception e) {
                System.out.println("Swagger UI element not found (may still be valid if URL is correct)");
            }

            boolean verified = domainMatches && isSwagger;
            System.out.println("Swagger UI domain verification for " + serviceName + ": " + verified);
            System.out.println("  - Domain matches (" + expectedDomain + "): " + domainMatches);
            System.out.println("  - Is Swagger URL: " + isSwagger);

            return verified;
        } catch (Exception e) {
            System.out.println("Error verifying Swagger UI domain: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close current tab and switch back to main window
     */
    public void closeCurrentTabAndSwitch() throws InterruptedException {
        try {
            System.out.println("Closing current tab and switching back...");
            driver.close();
            Thread.sleep(500);

            Set<String> windows = driver.getWindowHandles();
            ArrayList<String> tabs = new ArrayList<>(windows);

            if (!tabs.isEmpty()) {
                driver.switchTo().window(tabs.get(0));
                Thread.sleep(1000);
                System.out.println("Switched back to main window");
            }
        } catch (Exception e) {
            System.out.println("Error closing tab: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get current page URL
     */
    public String getCurrentURL() {
        return driver.getCurrentUrl();
    }

    /**
     * Close modal/dialog if open
     */
    public void closeModal() throws InterruptedException {
        try {
            WebElement closeBtn = driver.findElement(APIAndDocumentationPageLocators.MODAL_CLOSE_BUTTON);
            closeBtn.click();
            Thread.sleep(1000);
            System.out.println("Modal closed");
        } catch (Exception e) {
            System.out.println("No modal to close or error closing: " + e.getMessage());
        }
    }

    /**
     * Close modal/dialog with multiple retry attempts
     * More robust than closeModal() - tries multiple locators and methods
     */
    public void closeModalWithRetry() {
        try {
            // Try multiple common close button locators
            By[] closeButtonLocators = {
                    APIAndDocumentationPageLocators.MODAL_CLOSE_BUTTON,
                    By.xpath("//button[contains(@class, 'mat-dialog-close')]"),
                    By.xpath("//button[@mat-dialog-close]"),
                    By.xpath("//mat-icon[text()='close']/parent::button"),
                    By.cssSelector(".mat-dialog-container .close, .mat-dialog-container [aria-label='Close']"),
                    By.xpath("//button[contains(@aria-label, 'close') or contains(@aria-label, 'Close')]"),
                    By.cssSelector("button.close, button[data-dismiss='modal']")
            };

            boolean modalClosed = false;
            for (By locator : closeButtonLocators) {
                try {
                    WebElement closeBtn = new WebDriverWait(driver, Duration.ofSeconds(2))
                            .until(ExpectedConditions.elementToBeClickable(locator));

                    // Try regular click first
                    try {
                        closeBtn.click();
                    } catch (Exception clickEx) {
                        // If regular click fails, try JavaScript click
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
                    }

                    Thread.sleep(500);
                    modalClosed = true;
                    System.out.println("Modal closed successfully");
                    break;
                } catch (Exception e) {
                    // Try next locator
                    continue;
                }
            }

            if (!modalClosed) {
                // Try pressing ESC key as final fallback
                try {
                    driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
                    Thread.sleep(500);
                    System.out.println("Modal closed using ESC key");
                } catch (Exception escEx) {
                    System.out.println("No modal found to close (this is normal if no modal is open)");
                }
            }
        } catch (Exception e) {
            System.out.println("No modal to close (this is normal if no modal is open)");
        }
    }

    /**
     * Navigate back to the main API documentation service list
     * Call this after completing a service test to return to the main list
     */
    public void navigateBackToServiceList() throws InterruptedException {
        try {
            System.out.println("Navigating back to API documentation service list...");

            // Strategy 1: Try to find and click a Back button
            try {
                WebElement backBtn = new WebDriverWait(driver, Duration.ofSeconds(3))
                        .until(ExpectedConditions
                                .elementToBeClickable(APIAndDocumentationPageLocators.BACK_TO_LIST_BUTTON));
                backBtn.click();
                Thread.sleep(1000);
                System.out.println("Clicked Back button to return to service list");
                return;
            } catch (Exception e) {
                // Back button not found, try next strategy
            }

            // Strategy 2: Check if we're on a detail page (URL contains service name)
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("/api-documentation/") &&
                    (currentUrl.contains("/sms") || currentUrl.contains("/obd") ||
                            currentUrl.contains("/ccs") || currentUrl.contains("/waba"))) {

                // We're on a detail page, navigate back using browser back
                driver.navigate().back();
                Thread.sleep(1500);
                System.out.println("Used browser back to return to service list");

                // Wait for the service list to load
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        APIAndDocumentationPageLocators.SERVICE_CARDS_CONTAINER));
                return;
            }

            // Strategy 3: Re-click the API & Documentation menu to reload the main page
            try {
                WebElement apiDocMenu = wait.until(ExpectedConditions.elementToBeClickable(
                        APIAndDocumentationPageLocators.API_DOC_MENU));
                apiDocMenu.click();
                Thread.sleep(1500);
                System.out.println("Re-clicked API & Documentation menu to return to service list");
                return;
            } catch (Exception e) {
                System.out.println("Could not navigate back to service list: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error navigating back to service list: " + e.getMessage());
            // Not critical - we'll try to continue anyway
        }
    }

    /**
     * Wait for loading to complete
     */

    public void waitForLoadingToComplete() throws InterruptedException {
        try {
            Thread.sleep(1000);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    APIAndDocumentationPageLocators.LOADING_SPINNER));
            System.out.println("Loading completed");
        } catch (Exception e) {
            // No loading spinner or already completed
            System.out.println("No loading indicator or already completed");
        }
    }

    /**
     * Wait for page to be fully loaded (for headless mode stability)
     */
    private void waitForPageReady() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
            Thread.sleep(1500); // Extra wait for Angular/React app initialization
        } catch (Exception e) {
            System.out.println("Page ready wait completed: " + e.getMessage());
        }
    }

    /**
     * Remove blocking iframes (like chat widgets) that overlay clickable elements
     */
    private void removeBlockingIframes() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "var iframes = document.querySelectorAll('iframe[id*=\"btj\"], iframe[id*=\"chat\"], iframe[style*=\"z-index\"]');"
                            +
                            "iframes.forEach(function(iframe) {" +
                            "  console.log('Removing blocking iframe:', iframe.id);" +
                            "  iframe.remove();" +
                            "});");
            System.out.println("Removed blocking iframes");
        } catch (Exception e) {
            System.out.println("Could not remove iframes: " + e.getMessage());
        }
    }
}
