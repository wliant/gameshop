package org.example.gameshop.controller;

import org.example.gameshop.model.projection.GameSaleAggregateProjection;
import org.example.gameshop.model.projection.GameSaleProjection;
import org.example.gameshop.service.GameSaleAggregationService;
import org.example.gameshop.service.GameSaleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class GameSaleController {

    private final GameSaleService gameSaleService;
    private final GameSaleAggregationService gameSaleAggregationService;

    public GameSaleController(GameSaleService gameSaleService, GameSaleAggregationService gameSaleAggregationService) {
        this.gameSaleService = gameSaleService;
        this.gameSaleAggregationService = gameSaleAggregationService;
    }


    @GetMapping("/getGameSales")
    public ResponseEntity<Page<GameSaleProjection>> getGameSales(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @PageableDefault(size=100) Pageable pageable) {
        Page<GameSaleProjection> gameSales = gameSaleService.getGameSales(from, to, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(gameSales);
    }

    @GetMapping("/getTotalSales")
    public ResponseEntity<GameSaleAggregateProjection> getTotalSales(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) Integer gameNo) {
        return ResponseEntity.ok(gameSaleAggregationService.getGameSaleAggregates(from, to, gameNo));
    }

    @PostMapping("/calculate")
    public ResponseEntity<Void> calculate() {
        gameSaleAggregationService.aggregateGameSales();
        gameSaleAggregationService.aggregateGameSalesByGameNo();

        return ResponseEntity.ok().build();
    }
}
