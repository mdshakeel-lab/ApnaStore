package com.apnastore.controller;

import com.apnastore.config.SecurityConfig;
import com.apnastore.dto.OrderRequest;
import com.apnastore.exception.GlobalExceptionHandler;
import com.apnastore.security.JwtAuthFilter;
import com.apnastore.security.JwtUtil;
import com.apnastore.security.UserDetailsServiceImpl;
import com.apnastore.service.OrderService;
import com.apnastore.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
@DisplayName("OrderController WebMvc Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("POST /api/orders without authentication token returns 401 Unauthorized")
    void placeOrder_WithoutToken_Returns401() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setFullName("Aarav Roy");

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message", containsString("Authentication required")));
    }

    @Test
    @WithMockUser(username = "customer@example.com")
    @DisplayName("POST /api/orders with invalid input returns 400 Bad Request with fieldErrors")
    void placeOrder_WithInvalidInput_Returns400WithFieldErrors() throws Exception {
        // Missing mandatory fields: fullName, email, phone, address, items
        OrderRequest invalidRequest = new OrderRequest();

        mockMvc.perform(post("/api/orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed for request data"))
                .andExpect(jsonPath("$.fieldErrors").isMap())
                .andExpect(jsonPath("$.fieldErrors.fullName").value("Customer name is required"))
                .andExpect(jsonPath("$.fieldErrors.email").value("Email is required"))
                .andExpect(jsonPath("$.fieldErrors.phone").value("Phone number is required"))
                .andExpect(jsonPath("$.fieldErrors.address").value("Shipping address is required"))
                .andExpect(jsonPath("$.fieldErrors.items").value("Order must contain at least one item"));
    }

    @Test
    @WithMockUser(username = "customer@example.com")
    @DisplayName("GET /api/orders/9999 returns 404 in standard error format when order does not exist")
    void getOrderById_WhenNotFound_Returns404StandardErrorFormat() throws Exception {
        when(orderService.getOrderById("9999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Order not found with ID: 9999"));
    }
}

