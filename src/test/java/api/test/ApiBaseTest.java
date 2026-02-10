package api.test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;

public class ApiBaseTest {

    protected RequestSpecification requestSpec;
    public static String token;

    @BeforeClass
    public void setup() {
        // Initialize RestAssured Base URI from Routes (or Config if preferred)
        // For now, ensuring we pick up the same environment as UI
        RestAssured.baseURI = "https://stagingvault.smartping.io";

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(RestAssured.baseURI)
                .setContentType("application/json")
                .setAccept("application/json")
                .build();
    }

    // Helper to get token if needed explicitly (though we'll likely automate this)
    public void setAuthToken(String authToken) {
        this.token = authToken;
        requestSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .setBaseUri(RestAssured.baseURI)
                .setContentType("application/json")
                .setAccept("application/json")
                .build();
    }
}
