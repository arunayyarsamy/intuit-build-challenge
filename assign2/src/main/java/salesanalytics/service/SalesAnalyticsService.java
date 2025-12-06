package salesanalytics.service;

import salesanalytics.model.SalesRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for performing analytics on sales data using Java Streams.
 */
public class SalesAnalyticsService {

        public BigDecimal calculateTotalRevenue(List<SalesRecord> records) {
                return records.parallelStream()
                                .map(SalesRecord::getTotalRevenue)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        public BigDecimal calculateTotalProfit(List<SalesRecord> records) {
                return records.parallelStream()
                                .map(SalesRecord::getProfit)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        public Map<String, Long> countOrdersByRegion(List<SalesRecord> records) {
                return records.parallelStream()
                                .collect(Collectors.groupingBy(SalesRecord::getRegion, Collectors.counting()));
        }

        public Map<String, BigDecimal> calculateRevenueByRegion(List<SalesRecord> records) {
                return records.parallelStream()
                                .collect(Collectors.groupingBy(
                                                SalesRecord::getRegion,
                                                Collectors.mapping(SalesRecord::getTotalRevenue,
                                                                Collectors.reducing(BigDecimal.ZERO,
                                                                                BigDecimal::add))));
        }

        public Map<String, BigDecimal> calculateRevenueByCategory(List<SalesRecord> records) {
                return records.parallelStream()
                                .collect(Collectors.groupingBy(
                                                SalesRecord::getCategory,
                                                Collectors.mapping(SalesRecord::getTotalRevenue,
                                                                Collectors.reducing(BigDecimal.ZERO,
                                                                                BigDecimal::add))));
        }

        public Map<String, BigDecimal> calculateRevenueBySalesPerson(List<SalesRecord> records) {
                return records.parallelStream()
                                .collect(Collectors.groupingBy(
                                                SalesRecord::getSalesPerson,
                                                Collectors.mapping(SalesRecord::getTotalRevenue,
                                                                Collectors.reducing(BigDecimal.ZERO,
                                                                                BigDecimal::add))));
        }

        public List<String> getTopNProductsByRevenue(List<SalesRecord> records, int n) {
                // Group by product, sum revenue, sort descending, limit n, extract names
                Map<String, BigDecimal> productRevenue = records.parallelStream()
                                .collect(Collectors.groupingBy(
                                                SalesRecord::getProduct,
                                                Collectors.mapping(SalesRecord::getTotalRevenue,
                                                                Collectors.reducing(BigDecimal.ZERO,
                                                                                BigDecimal::add))));

                return productRevenue.entrySet().stream()
                                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                                .limit(n)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());
        }

        public Map<java.time.Month, BigDecimal> calculateRevenueByMonth(List<SalesRecord> records) {
                return records.parallelStream()
                                .collect(Collectors.groupingBy(
                                                record -> record.getDate().getMonth(),
                                                Collectors.mapping(SalesRecord::getTotalRevenue,
                                                                Collectors.reducing(BigDecimal.ZERO,
                                                                                BigDecimal::add))));
        }

        public double calculateAverageProfitMargin(List<SalesRecord> records) {
                return records.parallelStream()
                                .mapToDouble(SalesRecord::getProfitMargin)
                                .average()
                                .orElse(0.0);
        }
}
