package org.example.gameshop.service;

import org.apache.commons.csv.CSVRecord;
import org.example.gameshop.model.FileImport;

import java.util.List;

public interface FileImportService {
    FileImport updateStatusInProgress(FileImport fileImport);

    void completeFileImport(FileImport fileImport);

    void failFileImport(FileImport fileImport);

    void processCSVRecords(List<CSVRecord> csvRecords, FileImport fileImport);

    void timeoutImports();
    void retryImports();
}
