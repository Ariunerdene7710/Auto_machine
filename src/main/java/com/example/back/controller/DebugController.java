package com.example.back.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/upload-dir")
    public ResponseEntity<Map<String, Object>> getUploadDirInfo() {
        Map<String, Object> info = new HashMap<>();

        File dir = new File(uploadDir);
        info.put("configuredPath", uploadDir);
        info.put("absolutePath", dir.getAbsolutePath());
        info.put("exists", dir.exists());
        info.put("isDirectory", dir.isDirectory());
        info.put("canRead", dir.canRead());
        info.put("canWrite", dir.canWrite());

        if (dir.exists() && dir.isDirectory()) {
            String[] files = dir.list();
            info.put("fileCount", files != null ? files.length : 0);
            info.put("files", files);
        }

        return ResponseEntity.ok(info);
    }
}