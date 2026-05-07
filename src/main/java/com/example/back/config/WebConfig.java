package com.example.back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /api/images/** to the upload directory
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadAbsolutePath = uploadPath.toUri().toString();

        System.out.println("Static resource handler mapping: /api/images/** -> " + uploadAbsolutePath);

        registry.addResourceHandler("/api/images/**")
                .addResourceLocations(uploadAbsolutePath)
                .setCachePeriod(3600);

        // Also map without /api prefix for direct access
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadAbsolutePath)
                .setCachePeriod(3600);
    }
}
