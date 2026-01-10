# Test Case Inventory - RCS Automation Framework

## 📊 Complete Test Case Breakdown

**Total Test Classes:** 17  
**Estimated Total Test Cases:** 200+

---

## 🔐 Login Module

### LoginTest.java (6 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testSuperAdminLogin | 1 | Login with Super Admin credentials |
| 2 | testSuperAdminLogout | 2 | Logout from Super Admin |
| 3 | testEnterpriseLogin | 3 | Login with Enterprise credentials |
| 4 | testEnterpriseLogout | 4 | Logout from Enterprise |
| 5 | testInvalidCredentials | 5 | Login with invalid credentials |
| 6 | testBlankPassword | 6 | Login with blank password |

---

## 👨‍💼 Super Admin Test Suites

### SuperAdminDashboardTest.java (4 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndDashboardLoad | 1 | Login and verify dashboard loads |
| 2 | testDashboardStats | 2 | Verify dashboard statistics |
| 3 | testNavigationLinks | 3 | Test sidebar navigation |
| 4 | testLogout | 4 | Logout verification |

### SuperAdminCustomerOrgTest.java (25+ Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndNavigateToCustomerOrg | 1 | Navigate to Customer Org |
| 2 | testTableHeaders | 2 | Validate table headers |
| 3 | testPaginationNext | 3 | Next button in pagination |
| 4 | testPaginationPrevious | 4 | Previous button |
| 5 | testFilterByEnterprise | 5 | Filter by Enterprise type |
| 6 | testEnterpriseDrillDown | 6 | Drill into Enterprise details |
| 7 | testOrganizationDetailsTab | 7 | Org details tab validation |
| 8 | testWalletTab | 8 | Wallet tab operations |
| 9 | testRefillWallet | 9 | Wallet refill functionality |
| 10 | testDeductWallet | 10 | Wallet deduction |
| 11 | testSearchWallet | 11 | Search in wallet |
| 12 | testRedirectToServices | 12 | Redirect to services |
| 13 | testProfileTab | 13 | Profile tab validation |
| 14 | testRolesTab | 14 | Roles tab validation |
| 15+ | ... | ... | Additional profile/roles validations |

### SuperAdminTeamManagementTest.java (15 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndNavigate | 1 | Login and navigate to Team Mgmt |
| 2 | testTableHeaders | 2 | Validate team list headers |
| 3 | testAddNewButton | 3 | Add New button clickability |
| 4 | testAddNewFormFields | 4 | Validate form fields |
| 5 | testSearchFunctionality | 5 | Search team members |
| 6 | testPaginationNext | 6 | Pagination next |
| 7 | testPaginationPrevious | 7 | Pagination previous |
| 8 | testEditButton | 8 | Edit button validation |
| 9 | testViewButton | 9 | View button validation |
| 10 | testDeleteButton | 10 | Delete button validation |
| 11 | testRoleFilter | 11 | Filter by role |
| 12 | testStatusFilter | 12 | Filter by status |
| 13 | testEmailValidation | 13 | Email field validation |
| 14 | testPhoneValidation | 14 | Phone field validation |
| 15 | testLogout | 15 | Logout |

### SuperAdminRolesManagementTest.java (13 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndNavigate | 1 | Login and navigate to Roles |
| 2 | testTableHeaders | 2 | Validate table headers |
| 3 | testPaginationNext | 3 | Next button |
| 4 | testPaginationPrevious | 4 | Previous button |
| 5 | testEditButtonClickable | 5 | Edit button validation |
| 6 | testViewButtonClickable | 6 | View button validation |
| 7 | testSearchFunctionality | 7 | Search roles |
| 8 | testAddNewButtonClickable | 8 | Add New button |
| 9 | testAddNewFormFields | 9 | Validate permission accordions |
| 10 | testServiceNodeSSODropdown | 10 | SSO dropdown expansion |
| 11 | testNoDuplicateValues | 11 | Check for duplicates |
| 12 | testUsersCountNavigation | 12 | Users count click navigation |
| 13 | testLogout | 13 | Logout |

### SuperAdminServiceNodeSSOTest.java (8 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndNavigate | 1 | Login and navigate to SSO |
| 2 | testSSOConfigurationDisplay | 2 | SSO config visible |
| 3 | testSMSServiceSSO | 3 | SMS service SSO |
| 4 | testOBDServiceSSO | 4 | OBD service SSO |
| 5 | testCCSServiceSSO | 5 | CCS service SSO |
| 6 | testWABAServiceSSO | 6 | WABA service SSO |
| 7 | testSSOValidation | 7 | SSO validation rules |
| 8 | testLogout | 8 | Logout |

### SuperAdminAPIAndDocumentationTest.java (7 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndNavigateToAPIDoc | 1 | Login and navigate |
| 2 | testAPIDocTabVisibility | 2 | Tab visibility |
| 3 | testSMSServiceDocumentation | 3 | SMS API docs + Swagger |
| 4 | testOBDServiceDocumentation | 4 | OBD API docs + Swagger |
| 5 | testCCSServiceDocumentation | 5 | CCS API docs + Swagger |
| 6 | testWABAServiceDocumentation | 6 | WABA API docs + Swagger |
| 7 | testLogout | 7 | Logout |

---

## 🏢 Enterprise Test Suites

### EnterpriseDashboardNavigationTest.java (3 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginWithMaxServicesWallet | 1 | Login and select wallet |
| 2 | testDashboardNavigation | 2 | Dashboard navigation |
| 3 | testLogout | 3 | Logout |

### EnterpriseServicesTabTest.java (32+ Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndDashboardNavigation | 1 | Login with max services |
| 2 | testNavigateToSMSService | 2 | Navigate to SMS |
| 3 | testSMSViewDetails | 3 | SMS view details |
| 4 | testSMSClickSSO | 4 | SMS SSO button |
| 5 | testSMSSearch | 5 | SMS search |
| 6 | testSMSActiveFilter | 6 | SMS active filter |
| 7-12 | testNavigateToRCSService... | 7-12 | RCS service tests |
| 13-18 | testNavigateToWABAService... | 13-18 | WABA service tests |
| 19-24 | testNavigateToIVRService... | 19-24 | IVR service tests |
| 25-30 | testNavigateToOBDService... | 25-30 | OBD service tests |
| 31+ | testNavigateToCCSService... | 31+ | CCS service tests |

### EnterpriseWalletTabTest.java (12 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndNavigateToWallet | 1 | Navigate to Wallet |
| 2 | testWalletTableHeaders | 2 | Validate headers |
| 3 | testPaginationNext | 3 | Next button |
| 4 | testPaginationPrevious | 4 | Previous button |
| 5 | testRefillButton | 5 | Refill button validation |
| 6 | testRefillPopup | 6 | Refill popup fields |
| 7 | testDeductButton | 7 | Deduct button |
| 8 | testSearchFunctionality | 8 | Search transactions |
| 9 | testDateFilter | 9 | Date range filter |
| 10 | testGenerateReport | 10 | Generate report |
| 11 | testGenerateReportCancel | 11 | Cancel report popup |
| 12 | testLogout | 12 | Logout |

### EnterpriseReportsTabTest.java (40+ Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndNavigate | 1 | Navigate to Reports |
| 2 | testReportsTableHeaders | 2 | Validate headers |
| 3 | testSMSReportGeneration | 3 | Generate SMS report |
| 4 | testSMSReportDownload | 4 | Download SMS report |
| 5 | testOBDReportGeneration | 5 | OBD report |
| 6+ | ... | ... | Multiple report types and filters |

### EnterpriseRateCardTabTest.java (15 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndNavigate | 1 | Navigate to Rate Card |
| 2 | testRateCardTableHeaders | 2 | Validate headers |
| 3 | testSMSRateCard | 3 | SMS rate card |
| 4 | testOBDRateCard | 4 | OBD rate card |
| 5 | testDownloadRateCard | 5 | Download functionality |
| 6+ | ... | ... | Additional rate card tests |

### EnterpriseControlCenterTabTest.java (13 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndControlCenterNavigation | 1 | Navigate to Control Center |
| 2 | testTeamManagementTabNavigation | 2 | Team Management tab |
| 3 | testTableHeaders | 3 | Validate headers |
| 4 | testPaginationNext | 4 | Next button |
| 5 | testPaginationPrevious | 5 | Previous button |
| 6 | testEditButtonClickable | 6 | Edit button |
| 7 | testViewButtonClickable | 7 | View button |
| 8 | testSearchFunctionality | 8 | Search team members |
| 9 | testFilterByRole | 9 | Filter by role |
| 10 | testAddNewButtonClickable | 10 | Add New button |
| 11+ | ... | ... | Additional team tests |

### EnterpriseRolesManagementTest.java (14 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndControlCenterNavigation | 1 | Login and navigate |
| 2 | testRolesManagementTabNavigation | 2 | Roles Management tab |
| 3 | testTableHeaders | 3 | Validate headers |
| 4 | testPaginationNext | 4 | Next button |
| 5 | testPaginationPrevious | 5 | Previous button |
| 6 | testEditButtonClickable | 6 | Edit button |
| 7 | testViewButtonClickable | 7 | View button |
| 8 | testSearchFunctionality | 8 | Search roles |
| 9 | testAddNewButtonClickable | 9 | Add New button |
| 10 | testAddNewFormFields | 10 | Permission accordions |
| 11 | testDashboardAccordionExpansion | 11 | Dashboard accordion |
| 12 | testNoDuplicateValues | 12 | Check for duplicates |
| 13 | testUsersCountNavigation | 13 | Users count navigation |
| 14 | testLogout | 14 | Logout |

### EnterpriseAPIDocumentationTest.java (9 Test Cases)

| # | Test Case | Priority | Description |
|---|-----------|----------|-------------|
| 1 | testLoginAndNavigateToAPIDoc | 1 | Login and navigate |
| 2 | testAPIDocTabVisibility | 2 | Tab visibility |
| 3 | testSMSServiceDocumentation | 3 | SMS docs + Swagger |
| 4 | testOBDServiceDocumentation | 4 | OBD docs + Swagger |
| 5 | testCCSServiceDocumentation | 5 | CCS docs + Swagger |
| 6 | testWABAServiceDocumentation | 6 | WABA docs + Swagger |
| 7 | testRCSServiceDocumentation | 7 | RCS docs + Swagger |
| 8 | testIVRServiceDocumentation | 8 | IVR docs + Swagger |
| 9 | testLogout | 9 | Logout |

---

## ✅ Test Execution Priority

### Critical Path (Must Pass)
1. Login Tests
2. Dashboard Navigation
3. Service Navigation
4. Basic CRUD Operations

### High Priority
1. Wallet Operations
2. Report Generation
3. Team/Roles Management

### Medium Priority
1. Search/Filter
2. Pagination
3. API Documentation

---

## 📋 Test Tags

| Tag | Description |
|-----|-------------|
| `@critical` | Must-pass tests |
| `@smoke` | Quick validation |
| `@regression` | Full test suite |
| `@enterprise` | Enterprise user tests |
| `@superadmin` | Super Admin tests |

---

*Generated on January 7, 2026*
