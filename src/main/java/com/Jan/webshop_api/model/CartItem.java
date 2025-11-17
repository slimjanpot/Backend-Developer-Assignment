package com.Jan.webshop_api.model;

import lombok.Data;

@Data
public class CartItem {
    private Long productId;
    private int quantity;
}
