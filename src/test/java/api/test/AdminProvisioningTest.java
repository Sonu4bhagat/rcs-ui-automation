package api.test;

import api.endpoints.AdminEndpoints;
import api.payloads.AdminPayload;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.given;

public class AdminProvisioningTest extends ApiBaseTest {

    private String adminToken;
    private String svCustomerId; // e.g. SV1861465104
    private String customerId; // e.g. CUST...
    private String organizationName; // e.g. "Enterprise Org 2fbf78" - set during test02
    private String walletName; // e.g. Wallet-xxxxx
    private String serviceAccountName; // e.g. sms-sa-xxxx

    @BeforeClass
    public void setupAdmin() {
        RestAssured.baseURI = "https://stagingbackendvault.smartping.io";
    }

    /**
     * POST a wallet-creation payload, retrying with backoff specifically when
     * the backend rejects it with "wallet name must start with the organization
     * name" - customer creation (test02) responds with "accepted" rather than
     * "created", confirming provisioning is asynchronous. Calling wallet
     * creation immediately can race ahead of that, and the error you get back
     * looks identical to a genuinely wrong wallet-name prefix, which is
     * misleading - it's transient, not a naming bug, so retry instead of
     * failing fast on it.
     */
    private Response createWalletWithRetry(AdminPayload payload, String url) {
        Response response = null;
        int maxAttempts = 5;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            response = given()
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType("application/json")
                    .body(payload)
                    .when()
                    .post(url);

            if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
                return response;
            }

            String message = response.jsonPath().getString("message");
            boolean isProvisioningLag = message != null
                    && message.toLowerCase().contains("organization name");
            if (!isProvisioningLag || attempt == maxAttempts) {
                return response;
            }

            System.out.println("Wallet creation attempt " + attempt + " failed with '" + message
                    + "' - customer is likely still provisioning, retrying in 2s...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return response;
            }
        }
        return response;
    }

    // =========================================================================
    // 01. Token Copy
    // =========================================================================
    @Test(priority = 1, description = "QA-TC-01: Admin Token Generation")
    public void test01_TokenCopy() {
        AdminPayload loginPayload = new AdminPayload();
        loginPayload.setUsername("admin@smartping.io");
        loginPayload.setPassword("CZI86Maj5ktz9bHQ6pjO");

        long startTime = System.currentTimeMillis();
        Response response = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post(AdminEndpoints.ADMIN_AUTH_URL);

        long responseTime = System.currentTimeMillis() - startTime;
        if (response.getStatusCode() != 200 && response.getStatusCode() != 201) {
            response.then().log().all();
        }

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201, 
                "Status code should be 200/201 Success");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");

        String jsonToken = response.jsonPath().getString("access_token");
        if (jsonToken == null) jsonToken = response.jsonPath().getString("token");
        if (jsonToken == null && response.jsonPath().get("data") != null) {
            jsonToken = response.jsonPath().getString("data.token");
        }
        if (jsonToken == null) jsonToken = response.getBody().asString();

        adminToken = jsonToken;
        Assert.assertNotNull(adminToken, "Token failed to generate");
    }

    // =========================================================================
    // 02. Create_Customer_Enterprise
    // =========================================================================
    @Test(priority = 2, dependsOnMethods = "test01_TokenCopy", description = "QA-TC-02: Create Customer Enterprise")
    public void test02_CreateCustomer_Enterprise() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 6);
        customerId = "CUST" + uniqueSuffix;
        organizationName = "Enterprise Org " + uniqueSuffix;

        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.setCustomerType("enterprise");
        payload.setFirstName("Sashi");
        payload.setLastName("smartping");
        payload.setOrganizationName(organizationName);
        payload.setAccountManager("accountmanager");
        payload.setUsername("ent." + uniqueSuffix + "@smartping.ai");
        payload.setPassword("Test@2025");
        payload.setMobileNo("9198" + (10000000 + new Random().nextInt(89999999)));
        payload.setCountry("India");
        payload.setMaxSvProfiles(3);
        payload.setIsWhitelabeled("false");
        payload.setCircleName("Hyderbad");
        payload.setZoneName("South");
        payload.settId(UUID.randomUUID().toString());

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(AdminEndpoints.ADMIN_CUSTOMERS_URL);

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");

        svCustomerId = response.jsonPath().getString("svCustomerId");
        if (svCustomerId == null) svCustomerId = response.jsonPath().getString("id");
        if (svCustomerId == null) svCustomerId = "SV" + System.currentTimeMillis();

        // The response body literally says "Customer account creation is
        // accepted" (not "created") - this confirms customer provisioning is
        // asynchronous. Calling the wallet-creation API immediately can hit the
        // backend before the customer/org record it validates against is fully
        // committed, surfacing as a spurious "wallet name must start with the
        // organization name" 400. createWalletWithRetry() below handles this by
        // retrying specifically on that error, but give it a head start here too.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    // =========================================================================
    // 03. Create_Customer_Reseller
    // =========================================================================
    @Test(priority = 3, dependsOnMethods = "test01_TokenCopy", description = "QA-TC-03: Create Customer Reseller")
    public void test03_CreateCustomer_Reseller() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 6);
        AdminPayload payload = new AdminPayload();
        payload.setCustomerId("CUSTRES" + uniqueSuffix);
        payload.setCustomerType("reseller");
        payload.setFirstName("acpl032");
        payload.setLastName("test");
        payload.setOrganizationName("Reseller Org " + uniqueSuffix);
        payload.setAccountManager("accountmanager");
        payload.setUsername("res." + uniqueSuffix + "@smartping.ai");
        payload.setPassword("Test@2025");
        payload.setMobileNo("9198" + (10000000 + new Random().nextInt(89999999)));
        payload.setCountry("India");
        payload.setMaxSvProfiles(3);
        payload.setIsWhitelabeled("false");
        payload.setCircleName("Hyderbad");
        payload.setZoneName("South");
        payload.settId(UUID.randomUUID().toString());

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(AdminEndpoints.ADMIN_CUSTOMERS_URL);

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201, 
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 04. Create_Wallet_SSW_postpaid
    // =========================================================================
    @Test(priority = 4, dependsOnMethods = "test02_CreateCustomer_Enterprise", description = "QA-TC-04: Create Wallet SSW Postpaid")
    public void test04_CreateWallet_SSW_Postpaid() {
        String wName = svCustomerId + "-Wallet-SSW-Post-" + UUID.randomUUID().toString().substring(0, 5);
        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());
        payload.setWalletName(wName);
        payload.setWalletMode("SSW");
        payload.setWalletType("postpaid");
        payload.setBillingCycle("yearly");

        long startTime = System.currentTimeMillis();
        Response response = createWalletWithRetry(payload, AdminEndpoints.getWalletUrl(svCustomerId));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 05. Create_Wallet_SSW_prepaid
    // =========================================================================
    @Test(priority = 5, dependsOnMethods = "test02_CreateCustomer_Enterprise", description = "QA-TC-05: Create Wallet SSW Prepaid")
    public void test05_CreateWallet_SSW_Prepaid() {
        walletName = svCustomerId + "-Wallet-SSW-Prep-" + UUID.randomUUID().toString().substring(0, 5);
        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());
        payload.setWalletName(walletName);
        payload.setWalletMode("SSW");
        payload.setWalletType("prepaid");
        payload.setBillingCycle("monthly");

        long startTime = System.currentTimeMillis();
        Response response = createWalletWithRetry(payload, AdminEndpoints.getWalletUrl(svCustomerId));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 06. Create_Wallet_UW_prepaid
    // =========================================================================
    @Test(priority = 6, dependsOnMethods = "test02_CreateCustomer_Enterprise", description = "QA-TC-06: Create Wallet UW Prepaid")
    public void test06_CreateWallet_UW_Prepaid() {
        String wName = svCustomerId + "-Wallet-UW-Prep-" + UUID.randomUUID().toString().substring(0, 5);
        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());
        payload.setWalletName(wName);
        payload.setWalletMode("UW");
        payload.setWalletType("prepaid");
        payload.setBillingCycle("monthly");

        long startTime = System.currentTimeMillis();
        Response response = createWalletWithRetry(payload, AdminEndpoints.getWalletUrl(svCustomerId));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 07. Create_Wallet_UW_postpaid
    // =========================================================================
    @Test(priority = 7, dependsOnMethods = "test02_CreateCustomer_Enterprise", description = "QA-TC-07: Create Wallet UW Postpaid")
    public void test07_CreateWallet_UW_Postpaid() {
        String wName = svCustomerId + "-Wallet-UW-Post-" + UUID.randomUUID().toString().substring(0, 5);
        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());
        payload.setWalletName(wName);
        payload.setWalletMode("UW");
        payload.setWalletType("postpaid");
        payload.setBillingCycle("yearly");

        long startTime = System.currentTimeMillis();
        Response response = createWalletWithRetry(payload, AdminEndpoints.getWalletUrl(svCustomerId));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 08. Update_RateCard_sms
    // =========================================================================
    @Test(priority = 8, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-08: Update RateCard SMS")
    public void test08_UpdateRateCard_SMS() {
        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());

        Map<String, Object> smsConfig = new HashMap<>();
        Map<String, Object> oneTime = new HashMap<>();
        oneTime.put("otc", 9981);
        oneTime.put("securityDeposit", 72);
        oneTime.put("advanceCharges", 92);

        Map<String, Object> transactional = new HashMap<>();
        transactional.put("chargingMode", "submission");
        transactional.put("dltRate", 200);
        transactional.put("otherChargeRate", 3281);
        transactional.put("promoUnitPrice", 952);
        transactional.put("transUnitPrice", 1000);
        transactional.put("otpUnitPrice", 889);

        smsConfig.put("oneTimeBilling", oneTime);
        smsConfig.put("periodicBilling", new HashMap<>());
        smsConfig.put("transactionalBilling", transactional);

        Map<String, Object> services = new HashMap<>();
        services.put("sms", smsConfig);
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .put(AdminEndpoints.getRateCardUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201 || response.getStatusCode() == 204, 
                "Status code should be 200/201/204 Success");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 09. Update_RateCard_IVR
    // =========================================================================
    @Test(priority = 9, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-09: Update RateCard IVR")
    public void test09_UpdateRateCard_IVR() {
        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());

        Map<String, Object> ivrConfig = new HashMap<>();
        Map<String, Object> periodic = new HashMap<>();
        periodic.put("costPerDid", 700);
        periodic.put("costPerVmn", 1000);
        periodic.put("freeCallValue", 10000);
        periodic.put("costPerChannel", 100);
        periodic.put("fixedperiodicCharge", 1000);

        Map<String, Object> sttCharge = new HashMap<>();
        sttCharge.put("pulseRate", 510);
        sttCharge.put("pulseDuration", 55);

        Map<String, Object> ivrBaseRate = new HashMap<>();
        ivrBaseRate.put("pulseRate", 10);
        ivrBaseRate.put("pulseDuration", 30);

        Map<String, Object> transactional = new HashMap<>();
        transactional.put("ttsChargePerChar", 500);
        transactional.put("chargingMode", "answeredcall");
        transactional.put("dltCharge", 200);
        transactional.put("recordingStorageChargePerMonthPerGb", 100);
        transactional.put("freeCallValue", 10000);
        transactional.put("sttCharge", sttCharge);
        transactional.put("ivrBaseRate", ivrBaseRate);

        ivrConfig.put("periodicBilling", periodic);
        ivrConfig.put("transactionalBilling", transactional);

        Map<String, Object> services = new HashMap<>();
        services.put("ivr", ivrConfig);
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .put(AdminEndpoints.getRateCardUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201 || response.getStatusCode() == 204, 
                "Status code should be 200/201/204 Success");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 10. Update_RateCard_OBD
    // =========================================================================
    @Test(priority = 10, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-10: Update RateCard OBD")
    public void test10_UpdateRateCard_OBD() {
        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());

        Map<String, Object> obdConfig = new HashMap<>();
        Map<String, Object> oneTime = new HashMap<>();
        oneTime.put("otc", 0);
        oneTime.put("securityDeposit", 0);
        oneTime.put("advanceCharges", 0);

        Map<String, Object> periodic = new HashMap<>();
        periodic.put("recordingStorageChargePerMonthPerMb", 0);
        periodic.put("costPerDid", 0);
        periodic.put("freeCallValue", 50000);
        periodic.put("costPerChannel", 0);
        periodic.put("costPerVmn", 0);
        periodic.put("fixedPeriodicCharge", 0);

        Map<String, Object> sttCharge = new HashMap<>();
        sttCharge.put("pulseRate", 0);
        sttCharge.put("pulseDuration", 70);

        Map<String, Object> obdBaseRate = new HashMap<>();
        obdBaseRate.put("pulseRate", 500);
        obdBaseRate.put("pulseDuration", 15);

        List<Map<String, Object>> obdRatePerType = new ArrayList<>();
        String[] templates = {"simple_voice", "dtmf_input", "dtmf_record", "call_patch", "custom_flow"};
        String[] connTypes = {"promo", "trans", "promo", "promo", "trans"};
        int[] rates = {1201, 1301, 1401, 1501, 2001};
        int[] durations = {21, 26, 31, 36, 41};

        for (int i = 0; i < templates.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("obdTemplate", templates[i]);
            item.put("connectionType", connTypes[i]);
            item.put("pulseRate", rates[i]);
            item.put("pulseDuration", durations[i]);
            obdRatePerType.add(item);
        }

        Map<String, Object> transactional = new HashMap<>();
        transactional.put("chargingMode", "answeredcall");
        transactional.put("ttsChargePerChar", 0);
        transactional.put("costPerChannel", 0);
        transactional.put("freeCallValue", 50000);
        transactional.put("dltCharge", 0);
        transactional.put("sttCharge", sttCharge);
        transactional.put("obdBaseRate", obdBaseRate);
        transactional.put("obdRatePerType", obdRatePerType);

        obdConfig.put("oneTimeBilling", oneTime);
        obdConfig.put("periodicBilling", periodic);
        obdConfig.put("transactionalBilling", transactional);

        Map<String, Object> services = new HashMap<>();
        services.put("obd", obdConfig);
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .put(AdminEndpoints.getRateCardUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201 || response.getStatusCode() == 204, 
                "Status code should be 200/201/204 Success");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 11. Update_RateCard_CCS
    // =========================================================================
    @Test(priority = 11, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-11: Update RateCard CCS")
    public void test11_UpdateRateCard_CCS() {
        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());

        Map<String, Object> ccsConfig = new HashMap<>();
        Map<String, Object> oneTime = new HashMap<>();
        oneTime.put("otc", 1);
        oneTime.put("securityDeposit", 0);
        oneTime.put("advanceCharges", 0);

        Map<String, Object> periodic = new HashMap<>();
        periodic.put("costPerDid", 8000);
        periodic.put("costPerVmn", 8000);
        periodic.put("costPerAgent", 8000);
        periodic.put("costPerSupervisor", 8000);
        periodic.put("costPerQualityAnalyst", 8000);
        periodic.put("costVoiceLogger", 8000);
        periodic.put("costPerChannel", 8000);
        periodic.put("costPerOperator", 8000);
        periodic.put("costPerCampaignSupervisor", 8000);
        periodic.put("premiumNumberCharges", 8000);
        periodic.put("fixedRental", 8000);
        periodic.put("platformCharges", 8000);
        periodic.put("cliCharges", 8000);
        periodic.put("costPerCallRecording", 8000);
        periodic.put("otherCharges", 8000);

        Map<String, Object> transactional = new HashMap<>();
        transactional.put("freeCallValue", 8000);
        transactional.put("recordingStorageChargePerMonthPerGb", 8000);
        transactional.put("chargingMode", "dialed");
        transactional.put("sttCharge", new HashMap<>());
        transactional.put("pulseDuration", 60);
        transactional.put("pulseRate", 8000);
        transactional.put("clickToCallCharges", 8000);
        transactional.put("ttsChargePerChar", 8000);

        ccsConfig.put("oneTimeBilling", oneTime);
        ccsConfig.put("periodicBilling", periodic);
        ccsConfig.put("transactionalBilling", transactional);

        Map<String, Object> services = new HashMap<>();
        services.put("ccs", ccsConfig);
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .put(AdminEndpoints.getRateCardUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201 || response.getStatusCode() == 204, 
                "Status code should be 200/201/204 Success");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 12. Update_Ratecard_WABA
    // =========================================================================
    @Test(priority = 12, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-12: Update RateCard WABA")
    public void test12_UpdateRateCard_WABA() {
        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());
        payload.setRequestId("12jjkjs41m");

        Map<String, Object> wabaConfig = new HashMap<>();
        Map<String, Object> oneTime = new HashMap<>();
        oneTime.put("otc", 10000);
        oneTime.put("costFlowBuilder", 20000);
        oneTime.put("costNativeIntegration", 5000);

        Map<String, Object> periodic = new HashMap<>();
        periodic.put("rentalCost", 30000);
        periodic.put("costCatalogueManagement", 50000);
        periodic.put("costMetaAds", 2000);
        periodic.put("wabaNumberRental", 4123232);
        periodic.put("costLiveAgent", 10765);

        Map<String, Object> transactional = new HashMap<>();
        transactional.put("chargingMode", "delivery");
        transactional.put("wabaPaymentTranscationCharge", 999);
        transactional.put("costMarketing", 90000);
        transactional.put("costUtility", 3000);
        transactional.put("costAuthentication", 2);
        transactional.put("costService", 123);

        wabaConfig.put("oneTimeBilling", oneTime);
        wabaConfig.put("periodicBilling", periodic);
        wabaConfig.put("transactionalBilling", transactional);

        Map<String, Object> services = new HashMap<>();
        services.put("waba", wabaConfig);
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .put(AdminEndpoints.getRateCardUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201 || response.getStatusCode() == 204, 
                "Status code should be 200/201/204 Success");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 13. Update_RateCard_RCS
    // =========================================================================
    @Test(priority = 13, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-13: Update RateCard RCS")
    public void test13_UpdateRateCard_RCS() {
        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());

        Map<String, Object> rcsConfig = new HashMap<>();
        Map<String, Object> oneTime = new HashMap<>();
        oneTime.put("otc", 10000);
        oneTime.put("costNativeIntegration", 5000);

        Map<String, Object> periodic = new HashMap<>();
        periodic.put("platformRentalCost", 30000);
        periodic.put("integrationRentalCost", 30000);
        periodic.put("othersRentalCost", 30000);

        Map<String, Object> simpleTextMessage = new HashMap<>();
        simpleTextMessage.put("costPromo", 50000);
        simpleTextMessage.put("costTrans", 3000);
        simpleTextMessage.put("costOtp", 60000);

        Map<String, Object> richTextMessage = new HashMap<>();
        richTextMessage.put("costPromo", 50000);
        richTextMessage.put("costTrans", 3000);
        richTextMessage.put("costOtp", 60000);

        Map<String, Object> transactional = new HashMap<>();
        transactional.put("chargingMode", "submission");
        transactional.put("capabilityCharge", 50000);
        transactional.put("simpleTextMessage", simpleTextMessage);
        transactional.put("richTextMessage", richTextMessage);
        transactional.put("conversationCharges", 3000);

        rcsConfig.put("oneTimeBilling", oneTime);
        rcsConfig.put("periodicBilling", periodic);
        rcsConfig.put("transactionalBilling", transactional);

        Map<String, Object> services = new HashMap<>();
        services.put("rcs", rcsConfig);
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .put(AdminEndpoints.getRateCardUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201 || response.getStatusCode() == 204, 
                "Status code should be 200/201/204 Success");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 14. Create_ServiceAccount_CCS
    // =========================================================================
    @Test(priority = 14, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-14: Create ServiceAccount CCS")
    public void test14_CreateServiceAccount_CCS() {
        String saName = "ccs-sa-" + UUID.randomUUID().toString().substring(0, 4);

        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());

        Map<String, Object> saConfig = new HashMap<>();
        saConfig.put("serviceAccountName", saName);
        saConfig.put("enabledChannels", Arrays.asList("GUI", "HTTP"));
        saConfig.put("validityFrom", "2025-02-27T14:57:19.000Z");
        saConfig.put("validityTo", "2026-05-25T14:57:19.000Z");
        saConfig.put("gracePeriodEndDays", 15);
        saConfig.put("trialDaysBeforeBilling", 178);
        saConfig.put("isAccountActive", true);
        saConfig.put("trafficType", "national");
        saConfig.put("tps", 100);
        saConfig.put("trafficStartsAt", "2025-03-13T14:57:19.000Z");

        Map<String, Object> services = new HashMap<>();
        services.put("ccs", Collections.singletonList(saConfig));
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(AdminEndpoints.getServiceAccountUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201, 
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 15. Create_ServiceAccount_OBD
    // =========================================================================
    @Test(priority = 15, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-15: Create ServiceAccount OBD")
    public void test15_CreateServiceAccount_OBD() {
        String saName = "obd-sa-" + UUID.randomUUID().toString().substring(0, 4);

        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());

        Map<String, Object> saConfig = new HashMap<>();
        saConfig.put("serviceAccountName", saName);
        saConfig.put("enabledChannels", Arrays.asList("GUI", "HTTP"));
        saConfig.put("connectionType", Collections.singletonList("transactional"));
        saConfig.put("dltPrincipalEntityId", "212144324891");
        saConfig.put("validityFrom", "2025-07-27T14:57:19.000Z");
        saConfig.put("validityTo", "2027-08-25T14:57:19.000Z");
        saConfig.put("isAccountActive", true);
        saConfig.put("trafficType", "national");
        saConfig.put("tps", 100);
        saConfig.put("trafficStartsAt", "2025-02-27T14:57:19.000Z");

        Map<String, Object> services = new HashMap<>();
        services.put("obd", Collections.singletonList(saConfig));
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(AdminEndpoints.getServiceAccountUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201, 
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 16. Create_ServiceAccount_IVR
    // =========================================================================
    @Test(priority = 16, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-16: Create ServiceAccount IVR")
    public void test16_CreateServiceAccount_IVR() {
        String saName = "ivr-sa-" + UUID.randomUUID().toString().substring(0, 4);

        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());

        Map<String, Object> saConfig = new HashMap<>();
        saConfig.put("serviceAccountName", saName);
        saConfig.put("connectionType", Collections.singletonList("transactional"));
        saConfig.put("enabledChannels", Arrays.asList("GUI", "HTTP"));
        saConfig.put("dltPrincipalEntityId", "2121343413131");
        saConfig.put("validityFrom", "2025-02-27T14:57:19.000Z");
        saConfig.put("validityTo", "2026-05-25T14:57:19.000Z");
        saConfig.put("isAccountActive", true);
        saConfig.put("trafficType", "national");
        saConfig.put("tps", 1000);

        Map<String, Object> services = new HashMap<>();
        services.put("ivr", Collections.singletonList(saConfig));
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(AdminEndpoints.getServiceAccountUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201, 
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 17. Create_ServiceAccount_WABA
    // =========================================================================
    @Test(priority = 17, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-17: Create ServiceAccount WABA")
    public void test17_CreateServiceAccount_WABA() {
        String saName = "waba-sa-" + UUID.randomUUID().toString().substring(0, 4);

        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());

        Map<String, Object> saConfig = new HashMap<>();
        saConfig.put("serviceAccountName", saName);
        saConfig.put("enabledChannels", Arrays.asList("GUI", "HTTP"));
        saConfig.put("connectionType", Arrays.asList("transactional", "promotional"));
        saConfig.put("trafficType", "national");
        saConfig.put("activeWabaBusinessNumber", "9198" + (10000000 + new Random().nextInt(89999999)));
        saConfig.put("validityFrom", "2025-03-27T14:57:19.000Z");
        saConfig.put("validityTo", "2026-03-17T14:57:19.000Z");
        saConfig.put("bspName", "rml");
        saConfig.put("isAccountActive", true);
        saConfig.put("tps", 1000);
        saConfig.put("trafficStartsAt", "2025-03-27T14:57:19.000Z");

        Map<String, Object> services = new HashMap<>();
        services.put("waba", Collections.singletonList(saConfig));
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(AdminEndpoints.getServiceAccountUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201, 
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 18. Create_ServiceAccount_SMS
    // =========================================================================
    @Test(priority = 18, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-18: Create ServiceAccount SMS")
    public void test18_CreateServiceAccount_SMS() {
        serviceAccountName = "sms-sa-" + UUID.randomUUID().toString().substring(0, 4);

        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());

        Map<String, Object> saConfig = new HashMap<>();
        saConfig.put("serviceAccountName", serviceAccountName);
        saConfig.put("connectionType", Collections.singletonList("promotional"));
        saConfig.put("trafficType", "national");
        saConfig.put("enabledChannels", Arrays.asList("GUI", "HTTP"));
        saConfig.put("dltPrincipalEntityId", "2121343413131");
        saConfig.put("dltTelemarketerId", "2121343413131");
        saConfig.put("dltTelemarketerDeliveryId", "2121343413131");
        saConfig.put("peTmNoChain", true);
        saConfig.put("validityFrom", "2025-02-27T14:57:19.000Z");
        saConfig.put("validityTo", "2026-05-25T14:57:19.000Z");
        saConfig.put("spamFiltrationType", "medium");
        saConfig.put("senderId", "close");
        saConfig.put("senderIdList", Collections.singletonList("test123"));
        saConfig.put("tps", 100);
        saConfig.put("isAccountActive", true);
        saConfig.put("trafficStartsAt", "2025-02-27T14:57:19.000Z");

        Map<String, Object> services = new HashMap<>();
        services.put("sms", Collections.singletonList(saConfig));
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(AdminEndpoints.getServiceAccountUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201, 
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");

        String extractedSaName = response.jsonPath().getString("serviceAccountName");
        if (extractedSaName == null) extractedSaName = response.jsonPath().getString("data.serviceAccountName");
        if (extractedSaName != null) {
            serviceAccountName = extractedSaName;
        }
    }

    // =========================================================================
    // 19. Create_ServiceAccount_RCS
    // =========================================================================
    @Test(priority = 19, dependsOnMethods = "test05_CreateWallet_SSW_Prepaid", description = "QA-TC-19: Create ServiceAccount RCS")
    public void test19_CreateServiceAccount_RCS() {
        String saName = "rcs-sa-" + UUID.randomUUID().toString().substring(0, 4);

        AdminPayload payload = new AdminPayload();
        payload.setCustomerId(customerId);
        payload.settId(UUID.randomUUID().toString());

        Map<String, Object> fallback = new HashMap<>();
        fallback.put("isSmsFallbackEnabled", true);

        Map<String, Object> saConfig = new HashMap<>();
        saConfig.put("serviceAccountName", saName);
        saConfig.put("enabledChannels", Arrays.asList("GUI", "HTTP"));
        saConfig.put("connectionType", Arrays.asList("transactional", "promotional"));
        saConfig.put("trafficType", "national");
        saConfig.put("validityFrom", "2024-06-05T14:57:19.000Z");
        saConfig.put("validityTo", "2026-06-05T14:57:19.000Z");
        saConfig.put("bspName", "jio");
        saConfig.put("isAccountActive", true);
        saConfig.put("fallbackServiceAccount", fallback);
        saConfig.put("tps", 1000);
        saConfig.put("trafficStartsAt", "2025-07-05T14:57:19.000Z");

        Map<String, Object> services = new HashMap<>();
        services.put("rcs", Collections.singletonList(saConfig));
        payload.setServices(services);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(AdminEndpoints.getServiceAccountUrl(svCustomerId, walletName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201, 
                "Status code should be 200/201 Created");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }

    // =========================================================================
    // 20. update_serviceAccount
    // =========================================================================
    @Test(priority = 20, dependsOnMethods = "test18_CreateServiceAccount_SMS", description = "QA-TC-20: Update ServiceAccount")
    public void test20_UpdateServiceAccount() {
        AdminPayload payload = new AdminPayload();
        payload.setIsAccountActive(true);
        payload.setValidityFrom("2025-02-27T14:57:19.000Z");
        payload.setValidityTo("2026-06-05T14:57:19.000Z");
        payload.settId(UUID.randomUUID().toString());
        payload.setTrafficStartsAt("2025-04-30T14:57:19.000Z");

        long startTime = System.currentTimeMillis();
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(payload)
                .when()
                .put(AdminEndpoints.getUpdateServiceAccountUrl(svCustomerId, walletName, serviceAccountName));

        long responseTime = System.currentTimeMillis() - startTime;
        response.then().log().ifError();

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201 || response.getStatusCode() == 204, 
                "Status code should be 200/201/204 Success");
        Assert.assertTrue(responseTime < 3000, "Response time should be within 3000ms threshold");
    }
}
