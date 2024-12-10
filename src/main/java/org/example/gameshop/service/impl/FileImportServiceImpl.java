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
import org.example.gameshop.service.FileImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;


@Service
public class FileImportServiceImpl implements FileImportService {

    private final Logger logger = LoggerFactory.getLogger(FileImportServiceImpl.class.getName());
    private final FileImportRepository fileImportRepository;
    private final GameSaleStagingRepository gameSaleStagingRepository;
    private final GameSaleRepository gameSaleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ImportConfigurationProperties importConfigurationProperties;
    private final CSVRecordGameSaleStagingConverter csvRecordGameSaleStagingConverter;

    public FileImportServiceImpl(FileImportRepository fileImportRepository, GameSaleStagingRepository gameSaleStagingRepository, GameSaleRepository gameSaleRepository, ApplicationEventPublisher eventPublisher, ImportConfigurationProperties importConfigurationProperties, CSVRecordGameSaleStagingConverter csvRecordToGameSaleStagingConverter) {
        this.fileImportRepository = fileImportRepository;
        this.gameSaleStagingRepository = gameSaleStagingRepository;
        this.gameSaleRepository = gameSaleRepository;
        this.eventPublisher = eventPublisher;
        this.importConfigurationProperties = importConfigurationProperties;
        csvRecordGameSaleStagingConverter = csvRecordToGameSaleStagingConverter;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FileImport updateStatusInProgress(FileImport fileImport) {
        fileImport.setUpdatedTime(Instant.now());
        fileImport.setStatus(ImportStatus.IN_PROGRESS);
        fileImport.setProcessCount(fileImport.getProcessCount() + 1);
        return fileImportRepository.save(fileImport);
    }

    @Override
    @Transactional
    public void completeFileImport(FileImport fileImport) {
        logger.info("Completing import with ID: {}", fileImport.getId());
        fileImport.setUpdatedTime(Instant.now());
        fileImport.setStatus(ImportStatus.COMPLETED);
        gameSaleRepository.insertGameSalesFromStaging(fileImport.getId());

        logger.info("Inserted game sales for import with ID: {}", fileImport.getId());
        fileImportRepository.save(fileImport);
    }

    @Override
    @Transactional
    public void failFileImport(FileImport fileImport) {
        logger.info("Failing import with ID: {}", fileImport.getId());

        fileImport.setUpdatedTime(Instant.now());
        fileImport.setStatus(ImportStatus.FAILED);
        gameSaleStagingRepository.deleteByFileImportId(fileImport.getId());
        logger.info("Deleted staging data for import with ID: {}", fileImport.getId());

        fileImportRepository.save(fileImport);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processCSVRecords(List<CSVRecord> csvRecords, FileImport fileImport) {

        for (CSVRecord csvRecord : csvRecords) {
            if (Thread.currentThread().isInterrupted()) {
                throw new IllegalStateException();
            }

            GameSaleStaging staging = csvRecordGameSaleStagingConverter.convert(csvRecord);
            staging.setFileImport(fileImport);

            this.gameSaleStagingRepository.save(staging);
        }

    }

    @Override
    @Transactional
    public void timeoutImports() {
        int timeoutSecond = importConfigurationProperties.getTimeoutSecond();
        int timeoutFetchSize = importConfigurationProperties.getTimeoutFetchSize();

        List<ImportStatus> statuses = List.of(ImportStatus.UPLOADED, ImportStatus.IN_PROGRESS);
        Instant timeout = Instant.now().minusSeconds(timeoutSecond);
        List<FileImport> timedOut = fileImportRepository.findTimedOutImports(statuses, timeout, PageRequest.of(0, timeoutFetchSize));

        logger.info("Found {} imports to timeout", timedOut.size());

        timedOut.forEach(this::failFileImport);
    }

    @Override
    public void retryImports() {
        int retryDelay = importConfigurationProperties.getRetryDelay();
        int retryFetchSize = importConfigurationProperties.getRetryFetchSize();
        int maxRetry = importConfigurationProperties.getMaxRetry();

        Instant before = Instant.now().minusSeconds(retryDelay);
        List<FileImport> failedImports = fileImportRepository.findFailedImports(before, maxRetry, PageRequest.of(0, retryFetchSize));

        logger.info("Found {} failed imports to retry", failedImports.size());

        failedImports.forEach(fileImport -> {
            logger.info("Retrying import with ID: {}", fileImport.getId());
            fileImport.setUpdatedTime(Instant.now());
            fileImportRepository.save(fileImport);
            eventPublisher.publishEvent(new FileImportEvent(this, fileImport));
        });


    }


}
