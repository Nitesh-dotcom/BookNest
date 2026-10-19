package com.booknest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockUpdateRequest {

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    public StockUpdateRequest() {}

    public StockUpdateRequest(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getQuantity() { return stockQuantity; }
    public void setQuantity(Integer quantity) {
        if (this.stockQuantity == null) {
            this.stockQuantity = quantity;
        }
    }
}
