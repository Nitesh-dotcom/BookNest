package com.booknest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReviewRequest {

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    private Integer rating;

    @Size(max = 150, message = "Headline must not exceed 150 characters")
    private String headline;

    @NotBlank(message = "Review comment is required")
    @Size(min = 10, max = 2000, message = "Review comment must be between 10 and 2000 characters")
    private String comment;

    public CreateReviewRequest() {}

    public CreateReviewRequest(Integer rating, String headline, String comment) {
        this.rating = rating;
        this.headline = headline;
        this.comment = comment;
    }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public void setTitle(String title) { if (this.headline == null) this.headline = title; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public void setBody(String body) { if (this.comment == null) this.comment = body; }
}
