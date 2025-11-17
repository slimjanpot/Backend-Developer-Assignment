package com.Jan.webshop_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
// ---

import com.Jan.webshop_api.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class SchedulingService implements CommandLineRunner {

    public static final String PRODUCTS_CACHE_KEY = "products_cache";
    public static final String RATES_CACHE_KEY = "rates_cache";
    
    private final String PRODUCT_API_URL = "https://fakestoreapi.com/products";
    private final String RATES_API_URL = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json";

    @Autowired
    private RestTemplate restTemplate;

    // Autowire the new String template ---
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Add an ObjectMapper to convert to JSON ---
    @Autowired
    private ObjectMapper objectMapper;


    // It clears the cache. ---
    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- [STARTUP] Clearing entire Redis cache... ---");
        redisTemplate.execute((RedisConnection connection) -> {
            connection.serverCommands().flushAll();
            return null;
        });
        System.out.println("--- [STARTUP] Cache cleared. ---");
        
        System.out.println("--- [STARTUP] Populating cache... ---");
        fetchAndCacheProducts();
        fetchAndCacheRates();
        System.out.println("--- [STARTUP] Cache populated. Application ready. ---");
    }

    
    // Save a STRING Products ---
    @Scheduled(initialDelay = 300000, fixedRate = 300000)
    public void fetchAndCacheProducts() {
        try {
            System.out.println("Fetching products from API...");
            Product[] products = restTemplate.getForObject(PRODUCT_API_URL, Product[].class);
            
            if (products != null) {
                // Manually convert the product list to a JSON string
                String productsJson = objectMapper.writeValueAsString(List.of(products));
                
                // Save the plain string to Redis
                redisTemplate.opsForValue().set(PRODUCTS_CACHE_KEY, productsJson);
                System.out.println("Successfully cached " + products.length + " products as JSON string.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching or caching products: " + e.getMessage());
        }
    }

    // Save a STRING Rates ---
    @Scheduled(initialDelay = 300000, fixedRate = 300000)
    public void fetchAndCacheRates() {
        try {
            System.out.println("Fetching conversion rates from API...");
            Map<String, Object> rates = restTemplate.getForObject(RATES_API_URL, Map.class);
            
            if (rates != null) {
                // Manually convert the rates map to a JSON string
                String ratesJson = objectMapper.writeValueAsString(rates);
                
                // Save the plain string to Redis
                redisTemplate.opsForValue().set(RATES_CACHE_KEY, ratesJson);
                System.out.println("Successfully cached conversion rates as JSON string.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching or caching rates: " + e.getMessage());
        }
    }
}
