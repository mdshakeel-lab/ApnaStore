package com.apnastore.service;

import com.apnastore.model.*;
import com.apnastore.repository.OrderRepository;
import com.apnastore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public OrderResponse createOrder(OrderRequest request) {
        String orderNumber = "APNA-" + (100000 + new Random().nextInt(900000));
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setCustomerName(request.getFullName() != null ? request.getFullName() : "Valued Customer");
        order.setEmail(request.getEmail());
        order.setPhone(request.getPhone());
        order.setAddress(request.getAddress());
        order.setCity(request.getCity());
        order.setPostalCode(request.getPostalCode());
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "Credit Card");
        order.setPromoCode(request.getPromoCode());

        double subtotal = 0.0;
        List<OrderResponse.ProductSummary> summaryList = new ArrayList<>();

        if (request.getItems() != null) {
            for (OrderRequest.OrderItem itemReq : request.getItems()) {
                Optional<Product> prodOpt = productRepository.findById(itemReq.getProductId());
                if (prodOpt.isPresent()) {
                    Product prod = prodOpt.get();
                    double itemTotal = prod.getPrice() * itemReq.getQuantity();
                    subtotal += itemTotal;

                    OrderItem orderItem = new OrderItem(
                        order,
                        prod,
                        itemReq.getQuantity(),
                        prod.getPrice(),
                        itemReq.getSelectedSize(),
                        itemReq.getSelectedColor()
                    );
                    order.addItem(orderItem);

                    summaryList.add(new OrderResponse.ProductSummary(
                        prod.getId(),
                        prod.getName(),
                        prod.getPrice(),
                        itemReq.getQuantity(),
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

        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setShippingFee(shipping);
        order.setTotalAmount(total);

        // Save order and cascading order items to database
        orderRepository.save(order);

        // Map to standard response format expected by client
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderNumber());
        response.setStatus(order.getStatus());
        response.setDate(formattedDate);
        response.setCustomerName(order.getCustomerName());
        response.setShippingAddress(order.getAddress() + (order.getCity() != null ? ", " + order.getCity() : ""));
        response.setSubtotal(subtotal);
        response.setDiscount(discount);
        response.setShippingFee(shipping);
        response.setTotalAmount(total);
        response.setPaymentMethod(order.getPaymentMethod());
        response.setItems(summaryList);

        return response;
    }

    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(String id) {
        // Search by orderNumber (e.g., "APNA-123456")
        Optional<Order> orderOpt = orderRepository.findByOrderNumber(id);

        // Fallback: search by database numeric primary key ID if applicable
        if (orderOpt.isEmpty()) {
            try {
                Long orderId = Long.parseLong(id);
                orderOpt = orderRepository.findById(orderId);
            } catch (NumberFormatException ignored) {}
        }

        return orderOpt.map(this::mapToOrderResponse);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderNumber());
        response.setStatus(order.getStatus());
        response.setDate(order.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        response.setCustomerName(order.getCustomerName());
        response.setShippingAddress(order.getAddress() + (order.getCity() != null ? ", " + order.getCity() : ""));
        response.setSubtotal(order.getSubtotal());
        response.setDiscount(order.getDiscount());
        response.setShippingFee(order.getShippingFee());
        response.setTotalAmount(order.getTotalAmount());
        response.setPaymentMethod(order.getPaymentMethod());

        List<OrderResponse.ProductSummary> summaryList = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Product prod = item.getProduct();
                summaryList.add(new OrderResponse.ProductSummary(
                    prod.getId(),
                    prod.getName(),
                    item.getUnitPrice(),
                    item.getQuantity(),
                    prod.getImage()
                ));
            }
        }
        response.setItems(summaryList);
        return response;
    }
}
