package org.example.gameshop.model;

import java.nio.file.Path;

public record FileMetadata(Path filePath, String contentType, String fileName) {
}
