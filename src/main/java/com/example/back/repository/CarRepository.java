package com.example.back.repository;

import com.example.back.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByActiveTrue();

    List<Car> findByBrandContainingIgnoreCaseOrModelContainingIgnoreCase(String brand, String model);

    List<Car> findByBrandIgnoreCase(String brand);

    List<Car> findByIsFeaturedTrue();

    List<Car> findByCategoryId(Long categoryId);

    boolean existsByCategoryId(Long categoryId);

    List<Car> findByPriceBetween(Double minPrice, Double maxPrice);

    List<Car> findByYear(Integer year);

    List<Car> findByFuelTypeIgnoreCase(String fuelType);

    List<Car> findByTransmissionIgnoreCase(String transmission);

    @Query("SELECT c FROM Car c WHERE LOWER(c.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Car> searchByKeyword(@Param("keyword") String keyword);
}