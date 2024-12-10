package org.example.gameshop.service.impl;

import org.example.gameshop.config.ImportConfigurationProperties;
import org.example.gameshop.event.FileImportEvent;
import org.example.gameshop.model.FileImport;
import org.example.gameshop.model.FileMetadata;
import org.example.gameshop.model.ImportStatus;
import org.example.gameshop.model.projection.FileImportView;
import org.example.gameshop.repository.FileImportRepository;
import org.example.gameshop.service.UploadService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

    private final FileImportRepository fileImportRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final ImportConfigurationProperties importConfigurationProperties;

    public UploadServiceImpl(ImportConfigurationProperties importConfigurationProperties, FileImportRepository fileImportRepository, ApplicationEventPublisher eventPublisher) {
        this.fileImportRepository = fileImportRepository;
        this.eventPublisher = eventPublisher;
        this.importConfigurationProperties = importConfigurationProperties;
    }

    @Override
    public FileMetadata createFile(InputStream inputStream, String fileName, String contentType) throws IOException {        // Create subdirectory based on today's date
        String uploadPath = importConfigurationProperties.getUploadPath();

        String dateSubdirectory = LocalDate.now().toString();
        Path subdirectoryPath = Paths.get(uploadPath, dateSubdirectory);
        Files.createDirectories(subdirectoryPath);

        String uniqueFileName = UUID.randomUUID().toString();
        Path filePath = subdirectoryPath.resolve(uniqueFileName);

        Files.copy(inputStream, filePath);

        return new FileMetadata(filePath, contentType, fileName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FileImportView createFileImport(FileMetadata fileMetadata) {
        FileImport fileImport = new FileImport();
        fileImport.setFileMetadata(fileMetadata);
        fileImport.setCreationTime(Instant.now());
        fileImport.setUpdatedTime(Instant.now());
        fileImport.setProcessCount(1);
        fileImport.setStatus(ImportStatus.UPLOADED);

        FileImport saved = fileImportRepository.save(fileImport);
        eventPublisher.publishEvent(new FileImportEvent(this, saved));

        return saved::getId;
    }
}
