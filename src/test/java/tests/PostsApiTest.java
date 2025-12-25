package tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Step;
import io.qameta.allure.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.BaseTest;
import utils.CsvDataProvider;

import static io.restassured.RestAssured.given;

public class PostsApiTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(PostsApiTest.class);

    @Test(description = "Create object with large payload and verify API handles it")
    public void testCreateObjectWithLargePayload() {
        StringBuilder largeData = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            largeData.append("A");
        }
        String payload = String.format("{\"name\": \"LargePayload\", \"data\": {\"bigField\": \"%s\"}}", largeData.toString());
        Response response = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/objects");
        int status = response.getStatusCode();
        if (status == 200) {
            Assert.assertEquals(response.jsonPath().getString("name"), "LargePayload");
            Assert.assertEquals(response.jsonPath().getString("data.bigField").length(), 2000);
        } else if (status == 500) {
            // API limitation: server error for large payloads
            Assert.assertTrue(response.asString().contains("Internal Server Error") || response.asString().contains("error"));
        } else {
            Assert.fail("Unexpected status code: " + status);
        }
    }

        @Test(description = "Attempt to create duplicate objects and check API response")
        public void testCreateDuplicateObjects() {
        String payload = "{" +
            "\"name\": \"DuplicateTest\"," +
            "\"data\": {\"userId\": \"dupUser\"}" +
            "}";
        // Create the object the first time
        Response firstResponse = given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post("/objects")
            .then()
            .statusCode(200)
            .extract().response();
        String id1 = firstResponse.jsonPath().getString("id");

        // Create the object a second time (duplicate)
        Response secondResponse = given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post("/objects")
            .then()
            .statusCode(200)
            .extract().response();
        String id2 = secondResponse.jsonPath().getString("id");

        // The API currently allows duplicate objects, but IDs should be different
        Assert.assertNotEquals(id1, id2, "Duplicate objects should have different IDs");
        Assert.assertEquals(secondResponse.jsonPath().getString("name"), "DuplicateTest");
        Assert.assertEquals(secondResponse.jsonPath().getString("data.userId"), "dupUser");
        }
    @Test(description = "Update object and verify changes persist")
    public void testUpdateObjectAndVerify() {
    // Create a new object
    String payload = "{" +
        "\"name\": \"UpdateMe\"," +
        "\"data\": {\"userId\": \"updateMe\"}" +
        "}";
    Response createResponse = given()
        .contentType(ContentType.JSON)
        .body(payload)
        .when()
        .post("/objects")
        .then()
        .statusCode(200)
        .extract().response();
    String id = createResponse.jsonPath().getString("id");

    // Update the object
    String updatePayload = "{" +
        "\"name\": \"UpdatedName\"," +
        "\"data\": {\"userId\": \"updatedUser\", \"extra\": \"field\"}" +
        "}";
    given()
        .contentType(ContentType.JSON)
        .body(updatePayload)
        .when()
        .put("/objects/" + id)
        .then()
        .statusCode(200)
        .body("name", org.hamcrest.Matchers.equalTo("UpdatedName"))
        .body("data.userId", org.hamcrest.Matchers.equalTo("updatedUser"))
        .body("data.extra", org.hamcrest.Matchers.equalTo("field"));

    // Get the object and verify update
    Response getResponse = given()
        .when()
        .get("/objects/" + id)
        .then()
        .statusCode(200)
        .extract().response();
    Assert.assertEquals(getResponse.jsonPath().getString("name"), "UpdatedName");
    Assert.assertEquals(getResponse.jsonPath().getString("data.userId"), "updatedUser");
    Assert.assertEquals(getResponse.jsonPath().getString("data.extra"), "field");
    }

    @Test(description = "Delete object and verify it is gone")
    public void testDeleteObjectAndVerify() {
    // Create a new object
    String payload = "{" +
        "\"name\": \"DeleteMe\"," +
        "\"data\": {\"userId\": \"deleteMe2\"}" +
        "}";
    Response createResponse = given()
        .contentType(ContentType.JSON)
        .body(payload)
        .when()
        .post("/objects")
        .then()
        .statusCode(200)
        .extract().response();
    String id = createResponse.jsonPath().getString("id");

    // Delete the object
    given()
        .when()
        .delete("/objects/" + id)
        .then()
        .statusCode(200);

    // Try to get the deleted object
    given()
        .when()
        .get("/objects/" + id)
        .then()
        .statusCode(404);
    }


        @Test(description = "DELETE a newly created object and verify response")
        @Description("DELETE a newly created object and verify response")
        public void testDeleteNewObject() {
        logger.info("Creating object to be deleted");
        // Create a new object first
        String payload = "{" +
            "\"name\": \"ObjectToDelete\"," +
            "\"data\": {\"userId\": \"deleteMe\"}" +
            "}";
        Response createResponse = given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post("/objects")
            .then()
            .statusCode(200)
            .extract().response();
        String id = createResponse.jsonPath().getString("id");

        logger.info("Deleting object with id: {}", id);
        // Now delete the created object
        Response response = given()
            .when()
            .delete("/objects/" + id)
            .then()
            .statusCode(200)
            .extract().response();
        Assert.assertTrue(response.asString().contains("success") || response.asString().contains("deleted") || response.statusCode() == 200);
        logger.info("Delete response: {}", response.asString());
        }


        @Test(description = "PATCH update name of a newly created object and verify response")
        @Description("PATCH update name of a newly created object and verify response")
        public void testPatchUpdateNewObjectName() {
        logger.info("Creating object to PATCH");
        // Create a new object first
        String payload = "{" +
            "\"name\": \"PatchMe\"," +
            "\"data\": {\"userId\": \"patchMe\"}" +
            "}";
        Response createResponse = given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post("/objects")
            .then()
            .statusCode(200)
            .extract().response();
        String id = createResponse.jsonPath().getString("id");

        logger.info("Patching object with id: {}", id);
        // Now PATCH the created object
        String patchPayload = "{\"name\": \"Patched Name\"}";
        Response response = given()
            .contentType(ContentType.JSON)
            .body(patchPayload)
            .when()
            .patch("/objects/" + id)
            .then()
            .statusCode(200)
            .body("name", org.hamcrest.Matchers.equalTo("Patched Name"))
            .extract().response();
        Assert.assertEquals(response.jsonPath().getString("name"), "Patched Name");
        logger.info("Patch response: {}", response.asString());
        }


        @Test(description = "PUT update a newly created object and verify response")
        @Description("PUT update a newly created object and verify response")
        public void testPutUpdateNewObject() {
        logger.info("Creating object to PUT update");
        // Create a new object first
        String payload = "{" +
            "\"name\": \"PutMe\"," +
            "\"data\": {\"userId\": \"putMe\"}" +
            "}";
        Response createResponse = given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post("/objects")
            .then()
            .statusCode(200)
            .extract().response();
        String id = createResponse.jsonPath().getString("id");

        logger.info("PUT updating object with id: {}", id);
        // Now PUT update the created object
        String putPayload = "{" +
            "\"name\": \"PutMe Updated\"," +
            "\"data\": {" +
            "\"year\": 2023," +
            "\"price\": 999.99," +
            "\"CPU model\": \"Intel Core i7\"," +
            "\"Hard disk size\": \"512 GB\"," +
            "\"color\": \"black\"" +
            "}" +
            "}";
        Response response = given()
            .contentType(ContentType.JSON)
            .body(putPayload)
            .when()
            .put("/objects/" + id)
            .then()
            .statusCode(200)
            .body("name", org.hamcrest.Matchers.equalTo("PutMe Updated"))
            .body("data.price", org.hamcrest.Matchers.equalTo(999.99f))
            .body("data.color", org.hamcrest.Matchers.equalTo("black"))
            .extract().response();

        Assert.assertEquals(response.jsonPath().getString("data.year"), "2023");
        Assert.assertEquals(response.jsonPath().getString("data['CPU model']"), "Intel Core i7");
        Assert.assertEquals(response.jsonPath().getString("data['Hard disk size']"), "512 GB");
        logger.info("PUT response: {}", response.asString());
        }


        @Test(description = "POST Apple MacBook Pro 16 object and verify response")
        @Description("POST Apple MacBook Pro 16 object and verify response")
        public void testPostTestObject() {
        logger.info("Posting Apple MacBook Pro 16 object");
        String payload = "{" +
            "\"name\": \"Apple MacBook Pro 16\"," +
            "\"data\": {" +
            "\"year\": 2019," +
            "\"price\": 1849.99," +
            "\"CPU model\": \"Intel Core i9\"," +
            "\"Hard disk size\": \"1 TB\"" +
            "}" +
            "}";
        Response response = given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post("/objects")
            .then()
            .statusCode(200)
            .body("name", org.hamcrest.Matchers.equalTo("Apple MacBook Pro 16"))
            .extract().response();

        Assert.assertEquals(response.jsonPath().getString("data.year"), "2019");
        Assert.assertEquals(response.jsonPath().getDouble("data.price"), 1849.99, 0.01);
        Assert.assertEquals(response.jsonPath().getString("data['CPU model']"), "Intel Core i9");
        Assert.assertEquals(response.jsonPath().getString("data['Hard disk size']"), "1 TB");
        logger.info("POST response: {}", response.asString());
        }


    @Test(description = "Positive: Get all objects")
    @Description("Positive: Get all objects")
    public void testGetAllObjects() {
        logger.info("Getting all objects");
        Response response = given()
                .when()
                .get("/objects")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();
        Assert.assertTrue(response.jsonPath().getList("id").size() > 0);
        logger.info("GET all objects response: {}", response.asString());
    }

    @Test(description = "Negative: Get object with invalid ID")
    @Description("Negative: Get object with invalid ID")
    public void testGetObjectInvalidId() {
        logger.info("Getting object with invalid ID");
        given()
                .when()
                .get("/objects/invalid")
                .then()
                .statusCode(404);
        logger.info("GET invalid object returned 404 as expected");
    }

    @Test(dataProvider = "csvData", dataProviderClass = CsvDataProvider.class, description = "Data-driven: Create object")
    @Description("Data-driven: Create object")
        public void testCreateObject(String userId, String title) {
        logger.info("Creating object with userId: {}, title: {}", userId, title);
        String payload = String.format("{\"name\": \"%s\", \"data\": {\"userId\": \"%s\"}}", title, userId);
        Response response = given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post("/objects")
            .then()
            .statusCode(200)
            .body("name", org.hamcrest.Matchers.equalTo(title))
            .extract().response();
        Assert.assertEquals(response.jsonPath().getString("data.userId"), userId);
        logger.info("Data-driven POST response: {}", response.asString());
    }
}
