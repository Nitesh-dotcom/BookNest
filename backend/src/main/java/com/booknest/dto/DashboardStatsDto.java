package com.booknest.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardStatsDto {
    private long totalBooks;
    private long activeInventory;
    private long totalOrders;
    private long ordersAwaitingProcessing;
    private BigDecimal grossRevenue = BigDecimal.ZERO;
    private long totalCustomers;
    private List<OrderDto> recentOrders = new ArrayList<>();

    public DashboardStatsDto() {}

    public long getTotalBooks() { return totalBooks; }
    public void setTotalBooks(long totalBooks) { this.totalBooks = totalBooks; }

    public long getActiveInventory() { return activeInventory; }
    public void setActiveInventory(long activeInventory) { this.activeInventory = activeInventory; }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public long getOrdersAwaitingProcessing() { return ordersAwaitingProcessing; }
    public void setOrdersAwaitingProcessing(long ordersAwaitingProcessing) { this.ordersAwaitingProcessing = ordersAwaitingProcessing; }

    public BigDecimal getGrossRevenue() { return grossRevenue; }
    public void setGrossRevenue(BigDecimal grossRevenue) { this.grossRevenue = grossRevenue; }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public List<OrderDto> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<OrderDto> recentOrders) { this.recentOrders = recentOrders; }
}
