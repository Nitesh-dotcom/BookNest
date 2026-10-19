package com.booknest.dto;

import jakarta.validation.constraints.NotNull;

public class AddToWishlistRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    public AddToWishlistRequest() {}

    public AddToWishlistRequest(Long bookId) {
        this.bookId = bookId;
    }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
}
