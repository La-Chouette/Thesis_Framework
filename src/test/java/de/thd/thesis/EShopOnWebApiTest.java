package de.thd.thesis;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class EShopOnWebApiTest {

    private static final String ADMIN_LOGIN = """
        { "username": "admin@microsoft.com", "password": "Pass@word1" }
        """;

    private static final String NEW_ITEM = """
        {
          "catalogBrandId": 2,
          "catalogTypeId": 1,
          "name": "Test Mug",
          "description": "A mug created by an automated test",
          "price": 9.99
        }
        """;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:5200";
    }

    // 1. Reading the product list works without login
    @Test
    void catalogListReturnsProducts() {
        given()
        .when()
            .get("/api/catalog-items")
        .then()
            .statusCode(200)
            .body("catalogItems", not(empty()));
    }

    // 2. Admin login returns a token
    @Test
    void adminLoginReturnsToken() {
        given()
            .contentType(ContentType.JSON)
            .body(ADMIN_LOGIN)
        .when()
            .post("/api/authenticate")
        .then()
            .statusCode(200)
            .body("token", not(emptyOrNullString()));
    }

    // 3. Creating a product WITHOUT a token is refused
    @Test
    void createItemWithoutTokenReturns401() {
        given()
            .contentType(ContentType.JSON)
            .body(NEW_ITEM)
        .when()
            .post("/api/catalog-items")
        .then()
            .statusCode(401);
    }

    // 4. Creating a product WITH a token works
    @Test
    void createItemWithAdminTokenSucceeds() {
        String token =
            given()
                .contentType(ContentType.JSON)
                .body(ADMIN_LOGIN)
            .when()
                .post("/api/authenticate")
            .then()
                .statusCode(200)
                .extract().path("token");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(NEW_ITEM)
        .when()
            .post("/api/catalog-items")
        .then()
            .statusCode(anyOf(is(200), is(201)));
    }
}