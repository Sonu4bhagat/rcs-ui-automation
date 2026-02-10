package api.endpoints;

public class Routes {

    // Base URL will be read from Config or hardcoded here temporarily
    // Ideally: public static String BASE_URL = ConfigReader.get("api_url");
    // For now assuming a pattern based on UI URL
    public static String BASE_URL = "https://stagingvault.smartping.io";

    // Auth Module
    public static String LOGIN_URL = BASE_URL + "/login";
    public static String LOGOUT_URL = BASE_URL + "/logout";

    // Services Module
    public static String SERVICES_LIST_URL = BASE_URL + "/services";
    public static String SERVICE_DETAILS_URL = BASE_URL + "/services/{id}";

    // Wallet Module
    public static String WALLET_BALANCE_URL = BASE_URL + "/wallet/balance";

    // Customer Module (Admin)
    public static String CUSTOMERS_URL = BASE_URL + "/admin/customers";

    // ============================================
    // NEW ADMIN API ROUTES (Provisioning / Backend)
    // ============================================
    public static String ADMIN_BASE_URL = "https://stagingbackendvault.smartping.io";
}
