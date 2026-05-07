package com.example.back.dto;

import lombok.Data;
import java.util.List;

@Data
public class CarDTO {
    private String brand;
    private String model;
    private Integer year;
    private Double price;
    private Double originalPrice;
    private Double mileage;
    private String fuelType;
    private String transmission;
    private String location;
    private String description;
    private List<String> features;
    private Integer stock;
    private boolean isNew;
    private boolean isFeatured;
    private boolean active;
    private Long categoryId;
}