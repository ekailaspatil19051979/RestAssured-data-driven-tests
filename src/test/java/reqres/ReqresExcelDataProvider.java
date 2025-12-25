package reqres;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReqresExcelDataProvider {
    @DataProvider(name = "reqresUsersData")
    public static Object[][] usersData() throws IOException {
        return readSheet("src/test/resources/reqres/reqres_testdata.xlsx", "users");
    }

    @DataProvider(name = "reqresGetUsersData")
    public static Object[][] getUsersData() throws IOException {
        return readSheet("src/test/resources/reqres/reqres_testdata.xlsx", "get_users");
    }

    private static Object[][] readSheet(String filePath, String sheetName) throws IOException {
        FileInputStream fis = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetName);
        int rowCount = sheet.getPhysicalNumberOfRows();
        int colCount = sheet.getRow(0).getPhysicalNumberOfCells();
        List<Object[]> data = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // skip header
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Object[] rowData = new Object[colCount];
            for (int j = 0; j < colCount; j++) {
                Cell cell = row.getCell(j);
                rowData[j] = cell != null ? cell.toString() : "";
            }
            data.add(rowData);
        }
        workbook.close();
        fis.close();
        return data.toArray(new Object[0][0]);
    }
}
