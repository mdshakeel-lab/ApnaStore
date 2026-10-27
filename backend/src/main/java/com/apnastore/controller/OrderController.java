package com.apnastore.controller;

import com.apnastore.dto.OrderRequest;
import com.apnastore.dto.OrderResponse;
import com.apnastore.exception.ResourceNotFoundException;
import com.apnastore.model.User;
import com.apnastore.repository.UserRepository;
import com.apnastore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Order management endpoints.
 *
 * POST /api/orders       — place a new order (authenticated)
 * GET  /api/orders/{id}  — look up an order by number or ID (public, but secured by obscurity of order number)
 * GET  /api/orders/my    — list all orders for the logged-in user (authenticated)
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    /** POST /api/orders — requires authentication. */
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            Authentication authentication) {

        User user = resolveUser(authentication);
        OrderResponse response = orderService.createOrder(orderRequest, user);
        return ResponseEntity.ok(response);
    }

    /** GET /api/orders/{id} — retrieve by order number or DB id. */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    /** GET /api/orders/my — returns only the logged-in user's orders. */
    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        User user = resolveUser(authentication);
        if (user == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(orderService.getMyOrders(user));
    }

    /** Helper: look up the User entity from the current Spring Security principal (email). */
    private User resolveUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String email = authentication.getName(); // UserDetails.getUsername() == email
        return userRepository.findByEmail(email).orElse(null);
    }
}
