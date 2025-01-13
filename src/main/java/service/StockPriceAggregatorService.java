package service;

import ds.DataAggregator;

import java.util.HashMap;
import java.util.Map;

public class StockPriceAggregatorServiceService {
    private final DataAggregator<String, Map<String, Object>> dataAggregator;

    public StockPriceAggregatorServiceService() {
        dataAggregator = new DataAggregator<>(50, this::emitAggregatedStockData, this::aggregateStockData);
    }

    public void addStockPriceUpdate(String stockSymbol, double price) {
        Map<String, Object> stockData = new HashMap<>();
        stockData.put("symbol", stockSymbol);
        stockData.put("price", price);
        stockData.put("timestamp", System.currentTimeMillis());
        dataAggregator.addData(stockSymbol, stockData);
    }

    private void emitAggregatedStockData(String stockSymbol, Map<String, Object> aggregatedData) {
        System.out.println("Aggregated Stock Update: " + stockSymbol + " -> " + aggregatedData);
    }

    private Map<String, Object> aggregateStockData(Map<String, Object> existingData, Map<String, Object> newData) {
        // Custom aggregation logic: summing prices for example
        double existingPrice = (double) existingData.get("price");
        double newPrice = (double) newData.get("price");
        existingData.put("price", existingPrice + newPrice); // Sum prices (you could change this logic)
        existingData.put("timestamp", System.currentTimeMillis()); // Update timestamp
        return existingData;
    }

    public static void main(String[] args) throws InterruptedException {
        StockPriceAggregatorServiceService stockPriceAggregatorService = new StockPriceAggregatorServiceService();

        // Add stock price updates
        stockPriceAggregatorService.addStockPriceUpdate("AAPL", 150.0);
        stockPriceAggregatorService.addStockPriceUpdate("AAPL", 155.0);
        stockPriceAggregatorService.addStockPriceUpdate("GOOGL", 2800.0);

        // Sleep to allow time for aggregation
        Thread.sleep(100);
    }
}