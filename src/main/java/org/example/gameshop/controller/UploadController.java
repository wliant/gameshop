package org.example.gameshop.controller;

import org.example.gameshop.controller.validator.ValidMultipartFile;
import org.example.gameshop.model.projection.FileImportView;
import org.example.gameshop.model.FileMetadata;
import org.example.gameshop.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/import")
    public ResponseEntity<FileImportView> importData(@ValidMultipartFile @RequestParam("file") MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            FileMetadata fileMetadata = uploadService.createFile(is, file.getOriginalFilename(), file.getContentType());

            return ResponseEntity.ok(uploadService.createFileImport(fileMetadata));
        }
    }
}
