package locators;

import org.openqa.selenium.By;

public class DashboardPageLocators {
        // Common Elements
        public static final By SIDEBAR_CONTAINER = By.xpath("//div[contains(@class, 'sidebar')]"); // Generic sidebar
                                                                                                   // container
        public static final By HEADER_TITLE = By.xpath("//h6[contains(@class, 'font-weight-bolder')]"); // Page header
                                                                                                        // title
        public static final By PROFILE_ICON = By
                        .xpath("//div[contains(@class, 'sidenav-header')]//button[contains(@class, 'mat-mdc-menu-trigger')]"); // Same
                                                                                                                               // as
                                                                                                                               // implementation
                                                                                                                               // in
                                                                                                                               // LoginPageLocators

        // Role Display Locators - to verify logged in role on dashboard
        public static final By ROLE_DISPLAY_TEXT = By.xpath(
                        "//div[contains(@class, 'sidenav-header')]//span[contains(@class, 'text') or contains(@class, 'role')] | "
                                        +
                                        "//div[contains(@class, 'user-info')]//span | " +
                                        "//div[contains(@class, 'header')]//span[contains(text(), 'Admin') or contains(text(), 'Manager') or contains(text(), 'Enterprise')]");

        // Alternative: Find role text in header/sidebar area
        public static final By HEADER_USER_INFO = By
                        .xpath("//div[contains(@class, 'sidenav-header')]//div | //div[contains(@class, 'user-info')]");

        // Dynamic locator to check for specific role name
        public static By getRoleDisplayLocator(String roleName) {
                return By.xpath(
                                "//div[contains(@class, 'sidenav-header')]//*[contains(text(), '" + roleName + "')] | "
                                                +
                                                "//div[contains(@class, 'user-info')]//*[contains(text(), '" + roleName
                                                + "')] | " +
                                                "//div[contains(@class, 'header')]//*[contains(text(), '" + roleName
                                                + "')]");
        }

        // SPARC Logo - Universal dashboard indicator
        public static final By SPARC_LOGO = By.xpath(
                        "//img[contains(@src, 'sparc') or contains(@alt, 'sparc') or contains(@alt, 'SPARC')] | " +
                                        "//img[contains(@src, 'logo')] | " +
                                        "//*[contains(@class, 'logo')]//img | " +
                                        "//div[contains(@class, 'sidenav-header')]//img");

        // Alternative: Brand/Company logo in header
        public static final By DASHBOARD_LOGO = By.xpath(
                        "//div[contains(@class, 'navbar') or contains(@class, 'header') or contains(@class, 'sidenav')]//img[1]");

        // Role Specific Elements (Copied from LoginPageLocators for cohesion)
        public static final By SUPER_ADMIN_ITEM = By.xpath("//span[normalize-space()='Customer Org']");
        public static final By ENTERPRISE_ITEM = By.xpath("//span[normalize-space()='Rate Card']");
        public static final By RESELLER_ITEM = By.xpath("//span[normalize-space()='User Management']");

        // Generic Sidebar Items
        public static final By DASHBOARD_LINK = By.xpath("//span[normalize-space()='Dashboard']"); // Usually common
        public static final By REPORTS_LINK = By.xpath("//span[contains(text(), 'Reports')]");

        // Service Links (Sidebar or Menu)
        public static final By SERVICE_SMS = By.xpath("//span[normalize-space()='SMS']");
        public static final By SERVICE_RCS = By.xpath("//span[normalize-space()='RCS']");
        public static final By SERVICE_OBD = By.xpath("//span[normalize-space()='OBD']"); // Or Voice
        public static final By SERVICE_IVR = By.xpath("//span[normalize-space()='IVR']");
        public static final By SERVICE_WHATSAPP = By
                        .xpath("//span[normalize-space()='WhatsApp' or normalize-space()='WABA']");
        public static final By SERVICE_CCS = By.xpath("//span[normalize-space()='CCS']");
        public static final By SERVICE_LIVE_AGENT = By
                        .xpath("//span[normalize-space()='Live Agent' or normalize-space()='LiveAgent' or normalize-space()='liveagent']");

        // Filter Section (Dashboard)
        // Updated to handle both text 'Filter' and common icons (like mat-icon
        // 'filter_list')
        public static final By FILTER_BUTTON = By.xpath(
                        "//button[contains(., 'Filter') or .//mat-icon[normalize-space()='filter_list'] or contains(@class, 'filter')]");

        // Calendar/Date Filter specific
        // Refined to avoid hitting refresh buttons. Looking for specific aria-label or
        // standard material icon names
        public static final By CALENDAR_ICON = By.xpath(
                        "//mat-icon[normalize-space()='calendar_today' or normalize-space()='date_range' or normalize-space()='event' or normalize-space()='filter_alt'] | "
                                        +
                                        "//button[contains(@aria-label, 'Calendar') or contains(@aria-label, 'Date') or contains(@aria-label, 'Filter')] | "
                                        +
                                        "//mat-date-range-input | " +
                                        "//*[contains(@class, 'mat-datepicker-toggle')]//button");

        // Locator for 'Last 30 Days' option in the picker
        public static final By LAST_30_DAYS_OPTION = By
                        .xpath("//span[contains(text(), 'Last 30 Days')] | //button[contains(., 'Last 30 Days')]");

        // Locator for a clickable date cell (generic checks for gridcell,
        // mat-calendar-body-cell, or typical datepicker day class)
        public static final By CALENDAR_DATE_CELL = By.xpath(
                        "(//td[@role='gridcell'] | //button[contains(@class, 'mat-calendar-body-cell') or contains(@class, 'day')])[1]");

        // Date range might be valid, but checking for 'range-picker' or
        // 'mat-date-range-input'
        public static final By DATE_RANGE_FILTER = By.xpath(
                        "//input[contains(@placeholder, 'Date') or contains(@class, 'date-picker') or contains(@class, 'mat-date-range')]");

        // Apply often has 'Search', 'Apply', or 'Submit'
        public static final By APPLY_FILTER_BUTTON = By
                        .xpath("//button[contains(., 'Apply') or contains(., 'Search') or contains(., 'Submit')]");

        // Clear often has 'Clear', 'Reset'
        public static final By CLEAR_FILTER_BUTTON = By.xpath("//button[contains(., 'Clear') or contains(., 'Reset')]");
}
