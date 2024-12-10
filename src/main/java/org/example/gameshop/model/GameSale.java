package org.example.gameshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "game_sales", indexes = {
        @Index(name = "idx_date_of_sale", columnList = "dateOfSale"),
        @Index(name = "idx_sale_price", columnList = "salePrice")
})
public class GameSale extends AbstractGameSale {
    @Id
    private Long id;

    @Override
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }
}
