package com.booknest.dto;

import com.booknest.entities.CartItem;
import java.math.BigDecimal;

public class CartItemDto {
    private Long id;
    private BookDto book;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public CartItemDto() {}

    public static CartItemDto from(CartItem item) {
        if (item == null) return null;
        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setBook(BookDto.from(item.getBook()));
        dto.setQuantity(item.getQuantity());
        BigDecimal price = item.getBook().getPrice();
        dto.setUnitPrice(price);
        dto.setLineTotal(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BookDto getBook() { return book; }
    public void setBook(BookDto book) { this.book = book; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
