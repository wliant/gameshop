package org.example.gameshop.service.impl;

import org.apache.commons.csv.CSVRecord;
import org.example.gameshop.config.ImportConfigurationProperties;
import org.example.gameshop.event.FileImportEvent;
import org.example.gameshop.model.FileImport;
import org.example.gameshop.model.GameSaleStaging;
import org.example.gameshop.model.ImportStatus;
import org.example.gameshop.model.converter.CSVRecordGameSaleStagingConverter;
import org.example.gameshop.repository.FileImportRepository;
import org.example.gameshop.repository.GameSaleRepository;
import org.example.gameshop.repository.GameSaleStagingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileImportServiceImplTest {

    @Mock
    private FileImportRepository fileImportRepository;

    @Mock
    private GameSaleStagingRepository gameSaleStagingRepository;

    @Mock
    private GameSaleRepository gameSaleRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ImportConfigurationProperties importConfigurationProperties;

    @Mock
    private CSVRecordGameSaleStagingConverter csvRecordGameSaleStagingConverter;

    @InjectMocks
    private FileImportServiceImpl fileImportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateStatusInProgress() {
        FileImport fileImport = new FileImport();
        fileImport.setProcessCount(0);

        when(fileImportRepository.save(any(FileImport.class))).thenReturn(fileImport);

        FileImport updatedFileImport = fileImportService.updateStatusInProgress(fileImport);

        assertEquals(ImportStatus.IN_PROGRESS, updatedFileImport.getStatus());
        assertEquals(1, updatedFileImport.getProcessCount());
        verify(fileImportRepository, times(1)).save(fileImport);
    }

    @Test
    void testCompleteFileImport() {
        FileImport fileImport = new FileImport();
        fileImport.setId(1L);

        fileImportService.completeFileImport(fileImport);

        assertEquals(ImportStatus.COMPLETED, fileImport.getStatus());
        verify(gameSaleRepository, times(1)).insertGameSalesFromStaging(fileImport.getId());
        verify(fileImportRepository, times(1)).save(fileImport);
    }

    @Test
    void testFailFileImport() {
        FileImport fileImport = new FileImport();
        fileImport.setId(1L);

        fileImportService.failFileImport(fileImport);

        assertEquals(ImportStatus.FAILED, fileImport.getStatus());
        verify(gameSaleStagingRepository, times(1)).deleteByFileImportId(fileImport.getId());
        verify(fileImportRepository, times(1)).save(fileImport);
    }

    @Test
    void testProcessCSVRecords() {
        FileImport fileImport = new FileImport();
        CSVRecord csvRecord = mock(CSVRecord.class);
        GameSaleStaging gameSaleStaging = new GameSaleStaging();

        when(csvRecordGameSaleStagingConverter.convert(any(CSVRecord.class))).thenReturn(gameSaleStaging);

        fileImportService.processCSVRecords(List.of(csvRecord), fileImport);

        verify(gameSaleStagingRepository, times(1)).save(gameSaleStaging);
    }

    @Test
    void testTimeoutImports() {
        when(importConfigurationProperties.getTimeoutSecond()).thenReturn(3600);
        when(importConfigurationProperties.getTimeoutFetchSize()).thenReturn(10);

        FileImport fileImport = new FileImport();
        fileImport.setId(1L);
        fileImport.setStatus(ImportStatus.UPLOADED);

        when(fileImportRepository.findTimedOutImports(anyList(), any(Instant.class), any(PageRequest.class)))
                .thenReturn(List.of(fileImport));

        fileImportService.timeoutImports();

        verify(fileImportRepository, times(1)).findTimedOutImports(anyList(), any(Instant.class), any(PageRequest.class));
        verify(fileImportRepository, times(1)).save(fileImport);
        verify(gameSaleStagingRepository, times(1)).deleteByFileImportId(fileImport.getId());
    }

    @Test
    void testRetryImports() {
        when(importConfigurationProperties.getRetryDelay()).thenReturn(3600);
        when(importConfigurationProperties.getRetryFetchSize()).thenReturn(10);
        when(importConfigurationProperties.getMaxRetry()).thenReturn(3);

        FileImport fileImport = new FileImport();
        fileImport.setId(1L);
        fileImport.setStatus(ImportStatus.FAILED);

        when(fileImportRepository.findFailedImports(any(Instant.class), anyInt(), any(PageRequest.class)))
                .thenReturn(List.of(fileImport));

        fileImportService.retryImports();

        verify(fileImportRepository, times(1)).findFailedImports(any(Instant.class), anyInt(), any(PageRequest.class));
        verify(fileImportRepository, times(1)).save(fileImport);
        verify(eventPublisher, times(1)).publishEvent(any(FileImportEvent.class));
    }
}