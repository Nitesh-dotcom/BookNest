package com.booknest.controllers;

import com.booknest.dto.ApiResponse;
import com.booknest.dto.BookDto;
import com.booknest.dto.PagedResponse;
import com.booknest.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<BookDto>>> getBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStockOnly,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "12") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) String sortDirection) {

        String effectiveQuery = (query != null && !query.isBlank()) ? query : search;
        Long effectiveCategoryId = categoryId != null ? categoryId : category;
        boolean effectiveInStock = Boolean.TRUE.equals(inStockOnly) || Boolean.TRUE.equals(inStock);

        String sortField = sortBy;
        String sortDir = (sortDirection != null && !sortDirection.isBlank()) ? sortDirection : "DESC";

        if (sortBy != null && sortBy.contains("_")) {
            String[] parts = sortBy.split("_", 2);
            sortField = parts[0];
            sortDir = parts[1].toUpperCase();
        }

        PagedResponse<BookDto> books = bookService.getBooks(
                effectiveQuery, effectiveCategoryId, author, minPrice, maxPrice, effectiveInStock, true,
                page, size, sortField, sortDir
        );
        return ResponseEntity.ok(ApiResponse.ok(books));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> getBookById(@PathVariable Long id) {
        BookDto book = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.ok(book));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<BookDto>>> getFeaturedBooks(
            @RequestParam(defaultValue = "8") int limit) {
        List<BookDto> books = bookService.getFeaturedBooks(limit);
        return ResponseEntity.ok(ApiResponse.ok(books));
    }

    @GetMapping("/bestsellers")
    public ResponseEntity<ApiResponse<List<BookDto>>> getBestsellers(
            @RequestParam(defaultValue = "8") int limit) {
        List<BookDto> books = bookService.getBestsellers(limit);
        return ResponseEntity.ok(ApiResponse.ok(books));
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<ApiResponse<List<BookDto>>> getNewArrivals(
            @RequestParam(defaultValue = "8") int limit) {
        List<BookDto> books = bookService.getNewArrivals(limit);
        return ResponseEntity.ok(ApiResponse.ok(books));
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<ApiResponse<List<BookDto>>> getRelatedBooks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "4") int limit) {
        List<BookDto> related = bookService.getRelatedBooks(id, limit);
        return ResponseEntity.ok(ApiResponse.ok(related));
    }
}
