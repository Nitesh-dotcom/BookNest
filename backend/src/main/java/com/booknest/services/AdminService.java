package com.booknest.services;

import com.booknest.dto.*;
import com.booknest.entities.Book;
import com.booknest.entities.Order;
import com.booknest.entities.OrderStatus;
import com.booknest.entities.OrderItem;
import com.booknest.entities.PaymentStatus;
import com.booknest.entities.Role;
import com.booknest.exceptions.BadRequestException;
import com.booknest.exceptions.ResourceNotFoundException;
import com.booknest.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public AdminService(BookRepository bookRepository,
                        OrderRepository orderRepository,
                        UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalBooks(bookRepository.count());
        Long stock = bookRepository.sumTotalActiveStock();
        stats.setActiveInventory(stock != null ? stock : 0L);

        stats.setTotalOrders(orderRepository.count());
        long pending = orderRepository.countByOrderStatus(OrderStatus.PENDING);
        long confirmed = orderRepository.countByOrderStatus(OrderStatus.CONFIRMED);
        long processing = orderRepository.countByOrderStatus(OrderStatus.PROCESSING);
        stats.setOrdersAwaitingProcessing(pending + confirmed + processing);

        BigDecimal revenue = orderRepository.calculateTotalGrossRevenue();
        stats.setGrossRevenue(revenue != null ? revenue : BigDecimal.ZERO);

        stats.setTotalCustomers(userRepository.countByRole(Role.CUSTOMER));

        Pageable recentPageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<OrderDto> recent = orderRepository.findAllByOrderByCreatedAtDesc(recentPageable)
                .getContent().stream()
                .map(OrderDto::from)
                .collect(Collectors.toList());
        stats.setRecentOrders(recent);

        return stats;
    }

    @Transactional(readOnly = true)
    public PagedResponse<OrderDto> getAllOrders(OrderStatus status, String search, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orderPage = orderRepository.searchOrders(status, search, pageable);
        List<OrderDto> dtos = orderPage.getContent().stream().map(OrderDto::from).collect(Collectors.toList());
        return PagedResponse.from(orderPage, dtos);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus newStatus = request.getStatus();

        if (currentStatus == newStatus) {
            return OrderDto.from(order);
        }

        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        // If transitioning to CANCELLED, restore stock
        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Book book = item.getBook();
                book.setStockQuantity(book.getStockQuantity() + item.getQuantity());
                bookRepository.save(book);
            }
            if (order.getPaymentStatus() == PaymentStatus.PAID) {
                order.setPaymentStatus(PaymentStatus.REFUNDED);
            }
        } else if (newStatus == OrderStatus.DELIVERED && order.getPaymentStatus() == PaymentStatus.PENDING) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        order.setOrderStatus(newStatus);
        return OrderDto.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getCustomers() {
        return userRepository.findByRole(Role.CUSTOMER)
                .stream()
                .map(UserDto::from)
                .collect(Collectors.toList());
    }
}
