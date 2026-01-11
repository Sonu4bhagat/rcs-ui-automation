package pages;

import locators.CustomerOrgPageLocators;
import locators.DashboardPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CustomerOrgPage {
    WebDriver driver;
    WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT = 10;
    private static final int HEADLESS_TIMEOUT = 20;

    public CustomerOrgPage(WebDriver driver) {
        this.driver = driver;
        // Use longer timeout in headless mode for CI/CD stability
        int timeout = base.DriverFactory.isHeadlessModeEnabled() ? HEADLESS_TIMEOUT : DEFAULT_TIMEOUT;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public void navigateToCustomerOrg() {
        System.out.println("Navigating to Customer Org...");
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(DashboardPageLocators.SUPER_ADMIN_ITEM));
        link.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.PAGE_HEADER));
    }

    public void filterByEnterprise() {
        System.out.println("Applying Enterprise filter...");
        wait.until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.FILTER_DROPDOWN)).click();

        // Select Enterprise radio option (better to click label)
        wait.until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.ENTERPRISE_FILTER_OPTION)).click();

        // Click Apply button
        wait.until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.APPLY_FILTER_BUTTON)).click();

        // Wait for table to refresh
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.tagName("mat-progress-bar")));
            Thread.sleep(2000); // Wait for results to stabilize
        } catch (Exception e) {
        }
    }

    public void drillDownFirstUserUsingArrow() {
        System.out.println("Clicking arrow to go inside first customer...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.TABLE_ROWS));
        WebElement firstRow = driver.findElement(CustomerOrgPageLocators.TABLE_ROWS);
        WebElement arrow = firstRow.findElement(CustomerOrgPageLocators.DRILL_DOWN_ARROW);
        wait.until(ExpectedConditions.elementToBeClickable(arrow)).click();
    }

    public void drillDownToUserWithPositiveBalance() {
        System.out.println("Searching for a user with balance > 0...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.TABLE_ROWS));
        List<WebElement> rows = driver.findElements(CustomerOrgPageLocators.TABLE_ROWS);
        for (WebElement row : rows) {
            try {
                // Find balance in the current row (Column 5: Wallets)
                String balanceText = row.findElement(By.xpath(".//td[position()=5]")).getText().trim();
                System.out.println("Row balance text: '" + balanceText + "'");

                double balance = Double.parseDouble(balanceText.replaceAll("[^\\d.]", ""));
                if (balance > 0) {
                    System.out
                            .println("Found user with balance: " + balance + ". Clicking Organisation Name (Col 3)...");
                    // Click the Organisation Name (Column 3) for drill-down
                    row.findElement(By.xpath(".//td[position()=3]//a | .//td[position()=3]")).click();
                    return;
                }
            } catch (Exception e) {
                // Ignore rows with invalid balance text
            }
        }
        throw new RuntimeException("No Enterprise user found with a balance greater than 0.");
    }

    public List<String> getTableHeaderTexts() {
        System.out.println("Waiting for table headers to be visible...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.TABLE_HEADERS));
        List<WebElement> headers = driver.findElements(CustomerOrgPageLocators.TABLE_HEADERS);
        return headers.stream().map(h -> h.getText().trim()).filter(t -> !t.isEmpty()).toList();
    }

    public void navigateToTab(By tabLocator) {
        System.out.println("Switching to tab: " + tabLocator.toString());
        wait.until(ExpectedConditions.elementToBeClickable(tabLocator)).click();
    }

    public void drillDownToEnterprise() {
        System.out.println("Drilling down to Enterprise...");
        wait.until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.ENTERPRISE_ROW_LINK)).click();
    }

    public void drillDownToReseller() {
        System.out.println("Drilling down to Reseller...");
        wait.until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.RESELLER_ROW_LINK)).click();
    }

    public void drillDownToEnterpriseL2() {
        System.out.println("Drilling down to Enterprise L2...");
        // This likely requires being inside an Enterprise first.
        // We'll use a generic locator for any sub-user row in the current view.
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table//tr[.//td[contains(text(), 'L2')]]//td[1] | //table//tbody//tr[1]//td[1]"))).click();
    }

    public java.util.Map<String, String> getWalletDetails() {
        java.util.Map<String, String> details = new java.util.HashMap<>();
        try {
            // Find all key-value pairs in wallet (labels + values)
            List<WebElement> labels = driver.findElements(
                    By.xpath("//span[contains(@class, 'label')] | //div[contains(@class, 'wallet-info')]//p"));
            List<WebElement> values = driver.findElements(
                    By.xpath("//span[contains(@class, 'value')] | //div[contains(@class, 'wallet-info')]//h5"));

            for (int i = 0; i < Math.min(labels.size(), values.size()); i++) {
                details.put(labels.get(i).getText().trim(), values.get(i).getText().trim());
            }
        } catch (Exception e) {
            System.out.println("Detailed wallet info not found. Returning balance only.");
            details.put("Balance", getWalletValue());
        }
        return details;
    }

    public List<String> getAllTableData() {
        List<WebElement> cells = driver.findElements(CustomerOrgPageLocators.TABLE_DATA);
        return cells.stream().map(WebElement::getText).toList();
    }

    public String getWalletValue() {
        System.out.println("Attempting to get Wallet Balance...");
        try {
            // Try standard landing page wallet balance
            String val = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.WALLET_BALANCE))
                    .getText();
            System.out.println("Standard Balance Found: " + val);
            return val;
        } catch (TimeoutException e) {
            try {
                // Try redirected overview page balance
                String val = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.OVERVIEW_WALLET_BALANCE))
                        .getText();
                System.out.println("Overview Balance Found: " + val);
                return val;
            } catch (TimeoutException e2) {
                System.out.println("Balance not found in either location.");
                return "N/A";
            }
        }
    }

    public void profileSearch(String query) {
        WebElement input = wait
                .until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.PROFILE_SEARCH_INPUT));
        input.clear();
        input.sendKeys(query);
    }

    public boolean isPageLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.PAGE_HEADER))
                    .isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void clickAddCustomer() {
        System.out.println("Clicking Add Customer button...");
        wait.until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.ADD_CUSTOMER_BUTTON)).click();
    }

    public void searchCustomer(String name) {
        System.out.println("Searching for customer: " + name);
        WebElement input = wait
                .until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.SEARCH_INPUT));
        input.clear();
        input.sendKeys(name);
        wait.until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.SEARCH_BUTTON)).click();
    }

    public boolean isTableDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.CUSTOMER_TABLE))
                    .isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public int redirectToFirstEnterpriseWithWallets() {
        System.out.println("Searching for an Enterprise with at least 1 wallet...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.TABLE_ROWS));
        List<WebElement> rows = driver.findElements(CustomerOrgPageLocators.TABLE_ROWS);

        for (WebElement row : rows) {
            try {
                // Column 5 is 'Wallets'
                String walletText = row.findElement(By.xpath(".//td[position()=5]")).getText().trim();
                int count = Integer.parseInt(walletText.replaceAll("[^\\d]", ""));

                if (count >= 1) {
                    System.out.println("Found Enterprise with " + count + " wallets. Redirecting...");

                    String currentUrl = driver.getCurrentUrl();

                    WebElement redirectBtn = row.findElement(CustomerOrgPageLocators.REDIRECT_TO_OVERVIEW_BUTTON);
                    wait.until(ExpectedConditions.elementToBeClickable(redirectBtn)).click();

                    // CRITICAL: Wait for URL to change - navigation takes time
                    try {
                        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(currentUrl)));
                        Thread.sleep(2000); // Allow page to stabilize
                        System.out.println("✓ Navigation completed. New URL: " + driver.getCurrentUrl());
                    } catch (Exception e) {
                        System.err.println("⚠ WARNING: URL did not change after clicking redirect!");
                    }

                    return count;
                }
            } catch (Exception e) {
                // Skip rows that don't match or have issues
            }
        }
        throw new RuntimeException("No Enterprise user found with at least 1 wallet in the current view.");
    }

    public int getFirstRowWalletCount() {
        System.out.println("Getting wallet count from the first row...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.TABLE_ROWS));
        WebElement firstRow = driver.findElement(CustomerOrgPageLocators.TABLE_ROWS);

        // Column 5 is 'Wallets' based on detected headers [SV Customer ID, Customer ID,
        // Organisation Name, Status, Wallets, Type, Created On]
        String walletText = firstRow.findElement(By.xpath(".//td[position()=5]")).getText().trim();
        System.out.println("First row wallet text: '" + walletText + "'");

        try {
            return Integer.parseInt(walletText.replaceAll("[^\\d]", ""));
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse wallet count: " + walletText);
            return 0;
        }
    }

    public String getPrepaidBalance() {
        System.out.println("Fetching Prepaid Balance...");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.PREPAID_BALANCE))
                    .getText().trim();
        } catch (Exception e) {
            System.err.println("Prepaid Balance Fetch Failed: " + e.getMessage());
            return "N/A";
        }
    }

    public String getPostpaidBalance() {
        System.out.println("Fetching Postpaid Balance...");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.POSTPAID_BALANCE))
                    .getText().trim();
        } catch (Exception e) {
            System.err.println("Postpaid Balance Fetch Failed: " + e.getMessage());
            return "N/A";
        }
    }

    public void searchWallet(String name) {
        System.out.println("Searching for wallet: " + name);
        WebElement input = wait
                .until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.WALLET_SEARCH_INPUT));
        input.clear();
        input.sendKeys(name);
    }

    public void navigateToWalletUsage() {
        System.out.println("Navigating to Wallet Usage tab...");
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.WALLET_USAGE_TAB));
        tab.click();
        System.out.println("Clicked Wallet Usage tab. Waiting for content...");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.TABLE_HEADERS));
        } catch (Exception e) {
            System.err.println("Wallet Usage headers didn't appear: " + e.getMessage());
        }
    }

    public void redirectToFirstWalletServices() {
        System.out.println("Redirecting to Services for the first wallet...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.TABLE_ROWS));
        WebElement firstRow = driver.findElement(CustomerOrgPageLocators.TABLE_ROWS);
        WebElement servicesBtn = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(firstRow,
                CustomerOrgPageLocators.REDIRECT_TO_SERVICES_BUTTON));

        // Scroll to the button
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});",
                servicesBtn);
        System.out.println("Scrolled Redirect to Services button into view.");

        // Click with JS to avoid interception
        try {
            wait.until(ExpectedConditions.elementToBeClickable(servicesBtn)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", servicesBtn);
        }

        System.out.println("Clicked Redirect to Services button. Waiting for page load...");

        try {
            // Wait for URL to contain 'services' or similar if known, otherwise wait for
            // header
            // Adding a small static wait for stability as this transitions to a new view
            Thread.sleep(2000);

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.SERVICES_TABLE_HEADER),
                    ExpectedConditions.urlContains("services")));
            System.out.println("Successfully navigated. Current URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println(
                    "Warning: explicit wait for Services header failed. Current URL: " + driver.getCurrentUrl());
            // We don't throw yet, let the assertions in the test fail if needed
        }
    }

    public int getCustomerCount() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(CustomerOrgPageLocators.TABLE_ROWS));
            List<WebElement> rows = driver.findElements(CustomerOrgPageLocators.TABLE_ROWS);
            return rows.size();
        } catch (TimeoutException e) {
            return 0;
        }
    }

    public List<String> getServiceNames() {
        System.out.println("Fetching Service Names...");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.SERVICES_TABLE_ROWS));
            return driver.findElements(CustomerOrgPageLocators.SERVICES_NAME_CELLS)
                    .stream().map(WebElement::getText).map(String::trim).toList();
        } catch (Exception e) {
            System.out.println("No services found or unable to fetch names: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public boolean areServiceStatusesValid() {
        System.out.println("Verifying Service Statuses...");
        try {
            List<WebElement> statusCells = driver.findElements(CustomerOrgPageLocators.SERVICES_STATUS_CELLS);
            if (statusCells.isEmpty())
                return false; // Or true if empty table is allowed

            for (WebElement cell : statusCells) {
                String status = cell.getText().trim();
                System.out.println("Found Service Status: " + status);
                if (!status.equalsIgnoreCase("Active") && !status.equalsIgnoreCase("Inactive")
                        && !status.equalsIgnoreCase("Pending")) {
                    System.err.println("Invalid Status Found: " + status);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void performWalletTransaction(String type, double amount, String description) {
        System.out.println("Performing Wallet Transaction: " + type + " Amount: " + amount);

        // 1. Click Recharge/Adjustment button
        WebElement rechargeBtn = wait
                .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.WALLET_RECHARGE_BTN));
        rechargeBtn.click();

        // 2. Wait for Modal
        wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.TXN_MODAL_TITLE));

        // 3. Select Transaction Type
        if (type.equalsIgnoreCase("Credit")) {
            wait.until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.TXN_TYPE_CREDIT_RADIO)).click();
        } else if (type.equalsIgnoreCase("Debit")) {
            wait.until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.TXN_TYPE_DEBIT_RADIO)).click();
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + type);
        }

        // 4. Enter Amount
        WebElement amountInput = wait
                .until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.TXN_AMOUNT_INPUT));
        amountInput.clear();
        amountInput.sendKeys(String.valueOf(amount));

        // 5. Enter Description
        WebElement descInput = wait
                .until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.TXN_DESC_INPUT));
        descInput.clear();
        descInput.sendKeys(description);

        // 6. Submit
        WebElement submitBtn = wait
                .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.TXN_SUBMIT_BUTTON));
        submitBtn.click();

        // 7. Wait for Success Message
        wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.TXN_SUCCESS_MSG));
        // Wait for msg to disappear to avoid obscuring other elements or confirm action
        // completed
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    // ==================== Organization Details Tab Methods ====================

    public void navigateToOrgDetailsTab() {
        System.out.println("Navigating to Organization Details tab...");
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);

        // Check if we're already on the organization-details page
        if (currentUrl.contains("organization-details")) {
            System.out.println("✓ Already on organization-details page, skipping tab navigation");
            return;
        }

        try {
            // Try primary locator
            WebElement tab = wait
                    .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.ORG_DETAIL_TAB));
            tab.click();
            System.out.println("✓ Clicked Organization Details tab using primary locator");
        } catch (TimeoutException e) {
            System.err.println("Primary locator failed. Trying fallback locators...");
            try {
                // Fallback: Try to find tab with correct text 'Organizations details'
                WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[contains(@class, 'tab')][contains(., 'Organizations details')]")));
                tab.click();
                System.out.println("✓ Clicked tab using fallback locator");
            } catch (Exception e2) {
                System.err.println("ERROR: Could not find Organization Details tab");
                System.err.println("Current URL: " + driver.getCurrentUrl());
                System.err.println("Page Title: " + driver.getTitle());
                System.err.println("\nDEBUGGING - Trying multiple tab patterns:");

                // Try multiple patterns
                String[] patterns = {
                        "//div[@role='tab']",
                        "//mat-tab-header//div",
                        "//button[contains(@class, 'mat-tab')]",
                        "//div[contains(@class, 'mat-tab-label')]",
                        "//*[contains(@class, 'tab')]"
                };

                boolean foundAny = false;
                for (String pattern : patterns) {
                    try {
                        List<WebElement> elements = driver.findElements(By.xpath(pattern));
                        if (!elements.isEmpty()) {
                            System.err.println("\nPattern '" + pattern + "' found " + elements.size() + " elements:");
                            for (int i = 0; i < Math.min(elements.size(), 5); i++) {
                                String text = elements.get(i).getText();
                                System.err.println("  [" + (i + 1) + "] '" + text + "'");
                            }
                            foundAny = true;
                        }
                    } catch (Exception ex) {
                    }
                }

                if (!foundAny) {
                    System.err.println("\nNo tabs found! Listing all visible buttons:");
                    try {
                        List<WebElement> buttons = driver.findElements(By.xpath("//button"));
                        for (int i = 0; i < Math.min(buttons.size(), 15); i++) {
                            if (buttons.get(i).isDisplayed()) {
                                System.err.println("  Button: '" + buttons.get(i).getText() + "'");
                            }
                        }
                    } catch (Exception ex) {
                    }
                }
                throw new RuntimeException("Failed to navigate to Organization Details tab", e2);
            }
        }

        // Wait for content to load
        try {
            Thread.sleep(3000); // Increased wait time
        } catch (InterruptedException e) {
        }
        System.out.println("Waiting for Organization Details tab content to load...");
    }

    public boolean isOrgDetailsTabLoaded() {
        // Simple check: verify URL contains "organization-details"
        String currentUrl = driver.getCurrentUrl();
        boolean loaded = currentUrl.contains("organization-details");
        if (!loaded) {
            System.err.println("Organization Details tab not loaded. Current URL: " + currentUrl);
        }
        return loaded;
    }

    public String getCustomerId() {
        System.out.println("Fetching Customer ID from Organizations details tab...");

        // Small wait for page load
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }

        // Try locator from CustomerOrgPageLocators first
        try {
            List<WebElement> elements = driver.findElements(CustomerOrgPageLocators.ORG_DETAILS_CUSTOMER_ID);
            for (WebElement element : elements) {
                String text = element.getText().trim();
                if (!text.isEmpty()) {
                    // Text might be "Customer Id: CUST172" - extract just the value
                    if (text.contains("Customer Id:")) {
                        String value = text.replace("Customer Id:", "").trim();
                        if (!value.isEmpty()) {
                            System.out.println("✓ Customer ID found: " + value);
                            return value;
                        }
                    } else if (!text.equalsIgnoreCase("Customer ID") && !text.equalsIgnoreCase("Customer Id")) {
                        System.out.println("✓ Customer ID found: " + text);
                        return text;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error using primary locator: " + e.getMessage());
        }

        // Fallback: Try additional patterns
        String[] xpaths = {
                // p/strong patterns for the actual DOM structure
                "//p[strong[contains(text(), 'Customer Id')]]/text()[normalize-space()]",
                "//strong[contains(text(), 'Customer Id')]/following-sibling::text()",
                // Input fields
                "//input[@formcontrolname='customerId']",
                "//input[@name='customerId']",
                "//input[@id='customerId']"
        };

        for (String xpath : xpaths) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                for (WebElement element : elements) {
                    String value = element.getText().trim();
                    if (value.isEmpty()) {
                        value = element.getDomProperty("value");
                        if (value == null)
                            value = "";
                        else
                            value = value.trim();
                    }

                    if (!value.isEmpty() && !value.equalsIgnoreCase("Customer ID")
                            && !value.equalsIgnoreCase("Customer Id")) {
                        System.out.println("✓ Customer ID found (fallback): " + value);
                        return value;
                    }
                }
            } catch (Exception e) {
            }
        }

        // Debug: print all p tags with strong children
        System.err.println("\nERROR: Customer ID not found. Printing all <p> tags with <strong>:");
        try {
            List<WebElement> paragraphs = driver.findElements(By.xpath("//p[strong]"));
            System.err.println("Found " + paragraphs.size() + " <p> tags with <strong>:");
            for (int i = 0; i < Math.min(paragraphs.size(), 10); i++) {
                String text = paragraphs.get(i).getText().trim();
                if (!text.isEmpty()) {
                    System.err.println("  [" + (i + 1) + "] " + text);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not enumerate paragraphs: " + e.getMessage());
        }

        return "N/A";
    }

    // ==================== Profile Tab Methods ====================

    public void navigateToProfileTab() {
        System.out.println("Navigating to Profile tab...");
        String currentUrl = driver.getCurrentUrl();

        if (currentUrl.contains("profile")) {
            System.out.println("✓ Already on Profile tab");
            return;
        }

        try {
            // Wait for tab to be present
            WebElement tab = wait
                    .until(ExpectedConditions.presenceOfElementLocated(CustomerOrgPageLocators.PROFILE_TAB));

            System.out.println("Found Profile tab element: " + tab.getText());

            // Use JavaScript click for more reliable navigation
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
            System.out.println("✓ Clicked Profile tab using JS");

            Thread.sleep(2000);

            // Verify navigation succeeded
            String newUrl = driver.getCurrentUrl();
            if (!newUrl.contains("profile")) {
                throw new RuntimeException("Navigation to Profile tab failed. Current URL: " + newUrl);
            }
            System.out.println("✓ Successfully navigated to Profile tab. URL: " + newUrl);

        } catch (Exception e) {
            System.err.println("Failed to navigate to Profile tab: " + e.getMessage());
            System.err.println("Current URL: " + driver.getCurrentUrl());
            throw new RuntimeException("Failed to navigate to Profile tab", e);
        }
    }

    public boolean isProfileTabLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.PROFILE_TABLE));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getProfileTableHeaders() {
        System.out.println("Fetching Profile table headers...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.PROFILE_TABLE_HEADERS));
        List<WebElement> headers = driver.findElements(CustomerOrgPageLocators.PROFILE_TABLE_HEADERS);
        return headers.stream().map(h -> h.getText().trim()).filter(t -> !t.isEmpty()).toList();
    }

    public boolean isEditButtonClickable() {
        try {
            WebElement editBtn = wait
                    .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.PROFILE_EDIT_BUTTON));
            return editBtn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickEditButton() {
        System.out.println("Clicking Edit button...");
        WebElement editBtn = wait
                .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.PROFILE_EDIT_BUTTON));
        editBtn.click();
    }

    public boolean clickViewIconAndVerifyContent() {
        System.out.println("Clicking View icon...");
        try {
            WebElement viewIcon = wait
                    .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.PROFILE_VIEW_ICON));
            viewIcon.click();

            // Wait for modal/content to appear
            Thread.sleep(1500);

            // Check if any text content is visible
            List<WebElement> textElements = driver.findElements(CustomerOrgPageLocators.PROFILE_VIEW_MODAL_TEXT);

            for (WebElement element : textElements) {
                String text = element.getText().trim();
                if (!text.isEmpty() && text.length() > 2) {
                    System.out.println("✓ Found text content: " + text.substring(0, Math.min(50, text.length())));
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Failed to verify view content: " + e.getMessage());
            return false;
        }
    }

    public void searchProfile(String searchText) {
        System.out.println("Searching for profile: " + searchText);
        WebElement searchField = wait
                .until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.PROFILE_SEARCH_FIELD));
        searchField.clear();
        searchField.sendKeys(searchText);

        try {
            Thread.sleep(1000); // Wait for search to filter
        } catch (InterruptedException e) {
        }
    }

    public boolean isSearchFieldFunctional() {
        try {
            WebElement searchField = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.PROFILE_SEARCH_FIELD));
            return searchField.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void applyProfileFilters() {
        System.out.println("Applying profile filters...");
        try {
            // Click filter button to open filter panel
            WebElement filterBtn = wait
                    .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.PROFILE_FILTER_BUTTON));
            filterBtn.click();
            Thread.sleep(1000);

            // Find all filter dropdowns
            List<WebElement> dropdowns = driver.findElements(CustomerOrgPageLocators.PROFILE_FILTER_DROPDOWN);

            // Select first option from each dropdown
            for (WebElement dropdown : dropdowns) {
                if (dropdown.isDisplayed() && dropdown.isEnabled()) {
                    dropdown.click();
                    Thread.sleep(500);

                    // Select first available option
                    try {
                        WebElement firstOption = wait.until(
                                ExpectedConditions.elementToBeClickable(By.xpath("//mat-option[1] | //option[1]")));
                        firstOption.click();
                        Thread.sleep(500);
                    } catch (Exception e) {
                        System.out.println("⚠ Could not select dropdown option");
                    }
                }
            }

            // Try to click Apply button if it exists (some filters auto-apply)
            try {
                WebElement applyBtn = wait.withTimeout(java.time.Duration.ofSeconds(3))
                        .until(ExpectedConditions
                                .elementToBeClickable(CustomerOrgPageLocators.PROFILE_FILTER_APPLY_BUTTON));
                applyBtn.click();
                System.out.println("✓ Apply button clicked");
            } catch (Exception e) {
                System.out.println("⚠ Apply button not found - filter may auto-apply");
            }

            System.out.println("✓ Filters applied");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.err.println("Filter application encountered issues: " + e.getMessage());
        } finally {
            // Reset wait timeout to default
            wait.withTimeout(java.time.Duration.ofSeconds(10));
        }
    }

    public boolean isAddNewButtonClickable() {
        try {
            WebElement addNewBtn = wait
                    .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.PROFILE_ADD_NEW_BUTTON));
            return addNewBtn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickAddNewButton() {
        System.out.println("Clicking Add New button...");
        WebElement addNewBtn = wait
                .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.PROFILE_ADD_NEW_BUTTON));
        addNewBtn.click();
    }

    // ==========================================================================
    // ==================== Roles Tab Methods ====================
    // ==========================================================================

    public void navigateToRolesTab() {
        System.out.println("Navigating to Roles tab...");
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);

        if (currentUrl.contains("roles")) {
            System.out.println("✓ Already on Roles tab");
            return;
        }

        try {
            // Wait for tab to be present
            WebElement tab = wait
                    .until(ExpectedConditions.presenceOfElementLocated(CustomerOrgPageLocators.ROLES_TAB));

            String tabText = tab.getText().trim();
            System.out.println("Found Roles tab element: '" + tabText + "'");

            // Validate the tab text is exactly "Roles" or "Role" (not "Select Roles" or
            // other variations)
            if (!tabText.equals("Roles") && !tabText.equals("Role")) {
                System.err.println(
                        "WARNING: Found element text '" + tabText + "' doesn't match expected 'Roles' or 'Role'");

                // Try to find a better match
                List<WebElement> allTabs = driver
                        .findElements(By.xpath("//a | //div[@role='tab'] | //button[@role='tab']"));
                WebElement correctTab = null;

                System.out.println("Searching through " + allTabs.size() + " tab elements for exact match...");
                for (WebElement t : allTabs) {
                    String text = t.getText().trim();
                    if (text.equals("Roles") || text.equals("Role")) {
                        System.out.println("✓ Found correct tab with text: '" + text + "'");
                        correctTab = t;
                        break;
                    }
                }

                if (correctTab != null) {
                    tab = correctTab;
                    tabText = tab.getText().trim();
                } else {
                    throw new RuntimeException(
                            "Could not find a tab with exact text 'Roles' or 'Role'. Found: '" + tabText + "'");
                }
            }

            // Scroll tab into view first
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", tab);
            Thread.sleep(500);

            // Use JavaScript click for more reliable navigation
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
            System.out.println("✓ Clicked Roles tab using JS");

            Thread.sleep(2000);

            // Verify navigation succeeded
            String newUrl = driver.getCurrentUrl();
            if (!newUrl.contains("roles")) {
                throw new RuntimeException("Navigation to Roles tab failed. Current URL: " + newUrl);
            }
            System.out.println("✓ Successfully navigated to Roles tab. URL: " + newUrl);

        } catch (TimeoutException e) {
            System.err.println("ERROR: Roles tab element not found!");
            System.err.println("Current URL: " + driver.getCurrentUrl());

            // Debug: List all available tabs/links
            System.err.println("\nDEBUG: Searching for available tabs...");
            try {
                List<WebElement> allTabs = driver.findElements(By.xpath("//a | //div[@role='tab']"));
                System.err.println("Found " + allTabs.size() + " tab-like elements:");
                for (int i = 0; i < Math.min(allTabs.size(), 15); i++) {
                    String text = allTabs.get(i).getText().trim();
                    if (!text.isEmpty()) {
                        System.err.println("  [" + (i + 1) + "] '" + text + "'");
                    }
                }
            } catch (Exception ex) {
                System.err.println("Could not enumerate tabs: " + ex.getMessage());
            }

            throw new RuntimeException("Roles tab not found. Check if you're on a customer details page.", e);
        } catch (Exception e) {
            System.err.println("Failed to navigate to Roles tab: " + e.getMessage());
            System.err.println("Current URL: " + driver.getCurrentUrl());
            throw new RuntimeException("Failed to navigate to Roles tab", e);
        }
    }

    public boolean isRolesTabLoaded() {
        try {
            System.out.println("Checking if Roles table is visible...");
            wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.ROLES_TABLE));
            System.out.println("✓ Roles table is visible");
            return true;
        } catch (TimeoutException e) {
            System.err.println("ERROR: Roles table not found within timeout");
            System.err.println("Current URL: " + driver.getCurrentUrl());
            return false;
        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error checking Roles table: " + e.getMessage());
            return false;
        }
    }

    public List<String> getRolesTableHeaders() {
        System.out.println("Fetching Roles table headers...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.ROLES_TABLE_HEADERS));
        List<WebElement> headers = driver.findElements(CustomerOrgPageLocators.ROLES_TABLE_HEADERS);
        return headers.stream().map(h -> h.getText().trim()).filter(t -> !t.isEmpty()).toList();
    }

    public int getRolesTableRowCount() {
        try {
            return driver.findElements(CustomerOrgPageLocators.ROLES_TABLE_ROWS).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isRolesEditIconClickable() {
        try {
            WebElement editBtn = wait
                    .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.ROLES_EDIT_BUTTON));
            return editBtn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickRolesEditIcon() {
        System.out.println("Clicking Roles Edit icon...");
        WebElement editBtn = wait
                .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.ROLES_EDIT_BUTTON));
        editBtn.click();

        try {
            Thread.sleep(2000); // Wait for page/modal to load
        } catch (InterruptedException e) {
        }
    }

    public boolean isRolesViewIconClickable() {
        try {
            WebElement viewBtn = wait
                    .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.ROLES_VIEW_BUTTON));
            return viewBtn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickRolesViewIcon() {
        System.out.println("Clicking Roles View icon...");
        WebElement viewBtn = wait
                .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.ROLES_VIEW_BUTTON));
        viewBtn.click();

        try {
            Thread.sleep(2000); // Wait for view modal/page to load
        } catch (InterruptedException e) {
        }
    }

    public boolean isRolesViewModalDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.ROLES_VIEW_MODAL))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void searchRole(String roleName) {
        System.out.println("Searching for role: " + roleName);
        WebElement searchField = wait
                .until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.ROLES_SEARCH_FIELD));
        searchField.clear();
        searchField.sendKeys(roleName);

        try {
            Thread.sleep(1500); // Wait for search to filter results
        } catch (InterruptedException e) {
        }
    }

    public boolean isNoDataMessageDisplayed() {
        try {
            return wait
                    .until(ExpectedConditions.visibilityOfElementLocated(CustomerOrgPageLocators.ROLES_NO_DATA_MESSAGE))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void applyRolesStatusFilter(String status) {
        System.out.println("Applying Roles status filter: " + status);
        try {
            // Scroll to top first to avoid overlays
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
            Thread.sleep(500);

            // Click filter button
            WebElement filterBtn = wait
                    .until(ExpectedConditions.presenceOfElementLocated(CustomerOrgPageLocators.ROLES_FILTER_BUTTON));

            // Scroll into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", filterBtn);
            Thread.sleep(300);

            // Try normal click first, fallback to JS click
            try {
                wait.until(ExpectedConditions.elementToBeClickable(filterBtn)).click();
            } catch (Exception e) {
                System.out.println("Normal click failed, using JS click");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", filterBtn);
            }
            Thread.sleep(800);

            // Select status option based on provided status
            By statusLocator;
            switch (status.toLowerCase()) {
                case "active":
                    statusLocator = CustomerOrgPageLocators.ROLES_FILTER_OPTION_ACTIVE;
                    break;
                case "inactive":
                    statusLocator = CustomerOrgPageLocators.ROLES_FILTER_OPTION_INACTIVE;
                    break;
                default:
                    statusLocator = CustomerOrgPageLocators.ROLES_FILTER_OPTION_ALL;
            }

            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(statusLocator));
            option.click();
            Thread.sleep(500);

            // Click Apply button
            WebElement applyBtn = wait
                    .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.ROLES_FILTER_APPLY_BUTTON));
            applyBtn.click();

            Thread.sleep(2000); // Wait for filtered results
            System.out.println("✓ Filter applied: " + status);
        } catch (Exception e) {
            System.err.println("Error applying filter: " + e.getMessage());
            throw new RuntimeException("Failed to apply Roles filter", e);
        }
    }

    public List<String> getRoleStatusValues() {
        System.out.println("Fetching all role status values...");
        try {
            // Status is typically in the 4th column based on headers: Role Name, Users,
            // Permissions, Status
            List<WebElement> statusCells = driver
                    .findElements(By.xpath("//table//tbody//tr//td[position()=4]"));
            return statusCells.stream()
                    .map(cell -> cell.getText().trim())
                    .filter(text -> !text.isEmpty())
                    .toList();
        } catch (Exception e) {
            System.err.println("Error fetching status values: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public boolean isAddNewRoleButtonClickable() {
        try {
            WebElement addNewBtn = wait
                    .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.ROLES_ADD_NEW_BUTTON));
            return addNewBtn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickAddNewRole() {
        System.out.println("Clicking Add New Role button...");
        WebElement addNewBtn = wait
                .until(ExpectedConditions.elementToBeClickable(CustomerOrgPageLocators.ROLES_ADD_NEW_BUTTON));
        addNewBtn.click();

        try {
            Thread.sleep(2000); // Wait for page to load
        } catch (InterruptedException e) {
        }
    }

    public boolean isOnAddRolePage() {
        String currentUrl = driver.getCurrentUrl();
        return currentUrl.contains("roles/add");
    }

    public List<String> getAccordionLabels() {
        System.out.println("Fetching accordion labels from Add/Edit Role form...");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(CustomerOrgPageLocators.ROLE_FORM_ACCORDIONS));
            List<WebElement> accordions = driver.findElements(CustomerOrgPageLocators.ROLE_FORM_ACCORDIONS);
            return accordions.stream()
                    .map(a -> a.getText().trim())
                    .filter(t -> !t.isEmpty())
                    .toList();
        } catch (Exception e) {
            System.err.println("Error fetching accordion labels: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public String getFirstRoleName() {
        System.out.println("Fetching first role name from table...");
        try {
            // Role Name is in the 1st column
            WebElement firstRoleCell = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath("//table//tbody//tr[1]//td[position()=1]")));
            return firstRoleCell.getText().trim();
        } catch (Exception e) {
            System.err.println("Error fetching first role name: " + e.getMessage());
            return "";
        }
    }
}
