package org.example.gameshop.model.projection;

import org.example.gameshop.model.GameType;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface GameSaleProjection {
    Long getId();

    Integer getGameNumber();

    String getGameName();

    String getGameCode();

    GameType getGameType();

    BigDecimal getCostPrice();

    BigDecimal getTax();

    BigDecimal getSalePrice();

    LocalDate getDateOfSale();
}
