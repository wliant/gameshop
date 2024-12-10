package org.example.gameshop.model.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface GameSaleAggregateProjection {

    Long getSaleCount();

    BigDecimal getSaleSum();
}
