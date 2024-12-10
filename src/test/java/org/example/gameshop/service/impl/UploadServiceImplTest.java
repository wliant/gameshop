package org.example.gameshop.service.impl;

import org.example.gameshop.config.ImportConfigurationProperties;
import org.example.gameshop.event.FileImportEvent;
import org.example.gameshop.model.FileImport;
import org.example.gameshop.model.FileMetadata;
import org.example.gameshop.model.ImportStatus;
import org.example.gameshop.model.projection.FileImportView;
import org.example.gameshop.repository.FileImportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UploadServiceImplTest {

    @Mock
    private FileImportRepository fileImportRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ImportConfigurationProperties importConfigurationProperties;

    @InjectMocks
    private UploadServiceImpl uploadService;

    private final String uploadPath = "uploads";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(importConfigurationProperties.getUploadPath()).thenReturn(uploadPath);
    }

    @Test
    void testCreateFile() throws IOException {
        String fileName = "test.csv";
        String contentType = "text/csv";
        InputStream inputStream = new ByteArrayInputStream("test data".getBytes());

        FileMetadata fileMetadata = uploadService.createFile(inputStream, fileName, contentType);

        assertNotNull(fileMetadata);
        assertEquals(contentType, fileMetadata.contentType());
        assertEquals(fileName, fileMetadata.fileName());

        Path expectedPath = Paths.get(uploadPath, LocalDate.now().toString(), fileMetadata.filePath().getFileName().toString());
        assertTrue(Files.exists(expectedPath));
    }

    @Test
    void testCreateFileImport() {
        FileMetadata fileMetadata = new FileMetadata(Paths.get("test.csv"), "text/csv", "test.csv");
        FileImport fileImport = new FileImport();
        fileImport.setId(1L);
        fileImport.setFileMetadata(fileMetadata);
        fileImport.setCreationTime(Instant.now());
        fileImport.setUpdatedTime(Instant.now());
        fileImport.setProcessCount(1);
        fileImport.setStatus(ImportStatus.UPLOADED);

        when(fileImportRepository.save(any(FileImport.class))).thenReturn(fileImport);

        FileImportView fileImportView = uploadService.createFileImport(fileMetadata);

        assertNotNull(fileImportView);
        assertEquals(1L, fileImportView.getId());

        ArgumentCaptor<FileImportEvent> eventCaptor = ArgumentCaptor.forClass(FileImportEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        assertEquals(fileImport, eventCaptor.getValue().getFileImport());
    }
}