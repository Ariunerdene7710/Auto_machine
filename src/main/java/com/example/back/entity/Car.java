package com.example.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    private Integer year;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Double mileage;
    private String fuelType;
    private String transmission;
    private String location;

    @Column(length = 2000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "car_features", joinColumns = @JoinColumn(name = "car_id"))
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    private Integer stock = 0;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column(name = "is_new")
    private boolean isNew = false;

    @Column(name = "is_featured")
    private boolean isFeatured = false;

    private boolean active = true;

    @Column(name = "category_id")
    private Long categoryId;

    @ElementCollection
    @CollectionTable(name = "car_images", joinColumns = @JoinColumn(name = "car_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    private String createdAt;
    private String updatedAt;

    // Helper method
    public String getName() {
        return brand + " " + model;
    }
}