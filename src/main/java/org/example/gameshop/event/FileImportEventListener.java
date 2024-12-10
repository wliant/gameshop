package org.example.gameshop.event;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.gameshop.config.ImportConfigurationProperties;
import org.example.gameshop.model.FileImport;
import org.example.gameshop.model.ImportStatus;
import org.example.gameshop.repository.FileImportRepository;
import org.example.gameshop.service.FileImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

@Component
public class FileImportEventListener {

    private static final Logger logger = LoggerFactory.getLogger(FileImportEventListener.class);

    private final FileImportService fileImportService;
    private final ImportConfigurationProperties importConfigurationProperties;


    public FileImportEventListener(FileImportService fileImportService, ImportConfigurationProperties importConfigurationProperties) {
        this.fileImportService = fileImportService;
        this.importConfigurationProperties = importConfigurationProperties;
    }

    @Async("importExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFileImport(FileImportEvent event) {
        logger.info("File import event received: {}", event.getFileImport().getId());
        FileImport fileImport = event.getFileImport();
        try {
            fileImport = fileImportService.updateStatusInProgress(fileImport);
        } catch(OptimisticLockingFailureException e) {
            logger.error("Error updating file import status", e);
            return;
        }

        Path filePath = fileImport.getFileMetadata().filePath();
        ExecutorService es = Executors.newFixedThreadPool(importConfigurationProperties.getThreadPoolSize());

        int chunkSize = importConfigurationProperties.getChunkSize();
        boolean success = false;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            CSVParser parser = this.importConfigurationProperties.csvFormat()
                    .parse(reader);

            List<CSVRecord> chunk = new ArrayList<>();
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (CSVRecord csvRow : parser) {
                chunk.add(csvRow);
                if (chunk.size() == chunkSize) {
                    futures.add(dispatchChunk(chunk, es, fileImport));
                    chunk = new ArrayList<>();
                }
            }
            if (!chunk.isEmpty()) {
                futures.add(dispatchChunk(chunk, es, fileImport));
            }

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.get(3, TimeUnit.MINUTES);
            success = true;

        } catch (IOException | ExecutionException | TimeoutException | InterruptedException e) {
            logger.error("Error processing file import", e);
            es.shutdownNow();
        } finally {
            if (success) {
                fileImportService.completeFileImport(fileImport);
            } else {
                fileImportService.failFileImport(fileImport);
            }
            if (!es.isShutdown()) {
                es.shutdown();
            }
            logger.info("exit file processing");
        }
    }

    private CompletableFuture<Void> dispatchChunk(List<CSVRecord> chunk, ExecutorService es, FileImport fileImport) {
        return CompletableFuture.runAsync(() -> {
            try {
                this.fileImportService.processCSVRecords(chunk, fileImport);
            } catch(Exception e) {
                es.shutdownNow();
                throw e;
            }
        }, es);
    }
}
