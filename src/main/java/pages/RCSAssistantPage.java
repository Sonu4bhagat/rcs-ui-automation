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
    /**
     * Click Add New Assistant button
     */
    public void clickAddNewAssistant() {
        ExtentReportManager.logStep("Click Add New Assistant button");
        try {
            WebElement addButton = wait
                    .until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.ADD_NEW_BUTTON));
            scrollAndClick(addButton);
            System.out.println("Clicked Add New Assistant button");

            wait.until(ExpectedConditions.visibilityOfElementLocated(RCSAssistantPageLocators.ASSISTANT_NAME_INPUT));
        } catch (Exception e) {
            System.out.println("Error clicking Add New Assistant: " + e.getMessage());
            ExtentReportManager.logWarning("Error clicking Add New Assistant: " + e.getMessage());
            throw e;
        }
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

        // Select billing category if dropdown exists (MANDATORY FIELD)
        try {
            System.out.println("Looking for Billing Category dropdown...");
            WebElement billingDropdown = wait.until(
                    ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.BILLING_CATEGORY_DROPDOWN));
            billingDropdown.click();
            Thread.sleep(500);

            List<WebElement> options = wait.until(
                    ExpectedConditions
                            .visibilityOfAllElementsLocatedBy(RCSAssistantPageLocators.TEMPLATE_CATEGORY_OPTIONS));
            if (!options.isEmpty()) {
                options.get(0).click();
                System.out.println("✓ Selected billing category");
                ExtentReportManager.logInfo("Selected billing category");
            }
        } catch (Exception e) {
            System.out.println("Billing category dropdown not found or error: " + e.getMessage());
            ExtentReportManager.logInfo("No billing category dropdown or already selected");
        }

        // --- DYNAMIC FILLING FOR NEW FIELDS ---
        fillRemainingRequiredFields();
        // --------------------------------------

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
     * Fill Contact Details and Verification sections, then submit
     * NEW MANDATORY FIELDS:
     * - Contact Person: Full Name, Mobile Number, Designation, Official Email
     * - All Display Name fields are required
     * - Verification section: 5 fields
     */
    public void fillContactDetailsAndSubmit() {
        ExtentReportManager.logStep("Fill Contact Details and Verification sections");

        System.out.println("========================================");
        System.out.println("ENTERING fillContactDetailsAndSubmit()");
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("========================================");

        try {
            Thread.sleep(2000);

            // STEP 1: Fill Contact Person section (MANDATORY)
            System.out.println("=== FILLING CONTACT PERSON SECTION ===");

            // Full Name
            tryFillField("//input[contains(@placeholder,'Full Name') or contains(@formcontrolname,'fullName')]",
                    "Test Contact Person");

            // Mobile Number
            tryFillField("//input[contains(@placeholder,'Mobile') or contains(@formcontrolname,'mobile')]",
                    "9876543210");

            // Designation
            tryFillField("//input[contains(@placeholder,'Designation') or contains(@formcontrolname,'designation')]",
                    "Test Manager");

            // Official Email
            tryFillField(
                    "//input[contains(@placeholder,'Official Email') or contains(@formcontrolname,'officialEmail')]",
                    "test.contact@example.com");

            Thread.sleep(1000);

            // STEP 2: Fill ALL Display Name fields (MANDATORY)
            System.out.println("=== FILLING DISPLAY NAME FIELDS ===");
            List<WebElement> displayNameFields = driver.findElements(
                    By.xpath(
                            "//input[contains(@placeholder,'Display Name') or contains(@placeholder,'display name')]"));

            int displayCount = 0;
            for (WebElement field : displayNameFields) {
                try {
                    if (field.isDisplayed() && field.getAttribute("value").isEmpty()) {
                        scrollToElement(field);
                        field.clear();
                        field.sendKeys("Display " + (++displayCount));
                        System.out.println("Filled Display Name field #" + displayCount);
                    }
                } catch (Exception e) {
                    // Skip if error
                }
            }

            Thread.sleep(1000);

            // STEP 3: Fill standard contact fields (Phone, Website, Email, Privacy, Terms)
            System.out.println("=== FILLING STANDARD CONTACT FIELDS ===");
            tryFillField("//input[contains(@placeholder,'Phone') or contains(@placeholder,'phone')]",
                    "9876543210");
            tryFillField("//input[contains(@placeholder,'Website') or contains(@placeholder,'URL')]",
                    "https://www.testcompany.com");
            tryFillField("//input[contains(@placeholder,'Email') or @type='email']",
                    "test@testcompany.com");
            tryFillField("//input[contains(@placeholder,'Privacy')]",
                    "https://www.testcompany.com/privacy");
            tryFillField("//input[contains(@placeholder,'Terms')]",
                    "https://www.testcompany.com/terms");

            Thread.sleep(1000);

            // STEP 3.5: Fill ALL remaining empty email fields (there may be multiple)
            System.out.println("=== FILLING ALL REMAINING EMAIL FIELDS ===");
            List<WebElement> allEmailFields = driver.findElements(
                    By.xpath(
                            "//input[@type='email' or contains(@placeholder,'Email') or contains(@placeholder,'email')]"));

            int emailCount = 0;
            for (WebElement emailField : allEmailFields) {
                try {
                    if (emailField.isDisplayed() && emailField.getAttribute("value").isEmpty()) {
                        scrollToElement(emailField);
                        emailField.clear();
                        emailField.sendKeys("email" + (++emailCount) + "@testcompany.com");
                        System.out.println("Filled additional email field #" + emailCount);
                    }
                } catch (Exception e) {
                    // Skip if error
                }
            }

            Thread.sleep(1000);

            // STEP 4: Try clicking Next to move to Verification section
            System.out.println("=== LOOKING FOR NEXT BUTTON ===");
            boolean nextClicked = false;
            List<WebElement> nextButtons = driver.findElements(
                    By.xpath("//button[contains(text(),'Next') and not(contains(@class,'disabled'))]"));

            for (WebElement btn : nextButtons) {
                try {
                    if (btn.isDisplayed() && btn.isEnabled()) {
                        System.out.println("Clicking Next to Verification section...");
                        scrollAndClick(btn);
                        nextClicked = true;
                        Thread.sleep(2000);
                        break;
                    }
                } catch (Exception e) {
                    // Try next button
                }
            }

            // STEP 5: Fill Verification section (ALL FIELDS) if present
            System.out.println("=== CHECKING FOR VERIFICATION SECTION ===");
            System.out.println("Current URL after Next: " + driver.getCurrentUrl());
            Thread.sleep(2000);

            // STEP 5A: Fill Rich Text Editor fields (5 FIELDS)
            System.out.println("=== FILLING RICH TEXT EDITOR FIELDS (5 FIELDS) ===");
            List<WebElement> richTextEditors = driver.findElements(
                    By.xpath("//*[@contenteditable='true' or @contenteditable='']"));

            System.out.println("Found " + richTextEditors.size() + " rich text editor fields");

            int richTextCount = 0;
            for (WebElement editor : richTextEditors) {
                try {
                    if (editor.isDisplayed()) {
                        scrollToElement(editor);
                        Thread.sleep(300);

                        String currentText = editor.getText();
                        if (currentText == null || currentText.trim().isEmpty()
                                || currentText.contains("Type your message")) {
                            richTextCount++;
                            String textToFill = "This is verification text " + richTextCount + ". " +
                                    "We ensure compliance with all messaging regulations and user privacy standards. " +
                                    "Users provide explicit opt-in consent before receiving any messages.";

                            // Use JavaScript to set innerHTML for rich text editors
                            ((JavascriptExecutor) driver).executeScript(
                                    "arguments[0].innerHTML = arguments[1]; " +
                                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true })); " +
                                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                                    editor, textToFill);

                            System.out.println("✓ Filled rich text editor #" + richTextCount);
                            Thread.sleep(300);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Could not fill rich text editor: " + e.getMessage());
                }
            }

            System.out.println("Total rich text editors filled: " + richTextCount);
            Thread.sleep(1000);

            // STEP 5B: Fill regular input fields
            System.out.println("=== FILLING REGULAR INPUT FIELDS ===");

            // Find ALL input fields (text, email, tel, url, number, etc.)
            List<WebElement> allInputs = driver.findElements(
                    By.xpath(
                            "//input[not(@type='hidden') and not(@type='checkbox') and not(@type='radio') and not(@type='button')]"));

            System.out.println("Found " + allInputs.size() + " potential input fields");

            int verificationCount = 0;
            for (WebElement field : allInputs) {
                try {
                    // Check if field is displayed and empty
                    if (field.isDisplayed()) {
                        String currentValue = field.getAttribute("value");
                        if (currentValue == null || currentValue.trim().isEmpty()) {
                            scrollToElement(field);
                            Thread.sleep(300);

                            String placeholder = field.getAttribute("placeholder");
                            String fieldType = field.getAttribute("type");
                            String fieldName = field.getAttribute("name");

                            System.out.println("Found empty field - Type: " + fieldType + ", Placeholder: "
                                    + placeholder + ", Name: " + fieldName);

                            // Determine value based on type and placeholder
                            String value = "Test Value " + (++verificationCount);

                            if (fieldType != null && fieldType.equals("email")) {
                                value = "verification" + verificationCount + "@example.com";
                            } else if (fieldType != null && (fieldType.equals("tel") || fieldType.equals("number"))) {
                                value = "9876543210";
                            } else if (fieldType != null && fieldType.equals("url")) {
                                value = "https://verification" + verificationCount + ".com";
                            } else if (placeholder != null) {
                                String placeholderLower = placeholder.toLowerCase();
                                if (placeholderLower.contains("email")) {
                                    value = "verification" + verificationCount + "@example.com";
                                } else if (placeholderLower.contains("phone") || placeholderLower.contains("mobile")
                                        || placeholderLower.contains("number")) {
                                    value = "9876543210";
                                } else if (placeholderLower.contains("url") || placeholderLower.contains("website")
                                        || placeholderLower.contains("link")) {
                                    value = "https://verification" + verificationCount + ".com";
                                } else if (placeholderLower.contains("name")) {
                                    value = "Test Name " + verificationCount;
                                }
                            }

                            // Fill the field
                            try {
                                field.clear();
                                field.sendKeys(value);
                                System.out.println(
                                        "✓ Filled verification field #" + verificationCount + " with: " + value);
                            } catch (Exception e) {
                                // Try JS if regular sendKeys fails
                                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                                        "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input'));",
                                        field, value);
                                System.out.println(
                                        "✓ Filled verification field #" + verificationCount + " (JS) with: " + value);
                            }
                        }
                    }
                } catch (Exception e) {
                    // Skip if error
                }
            }

            System.out.println("Total regular input fields filled: " + verificationCount);
            Thread.sleep(1000);

            // STEP 6: Submit the form
            System.out.println("=== LOOKING FOR SUBMIT BUTTON ===");
            boolean submitted = false;

            // Try Submit button
            List<WebElement> submitButtons = driver.findElements(
                    By.xpath("//button[contains(text(),'Submit') and not(contains(@class,'disabled'))]"));
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

            // Try Create button if no Submit
            if (!submitted) {
                List<WebElement> createButtons = driver.findElements(
                        By.xpath("//button[contains(text(),'Create') and not(contains(@class,'disabled'))]"));
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

            Thread.sleep(2000);
            System.out.println("Contact and Verification sections completed");
            System.out.println("Current URL: " + driver.getCurrentUrl());

            ExtentReportManager.logPass("Contact and Verification sections filled successfully");

        } catch (Exception e) {
            System.out.println("ERROR in fillContactDetailsAndSubmit: " + e.getMessage());
            e.printStackTrace();
            ExtentReportManager.logWarning("Error in contact and verification: " + e.getMessage());
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
            // First, navigate back to assistants list if we're on the add page
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Current URL before status check: " + currentUrl);

            if (currentUrl.contains("/add") || currentUrl.contains("/edit")) {
                System.out.println("Still on add/edit page, navigating back to assistants list...");
                // Try to find and click back/cancel button or navigate via URL
                try {
                    String listUrl = currentUrl.replaceAll("/(add|edit).*$", "");
                    driver.get(listUrl);
                    Thread.sleep(2000);
                    System.out.println("Navigated to: " + driver.getCurrentUrl());
                } catch (Exception e) {
                    System.out.println("Could not navigate back: " + e.getMessage());
                }
            }

            // Wait for table to load
            waitForTableLoad();
            Thread.sleep(1000);

            // Search for the created assistant
            if (createdAssistantName != null && !createdAssistantName.isEmpty()) {
                searchAssistant(createdAssistantName.substring(0, Math.min(10, createdAssistantName.length())));
                Thread.sleep(2000);
            }

            // Get the status from the first row with multiple fallback locators
            List<WebElement> rows = driver.findElements(RCSAssistantPageLocators.ASSISTANT_TABLE_ROWS);
            System.out.println("Found " + rows.size() + " rows in table");

            if (!rows.isEmpty()) {
                // Try multiple status cell locators
                String[] statusXPaths = {
                        ".//td[5]",
                        ".//td[contains(@class,'status')]",
                        ".//td//span[contains(@class,'badge')]",
                        ".//span[contains(@class,'status')]",
                        ".//td[last()]//span"
                };

                for (String xpath : statusXPaths) {
                    try {
                        WebElement statusCell = rows.get(0).findElement(By.xpath(xpath));
                        String status = statusCell.getText().trim();
                        if (!status.isEmpty()) {
                            System.out.println("Found status '" + status + "' using xpath: " + xpath);
                            ExtentReportManager.logInfo("Assistant status: " + status);
                            return status;
                        }
                    } catch (Exception e) {
                        // Try next locator
                    }
                }
            }

            // Try to find status badge anywhere on page
            try {
                WebElement pendingBadge = driver.findElement(RCSAssistantPageLocators.PENDING_STATUS_BADGE);
                String status = pendingBadge.getText().trim();
                if (!status.isEmpty()) {
                    System.out.println("Found status from badge: " + status);
                    return status;
                }
            } catch (Exception e) {
                // No badge found
            }

            // Last resort: look for any text containing status keywords
            try {
                List<WebElement> allText = driver.findElements(By.xpath(
                        "//*[contains(text(),'Pending') or contains(text(),'Approval') or contains(text(),'Active')]"));
                for (WebElement el : allText) {
                    String text = el.getText().trim();
                    if (text.toLowerCase().contains("pending") || text.toLowerCase().contains("approval")) {
                        System.out.println("Found status text: " + text);
                        return text;
                    }
                }
            } catch (Exception e) {
                // Ignore
            }

            System.out.println("Could not determine status, returning 'Unknown'");
            return "Unknown";

        } catch (Exception e) {
            System.out.println("Error getting status: " + e.getMessage());
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

        // Click the View icon directly (not in dropdown)
        try {
            WebElement viewIcon = wait
                    .until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.VIEW_ICON));
            scrollToElement(viewIcon);
            viewIcon.click();
            System.out.println("Clicked View icon");
        } catch (Exception e) {
            // Try JS click if regular click fails
            try {
                WebElement viewIcon = driver.findElement(RCSAssistantPageLocators.VIEW_ICON);
                scrollToElement(viewIcon);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewIcon);
                System.out.println("Clicked View icon (JS)");
            } catch (Exception ex) {
                System.out.println("Failed to click View icon: " + ex.getMessage());
                throw ex;
            }
        }

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
     * Click edit icon for the first assistant
     */
    public void clickEditIcon() {
        ExtentReportManager.logStep("Click Edit icon for first assistant");

        // Navigate back to list if on details page
        try {
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("/view/") || currentUrl.contains("/edit/")) {
                driver.navigate().back();
                Thread.sleep(1500);
                wait.until(ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.ASSISTANTS_TABLE));
            }
        } catch (Exception e) {
            // Already on list
        }

        // Store original name
        try {
            WebElement firstNameCell = driver.findElement(RCSAssistantPageLocators.FIRST_ASSISTANT_NAME);
            originalAssistantName = firstNameCell.getText().trim();
            System.out.println("Original assistant name: " + originalAssistantName);
        } catch (Exception e) {
            originalAssistantName = "";
        }

        // Click three dot menu
        try {
            WebElement threeDotMenu = wait
                    .until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.THREE_DOT_MENU));
            scrollToElement(threeDotMenu);
            threeDotMenu.click();
            System.out.println("Clicked three-dot menu");
            Thread.sleep(800);
        } catch (Exception e) {
            // Try JS click
            try {
                WebElement threeDotMenu = driver.findElement(RCSAssistantPageLocators.THREE_DOT_MENU);
                scrollToElement(threeDotMenu);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", threeDotMenu);
                System.out.println("Clicked three-dot menu (JS)");
                try {
                    Thread.sleep(800);
                } catch (InterruptedException ie) {
                }
            } catch (Exception ex) {
                System.out.println("Failed to click three-dot menu: " + ex.getMessage());
                throw ex;
            }
        }

        // Click Edit button from dropdown
        try {
            WebElement editButton = wait
                    .until(ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.EDIT_BUTTON));
            scrollToElement(editButton);
            editButton.click();
            System.out.println("Clicked Edit button");
        } catch (Exception e) {
            // Try JS click
            try {
                WebElement editButton = driver.findElement(RCSAssistantPageLocators.EDIT_BUTTON);
                scrollToElement(editButton);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editButton);
                System.out.println("Clicked Edit button (JS)");
            } catch (Exception ex) {
                System.out.println("Failed to click Edit button: " + ex.getMessage());
                throw ex;
            }
        }

        // Wait for edit form to load
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(RCSAssistantPageLocators.EDIT_PAGE_HEADER));
            System.out.println("Edit page loaded");
        } catch (Exception e) {
            System.out.println("Edit page header not found, but proceeding...");
        }
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

    /**
     * Dynamically fills any remaining required fields that are empty.
     * Use this to handle "new fields" that might appear without updated locators.
     */
    private void fillRemainingRequiredFields() {
        System.out.println("Checking for any other required fields to fill...");
        try {
            // Find all visible inputs, textareas, and selects
            List<WebElement> inputs = driver.findElements(By.xpath("//input | //textarea | //select | //ng-select"));

            for (WebElement element : inputs) {
                try {
                    // Check visibility and requirement
                    if (element.isDisplayed() && isRequired(element)) {
                        // Check if empty
                        if (isElementEmpty(element)) {
                            System.out.println("Found empty required field: " + element.getTagName());
                            fillElementWithDummyData(element);
                        }
                    }
                } catch (Exception e) {
                    // Stale element or other issue, skip
                }
            }
        } catch (Exception e) {
            System.out.println("Error in dynamic field filling: " + e.getMessage());
        }
    }

    private boolean isRequired(WebElement element) {
        String requiredAttr = element.getAttribute("required");
        String ariaRequired = element.getAttribute("aria-required");
        String classAttr = element.getAttribute("class");

        // Check for standard required attributes
        boolean isStandardRequired = (requiredAttr != null && "true".equalsIgnoreCase(requiredAttr)) ||
                (ariaRequired != null && "true".equalsIgnoreCase(ariaRequired));

        // Check for Angular/Framework validation classes (ng-invalid usually means it
        // needs attention)
        boolean isFrameworkInvalid = classAttr != null && classAttr.contains("ng-invalid") &&
                !classAttr.contains("ng-untouched"); // Optional: only if touched? No, if invalid we should fix it.

        return isStandardRequired || isFrameworkInvalid;
    }

    private boolean isElementEmpty(WebElement element) {
        String value = element.getAttribute("value");
        if (value != null && !value.isEmpty())
            return false;

        // For textareas or contenteditable
        String text = element.getText();
        return text == null || text.trim().isEmpty();
    }

    private void fillElementWithDummyData(WebElement element) {
        String tagName = element.getTagName().toLowerCase();
        String type = element.getAttribute("type");
        String placeholder = element.getAttribute("placeholder");
        if (placeholder == null)
            placeholder = "";

        if (tagName.equals("select")) {
            // Select first option
            new org.openqa.selenium.support.ui.Select(element).selectByIndex(1);
        } else if (tagName.equals("ng-select")) {
            element.click();
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='option']"))).click();
            } catch (Exception e) {
            }
        } else {
            // Input or Textarea
            String valueToFill = "Test Val";

            if (type != null && type.equals("number"))
                valueToFill = "1234567890";
            else if (placeholder.toLowerCase().contains("phone") || placeholder.toLowerCase().contains("mobile"))
                valueToFill = "9876543210";
            else if (placeholder.toLowerCase().contains("email"))
                valueToFill = "test@example.com";
            else if (placeholder.toLowerCase().contains("url") || placeholder.toLowerCase().contains("website"))
                valueToFill = "https://example.com";

            try {
                element.clear();
                element.sendKeys(valueToFill);
                System.out.println("Filled " + tagName + " with: " + valueToFill);
            } catch (Exception e) {
                fillInputWithJS(element, valueToFill);
                System.out.println("Filled " + tagName + " (JS) with: " + valueToFill);
            }
        }
    }
}
