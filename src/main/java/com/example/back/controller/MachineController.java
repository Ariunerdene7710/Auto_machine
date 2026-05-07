package com.example.back.controller;

import com.example.back.dto.CarDTO;
import com.example.back.entity.Car;
import com.example.back.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/machines")
@CrossOrigin(origins = "*")
public class MachineController {

    @Autowired
    private CarService carService;

    @GetMapping("/public")
    public ResponseEntity<List<Car>> getAllMachines() {
        return ResponseEntity.ok(carService.getAllMachines());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<Car> getMachineById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getMachineById(id));
    }

    @GetMapping("/public/search")
    public ResponseEntity<List<Car>> searchMachines(@RequestParam String keyword) {
        return ResponseEntity.ok(carService.searchMachines(keyword));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Car> createMachine(
            @RequestPart("machine") CarDTO carDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(carService.createMachine(carDTO, images));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Car> updateMachine(
            @PathVariable Long id,
            @RequestPart("machine") CarDTO carDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(carService.updateMachine(id, carDTO, images));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMachine(@PathVariable Long id) {
        carService.deleteMachine(id);
        return ResponseEntity.ok("Machine deleted successfully");
    }
}
