package com.apnastore.dto;

import com.apnastore.model.Product;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductResponse {

    private String id;
    private String name;
    private String description;
    private double price;
    private double originalPrice;
    private String imageUrl;
    private String secondaryImage;
    private int stock;
    private double rating;
    private int reviewCount;
    private String badge;
    private String gender;
    private boolean isFeatured;
    private String category;
    private List<String> details = new ArrayList<>();
    private List<String> sizes = new ArrayList<>();
    private List<ColorDto> colors = new ArrayList<>();

    public ProductResponse() {}

    public static class ColorDto {
        private String name;
        private String hex;

        public ColorDto() {}

        public ColorDto(String name, String hex) {
            this.name = name;
            this.hex = hex;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getHex() { return hex; }
        public void setHex(String hex) { this.hex = hex; }
    }

    public static ProductResponse fromEntity(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setSecondaryImage(product.getSecondaryImage());
        dto.setStock(product.getStock());
        dto.setRating(product.getRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setBadge(product.getBadge());
        dto.setGender(product.getGender());
        dto.setFeatured(product.isFeatured());
        dto.setCategory(product.getCategoryEntity() != null ? product.getCategoryEntity().getId() : null);
        dto.setDetails(product.getDetails() != null ? new ArrayList<>(product.getDetails()) : new ArrayList<>());
        dto.setSizes(product.getSizes() != null ? new ArrayList<>(product.getSizes()) : new ArrayList<>());
        if (product.getColors() != null) {
            dto.setColors(product.getColors().stream()
                .map(c -> new ColorDto(c.getName(), c.getHex()))
                .collect(Collectors.toList()));
        }
        return dto;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @JsonProperty("image")
    public String getImage() { return imageUrl; }

    public String getSecondaryImage() { return secondaryImage; }
    public void setSecondaryImage(String secondaryImage) { this.secondaryImage = secondaryImage; }

    @JsonProperty("hoverImage")
    public String getHoverImage() { return secondaryImage; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @JsonProperty("stockCount")
    public int getStockCount() { return stock; }

    @JsonProperty("inStock")
    public boolean isInStock() { return stock > 0; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    @JsonProperty("isFeatured")
    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }

    public List<String> getSizes() { return sizes; }
    public void setSizes(List<String> sizes) { this.sizes = sizes; }

    public List<ColorDto> getColors() { return colors; }
    public void setColors(List<ColorDto> colors) { this.colors = colors; }
}
