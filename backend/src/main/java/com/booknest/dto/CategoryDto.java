package com.booknest.dto;

import com.booknest.entities.Category;

public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private boolean active;

    public CategoryDto() {}

    public CategoryDto(Long id, String name, String slug, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.active = active;
    }

    public static CategoryDto from(Category c) {
        if (c == null) return null;
        return new CategoryDto(
                c.getId(),
                c.getName(),
                c.getSlug(),
                c.getDescription(),
                c.isActive()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
