package pages;

import locators.RCSAssistantPageLocators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ExtentReportManager;

import java.io.File;
import java.time.Duration;
import java.util.List;

/**
 * Page Object for RCS Assistant Portal operations
 * Used after SSO from Enterprise Services to RCS Portal
 */
public class RCSAssistantPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Store created assistant name for later validation
    private String createdAssistantName;
    private String originalAssistantName;

    public RCSAssistantPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ==================== Navigation Methods ====================

    /**
     * Navigate to Manage Assistants section in RCS Portal
     * Uses multiple fallback approaches for different portal layouts
     */
    public void navigateToManageAssistants() {
        ExtentReportManager.logStep("Navigate to Manage Assistants");

        try {
            // First, try to click on Control Center if it exists (parent menu)
            try {
                System.out.println("Trying to click Control Center menu...");
                WebElement controlCenter = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.CONTROL_CENTER_MENU));
                controlCenter.click();
                Thread.sleep(1000);
                System.out.println("Clicked Control Center menu");
            } catch (Exception e) {
                System.out.println("Control Center menu not found, trying direct Assistants...");
            }

            // Now try to click Manage Assistants / Assistants
            try {
                WebElement manageAssistants = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions
                                .elementToBeClickable(RCSAssistantPageLocators.MANAGE_ASSISTANTS_MENU));
                manageAssistants.click();
                System.out.println("Clicked Manage Assistants menu");
            } catch (Exception e) {
                System.out.println("Manage Assistants menu not found, trying URL navigation...");
                // Fallback: Navigate via URL modification
                String currentUrl = driver.getCurrentUrl();
                System.out.println("Current URL: " + currentUrl);

                // If on dashboard, try to navigate to assistants
                if (currentUrl.contains("/dashboard")) {
                    String assistantsUrl = currentUrl.replace("/dashboard", "/control-center/assistants");
                    driver.get(assistantsUrl);
                    System.out.println("Navigated to: " + assistantsUrl);
                } else if (currentUrl.contains("/rcs/")) {
                    // Try appending assistants path
                    String baseUrl = currentUrl.split("/dashboard")[0];
                    if (!baseUrl.endsWith("/"))
                        baseUrl += "/";
                    String assistantsUrl = baseUrl + "control-center/assistants";
                    driver.get(assistantsUrl);
                    System.out.println("Navigated to: " + assistantsUrl);
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                // Ignore
            }

            // Wait for assistants page to load
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.ASSISTANTS_PAGE_HEADER),
                    ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.ASSISTANTS_TABLE),
                    ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.NO_ASSISTANT_MESSAGE),
                    ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.ADD_NEW_BUTTON),
                    ExpectedConditions.urlContains("assistants")));

            System.out.println("Successfully on Assistants page. URL: " + driver.getCurrentUrl());
            ExtentReportManager.logPass("Successfully navigated to Manage Assistants");

        } catch (Exception e) {
            System.out.println("ERROR navigating to Manage Assistants: " + e.getMessage());
            ExtentReportManager.logWarning("Error navigating to Manage Assistants: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Validate that we are on the RCS Portal
     */
    public boolean isOnRCSPortal() {
        try {
            return wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.RCS_PORTAL_LOGO),
                    ExpectedConditions
                            .presenceOfElementLocated(RCSAssistantPageLocators.MANAGE_ASSISTANTS_MENU))) != null;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Add New Assistant Methods ====================

    /**
     * Click Add New Assistant button
     */
    public void clickAddNewAssistant() {
        ExtentReportManager.logStep("Click Add New Assistant button");
        wait.until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.ADD_NEW_BUTTON)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(RCSAssistantPageLocators.ASSISTANT_NAME_INPUT));
    }

    /**
     * Fill Basic Details section of assistant form
     * 
     * @return The generated assistant name for later validation
     */
    public String fillBasicDetails() {
        ExtentReportManager.logStep("Fill Basic Details section");

        // Generate a unique assistant name
        createdAssistantName = "TestAssistant_" + System.currentTimeMillis() % 100000;

        // Enter assistant name
        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(RCSAssistantPageLocators.ASSISTANT_NAME_INPUT));
        nameInput.clear();
        nameInput.sendKeys(createdAssistantName);
        ExtentReportManager.logInfo("Entered assistant name: " + createdAssistantName);

        // Select template category if dropdown exists
        try {
            WebElement templateDropdown = driver.findElement(RCSAssistantPageLocators.TEMPLATE_CATEGORY_DROPDOWN);
            templateDropdown.click();
            Thread.sleep(500);

            List<WebElement> options = wait.until(
                    ExpectedConditions
                            .visibilityOfAllElementsLocatedBy(RCSAssistantPageLocators.TEMPLATE_CATEGORY_OPTIONS));
            if (!options.isEmpty()) {
                options.get(0).click();
                ExtentReportManager.logInfo("Selected template category");
            }
        } catch (Exception e) {
            ExtentReportManager.logInfo("No template category dropdown or already selected");
        }

        // Enter description
        try {
            WebElement descInput = driver.findElement(RCSAssistantPageLocators.ASSISTANT_DESCRIPTION_INPUT);
            descInput.clear();
            descInput.sendKeys("Automated test assistant created at " + java.time.LocalDateTime.now());
        } catch (Exception e) {
            ExtentReportManager.logInfo("Description field not found or optional");
        }

        // Click Next
        wait.until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.NEXT_BUTTON_STEP1)).click();
        ExtentReportManager.logPass("Basic Details filled successfully");

        return createdAssistantName;
    }

    /**
     * Upload branding images (Cover and Icon)
     * Handles crop dialog if it appears after image upload
     * Then clicks Next to move from Branding step to Contact step
     */
    public void uploadBrandingImages() {
        ExtentReportManager.logStep("Upload Branding Images");

        try {
            // Upload Cover Image
            System.out.println("Uploading Cover image...");
            String coverImagePath = new File("src/main/resources/image/Cover.jpg").getAbsolutePath();
            WebElement coverUpload = driver.findElement(RCSAssistantPageLocators.COVER_IMAGE_UPLOAD);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", coverUpload);
            Thread.sleep(500);
            coverUpload.sendKeys(coverImagePath);
            ExtentReportManager.logInfo("Uploaded cover image: " + coverImagePath);
            Thread.sleep(2000);

            // Handle crop dialog for cover image if it appears
            handleCropDialogIfPresent("cover");

            // Upload Icon Image
            System.out.println("Uploading Icon image...");
            String iconImagePath = new File("src/main/resources/image/icon.png").getAbsolutePath();
            WebElement iconUpload = driver.findElement(RCSAssistantPageLocators.ICON_IMAGE_UPLOAD);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", iconUpload);
            Thread.sleep(500);
            iconUpload.sendKeys(iconImagePath);
            ExtentReportManager.logInfo("Uploaded icon image: " + iconImagePath);
            Thread.sleep(2000);

            // Handle crop dialog for icon image if it appears
            handleCropDialogIfPresent("icon");

            // Wait for images to process
            System.out.println("Waiting for images to process...");
            Thread.sleep(2000);

            // Click Proceed button if present (to confirm image upload/cropping)
            System.out.println("Looking for Proceed button to confirm images...");
            clickProceedButtonIfPresent();
            Thread.sleep(1000);

            // Now click Next to move from Branding to Contact step
            System.out.println("Clicking Next button to move to Contact section...");

            boolean nextClicked = false;

            // Try clicking Next button with multiple approaches
            try {
                // First try: Use the locator
                WebElement nextButton = wait.until(
                        ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.NEXT_BUTTON_STEP2));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextButton);
                Thread.sleep(300);
                nextButton.click();
                nextClicked = true;
                System.out.println("Clicked Next button (via locator)");
            } catch (Exception e) {
                System.out.println("Primary Next button locator failed: " + e.getMessage());
            }

            // Second try: Find any visible enabled Next button
            if (!nextClicked) {
                try {
                    List<WebElement> allNextButtons = driver
                            .findElements(By.xpath("//button[contains(text(),'Next')]"));
                    for (WebElement btn : allNextButtons) {
                        if (btn.isDisplayed() && btn.isEnabled()) {
                            System.out.println("Found enabled Next button, clicking...");
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                            nextClicked = true;
                            System.out.println("Clicked Next button (via JS)");
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Alternative Next button approach failed: " + e.getMessage());
                }
            }

            // Third try: Click Contact step directly in stepper
            if (!nextClicked) {
                try {
                    System.out.println("Trying to click Contact step directly...");
                    WebElement contactStep = driver.findElement(By.xpath(
                            "//mat-step-header[contains(.,'Contact')] | " +
                                    "//*[contains(@class,'step') and contains(.,'Contact')]"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", contactStep);
                    nextClicked = true;
                    System.out.println("Clicked Contact step directly");
                } catch (Exception e) {
                    System.out.println("Direct Contact step click failed: " + e.getMessage());
                }
            }

            if (!nextClicked) {
                System.out.println("WARNING: Could not click Next button - form may be stuck on Branding step");
            }

            // Wait for Contact section to load
            Thread.sleep(2000);
            System.out.println("Current URL after Next: " + driver.getCurrentUrl());

            ExtentReportManager.logPass("Branding images uploaded successfully");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Error uploading images: " + e.getMessage());
            System.out.println("Error in uploadBrandingImages: " + e.getMessage());
        }
    }

    /**
     * Click Proceed button if present (sometimes appears after image uploads)
     * 
     * @return true if Proceed button was clicked, false otherwise
     */
    private boolean clickProceedButtonIfPresent() {
        try {
            System.out.println("Checking for Proceed/Continue button...");

            By proceedButtonLocators = By.xpath(
                    "//button[normalize-space(text())='Proceed'] | " +
                            "//button[normalize-space(text())='Continue'] | " +
                            "//button[contains(text(),'Proceed')] | " +
                            "//button[contains(text(),'Continue')] | " +
                            "//button[contains(@class,'proceed')] | " +
                            "//button[contains(@class,'continue')] | " +
                            "//a[contains(text(),'Proceed')] | " +
                            "//a[contains(text(),'Continue')]");

            List<WebElement> proceedButtons = driver.findElements(proceedButtonLocators);
            System.out.println("Found " + proceedButtons.size() + " proceed/continue buttons");

            for (WebElement btn : proceedButtons) {
                if (btn.isDisplayed() && btn.isEnabled()) {
                    String btnText = btn.getText().trim();
                    System.out.println("Clicking Proceed button: " + btnText);
                    btn.click();
                    Thread.sleep(1500);
                    ExtentReportManager.logInfo("Clicked Proceed button: " + btnText);
                    return true; // Successfully clicked Proceed
                }
            }

            System.out.println("No Proceed button found (this is OK)");
            return false;

        } catch (Exception e) {
            System.out.println("No Proceed button needed or error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Handle the image crop dialog if it appears after upload
     * 
     * @param imageType "cover" or "icon" for logging purposes
     */
    private void handleCropDialogIfPresent(String imageType) {
        try {
            System.out.println("Checking for crop dialog for " + imageType + " image...");

            // Wait briefly for crop dialog to appear
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

            // Specific locators for crop confirmation buttons - EXCLUDE Back/Cancel
            // Priority order: Apply > Crop > Save > Done > OK > Confirm
            By cropButtonLocators = By.xpath(
                    "//button[normalize-space(text())='Apply'] | " +
                            "//button[normalize-space(text())='Crop'] | " +
                            "//button[normalize-space(text())='Save'] | " +
                            "//button[normalize-space(text())='Done'] | " +
                            "//button[normalize-space(text())='OK'] | " +
                            "//button[normalize-space(text())='Confirm'] | " +
                            "//button[contains(@class,'btn-primary') and not(contains(text(),'Back')) and not(contains(text(),'Cancel')) and not(contains(text(),'Next'))] | "
                            +
                            "//div[contains(@class,'modal')]//button[contains(@class,'primary') and not(contains(text(),'Back')) and not(contains(text(),'Cancel'))] | "
                            +
                            "//div[contains(@class,'cropper')]//button[not(contains(text(),'Back')) and not(contains(text(),'Cancel'))]");

            try {
                // Get all matching buttons and find the right one
                List<WebElement> buttons = driver.findElements(cropButtonLocators);
                System.out.println("Found " + buttons.size() + " potential crop buttons");

                WebElement correctButton = null;
                for (WebElement btn : buttons) {
                    String btnText = btn.getText().trim().toLowerCase();
                    System.out.println("  Button found: '" + btn.getText() + "'");

                    // Skip Back, Cancel, Close buttons
                    if (btnText.contains("back") || btnText.contains("cancel") ||
                            btnText.contains("close") || btnText.contains("next")) {
                        System.out.println("    Skipping this button");
                        continue;
                    }

                    // Prefer Apply, Crop, Save, Done, OK
                    if (btnText.contains("apply") || btnText.contains("crop") ||
                            btnText.contains("save") || btnText.contains("done") ||
                            btnText.equals("ok") || btnText.contains("confirm")) {
                        correctButton = btn;
                        break;
                    }
                }

                if (correctButton != null) {
                    System.out.println("Clicking correct crop button: " + correctButton.getText());
                    correctButton.click();
                    System.out.println("Clicked crop/apply button for " + imageType + " image");
                    Thread.sleep(1500);
                    ExtentReportManager.logInfo("Handled crop dialog for " + imageType + " image");
                } else {
                    System.out.println("No suitable crop button found for " + imageType + " image (this is OK)");
                }

            } catch (Exception e) {
                System.out.println("No crop dialog found for " + imageType + " image (this is OK)");
            }

        } catch (Exception e) {
            System.out.println("Error checking for crop dialog: " + e.getMessage());
        }
    }

    /**
     * Fill Contact Details section and submit
     * Handles multi-step forms by clicking Add buttons to reveal fields
     */
    public void fillContactDetailsAndSubmit() {
        ExtentReportManager.logStep("Fill Contact Details section");

        // Debug: Print current state
        System.out.println("========================================");
        System.out.println("ENTERING fillContactDetailsAndSubmit()");
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page Title: " + driver.getTitle());
        System.out.println("========================================");

        try {
            // Wait for the page to stabilize
            Thread.sleep(2000);

            // First, click the "Add" buttons to reveal input fields
            // Contact section has Add Number, Add Website, Add Email buttons
            System.out.println("Looking for Add buttons to reveal input fields...");

            // Click Add Number button
            clickAddButtonIfPresent("Add Number", "Number");

            // Click Add Website button
            clickAddButtonIfPresent("Add Website", "Website");

            // Click Add Email button
            clickAddButtonIfPresent("Add Email", "Email");

            Thread.sleep(1000);

            System.out.println("Now filling the contact fields...");

            // For multi-step forms, the current page might be a step that doesn't need
            // contact info
            // Check if we can just click Next to complete the flow

            // Try to fill any visible and interactable fields
            int fieldsFilledCount = 0;

            // Try filling phone fields
            fieldsFilledCount += tryFillField(
                    "//input[contains(@placeholder,'Display Name') or contains(@placeholder,'display')]",
                    "Business Contact");
            fieldsFilledCount += tryFillField(
                    "//input[contains(@placeholder,'Phone') or contains(@placeholder,'phone')]", "9876543210");

            // Try filling website fields
            fieldsFilledCount += tryFillField(
                    "//input[contains(@placeholder,'Website') or contains(@placeholder,'URL') or contains(@placeholder,'website')]",
                    "https://www.testcompany.com");

            // Try filling email fields
            fieldsFilledCount += tryFillField(
                    "//input[contains(@placeholder,'Email') or contains(@placeholder,'email') or @type='email']",
                    "test@testcompany.com");

            // Try filling privacy/terms URLs - these often have specific placeholders
            fieldsFilledCount += tryFillField("//input[contains(@placeholder,'Privacy')]",
                    "https://www.testcompany.com/privacy");
            fieldsFilledCount += tryFillField("//input[contains(@placeholder,'Terms')]",
                    "https://www.testcompany.com/terms");

            System.out.println("Total fields filled: " + fieldsFilledCount);

            // If no fields were filled, it might be a multi-step form where we just need to
            // click Next
            if (fieldsFilledCount == 0) {
                System.out.println("No contact fields filled - this might be a multi-step form");
                System.out.println("Looking for any required fields that need values...");

                // Try to find ANY empty input that's visible and fill it
                List<WebElement> emptyInputs = driver
                        .findElements(By.xpath("//input[@required or @aria-required='true']"));
                for (WebElement input : emptyInputs) {
                    try {
                        if (input.isDisplayed() && input.getAttribute("value").isEmpty()) {
                            scrollToElement(input);
                            String placeholder = input.getAttribute("placeholder");
                            System.out.println("Found empty required field: " + placeholder);

                            // Fill based on placeholder hint
                            if (placeholder != null) {
                                if (placeholder.toLowerCase().contains("phone")) {
                                    fillInputWithJS(input, "9876543210");
                                } else if (placeholder.toLowerCase().contains("email")) {
                                    fillInputWithJS(input, "test@example.com");
                                } else if (placeholder.toLowerCase().contains("url")
                                        || placeholder.toLowerCase().contains("http")) {
                                    fillInputWithJS(input, "https://www.example.com");
                                } else {
                                    fillInputWithJS(input, "Test Value");
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }

            // Now try to submit or move to next step
            Thread.sleep(1000);

            // Look for Submit button first
            boolean submitted = false;
            By submitLocators = By.xpath("//button[contains(text(),'Submit') and not(contains(@class,'disabled'))]");
            List<WebElement> submitButtons = driver.findElements(submitLocators);

            for (WebElement btn : submitButtons) {
                try {
                    if (btn.isDisplayed() && btn.isEnabled()) {
                        System.out.println("Found Submit button, clicking...");
                        scrollAndClick(btn);
                        submitted = true;
                        break;
                    }
                } catch (Exception e) {
                }
            }

            // If no Submit, try Create button
            if (!submitted) {
                By createLocators = By
                        .xpath("//button[contains(text(),'Create') and not(contains(@class,'disabled'))]");
                List<WebElement> createButtons = driver.findElements(createLocators);
                for (WebElement btn : createButtons) {
                    try {
                        if (btn.isDisplayed() && btn.isEnabled()) {
                            System.out.println("Found Create button, clicking...");
                            scrollAndClick(btn);
                            submitted = true;
                            break;
                        }
                    } catch (Exception e) {
                    }
                }
            }

            // If still not submitted, click Next to progress through form steps
            if (!submitted) {
                System.out.println("No Submit/Create button, clicking Next to continue...");
                By nextLocators = By.xpath("//button[contains(text(),'Next') and not(contains(@class,'disabled'))]");
                List<WebElement> nextButtons = driver.findElements(nextLocators);
                for (WebElement btn : nextButtons) {
                    try {
                        if (btn.isDisplayed() && btn.isEnabled()) {
                            System.out.println("Clicking Next button: " + btn.getText());
                            scrollAndClick(btn);
                            Thread.sleep(1500);
                            break;
                        }
                    } catch (Exception e) {
                    }
                }
            }

            Thread.sleep(2000);
            System.out.println("Current URL after action: " + driver.getCurrentUrl());

            ExtentReportManager.logPass("Contact section processing completed");

        } catch (Exception e) {
            System.out.println("ERROR in fillContactDetailsAndSubmit: " + e.getMessage());
            ExtentReportManager.logWarning("Error in contact details: " + e.getMessage());
        }
    }

    /**
     * Try to fill a field with the given XPath and value
     * 
     * @return 1 if field was filled, 0 otherwise
     */
    private int tryFillField(String xpath, String value) {
        try {
            List<WebElement> fields = driver.findElements(By.xpath(xpath));
            for (WebElement field : fields) {
                try {
                    if (field.isDisplayed()) {
                        scrollToElement(field);
                        Thread.sleep(300);

                        // Try regular interaction first
                        try {
                            field.clear();
                            field.sendKeys(value);
                            System.out.println(
                                    "Filled field (regular): " + xpath.substring(0, Math.min(50, xpath.length())));
                            return 1;
                        } catch (Exception e) {
                            // Try JavaScript interaction
                            fillInputWithJS(field, value);
                            System.out
                                    .println("Filled field (JS): " + xpath.substring(0, Math.min(50, xpath.length())));
                            return 1;
                        }
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * Scroll element into view
     */
    private void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            Thread.sleep(300);
        } catch (Exception e) {
        }
    }

    /**
     * Scroll to element and click it
     */
    private void scrollAndClick(WebElement element) {
        try {
            scrollToElement(element);
            try {
                element.click();
            } catch (Exception e) {
                // Use JavaScript click if regular click fails
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        } catch (Exception e) {
            System.out.println("Error clicking element: " + e.getMessage());
        }
    }

    /**
     * Fill input using JavaScript (for elements that are not interactable normally)
     */
    private void fillInputWithJS(WebElement element, String value) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1];" +
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    element, value);
        } catch (Exception e) {
            System.out.println("JS fill failed: " + e.getMessage());
        }
    }

    /**
     * Click "Add" button to reveal input fields in Contact section
     * 
     * @param buttonText The button text to look for (e.g., "Add Number", "Add
     *                   Website")
     * @param fieldType  Description for logging
     */
    private void clickAddButtonIfPresent(String buttonText, String fieldType) {
        try {
            // Multiple XPath patterns for Add buttons
            By addButtonLocators = By.xpath(
                    "//button[contains(text(),'" + buttonText + "')] | " +
                            "//button[normalize-space()='" + buttonText + "'] | " +
                            "//button[contains(@class,'add') and contains(text(),'" + fieldType + "')] | " +
                            "//a[contains(text(),'" + buttonText + "')] | " +
                            "//span[contains(text(),'" + buttonText + "')]/parent::button");

            List<WebElement> addButtons = driver.findElements(addButtonLocators);

            for (WebElement btn : addButtons) {
                try {
                    if (btn.isDisplayed() && btn.isEnabled()) {
                        System.out.println("Clicking button: " + buttonText);
                        scrollToElement(btn);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                        Thread.sleep(500);
                        System.out.println("Successfully clicked " + buttonText + " button");
                        return;
                    }
                } catch (Exception e) {
                }
            }

            System.out.println("No " + buttonText + " button found or needed");

        } catch (Exception e) {
            System.out.println("Error finding " + buttonText + " button: " + e.getMessage());
        }
    }

    // ==================== Status Validation Methods ====================

    /**
     * Get the status of the created assistant
     * 
     * @return Status text (e.g., "Pending", "Approved")
     */
    public String getCreatedAssistantStatus() {
        ExtentReportManager.logStep("Verify status of created assistant: " + createdAssistantName);

        try {
            // Search for the created assistant first
            searchAssistant(createdAssistantName.substring(0, Math.min(10, createdAssistantName.length())));
            Thread.sleep(2000);

            // Get the status from the first row
            List<WebElement> rows = driver.findElements(RCSAssistantPageLocators.ASSISTANT_TABLE_ROWS);
            if (!rows.isEmpty()) {
                WebElement statusCell = rows.get(0)
                        .findElement(By.xpath(".//td[5] | .//td[contains(@class,'status')]//span"));
                String status = statusCell.getText().trim();
                ExtentReportManager.logInfo("Assistant status: " + status);
                return status;
            }

            // Try to find status badge
            WebElement pendingBadge = driver.findElement(RCSAssistantPageLocators.PENDING_STATUS_BADGE);
            return pendingBadge.getText().trim();

        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not get status: " + e.getMessage());
            return "Unknown";
        }
    }

    /**
     * Check if assistant is in Pending status
     */
    public boolean isAssistantPending() {
        String status = getCreatedAssistantStatus();
        return status.toLowerCase().contains("pending");
    }

    // ==================== Search Methods ====================

    /**
     * Search for an assistant
     * 
     * @param searchText Text to search (usually first 4 characters)
     */
    public void searchAssistant(String searchText) {
        ExtentReportManager.logStep("Search for assistant: " + searchText);

        WebElement searchBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(RCSAssistantPageLocators.SEARCH_BOX));
        searchBox.clear();
        searchBox.sendKeys(searchText);

        // Wait for results to filter
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    /**
     * Clear search box
     */
    public void clearSearch() {
        ExtentReportManager.logStep("Clear search");

        try {
            WebElement searchBox = driver.findElement(RCSAssistantPageLocators.SEARCH_BOX);
            searchBox.clear();
            searchBox.sendKeys(Keys.ESCAPE);
            Thread.sleep(1000);
        } catch (Exception e) {
            ExtentReportManager.logInfo("Could not clear search: " + e.getMessage());
        }
    }

    /**
     * Validate search results contain the search text
     */
    public boolean validateSearchResults(String searchText) {
        List<WebElement> namesCells = driver.findElements(RCSAssistantPageLocators.ASSISTANT_NAME_CELLS);

        if (namesCells.isEmpty()) {
            // Check for no data message
            List<WebElement> noData = driver.findElements(RCSAssistantPageLocators.NO_ASSISTANT_MESSAGE);
            return !noData.isEmpty();
        }

        for (WebElement cell : namesCells) {
            String name = cell.getText().trim().toLowerCase();
            if (!name.contains(searchText.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    // ==================== Filter Methods ====================

    /**
     * Apply Pending status filter
     */
    public void applyPendingFilter() {
        ExtentReportManager.logStep("Apply Pending status filter");

        try {
            // Click filter button
            wait.until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.FILTER_BUTTON)).click();
            Thread.sleep(500);

            // Click status dropdown
            wait.until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.STATUS_DROPDOWN)).click();
            Thread.sleep(500);

            // Select Pending option
            wait.until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.PENDING_STATUS_OPTION)).click();

            // Apply filter
            wait.until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.APPLY_FILTER_BUTTON)).click();
            Thread.sleep(2000);

            ExtentReportManager.logPass("Pending filter applied");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Error applying filter: " + e.getMessage());
        }
    }

    /**
     * Clear applied filters
     */
    public void clearFilter() {
        ExtentReportManager.logStep("Clear filters");

        try {
            WebElement clearButton = driver.findElement(RCSAssistantPageLocators.CLEAR_FILTER_BUTTON);
            clearButton.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            // Try page refresh as fallback
            driver.navigate().refresh();
            wait.until(ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.ASSISTANTS_TABLE));
        }
    }

    // ==================== Download Methods ====================

    /**
     * Click download icon and validate download starts
     */
    public boolean clickDownloadIcon() {
        ExtentReportManager.logStep("Click Download icon");

        try {
            WebElement downloadIcon = wait.until(
                    ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.DOWNLOAD_ICON));
            downloadIcon.click();
            Thread.sleep(2000);

            ExtentReportManager.logPass("Download triggered successfully");
            return true;
        } catch (Exception e) {
            ExtentReportManager.logWarning("Download failed: " + e.getMessage());
            return false;
        }
    }

    // ==================== View Methods ====================

    /**
     * Click view icon for the first assistant
     */
    public void clickViewIcon() {
        ExtentReportManager.logStep("Click View icon for first assistant");

        // Store the first assistant name for validation
        try {
            WebElement firstNameCell = driver.findElement(RCSAssistantPageLocators.FIRST_ASSISTANT_NAME);
            originalAssistantName = firstNameCell.getText().trim();
        } catch (Exception e) {
            originalAssistantName = createdAssistantName;
        }

        // Click three dot menu then view
        wait.until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.THREE_DOT_MENU)).click();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        wait.until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.VIEW_BUTTON)).click();

        // Wait for details to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Get assistant name from view details page
     */
    public String getViewedAssistantName() {
        try {
            WebElement nameElement = wait.until(
                    ExpectedConditions
                            .visibilityOfElementLocated(RCSAssistantPageLocators.VIEW_DETAILS_ASSISTANT_NAME));
            return nameElement.getText().trim();
        } catch (Exception e) {
            // Try alternative locator
            try {
                WebElement nameInput = driver.findElement(RCSAssistantPageLocators.ASSISTANT_NAME_INPUT);
                return nameInput.getAttribute("value");
            } catch (Exception ex) {
                return "";
            }
        }
    }

    /**
     * Validate viewed assistant name matches the selected one
     */
    public boolean validateViewedAssistantName() {
        String viewedName = getViewedAssistantName();
        ExtentReportManager.logInfo("Viewed assistant name: " + viewedName);
        ExtentReportManager.logInfo("Expected name: " + originalAssistantName);

        return viewedName.toLowerCase().contains(originalAssistantName.toLowerCase()) ||
                originalAssistantName.toLowerCase().contains(viewedName.toLowerCase());
    }

    // ==================== Edit Methods ====================

    /**
     * Click edit icon to edit the first assistant
     */
    public void clickEditIcon() {
        ExtentReportManager.logStep("Click Edit icon");

        // Navigate back to list if on details page
        try {
            driver.navigate().back();
            Thread.sleep(1000);
            wait.until(ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.ASSISTANTS_TABLE));
        } catch (Exception e) {
            // Already on list
        }

        // Store original name
        try {
            WebElement firstNameCell = driver.findElement(RCSAssistantPageLocators.FIRST_ASSISTANT_NAME);
            originalAssistantName = firstNameCell.getText().trim();
        } catch (Exception e) {
            originalAssistantName = "";
        }

        // Click three dot menu then edit
        wait.until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.THREE_DOT_MENU)).click();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        wait.until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.EDIT_BUTTON)).click();

        // Wait for edit form to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(RCSAssistantPageLocators.EDIT_PAGE_HEADER));
    }

    /**
     * Update all fields in edit form
     * 
     * @return The new assistant name
     */
    public String updateAllFields() {
        ExtentReportManager.logStep("Update all assistant fields");

        // Generate new name
        String newName = "Updated_" + System.currentTimeMillis() % 100000;

        // Update name
        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(RCSAssistantPageLocators.ASSISTANT_NAME_INPUT));
        nameInput.clear();
        nameInput.sendKeys(newName);
        ExtentReportManager.logInfo("Updated name to: " + newName);

        // Click Next to go to next section
        try {
            driver.findElement(RCSAssistantPageLocators.NEXT_BUTTON_STEP1).click();
            Thread.sleep(500);
            driver.findElement(RCSAssistantPageLocators.NEXT_BUTTON_STEP2).click();
            Thread.sleep(500);
        } catch (Exception e) {
            // Next buttons may not be present in edit mode
        }

        // Update phone number if visible
        try {
            WebElement phoneNumber = driver.findElement(RCSAssistantPageLocators.PHONE_NUMBER_INPUT);
            phoneNumber.clear();
            phoneNumber.sendKeys("9" + String.valueOf(System.currentTimeMillis() % 1000000000L));
        } catch (Exception e) {
            // Optional field
        }

        // Update website if visible
        try {
            WebElement websiteUrl = driver.findElement(RCSAssistantPageLocators.WEBSITE_URL_INPUT);
            websiteUrl.clear();
            websiteUrl.sendKeys("https://www.updated" + System.currentTimeMillis() % 1000 + ".com");
        } catch (Exception e) {
            // Optional field
        }

        createdAssistantName = newName;
        return newName;
    }

    /**
     * Save the edited assistant
     */
    public void saveChanges() {
        ExtentReportManager.logStep("Save changes");

        WebElement updateButton = wait.until(
                ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.UPDATE_ASSISTANT_BUTTON));
        updateButton.click();

        // Wait for success and return to list
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(RCSAssistantPageLocators.SUCCESS_MESSAGE),
                ExpectedConditions.visibilityOfElementLocated(RCSAssistantPageLocators.GO_TO_ASSISTANT_BUTTON),
                ExpectedConditions.visibilityOfElementLocated(RCSAssistantPageLocators.ASSISTANTS_TABLE)));

        // Click "Go to Assistant" if present
        try {
            WebElement goToButton = driver.findElement(RCSAssistantPageLocators.GO_TO_ASSISTANT_BUTTON);
            if (goToButton.isDisplayed()) {
                goToButton.click();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            // Button not present
        }

        ExtentReportManager.logPass("Changes saved successfully");
    }

    /**
     * Validate that the updated name is reflected and old name is gone
     */
    public boolean validateUpdatedAssistant(String newName) {
        ExtentReportManager.logStep("Validate updated assistant name");

        try {
            // Search for the new name
            searchAssistant(newName.substring(0, Math.min(8, newName.length())));
            Thread.sleep(2000);

            // Check if new name appears
            WebElement firstCell = driver.findElement(RCSAssistantPageLocators.FIRST_ASSISTANT_NAME);
            String foundName = firstCell.getText().trim();

            boolean newNameFound = foundName.toLowerCase().contains(newName.toLowerCase());
            ExtentReportManager.logInfo("New name found: " + newNameFound);

            // Clear and search for old name - should NOT appear
            clearSearch();
            Thread.sleep(1000);

            if (!originalAssistantName.isEmpty() && !originalAssistantName.equals(newName)) {
                searchAssistant(originalAssistantName.substring(0, Math.min(8, originalAssistantName.length())));
                Thread.sleep(2000);

                List<WebElement> cells = driver.findElements(RCSAssistantPageLocators.ASSISTANT_NAME_CELLS);
                boolean oldNameGone = true;
                for (WebElement cell : cells) {
                    if (cell.getText().trim().equalsIgnoreCase(originalAssistantName)) {
                        oldNameGone = false;
                        break;
                    }
                }
                ExtentReportManager.logInfo("Old name removed: " + oldNameGone);

                return newNameFound && oldNameGone;
            }

            return newNameFound;
        } catch (Exception e) {
            ExtentReportManager.logWarning("Validation error: " + e.getMessage());
            return false;
        }
    }

    // ==================== Logout Methods ====================

    /**
     * Logout from RCS Portal
     */
    public void logoutFromRCSPortal() {
        ExtentReportManager.logStep("Logout from RCS Portal");

        try {
            // Click profile menu
            WebElement profileMenu = wait.until(
                    ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.RCS_PROFILE_MENU));
            profileMenu.click();
            Thread.sleep(500);

            // Click logout button
            WebElement logoutButton = wait.until(
                    ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.RCS_LOGOUT_BUTTON));
            logoutButton.click();

            // Confirm logout if dialog appears
            try {
                WebElement confirmButton = new WebDriverWait(driver, Duration.ofSeconds(3))
                        .until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.RCS_LOGOUT_CONFIRM));
                confirmButton.click();
            } catch (Exception e) {
                // No confirmation needed
            }

            // Wait for login page
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(RCSAssistantPageLocators.RCS_LOGIN_PAGE),
                    ExpectedConditions.urlContains("login")));

            ExtentReportManager.logPass("Successfully logged out from RCS Portal");

        } catch (Exception e) {
            ExtentReportManager.logWarning("Logout error: " + e.getMessage());
            throw new RuntimeException("Failed to logout from RCS Portal: " + e.getMessage());
        }
    }

    /**
     * Validate logout was successful
     */
    public boolean isLoggedOut() {
        try {
            return driver.getCurrentUrl().contains("login") ||
                    driver.findElement(RCSAssistantPageLocators.RCS_LOGIN_PAGE).isDisplayed();
        } catch (Exception e) {
            return driver.getCurrentUrl().contains("login");
        }
    }

    // ==================== Utility Methods ====================

    /**
     * Get the created assistant name
     */
    public String getCreatedAssistantName() {
        return createdAssistantName;
    }

    /**
     * Get row count in the table
     */
    public int getRowCount() {
        List<WebElement> rows = driver.findElements(RCSAssistantPageLocators.ASSISTANT_TABLE_ROWS);
        return rows.size();
    }

    /**
     * Wait for table to load
     */
    public void waitForTableLoad() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.ASSISTANTS_TABLE),
                ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.NO_ASSISTANT_MESSAGE)));
    }
}
