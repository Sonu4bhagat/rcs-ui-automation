package pages;

import locators.ServicesPageLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import java.util.Set;

public class ServicesPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public ServicesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void clickServicesTab() {
        System.out.println("Clicking on Services Tab...");
        System.out.println("Current URL before: " + driver.getCurrentUrl());

        try {
            // Robustly wait for sidebar/header to ensure we are on a page with the menu
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.PAGE_HEADER),
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(@class, 'sidenav') or contains(@class, 'menu')]"))));

            WebElement servicesTab = wait
                    .until(ExpectedConditions.elementToBeClickable(ServicesPageLocators.SERVICES_TAB));
            servicesTab.click();
        } catch (Exception e) {
            System.out.println("Standard click failed/timed out. Trying JS Click...");
            try {
                WebElement servicesTab = driver.findElement(ServicesPageLocators.SERVICES_TAB);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", servicesTab);
                Thread.sleep(500);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", servicesTab);
            } catch (Exception ex) {
                System.out.println("JS Click also failed: " + ex.getMessage());
                throw new RuntimeException("Failed to click Services tab", ex);
            }
        }

        System.out.println("Clicked on Services Tab.");

        // Wait for URL to contain 'services' or for Services page header to appear
        try {
            // Wait for URL change first
            WebDriverWait urlWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            urlWait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("services"),
                    ExpectedConditions.urlContains("service"),
                    ExpectedConditions.presenceOfElementLocated(ServicesPageLocators.PAGE_HEADER)));
            System.out.println("Current URL after: " + driver.getCurrentUrl());

            // Additional wait for page content to fully load
            Thread.sleep(2000);

            // Verify we're on services page - check for service cards or page header
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(ServicesPageLocators.PAGE_HEADER),
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'card')]")),
                        ExpectedConditions.presenceOfElementLocated(By.xpath(
                                "//*[contains(text(), 'SMS') or contains(text(), 'RCS') or contains(text(), 'WABA')]"))));
                System.out.println("Services page loaded successfully - service cards or header found.");
            } catch (Exception e) {
                System.out.println("Warning: Could not verify service cards/header but continuing...");
            }

        } catch (Exception e) {
            System.out.println("Warning: URL/page validation timeout, but click was successful. Current URL: "
                    + driver.getCurrentUrl());
        }
    }

    // New: Navigate to specific service (SMS, RCS, etc.) from the 'Services Home'
    // cards
    public void clickServiceCardViewDetails(String serviceName) {
        System.out.println("Looking for View Details button for service: " + serviceName);
        System.out.println("Current URL: " + driver.getCurrentUrl());

        // Remove any blocking iframes (chat widgets, etc.) in headless mode
        if (base.DriverFactory.isHeadlessModeEnabled()) {
            removeBlockingIframes();
        }

        // Wait for page to fully load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        // Print page source snippet for debugging (first 500 chars)
        try {
            String pageSource = driver.getPageSource();
            if (pageSource.toLowerCase().contains(serviceName.toLowerCase())) {
                System.out.println("Page contains '" + serviceName + "' text - good sign!");
            } else {
                System.out.println("WARNING: Page does NOT contain '" + serviceName + "' text!");
            }
        } catch (Exception e) {
            System.out.println("Could not check page source: " + e.getMessage());
        }

        try {
            By locator = ServicesPageLocators.getViewDetailsButtonForService(serviceName);
            System.out.println("Using locator: " + locator.toString());

            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(locator));
            System.out.println("Found button: " + button.getText());

            // Use JavaScript click in headless mode to avoid overlay issues
            if (base.DriverFactory.isHeadlessModeEnabled()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            } else {
                button.click();
            }
            System.out.println("Clicked View Details for service: " + serviceName);
        } catch (Exception e) {
            System.out.println("ERROR: Could not find/click View Details for " + serviceName);
            System.out.println("Error details: " + e.getMessage());

            // Try alternative approach: click any card button with JS
            try {
                System.out.println("Trying alternative: finding any card with service name...");
                List<WebElement> allCards = driver.findElements(By.xpath("//div[contains(@class, 'card')]"));
                System.out.println("Found " + allCards.size() + " cards on page");

                for (WebElement card : allCards) {
                    String cardText = card.getText();
                    if (cardText.toLowerCase().contains(serviceName.toLowerCase())) {
                        System.out.println("Found card containing '" + serviceName + "': "
                                + cardText.substring(0, Math.min(100, cardText.length())));
                        List<WebElement> buttons = card.findElements(By.tagName("button"));
                        for (WebElement btn : buttons) {
                            if (btn.getText().toLowerCase().contains("view")) {
                                System.out.println("Clicking button with JS: " + btn.getText());
                                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                                return;
                            }
                        }
                        // If no View button, click the first button
                        if (!buttons.isEmpty()) {
                            System.out.println("Clicking first button in card with JS: " + buttons.get(0).getText());
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", buttons.get(0));
                            return;
                        }
                    }
                }
                throw new RuntimeException("Could not find card for service: " + serviceName);
            } catch (Exception ex) {
                System.out.println("Alternative approach also failed: " + ex.getMessage());
                throw new RuntimeException("Failed to click View Details for " + serviceName + ": " + ex.getMessage());
            }
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

    public void searchService(String searchText) {
        WebElement searchInput = wait
                .until(ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.SEARCH_INPUT));
        searchInput.clear();
        searchInput.sendKeys(searchText);
        System.out.println("Searched for: " + searchText);
        // Wait for results to update (increased wait time for search results)
        try {
            Thread.sleep(3000);
            // Wait for table to refresh
            wait.until(ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.TABLE_ROWS));
        } catch (InterruptedException e) {
        } catch (Exception e) {
            System.out.println("Warning: Search results may not have fully loaded.");
        }
    }

    public void applyActiveFilter() {
        wait.until(ExpectedConditions.elementToBeClickable(ServicesPageLocators.FILTER_BUTTON)).click();
        System.out.println("Clicked filter button.");
        // Wait for filter dropdown to be visible
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        WebElement activeCheckbox = wait
                .until(ExpectedConditions.elementToBeClickable(ServicesPageLocators.STATUS_FILTER_ACTIVE));
        if (!activeCheckbox.isSelected()) {
            activeCheckbox.click();
            System.out.println("Selected Active filter checkbox.");
        }
        wait.until(ExpectedConditions.elementToBeClickable(ServicesPageLocators.APPLY_FILTER_BTN)).click();
        System.out.println("Clicked Apply filter button.");
        // Wait for table to refresh after filter application
        try {
            Thread.sleep(3000);
            // Verify table is refreshed
            wait.until(ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.TABLE_ROWS));
        } catch (InterruptedException e) {
        } catch (Exception e) {
            System.out.println("Warning: Filter may not have been fully applied.");
        }
    }

    public int getRowCount() {
        return driver.findElements(ServicesPageLocators.TABLE_ROWS).size();
    }

    public String getServiceName(int rowIndex) {
        return driver.findElement(ServicesPageLocators.getServiceNameByRow(rowIndex)).getText();
    }

    public String getServiceStatus(int rowIndex) {
        try {
            return driver.findElement(ServicesPageLocators.getStatusBadgeByRow(rowIndex)).getText();
        } catch (Exception e) {
            System.out.println("Warning: Status badge not found for row " + rowIndex + ". Error: " + e.getMessage());
            return "N/A"; // Return N/A if status badge doesn't exist
        }
    }

    public void clickViewIcon(int rowIndex) {
        WebElement viewIcon = wait.until(
                ExpectedConditions.elementToBeClickable(ServicesPageLocators.getViewIconByRow(rowIndex)));
        viewIcon.click();
        System.out.println("Clicked View Icon for row " + rowIndex);
        // Wait for details page to load
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
    }

    public void clickSSOIcon(int rowIndex) {
        WebElement ssoIcon = wait.until(
                ExpectedConditions.elementToBeClickable(ServicesPageLocators.getSSOIconByRow(rowIndex)));
        ssoIcon.click();
        System.out.println("Clicked SSO Icon for row " + rowIndex);
        // Wait for new tab to start opening
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }

    public void clickBackButton() {
        wait.until(ExpectedConditions.elementToBeClickable(ServicesPageLocators.BACK_BUTTON)).click();
        System.out.println("Clicked Back Button.");
    }

    public String getDetailsServiceName() {
        try {
            // Wait for details page to fully load with increased timeout
            WebDriverWait detailsWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement nameElement = detailsWait.until(
                    ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.DETAILS_SERVICE_NAME));
            String name = nameElement.getText();
            System.out.println("Retrieved details service name: " + name);
            return name;
        } catch (Exception e) {
            System.out.println("Error retrieving details service name: " + e.getMessage());
            // Retry once after a short wait
            try {
                Thread.sleep(2000);
                return wait
                        .until(ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.DETAILS_SERVICE_NAME))
                        .getText();
            } catch (Exception ex) {
                System.out.println("Retry failed: " + ex.getMessage());
                return "";
            }
        }
    }

    // Helper to get the first available row (any status)
    public int getFirstAvailableRow() {
        waitForTableToLoad();
        // Additional wait to ensure data is fully populated
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        int rows = getRowCount();
        System.out.println("Total rows found in table: " + rows);
        if (rows > 0) {
            String status = getServiceStatus(1);
            System.out.println("Row 1 status: " + status);
            return 1; // Return first row
        }
        return -1; // No rows found
    }

    // Helper to find the first row index that matches Status 'Active'
    public int findFirstActiveServiceRow() {
        waitForTableToLoad();
        // Additional wait to ensure data is fully populated
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        int rows = getRowCount();
        System.out.println("Total rows found in table: " + rows);
        for (int i = 1; i <= rows; i++) {
            String status = getServiceStatus(i);
            System.out.println("Row " + i + " status: " + status);
            if (status.equalsIgnoreCase("Active")) {
                return i;
            }
        }
        return -1; // No active service found
    }

    private void waitForTableToLoad() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.TABLE_ROWS));
            System.out.println("Table rows are now visible.");
        } catch (Exception e) {
            System.out.println("No rows found in table or timeout waiting for table rows.");
        }
    }

    // Wait for service details page to fully load after navigating from service
    // card
    public void waitForServiceDetailsPageLoad() {
        System.out.println("Waiting for service details page to load...");
        try {
            // Wait for table to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.TABLE_ROWS));
            // Additional wait for data to populate
            Thread.sleep(2000);
            System.out.println("Service details page loaded successfully.");
        } catch (Exception e) {
            System.out.println("Warning: Service details page may not have loaded completely.");
        }
    }

    public void switchToNewTab() {
        String currentWindow = driver.getWindowHandle();
        // Wait for new window/tab to open with retry mechanism
        int maxRetries = 5;
        int retryCount = 0;
        boolean switched = false;

        while (retryCount < maxRetries && !switched) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            Set<String> handles = driver.getWindowHandles();
            System.out.println("Window handles count: " + handles.size());

            if (handles.size() > 1) {
                for (String handle : handles) {
                    if (!handle.equals(currentWindow)) {
                        driver.switchTo().window(handle);
                        System.out.println("Switched to new tab successfully.");
                        switched = true;
                        // Wait for new tab to load
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                        }
                        break;
                    }
                }
            }
            retryCount++;
        }

        if (!switched) {
            System.out.println("Warning: Failed to switch to new tab after " + maxRetries + " retries.");
        }
    }

    public void closeCurrentTabAndSwitchBack(String originalHandle) {
        driver.close();
        driver.switchTo().window(originalHandle);
    }

    // Helper to find the first row index that does NOT have "Failed" status
    public int findFirstNonFailedServiceRow() {
        waitForTableToLoad();
        // Additional wait to ensure data is fully populated
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        int rows = getRowCount();
        System.out.println("Total rows found in table: " + rows);
        for (int i = 1; i <= rows; i++) {
            String status = getServiceStatus(i);
            System.out.println("Row " + i + " status: " + status);
            if (!status.equalsIgnoreCase("Failed") && !status.equals("N/A")) {
                System.out.println("Found non-failed service account at row: " + i);
                return i;
            }
        }
        return -1; // No non-failed service found
    }

    // Get service account name from SSO dashboard page
    public String getSSODashboardServiceName() {
        try {
            // Wait for SSO dashboard to load
            WebDriverWait ssoWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement nameElement = ssoWait.until(
                    ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.SSO_DASHBOARD_SERVICE_NAME));
            String name = nameElement.getText();
            System.out.println("Retrieved SSO dashboard service name: " + name);
            return name;
        } catch (Exception e) {
            System.out.println("Error retrieving SSO dashboard service name: " + e.getMessage());
            return "";
        }
    }
}
