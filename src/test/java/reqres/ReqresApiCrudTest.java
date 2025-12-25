package reqres;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Step;
import io.qameta.allure.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

public class ReqresApiCrudTest {

    private static final Logger logger = LoggerFactory.getLogger(ReqresApiCrudTest.class);

    @Test(dataProvider = "reqresGetUsersData", dataProviderClass = ReqresExcelDataProvider.class, description = "GET user(s) data-driven")
    @Description("GET user(s) data-driven from Excel")
    public void testGetUsers(String userId, String expectedStatus) {
        String endpoint = (userId == null || userId.isEmpty()) ? "/users" : "/users/" + userId;
        logger.info("GET request to endpoint: {}", endpoint);
        Response response = given()
                .baseUri("https://reqres.in/api")
                .when()
                .get(endpoint);
        logger.info("GET response: {}", response.asString());
        response.then().statusCode(Integer.parseInt(expectedStatus));
        if (userId != null && !userId.isEmpty() && expectedStatus.equals("200")) {
            Assert.assertEquals(response.jsonPath().getString("data.id"), userId);
        }
    }

    @Test(dataProvider = "reqresUsersData", dataProviderClass = ReqresExcelDataProvider.class, description = "CRUD user data-driven")
    @Description("CRUD user data-driven from Excel")
    public void testUserCrud(String name, String job, String expectedStatus, String method, String userId) {
        String endpoint = "/users" + ((userId != null && !userId.isEmpty()) ? "/" + userId : "");
        String payload = null;
        if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) {
            payload = String.format("{\"name\": \"%s\", \"job\": \"%s\"}", name, job);
        }
        logger.info("{} request to endpoint: {} with payload: {}", method, endpoint, payload);
        Response response;
        switch (method.toUpperCase()) {
            case "POST":
                response = given()
                        .baseUri("https://reqres.in/api")
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .when()
                        .post(endpoint);
                break;
            case "PUT":
                response = given()
                        .baseUri("https://reqres.in/api")
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .when()
                        .put(endpoint);
                break;
            case "DELETE":
                response = given()
                        .baseUri("https://reqres.in/api")
                        .when()
                        .delete(endpoint);
                break;
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
        logger.info("{} response: {}", method, response.asString());
        response.then().statusCode(Integer.parseInt(expectedStatus));
        if (method.equalsIgnoreCase("POST") && expectedStatus.equals("201")) {
            Assert.assertEquals(response.jsonPath().getString("name"), name);
            Assert.assertEquals(response.jsonPath().getString("job"), job);
        }
        if (method.equalsIgnoreCase("PUT") && expectedStatus.equals("200")) {
            Assert.assertEquals(response.jsonPath().getString("name"), name);
            Assert.assertEquals(response.jsonPath().getString("job"), job);
        }
    }
}
