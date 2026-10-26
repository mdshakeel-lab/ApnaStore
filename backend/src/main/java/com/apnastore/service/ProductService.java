package com.apnastore.service;

import com.apnastore.dto.CategoryResponse;
import com.apnastore.dto.PageResponse;
import com.apnastore.dto.ProductResponse;
import com.apnastore.model.Product;
import com.apnastore.repository.CategoryRepository;
import com.apnastore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
        "id", "name", "price", "originalprice", "rating", "reviewcount", "stock"
    );

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Optional<ProductResponse> getProductResponseById(String id) {
        return productRepository.findById(id)
                .map(ProductResponse::fromEntity);
    }

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> filterProducts(String category, String query, String sortParam) {
        String cat = (category != null && !category.trim().isEmpty()) ? category.trim() : null;
        String q = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        Sort sort = buildSort(sortParam);

        List<Product> products = sort.isSorted()
                ? productRepository.filterProducts(cat, q, sort)
                : productRepository.filterProducts(cat, q);

        return products.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public PageResponse<ProductResponse> filterProductsPaged(String category, String query, int page, int size, String sortParam) {
        String cat = (category != null && !category.trim().isEmpty()) ? category.trim() : null;
        String q = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        Sort sort = buildSort(sortParam);

        Pageable pageable = sort.isSorted()
                ? PageRequest.of(page, size, sort)
                : PageRequest.of(page, size);

        Page<Product> productPage = productRepository.filterProductsPaged(cat, q, pageable);
        Page<ProductResponse> dtoPage = productPage.map(ProductResponse::fromEntity);

        return PageResponse.from(dtoPage);
    }

    public Sort buildSort(String sortParam) {
        if (sortParam == null || sortParam.trim().isEmpty()) {
            return Sort.unsorted();
        }

        String[] parts = sortParam.split(",");
        String property = parts[0].trim();

        if ("stockCount".equalsIgnoreCase(property)) {
            property = "stock";
        }

        String lowerProp = property.toLowerCase();
        if (!ALLOWED_SORT_PROPERTIES.contains(lowerProp)) {
            return Sort.unsorted();
        }

        // Map back to exact camelCase field on entity if needed
        if ("originalprice".equals(lowerProp)) {
            property = "originalPrice";
        } else if ("reviewcount".equals(lowerProp)) {
            property = "reviewCount";
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }

        return Sort.by(direction, property);
    }
}
