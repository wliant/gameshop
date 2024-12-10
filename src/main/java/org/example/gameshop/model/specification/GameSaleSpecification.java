package org.example.gameshop.model.specification;

import org.example.gameshop.model.GameSale;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class GameSaleSpecification {

    private GameSaleSpecification() {
    }

    public static Specification<GameSale> hasDateRange(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null && toDate == null) {
                return null;
            } else if (fromDate == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("dateOfSale"), toDate);
            } else if (toDate == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("dateOfSale"), fromDate);
            } else {
                return criteriaBuilder.between(root.get("dateOfSale"), fromDate, toDate);
            }
        };
    }

    public static Specification<GameSale> hasPriceRange(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            } else if (minPrice == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("salePrice"), maxPrice);
            } else if (maxPrice == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("salePrice"), minPrice);
            } else {
                return criteriaBuilder.between(root.get("salePrice"), minPrice, maxPrice);
            }
        };
    }
}