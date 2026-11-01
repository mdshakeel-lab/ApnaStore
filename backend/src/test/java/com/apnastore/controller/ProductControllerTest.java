package com.apnastore.controller;

import com.apnastore.config.SecurityConfig;
import com.apnastore.dto.ProductResponse;
import com.apnastore.security.JwtAuthFilter;
import com.apnastore.security.JwtUtil;
import com.apnastore.security.UserDetailsServiceImpl;
import com.apnastore.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
@ActiveProfiles("test")
@DisplayName("ProductController WebMvc Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("GET /api/products returns HTTP 200 with product list")
    void getAllProducts_Returns200() throws Exception {
        ProductResponse p1 = new ProductResponse();
        p1.setId("prod-01");
        p1.setName("Royal Silk Lehenga");
        p1.setPrice(1200.0);

        ProductResponse p2 = new ProductResponse();
        p2.setId("prod-02");
        p2.setName("Velvet Sherwani");
        p2.setPrice(950.0);

        when(productService.filterProducts(null, null, null)).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("prod-01"))
                .andExpect(jsonPath("$[0].name").value("Royal Silk Lehenga"))
                .andExpect(jsonPath("$[1].id").value("prod-02"));
    }
}
