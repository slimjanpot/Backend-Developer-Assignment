package com.Jan.webshop_api.controller;

import com.Jan.webshop_api.model.EnrichedProduct;
import com.Jan.webshop_api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // --- CHANGE 1: Add 'throws Exception' to the method ---
    @GetMapping
    public List<EnrichedProduct> getAllProducts(
            @RequestParam(defaultValue = "EUR") String currency) throws Exception {
        
        return productService.getEnrichedProducts(currency);
    }
}
