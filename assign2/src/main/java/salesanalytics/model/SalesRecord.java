package salesanalytics.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Immutable domain object representing a single sales record.
 */
public class SalesRecord {
    private final String orderId;
    private final LocalDate date;
    private final String region;
    private final String salesPerson;
    private final String product;
    private final String category;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal discount;
    private final BigDecimal costPrice;

    public SalesRecord(String orderId, LocalDate date, String region, String salesPerson, String product,
            String category, int quantity, BigDecimal unitPrice, BigDecimal discount, BigDecimal costPrice) {
        this.orderId = orderId;
        this.date = date;
        this.region = region;
        this.salesPerson = salesPerson;
        this.product = product;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.costPrice = costPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getRegion() {
        return region;
    }

    public String getSalesPerson() {
        return salesPerson;
    }

    public String getProduct() {
        return product;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    // --- Computed Metrics ---

    public BigDecimal getNetUnitPrice() {
        return unitPrice.multiply(BigDecimal.ONE.subtract(discount));
    }

    public BigDecimal getTotalRevenue() {
        return getNetUnitPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalCost() {
        return costPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getProfit() {
        return getTotalRevenue().subtract(getTotalCost());
    }

    public double getProfitMargin() {
        BigDecimal revenue = getTotalRevenue();
        if (revenue.compareTo(BigDecimal.ZERO) == 0)
            return 0.0;
        return getProfit().divide(revenue, 4, java.math.RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public String toString() {
        return "SalesRecord{" +
                "orderId='" + orderId + '\'' +
                ", date=" + date +
                ", product='" + product + '\'' +
                ", profit=" + getProfit() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SalesRecord that = (SalesRecord) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
