package com.apnastore.service;

import com.apnastore.model.Category;
import com.apnastore.model.Product;
import com.apnastore.repository.CategoryRepository;
import com.apnastore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public List<Product> getProductsByCategory(String categoryId) {
        if (categoryId == null || categoryId.trim().isEmpty() || "all".equalsIgnoreCase(categoryId.trim())) {
            return productRepository.findAll();
        }
        return productRepository.findByCategory_Id(categoryId.trim());
    }

    public List<Product> searchProducts(String query) {
        return filterProducts(null, query);
    }

    public List<Product> filterProducts(String category, String query) {
        String cat = (category != null && !category.trim().isEmpty()) ? category.trim() : null;
        String q = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        return productRepository.filterProducts(cat, q);
    }
}
