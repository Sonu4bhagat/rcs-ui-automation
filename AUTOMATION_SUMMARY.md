# RCS Automation — Summary

**Audience:** QA leads, engineering managers
**Scope:** UI regression of the SmartPing RCS platform (staging) for the Super Admin, Enterprise and Reseller portals.
Technical details: [README.md](README.md) · Full test list: [TEST_CASE_INVENTORY.md](TEST_CASE_INVENTORY.md)

---

## Coverage

**305 automated test cases in 22 test classes.**

| Portal | Test classes | Test cases | Modules |
|---|---:|---:|---|
| Common (all roles) | 1 | 6 | Login for each role, invalid-credential and blank-field validation |
| Enterprise | 11 | 164 | Dashboard navigation, Services (SMS, RCS, WABA, IVR, OBD, CCS), Wallet, Reports, Rate Card, Control Center, Roles Management, API Documentation, RCS Assistant flow, Assistant list, Media Library |
| Reseller | 4 | 62 | My Wallets, Users, Analytics, User Management (wallets, services, refill/deduct, organisation details, profiles, roles) |
| Super Admin | 6 | 73 | Dashboard, Customer Org (drill-down, wallets, profiles, roles), Team Management, Roles Management, Service Node SSO, API Documentation |

REST API tests exist under `src/test/java/api` but are not yet part of the regression suites.

---

## How it runs

- **Jenkins pipeline** with a choice of suite: everything, or one portal at a time (`enterprise.xml`, `reseller.xml`, `superadmin.xml`).
- **Headless Chrome**, so it runs on the Jenkins agent without a display.
- **Email summary after every Jenkins build**: pass/fail counts, pass rate, results per portal and class, failed and skipped tests with reasons, and the detailed HTML report attached.
- **Detailed HTML report** (Extent) with step-by-step logs and a screenshot for every failure.

## Reliability measures

- Each failing test is retried once; only the final result is reported.
- If the browser crashes, a new one is started for the next test.
- If a session ends unexpectedly, selected test classes log in again and continue instead of failing every remaining test.

---

## Current limitations and next steps

| Item | Impact | Proposed action |
|---|---|---|
| Test accounts are shared with manual users and other runs | A login elsewhere can end a running test's session and cause unrelated failures | Dedicated automation-only accounts per role |
| Credentials are stored in the repository configuration file | Security exposure | Move to Jenkins credentials / environment variables; rotate the current passwords |
| Many fixed waits (`Thread.sleep`) | Longer runs and occasional timing failures | Replace with condition-based waits as tests are touched |
| Some tests pass without checking anything when data is missing (e.g. empty tables) | Coverage gaps can go unnoticed | Review and make these explicit skips or failures |
| API tests not in any suite | API regressions are not caught | Add an API suite to the pipeline |

## Execution time

Full-suite duration: **[to be measured from the Jenkins build history]**
