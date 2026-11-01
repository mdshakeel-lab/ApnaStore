package com.apnastore.integration;

import com.apnastore.dto.LoginRequest;
import com.apnastore.dto.OrderRequest;
import com.apnastore.dto.RegisterRequest;
import com.apnastore.model.Order;
import com.apnastore.model.Product;
import com.apnastore.repository.OrderRepository;
import com.apnastore.repository.ProductRepository;
import com.apnastore.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("End-to-End Order Flow Integration Test")
class OrderFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Complete E2E Flow: register -> login -> place order with JWT -> verify DB order & stock deduction")
    void completeOrderFlow_WithAuthAndStockDeduction() throws Exception {
        // 0. Verify initial product stock in in-memory H2 database
        Product initialProduct = productRepository.findById("prod-ind-01").orElseThrow();
        int initialStock = initialProduct.getStock();
        int orderQuantity = 2;
        assertThat(initialStock).isGreaterThanOrEqualTo(orderQuantity);

        // 1. Register a new customer
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Kavita Sen");
        registerRequest.setEmail("kavita.flow@example.com");
        registerRequest.setPassword("RoyalSecurePass@2026");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("kavita.flow@example.com"))
                .andExpect(jsonPath("$.token").isString());

        // 2. Login to retrieve authenticated JWT token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("kavita.flow@example.com");
        loginRequest.setPassword("RoyalSecurePass@2026");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String jwtToken = loginJson.get("token").asText();
        assertThat(jwtToken).isNotBlank();

        // 3. Place order using Bearer JWT authentication header
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setFullName("Kavita Sen");
        orderRequest.setEmail("kavita.flow@example.com");
        orderRequest.setPhone("+91 98765 43210");
        orderRequest.setAddress("74 Royal Palace Avenue");
        orderRequest.setCity("Udaipur");
        orderRequest.setPostalCode("313001");
        orderRequest.setPaymentMethod("Credit Card");
        orderRequest.setPromoCode("APNA20");
        orderRequest.setItems(List.of(
            new OrderRequest.OrderItem("prod-ind-01", orderQuantity, "M", "Royal Maroon")
        ));

        MvcResult orderResult = mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", startsWith("APNA-")))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.customerName").value("Kavita Sen"))
                .andReturn();

        JsonNode orderResponseJson = objectMapper.readTree(orderResult.getResponse().getContentAsString());
        String orderNumber = orderResponseJson.get("orderId").asText();

        // 4. Verify order entity is persisted in the H2 database and linked to the user
        Optional<Order> persistedOrderOpt = orderRepository.findByOrderNumber(orderNumber);
        assertThat(persistedOrderOpt).isPresent();
        Order persistedOrder = persistedOrderOpt.get();
        assertThat(persistedOrder.getUser()).isNotNull();
        assertThat(persistedOrder.getItems()).hasSize(1);
        assertThat(persistedOrder.getTotalAmount()).isGreaterThan(0.0);

        // Verify user relationship by querying user repository with the linked user ID
        com.apnastore.model.User orderUser = userRepository.findById(persistedOrder.getUser().getId()).orElseThrow();
        assertThat(orderUser.getEmail()).isEqualTo("kavita.flow@example.com");

        // 5. Verify product stock in H2 database was decremented by ordered quantity
        Product updatedProduct = productRepository.findById("prod-ind-01").orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(initialStock - orderQuantity);
    }
}
