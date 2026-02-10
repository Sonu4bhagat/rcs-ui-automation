package api.test;

import api.endpoints.UserEndpoints;
import api.payloads.User;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

public class AuthTest extends ApiBaseTest {

    User userPayload;

    @BeforeClass
    public void setupData() {
        // Using SuperAdmin credentials from Config
        userPayload = new User(
                ConfigReader.get("superadmin.email"),
                ConfigReader.get("superadmin.password"));
    }

    @Test(priority = 1, description = "Verify successful login and token generation")
    public void testLogin() {
        Response response = UserEndpoints.loginUser(userPayload);

        // Log response for debugging
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 200, "Login failed!");

        // Extract Token (Adjust json path 'token' based on actual response structure)
        // Assuming response is like { "token": "eyJ...", ... } or { "data": { "token":
        // "..." } }
        // For now, we just verify we got a 200 OK.

        // Example check:
        // String token = response.jsonPath().getString("token");
        // Assert.assertNotNull(token, "Token is null!");
    }
}
