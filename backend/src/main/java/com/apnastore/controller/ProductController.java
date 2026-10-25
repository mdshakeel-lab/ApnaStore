package com.apnastore.controller;

import com.apnastore.model.Category;
import com.apnastore.model.Product;
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

    // GET /api/products
    @GetMapping
    public List<Product> getAllProducts(@RequestParam(value = "category", required = false) String category,
                                         @RequestParam(value = "q", required = false) String query) {
        return productService.filterProducts(category, query);
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/products/categories
    @GetMapping("/categories")
    public List<Category> getCategories() {
        return productService.getCategories();
    }
}
