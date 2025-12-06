# Architecture Overview – Sales Analytics (Problem 2)

This document describes the architecture for the **Java-based Sales Analytics** solution built on top of a CSV dataset.  
The goal is to demonstrate clean object-oriented design and **heavy use of Java Streams, lambdas, and aggregations** for analytics.

---

# 1. System Architecture

The system implements a simple, read-only analytics pipeline:

```
CSV File → CsvSalesRecordLoader → List<SalesRecord> → SalesAnalyticsService → Console Output
```

- Input: Local CSV file containing sales records.
- Core model: Immutable `SalesRecord` objects mapped from each CSV row.
- Analytics: Stream-based, functional-style queries over `List<SalesRecord>`.
- Output: Aggregated metrics printed as formatted console reports.

The solution intentionally runs single-threaded; the focus here is **functional programming with Streams**, not concurrency.

---

# 2. Architectural Goals

### ✔️ Clarity & Simplicity  
Small, focused classes with clear responsibilities.

### ✔️ Functional Style  
Use of Java Stream API, lambda expressions, and collectors.

### ✔️ Extensibility  
Easy to add new analytics or replace the data source.

### ✔️ Self-Containment  
The project works entirely with a **local CSV file** (`data/sales_data.csv`).

---

# 3. Data Model

## 3.1 CSV Schema

Source file: `data/sales_data.csv`

```
OrderId,Date,Region,SalesPerson,Product,Category,Quantity,UnitPrice,Discount,CostPrice
101,2024-01-05,East,Alice,Laptop,Electronics,2,1200,0.10,900
102,2024-01-05,West,Bob,Mouse,Accessories,10,25,0.00,15
103,2024-01-10,North,Charlie,Keyboard,Accessories,5,45,0.05,30
```

---

## 3.2 Model: `SalesRecord`

`SalesRecord` is an immutable domain object representing one CSV row.

**Fields include:**

- `orderId : String`  
- `date : LocalDate`  
- `region : String`  
- `salesPerson : String`  
- `product : String`  
- `category : String`  
- `quantity : int`  
- `unitPrice : BigDecimal`  
- `discount : BigDecimal`  
- `costPrice : BigDecimal`

**Derived metrics:**

- `netUnitPrice = unitPrice * (1 - discount)`  
- `totalRevenue = quantity * netUnitPrice`  
- `totalCost = quantity * costPrice`  
- `profit = totalRevenue - totalCost`  
- `profitMargin = profit / totalRevenue`  

---

# 4. Component Architecture

Package root: `salesanalytics`

## 4.1 Model Layer

### `SalesRecord`
The core domain object with getters and computed metric methods.

---

## 4.2 I/O Layer

### `CsvSalesRecordLoader`

**Responsibilities:**

- Read CSV file.
- Split each line into fields.
- Parse data into a `SalesRecord`.
- Skip header row.

Uses:
- `Files.lines()`
- Streams to transform lines into model objects.

---

## 4.3 Service Layer

### `SalesAnalyticsService`

**Responsibilities:**

Perform analytics using Java Streams:

- Total Revenue  
- Total Profit  
- Revenue by Region  
- Profit by Region  
- Revenue by Category  
- Revenue by SalesPerson  
- Top N Products by Revenue  
- Revenue by Month  
- Average Profit Margin  

All methods return immutable maps/lists or scalar values.

The service is **pure**:  
`List<SalesRecord> → Stream → Aggregated Output`

---

# 5. Application Layer

### `SalesAnalyticsApplication` (Main)

**Responsibilities:**

1. Initialize configuration (CSV path, top N, locale).  
2. Load `List<SalesRecord>` using `CsvSalesRecordLoader`.  
3. Construct `SalesAnalyticsService`.  
4. Execute analytics queries.  
5. Print results to the console.

This is the orchestration layer connecting all components.

---

# 6. Stream-Based Analytics Architecture

Analytics leverage:

- `map()`  
- `filter()`  
- `collect()`  
- `groupingBy()`  
- `reducing()`  
- `averagingDouble()`  
- `sorted()`  
- `limit()`  

No external state is mutated; everything is expressed as pure transformations.

---

# 7. Extensibility

### Easy extensions include:

- Add new computed metrics (e.g., average discount).
- Add analytics per salesperson, per product, per time period.
- Swap CSV loader with a database-backed loader.
- Export aggregated results into new CSV/JSON.

Because analytics depend only on `List<SalesRecord>`, the architecture is flexible.

---

# 8. Design Rationale Summary

- **Pure functional analytics** using Streams.  
- **Immutable model** ensures safety and clarity.  
- **Dedicated loader** isolates I/O concerns.  
- **Service layer** centralizes all business logic.  
- **Main class** handles orchestration.

A clean, maintainable, interview-ready architecture.

---
