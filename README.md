# RCS Automation Framework

UI regression automation for the SmartPing RCS platform (staging), covering the **Super Admin**, **Enterprise** and **Reseller** portals.

- **305 test cases** in **22 test classes** — full list in [TEST_CASE_INVENTORY.md](TEST_CASE_INVENTORY.md)
- Summary for leads/managers: [AUTOMATION_SUMMARY.md](AUTOMATION_SUMMARY.md)
- Runs locally with Maven or from Jenkins ([Jenkinsfile](Jenkinsfile)), with an HTML report and an email summary

---

## Tech stack

| Component | Version |
|---|---|
| Java | 17 |
| Maven | 3.x |
| Selenium WebDriver | 4.29.0 |
| TestNG | 7.10.2 |
| WebDriverManager | 5.8.0 |
| ExtentReports (Spark HTML report) | 5.0.9 |
| JavaMail (report email) | 1.6.2 |
| REST Assured / Jackson (API tests) | 5.4.0 / 2.16.1 |
| Browser | Google Chrome (headless supported) |

---

## Project structure

Pages, locators and tests use the same role folders, so everything for a feature sits in one place.

```
RCS_Automation/
├── src/main/java/
│   ├── base/          BaseTest (driver lifecycle, report hooks, session recovery), DriverFactory
│   ├── enums/         UserRole
│   ├── locators/      Element locators            ┐
│   │   ├── common/      used by more than one role │ same role folders
│   │   ├── enterprise/                             │ in all three
│   │   ├── reseller/                               │
│   │   └── superadmin/                             │
│   ├── pages/         Page Object classes          │
│   │   └── common/ enterprise/ reseller/ superadmin/┘
│   └── utils/         ConfigReader, ExtentReportManager, EmailReportSender, RetryAnalyzer, ...
├── src/main/resources/
│   ├── image/         Files uploaded by tests (images, video, PDF)
│   └── META-INF/services/  Registers the email reporter with TestNG
├── src/test/java/
│   ├── tests/         Test classes
│   │   ├── common/      tests covering all roles (LoginTest)
│   │   ├── enterprise/  tests that log in as an Enterprise user
│   │   ├── reseller/    tests that log in as a Reseller user
│   │   ├── superadmin/  tests that log in as Super Admin
│   │   └── ccs/         CCS project: CCS Enterprise account, SSO to CCS-Live, Users management, Call Flow / Queue
│   ├── helpers/       Reusable test steps shared across classes
│   └── api/           REST API tests (not part of the UI suites)
├── src/test/resources/config.properties
├── testng.xml         Full suite (all roles)
├── common.xml         Common suite (tests covering all roles)
├── enterprise.xml     Enterprise suite
├── reseller.xml       Reseller suite
├── superadmin.xml     Super Admin suite
├── ccs.xml            CCS project suite
├── Jenkinsfile        CI pipeline
└── pom.xml
```

### Adding a new test

1. Put the class in the folder of the user it **logs in as**: `tests/enterprise`, `tests/reseller` or `tests/superadmin`. Use `tests/common` only for tests that cover several roles.
2. Set the package to match the folder, e.g. `package tests.reseller;`, and prefix the class name with the role, e.g. `ResellerDashboardWalletTest`.
3. Put its page and locator classes in the **same role folder** under `pages/` and `locators/`. A page used by more than one role goes in `common/`.
4. Register the class in `testng.xml` **and** in that role's suite file.
5. Classes that keep one login across tests (`retainSession = true`) should also set `sessionRole` (see [Session recovery](#session-recovery)).

The Extent report and the email group results by role automatically, based on the folder.

---

## Configuration

Settings are read from `src/test/resources/config.properties`. Any key can be overridden without editing the file:

1. JVM system property, e.g. `-Dbrowser.headless=true`
2. Environment variable — key in upper case with dots as underscores, e.g. `EMAIL_PASSWORD`
3. `config.properties`

| Key | Purpose |
|---|---|
| `url` | Application login URL |
| `superadmin.email` / `superadmin.password` | Super Admin test account |
| `enterprise.email` / `enterprise.password` | Enterprise test account |
| `reseller.email` / `reseller.password` | Reseller test account |
| `browser.headless` | `true` for headless Chrome (Jenkins), `false` to watch the browser |
| `email.enable`, `email.recipients` | Report email on/off and recipients (set by Jenkins parameters) |
| `email.smtp.host`, `email.smtp.port`, `email.sender`, `email.password` | SMTP settings for the report email |

> Credentials should be supplied through Jenkins credentials / environment variables rather than committed values.

---

## Running tests

```bash
# Full suite (all roles)
mvn clean test -DsuiteXmlFile=testng.xml

# One role
mvn test -DsuiteXmlFile=enterprise.xml      # or common.xml / reseller.xml / superadmin.xml / ccs.xml

# One class, or one method
mvn test -Dtest=ResellerDashboardAnalyticsTest
mvn test -Dtest=LoginTest#testSuperAdminLogin

# Headless (as in Jenkins)
mvn test -Dtest=LoginTest -Dbrowser.headless=true
```

**Run one test run at a time.** All runs share the same four staging accounts (Super Admin, Enterprise, Reseller, CCS Enterprise); a second run (or a manual login with the same account) can end the other run's session.

### Jenkins

The pipeline ([Jenkinsfile](Jenkinsfile)) checks out `main`, compiles and runs the selected suite. Parameters:

| Parameter | Default | Purpose |
|---|---|---|
| `ALL_TESTS` | off | Tick to run every test case (overrides the group boxes) |
| `COMMON` / `ENTERPRISE` / `RESELLER` / `SUPER_ADMIN` / `CCS` | off | Tick one or more groups - all ticked groups run together in one build, with one report and one email |
| `TEST_CLASS` | empty | Optional: run only specific class(es) instead, e.g. `LoginTest`, `LoginTest,SuperAdminDashboardTest`, or `LoginTest#testSuperAdminLogin` |

If nothing is ticked and `TEST_CLASS` is empty, all tests run.
| `HEADLESS` | `true` | Headless Chrome |
| `SEND_EMAIL` | `true` | Send the summary email |
| `EMAIL_RECIPIENTS` | team list | Comma-separated recipients |

Failed tests mark the build **UNSTABLE** (the run still completes and the report is produced). Concurrent builds are disabled.

---

## Reports

| Output | Location |
|---|---|
| Extent HTML report (steps, screenshots on failure, grouped by role and class) | `test-output/ExtentReport.html` (+ `test-output/spark/` assets) |
| TestNG / Surefire results | `target/surefire-reports/` |
| Jenkins | Archived artifacts and the **Test Results** trend for each build |

**Email summary** — sent automatically **only for Jenkins builds** (never for local runs). It contains build details, total/passed/failed/skipped counts and pass rate, results by role and class, failed and skipped tests with reasons, and the Extent report attached as a ZIP.

---

## Framework features

- **Page Object Model** with locators kept in separate classes.
- **Retry on failure** — every test is retried once (`RetryAnalyzer`, applied by `AnnotationTransformer` in the suite files). Only the final attempt is counted in the report and email.
- **Screenshot on failure**, embedded in the Extent report.
- **Headless Chrome at 1920×1080**, so wide pages render the same as on a desktop.
- **Driver self-healing** — if the browser crashes, `BaseTest` starts a new one before the next test.
- <a id="session-recovery"></a>**Session recovery (opt-in)** — a class that keeps one login (`retainSession = true`) can set `sessionRole = UserRole.X`. If a test then finds itself on the login page unexpectedly (browser crash, or the session ended because the account logged in elsewhere), the framework logs in again as that role and returns to the last page, instead of failing every remaining test. It does not interfere with a test's own deliberate logout. Override `loginAgain(...)` when a class logs in differently (e.g. a specific wallet). Enabled today in `SuperAdminCustomerOrgTest`, `EnterpriseReportsTabTest` and `ResellerDashboardUserManagementTest`.

---

## Troubleshooting

| Symptom | Cause / fix |
|---|---|
| `WARNING: Unable to find CDP implementation matching 155` | Harmless — Chrome is newer than the Selenium DevTools bindings. Tests are unaffected. |
| Tests suddenly start on `/login` mid-class | The shared test account was used elsewhere at the same time (another run, Jenkins, or a manual login). Run one suite at a time; enable session recovery for the class. |
| Element visible in GUI mode but "not found" headless | Check the element is not hidden by a narrow layout; headless runs at 1920×1080. |
| Report opened from Jenkins has no styling | Jenkins' Content Security Policy blocks the report's scripts. Use the ZIP attached to the email, or relax `hudson.model.DirectoryBrowserSupport.CSP` on the Jenkins server. |
