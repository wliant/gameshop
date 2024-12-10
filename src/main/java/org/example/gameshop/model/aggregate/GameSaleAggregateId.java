package org.example.gameshop.model.aggregate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class GameSaleAggregateId implements Serializable {
    private LocalDate dateOfSale;
    private Integer gameNo;

    public LocalDate getDateOfSale() {
        return dateOfSale;
    }

    public void setDateOfSale(LocalDate dateOfSale) {
        this.dateOfSale = dateOfSale;
    }

    public Integer getGameNo() {
        return gameNo;
    }

    public void setGameNo(Integer gameNo) {
        this.gameNo = gameNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameSaleAggregateId that = (GameSaleAggregateId) o;
        return Objects.equals(dateOfSale, that.dateOfSale) && Objects.equals(gameNo, that.gameNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateOfSale, gameNo);
    }
}
