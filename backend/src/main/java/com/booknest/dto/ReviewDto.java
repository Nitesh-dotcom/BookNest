package com.booknest.dto;

import com.booknest.entities.Review;
import java.time.LocalDateTime;

public class ReviewDto {
    private Long id;
    private Long bookId;
    private Long userId;
    private String userName;
    private int rating;
    private String headline;
    private String comment;
    private boolean verifiedPurchase;
    private LocalDateTime createdAt;

    public ReviewDto() {}

    public static ReviewDto from(Review r) {
        if (r == null) return null;
        ReviewDto dto = new ReviewDto();
        dto.setId(r.getId());
        dto.setBookId(r.getBook() != null ? r.getBook().getId() : null);
        dto.setUserId(r.getUser() != null ? r.getUser().getId() : null);
        dto.setUserName(r.getUser() != null ? r.getUser().getFullName() : "Anonymous Reader");
        dto.setRating(r.getRating());
        dto.setHeadline(r.getHeadline());
        dto.setComment(r.getComment());
        dto.setVerifiedPurchase(r.isVerifiedPurchase());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getReviewerName() { return userName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getTitle() { return headline; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getBody() { return comment; }

    public boolean isVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
