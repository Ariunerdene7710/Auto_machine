package com.example.back.service;

import com.example.back.dto.CarDTO;
import com.example.back.entity.Car;
import com.example.back.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ============= CAR METHODS =============

    public List<Car> getAllCars() {
        return carRepository.findByActiveTrue();
    }

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public List<Car> searchCars(String keyword) {
        return carRepository.searchByKeyword(keyword);
    }

    public List<Car> getCarsByBrand(String brand) {
        return carRepository.findByBrandIgnoreCase(brand);
    }

    public List<Car> getFeaturedCars() {
        return carRepository.findByIsFeaturedTrue();
    }

    public Car createCar(CarDTO carDTO, List<MultipartFile> images) throws IOException {
        Car car = new Car();
        updateCarFromDTO(car, carDTO);

        String now = LocalDateTime.now().format(FORMATTER);
        car.setCreatedAt(now);
        car.setUpdatedAt(now);

        if (images != null && !images.isEmpty()) {
            car.setImages(saveImages(images));
        }

        return carRepository.save(car);
    }

    public Car updateCar(Long id, CarDTO carDTO, List<MultipartFile> images) throws IOException {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));

        updateCarFromDTO(existingCar, carDTO);
        existingCar.setUpdatedAt(LocalDateTime.now().format(FORMATTER));

        if (images != null && !images.isEmpty()) {
            List<String> currentImages = existingCar.getImages();
            if (currentImages == null) {
                currentImages = new ArrayList<>();
            }
            currentImages.addAll(saveImages(images));
            existingCar.setImages(currentImages);
        }

        return carRepository.save(existingCar);
    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        car.setActive(false);
        carRepository.save(car);
    }

    // ============= MACHINE METHODS (for compatibility) =============

    public List<Car> getAllMachines() {
        return getAllCars();
    }

    public Car getMachineById(Long id) {
        return getCarById(id).orElseThrow(() -> new RuntimeException("Machine not found"));
    }

    public List<Car> searchMachines(String keyword) {
        return searchCars(keyword);
    }

    public Car createMachine(CarDTO carDTO, List<MultipartFile> images) {
        try {
            return createCar(carDTO, images);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create machine: " + e.getMessage());
        }
    }

    public Car updateMachine(Long id, CarDTO carDTO, List<MultipartFile> images) {
        try {
            return updateCar(id, carDTO, images);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update machine: " + e.getMessage());
        }
    }

    public void deleteMachine(Long id) {
        deleteCar(id);
    }

    // ============= PRIVATE HELPER METHODS =============

    private void updateCarFromDTO(Car car, CarDTO dto) {
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setPrice(BigDecimal.valueOf(dto.getPrice()));
        car.setOriginalPrice(dto.getOriginalPrice() != null ? BigDecimal.valueOf(dto.getOriginalPrice()) : null);
        car.setMileage(dto.getMileage());
        car.setFuelType(dto.getFuelType());
        car.setTransmission(dto.getTransmission());
        car.setLocation(dto.getLocation());
        car.setDescription(dto.getDescription());
        car.setFeatures(dto.getFeatures() != null ? dto.getFeatures() : new ArrayList<>());
        car.setStock(dto.getStock() != null ? dto.getStock() : 0);
        car.setStockQuantity(dto.getStock() != null ? dto.getStock() : 0);
        car.setNew(dto.isNew());
        car.setFeatured(dto.isFeatured());
        car.setActive(dto.isActive());
        car.setCategoryId(dto.getCategoryId());
    }

    private List<String> saveImages(List<MultipartFile> images) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                String originalFileName = image.getOriginalFilename();
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                String fileName = UUID.randomUUID().toString() + fileExtension;
                Path targetLocation = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                imageUrls.add("/api/images/" + fileName);
            }
        }

        return imageUrls;
    }
}