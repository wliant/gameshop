package org.example.gameshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.example.gameshop.model.converter.GameTypeConverter;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDate;

@MappedSuperclass
public abstract class AbstractGameSale implements Persistable<Long> {
    @Column(name = "game_no")
    @Min(1)
    @Max(100)
    private Integer gameNumber;

    @Column(name = "game_name", length = 20)
    @Size(min=1, max = 20)
    private String gameName;

    @Column(name = "game_code", length = 5)
    @Size(min=1, max = 5)
    private String gameCode;

    @Column(name = "type", columnDefinition = "INTEGER")
    @Convert(converter = GameTypeConverter.class)
    @NotNull
    private GameType gameType;

    @Column(name = "cost_price", precision = 5, scale = 2)
    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value= "100")
    private BigDecimal costPrice;

    @Column(name = "tax", precision = 3, scale = 2)
    private BigDecimal tax = new BigDecimal("0.09");

    @Column(name = "sale_price", precision = 5, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "date_of_sale", columnDefinition = "TIMESTAMP(3)")
    @NotNull
    private LocalDate dateOfSale;

    @Transient
    protected boolean isNew = true;

    @PrePersist
    public void calculateSalePrice() {
        if (costPrice != null) {
            salePrice = costPrice.add(costPrice.multiply(tax));
        }
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public Integer getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(Integer gameNumber) {
        this.gameNumber = gameNumber;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType type) {
        this.gameType = type;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public LocalDate getDateOfSale() {
        return dateOfSale;
    }

    public void setDateOfSale(LocalDate dateOfSale) {
        this.dateOfSale = dateOfSale;
    }
}
