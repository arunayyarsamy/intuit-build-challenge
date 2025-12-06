# Sales Analytics System

A Java-based sales analytics application that processes CSV data using **Java Streams** and functional programming concepts.

## Features

- **Data Ingestion**: robustly loads sales records from CSV files.
- **Advanced Analytics**:
    - Total Revenue & Profit
    - Revenue by Region, Category, Salesperson, and Month
    - Top N Products by Revenue
    - Average Profit Margin
- **Performance**: Optimized with `parallelStream()` for efficient processing of large datasets.
- **Clean Architecture**: Functional service layer with immutable models.

## Project Structure

```
salesanalytics/
├── io/
│   └── CsvSalesRecordLoader.java   # CSV parsing and validation
├── model/
│   └── SalesRecord.java            # Immutable data model
├── service/
│   └── SalesAnalyticsService.java  # Stream-based analytics logic
└── SalesAnalyticsApplication.java  # Main entry point
```

## How to Build and Run

### Prerequisites
- JDK 17+
- Maven 3.x

### Build

```bash
mvn clean package
```

### Run

```bash
java -cp target/sales-analytics-1.0-SNAPSHOT.jar salesanalytics.SalesAnalyticsApplication
```

By default, the application processes `data/sales_2050_rows.csv`.

## Testing

Run unit tests with Maven:

```bash
mvn test
```
