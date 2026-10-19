package com.booknest.dto;

import java.util.ArrayList;
import java.util.List;

public class CartMergeRequest {

    private List<AddToCartRequest> items = new ArrayList<>();

    public CartMergeRequest() {}

    public CartMergeRequest(List<AddToCartRequest> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public List<AddToCartRequest> getItems() { return items; }
    public void setItems(List<AddToCartRequest> items) { this.items = items; }
}
