package com.Jan.webshop_api.service;

import com.Jan.webshop_api.model.EnrichedProduct;
import com.Jan.webshop_api.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final double FEE_RATE = 1.10;

    // Autowire the new String template ---
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Autowire ObjectMapper to parse JSON ---
    @Autowired
    private ObjectMapper objectMapper;

    public List<EnrichedProduct> getEnrichedProducts(String currencyCode) throws Exception { // <-- Add 'throws'

        // Read plain strings from Redis ---
        String productsJson = redisTemplate.opsForValue().get(SchedulingService.PRODUCTS_CACHE_KEY);
        String ratesJson = redisTemplate.opsForValue().get(SchedulingService.RATES_CACHE_KEY);

        if (productsJson == null || ratesJson == null) {
            System.out.println("Cache is empty!");
            return List.of();
        }

        // Manually parse the JSON strings back into objects ---
        List<Product> products = objectMapper.readValue(productsJson, new TypeReference<List<Product>>() {});
        Map<String, Object> rates = objectMapper.readValue(ratesJson, new TypeReference<Map<String, Object>>() {});


        // Get the conversion rate
        Map<String, Object> eurRates = (Map<String, Object>) rates.get("eur");
        String key = currencyCode.toLowerCase();
        double conversionRate = 1.0;
        if (eurRates.containsKey(key)) {
            conversionRate = ((Number) eurRates.get(key)).doubleValue();
        }

        // Loop and convert
        final double finalConversionRate = conversionRate;
        return products.stream()
                .map(product -> convertToEnriched(product, finalConversionRate, currencyCode))
                .collect(Collectors.toList());
    }

    private EnrichedProduct convertToEnriched(Product product, double conversionRate, String currencyCode) {
        double originalEurPrice = product.getPrice();
        double salePriceEur = originalEurPrice * FEE_RATE;
        EnrichedProduct enriched = new EnrichedProduct();
        enriched.setId(product.getId());
        enriched.setTitle(product.getTitle());
        enriched.setDescription(product.getDescription());
        enriched.setCategory(product.getCategory());
        enriched.setImage(product.getImage());
        enriched.setPurchasePrice(originalEurPrice * conversionRate);
        enriched.setSalePrice(salePriceEur * conversionRate);
        enriched.setCurrency(currencyCode.toUpperCase());
        return enriched;
    }
}