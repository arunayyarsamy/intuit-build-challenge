package salesanalytics;

import salesanalytics.io.CsvSalesRecordLoader;
import salesanalytics.model.SalesRecord;
import salesanalytics.service.SalesAnalyticsService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Main application for generating sales analytics reports.
 */
public class SalesAnalyticsApplication {

    private static final String DEFAULT_CSV_PATH = "data/sales_2050_rows.csv";

    public static void main(String[] args) {
        String csvPath = args.length > 0 ? args[0] : DEFAULT_CSV_PATH;

        System.out.println("Starting Sales Analytics Application...");
        System.out.println("Loading data from: " + csvPath);

        CsvSalesRecordLoader loader = new CsvSalesRecordLoader();
        SalesAnalyticsService service = new SalesAnalyticsService();

        try {
            long startTime = System.currentTimeMillis();
            List<SalesRecord> records = loader.load(csvPath);
            long loadTime = System.currentTimeMillis() - startTime;

            System.out.println("Loaded " + records.size() + " records in " + loadTime + " ms.");
            System.out.println("--------------------------------------------------");

            // 1. Total Revenue
            BigDecimal totalRevenue = service.calculateTotalRevenue(records);
            System.out.println("Total Revenue: " + totalRevenue);

            // 2. Total Profit
            BigDecimal totalProfit = service.calculateTotalProfit(records);
            System.out.println("Total Profit: " + totalProfit);

            // 3. Orders by Region
            System.out.println("\nOrders by Region:");
            Map<String, Long> ordersByRegion = service.countOrdersByRegion(records);
            ordersByRegion.forEach((region, count) -> System.out.println("  " + region + ": " + count));

            // 4. Revenue by Region
            System.out.println("\nRevenue by Region:");
            Map<String, BigDecimal> revenueByRegion = service.calculateRevenueByRegion(records);
            revenueByRegion.forEach((region, revenue) -> System.out.println("  " + region + ": " + revenue));

            // 5. Revenue by Category
            System.out.println("\nRevenue by Category:");
            Map<String, BigDecimal> revenueByCategory = service.calculateRevenueByCategory(records);
            revenueByCategory.forEach((cat, revenue) -> System.out.println("  " + cat + ": " + revenue));

            // 6. Revenue by Month
            System.out.println("\nRevenue by Month:");
            Map<java.time.Month, BigDecimal> revenueByMonth = service.calculateRevenueByMonth(records);
            revenueByMonth.forEach((month, revenue) -> System.out.println("  " + month + ": " + revenue));

            // 7. Average Profit Margin
            double avgMargin = service.calculateAverageProfitMargin(records);
            System.out.printf("\nAverage Profit Margin: %.2f%%%n", avgMargin * 100);

            // 8. Top 5 Products
            System.out.println("\nTop 5 Products by Revenue:");
            List<String> topProducts = service.getTopNProductsByRevenue(records, 5);
            topProducts.forEach(prod -> System.out.println("  - " + prod));

            System.out.println("--------------------------------------------------");
            System.out.println("Analysis Complete.");

        } catch (IOException e) {
            System.err.println("Error loading CSV file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
