package com.booknest.controllers;

import com.booknest.dto.ApiResponse;
import com.booknest.dto.CreateOrderRequest;
import com.booknest.dto.OrderDto;
import com.booknest.dto.PagedResponse;
import com.booknest.entities.Role;
import com.booknest.security.UserPrincipal;
import com.booknest.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDto order = orderService.createOrder(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order placed successfully!", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OrderDto>>> getCustomerOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<OrderDto> orders = orderService.getCustomerOrders(principal.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        boolean isAdmin = principal.getRole() == Role.ADMIN;
        OrderDto order = orderService.getOrderById(principal.getId(), id, isAdmin);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        boolean isAdmin = principal.getRole() == Role.ADMIN;
        OrderDto order = orderService.cancelOrder(principal.getId(), id, isAdmin);
        return ResponseEntity.ok(ApiResponse.ok("Order has been cancelled successfully", order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrderDelete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return cancelOrder(principal, id);
    }
}
