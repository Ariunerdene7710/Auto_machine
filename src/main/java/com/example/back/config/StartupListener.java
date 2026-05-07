package com.example.back.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class StartupListener {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath);
            } else {
                System.out.println("Upload directory exists: " + uploadPath);
            }
            System.out.println("Upload directory absolute path: " + uploadPath);
        } catch (IOException e) {
            System.err.println("Could not create upload directory: " + e.getMessage());
        }
    }
}