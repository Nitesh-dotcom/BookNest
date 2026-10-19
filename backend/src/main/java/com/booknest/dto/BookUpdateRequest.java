package com.booknest.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BookUpdateRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 180, message = "Author must not exceed 180 characters")
    private String author;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "Original price cannot be negative")
    private BigDecimal originalPrice;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity = 0;

    private String coverImageUrl;
    private String publisher;
    private LocalDate publicationDate;
    private Boolean featured;
    private Boolean bestseller;
    private Boolean active;

    public BookUpdateRequest() {}

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

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

    public String getImageUrl() { return coverImageUrl; }
    public void setImageUrl(String imageUrl) {
        if (this.coverImageUrl == null || this.coverImageUrl.isBlank()) {
            this.coverImageUrl = imageUrl;
        }
    }

    public void setDiscountedPrice(BigDecimal disc) {
        if (disc != null && disc.compareTo(BigDecimal.ZERO) > 0) {
            if (this.price != null && disc.compareTo(this.price) < 0) {
                this.originalPrice = this.price;
                this.price = disc;
            }
        }
    }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }

    public Boolean getBestseller() { return bestseller; }
    public void setBestseller(Boolean bestseller) { this.bestseller = bestseller; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
