# DataAggregator

## Overview

The `DataAggregator` is a custom data structure designed to solve problems related to **high-frequency event updates** in systems that need to **aggregate data** before emitting it. It is particularly useful in **low-latency, high-throughput domains** where updates are frequent, and the consumer struggles to keep up with the incoming data.

### Problem Statement

In domains like **stock market pricing**, **IoT sensor networks**, and **real-time data feeds**, systems are required to process and aggregate large volumes of data in real-time. These systems often face challenges in:

1. **High Throughput:** The system receives a high volume of updates per second (e.g., stock prices or sensor data).
2. **Latency Concerns:** Data must be processed quickly, and the consumer system may not be able to keep up with the incoming rate of updates.
3. **Efficient Aggregation:** Multiple updates for the same entity (e.g., stock symbol, sensor ID) need to be aggregated over a time window.
4. **Avoiding Redundant Emissions:** Frequent updates may cause unnecessary emissions, which can overwhelm the receiving system.

### How We Solved It

We designed a custom `DataAggregator` class to address these challenges efficiently. It operates by:

1. **Buffering Updates:** Incoming updates are initially buffered in a `ConcurrentHashMap`, preventing data loss and providing a fast way to accumulate values.
2. **Delayed Aggregation:** We use a `PriorityBlockingQueue` to manage the timing of data aggregation. Once a configurable delay (e.g., 50 milliseconds) has passed, the system aggregates the data and processes it.
3. **Reducing Emissions:** By aggregating data for a given key (e.g., stock symbol), we limit the number of emissions to the consumer system, preventing it from being overwhelmed by frequent updates.
4. **Concurrent Processing:** The system supports concurrent data updates and processing, ensuring that it can handle high throughput scenarios efficiently.
5. **Custom Aggregation Logic:** The aggregation logic is flexible, allowing users to define how data for the same key should be combined (e.g., summing prices, averaging values, etc.).

This design enables the system to **collect data in batches**, **reduce the emission frequency**, and **minimize the load on downstream systems**, all while maintaining low latency and high throughput.

---

## Key Features

- **Concurrency Support:** Efficient handling of concurrent updates and processing, making it suitable for multi-threaded environments.
- **Custom Aggregation Logic:** Users can define custom aggregation functions (e.g., sum, average, etc.) for different use cases.
- **Automatic Timed Processing:** Updates are processed after a configurable delay (e.g., 50ms), allowing for efficient batch processing.
- **Efficient Data Emission:** By limiting emissions based on timed aggregation, the system prevents overwhelming the receiver with redundant updates.
- **Priority Queue for Timed Processing:** Uses a `PriorityBlockingQueue` to handle the timing and ordering of data aggregation efficiently.

---

## How It Works

### 1. **Data Ingestion**
- Data updates are added using the `addData(K key, V value)` method, where `K` is the unique key (e.g., stock symbol, sensor ID) and `V` is the data payload (e.g., price, sensor reading).
- The system uses a `ConcurrentHashMap` to store data, ensuring thread safety.

### 2. **Delayed Processing**
- Each data update is added to a `PriorityBlockingQueue` with a delay time.
- The system periodically checks the queue and processes keys when the configured delay time has passed.

### 3. **Aggregation**
- Aggregation logic can be customized. For example, you could sum up the prices for a given stock symbol or average sensor readings.
- This reduces the volume of data sent to the receiver while still providing meaningful, aggregated results.

### 4. **Consumer Processing**
- After data is aggregated, a `DataProcessor` processes the results. This could involve emitting the aggregated data to a different part of the system or saving it to a database.

---

## Example Use Case: Stock Price Updates

Imagine a stock trading system that receives stock price updates every few milliseconds. The system needs to:

1. **Aggregate Stock Prices:** When multiple price updates for a single stock are received within a short time frame, the system should aggregate them (e.g., sum, average) to avoid overwhelming the consumer.
2. **Control Update Rate:** The system should emit aggregated results periodically, rather than emitting each individual price update.

### Example Code

```java
StockPriceAggregator stockPriceAggregator = new StockPriceAggregator();

// Add stock price updates
stockPriceAggregator.addStockPriceUpdate("AAPL", 150.0);
stockPriceAggregator.addStockPriceUpdate("AAPL", 155.0);
stockPriceAggregator.addStockPriceUpdate("GOOGL", 2800.0);

// Sleep to allow time for aggregation
Thread.sleep(100);
