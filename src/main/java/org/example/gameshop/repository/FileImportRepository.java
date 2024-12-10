package org.example.gameshop.repository;

import org.example.gameshop.model.FileImport;
import org.example.gameshop.model.ImportStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface FileImportRepository extends JpaRepository<FileImport, Long> {
    @Query("SELECT fi FROM FileImport fi WHERE fi.status IN (:statuses) AND fi.updatedTime < :timeout ORDER BY fi.updatedTime ASC")
    List<FileImport> findTimedOutImports(@Param("statuses") List<ImportStatus> statuses, @Param("timeout") Instant timeout, Pageable pageable);

    @Query("SELECT fi FROM FileImport fi WHERE fi.status = org.example.gameshop.model.ImportStatus.FAILED AND fi.updatedTime < :before AND fi.processCount < :maxRetry ORDER BY fi.updatedTime ASC")
    List<FileImport> findFailedImports(@Param("before") Instant before, @Param("maxRetry") int maxRetry, Pageable pageable);

}
