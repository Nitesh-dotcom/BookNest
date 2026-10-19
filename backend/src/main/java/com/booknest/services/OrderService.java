package com.booknest.services;

import com.booknest.dto.CreateOrderRequest;
import com.booknest.dto.OrderDto;
import com.booknest.dto.PagedResponse;
import com.booknest.entities.*;
import com.booknest.exceptions.BadRequestException;
import com.booknest.exceptions.InsufficientStockException;
import com.booknest.exceptions.ResourceNotFoundException;
import com.booknest.exceptions.UnauthorizedException;
import com.booknest.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        BookRepository bookRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public OrderDto createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Shopping cart is empty"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cannot checkout with an empty cart");
        }

        // 1. Verify stock for all items before any deduction
        for (CartItem item : cartItems) {
            Book book = item.getBook();
            if (!book.isActive()) {
                throw new BadRequestException("The book '" + book.getTitle() + "' is no longer active in our catalog");
            }
            if (book.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for '" + book.getTitle() + "'. Available: " +
                        book.getStockQuantity() + ", requested: " + item.getQuantity());
            }
        }

        // 2. Calculate authoritative server-side totals
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        Order order = new Order();
        String orderNumber = "BN-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setCustomerName(request.getCustomerName().trim());
        order.setCustomerPhone(request.getCustomerPhone().trim());
        order.setDeliveryAddress(request.getDeliveryAddress().trim());
        order.setCity(request.getCity().trim());
        order.setState(request.getState().trim());
        order.setPostalCode(request.getPostalCode().trim());
        order.setDeliveryInstructions(request.getDeliveryInstructions());
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.COD);

        if (order.getPaymentMethod() == PaymentMethod.SIMULATED_CARD) {
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderStatus(OrderStatus.CONFIRMED);
        } else {
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setOrderStatus(OrderStatus.PENDING);
        }

        for (CartItem cartItem : cartItems) {
            Book book = cartItem.getBook();
            int qty = cartItem.getQuantity();
            BigDecimal unitPrice = book.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            subtotal = subtotal.add(lineTotal);

            // Deduct stock atomically
            book.setStockQuantity(book.getStockQuantity() - qty);
            bookRepository.save(book);

            // Create OrderItem snapshot
            OrderItem orderItem = new OrderItem(
                    order,
                    book,
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCoverImageUrl(),
                    unitPrice,
                    qty,
                    lineTotal
            );
            orderItems.add(orderItem);
        }

        BigDecimal tax = subtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shippingFee = (subtotal.compareTo(new BigDecimal("50.00")) >= 0)
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : new BigDecimal("4.99");
        BigDecimal totalAmount = subtotal.add(tax).add(shippingFee).setScale(2, RoundingMode.HALF_UP);

        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // 3. Clear cart items after successful order creation
        cartItemRepository.deleteAll(cartItems);
        if (cart.getItems() != null) {
            cart.getItems().clear();
        }

        return OrderDto.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public PagedResponse<OrderDto> getCustomerOrders(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orderPage = orderRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        List<OrderDto> dtos = orderPage.getContent().stream().map(OrderDto::from).collect(Collectors.toList());
        return PagedResponse.from(orderPage, dtos);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long userId, Long orderId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have permission to view this order");
        }

        return OrderDto.from(order);
    }

    @Transactional
    public OrderDto cancelOrder(Long userId, Long orderId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have permission to cancel this order");
        }

        if (!order.getOrderStatus().isCancellableByCustomer() && !isAdmin) {
            throw new BadRequestException("Order cannot be cancelled in its current state: " + order.getOrderStatus());
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }

        // Restore inventory for all items
        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            book.setStockQuantity(book.getStockQuantity() + item.getQuantity());
            bookRepository.save(book);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        return OrderDto.from(orderRepository.save(order));
    }
}
