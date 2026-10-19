package com.booknest.dto;

import com.booknest.entities.Book;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookDto {
    private Long id;
    private CategoryDto category;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private int stockQuantity;
    private String coverImageUrl;
    private String publisher;
    private LocalDate publicationDate;
    private BigDecimal averageRating;
    private int reviewCount;
    private boolean featured;
    private boolean bestseller;
    private boolean active;
    private boolean inStock;
    private LocalDateTime createdAt;

    public BookDto() {}

    public static BookDto from(Book b) {
        if (b == null) return null;
        BookDto dto = new BookDto();
        dto.setId(b.getId());
        dto.setCategory(CategoryDto.from(b.getCategory()));
        dto.setTitle(b.getTitle());
        dto.setAuthor(b.getAuthor());
        dto.setIsbn(b.getIsbn());
        dto.setDescription(b.getDescription());
        dto.setPrice(b.getPrice());
        dto.setOriginalPrice(b.getOriginalPrice());
        dto.setStockQuantity(b.getStockQuantity());
        dto.setInStock(b.getStockQuantity() > 0 && b.isActive());
        dto.setCoverImageUrl(b.getCoverImageUrl());
        dto.setPublisher(b.getPublisher());
        dto.setPublicationDate(b.getPublicationDate());
        dto.setAverageRating(b.getAverageRating());
        dto.setReviewCount(b.getReviewCount());
        dto.setFeatured(b.isFeatured());
        dto.setBestseller(b.isBestseller());
        dto.setActive(b.isActive());
        dto.setCreatedAt(b.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CategoryDto getCategory() { return category; }
    public void setCategory(CategoryDto category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public boolean isBestseller() { return bestseller; }
    public void setBestseller(boolean bestseller) { this.bestseller = bestseller; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
