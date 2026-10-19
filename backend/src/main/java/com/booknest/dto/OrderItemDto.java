package com.booknest.dto;

import com.booknest.entities.OrderItem;
import java.math.BigDecimal;

public class OrderItemDto {
    private Long id;
    private Long bookId;
    private String bookTitleSnapshot;
    private String bookAuthorSnapshot;
    private String bookCoverSnapshot;
    private BigDecimal unitPriceSnapshot;
    private int quantity;
    private BigDecimal lineTotal;

    public OrderItemDto() {}

    public static OrderItemDto from(OrderItem item) {
        if (item == null) return null;
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setBookId(item.getBook() != null ? item.getBook().getId() : null);
        dto.setBookTitleSnapshot(item.getBookTitleSnapshot());
        dto.setBookAuthorSnapshot(item.getBookAuthorSnapshot());
        dto.setBookCoverSnapshot(item.getBookCoverSnapshot());
        dto.setUnitPriceSnapshot(item.getUnitPriceSnapshot());
        dto.setQuantity(item.getQuantity());
        dto.setLineTotal(item.getLineTotal());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBookTitleSnapshot() { return bookTitleSnapshot; }
    public void setBookTitleSnapshot(String bookTitleSnapshot) { this.bookTitleSnapshot = bookTitleSnapshot; }
    public String getTitle() { return bookTitleSnapshot; }

    public String getBookAuthorSnapshot() { return bookAuthorSnapshot; }
    public void setBookAuthorSnapshot(String bookAuthorSnapshot) { this.bookAuthorSnapshot = bookAuthorSnapshot; }
    public String getAuthor() { return bookAuthorSnapshot; }

    public String getBookCoverSnapshot() { return bookCoverSnapshot; }
    public void setBookCoverSnapshot(String bookCoverSnapshot) { this.bookCoverSnapshot = bookCoverSnapshot; }
    public String getCoverImageUrl() { return bookCoverSnapshot; }
    public String getImageUrl() { return bookCoverSnapshot; }

    public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }
    public void setUnitPriceSnapshot(BigDecimal unitPriceSnapshot) { this.unitPriceSnapshot = unitPriceSnapshot; }
    public BigDecimal getUnitPrice() { return unitPriceSnapshot; }
    public BigDecimal getPrice() { return unitPriceSnapshot; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
