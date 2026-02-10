package api.endpoints;

public class AdminEndpoints {

    // Auth
    public static String ADMIN_AUTH_URL = Routes.ADMIN_BASE_URL + "/backend/smartvault/admin/api/v2/auth";

    // Customers
    public static String ADMIN_CUSTOMERS_URL = Routes.ADMIN_BASE_URL + "/backend/smartvault/admin/api/v2/customers";

    // Wallets (Dynamic URLs handled in test or via String format)
    // URL Pattern: /backend/smartvault/admin/api/v2/{svId}/wallets
    public static String ADMIN_WALLET_BASE_URL = Routes.ADMIN_BASE_URL + "/backend/smartvault/admin/api/v2";

    // Service Accounts & Rate Cards
    // URL Pattern: /backend/smartvault/admin/api/v2/{svId}/{walletId}/rate-cards
    // URL Pattern:
    // /backend/smartvault/admin/api/v2/{svId}/{walletId}/service-accounts

    public static String getWalletUrl(String svId) {
        return ADMIN_WALLET_BASE_URL + "/" + svId + "/wallets";
    }

    public static String getRateCardUrl(String svId, String walletId) {
        return ADMIN_WALLET_BASE_URL + "/" + svId + "/" + walletId + "/rate-cards";
    }

    public static String getServiceAccountUrl(String svId, String walletId) {
        return ADMIN_WALLET_BASE_URL + "/" + svId + "/" + walletId + "/service-accounts";
    }

    public static String getUpdateServiceAccountUrl(String svId, String walletId, String saName) {
        return ADMIN_WALLET_BASE_URL + "/" + svId + "/" + walletId + "/service-accounts/" + saName;
    }

    public static String getLicenseUrl(String svId, String walletId, String saId, String licenseId) {
        return ADMIN_WALLET_BASE_URL + "/" + svId + "/" + walletId + "/" + saId + "/licences/" + licenseId;
    }

    public static String getWalletActionUrl(String svId, String walletId) {
        return ADMIN_WALLET_BASE_URL + "/" + svId + "/wallets/" + walletId;
    }
}
