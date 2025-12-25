package utils;

import org.testng.annotations.DataProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CsvDataProvider {
    @DataProvider(name = "csvData")
    public static Iterator<Object[]> csvDataProvider() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/testdata.csv"));
        return lines.stream().skip(1) // skip header
                .map(line -> line.split(","))
                .map(arr -> new Object[]{arr[0], arr[1]})
                .collect(Collectors.toList()).iterator();
    }
}
