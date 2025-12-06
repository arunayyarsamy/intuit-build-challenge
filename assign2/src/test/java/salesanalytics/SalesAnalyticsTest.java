package salesanalytics;

import salesanalytics.model.SalesRecord;
import salesanalytics.service.SalesAnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for SalesAnalyticsService.
 */
class SalesAnalyticsTest {

    private SalesAnalyticsService service;
    private List<SalesRecord> testRecords;

    @BeforeEach
    void setUp() {
        service = new SalesAnalyticsService();
        testRecords = Arrays.asList(
                // OrderId, Date, Region, SalesPerson, Product, Category, Quantity, UnitPrice,
                // Discount, CostPrice
                new SalesRecord("1", LocalDate.now(), "East", "Alice", "Laptop", "Electronics", 2,
                        new BigDecimal("1000"), new BigDecimal("0.1"), new BigDecimal("800")),
                // NetPrice=900, Rev=1800, Cost=1600, Profit=200

                new SalesRecord("2", LocalDate.now(), "West", "Bob", "Mouse", "Accessories", 10, new BigDecimal("20"),
                        new BigDecimal("0.0"), new BigDecimal("10")),
                // NetPrice=20, Rev=200, Cost=100, Profit=100

                new SalesRecord("3", LocalDate.now(), "East", "Charlie", "Laptop", "Electronics", 1,
                        new BigDecimal("1200"), new BigDecimal("0.0"), new BigDecimal("900"))
        // NetPrice=1200, Rev=1200, Cost=900, Profit=300
        );
    }

    @Test
    void testCalculateTotalRevenue() {
        // 1800 + 200 + 1200 = 3200
        BigDecimal revenue = service.calculateTotalRevenue(testRecords);
        assertEquals(new BigDecimal("3200.0").doubleValue(), revenue.doubleValue(), 0.01);
    }

    @Test
    void testCalculateTotalProfit() {
        // 200 + 100 + 300 = 600
        BigDecimal profit = service.calculateTotalProfit(testRecords);
        assertEquals(new BigDecimal("600.0").doubleValue(), profit.doubleValue(), 0.01);
    }

    @Test
    void testCountOrdersByRegion() {
        Map<String, Long> counts = service.countOrdersByRegion(testRecords);
        assertEquals(2, counts.get("East"));
        assertEquals(1, counts.get("West"));
    }

    @Test
    void testCalculateRevenueByRegion() {
        // East: 1800 + 1200 = 3000
        // West: 200
        Map<String, BigDecimal> revByRegion = service.calculateRevenueByRegion(testRecords);
        assertEquals(new BigDecimal("3000.0").doubleValue(), revByRegion.get("East").doubleValue(), 0.01);
        assertEquals(new BigDecimal("200.0").doubleValue(), revByRegion.get("West").doubleValue(), 0.01);
    }

    @Test
    void testTopNProducts() {
        // Laptop: 1800 + 1200 = 3000
        // Mouse: 200
        List<String> top = service.getTopNProductsByRevenue(testRecords, 1);
        assertEquals(1, top.size());
        assertEquals("Laptop", top.get(0));
    }

    @Test
    void testCalculateRevenueByCategory() {
        // Electronics: 1800 + 1200 = 3000
        // Accessories: 200
        Map<String, BigDecimal> revByCat = service.calculateRevenueByCategory(testRecords);
        assertEquals(new BigDecimal("3000.0").doubleValue(), revByCat.get("Electronics").doubleValue(), 0.01);
        assertEquals(new BigDecimal("200.0").doubleValue(), revByCat.get("Accessories").doubleValue(), 0.01);
    }

    @Test
    void testCalculateRevenueBySalesPerson() {
        // Alice: 1800
        // Bob: 200
        // Charlie: 1200
        Map<String, BigDecimal> revByPerson = service.calculateRevenueBySalesPerson(testRecords);
        assertEquals(new BigDecimal("1800.0").doubleValue(), revByPerson.get("Alice").doubleValue(), 0.01);
        assertEquals(new BigDecimal("200.0").doubleValue(), revByPerson.get("Bob").doubleValue(), 0.01);
        assertEquals(new BigDecimal("1200.0").doubleValue(), revByPerson.get("Charlie").doubleValue(), 0.01);
    }

    @Test
    void testCalculateRevenueByMonth() {
        // All test records are LocalDate.now() -> Current Month
        Map<java.time.Month, BigDecimal> revByMonth = service.calculateRevenueByMonth(testRecords);
        java.time.Month currentMonth = LocalDate.now().getMonth();
        assertEquals(new BigDecimal("3200.0").doubleValue(), revByMonth.get(currentMonth).doubleValue(), 0.01);
    }

    @Test
    void testCalculateAverageProfitMargin() {
        // Record 1: Rev=1800, Profit=200 -> Margin = 200/1800 = 0.1111
        // Record 2: Rev=200, Profit=100 -> Margin = 100/200 = 0.5000
        // Record 3: Rev=1200, Profit=300 -> Margin = 300/1200 = 0.2500
        // Avg = (0.1111 + 0.5000 + 0.2500) / 3 = 0.86111... / 3 = 0.287037...
        double avgMargin = service.calculateAverageProfitMargin(testRecords);
        assertEquals(0.2870, avgMargin, 0.001);
    }
}
