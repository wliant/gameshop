package org.example.gameshop.model.aggregate;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@IdClass(GameSaleAggregateId.class)
public class GameSaleGameNoAggregate {

    @Id
    private LocalDate dateOfSale;

    @Id
    private Integer gameNo;

    @Column
    private Long saleCount;

    @Column(precision = 19, scale = 2)
    private BigDecimal saleSum;

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

    public Long getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(Long saleCount) {
        this.saleCount = saleCount;
    }

    public BigDecimal getSaleSum() {
        return saleSum;
    }

    public void setSaleSum(BigDecimal saleSum) {
        this.saleSum = saleSum;
    }
}
