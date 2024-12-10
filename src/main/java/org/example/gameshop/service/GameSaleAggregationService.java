package org.example.gameshop.service;

import org.example.gameshop.model.projection.GameSaleAggregateProjection;

import java.time.LocalDate;

public interface GameSaleAggregationService {
    void aggregateGameSales();

    void aggregateGameSalesByGameNo();

    GameSaleAggregateProjection getGameSaleAggregates(LocalDate from, LocalDate to, Integer gameNo);
}
