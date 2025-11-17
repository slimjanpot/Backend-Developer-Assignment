package com.Jan.webshop_api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnrichedProduct {
    // All the original info
    private Long id;
    private String title;
    private String description;
    private String category;
    private String image;

    // Your new, required fields
    private double purchasePrice; // The price before fees (in new currency)
    private double salePrice;     // The price with 10% fee (in new currency)
    private String currency;      // The currency code (e.g., "USD")
}
