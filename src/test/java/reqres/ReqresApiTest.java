package reqres;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static io.restassured.RestAssured.given;

public class ReqresApiTest {

    @DataProvider(name = "excelData")
    public Object[][] excelDataProvider() throws IOException {
        String excelPath = "src/test/resources/reqres/reqres_testdata.xlsx";
        FileInputStream fis = new FileInputStream(new File(excelPath));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        int rowCount = sheet.getPhysicalNumberOfRows();
        int colCount = sheet.getRow(0).getPhysicalNumberOfCells();
        Object[][] data = new Object[rowCount - 1][colCount];
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // skip header
        int i = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            for (int j = 0; j < colCount; j++) {
                Cell cell = row.getCell(j);
                data[i][j] = cell != null ? cell.toString() : "";
            }
            i++;
        }
        workbook.close();
        fis.close();
        return data;
    }

    @Test(dataProvider = "excelData", description = "Positive: Create user with valid data")
    public void testCreateUserPositive(String name, String job, String expectedStatus) {
        String payload = String.format("{\"name\":\"%s\",\"job\":\"%s\"}", name, job);
        Response response = given()
                .baseUri("https://reqres.in/api")
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/users");
        if ("201".equals(expectedStatus)) {
            response.then().statusCode(201);
            Assert.assertEquals(response.jsonPath().getString("name"), name);
            Assert.assertEquals(response.jsonPath().getString("job"), job);
        } else {
            response.then().statusCode(Integer.parseInt(expectedStatus));
        }
    }

    @Test(description = "Negative: Create user with empty payload")
    public void testCreateUserNegativeEmptyPayload() {
        given()
                .baseUri("https://reqres.in/api")
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/users")
                .then()
                .statusCode(201); // reqres.in still returns 201, but no name/job in response
    }

    @Test(description = "Negative: Create user with missing job field")
    public void testCreateUserNegativeMissingJob() {
        String payload = "{\"name\":\"NoJobUser\"}";
        Response response = given()
                .baseUri("https://reqres.in/api")
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/users");
        response.then().statusCode(201);
        Assert.assertEquals(response.jsonPath().getString("name"), "NoJobUser");
        Assert.assertNull(response.jsonPath().getString("job"));
    }
}
