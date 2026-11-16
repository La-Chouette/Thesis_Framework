package de.thd.thesis;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Testcontainers
class EShopOnWebApiTest {

    private static final String API_SERVICE = "eshoppublicapi-1";
    private static final int API_PORT = 8080;

    // Starts eShopOnWeb (web, API, database) from its compose files
    @SuppressWarnings("resource")
    @Container
        static final ComposeContainer ESHOP = new ComposeContainer(
            new File("../eShopOnWeb/docker-compose.yml"),
            new File("../eShopOnWeb/docker-compose.override.yml"))
        .withExposedService(API_SERVICE, API_PORT,
            Wait.forHttp("/api/catalog-items")
                .forStatusCode(200)
                .withStartupTimeout(Duration.ofMinutes(5)));

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
        String host = ESHOP.getServiceHost(API_SERVICE, API_PORT);
        int port = ESHOP.getServicePort(API_SERVICE, API_PORT);
        RestAssured.baseURI = "http://" + host + ":" + port;
    }

    @Test
    void catalogListReturnsProducts() {
        given()
        .when()
            .get("/api/catalog-items")
        .then()
            .statusCode(200)
            .body("catalogItems", not(empty()));
    }

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