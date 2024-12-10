package org.example.gameshop.repository;

import org.example.gameshop.model.GameSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameSaleRepository extends JpaRepository<GameSale, Long>, JpaSpecificationExecutor<GameSale> {

    @Modifying
    @Query(value = "INSERT INTO GameSale (id, gameNumber, gameName, gameCode, gameType, costPrice, tax, salePrice, dateOfSale) " +
            "SELECT id, gameNumber, gameName, gameCode, gameType, costPrice,tax, salePrice,dateOfSale FROM GameSaleStaging " +
            "WHERE fileImport.id = :importId")
    void insertGameSalesFromStaging(@Param("importId") Long importId);
}
