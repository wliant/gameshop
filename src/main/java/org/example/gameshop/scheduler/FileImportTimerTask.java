package org.example.gameshop.scheduler;

import org.example.gameshop.service.FileImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class FileImportTimerTask {
    private static final Logger logger = LoggerFactory.getLogger(FileImportTimerTask.class.getName());

    private final FileImportService fileImportService;

    public FileImportTimerTask(FileImportService fileImportService) {
        this.fileImportService = fileImportService;
    }

    @Scheduled(fixedRate = 30000)
    public void processTimeout() {
        try {
            fileImportService.timeoutImports();
        } catch (Exception e) {
            logger.error("Error in processTimeout: ", e);
        }
    }

    @Scheduled(fixedRate = 30000)
    public void processFailed() {
        try {
            fileImportService.retryImports();
        } catch (Exception e) {
            logger.error("Error in processFailed: ", e);
        }
    }
}
