package org.example.gameshop.service.impl;

import org.example.gameshop.model.GameSale;
import org.example.gameshop.model.projection.GameSaleProjection;
import org.example.gameshop.model.specification.GameSaleSpecification;
import org.example.gameshop.repository.GameSaleRepository;
import org.example.gameshop.service.GameSaleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class GameSaleServiceImpl implements GameSaleService {


    private final GameSaleRepository gameSaleRepository;

    public GameSaleServiceImpl(GameSaleRepository gameSaleRepository) {
        this.gameSaleRepository = gameSaleRepository;
    }

    @Override
    public Page<GameSaleProjection> getGameSales(LocalDate from, LocalDate to, Double minPrice, Double maxPrice, Pageable pageable) {
        Specification<GameSale> spec = Specification.where(GameSaleSpecification.hasDateRange(from, to))
                .and(GameSaleSpecification.hasPriceRange(minPrice, maxPrice));
        return gameSaleRepository.findBy(spec, q -> q.as(GameSaleProjection.class).page(pageable));
    }
}
