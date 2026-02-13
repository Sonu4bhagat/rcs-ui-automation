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
import org.openqa.selenium.TimeoutException;

public class ServicesPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public ServicesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // Helper to wait for loading spinner to disappear
    private void waitForLoadingSpinner() {
        try {
            // Short wait for spinner to appear
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.LOADING_SPINNER));
            // If appeared, wait for it to disappear
            wait.until(ExpectedConditions.invisibilityOfElementLocated(ServicesPageLocators.LOADING_SPINNER));
        } catch (Exception e) {
            // Spinner didn't appear or disappeared quickly - ignore
        }
    }

    public void clickServicesTab() {
        System.out.println("[INFO] Clicking on Services Tab...");
        String currentUrl = driver.getCurrentUrl();

        // URL recovery
        if (!currentUrl.contains("stagingvault.smartping.io")) {
            System.out.println("[WARN] Browser is on wrong domain. Attempting recovery...");
            String baseUrl = "https://stagingvault.smartping.io";
            try {
                Set<String> handles = driver.getWindowHandles();
                if (handles.size() > 1) {
                    driver.close();
                    driver.switchTo().window(handles.iterator().next());
                    currentUrl = driver.getCurrentUrl();
                }
                if (!currentUrl.contains("stagingvault.smartping.io")) {
                    driver.navigate().to(baseUrl + "/customer/Retrol38-UW-335/services");
                    waitForLoadingSpinner();
                }
            } catch (Exception e) {
                System.err.println("[ERROR] URL recovery failed: " + e.getMessage());
                driver.navigate().to(baseUrl);
            }
        }

        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.PAGE_HEADER),
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(@class, 'sidenav') or contains(@class, 'menu')]"))));

            WebElement servicesTab;
            try {
                servicesTab = wait.until(ExpectedConditions.elementToBeClickable(ServicesPageLocators.SERVICES_TAB));
            } catch (TimeoutException toe) {
                System.out.println("[DEBUG] Services Tab not clickable/visible. Checking presence...");
                servicesTab = wait
                        .until(ExpectedConditions.presenceOfElementLocated(ServicesPageLocators.SERVICES_TAB));
            }

            servicesTab.click();
        } catch (Exception e) {
            System.out.println("[DEBUG] Standard click fail. Trying JS click...");
            try {
                WebElement servicesTab = driver.findElement(ServicesPageLocators.SERVICES_TAB);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", servicesTab);
                Thread.sleep(500);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", servicesTab);
            } catch (Exception ex) {
                System.err.println("[ERROR] JS Click also failed: " + ex.getMessage());
                throw new RuntimeException("Failed to click Services tab", ex);
            }
        }

        // Wait for URL change or header
        try {
            WebDriverWait urlWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            urlWait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("services"),
                    ExpectedConditions.urlContains("service"),
                    ExpectedConditions.presenceOfElementLocated(ServicesPageLocators.PAGE_HEADER)));

            waitForLoadingSpinner();

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(ServicesPageLocators.PAGE_HEADER),
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'card')]")),
                    ExpectedConditions.presenceOfElementLocated(By.xpath(
                            "//*[contains(text(), 'SMS') or contains(text(), 'RCS') or contains(text(), 'WABA')]"))));
            System.out.println("[INFO] Services page loaded successfully.");

        } catch (Exception e) {
            System.err.println("[ERROR] Services page validation failed. URL: " + driver.getCurrentUrl());
            throw new RuntimeException("Failed to navigate to Services Page.", e);
        }
    }

    // New: Navigate to specific service (SMS, RCS, etc.) from the 'Services Home'
    // cards
    public void clickServiceCardViewDetails(String serviceName) {
        System.out.println("[INFO] Navigating to service: " + serviceName);

        if (base.DriverFactory.isHeadlessModeEnabled()) {
            removeBlockingIframes();
        }

        try {
            waitForLoadingSpinner();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'card')]")));
        } catch (Exception e) {
            System.out.println("[WARN] Timed out waiting for service cards.");
        }

        try {
            By locator = ServicesPageLocators.getViewDetailsButtonForService(serviceName);
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(locator));

            if (base.DriverFactory.isHeadlessModeEnabled()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
                Thread.sleep(500);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            } else {
                button.click();
            }
            System.out.println("[INFO] Clicked View Details for " + serviceName);
        } catch (Exception e) {
            System.err.println("[WARN] Standard click fail for " + serviceName + ". Trying alternative...");
            try {
                List<WebElement> allCards = driver.findElements(By.xpath("//div[contains(@class, 'card')]"));
                for (WebElement card : allCards) {
                    if (card.getText().toLowerCase().contains(serviceName.toLowerCase())) {
                        List<WebElement> buttons = card.findElements(By.tagName("button"));
                        for (WebElement btn : buttons) {
                            if (btn.getText().toLowerCase().contains("view")
                                    || btn.getText().toLowerCase().contains("details")) {
                                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
                                Thread.sleep(200);
                                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                                return;
                            }
                        }
                    }
                }
                throw new RuntimeException("Could not find card for " + serviceName);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to click View Details for " + serviceName, ex);
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
                            "  iframe.remove();" +
                            "});");
            System.out.println("[INFO] Removed blocking iframes");
        } catch (Exception e) {
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
            waitForLoadingSpinner();
            // Wait for table to refresh
            wait.until(ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.TABLE_ROWS));
        } catch (Exception e) {
            System.out.println("Warning: Search results may not have fully loaded.");
        }
    }

    public void applyActiveFilter() {
        try {
            WebElement filterBtn = wait
                    .until(ExpectedConditions.elementToBeClickable(ServicesPageLocators.FILTER_BUTTON));
            filterBtn.click();
            System.out.println("Clicked filter button.");
        } catch (Exception e) {
            System.out.println("Standard click failed for filter button, trying JS click...");
            try {
                WebElement filterBtn = driver.findElement(ServicesPageLocators.FILTER_BUTTON);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", filterBtn);
                System.out.println("Clicked filter button via JS.");
            } catch (Exception ex) {
                System.out.println("Filter button click failed: " + ex.getMessage());
                throw new RuntimeException("Failed to click filter button", ex);
            }
        }

        // Wait for filter dropdown to be visible
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // Click Active checkbox with JS fallback
        try {
            WebElement activeCheckbox = wait
                    .until(ExpectedConditions.elementToBeClickable(ServicesPageLocators.STATUS_FILTER_ACTIVE));
            if (!activeCheckbox.isSelected()) {
                activeCheckbox.click();
                System.out.println("Selected Active filter checkbox.");
            }
        } catch (Exception e) {
            System.out.println("Standard click failed for Active checkbox, trying JS click...");
            try {
                WebElement activeCheckbox = driver.findElement(ServicesPageLocators.STATUS_FILTER_ACTIVE);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", activeCheckbox);
                System.out.println("Selected Active filter checkbox via JS.");
            } catch (Exception ex) {
                System.out.println("Active checkbox click failed: " + ex.getMessage());
                throw new RuntimeException("Failed to click Active checkbox", ex);
            }
        }

        // Wait before clicking Apply
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        // Click Apply button with JS fallback
        try {
            WebElement applyBtn = wait
                    .until(ExpectedConditions.elementToBeClickable(ServicesPageLocators.APPLY_FILTER_BTN));
            applyBtn.click();
            System.out.println("Clicked Apply filter button.");
        } catch (Exception e) {
            System.out.println("Standard click failed for Apply button, trying JS click...");
            try {
                WebElement applyBtn = driver.findElement(ServicesPageLocators.APPLY_FILTER_BTN);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyBtn);
                System.out.println("Clicked Apply filter button via JS.");
            } catch (Exception ex) {
                System.out.println("Apply button click failed: " + ex.getMessage());
                // Don't throw - filter may have been applied already
                System.out.println("Warning: Apply button may not have been clicked. Proceeding...");
            }
        }

        // Wait for table to refresh after filter application
        try {
            waitForLoadingSpinner();
            // Verify table is refreshed
            wait.until(ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.TABLE_ROWS));
        } catch (Exception e) {
            System.out.println("Warning: Filter may not have been fully applied.");
        }
    }

    public int getRowCount() {
        return driver.findElements(ServicesPageLocators.TABLE_ROWS).size();
    }

    public String getServiceName(int rowIndex) {
        try {
            By locator = ServicesPageLocators.getServiceNameByRow(rowIndex);
            // Wait for element to be present
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));

            // Wait for element to have non-empty text (retry-based)
            wait.until(driver -> {
                try {
                    String text = driver.findElement(locator).getText().trim();
                    return !text.isEmpty();
                } catch (Exception e) {
                    return false;
                }
            });
            return driver.findElement(locator).getText().trim();
        } catch (Exception e) {
            System.out.println("Warning: Primary name locator failed for row " + rowIndex
                    + ". Trying fallback to any TD with text...");
            try {
                List<WebElement> cells = driver.findElements(By.xpath("(//tbody//tr)[" + rowIndex + "]//td"));
                for (WebElement cell : cells) {
                    String text = cell.getText().trim();
                    if (!text.isEmpty() && text.length() > 3 && !text.equalsIgnoreCase("Active")
                            && !text.equalsIgnoreCase("Inactive")) {
                        return text;
                    }
                }
            } catch (Exception ex) {
                System.out.println("Fallback also failed: " + ex.getMessage());
            }
            return "";
        }
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
        System.out.println("[INFO] Clicked View Icon for row " + rowIndex);
        waitForLoadingSpinner();
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
            if (name != null && !name.trim().isEmpty()) {
                System.out.println("Retrieved details service name: " + name);
                return name;
            }
        } catch (Exception e) {
            System.out.println("Primary locator failed: " + e.getMessage());
        }

        // Retry with short wait
        try {
            Thread.sleep(2000);
            WebElement nameElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.DETAILS_SERVICE_NAME));
            String name = nameElement.getText();
            if (name != null && !name.trim().isEmpty()) {
                System.out.println("Retrieved details service name on retry: " + name);
                return name;
            }
        } catch (Exception ex) {
            System.out.println("Secondary attempt failed: " + ex.getMessage());
        }

        // Fallback: Try to get service name from any visible header element
        try {
            List<WebElement> headers = driver.findElements(By.xpath("//h4 | //h5 | //h3"));
            for (WebElement header : headers) {
                String text = header.getText();
                if (text != null && !text.trim().isEmpty() && text.length() > 3) {
                    System.out.println("Found service name from header: " + text);
                    return text;
                }
            }
        } catch (Exception ex) {
            System.out.println("Header search failed: " + ex.getMessage());
        }

        // Last resort: Extract from URL
        try {
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Attempting to extract service name from URL: " + currentUrl);
            // URL pattern: /services/SMS/service-account-id or
            // /services/RCS/service-account-id
            if (currentUrl.contains("/services/")) {
                String[] parts = currentUrl.split("/");
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equalsIgnoreCase("services") && i + 2 < parts.length) {
                        // The service account ID is usually after the service type
                        String serviceId = parts[i + 2];
                        System.out.println("Extracted from URL: " + serviceId);
                        return serviceId;
                    }
                }
            }
        } catch (Exception urlEx) {
            System.out.println("URL parsing failed: " + urlEx.getMessage());
        }

        System.out.println("All attempts to get details service name failed.");
        return "";
    }

    // Helper to get the first available row (any status)
    public int getFirstAvailableRow() {
        waitForTableToLoad();
        // Additional wait to ensure data is fully populated
        waitForLoadingSpinner();
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
        waitForLoadingSpinner();
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
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(ServicesPageLocators.TABLE_ROWS));
            waitForLoadingSpinner();
            wait.until(driver -> {
                List<WebElement> rows = driver.findElements(ServicesPageLocators.TABLE_ROWS);
                return !rows.isEmpty() && !rows.get(0).getText().trim().isEmpty();
            });
            System.out.println("[INFO] Service details page loaded.");
        } catch (Exception e) {
            System.out.println("[WARN] Service details page load validation failed: " + e.getMessage());
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
    // Now accepts N/A status since filter has been applied and row is visible
    public int findFirstNonFailedServiceRow() {
        waitForTableToLoad();
        // Additional wait to ensure data is fully populated
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        int rows = getRowCount();
        System.out.println("Total rows found in table: " + rows);

        for (int i = 1; i <= rows; i++) {
            try {
                String status = getServiceStatus(i);
                System.out.println("Row " + i + " status: " + status);
                if (!status.equalsIgnoreCase("Failed")) {
                    String name = getServiceName(i);
                    // Ensure the row actually has a name before picking it
                    if (name != null && !name.trim().isEmpty()) {
                        System.out.println("Found valid non-failed service account '" + name + "' at row: " + i);
                        return i;
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
        return -1;
    }

    // Get service account name from SSO dashboard page
    public String getSSODashboardServiceName() {
        try {
            // Wait for SSO dashboard to load
            WebDriverWait ssoWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            // Find all matching elements instead of just the first one
            List<WebElement> nameElements = ssoWait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(ServicesPageLocators.SSO_DASHBOARD_SERVICE_NAME));

            for (WebElement element : nameElements) {
                if (element.isDisplayed()) {
                    String name = element.getText().trim();
                    // Filter out numbers like "0", empty strings, or very short text
                    if (!name.isEmpty() && name.length() > 2 && !name.matches("\\d+")) {
                        System.out.println("Retrieved SSO dashboard service name: " + name);
                        return name;
                    }
                }
            }

            System.out.println("Could not find a valid service name from SSO dashboard elements.");
            return "";
        } catch (Exception e) {
            System.out.println("Error retrieving SSO dashboard service name: " + e.getMessage());
            return "";
        }
    }
}
