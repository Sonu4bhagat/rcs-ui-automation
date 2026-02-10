package api.endpoints;

public class RcsEndpoints {

    // RCS / SMS Endpoints
    public static String SEND_SMS_URL = Routes.BASE_URL + "/api/v1/send";
    public static String SEND_OBD_URL = Routes.BASE_URL + "/api/v1/sendObd";
    public static String MULTI_SEND_URL = Routes.BASE_URL + "/api/v1/multiSend";
    public static String ONE_2_ONE_URL = Routes.BASE_URL + "/api/v1/one2One";
    public static String ONE_2_MANY_URL = Routes.BASE_URL + "/api/v1/one2Many";

    // WABA Endpoints
    public static String WABA_CREDENTIALS_URL = Routes.BASE_URL + "/licenses/swagger/credentialsdata";
    public static String WABA_TEMPLATES_URL = Routes.BASE_URL + "/templates/";
    public static String WABA_SAVE_CAMPAIGN_URL = Routes.BASE_URL + "/campaign/save-campaign";
    public static String WABA_SEND_CAMPAIGN_URL = Routes.BASE_URL + "/campaign/send-campaign";

}
