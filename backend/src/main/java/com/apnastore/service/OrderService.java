package com.apnastore.service;

import com.apnastore.model.OrderRequest;
import com.apnastore.model.OrderResponse;
import com.apnastore.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private ProductService productService;

    private final Map<String, OrderResponse> orderDatabase = new HashMap<>();

    public OrderResponse createOrder(OrderRequest request) {
        String orderId = "APNA-" + (100000 + new Random().nextInt(900000));
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));

        double subtotal = 0.0;
        List<OrderResponse.ProductSummary> summaryList = new ArrayList<>();

        if (request.getItems() != null) {
            for (OrderRequest.OrderItem item : request.getItems()) {
                Optional<Product> prodOpt = productService.getProductById(item.getProductId());
                if (prodOpt.isPresent()) {
                    Product prod = prodOpt.get();
                    double itemTotal = prod.getPrice() * item.getQuantity();
                    subtotal += itemTotal;

                    summaryList.add(new OrderResponse.ProductSummary(
                        prod.getId(),
                        prod.getName(),
                        prod.getPrice(),
                        item.getQuantity(),
                        prod.getImage()
                    ));
                }
            }
        }

        double discount = 0.0;
        if (request.getPromoCode() != null) {
            String promo = request.getPromoCode().trim().toUpperCase();
            if ("APNA20".equals(promo) || "ROYAL20".equals(promo) || "LUMIERE20".equals(promo)) {
                discount = subtotal * 0.20; // 20% off
            } else if ("ROYAL50".equals(promo) || "VIP50".equals(promo)) {
                discount = Math.min(50.0, subtotal); // Flat $50 off
            } else if ("WELCOME10".equals(promo)) {
                discount = subtotal * 0.10; // 10% off
            }
        }

        double shipping = subtotal > 300 ? 0.0 : 25.0;
        double total = Math.max(0, subtotal - discount + shipping);

        OrderResponse response = new OrderResponse();
        response.setOrderId(orderId);
        response.setStatus("CONFIRMED");
        response.setDate(date);
        response.setCustomerName(request.getFullName());
        response.setShippingAddress(request.getAddress() + ", " + request.getCity());
        response.setSubtotal(subtotal);
        response.setDiscount(discount);
        response.setShippingFee(shipping);
        response.setTotalAmount(total);
        response.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "Credit Card");
        response.setItems(summaryList);

        orderDatabase.put(orderId, response);
        return response;
    }

    public Optional<OrderResponse> getOrderById(String orderId) {
        return Optional.ofNullable(orderDatabase.get(orderId));
    }
}
