package com.booknest.controllers;

import com.booknest.dto.*;
import com.booknest.entities.OrderStatus;
import com.booknest.services.AdminService;
import com.booknest.services.BookService;
import com.booknest.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final BookService bookService;
    private final CategoryService categoryService;

    public AdminController(AdminService adminService,
                           BookService bookService,
                           CategoryService categoryService) {
        this.adminService = adminService;
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    // --- Book Management ---
    @PostMapping("/books")
    public ResponseEntity<ApiResponse<BookDto>> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookDto book = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Book created successfully", book));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<ApiResponse<BookDto>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request) {
        BookDto book = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Book updated successfully", book));
    }

    @GetMapping("/books")
    public ResponseEntity<ApiResponse<PagedResponse<BookDto>>> getAdminBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<BookDto> books = bookService.getBooks(
                query, categoryId, null, null, null, false, false,
                page, size, "createdAt", "DESC"
        );
        return ResponseEntity.ok(ApiResponse.ok(books));
    }

    @PatchMapping("/books/{id}/toggle-active")
    public ResponseEntity<ApiResponse<BookDto>> toggleBookActive(@PathVariable Long id) {
        BookDto book = bookService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.ok("Book active status updated", book));
    }

    @PatchMapping("/books/{id}/stock")
    public ResponseEntity<ApiResponse<BookDto>> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {
        BookDto book = bookService.updateStock(id, request.getStockQuantity());
        return ResponseEntity.ok(ApiResponse.ok("Stock updated successfully", book));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.ok("Book deactivated successfully", null));
    }

    // --- Category Management ---
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryDto category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Category created successfully", category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryDto category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Category updated successfully", category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok("Category deactivated successfully", null));
    }

    // --- Order Management ---
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PagedResponse<OrderDto>>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        PagedResponse<OrderDto> orders = adminService.getAllOrders(status, search, page, size);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderDto order = adminService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Order status updated successfully", order));
    }

    // --- Customer Management ---
    @GetMapping({"/customers", "/users"})
    public ResponseEntity<ApiResponse<List<UserDto>>> getCustomers() {
        List<UserDto> customers = adminService.getCustomers();
        return ResponseEntity.ok(ApiResponse.ok(customers));
    }
}
