package com.example.travelinsurance.dto;

import java.math.BigDecimal;

public class PriceCalculationResponse {

    private BigDecimal price;

    public PriceCalculationResponse() {
    }

    public PriceCalculationResponse(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}