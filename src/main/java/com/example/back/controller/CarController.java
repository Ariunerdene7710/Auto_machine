package com.example.back.controller;

import com.example.back.dto.CarDTO;
import com.example.back.entity.Car;
import com.example.back.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:5174"
})
public class CarController {

    @Autowired
    private CarService carService;

    // ============= PUBLIC ENDPOINTS =============

    // Бүх машиныг авах
    @GetMapping("/public")
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    // ID-ээр машин авах
    @GetMapping("/public/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return carService.getCarById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Хайлт хийх
    @GetMapping("/public/search")
    public ResponseEntity<List<Car>> searchCars(@RequestParam String keyword) {
        return ResponseEntity.ok(carService.searchCars(keyword));
    }

    // Брэндээр шүүх
    @GetMapping("/public/brand/{brand}")
    public ResponseEntity<List<Car>> getCarsByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(carService.getCarsByBrand(brand));
    }

    // Онцлох машинууд
    @GetMapping("/public/featured")
    public ResponseEntity<List<Car>> getFeaturedCars() {
        return ResponseEntity.ok(carService.getFeaturedCars());
    }

    // ============= ADMIN ENDPOINTS =============

    // Шинэ машин үүсгэх
    @PostMapping("/admin")
    public ResponseEntity<Car> createCar(
            @RequestPart("car") CarDTO carDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            Car savedCar = carService.createCar(carDTO, images);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCar);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Машин шинэчлэх
    @PutMapping("/admin/{id}")
    public ResponseEntity<Car> updateCar(
            @PathVariable Long id,
            @RequestPart("car") CarDTO carDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            Car updatedCar = carService.updateCar(id, carDTO, images);
            return ResponseEntity.ok(updatedCar);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Машин устгах
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
