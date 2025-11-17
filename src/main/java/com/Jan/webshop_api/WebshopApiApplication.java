package com.Jan.webshop_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling  // <-- ADD THIS ANNOTATION
public class WebshopApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebshopApiApplication.class, args);
    }

    // ADD THIS METHOD. It creates a 'RestTemplate' tool
    // that other services (like our new one) can use.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
