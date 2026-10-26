package com.apnastore;

import com.apnastore.dto.OrderRequest;
import com.apnastore.model.Product;
import com.apnastore.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApnaStoreBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Req 1 & 6: GET /api/products returns unpaged list of ProductResponse DTOs by default")
    void testGetProductsDefaultUnpaged() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].price").exists())
                .andExpect(jsonPath("$[0].image").exists())
                .andExpect(jsonPath("$[0].stockCount").exists())
                .andExpect(jsonPath("$[0].inStock").exists());
    }

    @Test
    @DisplayName("Req 6: GET /api/products with page, size and sort returns PageResponse")
    void testGetProductsPagedAndSorted() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "price,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.isFirst").value(true));
    }

    @Test
    @DisplayName("Req 6: Sorting order is correctly ascending by price")
    void testGetProductsSortedPriceAsc() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/products")
                        .param("sort", "price,asc"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(root.isArray()).isTrue();
        double previousPrice = 0.0;
        for (JsonNode item : root) {
            double currentPrice = item.get("price").asDouble();
            assertThat(currentPrice).isGreaterThanOrEqualTo(previousPrice);
            previousPrice = currentPrice;
        }
    }

    @Test
    @DisplayName("Req 2 & 3: Validation failure returns HTTP 400 with consistent ErrorResponse and fieldErrors")
    void testOrderValidationFailure() throws Exception {
        // Missing name, missing address, invalid email, invalid phone, empty items
        OrderRequest invalidReq = new OrderRequest();
        invalidReq.setFullName("");
        invalidReq.setAddress("");
        invalidReq.setEmail("not-an-email");
        invalidReq.setPhone("123");
        invalidReq.setItems(Collections.emptyList());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed for request data"))
                .andExpect(jsonPath("$.fieldErrors.fullName").exists())
                .andExpect(jsonPath("$.fieldErrors.address").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.phone").exists())
                .andExpect(jsonPath("$.fieldErrors.items").exists());
    }

    @Test
    @DisplayName("Req 2 & 3: Item quantity <= 0 fails Bean Validation with HTTP 400")
    void testOrderZeroQuantityValidation() throws Exception {
        OrderRequest req = new OrderRequest();
        req.setFullName("Aarav Sharma");
        req.setAddress("42 Royal Court");
        req.setEmail("aarav@example.com");
        req.setPhone("+91 98765 43210");
        req.setItems(List.of(
            new OrderRequest.OrderItem("prod-ind-01", 0, "M", "Royal Maroon")
        ));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors['items[0].quantity']").value("Item quantity must be greater than 0"));
    }

    @Test
    @DisplayName("Req 3: Non-existent product ID returns HTTP 404 with consistent ErrorResponse")
    void testProductNotFound404() throws Exception {
        mockMvc.perform(get("/api/products/non-existent-id-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message", containsString("Product not found with ID: non-existent-id-999")));
    }

    @Test
    @DisplayName("Req 3 & 5: Insufficient stock returns HTTP 409 Conflict with clear error message")
    void testInsufficientStockReturns409() throws Exception {
        Product prod = productRepository.findById("prod-ind-01").orElseThrow();
        int excessiveQuantity = prod.getStock() + 1000;

        OrderRequest req = new OrderRequest();
        req.setFullName("Deepika Rao");
        req.setAddress("Palace Road, Jaipur");
        req.setEmail("deepika@example.com");
        req.setPhone("+91 91234 56789");
        req.setItems(List.of(
            new OrderRequest.OrderItem(prod.getId(), excessiveQuantity, "M", "Gold")
        ));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message", containsString("Insufficient stock for product")));
    }

    @Test
    @DisplayName("Req 4 & 5: Server calculates price from DB, validates APNA20 20% promo, reduces stock")
    void testSuccessfulOrderWithPromoAndStockDeduction() throws Exception {
        Product prod = productRepository.findById("prod-ind-02").orElseThrow();
        int initialStock = prod.getStock();
        assertThat(initialStock).isGreaterThan(0);
        double dbPrice = prod.getPrice();

        OrderRequest req = new OrderRequest();
        req.setFullName("Rohan Verma");
        req.setAddress("15 Marine Drive");
        req.setEmail("rohan.verma@example.com");
        req.setPhone("+91 98765 12345");
        req.setPromoCode("APNA20"); // 20% discount
        req.setItems(List.of(
            new OrderRequest.OrderItem(prod.getId(), 1, "L", "Navy")
        ));

        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", startsWith("APNA-")))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.customerName").value("Rohan Verma"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        double subtotal = json.get("subtotal").asDouble();
        double discount = json.get("discount").asDouble();
        double total = json.get("totalAmount").asDouble();

        // Verify subtotal matches DB price
        assertThat(subtotal).isEqualTo(dbPrice);

        // Verify APNA20 applied 20% off
        double expectedDiscount = Math.round(dbPrice * 0.20 * 100.0) / 100.0;
        assertThat(discount).isEqualTo(expectedDiscount);

        // Verify shipping is free if subtotal > 300, else 25
        double expectedShipping = subtotal > 300.0 ? 0.0 : 25.0;
        double expectedTotal = Math.round((subtotal - discount + expectedShipping) * 100.0) / 100.0;
        assertThat(total).isEqualTo(expectedTotal);

        // Verify stock was reduced in DB inside @Transactional method
        Product updatedProd = productRepository.findById("prod-ind-02").orElseThrow();
        assertThat(updatedProd.getStock()).isEqualTo(initialStock - 1);
    }
}
