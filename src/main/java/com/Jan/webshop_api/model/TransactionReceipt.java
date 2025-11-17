package com.Jan.webshop_api.model;

import lombok.Data;
import java.util.List;

@Data
public class TransactionReceipt {
    private String currency;
    private double totalPurchasePrice; // Total *before* fees
    private double totalSalePrice;     // Total *with* fees
    private int totalItems;
    private List<Product> productsPurchased; // A list of the products
}
