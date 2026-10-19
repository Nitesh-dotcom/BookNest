package com.booknest.dto;

import com.booknest.entities.WishlistItem;
import java.time.LocalDateTime;

public class WishlistItemDto {
    private Long id;
    private BookDto book;
    private LocalDateTime addedAt;

    public WishlistItemDto() {}

    public static WishlistItemDto from(WishlistItem item) {
        if (item == null) return null;
        WishlistItemDto dto = new WishlistItemDto();
        dto.setId(item.getId());
        dto.setBook(BookDto.from(item.getBook()));
        dto.setAddedAt(item.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BookDto getBook() { return book; }
    public void setBook(BookDto book) { this.book = book; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
