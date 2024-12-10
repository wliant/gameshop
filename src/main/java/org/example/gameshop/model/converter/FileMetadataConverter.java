package org.example.gameshop.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.gameshop.model.FileMetadata;

@Converter(autoApply = true)
public class FileMetadataConverter implements AttributeConverter<FileMetadata, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(FileMetadata fileMetadata) {
        try {
            return objectMapper.writeValueAsString(fileMetadata);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting FileMetadata to JSON", e);
        }
    }

    @Override
    public FileMetadata convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, FileMetadata.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to FileMetadata", e);
        }
    }
}