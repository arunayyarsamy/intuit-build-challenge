package salesanalytics.io;

import salesanalytics.model.SalesRecord;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads sales records from a CSV file.
 */
public class CsvSalesRecordLoader {

    public List<SalesRecord> load(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }

        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .skip(1) // Skip header
                    .filter(line -> !line.trim().isEmpty())
                    .filter(line -> line.split(",").length == 10)
                    .map(this::parseLine)
                    .collect(Collectors.toList());
        }
    }

    private SalesRecord parseLine(String line) {
        String[] parts = line.split(",");
        // Basic validation or error handling could go here

        // OrderId,Date,Region,SalesPerson,Product,Category,Quantity,UnitPrice,Discount,CostPrice
        String orderId = parts[0].trim();
        LocalDate date = LocalDate.parse(parts[1].trim());
        String region = parts[2].trim();
        String salesPerson = parts[3].trim();
        String product = parts[4].trim();
        String category = parts[5].trim();
        int quantity = Integer.parseInt(parts[6].trim());
        BigDecimal unitPrice = new BigDecimal(parts[7].trim());
        BigDecimal discount = new BigDecimal(parts[8].trim());
        BigDecimal costPrice = new BigDecimal(parts[9].trim());

        return new SalesRecord(orderId, date, region, salesPerson, product, category, quantity, unitPrice, discount,
                costPrice);
    }
}
