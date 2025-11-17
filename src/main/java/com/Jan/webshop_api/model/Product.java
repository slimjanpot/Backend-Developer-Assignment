package com.Jan.webshop_api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;

@Data // Creates getters, setters, toString, etc. for all fields
@NoArgsConstructor // Creates an empty constructor (needed for JSON)
@AllArgsConstructor // Creates a constructor with all fields
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private Long id;
    private String title;
    private double price; // This will be the price in EUR from the API
    private String description;
    private String category;
    private String image;
    // We can ignore the "rating" field for now if we don't need it
}
