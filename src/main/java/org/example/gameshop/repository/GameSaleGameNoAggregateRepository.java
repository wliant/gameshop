package org.example.gameshop.repository;

import org.example.gameshop.model.aggregate.GameSaleGameNoAggregate;
import org.example.gameshop.model.aggregate.GameSaleAggregateId;
import org.example.gameshop.model.projection.GameSaleAggregateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface GameSaleGameNoAggregateRepository extends JpaRepository<GameSaleGameNoAggregate, GameSaleAggregateId> {
    @Modifying
    @Query("INSERT INTO GameSaleGameNoAggregate (dateOfSale, gameNo, saleCount, saleSum) " +
            "SELECT g.dateOfSale, g.gameNumber, COUNT(g), SUM(g.salePrice) FROM GameSale g GROUP BY g.dateOfSale, g.gameNumber")
    void insert();

    @Query("SELECT SUM(g.saleCount) as saleCount, SUM(g.saleSum) as saleSum " +
            "FROM GameSaleGameNoAggregate g " +
            "WHERE (:fromDate IS NULL OR g.dateOfSale >= :fromDate) AND (:toDate IS NULL OR g.dateOfSale <= :toDate) AND g.gameNo = :gameNo")
    Optional<GameSaleAggregateProjection> getTotalSales(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("gameNo") Integer gameNo);
}
