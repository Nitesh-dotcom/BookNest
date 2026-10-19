package com.booknest.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CartDto {
    private Long id;
    private List<CartItemDto> items = new ArrayList<>();
    private int itemCount = 0;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;
    private BigDecimal shippingFee = BigDecimal.ZERO;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public CartDto() {}

    public static CartDto of(Long id, List<CartItemDto> items) {
        CartDto dto = new CartDto();
        dto.setId(id);
        dto.setItems(items != null ? items : new ArrayList<>());

        int count = 0;
        BigDecimal sub = BigDecimal.ZERO;

        for (CartItemDto item : dto.getItems()) {
            count += item.getQuantity();
            sub = sub.add(item.getLineTotal());
        }

        dto.setItemCount(count);
        dto.setSubtotal(sub.setScale(2, RoundingMode.HALF_UP));

        // 5% standard tax calculation
        BigDecimal calculatedTax = sub.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        dto.setTax(calculatedTax);

        // Free shipping if subtotal >= $50, else $4.99
        BigDecimal shipping = (sub.compareTo(new BigDecimal("50.00")) >= 0 || sub.compareTo(BigDecimal.ZERO) == 0)
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : new BigDecimal("4.99");
        dto.setShippingFee(shipping);

        dto.setTotalAmount(sub.add(calculatedTax).add(shipping).setScale(2, RoundingMode.HALF_UP));
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
