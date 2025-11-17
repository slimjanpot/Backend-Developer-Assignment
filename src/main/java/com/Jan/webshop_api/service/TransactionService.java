package com.Jan.webshop_api.service;

import com.Jan.webshop_api.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final double FEE_RATE = 1.10;

    // Autowire the new String template ---
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Autowire ObjectMapper to parse JSON ---
    @Autowired
    private ObjectMapper objectMapper;

    public TransactionReceipt createTransaction(TransactionRequest request) {

        try { 

            // Read plain strings from Redis ---
            String productsJson = redisTemplate.opsForValue().get(SchedulingService.PRODUCTS_CACHE_KEY);
            String ratesJson = redisTemplate.opsForValue().get(SchedulingService.RATES_CACHE_KEY);

            if (productsJson == null || ratesJson == null) {
                throw new RuntimeException("Service is not ready, cache is empty.");
            }

            // Manually parse the JSON strings back into objects ---
            List<Product> allProducts = objectMapper.readValue(productsJson, new TypeReference<List<Product>>() {});
            Map<String, Object> rates = objectMapper.readValue(ratesJson, new TypeReference<Map<String, Object>>() {});

            // Get the conversion rate
            Map<String, Object> eurRates = (Map<String, Object>) rates.get("eur");
            String key = request.getCurrency().toLowerCase();
            double conversionRate = 1.0;
            if (eurRates.containsKey(key)) {
                conversionRate = ((Number) eurRates.get(key)).doubleValue();
            }

            // Process the transaction
            double totalPurchasePrice = 0;
            double totalSalePrice = 0;
            int totalItems = 0;
            
            Map<Long, Product> productMap = allProducts.stream()
                    .collect(Collectors.toMap(Product::getId, product -> product));

            List<Product> purchasedProducts = new java.util.ArrayList<>();
            
            for (CartItem item : request.getItems()) {
                Product product = productMap.get(item.getProductId());
                if (product == null) {
                    continue; 
                }
                int quantity = item.getQuantity();
                double originalEurPrice = product.getPrice();
                totalPurchasePrice += (originalEurPrice * quantity);
                totalSalePrice += (originalEurPrice * FEE_RATE * quantity);
                totalItems += quantity;
                purchasedProducts.add(product);
            }

            // Build the final receipt
            TransactionReceipt receipt = new TransactionReceipt();
            receipt.setCurrency(request.getCurrency().toUpperCase());
            receipt.setTotalItems(totalItems);
            receipt.setProductsPurchased(purchasedProducts);
            receipt.setTotalPurchasePrice(roundToTwoDecimals(totalPurchasePrice * conversionRate));
            receipt.setTotalSalePrice(roundToTwoDecimals(totalSalePrice * conversionRate));

            return receipt;

        } catch (Exception e) {
            // If the JSON parsing fails, throw an error
            throw new RuntimeException("Could not create transaction: " + e.getMessage(), e);
        }
    }
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}