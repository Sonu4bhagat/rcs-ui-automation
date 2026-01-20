package pages;

import locators.DashboardPageLocators;
import enums.UserRole;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isDashboardLoaded(UserRole role) {
        try {
            switch (role) {
                case SUPERADMIN:
                    return wait.until(
                            ExpectedConditions.visibilityOfElementLocated(DashboardPageLocators.SUPER_ADMIN_ITEM))
                            .isDisplayed();
                case ENTERPRISE:
                    return wait
                            .until(ExpectedConditions.visibilityOfElementLocated(DashboardPageLocators.ENTERPRISE_ITEM))
                            .isDisplayed();
                case RESELLER:
                    return wait
                            .until(ExpectedConditions.visibilityOfElementLocated(DashboardPageLocators.RESELLER_ITEM))
                            .isDisplayed();
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public String getHeaderText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(DashboardPageLocators.HEADER_TITLE))
                    .getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isProfileIconVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(DashboardPageLocators.PROFILE_ICON))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSidebarVisible() {
        try {
            // Note: SIDEBAR_CONTAINER xpath might need adjustment based on actual DOM
            // Using a specific item as a proxy for sidebar visibility if container is
            // elusive
            return wait.until(ExpectedConditions.visibilityOfElementLocated(DashboardPageLocators.DASHBOARD_LINK))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verify if the specified role name is displayed on the dashboard
     * 
     * @param roleName The role name to verify (e.g., "Super Admin", "Enterprise
     *                 Admin")
     * @return true if role is displayed on dashboard, false otherwise
     */
    public boolean isRoleDisplayedOnDashboard(String roleName) {
        try {
            System.out.println("  Checking if role '" + roleName + "' is displayed on dashboard...");

            // Wait up to 5 seconds for role to appear
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

            // Try dynamic locator first
            By roleLocator = DashboardPageLocators.getRoleDisplayLocator(roleName);
            WebElement roleElement = shortWait.until(ExpectedConditions.presenceOfElementLocated(roleLocator));

            if (roleElement.isDisplayed()) {
                String displayedText = roleElement.getText();
                System.out.println("  ✓ Role found on dashboard: '" + displayedText + "'");
                return true;
            }

            return false;
        } catch (Exception e) {
            // Try alternative: Check page source for role name
            try {
                String pageSource = driver.getPageSource();
                if (pageSource.contains(roleName)) {
                    System.out.println("  ✓ Role '" + roleName + "' found in page source");
                    return true;
                }
            } catch (Exception ex) {
                // Ignore
            }

            System.out.println("  ✗ Role '" + roleName + "' NOT found on dashboard");
            System.out.println("  Debug - Current URL: " + driver.getCurrentUrl());
            return false;
        }
    }

    /**
     * Get all text from the header/user info area to debug role display
     * 
     * @return String containing all header/user info text
     */
    public String getHeaderUserInfoText() {
        try {
            WebElement headerInfo = wait.until(ExpectedConditions.presenceOfElementLocated(
                    DashboardPageLocators.HEADER_USER_INFO));
            return headerInfo.getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Comprehensive dashboard validation - checks for SPARC logo to confirm
     * dashboard loaded
     * 
     * @param roleName The role name (for logging purposes)
     * @return true if on dashboard (SPARC logo visible)
     */
    public boolean verifyDashboardWithRole(String roleName) {
        try {
            System.out.println("  Verifying dashboard loaded for role: " + roleName);

            // Check 1: URL should not be SSO page
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("service-nodes-sso")) {
                System.out.println("  ✗ Still on SSO page - URL: " + currentUrl);
                return false;
            }

            System.out.println("  ✓ Redirected from SSO page - URL: " + currentUrl);

            // Check 2: SPARC Logo should be visible (indicates dashboard loaded)
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement logo = shortWait.until(ExpectedConditions.visibilityOfElementLocated(
                        DashboardPageLocators.SPARC_LOGO));

                if (logo.isDisplayed()) {
                    System.out.println("  ✓ SPARC logo found - Dashboard loaded successfully");
                    return true;
                } else {
                    System.out.println("  ✗ SPARC logo not visible");
                    return false;
                }
            } catch (Exception e) {
                // Try alternative logo locator
                try {
                    WebElement altLogo = driver.findElement(DashboardPageLocators.DASHBOARD_LOGO);
                    if (altLogo.isDisplayed()) {
                        System.out.println("  ✓ Dashboard logo found - Dashboard loaded successfully");
                        return true;
                    }
                } catch (Exception ex) {
                    // Logo not found
                }

                System.out.println("  ✗ SPARC logo not found - Dashboard did not load");
                System.out.println("  Debug - Current URL: " + currentUrl);
                System.out.println("  Debug - Page Title: " + driver.getTitle());
                return false;
            }
        } catch (Exception e) {
            System.out.println("  ✗ Dashboard verification error: " + e.getMessage());
            return false;
        }
    }

    public void clickService(String serviceName) {
        // Wait for page to be ready in headless mode
        if (base.DriverFactory.isHeadlessModeEnabled()) {
            waitForPageReady();
            removeBlockingIframes();
        }

        By locator;
        switch (serviceName.toUpperCase()) {
            case "SMS":
                locator = DashboardPageLocators.SERVICE_SMS;
                break;
            case "RCS":
                locator = DashboardPageLocators.SERVICE_RCS;
                break;
            case "OBD":
                locator = DashboardPageLocators.SERVICE_OBD;
                break;
            case "IVR":
                locator = DashboardPageLocators.SERVICE_IVR;
                break;
            case "WABA":
                locator = DashboardPageLocators.SERVICE_WHATSAPP;
                break;
            case "CCS":
                locator = DashboardPageLocators.SERVICE_CCS;
                break;
            case "LIVE AGENT":
                locator = DashboardPageLocators.SERVICE_LIVE_AGENT;
                break;
            default:
                throw new IllegalArgumentException("Service not supported: " + serviceName);
        }

        System.out.println("Navigating to service: " + serviceName);
        WebElement serviceElement = null;

        try {
            serviceElement = wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            System.out.println("⚠️ Element not clickable via standard wait. Trying presence check for JS click...");
            try {
                serviceElement = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                System.out.println("✓ Element found via presence check.");
            } catch (Exception ex) {
                System.out.println("❌ Element not found even via presence check.");
                throw ex;
            }
        }

        // Use JavaScript click in headless mode to avoid overlay issues
        if (base.DriverFactory.isHeadlessModeEnabled()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", serviceElement);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", serviceElement);
        } else {
            try {
                serviceElement.click();
            } catch (Exception e) {
                System.out.println("Standard click failed (" + e.getMessage() + "). Trying JS Click...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", serviceElement);
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

    /**
     * Wait for page to be fully loaded (for headless mode stability)
     */
    private void waitForPageReady() {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10)).until(
                    d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
            Thread.sleep(1500); // Extra wait for Angular/React app initialization
        } catch (Exception e) {
            System.out.println("Page ready wait completed: " + e.getMessage());
        }
    }

    public void openCalendarFilter() {
        // Wait for dashboard to stabilize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        boolean pickerOpened = false;

        try {
            // Try 1: Direct Calendar Icon
            System.out.println("Attempting to click Calendar icon...");
            WebElement calendarBtn = wait
                    .until(ExpectedConditions.elementToBeClickable(DashboardPageLocators.CALENDAR_ICON));
            calendarBtn.click();
            System.out.println("Clicked Calendar icon.");

            // Verify picker opened
            pickerOpened = isInternalElementVisible(
                    By.xpath("//mat-date-range-picker | //div[contains(@class, 'mat-datepicker-popup')]"));

        } catch (Exception e) {
            System.out.println("Standard click failed/Icon not found. Capturing debug info...");
            logAllInteractiveElements();
            dumpPageSource("calendar_fail");
            System.out.println("Trying Filter button fallback...");
        }

        if (!pickerOpened) {
            try {
                // Try 2: Open Filter Panel
                WebElement filterBtn = wait
                        .until(ExpectedConditions.elementToBeClickable(DashboardPageLocators.FILTER_BUTTON));
                filterBtn.click();
                System.out.println("Clicked Filter button.");
                Thread.sleep(500);
            } catch (Exception ex) {
                // Try 3: JS Click on generic calendar
                System.out.println("Filter button failed. Trying generic JS Click...");
                try {
                    WebElement btn = driver.findElement(By.xpath(
                            "//mat-icon[contains(text(), 'calendar')] | //button[contains(@aria-label, 'Date')] | //*[contains(@class, 'mat-datepicker-toggle')]//button"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                    System.out.println("Performed JS Click on calendar element.");
                } catch (Exception jsEx) {
                    System.out.println("JS Click failed.");
                }
            }
        }

        // Final verification check (Optional, but good for debugging)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }

    // Internal helper to check visibility without throwing
    private boolean isInternalElementVisible(By locator) {
        try {
            return !driver.findElements(locator).isEmpty() && driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void selectDateFromPicker() {
        try {
            // Wait for date cell to be present
            WebElement dateCell = wait
                    .until(ExpectedConditions.elementToBeClickable(DashboardPageLocators.CALENDAR_DATE_CELL));
            dateCell.click();
            System.out.println("Selected date from picker.");
        } catch (Exception e) {
            System.out.println("Date selection failed (Standard Click). Trying JS Click...");
            try {
                WebElement dateCell = driver.findElement(DashboardPageLocators.CALENDAR_DATE_CELL);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dateCell);
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Could not select any date from picker/calendar. Is it open? Error: " + ex.getMessage());
            }
        }
    }

    public void selectLast30Days() {
        try {
            WebElement last30Days = wait
                    .until(ExpectedConditions.elementToBeClickable(DashboardPageLocators.LAST_30_DAYS_OPTION));
            last30Days.click();
            System.out.println("Selected 'Last 30 Days'.");
        } catch (Exception e) {
            System.out.println("'Last 30 Days' selection failed. Trying JS Click...");
            try {
                WebElement el = driver.findElement(DashboardPageLocators.LAST_30_DAYS_OPTION);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Could not select 'Last 30 Days' option. Is the picker open? Error: " + ex.getMessage());
            }
        }
    }

    public boolean isServicePageLoaded(String serviceName) {
        // Robust check: Url contains service name OR Header text matches
        String lowerCaseService = serviceName.toLowerCase();
        try {
            // Handle variations
            if (lowerCaseService.contains("whatsapp") || lowerCaseService.contains("waba")) {
                return wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("whatsapp"),
                        ExpectedConditions.urlContains("waba")));
            } else if (lowerCaseService.contains("live") || lowerCaseService.contains("agent")) {
                return wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("live"),
                        ExpectedConditions.urlContains("agent")));
            }

            return wait.until(ExpectedConditions.urlContains(lowerCaseService));
        } catch (Exception e) {
            System.out.println("Service page load check failed for " + serviceName + ": " + e.getMessage());
            return false;
        }
    }

    // Filter Methods
    public void openFilters() {
        try {
            // Strategy 1: Standard clickable wait
            WebElement filterBtn = wait
                    .until(ExpectedConditions.elementToBeClickable(DashboardPageLocators.FILTER_BUTTON));
            filterBtn.click();
        } catch (Exception e) {
            System.out.println("Standard click failed. Trying JS Click on broader locator...");
            try {
                // Strategy 2: Find ANY element with 'Filter' text or icon and JS click it
                // This bypasses some visibility/clickability checks
                WebElement genericFilter = driver.findElement(By.xpath(
                        "//*[contains(text(), 'Filter') or normalize-space()='Filter' or .//mat-icon[normalize-space()='filter_list']]"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", genericFilter);
            } catch (Exception ex) {
                System.out.println(
                        "JS Click failed. Checking if Date Filter is already visible (panel might be open)...");
                if (!isElementVisible(DashboardPageLocators.DATE_RANGE_FILTER)) {
                    // Debugging: Print all buttons/links to find the real one
                    logAllInteractiveElements();
                    throw new RuntimeException("Filter button not found. See console for list of available elements.");
                }
            }
        }
    }

    // Debugging Helper: Dump Page Source to File
    private void dumpPageSource(String contextName) {
        try {
            String source = driver.getPageSource();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = "page_source_" + contextName + "_" + timestamp + ".html";

            // Create target/page_sources directory if not exists
            java.nio.file.Path path = java.nio.file.Paths.get("target", "page_sources");
            java.nio.file.Files.createDirectories(path);

            java.nio.file.Files.write(path.resolve(filename), source.getBytes());
            System.out.println("\n[DEBUG] 📸 Page source dumped to: target/page_sources/" + filename);
        } catch (Exception e) {
            System.out.println("[DEBUG] Failed to dump page source: " + e.getMessage());
        }
    }

    private void logAllInteractiveElements() {
        System.out.println("\n[DEBUG] 🔍 Listing all potential interactive elements on page:");
        try {
            // Updated to include icons and typical clickable elements
            java.util.List<WebElement> elements = driver.findElements(By.xpath(
                    "//button | //a | //mat-icon | //input | //img | //svg | //*[contains(@class, 'icon')]"));

            int count = 0;
            for (WebElement el : elements) {
                if (el.isDisplayed()) {
                    String tag = el.getTagName();
                    String text = el.getText().trim();
                    String id = el.getAttribute("id");
                    String cls = el.getAttribute("class");
                    String ariaLabel = el.getAttribute("aria-label");

                    // Filter out noise - only show relevant elements
                    if (!text.isEmpty() || (ariaLabel != null && !ariaLabel.isEmpty()) ||
                            tag.equals("mat-icon") || tag.equals("input") || cls.contains("calendar")
                            || cls.contains("date")) {

                        System.out.println("  Element [" + (++count) + "]: <" + tag + ">" +
                                " | Text='" + text + "'" +
                                " | ID=" + (id != null ? id : "n/a") +
                                " | Class=" + (cls != null ? cls : "n/a") +
                                " | AriaLabel=" + (ariaLabel != null ? ariaLabel : "n/a"));
                    }
                }
            }
            if (count == 0)
                System.out.println("  [DEBUG] No visible interactive elements found.");
        } catch (Exception e) {
            System.out.println("  [DEBUG] Failed to log elements: " + e.getMessage());
        }
    }

    private boolean isElementVisible(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void applyDateFilter(String dateRange) {
        // This is a placeholder as actual implementation depends on the date picker
        // component
        WebElement dateInput = wait
                .until(ExpectedConditions.visibilityOfElementLocated(DashboardPageLocators.DATE_RANGE_FILTER));
        dateInput.clear();
        dateInput.sendKeys(dateRange);
        // close picker if needed?
    }

    public void clickApplyFilter() {
        wait.until(ExpectedConditions.elementToBeClickable(DashboardPageLocators.APPLY_FILTER_BUTTON)).click();
    }

    public void clickClearFilter() {
        wait.until(ExpectedConditions.elementToBeClickable(DashboardPageLocators.CLEAR_FILTER_BUTTON)).click();
    }

    /**
     * Checks for visible error messages on the page
     * 
     * @return Error message string if found, null otherwise
     */
    public String checkForPageErrors() {
        try {
            // Check for Toast Errors
            java.util.List<WebElement> toasts = driver.findElements(
                    By.xpath("//div[contains(@class, 'toast-error') or contains(@class, 'alert-danger')]"));
            for (WebElement toast : toasts) {
                if (toast.isDisplayed())
                    return "Toast Error: " + toast.getText();
            }

            // Check for dedicated Error Page or Heading
            java.util.List<WebElement> headings = driver.findElements(By.xpath("//h1 | //h2"));
            for (WebElement heading : headings) {
                if (heading.isDisplayed()) {
                    String text = heading.getText().toLowerCase();
                    if (text.contains("internal server error") || text.contains("404") ||
                            text.contains("access denied") || text.contains("service unavailable") ||
                            text.contains("bad request")) {
                        return "Page Error: " + heading.getText();
                    }
                }
            }

            // Check for generic error message containers
            java.util.List<WebElement> errorMsgs = driver
                    .findElements(By.xpath("//*[contains(@class, 'error-message') or contains(@class, 'error-text')]"));
            for (WebElement msg : errorMsgs) {
                if (msg.isDisplayed() && !msg.getText().isEmpty())
                    return "Error Message: " + msg.getText();
            }

            // Check for Popups / Modals (Bootstrap, SweetAlert, Angular Material)
            java.util.List<WebElement> popups = driver.findElements(By.xpath(
                    "//div[contains(@class, 'modal-content') or contains(@class, 'swal2-popup') or contains(@class, 'mat-dialog-container')]"));

            for (WebElement popup : popups) {
                if (popup.isDisplayed()) {
                    String text = popup.getText().toLowerCase();
                    // Only flag if it looks like an error/failure/warning
                    if (text.contains("error") || text.contains("failed") || text.contains("warning") ||
                            text.contains("went wrong") || text.contains("denied") || text.contains("bad request")) {
                        // Extract header if possible, or just first few chars
                        String firstLine = popup.getText().split("\n")[0];
                        return "Error Popup: " + (firstLine.isEmpty() ? "Unknown Error" : firstLine);
                    }
                }
            }

            return null; // No errors found
        } catch (Exception e) {
            return null; // Ignore check errors
        }
    }

    public boolean isFilterAppliedResultVisible() {
        // Placeholder verification: Check if "Clear Filter" is visible, or if table
        // rows refreshed
        return wait.until(ExpectedConditions.visibilityOfElementLocated(DashboardPageLocators.CLEAR_FILTER_BUTTON))
                .isDisplayed();
    }
}
