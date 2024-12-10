package org.example.gameshop.service;

import org.example.gameshop.model.projection.GameSaleProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface GameSaleService {
    Page<GameSaleProjection> getGameSales(LocalDate from, LocalDate to, Double minPrice, Double maxPrice, Pageable pageable);
}
