# Service Node SSO Test Suite

## Overview
This test suite validates the Service Node SSO (Single Sign-On) functionality for Super Admin users. It automatically discovers all available services, tests SSO login with all available roles for each service, and validates proper redirection.

## Files Created

### 1. **ServiceNodeSSOPageLocators.java**
- **Location:** `src/main/java/locators/ServiceNodeSSOPageLocators.java`
- **Purpose:** Contains all XPath and CSS selectors for Service Node SSO page elements
- **Key Locators:**
  - Navigation menu
  - Service list/table
  - Login buttons
  - Role selection modal
  - Validation elements

### 2. **ServiceNodeSSOPage.java**
- **Location:** `src/main/java/pages/ServiceNodeSSOPage.java`
- **Purpose:** Page Object Model for Service Node SSO functionality
- **Key Methods:**
  - `navigateToServiceNodeSSO()` - Navigate to SSO page
  - `getAvailableServices()` - Discover all services
  - `clickLoginForService(serviceName)` - Click login for specific service
  - `getAvailableRoles()` - Get roles for current service
  - `performSSOLogin(serviceName, roleName)` - Complete SSO login flow
  - `isRedirectedToService()` - Validate successful redirection

### 3. **SuperAdminServiceNodeSSOTest.java**
- **Location:** `src/test/java/tests/SuperAdminServiceNodeSSOTest.java`
- **Purpose:** TestNG test class for SSO validation
- **Test Flow:**
  1. Login as Super Admin
  2. Navigate to Service Node SSO
  3. Discover all available services
  4. For each service, test SSO with all roles
  5. Validate redirections
  6. Generate test summary

## Test Execution Flow

```
1. testLoginAndNavigateToSSO (Priority 1)
   └─→ Login as Super Admin
   └─→ Navigate to Service Node SSO page
   
2. testDiscoverAvailableServices (Priority 2)
   └─→ Get list of all available services
   └─→ Validate at least one service exists
   
3. testSSOLoginForAllServicesAndRoles (Priority 3)
   └─→ For each service:
       └─→ Click Login button
       └─→ Get available roles
       └─→ For each role:
           └─→ Select role
           └─→ Submit
           └─→ Validate redirection
           └─→ Navigate back to SSO page
       └─→ Print test summary
       
99. testLogout (Priority 99)
    └─→ Logout from Super Admin session
```

## How to Run

### Run All SSO Tests
```bash
mvn test -Dtest=SuperAdminServiceNodeSSOTest
```

### Run Specific Test
```bash
mvn test -Dtest=SuperAdminServiceNodeSSOTest#testSSOLoginForAllServicesAndRoles
```

### Run in TestNG XML
Add to your `testng.xml`:
```xml
<test name="Service Node SSO Tests">
    <classes>
        <class name="tests.SuperAdminServiceNodeSSOTest"/>
    </classes>
</test>
```

## Locator Customization

**IMPORTANT:** The locators are generic and may need adjustment based on your actual UI.

### How to Update Locators:

1. **Open the SSO page in browser**
2. **Inspect elements** using Chrome DevTools (F12)
3. **Update locators in** `ServiceNodeSSOPageLocators.java`

#### Example: Updating Service Login Button
```java
// Current (generic)
public static final By SERVICE_LOGIN_BUTTON = By.xpath(
    "//button[contains(., 'Login')]");

// If your button has specific class:
public static final By SERVICE_LOGIN_BUTTON = By.cssSelector(
    "button.btn-sso-login");

// If your button has specific ID:
public static final By SERVICE_LOGIN_BUTTON = By.id("ssoLoginBtn");
```

## Common Customization Scenarios

### Scenario 1: Services displayed as cards (not table)
In `ServiceNodeSSOPage.java`, modify `getAvailableServices()`:
```java
// Change from table cells to card elements
List<WebElement> serviceElements = driver.findElements(
    By.xpath("//div[@class='service-card']//h3")); // Adjust selector
```

### Scenario 2: Role selection uses radio buttons
Already handled in `selectRole()` method with fallback logic.

### Scenario 3: SSO opens in new window/tab
Use these methods from `ServiceNodeSSOPage.java`:
```java
ssoPage.switchToNewWindow();
// ... validate redirection
ssoPage.closeCurrentAndSwitchToMain();
```

## Test Reports

### Console Output Format
```
========================================
SERVICE NODE SSO TEST SUITE - STARTING
========================================

Step 1: Logging in as Super Admin...
✓ Dashboard loaded

Step 2: Navigating to Service Node SSO...
✓ Successfully navigated to Service Node SSO page

========================================
DISCOVERING AVAILABLE SERVICES
========================================
✓ Found 3 service(s): [RCS, Chatbot, Analytics]

========================================
TESTING SSO LOGIN FOR ALL SERVICES
========================================

----------------------------------------
SERVICE: RCS
----------------------------------------
✓ Role selection modal appeared
Available Roles: [Admin, User, Viewer]

  → Testing Role: Admin (1/3)
  ✓ PASS: Successfully redirected for role 'Admin'
    Current URL: https://.../rcs/dashboard

  → Testing Role: User (2/3)
  ✓ PASS: Successfully redirected for role 'User'
    Current URL: https://.../rcs/dashboard
    
========================================
SSO TEST SUMMARY
========================================
Total SSO Tests:    9
Passed:            9
Failed:            0
Success Rate:      100%
========================================
```

## Troubleshooting

### Issue: Services not discovered
**Solution:** Update `SERVICE_NAME_CELLS` locator in `ServiceNodeSSOPageLocators.java`
```java
public static final By SERVICE_NAME_CELLS = By.xpath(
    "//YOUR_ACTUAL_XPATH_HERE");
```

### Issue: Role modal not appearing
**Solution:** Check if roles are loaded directly without modal
- Inspect the page after clicking Login
- Update `isRoleSelectionModalDisplayed()` logic

### Issue: Redirection validation fails
**Solution:** Update validation logic in `isRedirectedToService()`
- Add service-specific URL patterns
- Add service-specific page title checks
- Add service-specific unique element checks

### Issue: Login button not clickable
**Solution:** Element might be obscured
- Already handled with scroll into view
- Check if button requires hover first
- Add hover action before click if needed

## Advanced Configuration

### Enable/Disable Specific Tests
In `SuperAdminServiceNodeSSOTest.java`, toggle `enabled` flag:
```java
@Test(priority = 4, enabled = true)  // Set to false to skip
public void testSSOForRCSService() {
    // ...
}
```

### Add Custom Service-Specific Tests
```java
@Test(priority = 10, description = "Test RCS specific functionality")
public void testRCSSpecificFeature() {
    ssoPage.performSSOLogin("RCS", "Admin");
    // Add RCS-specific validations
}
```

### Add Wait Time Configuration
Adjust waits in page object if needed:
```java
// In ServiceNodeSSOPage constructor
this.wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Increase if slow
```

## Best Practices

1. **Run tests in a stable environment** - SSO might fail in unstable networks
2. **Update locators regularly** - UI changes require locator updates
3. **Monitor test execution** - Check console output for failures
4. **Keep role data updated** - If new roles added, tests auto-discover them
5. **Handle new windows properly** - Some services may open in new tabs

## Future Enhancements

Potential improvements you can add:

1. **Data-driven testing** - Read service/role combinations from Excel/CSV
2. **Screenshot capture** - Capture screenshots on failure
3. **Detailed reporting** - Generate HTML reports with ExtentReports
4. **Parallel execution** - Test multiple services in parallel
5. **Role permission validation** - Validate each role has correct permissions

## Support

For issues or questions:
1. Check console output for detailed error messages
2. Verify locators match your UI
3. Ensure Super Admin account has SSO access
4. Check network connectivity for SSO redirections

---

**Created:** 2026-01-03  
**Version:** 1.0  
**Author:** Automation Team  
**Last Updated:** 2026-01-03
