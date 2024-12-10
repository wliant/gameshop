package org.example.gameshop.model;

import jakarta.persistence.*;
import org.example.gameshop.model.converter.FileMetadataConverter;

import java.time.Instant;

@Entity
public class FileImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP(3)")
    private Instant creationTime;

    @Column(columnDefinition = "TIMESTAMP(3)")
    private Instant updatedTime;

    @Column
    @Convert(converter = FileMetadataConverter.class)
    private FileMetadata fileMetadata;

    @Column
    private Integer processCount;

    @Enumerated(EnumType.STRING)
    @Column
    private ImportStatus status;

    @Version
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Instant getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Instant updatedTime) {
        this.updatedTime = updatedTime;
    }

    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public void setFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
    }

    public Integer getProcessCount() {
        return processCount;
    }

    public void setProcessCount(Integer processCount) {
        this.processCount = processCount;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
