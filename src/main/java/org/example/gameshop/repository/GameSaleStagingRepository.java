package org.example.gameshop.repository;

import org.example.gameshop.model.GameSaleStaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GameSaleStagingRepository extends JpaRepository<GameSaleStaging, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM GameSaleStaging WHERE fileImport.id = :importId")
    void deleteByFileImportId(@Param("importId") Long importId);

}
