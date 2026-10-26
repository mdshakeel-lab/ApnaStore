package com.apnastore.dto;

import com.apnastore.model.Category;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryResponse {

    private String id;
    private String name;
    private Integer count;

    public CategoryResponse() {}

    public CategoryResponse(String id, String name, Integer count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public static CategoryResponse fromEntity(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getCount()
        );
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

    @JsonProperty("count")
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @JsonProperty("productCount")
    public Integer getProductCount() {
        return count;
    }
}
