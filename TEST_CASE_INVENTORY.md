# Test Case Inventory

Generated from the `@Test` annotations in `src/test/java/tests` on 2026-10-23. Regenerate it when tests are added or removed.

**Total:** 305 test cases in 22 test classes.

| Role | Test class | Test cases |
|---|---|---:|
| Common (all roles) | `LoginTest` | 6 |
| Enterprise | `AssistantListTestCA` | 5 |
| Enterprise | `EnterpriseAPIDocumentationTest` | 9 |
| Enterprise | `EnterpriseControlCenterTabTest` | 11 |
| Enterprise | `EnterpriseDashboardNavigationTest` | 9 |
| Enterprise | `EnterpriseRCSAssistantFlowTest` | 18 |
| Enterprise | `EnterpriseRateCardTabTest` | 9 |
| Enterprise | `EnterpriseReportsTabTest` | 44 |
| Enterprise | `EnterpriseRolesManagementTest` | 14 |
| Enterprise | `EnterpriseServicesTabTest` | 31 |
| Enterprise | `EnterpriseWalletTabTest` | 12 |
| Enterprise | `MediaLibraryTest` | 2 |
| Reseller | `ResellerDashboardAnalyticsTest` | 6 |
| Reseller | `ResellerDashboardMyWalletsTest` | 14 |
| Reseller | `ResellerDashboardUserManagementTest` | 31 |
| Reseller | `ResellerDashboardUsersTest` | 11 |
| Super Admin | `SuperAdminAPIAndDocumentationTest` | 7 |
| Super Admin | `SuperAdminCustomerOrgTest` | 25 |
| Super Admin | `SuperAdminDashboardTest` | 9 |
| Super Admin | `SuperAdminRolesManagementTest` | 13 |
| Super Admin | `SuperAdminServiceNodeSSOTest` | 9 |
| Super Admin | `SuperAdminTeamManagementTest` | 10 |

## Common (all roles) — `tests/common`

### LoginTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testSuperAdminLogin` | Login with SuperAdmin |
| 2 | `testEnterpriseLogin` | Login with Enterprise User |
| 3 | `testResellerLogin` | Login with Reseller User |
| 4 | `testLoginWithInvalidCredentials` | Login with Invalid Credentials |
| 5 | `testLoginWithBlankPassword` | Validate Error: Username present, Password blank |
| 6 | `testLoginWithBlankUsername` | Validate Error: Username blank, Password present |

## Enterprise — `tests/enterprise`

### AssistantListTestCA

| # | Test method | Description |
|---:|---|---|
| 1 | `verifyAssistantHeadersOrNoData` | — |
| 2 | `verifyAssistantSearchFunctionality` | — |
| 3 | `verifyEditAssistantFunctionality` | — |
| 4 | `verifyAssistantFilterWithCalendarAndStatus` | — |
| 5 | `verifyAddNewAssistantFunctionality` | — |

### EnterpriseAPIDocumentationTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndNavigateToAPIDoc` | Login as Enterprise user and navigate to API & Documentation |
| 2 | `testAPIDocTabVisibilityAndClickability` | Verify API & Documentation tab visibility and clickability |
| 3 | `testSMSServiceDocumentation` | Validate SMS service documentation and Swagger UI |
| 4 | `testOBDServiceDocumentation` | Validate OBD service documentation and Swagger UI |
| 5 | `testCCSServiceDocumentation` | Validate CCS service documentation and Swagger UI |
| 6 | `testWABAServiceDocumentation` | Validate WABA service documentation and Swagger UI |
| 7 | `testRCSServiceDocumentation` | Validate RCS service documentation if available |
| 8 | `testIVRServiceDocumentation` | Validate IVR service documentation if available |
| 9 | `testLogout` | Logout from Enterprise account |

### EnterpriseControlCenterTabTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndControlCenterNavigation` | Login as Enterprise user and navigate to Control Center |
| 2 | `testTeamManagementTabNavigation` | Navigate to Team Management tab from Control Center |
| 3 | `testTableHeaders` | Validate Team Management table headers |
| 4 | `testPaginationNext` | Test pagination Next button |
| 5 | `testPaginationPrevious` | Test pagination Previous button |
| 6 | `testEditButtonClickable` | Verify Edit button is clickable |
| 7 | `testViewButtonClickable` | Verify View button is clickable |
| 8 | `testSearchFunctionality` | Test search functionality with 3 characters from table data |
| 9 | `testFilterByRole` | Test filter by role from dropdown |
| 10 | `testAddNewButtonClickable` | Verify Add New button is clickable |
| 11 | `testLogout` | Logout from Enterprise account |

### EnterpriseDashboardNavigationTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testEnterpriseLoginAndWalletSelection` | Login with Enterprise User and Select Max Services Wallet |
| 2 | `testRCSService` | Test RCS service navigation |
| 3 | `testCCSService` | Test CCS service navigation |
| 4 | `testSMSService` | Test SMS service navigation |
| 5 | `testIVRService` | Test IVR service navigation |
| 6 | `testOBDService` | Test OBD service navigation |
| 7 | `testWABAService` | Test WABA service navigation |
| 8 | `testLiveAgentService` | Test Live Agent service navigation |
| 9 | `testLogout` | Logout from Enterprise |

### EnterpriseRCSAssistantFlowTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAsEnterprise` | Login as Enterprise user with max service accounts |
| 2 | `testNavigateToServices` | Navigate to Services tab |
| 3 | `testOpenRCSService` | Open RCS Service details |
| 4 | `testIdentifyActiveServiceAccount` | Identify Active Service Account |
| 5 | `testPerformSSO` | Perform SSO to RCS Portal |
| 6 | `testValidateRCSPortalRedirection` | Validate RCS Portal redirection |
| 7 | `testManageAssistantsNavigation` | Navigate to Manage Assistants |
| 8 | `testAddNewAssistantBasicDetails` | Add New Assistant - Fill Basic Details |
| 9 | `testBrandingSection` | Upload Branding Images |
| 10 | `testContactSection` | Fill Contact Details and Submit |
| 11 | `testPostCreationStatusValidation` | Validate Pending Status after creation |
| 12 | `testSearchFunctionality` | Search for Assistant |
| 13 | `testDownloadAssistantList` | Download Assistant List |
| 14 | `testFilterPendingStatus` | Filter by Pending Status |
| 15 | `testViewAssistantDetails` | View Assistant Details |
| 16 | `testEditAssistant` | Edit Assistant |
| 17 | `testPostUpdateValidation` | Validate Post-Update Changes |
| 18 | `testLogoutFromRCSPortal` | Logout from RCS SSO workspace |

### EnterpriseRateCardTabTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndRateCardNavigation` | Login as Enterprise user, navigate to Dashboard and redirect to Rate Card Tab |
| 2 | `testRateCardListHeaderValidation` | Validate rate card list column headers are displayed correctly |
| 3 | `testPaginationFunctionality` | Validate pagination functionality - Next and Previous buttons |
| 4 | `testDuplicateSerialNumberValidation` | Validate no duplicate serial numbers across all pagination pages |
| 5 | `testLatestRateCardOrdering` | Validate latest rate card entry is displayed at top of list |
| 6 | `testActiveRateCardViewIcon` | Click on View icon for Active status rate card |
| 7 | `testRateCardDetailsServicesDisplay` | All wallet services visible in details |
| 8 | `testEditFunctionalityRestriction` | Validate no Edit functionality available for Rate Cards |
| 9 | `testLogout` | Logout from Enterprise account |

### EnterpriseReportsTabTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndReportsNavigation` | Login as Enterprise user, navigate to Dashboard and redirect to Reports Tab |
| 2 | `testSMSViewDetailsNavigation` | Validate View Details button for SMS is clickable and redirects correctly |
| 3 | `testSMSReportsListHeaderValidation` | Validate SMS Reports list column headers are displayed correctly |
| 4 | `testSearchFunctionality` | Enter first 3 characters of top record and validate search results |
| 5 | `testActiveStatusFilter` | Apply Active filter and validate filtered results |
| 6 | `testRedirectToServiceNode` | Click redirect icon for non-Failed record and validate service account name |
| 7 | `testReportsTabSelection` | Validate Reports tab is selected after redirection, not any other tab |
| 8 | `testServiceAccountNameValidation` | Validate Service Account Name matches on Dashboard after SSO redirect |
| 9 | `testRCSViewDetailsNavigation` | Navigate to RCS Reports View Details |
| 10 | `testRCSReportsListHeaderValidation` | Validate RCS Reports list headers |
| 11 | `testRCSSearchFunctionality` | Validate RCS Search Functionality |
| 12 | `testRCSActiveStatusFilter` | Validate RCS Active Status Filter |
| 13 | `testRCSRedirectToServiceNode` | Validate RCS Redirect to Service Node |
| 14 | `testRCSReportsTabSelection` | Validate RCS Reports Tab Selection |
| 15 | `testRCSServiceAccountNameValidation` | Validate RCS Service Account Name |
| 16 | `testWABAViewDetailsNavigation` | Navigate to WABA Reports View Details |
| 17 | `testWABAReportsListHeaderValidation` | Validate WABA Reports list headers |
| 18 | `testWABASearchFunctionality` | Validate WABA Search Functionality |
| 19 | `testWABAActiveStatusFilter` | Validate WABA Active Status Filter |
| 20 | `testWABARedirectToServiceNode` | Validate WABA Redirect to Service Node |
| 21 | `testWABAReportsTabSelection` | Validate WABA Reports Tab Selection |
| 22 | `testWABAServiceAccountNameValidation` | Validate WABA Service Account Name |
| 23 | `testIVRViewDetailsNavigation` | Navigate to IVR Reports View Details |
| 24 | `testIVRReportsListHeaderValidation` | Validate IVR Reports list headers |
| 25 | `testIVRSearchFunctionality` | Validate IVR Search Functionality |
| 26 | `testIVRActiveStatusFilter` | Validate IVR Active Status Filter |
| 27 | `testIVRRedirectToServiceNode` | Validate IVR Redirect to Service Node |
| 28 | `testIVRReportsTabSelection` | Validate IVR Reports Tab Selection |
| 29 | `testIVRServiceAccountNameValidation` | Validate IVR Service Account Name |
| 30 | `testOBDViewDetailsNavigation` | Navigate to OBD Reports View Details |
| 31 | `testOBDReportsListHeaderValidation` | Validate OBD Reports list headers |
| 32 | `testOBDSearchFunctionality` | Validate OBD Search Functionality |
| 33 | `testOBDActiveStatusFilter` | Validate OBD Active Status Filter |
| 34 | `testOBDRedirectToServiceNode` | Validate OBD Redirect to Service Node |
| 35 | `testOBDReportsTabSelection` | Validate OBD Reports Tab Selection |
| 36 | `testOBDServiceAccountNameValidation` | Validate OBD Service Account Name |
| 37 | `testCCSViewDetailsNavigation` | Navigate to CCS Reports View Details |
| 38 | `testCCSReportsListHeaderValidation` | Validate CCS Reports list headers |
| 39 | `testCCSSearchFunctionality` | Validate CCS Search Functionality |
| 40 | `testCCSActiveStatusFilter` | Validate CCS Active Status Filter |
| 41 | `testCCSRedirectToServiceNode` | Validate CCS Redirect to Service Node |
| 42 | `testCCSReportsTabSelection` | Validate CCS Reports Tab Selection |
| 43 | `testCCSServiceAccountNameValidation` | Validate CCS Service Account Name |
| 44 | `testLogout` | Logout from Enterprise account |

### EnterpriseRolesManagementTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndControlCenterNavigation` | Login as Enterprise user, click max service wallet, navigate to Control Center |
| 2 | `testRolesManagementTabNavigation` | Navigate to Roles Management tab from Control Center |
| 3 | `testTableHeaders` | Validate Roles Management table headers |
| 4 | `testPaginationNext` | Validate pagination Next button |
| 5 | `testPaginationPrevious` | Validate pagination Previous button |
| 6 | `testEditButtonClickable` | Validate Edit button is clickable |
| 7 | `testViewButtonClickable` | Validate View button is clickable |
| 8 | `testSearchFunctionality` | Validate Search functionality with first 3 characters |
| 9 | `testAddNewButtonClickable` | Validate Add New button is clickable |
| 10 | `testAddNewFormFields` | Validate permission fields in Add New form |
| 11 | `testDashboardAccordionExpansion` | Validate Dashboard accordion expansion |
| 12 | `testNoDuplicateValues` | Validate no duplicate values in form |
| 13 | `testUsersCountNavigation` | Validate Users count navigation to Team Management |
| 14 | `testLogout` | Logout from Enterprise account |

### EnterpriseServicesTabTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndNavigation` | Login as Enterprise and Navigate to Services |
| 2 | `testSMSNavigateToDetails` | Navigate to SMS Service Details |
| 3 | `testSMSSearchPositive` | Validate SMS Search Functionality - Positive Flow |
| 4 | `testSMSFilterByStatus` | Validate SMS Filter Functionality - Status Based |
| 5 | `testSMSViewDetailsValidation` | Validate SMS View Details - Excluding Failed Accounts |
| 6 | `testSMSSSORedirectionAndLogout` | Validate SMS SSO Redirection and Logout |
| 7 | `testRCSNavigateToDetails` | Navigate to RCS Service Details |
| 8 | `testRCSSearchPositive` | Validate RCS Search Functionality - Positive Flow |
| 9 | `testRCSFilterByStatus` | Validate RCS Filter Functionality - Status Based |
| 10 | `testRCSViewDetailsValidation` | Validate RCS View Details - Excluding Failed Accounts |
| 11 | `testRCSSSORedirectionAndLogout` | Validate RCS SSO Redirection and Logout |
| 12 | `testWABANavigateToDetails` | Navigate to WABA Service Details |
| 13 | `testWABASearchPositive` | Validate WABA Search Functionality - Positive Flow |
| 14 | `testWABAFilterByStatus` | Validate WABA Filter Functionality - Status Based |
| 15 | `testWABAViewDetailsValidation` | Validate WABA View Details - Excluding Failed Accounts |
| 16 | `testWABASSORedirectionAndLogout` | Validate WABA SSO Redirection and Logout |
| 17 | `testIVRNavigateToDetails` | Navigate to IVR Service Details |
| 18 | `testIVRSearchPositive` | Validate IVR Search Functionality - Positive Flow |
| 19 | `testIVRFilterByStatus` | Validate IVR Filter Functionality - Status Based |
| 20 | `testIVRViewDetailsValidation` | Validate IVR View Details - Excluding Failed Accounts |
| 21 | `testIVRSSORedirectionAndLogout` | Validate IVR SSO Redirection and Logout |
| 22 | `testOBDNavigateToDetails` | Navigate to OBD Service Details |
| 23 | `testOBDSearchPositive` | Validate OBD Search Functionality - Positive Flow |
| 24 | `testOBDFilterByStatus` | Validate OBD Filter Functionality - Status Based |
| 25 | `testOBDViewDetailsValidation` | Validate OBD View Details - Excluding Failed Accounts |
| 26 | `testOBDSSORedirectionAndLogout` | Validate OBD SSO Redirection and Logout |
| 27 | `testCCSNavigateToDetails` | Navigate to CCS Service Details |
| 28 | `testCCSSearchPositive` | Validate CCS Search Functionality - Positive Flow |
| 29 | `testCCSFilterByStatus` | Validate CCS Filter Functionality - Status Based |
| 30 | `testCCSViewDetailsValidation` | Validate CCS View Details - Excluding Failed Accounts |
| 31 | `testCCSSSORedirectionAndLogout` | Validate CCS SSO Redirection and Logout |

### EnterpriseWalletTabTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndWalletNavigation` | Login as Enterprise user, navigate to Dashboard and redirect to Wallet Tab |
| 2 | `testTransactionListVisibility` | Validate transaction list visibility or No Data message |
| 3 | `testTransactionListHeaders` | Validate transaction list column headers |
| 4 | `testTransactionTypeFilter` | Validate Transaction Type filter (Transaction/Recharge/Deduction) |
| 5 | `testPaginationFunctionality` | Validate list pagination behavior |
| 6 | `testTotalCreditAmountValidation` | Validate Total Credit amount equals sum of all Credit values |
| 7 | `testTotalDebitAmountValidation` | Validate Total Debit amount equals sum of all Debit values |
| 8 | `testArchiveReportButtonNavigation` | Validate Archive Report button navigation |
| 9 | `testArchiveReportDownloadBehavior` | Validate Active reports downloadable, Expired not downloadable |
| 10 | `testGenerateReportButtonValidation` | Validate Generate Report button opens popup |
| 11 | `testGenerateReportPopupCancelValidation` | Validate Generate Report popup cancel behavior |
| 12 | `testLogout` | Logout from Enterprise account |

### MediaLibraryTest

| # | Test method | Description |
|---:|---|---|
| 1 | `verifyMediaLibraryPageLoaded` | — |
| 2 | `verifyMediaLibraryTabVisible` | — |

## Reseller — `tests/reseller`

### ResellerDashboardAnalyticsTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testAnalyticsTabClickable` | TC01: Verify Analytics tab is clickable and loads the Performance Overview |
| 2 | `testAllServiceTabsNoError` | TC02: Verify navigation across all service tabs shows no error |
| 3 | `testCustomerFilter` | TC03: Verify Customer filter shows matching services, or the access-denied message when none exist |
| 4 | `testCancelCustomerFilter` | TC04: Verify cancelling the selected customer restores all previous service tabs |
| 5 | `testCalendarFilter` | TC05: Verify applying the calendar/date-range filter |
| 6 | `testLogout` | Logout from Reseller account |

### ResellerDashboardMyWalletsTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testMyWalletsTabDefaultSelection` | TC01: Verify user lands on My Wallets tab by default after Dashboard navigation |
| 2 | `testWalletListing` | TC02: Verify wallet listing and expected wallet info |
| 3 | `testPageHeader` | TC03: Verify My Wallets page header |
| 4 | `testPagination` | TC04: Verify pagination shows only when wallet count > 10 |
| 5 | `testServiceNavigation` | TC05: Verify wallet Action button navigation |
| 6 | `testWalletSearch` | TC06: Verify wallet search functionality |
| 7 | `testWalletFilters` | TC07: Verify Active status + Prepaid type filters |
| 8 | `testClearAllFilters` | TC08: Verify Clear All removes filters and restores full list |
| 9 | `testFundTransferPopup` | TC09: Verify Wallet Fund Transfer button opens popup |
| 10 | `testSourceWalletSelection` | TC10: Verify source wallet (with sufficient balance) selection |
| 11 | `testDestinationWalletSelection` | TC11: Verify destination wallet selection |
| 12 | `testFundTransfer` | TC12: Verify Fund Transfer of ₹1 completes successfully |
| 13 | `testWalletBalanceAfterTransfer` | TC13: Verify source/destination balances reflect the transfer |
| 14 | `testLogout` | Logout from Reseller account |

### ResellerDashboardUserManagementTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testUserListHeaders` | TC01: Validate the header from the User list |
| 2 | `testUpdatePlanRedirect` | TC02: Validate Update Plan clickable and redirects to the Users Rate Card tab |
| 3 | `testAddCreditRedirect` | TC03: Validate Add Credit clickable and redirects to the User Fund Management page |
| 4 | `testSearchByName` | TC04: Validate search functionality from 'Search by name' |
| 5 | `testFilterActiveApplied` | TC05: Validate filter active applied and result matches |
| 6 | `testClearAllFilters` | TC06: Validate clear all restores full result list |
| 7 | `testCreateUserButtonRedirect` | TC07: Validate Create user button clickable and redirects to the create user page |
| 8 | `testArrowRedirectToCustomerWallets` | TC08: Validate arrow button redirects to the customer wallet page with wallet list |
| 9 | `testWalletSearchByName` | TC09: Validate search by wallet name functionality |
| 10 | `testWalletFilterApplied` | TC10: Validate the result after applying the wallet filter |
| 11 | `testWalletClearAllFilters` | TC11: Validate result display after clicking Clear All |
| 12 | `testAddWalletCTA` | TC12: Validate Add Wallet CTA clickable and redirects to the create wallet page |
| 13 | `testWalletServicesDisplay` | TC13: Validate all services available in the wallet display (or a no-data message) |
| 14 | `testAddNewServiceRedirect` | TC14: Validate Add New button redirects to the service account detail page |
| 15 | `testRefillBalanceValidation` | TC15: Validate Refill adds ₹5 and the available balance updates correctly |
| 16 | `testDeductBalanceValidation` | TC16: Validate Deduct removes ₹5 and the available balance updates correctly |
| 17 | `testOrganizationDetailsTab` | TC17: Validate Organization Details tab shows Contact Information and org level |
| 18 | `testProfileTeamMembersList` | TC18: Validate Profile tab shows Team Members list |
| 19 | `testProfileHeader` | TC19: Validate the Profile tab header |
| 20 | `testProfileSearchByName` | TC20: Validate search by name functionality on Profile tab |
| 21 | `testProfileFilterActive` | TC21: Validate data appears based on applied Active filter (Profile) |
| 22 | `testProfileAddNewRedirect` | TC22: Validate Add New button redirects to the Add Team Member page |
| 23 | `testProfileEditButton` | TC23: Validate Edit button clickable with predefined values |
| 24 | `testProfileViewIcon` | TC24: Validate View icon clickable and SV Customer ID is displayed |
| 25 | `testRolesTabRedirect` | TC25: Validate Roles tab clickable and redirects to Role Management |
| 26 | `testRolesSearchByName` | TC26: Validate result appears based on search name (Roles) |
| 27 | `testRolesFilterActive` | TC27: Validate result appears based on applied Active filter, handling no-data message |
| 28 | `testRolesEditButton` | TC28: Validate Edit role button redirects to the edit role page with predefined fields |
| 29 | `testRolesViewIconOverview` | TC29: Validate View icon clickable and redirects to the Overview page |
| 30 | `testRolesAddNewRedirect` | TC30: Validate Add New redirects to the add new role page |
| 31 | `testLogout` | Logout from Reseller account |

### ResellerDashboardUsersTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testUsersTabClickable` | TC01: Verify Users tab is clickable and loads the Users listing |
| 2 | `testUserListingAndPagination` | TC02: Verify user listing, and pagination when data exceeds 10 rows |
| 3 | `testTableHeaders` | TC03: Verify Users table headers |
| 4 | `testSearchByName` | TC04: Verify search by text from 'Search by Name' field |
| 5 | `testFilterAppliedDataMatches` | TC05: Verify data present matches the applied Status filter |
| 6 | `testClearAllFilters` | TC06: Verify Clear All restores the full unfiltered list |
| 7 | `testCreateUserButton` | TC07: Verify Create user button redirects to the create user page |
| 8 | `testUpdatePlanRedirect` | TC08: Verify Update Plan redirects to the Users Rate Card page |
| 9 | `testAddCreditRedirect` | TC09: Verify Add Credit redirects to User Fund Management with matching org name |
| 10 | `testActionButtonRedirect` | TC10: Verify the row Action button redirect |
| 11 | `testLogout` | Logout from Reseller account |

## Super Admin — `tests/superadmin`

### SuperAdminAPIAndDocumentationTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndNavigateToAPIDoc` | Login as Super Admin and navigate to API & Documentation |
| 2 | `testAPIDocTabVisibilityAndClickability` | Verify API & Documentation tab visibility and clickability |
| 3 | `testSMSServiceDocumentation` | Validate SMS service documentation and Swagger UI |
| 4 | `testOBDServiceDocumentation` | Validate OBD service documentation and Swagger UI |
| 5 | `testCCSServiceDocumentation` | Validate CCS service documentation and Swagger UI |
| 6 | `testWABAServiceDocumentation` | Validate WABA service documentation and Swagger UI |
| 7 | `testLogout` | Logout from Super Admin |

### SuperAdminCustomerOrgTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndNavigation` | Login as SuperAdmin and Navigate to Customer Org |
| 2 | `testTableHeaders` | Comprehensive Table Header Validation |
| 3 | `testEnterpriseDrillDown` | Filter Enterprise and Redirect to Overview with Wallet Check |
| 4 | `testWalletTableAndSearch` | Verify Wallet Table and Search |
| 5 | `testWalletUsageTabAndBalances` | Verify Wallet Usage tab shows Prepaid/Postpaid balances |
| 6 | `testOrganizationDetailsTab` | Verify Organization Details Tab - Customer ID |
| 7 | `testNavigateToProfileTab` | Navigate to Profile Tab |
| 8 | `testProfileTableHeaders` | Verify Profile Table Headers |
| 9 | `testEditButtonClickable` | Verify Edit Button Clickable |
| 10 | `testViewIconAndContent` | Verify View Icon and Content |
| 11 | `testSearchField` | Verify Search Field |
| 12 | `testFilterSelectionAndApply` | Verify Filter Selection and Apply |
| 13 | `testAddNewButtonClickable` | Verify Add New Button Clickable |
| 14 | `testRolesTabVisibility` | Verify Roles tab is visible and accessible |
| 15 | `testRolesTableHeaders` | Validate Roles table headers and structure |
| 16 | `testRolesEditIconClickable` | Verify Edit icon clickability for roles |
| 17 | `testRolesViewIconClickable` | Verify View icon clickability for roles |
| 18 | `testRolesSearchNoResults` | Search with non-existent role name (negative test) |
| 19 | `testRolesSearchWithResults` | Search with partial role name (positive test) |
| 20 | `testRolesFilterActive` | Apply Active status filter |
| 21 | `testRolesFilterInactiveAndAll` | Apply Inactive and All role status filters (extended regression) |
| 22 | `testRolesAddNewButton` | Verify Add New button functionality |
| 23 | `testRoleFormSectionHeaders` | Validate section header structure in Add/Edit role form |
| 24 | `testLogout` | Logout from SuperAdmin session |
| 25 | `testResellerDrillDownRegression` | Verify Reseller drill-down and wallet balance (regression) |

### SuperAdminDashboardTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndDashboard` | Login as Super Admin and verify dashboard |
| 2 | `testRCSService` | Test RCS service navigation |
| 3 | `testCCSService` | Test CCS service navigation |
| 4 | `testSMSService` | Test SMS service navigation |
| 5 | `testIVRService` | Test IVR service navigation |
| 6 | `testOBDService` | Test OBD service navigation |
| 7 | `testWABAService` | Test WABA service navigation |
| 8 | `testLiveAgentService` | Test Live Agent service navigation |
| 9 | `testLogout` | Logout from Super Admin |

### SuperAdminRolesManagementTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndNavigateToRolesManagement` | Login as Super Admin and navigate to Roles Management |
| 2 | `testTableHeaders` | Validate table headers are displayed correctly |
| 3 | `testPaginationNext` | Validate pagination Next button |
| 4 | `testPaginationPrevious` | Validate pagination Previous button |
| 5 | `testEditButtonClickable` | Validate Edit button is clickable |
| 6 | `testViewButtonClickable` | Validate View button is clickable |
| 7 | `testSearchFunctionality` | Validate Search functionality with first 3 characters |
| 8 | `testAddNewButtonClickable` | Validate Add New button is clickable |
| 9 | `testAddNewFormFields` | Validate all permission fields in Add New form |
| 10 | `testServiceNodeSSODropdownExpansion` | Validate Service Node SSO dropdown expansion |
| 11 | `testNoDuplicateValues` | Validate no duplicate values in form |
| 12 | `testUsersCountNavigation` | Validate Users count navigation to Team Management |
| 13 | `testLogout` | Logout from Super Admin |

### SuperAdminServiceNodeSSOTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndNavigateToSSO` | Login as Super Admin and navigate to Service Node SSO |
| 2 | `testSMSServiceSSO` | Test SSO login for SMS service with all roles |
| 3 | `testRCSServiceSSO` | Test SSO login for RCS service with all roles |
| 4 | `testCCSServiceSSO` | Test SSO login for CCS service with all roles |
| 5 | `testOBDServiceSSO` | Test SSO login for OBD service with all roles |
| 6 | `testIVRServiceSSO` | Test SSO login for IVR service with all roles |
| 7 | `testWABAServiceSSO` | Test SSO login for WABA service with all roles |
| 8 | `testLiveAgentServiceSSO` | Test SSO login for Live Agent service with all roles |
| 9 | `testLogout` | Logout from Super Admin |

### SuperAdminTeamManagementTest

| # | Test method | Description |
|---:|---|---|
| 1 | `testLoginAndNavigateToTeamManagement` | Login as Super Admin and navigate to Team Management |
| 2 | `testTableHeaders` | Validate table headers are displayed correctly |
| 3 | `testPaginationNext` | Validate pagination Next button |
| 4 | `testPaginationPrevious` | Validate pagination Previous button |
| 5 | `testEditButtonClickable` | Validate Edit button is clickable |
| 6 | `testViewButtonClickable` | Validate View button is clickable |
| 7 | `testSearchFunctionality` | Validate Search functionality with 'Super Admin' |
| 8 | `testFilterByRole` | Validate Filter by role 'Reporting Manager' |
| 9 | `testAddNewButtonClickable` | Validate Add New button is clickable |
| 10 | `testLogout` | Logout from Super Admin |
