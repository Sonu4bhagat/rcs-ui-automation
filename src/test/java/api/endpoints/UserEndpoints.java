package api.endpoints;

import api.payloads.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserEndpoints {

    public static Response loginUser(User payload) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post(Routes.LOGIN_URL);
    }

    // Placeholder for other User-related endpoints
    // public static Response createUser(User payload) { ... }
}
