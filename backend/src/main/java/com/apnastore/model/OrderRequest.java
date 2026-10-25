package com.apnastore.model;

import java.util.List;

public class OrderRequest {
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String postalCode;
    private String paymentMethod;
    private String promoCode;
    private List<OrderItem> items;

    public OrderRequest() {}

    public static class OrderItem {
        private String productId;
        private int quantity;
        private String selectedSize;
        private String selectedColor;

        public OrderItem() {}

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public String getSelectedSize() { return selectedSize; }
        public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

        public String getSelectedColor() { return selectedColor; }
        public void setSelectedColor(String selectedColor) { this.selectedColor = selectedColor; }
    }

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
