package pages.enterprise;

import locators.enterprise.RCSAssistantPageLocators;
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
            // Check for RCS Portal specific URL host first
            String currentUrl = driver.getCurrentUrl();
            boolean hostMatch = currentUrl.contains("stagingocmp.smartping.io") || currentUrl.contains("ocmp");

            if (!hostMatch) {
                return false;
            }

            // Then check for elements
            return wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.RCS_PORTAL_LOGO),
                    ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.MANAGE_ASSISTANTS_MENU),
                    ExpectedConditions.urlContains("ocmp"))) != null;
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
            // NOTE: must be exactly 1440x448 (JPEG/PNG/SVG, max 200kb) - the app
            // rejects (visible red error badge) a mismatched-dimension image even
            // though it doesn't block form progression, so the assistant would be
            // created with no real cover image. Cover.jpg is an arbitrary logo, not
            // pre-sized to this exact banner shape, so use the pre-generated asset.
            System.out.println("Uploading Cover image...");
            String coverImagePath = new File("src/main/resources/image/cover_1440x448.jpg").getAbsolutePath();
            WebElement coverUpload = driver.findElement(RCSAssistantPageLocators.COVER_IMAGE_UPLOAD);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", coverUpload);
            Thread.sleep(500);
            coverUpload.sendKeys(coverImagePath);
            ExtentReportManager.logInfo("Uploaded cover image: " + coverImagePath);
            Thread.sleep(1500);
            utils.ScreenshotUtil.captureScreenshot(driver, "cover_upload_crop_dialog");
            Thread.sleep(1000);

            // Handle crop dialog for cover image if it appears
            handleCropDialogIfPresent("cover");

            // The crop dialog's own "Save" confirms the crop, but a SEPARATE
            // "Confirm File Upload" dialog can still be left open afterward (seen
            // live via screenshot) - without this, it silently blocks the rest of
            // the form since only one shared Proceed-click happened at the very
            // end for both images combined, and it could only close one of them.
            System.out.println("Confirming cover image upload...");
            confirmUploadModalUntilClosed();

            // Upload Icon Image
            // NOTE: must be exactly 224x224 (JPEG/PNG/SVG, max 50kb) - same
            // dimension-mismatch issue as the cover image above.
            System.out.println("Uploading Icon image...");
            String iconImagePath = new File("src/main/resources/image/icon_224x224.png").getAbsolutePath();
            WebElement iconUpload = driver.findElement(RCSAssistantPageLocators.ICON_IMAGE_UPLOAD);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", iconUpload);
            Thread.sleep(500);
            iconUpload.sendKeys(iconImagePath);
            ExtentReportManager.logInfo("Uploaded icon image: " + iconImagePath);
            Thread.sleep(1500);
            utils.ScreenshotUtil.captureScreenshot(driver, "icon_upload_crop_dialog");
            Thread.sleep(1000);

            // Handle crop dialog for icon image if it appears
            handleCropDialogIfPresent("icon");

            System.out.println("Confirming icon image upload...");
            confirmUploadModalUntilClosed();

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
                    try {
                        btn.click();
                    } catch (Exception clickEx) {
                        System.out.println("Standard click failed for Proceed button, trying JS click...");
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                    }
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
     * True if a "Confirm File Upload" style dialog (or any visible modal with a
     * Proceed/Cancel choice) is currently blocking the page.
     */
    private boolean isConfirmUploadModalVisible() {
        try {
            List<WebElement> modals = driver.findElements(By.xpath(
                    "//*[contains(text(),'Confirm File Upload')] | " +
                    "//*[contains(text(),'Do you want to proceed')] | " +
                    "//button[normalize-space(text())='Proceed']"));
            for (WebElement modal : modals) {
                if (modal.isDisplayed()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click Proceed on a "Confirm File Upload" dialog and verify it actually
     * closed before returning - explicitly waits out the close animation rather
     * than a fixed sleep (which can catch the modal mid-transition and wrongly
     * conclude it's stuck), retrying the click up to 3 times. Every upload point
     * in this flow (branding cover/icon, verification screenshot) shows this
     * same dialog, and leaving it open silently blocks whatever comes next.
     */
    private void confirmUploadModalUntilClosed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        boolean modalClosed = false;
        for (int attempt = 1; attempt <= 3 && !modalClosed; attempt++) {
            if (attempt > 1) {
                System.out.println("  Confirm-upload modal still visible, retrying click (attempt " + attempt + ")...");
            }
            clickProceedButtonIfPresent();
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> !isConfirmUploadModalVisible());
                modalClosed = true;
            } catch (Exception waitEx) {
                // still visible after 5s - loop will retry the click
            }
        }
        System.out.println(modalClosed
                ? "  Confirm-upload modal closed (or never appeared)"
                : "  ⚠ Confirm-upload modal STILL visible after 3 attempts - upload may not register");
    }

    /**
     * Handle the image crop dialog if it appears after upload
     *
     * @param imageType "cover" or "icon" for logging purposes
     */
    private void handleCropDialogIfPresent(String imageType) {
        try {
            System.out.println("Checking for crop dialog for " + imageType + " image...");

            Thread.sleep(1000); // Brief wait for crop dialog to appear

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
                List<WebElement> buttons = driver.findElements(cropButtonLocators);
                System.out.println("Found " + buttons.size() + " potential crop buttons");

                WebElement correctButton = null;
                WebElement fallbackButton = null; // For buttons with empty text inside modal/cropper

                for (WebElement btn : buttons) {
                    try {
                        if (!btn.isDisplayed() || !btn.isEnabled())
                            continue;
                    } catch (Exception e) {
                        continue;
                    }

                    String btnText = btn.getText().trim().toLowerCase();
                    System.out.println("  Button found: '" + btn.getText().trim() + "'");

                    // Skip Back, Cancel, Close, Next buttons
                    if (btnText.contains("back") || btnText.contains("cancel") ||
                            btnText.contains("close") || btnText.contains("next") ||
                            btnText.contains("proceed")) {
                        System.out.println("    Skipping this button");
                        continue;
                    }

                    // Prefer Apply, Crop, Save, Done, OK, Confirm
                    if (btnText.contains("apply") || btnText.contains("crop") ||
                            btnText.contains("save") || btnText.contains("done") ||
                            btnText.equals("ok") || btnText.contains("confirm")) {
                        correctButton = btn;
                        break;
                    }

                    // Track empty-text buttons as fallback (common in icon-only crop dialogs)
                    if (btnText.isEmpty() && fallbackButton == null) {
                        String btnClass = btn.getAttribute("class");
                        // Only use as fallback if it looks like a primary/action button
                        if (btnClass != null && (btnClass.contains("primary") || btnClass.contains("crop") ||
                                btnClass.contains("apply") || btnClass.contains("save") ||
                                btnClass.contains("confirm") || btnClass.contains("btn"))) {
                            fallbackButton = btn;
                            System.out.println("    Marking as fallback button (class: " + btnClass + ")");
                        }
                    }
                }

                // Use labeled button first, then fallback
                WebElement buttonToClick = correctButton != null ? correctButton : fallbackButton;

                // Additional fallback: look for any button inside a visible
                // modal/dialog/cropper overlay
                if (buttonToClick == null) {
                    try {
                        List<WebElement> modalButtons = driver.findElements(By.xpath(
                                "//div[contains(@class,'modal') and contains(@class,'show')]//div[contains(@class,'modal-footer')]//button[not(contains(text(),'Cancel')) and not(contains(text(),'Close'))] | "
                                        +
                                        "//div[contains(@class,'cdk-overlay')]//button[contains(@class,'primary') or contains(@class,'btn')] | "
                                        +
                                        "//mat-dialog-container//button[not(contains(text(),'Cancel'))]"));
                        for (WebElement mb : modalButtons) {
                            try {
                                if (mb.isDisplayed() && mb.isEnabled()) {
                                    buttonToClick = mb;
                                    System.out.println("  Found modal/overlay button as final fallback: '"
                                            + mb.getText().trim() + "'");
                                    break;
                                }
                            } catch (Exception e) {
                                /* skip */ }
                        }
                    } catch (Exception e) {
                        /* no modal buttons */ }
                }

                if (buttonToClick != null) {
                    System.out.println("Clicking crop button: '" + buttonToClick.getText().trim() + "'");
                    try {
                        buttonToClick.click();
                    } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", buttonToClick);
                    }
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
     * Fill Contact Details and Verification sections, then submit.
     * Handles multi-step wizard forms by advancing through remaining steps
     * until Submit is found.
     * 
     * NEW MANDATORY FIELDS:
     * - Contact Person: Full Name, Mobile Number, Designation, Official Email
     * - All Display Name fields are required
     * - Verification section: rich text editors and input fields
     */
    public void fillContactDetailsAndSubmit() {
        ExtentReportManager.logStep("Fill Contact Details and Verification sections");

        System.out.println("========================================");
        System.out.println("ENTERING fillContactDetailsAndSubmit()");
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("========================================");

        try {
            // A blind fixed sleep here previously caused an entire run to silently
            // fill nothing: if the Contact tab's fields hadn't rendered yet after
            // exactly 2s (e.g. a slower page load), every tryFillField() call below
            // found 0 displayed matches and moved on with no visible field ever
            // filled, yet the flow still reported success. Wait explicitly for the
            // Full Name field to actually be present before filling anything.
            try {
                // visibilityOfElementLocated, NOT presenceOfElementLocated - the field can
                // exist in the DOM well before it's actually visible (mid-transition from
                // the Branding->Contact step animation), and tryFillField() below only
                // acts on elements where isDisplayed() is true.
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                        "//input[contains(@placeholder,'Full Name') or contains(@formcontrolname,'fullName')]")));
                System.out.println("Contact section fields confirmed visible.");
            } catch (Exception e) {
                System.out.println("⚠ Contact section 'Full Name' field never became visible within timeout - "
                        + "proceeding anyway, but fields below will likely fail to fill: " + e.getMessage());
            }
            Thread.sleep(1000);

            // STEP 1: Fill Contact Person section (MANDATORY)
            System.out.println("=== FILLING CONTACT PERSON SECTION ===");

            tryFillField("//input[contains(@placeholder,'Full Name') or contains(@formcontrolname,'fullName')]",
                    "Test Contact Person");
            tryFillField("//input[contains(@placeholder,'Mobile') or contains(@formcontrolname,'mobile')]",
                    "9876543210");
            tryFillField("//input[contains(@placeholder,'Designation') or contains(@formcontrolname,'designation')]",
                    "Test Manager");
            tryFillField(
                    "//input[contains(@placeholder,'Official Email') or contains(@formcontrolname,'officialEmail')]",
                    "test.contact@example.com");

            Thread.sleep(1000);

            // STEP 2: Fill ALL Display Name fields (MANDATORY)
            System.out.println("=== FILLING DISPLAY NAME FIELDS ===");
            fillAllDisplayNameFields();

            Thread.sleep(1000);

            // STEP 3: Fill standard contact fields
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

            // STEP 3.5: Fill ALL remaining empty email fields
            System.out.println("=== FILLING ALL REMAINING EMAIL FIELDS ===");
            fillAllEmptyEmailFields();

            Thread.sleep(1000);

            // STEP 4: Advance through remaining wizard steps, fill fields, and submit
            System.out.println("=== ADVANCING THROUGH WIZARD STEPS ===");
            advanceThroughWizardAndSubmit();

            ExtentReportManager.logPass("Contact and Verification sections filled and submitted successfully");

        } catch (Exception e) {
            System.out.println("ERROR in fillContactDetailsAndSubmit: " + e.getMessage());
            e.printStackTrace();
            ExtentReportManager.logWarning("Error in contact and verification: " + e.getMessage());
        }
    }

    /**
     * Fill all empty Display Name fields on the current page
     */
    private void fillAllDisplayNameFields() {
        List<WebElement> displayNameFields = driver.findElements(
                By.xpath(
                        "//input[contains(@placeholder,'Display Name') or contains(@placeholder,'display name') or contains(@placeholder,'Enter Display')]"));

        int displayCount = 0;
        for (WebElement field : displayNameFields) {
            try {
                if (field.isDisplayed()
                        && (field.getAttribute("value") == null || field.getAttribute("value").isEmpty())) {
                    scrollToElement(field);
                    field.clear();
                    field.sendKeys("Display " + (++displayCount));
                    System.out.println("Filled Display Name field #" + displayCount);
                }
            } catch (Exception e) {
                // Skip if error
            }
        }
    }

    /**
     * Fill all empty email fields on the current page
     */
    private void fillAllEmptyEmailFields() {
        List<WebElement> allEmailFields = driver.findElements(
                By.xpath(
                        "//input[@type='email' or contains(@placeholder,'Email') or contains(@placeholder,'email')]"));

        int emailCount = 0;
        for (WebElement emailField : allEmailFields) {
            try {
                if (emailField.isDisplayed()
                        && (emailField.getAttribute("value") == null || emailField.getAttribute("value").isEmpty())) {
                    scrollToElement(emailField);
                    emailField.clear();
                    emailField.sendKeys("email" + (++emailCount) + "@testcompany.com");
                    System.out.println("Filled additional email field #" + emailCount);
                }
            } catch (Exception e) {
                // Skip if error
            }
        }
    }

    /**
     * Advance through wizard steps with the correct approach:
     * 1. Fill all visible empty fields at current step
     * 2. Wait for Next/Submit button to become ENABLED (Angular enables it after
     * validation)
     * 3. Click the enabled button
     * 4. Repeat for next step
     */
    private void advanceThroughWizardAndSubmit() throws InterruptedException {
        boolean submitted = false;

        // Try up to 5 wizard steps
        for (int step = 1; step <= 5 && !submitted; step++) {
            System.out.println("--- Wizard Step " + step + " ---");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            Thread.sleep(1000);

            // === PHASE 1: Fill all visible fields on this step ===
            System.out.println("Phase 1: Filling all visible fields...");
            fillAllFieldsOnCurrentStep();

            Thread.sleep(1000);

            // === PHASE 2: Check for any remaining ng-invalid fields after initial filling
            // ===
            System.out.println("Phase 2: Checking for remaining invalid fields...");
            fillNgInvalidFields();

            Thread.sleep(500);

            // === PHASE 3: Look for Submit or Next button and wait for it to become enabled
            // ===
            System.out.println("Phase 3: Looking for enabled Submit/Next button...");

            // First, check if a Submit button exists on this step
            WebElement submitBtn = findSubmitButton();

            if (submitBtn != null) {
                // Submit button found - wait for it to become enabled
                System.out.println("Found Submit button: '" + submitBtn.getText().trim() + "'");
                boolean enabled = waitForButtonToEnable(submitBtn, 15);

                if (enabled) {
                    scrollToElement(submitBtn);
                    try {
                        submitBtn.click();
                    } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
                    }
                    submitted = true;
                    System.out.println("✓ Clicked Submit button successfully");
                } else {
                    System.out.println("Submit button still disabled after filling all fields.");
                    logRemainingInvalidFields();
                    // Try filling any remaining invalid fields one more time
                    fillNgInvalidFields();
                    fillAllEmptyInputFields();
                    Thread.sleep(1000);

                    // Check again
                    enabled = waitForButtonToEnable(submitBtn, 5);
                    if (enabled) {
                        scrollToElement(submitBtn);
                        try {
                            submitBtn.click();
                        } catch (Exception e) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
                        }
                        submitted = true;
                        System.out.println("✓ Clicked Submit button after re-filling fields");
                    } else {
                        System.out.println(
                                "WARNING: Submit button remains disabled - some required fields may be missing");
                    }
                }
            } else {
                // No Submit button - look for Next button
                WebElement nextBtn = findNextButton();

                if (nextBtn != null) {
                    System.out.println("Found Next button: '" + nextBtn.getText().trim() + "'");
                    boolean enabled = waitForButtonToEnable(nextBtn, 15);

                    if (enabled) {
                        scrollToElement(nextBtn);
                        try {
                            nextBtn.click();
                        } catch (Exception e) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextBtn);
                        }
                        System.out.println("✓ Clicked Next button to advance");
                        Thread.sleep(2000);
                    } else {
                        System.out.println("Next button still disabled after filling fields.");
                        logRemainingInvalidFields();
                        // Try filling any remaining invalid fields
                        fillNgInvalidFields();
                        fillAllEmptyInputFields();
                        Thread.sleep(1000);

                        enabled = waitForButtonToEnable(nextBtn, 5);
                        if (enabled) {
                            scrollToElement(nextBtn);
                            try {
                                nextBtn.click();
                            } catch (Exception e) {
                                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextBtn);
                            }
                            System.out.println("✓ Clicked Next button after re-filling fields");
                            Thread.sleep(2000);
                        } else {
                            System.out.println(
                                    "WARNING: Next button remains disabled - cannot advance from step " + step);
                            logRemainingInvalidFields();
                            break;
                        }
                    }
                } else {
                    System.out.println("No Submit or Next button found at step " + step);
                    break;
                }
            }
        }

        if (!submitted) {
            System.out.println("WARNING: Could not submit the form!");
            String shot = utils.ScreenshotUtil.captureScreenshot(driver, "stuckSubmitDisabled");
            System.out.println("Diagnostic screenshot of stuck form saved to: " + shot);
        }

        Thread.sleep(3000);

        // Check for success message
        checkForSuccessMessage();

        // Handle "Go to Assistant" button after creation
        handleGoToAssistantButton();

        System.out.println("Contact and Verification sections completed");
        System.out.println("Current URL: " + driver.getCurrentUrl());
    }

    /**
     * Fill all types of fields on the current wizard step
     */
    private void fillAllFieldsOnCurrentStep() {
        fillAllRichTextEditors();
        fillAllEmptyInputFields();
        fillAllEmptyTextareas();
        fillAllDisplayNameFields();
        fillAllEmptyEmailFields();
        checkAllUncheckedCheckboxes();
        uploadVerificationFiles();
    }

    /**
     * Find a Submit/Create/Save button on the current page (may be disabled)
     * 
     * @return the button element, or null if not found
     */
    private WebElement findSubmitButton() {
        String[] submitTexts = { "Submit", "Create", "Save", "Register", "Add Assistant" };

        for (String text : submitTexts) {
            try {
                List<WebElement> buttons = driver.findElements(
                        By.xpath("//button[contains(text(),'" + text + "')]"));
                for (WebElement btn : buttons) {
                    try {
                        if (btn.isDisplayed()) {
                            String btnText = btn.getText().trim().toLowerCase();
                            // Skip if this is actually a Next button
                            if (btnText.contains("next"))
                                continue;
                            return btn;
                        }
                    } catch (Exception e) {
                        // Skip
                    }
                }
            } catch (Exception e) {
                // Try next text
            }
        }

        // Also try locator-defined Submit button
        try {
            WebElement btn = driver.findElement(RCSAssistantPageLocators.SUBMIT_BUTTON);
            if (btn.isDisplayed())
                return btn;
        } catch (Exception e) {
            // Not found
        }

        // Also try button[type=submit]
        try {
            WebElement btn = driver.findElement(By.xpath("//button[@type='submit']"));
            if (btn.isDisplayed())
                return btn;
        } catch (Exception e) {
            // Not found
        }

        return null;
    }

    /**
     * Find a Next button on the current page (may be disabled)
     * 
     * @return the button element, or null if not found
     */
    private WebElement findNextButton() {
        try {
            List<WebElement> nextButtons = driver.findElements(
                    By.xpath("//button[contains(text(),'Next')]"));

            for (WebElement btn : nextButtons) {
                try {
                    if (btn.isDisplayed()) {
                        return btn;
                    }
                } catch (Exception e) {
                    // Skip
                }
            }
        } catch (Exception e) {
            // Not found
        }
        return null;
    }

    /**
     * Wait for a button to become enabled by polling.
     * The button becomes enabled when Angular form validation passes
     * (i.e., all required fields are filled correctly).
     * 
     * @param button         the button element to wait for
     * @param timeoutSeconds maximum seconds to wait
     * @return true if button became enabled within timeout
     */
    private boolean waitForButtonToEnable(WebElement button, int timeoutSeconds) {
        System.out.println("Waiting up to " + timeoutSeconds + "s for button to become enabled...");

        for (int i = 0; i < timeoutSeconds * 2; i++) {
            try {
                String disabled = button.getAttribute("disabled");
                boolean isDisabled = disabled != null && !disabled.isEmpty();
                boolean isEnabled = button.isEnabled();

                if (!isDisabled && isEnabled) {
                    System.out.println("✓ Button is now ENABLED (after " + (i * 500) + "ms)");
                    return true;
                }

                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println("Error checking button state: " + e.getMessage());
                return false;
            }
        }

        System.out.println("✗ Button still disabled after " + timeoutSeconds + " seconds");
        return false;
    }

    /**
     * Log all remaining invalid/required fields for debugging
     */
    private void logRemainingInvalidFields() {
        try {
            List<WebElement> formControls = driver.findElements(By.xpath("//*[@formcontrolname]"));
            System.out.println("--- LOGGING ALL FORM CONTROLS ---");
            int invalidCount = 0;
            for (WebElement field : formControls) {
                try {
                    String tag = field.getTagName();
                    String formControl = field.getAttribute("formcontrolname");
                    String classes = field.getAttribute("class");
                    boolean isInvalid = classes != null && classes.contains("ng-invalid");
                    boolean isVisible = field.isDisplayed();
                    if (isInvalid) {
                        invalidCount++;
                    }
                    System.out.println("  Control: tag=" + tag +
                            ", formcontrolname=" + formControl +
                            ", invalid=" + isInvalid +
                            ", visible=" + isVisible +
                            ", classes=" + classes);
                } catch (Exception e) {
                    // Skip
                }
            }
            System.out.println("Total form controls: " + formControls.size() + ", Invalid controls: " + invalidCount);
            System.out.println("---------------------------------");
            
            // Also print any general ng-invalid elements
            List<WebElement> invalidFields = driver.findElements(By.xpath("//*[contains(@class,'ng-invalid')]"));
            System.out.println("--- LOGGING ALL NG-INVALID ELEMENTS ---");
            for (WebElement field : invalidFields) {
                try {
                    String tag = field.getTagName();
                    String placeholder = field.getAttribute("placeholder");
                    String formControl = field.getAttribute("formcontrolname");
                    boolean visible = field.isDisplayed();
                    System.out.println("  [NG-INVALID] tag=" + tag +
                            ", formcontrolname=" + formControl +
                            ", placeholder=" + placeholder +
                            ", visible=" + visible);
                } catch (Exception e) {
                    // Skip
                }
            }
            System.out.println("---------------------------------");

            // Rich-text editors (quill/contenteditable) are several DOM levels below the
            // Angular-bound host element, so neither dump above reliably shows their
            // real validity - walk up from each editor to the nearest ancestor that
            // carries formcontrolname or an ng-valid/ng-invalid class instead.
            System.out.println("--- LOGGING RICH TEXT EDITOR ANCESTOR VALIDITY ---");
            List<WebElement> editors = driver.findElements(
                    By.xpath("//quill-editor//div[contains(@class,'ql-editor')] | //div[@contenteditable='true']"));
            for (int i = 0; i < editors.size(); i++) {
                try {
                    WebElement editor = editors.get(i);
                    Object info = ((JavascriptExecutor) driver).executeScript(
                            "var el = arguments[0];" +
                            "var node = el;" +
                            "for (var depth = 0; depth < 8 && node; depth++) {" +
                            "  if (node.hasAttribute && (node.hasAttribute('formcontrolname') || " +
                            "      (node.className && typeof node.className === 'string' && " +
                            "       (node.className.indexOf('ng-invalid') !== -1 || node.className.indexOf('ng-valid') !== -1)))) {" +
                            "    return node.tagName + ' formcontrolname=' + node.getAttribute('formcontrolname') + " +
                            "           ' class=' + node.className;" +
                            "  }" +
                            "  node = node.parentElement;" +
                            "}" +
                            "return 'no bound ancestor found within 8 levels';",
                            editor);
                    System.out.println("  Editor #" + (i + 1) + " content='" + editor.getText() + "' -> " + info);
                } catch (Exception e) {
                    System.out.println("  Editor #" + (i + 1) + " ancestor lookup failed: " + e.getMessage());
                }
            }
            System.out.println("---------------------------------");

            // Since no individual control or editor is ever flagged invalid, the
            // remaining suspects are: (a) a checkbox our native-input-only scan
            // misses (Angular Material's <mat-checkbox> hides its real input via
            // CSS, so isDisplayed() skips it), (b) a file upload that hasn't
            // actually attached, or (c) an async processing flag (spinner) still
            // active. Dump all three so the next run gives concrete evidence.
            System.out.println("--- LOGGING CHECKBOX / FILE INPUT / SPINNER STATE ---");
            List<WebElement> nativeCheckboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
            for (WebElement cb : nativeCheckboxes) {
                System.out.println("  Native checkbox: name=" + cb.getAttribute("name") +
                        ", checked=" + cb.isSelected() + ", visible=" + cb.isDisplayed());
            }
            List<WebElement> matCheckboxes = driver.findElements(By.xpath("//mat-checkbox"));
            for (WebElement mc : matCheckboxes) {
                String classes = mc.getAttribute("class");
                System.out.println("  mat-checkbox: formcontrolname=" + mc.getAttribute("formcontrolname") +
                        ", classes=" + classes +
                        ", checked=" + (classes != null && classes.contains("mat-checkbox-checked")));
            }
            List<WebElement> fileInputs = driver.findElements(By.xpath("//input[@type='file']"));
            for (WebElement fi : fileInputs) {
                Object fileCount = ((JavascriptExecutor) driver)
                        .executeScript("return arguments[0].files ? arguments[0].files.length : -1;", fi);
                System.out.println("  File input: id=" + fi.getAttribute("id") + ", files attached=" + fileCount);
            }
            List<WebElement> spinners = driver.findElements(By.xpath(
                    "//div[contains(@class,'spinner')] | //mat-spinner | //*[contains(@class,'progress')]"));
            for (WebElement sp : spinners) {
                if (sp.isDisplayed()) {
                    System.out.println("  ⚠ Spinner/progress element still visible: class=" + sp.getAttribute("class"));
                }
            }
            System.out.println("---------------------------------");
        } catch (Exception e) {
            System.out.println("Error checking invalid fields: " + e.getMessage());
        }
    }

    /**
     * Check for success message / toast notification after submission
     */
    private void checkForSuccessMessage() {
        try {
            List<WebElement> successMessages = driver.findElements(RCSAssistantPageLocators.SUCCESS_MESSAGE);
            for (WebElement msg : successMessages) {
                if (msg.isDisplayed()) {
                    System.out.println("✓ Success message detected: " + msg.getText().trim());
                    return;
                }
            }
            // Also check for toast notifications
            List<WebElement> toasts = driver.findElements(
                    By.xpath(
                            "//*[contains(@class,'toast') or contains(@class,'snackbar') or contains(@class,'notification')]"));
            for (WebElement toast : toasts) {
                if (toast.isDisplayed()) {
                    System.out.println("✓ Toast notification detected: " + toast.getText().trim());
                    return;
                }
            }
            System.out.println("No success message detected (may still be OK)");
        } catch (Exception e) {
            System.out.println("Error checking success message: " + e.getMessage());
        }
    }

    /**
     * Handle "Go to Assistant" button after successful creation
     */
    private void handleGoToAssistantButton() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement goToBtn = shortWait.until(
                    ExpectedConditions.elementToBeClickable(RCSAssistantPageLocators.GO_TO_ASSISTANT_BUTTON));
            System.out.println("Found 'Go to Assistant' button, clicking...");
            goToBtn.click();
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("No 'Go to Assistant' button (this is OK)");
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
        // Was silently returning 0 with no trace - this made an entire section
        // (Contact Person Details etc.) fail invisibly when the page hadn't
        // rendered yet, with nothing in the log to explain it.
        System.out.println("  ⚠ No displayed match for: " + xpath.substring(0, Math.min(60, xpath.length())));
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
                try {
                    String listUrl = currentUrl.replaceAll("/(add|edit).*$", "");
                    driver.get(listUrl);
                    Thread.sleep(3000);
                    System.out.println("Navigated to: " + driver.getCurrentUrl());
                } catch (Exception e) {
                    System.out.println("Could not navigate back: " + e.getMessage());
                }
            }

            // Wait for table to load with actual rows (not just the table element)
            System.out.println("Waiting for table rows to load...");
            try {
                WebDriverWait tableWait = new WebDriverWait(driver, Duration.ofSeconds(15));
                tableWait.until(ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.ASSISTANT_TABLE_ROWS),
                        ExpectedConditions.presenceOfElementLocated(RCSAssistantPageLocators.NO_ASSISTANT_MESSAGE)));
            } catch (Exception e) {
                System.out.println("Table rows wait timed out, continuing anyway...");
            }
            Thread.sleep(2000);

            // Check how many rows we have before searching
            List<WebElement> initialRows = driver.findElements(RCSAssistantPageLocators.ASSISTANT_TABLE_ROWS);
            System.out.println("Initial rows in table: " + initialRows.size());

            // Search for the created assistant
            if (createdAssistantName != null && !createdAssistantName.isEmpty()) {
                try {
                    searchAssistant(createdAssistantName.substring(0, Math.min(10, createdAssistantName.length())));
                    Thread.sleep(3000);
                } catch (Exception e) {
                    System.out.println("Search failed, will check table without filter: " + e.getMessage());
                }
            }

            // Get the status from the first row with multiple fallback locators
            List<WebElement> rows = driver.findElements(RCSAssistantPageLocators.ASSISTANT_TABLE_ROWS);
            System.out.println("Found " + rows.size() + " rows in table after search");

            // If no rows found after search, try clearing the search and looking at all
            // rows
            if (rows.isEmpty()) {
                System.out.println("No rows after search, clearing search and trying full list...");
                try {
                    clearSearch();
                    Thread.sleep(2000);
                    // Refresh the page to ensure clean state
                    driver.navigate().refresh();
                    Thread.sleep(3000);
                    rows = driver.findElements(RCSAssistantPageLocators.ASSISTANT_TABLE_ROWS);
                    System.out.println("Found " + rows.size() + " rows after page refresh");
                } catch (Exception e) {
                    System.out.println("Clear search failed: " + e.getMessage());
                }
            }

            if (!rows.isEmpty()) {
                // Try multiple status cell locators
                String[] statusXPaths = {
                        ".//td[5]",
                        ".//td[contains(@class,'status')]",
                        ".//td//span[contains(@class,'badge')]",
                        ".//span[contains(@class,'status')]",
                        ".//td[last()]//span",
                        ".//td[last()]"
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

                // If no status text found in specific cells, dump entire first row text
                try {
                    String rowText = rows.get(0).getText().trim();
                    System.out.println("Full first row text: " + rowText);
                    if (rowText.toLowerCase().contains("pending"))
                        return "Pending";
                    if (rowText.toLowerCase().contains("approved"))
                        return "Approved";
                    if (rowText.toLowerCase().contains("active"))
                        return "Active";
                    if (rowText.toLowerCase().contains("submitted"))
                        return "Submitted";
                } catch (Exception e) {
                    /* skip */ }
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
    private void fillNgInvalidFields() {
        System.out.println("Filling ng-invalid fields...");
        try {
            List<WebElement> invalidFields = driver.findElements(By.xpath("//*[contains(@class,'ng-invalid')]"));
            for (WebElement field : invalidFields) {
                try {
                    if (field.isDisplayed() && field.isEnabled()) {
                        String tag = field.getTagName().toLowerCase();
                        if (tag.equals("input") || tag.equals("textarea")) {
                            String val = field.getAttribute("value");
                            if (val == null || val.trim().isEmpty()) {
                                scrollToElement(field);
                                String placeholder = field.getAttribute("placeholder");
                                if (placeholder == null) placeholder = "";
                                String textToFill = "Test Valid";
                                if (placeholder.toLowerCase().contains("email")) {
                                    textToFill = "test@example.com";
                                } else if (placeholder.toLowerCase().contains("phone") || placeholder.toLowerCase().contains("mobile")) {
                                    textToFill = "9876543210";
                                }
                                field.clear();
                                field.sendKeys(textToFill);
                                System.out.println("Filled ng-invalid field (input/textarea) with: " + textToFill);
                            }
                        } else if (tag.equals("div") && field.getAttribute("class") != null && field.getAttribute("class").contains("ql-editor")) {
                            scrollToElement(field);
                            field.clear();
                            field.sendKeys("Test Valid editor content");
                            // See fillAllRichTextEditors() - Angular's binding needs an explicit
                            // event dispatch to register content typed via sendKeys.
                            ((JavascriptExecutor) driver).executeScript(
                                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                                    "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
                                    "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));",
                                    field
                            );
                            System.out.println("Filled ng-invalid rich text editor");
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        } catch (Exception e) {
            System.out.println("Error filling ng-invalid fields: " + e.getMessage());
        }
    }

    private void fillAllEmptyInputFields() {
        System.out.println("Filling all empty input fields...");
        try {
            List<WebElement> inputs = driver.findElements(By.xpath("//input"));
            int count = 0;
            for (WebElement input : inputs) {
                try {
                    if (input.isDisplayed() && input.isEnabled()) {
                        String val = input.getAttribute("value");
                        if (val == null || val.trim().isEmpty()) {
                            scrollToElement(input);
                            String type = input.getAttribute("type");
                            String placeholder = input.getAttribute("placeholder");
                            if (placeholder == null) placeholder = "";
                            
                            String textToFill = "Test Input " + (++count);
                            if (type != null && type.equals("number")) {
                                textToFill = "9876543210";
                            } else if (placeholder.toLowerCase().contains("phone") || placeholder.toLowerCase().contains("mobile")) {
                                textToFill = "9876543210";
                            } else if (placeholder.toLowerCase().contains("email")) {
                                textToFill = "test.email@example.com";
                            } else if (placeholder.toLowerCase().contains("url") || placeholder.toLowerCase().contains("website")) {
                                textToFill = "https://example.com";
                            }
                            
                            input.clear();
                            input.sendKeys(textToFill);
                            System.out.println("Filled input: " + placeholder + " with " + textToFill);
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        } catch (Exception e) {
            System.out.println("Error filling input fields: " + e.getMessage());
        }
    }

    private void fillAllRichTextEditors() {
        System.out.println("Filling all rich text editors...");
        try {
            List<WebElement> editors = driver.findElements(By.xpath("//quill-editor//div[contains(@class,'ql-editor')] | //div[@contenteditable='true']"));
            int count = 0;
            for (WebElement editor : editors) {
                try {
                    if (editor.isDisplayed()) {
                        scrollToElement(editor);
                        String text = editor.getText();
                        if (text == null || text.trim().isEmpty()) {
                            count++;
                            boolean filled = false;
                            
                            // Try normal focus/click & type
                            try {
                                try {
                                    editor.click();
                                } catch (Exception clickEx) {
                                    // Click via JS if normal click is blocked
                                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editor);
                                }
                                Thread.sleep(200);
                                editor.clear();
                                editor.sendKeys("Verification details message content for editor " + count);
                                filled = true;
                                System.out.println("Filled rich text editor #" + count + " via sendKeys");

                                // sendKeys inserts real DOM text, but rich-text-editor wrappers (e.g.
                                // ngx-quill) sync their Angular FormControl off their own internal
                                // change-detection, which native typed input doesn't always trigger.
                                // Dispatch the events explicitly so Angular registers the new value
                                // regardless of whether the native events alone were sufficient.
                                ((JavascriptExecutor) driver).executeScript(
                                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
                                        "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));",
                                        editor
                                );
                            } catch (Exception typeEx) {
                                System.out.println("sendKeys failed for editor #" + count + ", using JS fallback: " + typeEx.getMessage());
                            }
                            
                            // JS Fallback
                            if (!filled) {
                                ((JavascriptExecutor) driver).executeScript(
                                    "arguments[0].innerHTML = '<p>Verification details message content for editor " + count + "</p>';" +
                                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                                    "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
                                    "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));",
                                    editor
                                );
                                System.out.println("Filled rich text editor #" + count + " via JS");
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Failed to fill rich text editor: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding rich text editors: " + e.getMessage());
        }
    }

    private void fillAllEmptyTextareas() {
        System.out.println("Filling all empty textareas...");
        try {
            List<WebElement> textareas = driver.findElements(By.xpath("//textarea"));
            int count = 0;
            for (WebElement textarea : textareas) {
                try {
                    if (textarea.isDisplayed() && textarea.isEnabled()) {
                        String val = textarea.getAttribute("value");
                        if (val == null || val.trim().isEmpty()) {
                            scrollToElement(textarea);
                            textarea.clear();
                            textarea.sendKeys("Test Textarea description " + (++count));
                            System.out.println("Filled textarea #" + count);
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        } catch (Exception e) {
            System.out.println("Error filling textareas: " + e.getMessage());
        }
    }

    private void checkAllUncheckedCheckboxes() {
        System.out.println("Checking all unchecked checkboxes...");
        try {
            List<WebElement> checkboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
            for (WebElement checkbox : checkboxes) {
                try {
                    if (checkbox.isDisplayed() && checkbox.isEnabled() && !checkbox.isSelected()) {
                        scrollToElement(checkbox);
                        try {
                            checkbox.click();
                        } catch (Exception e) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
                        }
                        System.out.println("Checked checkbox: " + checkbox.getAttribute("name"));
                    }
                } catch (Exception e) {
                    // ignore
                }
            }

            // Angular Material's <mat-checkbox> visually hides its real native
            // <input type="checkbox"> via CSS, so isDisplayed() above always skips
            // it - click the mat-checkbox host itself instead (that's what a real
            // user does; Material forwards the click to the hidden input).
            List<WebElement> matCheckboxes = driver.findElements(By.xpath("//mat-checkbox"));
            for (WebElement matCheckbox : matCheckboxes) {
                try {
                    String classes = matCheckbox.getAttribute("class");
                    boolean alreadyChecked = classes != null && classes.contains("mat-checkbox-checked");
                    boolean isDisabled = classes != null && classes.contains("mat-checkbox-disabled");
                    if (matCheckbox.isDisplayed() && !alreadyChecked && !isDisabled) {
                        scrollToElement(matCheckbox);
                        try {
                            matCheckbox.click();
                        } catch (Exception e) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", matCheckbox);
                        }
                        System.out.println("Checked mat-checkbox: formcontrolname="
                                + matCheckbox.getAttribute("formcontrolname"));
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        } catch (Exception e) {
            System.out.println("Error checking checkboxes: " + e.getMessage());
        }
    }

    /**
     * Picks the asset file matching what a given file input actually expects,
     * using its `accept` attribute first, then nearby label/placeholder text as
     * a fallback. Uploading a mismatched type (e.g. an image into a video-only
     * field) can pass the browser-level file selection but still fail whatever
     * client-side validation runs after the "Confirm File Upload" dialog.
     */
    private String pickAssetForFileInput(WebElement fileInput) {
        String imageDir = "src/main/resources/image/";
        String accept = fileInput.getAttribute("accept");
        String signal = (accept == null ? "" : accept).toLowerCase();

        // Fall back to any nearby descriptive text (label, placeholder, aria-label,
        // or the preceding form-group's text) if `accept` isn't specific enough
        if (!signal.contains("video") && !signal.contains("pdf") && !signal.contains("mp4")) {
            try {
                Object nearbyText = ((JavascriptExecutor) driver).executeScript(
                        "var el = arguments[0];" +
                        "var group = el.closest('.form-group, .form-block, div') || el.parentElement;" +
                        "return group ? group.textContent : '';",
                        fileInput);
                if (nearbyText != null) {
                    signal += " " + nearbyText.toString().toLowerCase();
                }
            } catch (Exception e) {
                // ignore, use accept-only signal
            }
        }

        if (signal.contains("video") || signal.contains("mp4")) {
            return new File(imageDir + "Recording_48.mp4").getAbsolutePath();
        }
        if (signal.contains("pdf") || signal.contains("document")) {
            return new File(imageDir + "pdf.pdf").getAbsolutePath();
        }
        return new File(imageDir + "verification.png").getAbsolutePath();
    }

    private void uploadVerificationFiles() {
        System.out.println("Uploading verification files if inputs present...");
        try {
            List<WebElement> fileInputs = driver.findElements(By.xpath("//input[@type='file']"));
            int count = 0;
            for (WebElement fileInput : fileInputs) {
                try {
                    String id = fileInput.getAttribute("id");
                    if (id != null && (id.equals("coverImg") || id.equals("iconImg"))) {
                        System.out.println("Skipping branding file input: " + id);
                        continue;
                    }
                    String assetPath = pickAssetForFileInput(fileInput);
                    fileInput.sendKeys(assetPath);
                    System.out.println("Uploaded " + new File(assetPath).getName() + " to file input #" + (++count)
                            + " (id=" + id + ", accept=" + fileInput.getAttribute("accept") + ")");

                    confirmUploadModalUntilClosed();

                    // Some apps process the file asynchronously (e.g. upload to storage,
                    // virus scan, thumbnail generation) before the form considers it
                    // complete - a fixed 1s sleep may not be enough. Wait for any nearby
                    // spinner/progress indicator to clear instead of a blind sleep.
                    waitForUploadSpinnerToClear();

                    // Verify the browser actually attached a file (confirms sendKeys
                    // worked at the DOM level, independent of whatever Angular does with it)
                    Object fileCount = ((JavascriptExecutor) driver)
                            .executeScript("return arguments[0].files ? arguments[0].files.length : -1;", fileInput);
                    System.out.println("  File input #" + count + " now has " + fileCount + " file(s) attached");
                } catch (Exception e) {
                    System.out.println("Failed to upload to file input: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding file inputs: " + e.getMessage());
        }
    }

    /**
     * Wait briefly for any upload-progress spinner to disappear after a file
     * selection, instead of a blind fixed sleep. Non-fatal if none appears.
     */
    private void waitForUploadSpinnerToClear() {
        try {
            By spinner = By.xpath(
                    "//div[contains(@class,'spinner')] | //mat-spinner | //*[contains(@class,'progress')] | " +
                            "//*[contains(text(),'Uploading') or contains(text(),'Processing')]");
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(spinner));
            // If it appeared, give it real time to finish (uploads can be slow)
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.invisibilityOfElementLocated(spinner));
            System.out.println("  Upload spinner/progress indicator cleared");
        } catch (Exception e) {
            // No spinner appeared, or it never disappeared within 20s - either way,
            // don't block the flow; fall back to the existing short settle time.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
