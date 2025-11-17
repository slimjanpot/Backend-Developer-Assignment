package com.Jan.webshop_api.model;

import lombok.Data;
import java.util.List;

@Data
public class TransactionRequest {
    private String currency; // "USD", "JPY", etc.
    private List<CartItem> items;
}
