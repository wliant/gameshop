package org.example.gameshop.service.impl;

import org.example.gameshop.model.projection.GameSaleAggregateProjection;
import org.example.gameshop.repository.GameSaleGameNoAggregateRepository;
import org.example.gameshop.repository.GameSaleAggregateRepository;
import org.example.gameshop.service.GameSaleAggregationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class GameSaleAggregationServiceImpl implements GameSaleAggregationService {
    private final GameSaleAggregateRepository gameSaleAggregateRepository;
    private final GameSaleGameNoAggregateRepository gameSaleGameNoAggregateRepository;

    public GameSaleAggregationServiceImpl(GameSaleAggregateRepository gameSaleAggregateRepository, GameSaleGameNoAggregateRepository gameSaleGameNoAggregateRepository) {
        this.gameSaleAggregateRepository = gameSaleAggregateRepository;
        this.gameSaleGameNoAggregateRepository = gameSaleGameNoAggregateRepository;
    }

    @Transactional
    @Override
    public void aggregateGameSales() {
        this.gameSaleAggregateRepository.deleteAll();
        this.gameSaleAggregateRepository.insert();
    }

    @Transactional
    @Override
    public void aggregateGameSalesByGameNo() {
        this.gameSaleGameNoAggregateRepository.deleteAll();
        this.gameSaleGameNoAggregateRepository.insert();
    }

    @Override
    public GameSaleAggregateProjection getGameSaleAggregates(LocalDate from, LocalDate to, Integer gameNo) {
        if (gameNo != null) {
            return this.gameSaleGameNoAggregateRepository.getTotalSales(from, to, gameNo).orElseThrow();
        } else {
            return this.gameSaleAggregateRepository.getTotalSales(from, to).orElseThrow();
        }


    }
}
