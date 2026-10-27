package com.apnastore.service;

import com.apnastore.dto.OrderRequest;
import com.apnastore.dto.OrderResponse;
import com.apnastore.exception.BadRequestException;
import com.apnastore.exception.InsufficientStockException;
import com.apnastore.exception.ResourceNotFoundException;
import com.apnastore.model.Order;
import com.apnastore.model.OrderItem;
import com.apnastore.model.Product;
import com.apnastore.model.User;
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

    /**
     * Creates an order with strict server-side price calculation,
     * promo code APNA20 validation, and transactional stock deduction.
     * @param user The authenticated user (null for unauthenticated/guest orders).
     */
    public OrderResponse createOrder(OrderRequest request, User user) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }

        // Aggregate requested quantities per product to check against available stock
        Map<String, Integer> requestedQuantities = new LinkedHashMap<>();
        for (OrderRequest.OrderItem itemReq : request.getItems()) {
            if (itemReq.getProductId() == null || itemReq.getProductId().trim().isEmpty()) {
                throw new BadRequestException("Product ID is required for each order item");
            }
            if (itemReq.getQuantity() <= 0) {
                throw new BadRequestException("Quantity must be greater than 0 for product ID: " + itemReq.getProductId());
            }
            requestedQuantities.merge(itemReq.getProductId().trim(), itemReq.getQuantity(), Integer::sum);
        }

        // 1. Verify existence of all products & check stock
        Map<String, Product> productMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : requestedQuantities.entrySet()) {
            String prodId = entry.getKey();
            int qtyNeeded = entry.getValue();

            Product prod = productRepository.findById(prodId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + prodId));

            if (prod.getStock() < qtyNeeded) {
                throw new InsufficientStockException(String.format(
                    "Insufficient stock for product '%s' (ID: %s). Available stock: %d, requested: %d",
                    prod.getName(), prod.getId(), prod.getStock(), qtyNeeded
                ));
            }
            productMap.put(prodId, prod);
        }

        // 2. Reduce stock inside @Transactional method
        for (Map.Entry<String, Integer> entry : requestedQuantities.entrySet()) {
            Product prod = productMap.get(entry.getKey());
            prod.setStock(prod.getStock() - entry.getValue());
            productRepository.save(prod);
        }

        // 3. Create Order entity and calculate totals strictly from DB prices
        String orderNumber = "APNA-" + (100000 + new Random().nextInt(900000));
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setCustomerName(request.getFullName() != null && !request.getFullName().trim().isEmpty() 
            ? request.getFullName().trim() 
            : "Valued Customer");
        order.setEmail(request.getEmail());
        order.setPhone(request.getPhone());
        order.setAddress(request.getAddress());
        order.setCity(request.getCity());
        order.setPostalCode(request.getPostalCode());
        order.setPaymentMethod(request.getPaymentMethod() != null && !request.getPaymentMethod().trim().isEmpty()
            ? request.getPaymentMethod()
            : "Credit Card");
        order.setPromoCode(request.getPromoCode());

        double subtotal = 0.0;
        List<OrderResponse.ProductSummary> summaryList = new ArrayList<>();

        for (OrderRequest.OrderItem itemReq : request.getItems()) {
            Product prod = productMap.get(itemReq.getProductId().trim());
            // NEVER trust frontend prices - use prod.getPrice() from database
            double unitPrice = prod.getPrice();
            int quantity = itemReq.getQuantity();
            double itemTotal = unitPrice * quantity;
            subtotal += itemTotal;

            OrderItem orderItem = new OrderItem(
                order,
                prod,
                quantity,
                unitPrice,
                itemReq.getSelectedSize(),
                itemReq.getSelectedColor()
            );
            order.addItem(orderItem);

            summaryList.add(new OrderResponse.ProductSummary(
                prod.getId(),
                prod.getName(),
                unitPrice,
                quantity,
                prod.getImage()
            ));
        }

        // 4. Server-side promo code validation (APNA20 gives 20% off)
        double discount = 0.0;
        if (request.getPromoCode() != null && !request.getPromoCode().trim().isEmpty()) {
            String promo = request.getPromoCode().trim().toUpperCase();
            if ("APNA20".equals(promo) || "ROYAL20".equals(promo) || "LUMIERE20".equals(promo)) {
                discount = subtotal * 0.20; // 20% off
            } else if ("ROYAL50".equals(promo) || "VIP50".equals(promo)) {
                discount = Math.min(50.0, subtotal); // Flat $50 off
            } else if ("WELCOME10".equals(promo)) {
                discount = subtotal * 0.10; // 10% off
            }
        }

        double shipping = subtotal > 300.0 ? 0.0 : 25.0;
        double total = Math.max(0.0, subtotal - discount + shipping);

        subtotal = Math.round(subtotal * 100.0) / 100.0;
        discount = Math.round(discount * 100.0) / 100.0;
        shipping = Math.round(shipping * 100.0) / 100.0;
        total = Math.round(total * 100.0) / 100.0;

        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setShippingFee(shipping);
        order.setTotalAmount(total);

        // Link order to the authenticated user (if present)
        if (user != null) {
            order.setUser(user);
        }

        // Save order and cascading order items
        orderRepository.save(order);

        // Map to OrderResponse DTO
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

    /**
     * Returns all orders placed by the given user, newest first.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
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
