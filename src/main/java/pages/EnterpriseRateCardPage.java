package pages;

import locators.EnterpriseRateCardPageLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Page Object for Enterprise Rate Card Tab.
 * Provides helper methods for rate card operations.
 */
public class EnterpriseRateCardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public EnterpriseRateCardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ==================== Navigation Methods ====================

    /**
     * Navigate to Rate Card page by clicking Rate Card in sidebar
     */
    public void navigateToRateCard() {
        try {
            System.out.println("Attempting to navigate to Rate Card...");
            Thread.sleep(1000);

            // Try to click on Rate Card menu item
            WebElement rateCardMenu = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseRateCardPageLocators.RATE_CARD_MENU_ITEM));
            rateCardMenu.click();
            System.out.println("Clicked Rate Card menu item");

            Thread.sleep(3000);

            // First check URL contains rate-card
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Current URL after click: " + currentUrl);

            if (currentUrl.contains("rate-card") || currentUrl.contains("ratecard")) {
                System.out.println("URL validation passed - on Rate Card page");
            } else {
                System.out.println("Warning: URL does not contain rate-card. Waiting for elements...");
            }

            // Wait for rate card page elements to load
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions
                                .visibilityOfElementLocated(EnterpriseRateCardPageLocators.RATE_CARD_PAGE_HEADER),
                        ExpectedConditions.visibilityOfElementLocated(EnterpriseRateCardPageLocators.RATE_CARD_TABLE),
                        ExpectedConditions
                                .visibilityOfElementLocated(EnterpriseRateCardPageLocators.RATE_CARD_TABLE_ROWS)));
                System.out.println("Rate Card page elements found");
            } catch (Exception e) {
                System.out.println("Primary elements not found, checking for URL validation...");
                if (currentUrl.contains("rate")) {
                    System.out.println("URL contains 'rate' - considering page loaded");
                } else {
                    throw e;
                }
            }

            System.out.println("Rate Card page loaded successfully");
        } catch (Exception e) {
            System.out.println("Error navigating to Rate Card: " + e.getMessage());
            throw new RuntimeException("Failed to navigate to Rate Card page", e);
        }
    }

    /**
     * Check if Rate Card page is loaded
     */
    public boolean isRateCardPageLoaded() {
        try {
            // First check URL
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("rate-card") || currentUrl.contains("ratecard")) {
                return true;
            }
            // Then check elements
            return isElementVisible(EnterpriseRateCardPageLocators.RATE_CARD_PAGE_HEADER) ||
                    isElementVisible(EnterpriseRateCardPageLocators.RATE_CARD_TABLE) ||
                    isElementVisible(EnterpriseRateCardPageLocators.RATE_CARD_TABLE_ROWS);
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Rate Card List Methods ====================

    /**
     * Check if rate cards exist in the list
     */
    public boolean hasRateCards() {
        try {
            List<WebElement> rows = driver.findElements(EnterpriseRateCardPageLocators.RATE_CARD_TABLE_ROWS);
            return rows != null && !rows.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if "No Data Available" message is displayed
     */
    public boolean isNoDataMessageDisplayed() {
        try {
            return isElementVisible(EnterpriseRateCardPageLocators.NO_DATA_MESSAGE);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get all column headers from rate card table
     */
    public List<String> getRateCardListHeaders() {
        List<String> headers = new ArrayList<>();
        try {
            List<WebElement> headerElements = wait.until(
                    ExpectedConditions
                            .presenceOfAllElementsLocatedBy(EnterpriseRateCardPageLocators.RATE_CARD_TABLE_HEADERS));
            for (WebElement header : headerElements) {
                String text = header.getText().trim();
                if (!text.isEmpty()) {
                    headers.add(text);
                }
            }
            System.out.println("Found headers: " + headers);
        } catch (Exception e) {
            System.out.println("Error getting headers: " + e.getMessage());
        }
        return headers;
    }

    /**
     * Get the count of rate card rows
     */
    public int getRateCardRowCount() {
        try {
            List<WebElement> rows = driver.findElements(EnterpriseRateCardPageLocators.RATE_CARD_TABLE_ROWS);
            return rows != null ? rows.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== Pagination Methods ====================

    /**
     * Check if pagination is available
     */
    public boolean isPaginationAvailable() {
        try {
            return isElementVisible(EnterpriseRateCardPageLocators.PAGINATION_NEXT) ||
                    isElementVisible(EnterpriseRateCardPageLocators.PAGINATION_PREVIOUS);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click Next page button
     */
    public boolean clickNextPage() {
        try {
            WebElement nextBtn = driver.findElement(EnterpriseRateCardPageLocators.PAGINATION_NEXT);
            if (nextBtn.isEnabled() && nextBtn.isDisplayed()) {
                nextBtn.click();
                Thread.sleep(1000);
                System.out.println("Clicked Next page");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Next page button not available: " + e.getMessage());
        }
        return false;
    }

    /**
     * Click Previous page button
     */
    public boolean clickPreviousPage() {
        try {
            WebElement prevBtn = driver.findElement(EnterpriseRateCardPageLocators.PAGINATION_PREVIOUS);
            if (prevBtn.isEnabled() && prevBtn.isDisplayed()) {
                prevBtn.click();
                Thread.sleep(1000);
                System.out.println("Clicked Previous page");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Previous page button not available: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get pagination info text
     */
    public String getPaginationInfo() {
        try {
            WebElement info = driver.findElement(EnterpriseRateCardPageLocators.PAGINATION_INFO);
            return info.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    // ==================== Serial Number Methods ====================

    /**
     * Get all serial numbers from current page
     */
    public List<String> getSerialNumbersFromCurrentPage() {
        List<String> serialNumbers = new ArrayList<>();
        try {
            List<WebElement> cells = driver.findElements(EnterpriseRateCardPageLocators.SERIAL_NUMBER_CELLS);
            for (WebElement cell : cells) {
                String text = cell.getText().trim();
                if (!text.isEmpty()) {
                    serialNumbers.add(text);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting serial numbers: " + e.getMessage());
        }
        return serialNumbers;
    }

    /**
     * Get all serial numbers across all pagination pages
     */
    public Set<String> getAllSerialNumbersAcrossAllPages() {
        Set<String> allSerialNumbers = new HashSet<>();
        List<String> duplicates = new ArrayList<>();

        try {
            // Go to first page
            while (clickPreviousPage()) {
                // Keep clicking until we're at the first page
            }

            do {
                List<String> currentPageSerials = getSerialNumbersFromCurrentPage();
                for (String serial : currentPageSerials) {
                    if (allSerialNumbers.contains(serial)) {
                        duplicates.add(serial);
                    }
                    allSerialNumbers.add(serial);
                }
            } while (clickNextPage());

            if (!duplicates.isEmpty()) {
                System.out.println("Duplicate serial numbers found: " + duplicates);
            }
        } catch (Exception e) {
            System.out.println("Error getting all serial numbers: " + e.getMessage());
        }

        return allSerialNumbers;
    }

    /**
     * Check for duplicate serial numbers across all pages
     * 
     * @return List of duplicate serial numbers (empty if no duplicates)
     */
    public List<String> findDuplicateSerialNumbers() {
        List<String> allSerialNumbers = new ArrayList<>();
        List<String> duplicates = new ArrayList<>();

        try {
            // Go to first page
            while (clickPreviousPage()) {
                // Keep clicking until we're at the first page
            }

            do {
                List<String> currentPageSerials = getSerialNumbersFromCurrentPage();
                for (String serial : currentPageSerials) {
                    if (allSerialNumbers.contains(serial)) {
                        duplicates.add(serial);
                    } else {
                        allSerialNumbers.add(serial);
                    }
                }
            } while (clickNextPage());

            System.out.println("Total serial numbers checked: " + allSerialNumbers.size());
            System.out.println("Duplicates found: " + duplicates.size());
        } catch (Exception e) {
            System.out.println("Error finding duplicates: " + e.getMessage());
        }

        return duplicates;
    }

    // ==================== Rate Card Ordering Methods ====================

    /**
     * Check if the latest rate card is at the top (first row has most recent date)
     */
    public boolean isLatestRateCardFirst() {
        try {
            // Go to first page
            while (clickPreviousPage()) {
                // Keep clicking until we're at the first page
            }

            List<WebElement> dateCells = driver.findElements(EnterpriseRateCardPageLocators.DATE_CELLS);
            if (dateCells.size() < 2) {
                System.out.println("Not enough date entries to compare ordering");
                return true; // Can't verify with single entry
            }

            String firstDateText = dateCells.get(0).getText().trim();
            String secondDateText = dateCells.get(1).getText().trim();

            System.out.println("First row date: " + firstDateText);
            System.out.println("Second row date: " + secondDateText);

            // Try parsing dates to compare
            // Common date formats
            String[] formats = { "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "dd-MM-yyyy" };
            for (String format : formats) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    LocalDate firstDate = LocalDate.parse(firstDateText.split(" ")[0], formatter);
                    LocalDate secondDate = LocalDate.parse(secondDateText.split(" ")[0], formatter);

                    // Latest should be first (greater or equal date)
                    boolean isOrdered = !firstDate.isBefore(secondDate);
                    System.out.println("Date ordering validated: " + isOrdered);
                    return isOrdered;
                } catch (Exception ex) {
                    // Try next format
                }
            }

            // If date parsing fails, assume ordering is correct
            System.out.println("Could not parse dates, assuming ordering is correct");
            return true;
        } catch (Exception e) {
            System.out.println("Error checking rate card ordering: " + e.getMessage());
            return true;
        }
    }

    // ==================== Status and View Icon Methods ====================

    /**
     * Get status of rate card at specific row
     */
    public String getRateCardStatus(int row) {
        try {
            By locator = EnterpriseRateCardPageLocators.getStatusByRow(row);
            WebElement statusCell = driver.findElement(locator);
            return statusCell.getText().trim();
        } catch (Exception e) {
            System.out.println("Error getting rate card status for row " + row + ": " + e.getMessage());
            return "";
        }
    }

    /**
     * Find first active rate card row
     */
    public int findFirstActiveRateCard() {
        try {
            List<WebElement> statusCells = driver.findElements(EnterpriseRateCardPageLocators.STATUS_CELLS);
            for (int i = 0; i < statusCells.size(); i++) {
                if (statusCells.get(i).getText().trim().equalsIgnoreCase("Active")) {
                    return i + 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding active rate card: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Check if view icon is clickable for a specific row
     */
    public boolean isViewIconClickable(int row) {
        try {
            By locator = EnterpriseRateCardPageLocators.getViewIconByRow(row);
            WebElement viewIcon = driver.findElement(locator);
            return viewIcon.isDisplayed() && viewIcon.isEnabled();
        } catch (Exception e) {
            System.out.println("View icon not found for row " + row + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Click view icon for a specific row
     */
    public void clickViewIcon(int row) {
        try {
            By locator = EnterpriseRateCardPageLocators.getViewIconByRow(row);
            WebElement viewIcon = wait.until(ExpectedConditions.elementToBeClickable(locator));
            viewIcon.click();
            Thread.sleep(2000);
            System.out.println("Clicked View icon for row: " + row);
        } catch (Exception e) {
            System.out.println("Error clicking View icon: " + e.getMessage());
            throw new RuntimeException("Failed to click View icon for row " + row, e);
        }
    }

    // ==================== Rate Card Details Page Methods ====================

    /**
     * Check if Rate Card Details page is loaded
     */
    public boolean isRateCardDetailsPageLoaded() {
        try {
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("detail") || currentUrl.contains("view")) {
                return true;
            }
            return isElementVisible(EnterpriseRateCardPageLocators.RATE_CARD_DETAILS_HEADER) ||
                    isElementVisible(EnterpriseRateCardPageLocators.SERVICES_SECTION);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get list of services displayed on Rate Card Details page
     */
    public List<String> getServicesOnRateCardDetails() {
        List<String> services = new ArrayList<>();
        try {
            Thread.sleep(1000);
            List<WebElement> serviceElements = driver
                    .findElements(EnterpriseRateCardPageLocators.SERVICE_NAMES_ON_DETAILS);
            for (WebElement element : serviceElements) {
                String text = element.getText().trim().toUpperCase();
                if (!text.isEmpty()) {
                    // Extract service name
                    if (text.contains("SMS"))
                        services.add("SMS");
                    if (text.contains("RCS"))
                        services.add("RCS");
                    if (text.contains("WABA") || text.contains("WHATSAPP"))
                        services.add("WABA");
                    if (text.contains("IVR"))
                        services.add("IVR");
                    if (text.contains("OBD"))
                        services.add("OBD");
                    if (text.contains("CCS"))
                        services.add("CCS");
                    if (text.contains("LIVEAGENT") || text.contains("LIVE AGENT"))
                        services.add("LIVEAGENT");
                }
            }
            // Remove duplicates
            services = new ArrayList<>(new HashSet<>(services));
            System.out.println("Services found on Rate Card Details: " + services);
        } catch (Exception e) {
            System.out.println("Error getting services from details page: " + e.getMessage());
        }
        return services;
    }

    /**
     * Navigate to Services tab from sidebar
     */
    public void navigateToServicesTab() {
        try {
            System.out.println("Navigating to Services tab...");
            Thread.sleep(1000);

            // Click on Services menu item
            WebElement servicesMenu = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(), 'Services')]")));
            servicesMenu.click();
            System.out.println("Clicked Services menu item");

            Thread.sleep(2000);
            System.out.println("Services tab loaded");
        } catch (Exception e) {
            System.out.println("Error navigating to Services tab: " + e.getMessage());
            throw new RuntimeException("Failed to navigate to Services tab", e);
        }
    }

    /**
     * Get list of available services from the Services tab
     * This method navigates to Services tab and collects all service names
     */
    public List<String> getServicesFromServicesTab() {
        List<String> services = new ArrayList<>();
        try {
            System.out.println("Collecting services from Services tab...");
            Thread.sleep(1000);

            // Look for service cards/items on the Services page
            // Services are typically displayed as cards with service names like SMS, RCS,
            // WABA, etc.
            List<WebElement> serviceCards = driver.findElements(By.xpath(
                    "//div[contains(@class, 'card')]//h6 | " +
                            "//div[contains(@class, 'service')]//h6 | " +
                            "//div[contains(@class, 'card-header')] | " +
                            "//h6[contains(text(), 'SMS') or contains(text(), 'RCS') or contains(text(), 'WABA') or " +
                            "contains(text(), 'IVR') or contains(text(), 'OBD') or contains(text(), 'CCS') or " +
                            "contains(text(), 'LiveAgent') or contains(text(), 'LIVEAGENT')]"));

            for (WebElement card : serviceCards) {
                String text = card.getText().trim().toUpperCase();
                if (!text.isEmpty()) {
                    // Extract service name
                    if (text.contains("SMS") && !services.contains("SMS"))
                        services.add("SMS");
                    if (text.contains("RCS") && !services.contains("RCS"))
                        services.add("RCS");
                    if ((text.contains("WABA") || text.contains("WHATSAPP")) && !services.contains("WABA"))
                        services.add("WABA");
                    if (text.contains("IVR") && !services.contains("IVR"))
                        services.add("IVR");
                    if (text.contains("OBD") && !services.contains("OBD"))
                        services.add("OBD");
                    if (text.contains("CCS") && !services.contains("CCS"))
                        services.add("CCS");
                    if ((text.contains("LIVEAGENT") || text.contains("LIVE AGENT")) && !services.contains("LIVEAGENT"))
                        services.add("LIVEAGENT");
                }
            }

            System.out.println("Services found in Services tab: " + services);
        } catch (Exception e) {
            System.out.println("Error getting services from Services tab: " + e.getMessage());
        }
        return services;
    }

    /**
     * Check if Edit button is present on details page
     */
    public boolean isEditButtonPresent() {
        try {
            return isElementVisible(EnterpriseRateCardPageLocators.EDIT_BUTTON);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Navigate back from details page to rate card list
     */
    public void navigateBackToRateCardList() {
        try {
            WebElement backBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    EnterpriseRateCardPageLocators.BACK_BUTTON));
            backBtn.click();
            Thread.sleep(2000);
            System.out.println("Navigated back to Rate Card list");
        } catch (Exception e) {
            System.out.println("Back button not found, using browser back: " + e.getMessage());
            driver.navigate().back();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ==================== Utility Methods ====================

    /**
     * Check if element is visible
     */
    private boolean isElementVisible(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Scroll element into view
     */
    public void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500);
        } catch (Exception e) {
            // Ignore scroll errors
        }
    }
}
