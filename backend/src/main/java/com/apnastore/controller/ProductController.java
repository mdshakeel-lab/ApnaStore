package com.apnastore.controller;

import com.apnastore.dto.CategoryResponse;
import com.apnastore.dto.PageResponse;
import com.apnastore.dto.ProductResponse;
import com.apnastore.exception.ResourceNotFoundException;
import com.apnastore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Allows cross-origin requests from frontend
public class ProductController {

    @Autowired
    private ProductService productService;

    // GET /api/products (supports optional page, size, sort, category, q)
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {

        if (page == null && size == null) {
            // Default unpaged behavior: returns List<ProductResponse>
            List<ProductResponse> products = productService.filterProducts(category, query, sort);
            return ResponseEntity.ok(products);
        }

        // Paged behavior: returns PageResponse<ProductResponse>
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;
        PageResponse<ProductResponse> paged = productService.filterProductsPaged(category, query, pageNumber, pageSize, sort);
        return ResponseEntity.ok(paged);
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse product = productService.getProductResponseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return ResponseEntity.ok(product);
    }

    // GET /api/products/categories
    @GetMapping("/categories")
    public List<CategoryResponse> getCategories() {
        return productService.getCategories();
    }
}
