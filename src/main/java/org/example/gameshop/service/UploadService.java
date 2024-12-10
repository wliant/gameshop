package org.example.gameshop.service;

import org.example.gameshop.model.projection.FileImportView;
import org.example.gameshop.model.FileMetadata;

import java.io.IOException;
import java.io.InputStream;

public interface UploadService {
    FileMetadata createFile(InputStream inputStream, String fileName, String contentType) throws IOException;

    FileImportView createFileImport(FileMetadata fileMetadata);
}
