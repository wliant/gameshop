package org.example.gameshop.scheduler;

import org.example.gameshop.service.GameSaleAggregationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AggregationTimerTask {

    private static final Logger logger = LoggerFactory.getLogger(AggregationTimerTask.class);

    private final GameSaleAggregationService gameSaleAggregationService;

    public AggregationTimerTask(GameSaleAggregationService gameSaleAggregationService) {
        this.gameSaleAggregationService = gameSaleAggregationService;
    }

    @Scheduled(cron = "${aggregation.cron:0 0 4 * * *}")
    public void performTask() {
        logger.info("AggregationTimerTask started");
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(gameSaleAggregationService::aggregateGameSalesByGameNo)
                .exceptionally(ex -> {
                    logger.error("Error in aggregateGameSalesByGameNo", ex);
                    return null;
                });

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(gameSaleAggregationService::aggregateGameSales)
                .exceptionally(ex -> {
                    logger.error("Error in aggregateGameSales", ex);
                    return null;
                });

        CompletableFuture.allOf(future1, future2).join();
    }
}
