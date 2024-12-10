package org.example.gameshop.repository;

import org.example.gameshop.model.aggregate.GameSaleAggregate;
import org.example.gameshop.model.projection.GameSaleAggregateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GameSaleAggregateRepository extends JpaRepository<GameSaleAggregate, Long> {
    @Modifying
    @Query("INSERT INTO GameSaleAggregate (dateOfSale, saleCount, saleSum) " +
            "SELECT g.dateOfSale, SUM(g.saleCount), SUM(g.saleSum) FROM GameSaleGameNoAggregate g GROUP BY g.dateOfSale")
    void insert();

    @Query("SELECT SUM(g.saleCount) as saleCount, SUM(g.saleSum) as saleSum " +
            "FROM GameSaleAggregate g " +
            "WHERE (:fromDate IS NULL OR g.dateOfSale >= :fromDate) AND (:toDate IS NULL OR g.dateOfSale <= :toDate)")
    Optional<GameSaleAggregateProjection> getTotalSales(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

}
