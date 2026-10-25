package com.apnastore.repository;

import com.apnastore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByCategory_Id(String categoryId);

    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR :category = '' OR :category = 'all' OR LOWER(p.category.id) = LOWER(:category) OR LOWER(p.gender) = LOWER(:category)) AND " +
           "(:query IS NULL OR :query = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> filterProducts(@Param("category") String category, @Param("query") String query);
}
