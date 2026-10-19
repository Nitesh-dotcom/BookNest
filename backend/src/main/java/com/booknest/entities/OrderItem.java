package com.booknest.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "book_title_snapshot", nullable = false)
    private String bookTitleSnapshot;

    @Column(name = "book_author_snapshot", nullable = false, length = 180)
    private String bookAuthorSnapshot;

    @Column(name = "book_cover_snapshot", length = 500)
    private String bookCoverSnapshot;

    @Column(name = "unit_price_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPriceSnapshot;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal;

    public OrderItem() {}

    public OrderItem(Order order, Book book, String bookTitleSnapshot, String bookAuthorSnapshot,
                     String bookCoverSnapshot, BigDecimal unitPriceSnapshot, int quantity, BigDecimal lineTotal) {
        this.order = order;
        this.book = book;
        this.bookTitleSnapshot = bookTitleSnapshot;
        this.bookAuthorSnapshot = bookAuthorSnapshot;
        this.bookCoverSnapshot = bookCoverSnapshot;
        this.unitPriceSnapshot = unitPriceSnapshot;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public String getBookTitleSnapshot() { return bookTitleSnapshot; }
    public void setBookTitleSnapshot(String bookTitleSnapshot) { this.bookTitleSnapshot = bookTitleSnapshot; }

    public String getBookAuthorSnapshot() { return bookAuthorSnapshot; }
    public void setBookAuthorSnapshot(String bookAuthorSnapshot) { this.bookAuthorSnapshot = bookAuthorSnapshot; }

    public String getBookCoverSnapshot() { return bookCoverSnapshot; }
    public void setBookCoverSnapshot(String bookCoverSnapshot) { this.bookCoverSnapshot = bookCoverSnapshot; }

    public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }
    public void setUnitPriceSnapshot(BigDecimal unitPriceSnapshot) { this.unitPriceSnapshot = unitPriceSnapshot; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
