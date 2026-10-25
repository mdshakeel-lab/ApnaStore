package com.apnastore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    private String id; // e.g., "all", "ethnic-women", "ethnic-men", "outerwear", "jewelry"

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    @Transient
    private Integer count; // Optional override for category badge counts

    public Category() {}

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @JsonProperty("count")
    public Integer getCount() {
        if (count != null) {
            return count;
        }
        return products != null ? products.size() : 0;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @JsonProperty("productCount")
    public Integer getProductCount() {
        return getCount();
    }
}
