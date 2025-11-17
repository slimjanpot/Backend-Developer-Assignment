package com.Jan.webshop_api.controller;

import com.Jan.webshop_api.model.TransactionRequest;
import com.Jan.webshop_api.model.TransactionReceipt;
import com.Jan.webshop_api.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions") // Endpoints start with /transactions
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Creates a new transaction from a list of items and a currency.
     *
     * Example URL: POST http://localhost:8080/transactions
     * Example Body (JSON):
     * {
     * "currency": "USD",
     * "items": [
     * { "productId": 1, "quantity": 2 },
     * { "productId": 5, "quantity": 1 }
     * ]
     * }
     */
    @PostMapping
    public TransactionReceipt createTransaction(@RequestBody TransactionRequest request) {
        // @RequestBody tells Spring to get the JSON from the user's
        // request and turn it into our TransactionRequest object
        
        return transactionService.createTransaction(request);
    }
}
