# RCS Automation Framework Documentation

> **Manager/Lead Summary:** For a high-level overview of coverage and ROI, please see [AUTOMATION_SUMMARY.md](AUTOMATION_SUMMARY.md).


## 📋 Project Overview

**Project Name:** RCS_Automation  
**Framework:** Selenium WebDriver + TestNG  
**Language:** Java 21  
**Build Tool:** Maven  
**Last Updated:** January 7, 2026

This is a comprehensive UI automation framework for testing the SmartPing RCS Platform. It covers both **Super Admin** and **Enterprise** user flows including dashboard, services, wallet, reports, team management, roles management, and API documentation modules.

---

## 🏗️ Architecture

```
RCS_Automation/
├── src/
│   ├── main/java/
│   │   ├── base/              # Base test configuration
│   │   ├── locators/          # Page element locators
│   │   ├── pages/             # Page Object Model classes
│   │   └── utils/             # Utility classes
│   └── test/java/
│       ├── helpers/           # Test helper classes (shared across roles)
│       └── tests/             # Test classes, one folder per user role
│           ├── common/        #   Tests covering all roles (LoginTest)
│           ├── enterprise/    #   Tests that log in as an Enterprise user
│           ├── reseller/      #   Tests that log in as a Reseller user
│           └── superadmin/    #   Tests that log in as Super Admin
├── pom.xml                    # Maven dependencies
├── testng.xml                 # Full suite (all roles)
├── enterprise.xml             # Enterprise suite (tests/enterprise + LoginTest)
├── reseller.xml               # Reseller suite (tests/reseller)
├── superadmin.xml             # Super Admin suite (tests/superadmin)
└── config.properties          # Application configuration
```

### Where to add a new test

1. Put the class in the folder of the user it **logs in as**: `tests/enterprise`, `tests/reseller` or `tests/superadmin`. Use `tests/common` only for tests that cover several roles.
2. Set the package to match the folder, e.g. `package tests.reseller;`.
3. Prefix the class name with the role, e.g. `ResellerDashboardWalletTest`.
4. Register it in `testng.xml` **and** in that role's suite file (`enterprise.xml`, `reseller.xml` or `superadmin.xml`).

The Extent report and the email summary group results by role automatically based on the folder.

### Running one role

```bash
mvn test -DsuiteXmlFile=reseller.xml        # or enterprise.xml / superadmin.xml / testng.xml
```
In Jenkins, choose the suite in the `TEST_TO_RUN` parameter.

---

## 📦 Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Selenium WebDriver | 4.27.0 | Browser automation |
| TestNG | 7.x | Test framework |
| WebDriverManager | Latest | Driver management |
| Maven | 3.x | Build tool |
| Java | 21 | Runtime |

---

## 🧪 Test Suites Overview

### Super Admin Test Suites (7 Classes)

| Test Class | Test Cases | Description |
|------------|------------|-------------|
| `LoginTest.java` | 6 | Login validation for all user roles |
| `SuperAdminDashboardTest.java` | 4 | Dashboard navigation and validation |
| `SuperAdminCustomerOrgTest.java` | 25+ | Customer organization management |
| `SuperAdminTeamManagementTest.java` | 15 | Team member CRUD operations |
| `SuperAdminRolesManagementTest.java` | 13 | Role permissions and management |
| `SuperAdminServiceNodeSSOTest.java` | 8 | Service Node SSO configuration |
| `SuperAdminAPIAndDocumentationTest.java` | 7 | API docs and Swagger validation |

### Enterprise Test Suites (10 Classes)

| Test Class | Test Cases | Description |
|------------|------------|-------------|
| `EnterpriseDashboardNavigationTest.java` | 3 | Dashboard navigation |
| `EnterpriseServicesTabTest.java` | 32+ | All services (SMS, RCS, WABA, IVR, OBD, CCS) |
| `EnterpriseWalletTabTest.java` | 12 | Wallet operations and transactions |
| `EnterpriseReportsTabTest.java` | 40+ | Report generation and filters |
| `EnterpriseRateCardTabTest.java` | 15 | Rate card viewing |
| `EnterpriseControlCenterTabTest.java` | 13 | Team management in control center |
| `EnterpriseRolesManagementTest.java` | 14 | Roles and permissions |
| `EnterpriseAPIDocumentationTest.java` | 9 | API documentation validation |
| `MediaLibraryTest.java` | 2 | Media library operations |
| `AssistantListTestCA.java` | 7 | Assistant/chatbot management |

---

## 📁 Page Object Model (POM) Structure

### Page Objects (18 Classes)

| Page Class | Module | Key Methods |
|------------|--------|-------------|
| `LoginPage.java` | Authentication | `loginWithSuperAdminCredentials()`, `loginWithEnterpriseMaxServices()`, `logout()` |
| `DashboardPage.java` | Dashboard | `navigateToService()`, `getWalletBalance()` |
| `CustomerOrgPage.java` | Customer Org | `filterByType()`, `drillDownToCustomer()` |
| `TeamManagementPage.java` | Team Mgmt | `addNewMember()`, `editMember()`, `search()` |
| `RolesManagementPage.java` | Roles | `validatePermissions()`, `clickAddNew()` |
| `ServicesPage.java` | Services | `viewServiceDetails()`, `clickSSO()` |
| `EnterpriseWalletPage.java` | Wallet | `navigateToWallet()`, `clickRefill()` |
| `EnterpriseReportsPage.java` | Reports | `generateReport()`, `downloadReport()` |
| `EnterpriseRateCardPage.java` | Rate Card | `viewRateCard()`, `downloadRateCard()` |
| `EnterpriseControlCenterPage.java` | Control Center | `navigateToControlCenter()`, `clickTeamManagementTab()` |
| `EnterpriseRolesManagementPage.java` | Roles | `clickRolesManagementTab()`, `validateAccordions()` |
| `APIAndDocumentationPage.java` | API Docs | `clickExploreSwaggerUI()`, `downloadAPIDocumentation()` |
| `ServiceNodeSSOPage.java` | SSO | `validateSSOConfiguration()` |

### Locators (18 Files)

All locators use XPath with fallback patterns for robustness.

---

## 🔧 Configuration

### config.properties
```properties
url=https://stagingvault.smartping.io
browser=chrome
headless=false
implicitWait=10
explicitWait=15
```

### User Credentials
Managed via `UserCredentialProvider.java`:
- Super Admin credentials
- Enterprise credentials  
- Reseller credentials

---

## 🚀 How to Run Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn test -Dtest=EnterpriseServicesTabTest
mvn test -Dtest=SuperAdminRolesManagementTest
```

### Run Multiple Test Classes
```bash
mvn test -Dtest=LoginTest,SuperAdminDashboardTest
```

### Run with TestNG XML
```bash
mvn test -DsuiteXmlFile=testng.xml
```

---

## 📊 Test Execution Flow

### Super Admin Flow
```
Login → Dashboard → Customer Org → Team Management → Roles → API Docs → Logout
```

### Enterprise Flow
```
Login → Select Wallet → Dashboard → Services → Wallet → Reports → Rate Card → Control Center → Logout
```

---

## 🔑 Key Features

1. **Page Object Model (POM)**: Clean separation of test logic and page interactions
2. **Session Retention**: Tests maintain login session using `retainSession = true`
3. **Robust Locators**: Multiple fallback XPath patterns for each element
4. **JavaScript Fallback**: Uses JS click when standard click fails
5. **Explicit Waits**: WebDriverWait for reliable element interactions
6. **Comprehensive Logging**: Console output for debugging
7. **Modular Helpers**: Reusable helper classes for common validations

---

## 📈 Test Coverage Summary

| Module | Super Admin | Enterprise |
|--------|-------------|------------|
| Login | ✅ | ✅ |
| Dashboard | ✅ | ✅ |
| Services | - | ✅ (6 services) |
| Wallet | - | ✅ |
| Reports | - | ✅ |
| Rate Card | - | ✅ |
| Team Management | ✅ | ✅ |
| Roles Management | ✅ | ✅ |
| Customer Org | ✅ | - |
| API Documentation | ✅ | ✅ |
| Service Node SSO | ✅ | - |

---

## 🛠️ Utility Classes

| Class | Purpose |
|-------|---------|
| `ConfigReader.java` | Reads configuration properties |
| `EmailReader.java` | Email/OTP verification |
| `ExtentReportManager.java` | HTML report generation |
| `ScreenshotUtil.java` | Screenshot capture on failure |
| `TestUtil.java` | Common test utilities |
| `UserCredentialProvider.java` | Credential management |

---

## 📝 Test Naming Convention

- **Super Admin tests**: `SuperAdmin[Module]Test.java`
- **Enterprise tests**: `Enterprise[Module]Test.java`
- **Test methods**: `test[Action][Target]()`

---

## 🐛 Troubleshooting

### CDP Warning
```
WARNING: Unable to find CDP implementation matching 143
```
**Solution**: This is a harmless warning. Tests will continue to run.

### Element Not Found
- Check if page has fully loaded
- Verify XPath locator matches current UI
- Add explicit wait before interaction

### Session Issues
- Ensure `retainSession = true` in `@BeforeClass`
- Check if previous test failed and left browser in bad state

---

## 👥 Contact

**QA Team**: SmartPing QA  
**Project**: RCS Automation Framework

---

*Documentation generated on January 7, 2026*
