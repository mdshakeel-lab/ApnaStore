package com.apnastore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    private String id; // e.g. "prod-ind-01"

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private double price;
    private double originalPrice;

    private String imageUrl;
    private String secondaryImage;

    private int stock; // Maps to stock requirement

    private double rating;
    private int reviewCount;
    private String badge;
    private String gender;
    private boolean isFeatured;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_details", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "detail")
    private List<String> details = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_sizes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "size")
    private List<String> sizes = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_colors", joinColumns = @JoinColumn(name = "product_id"))
    private List<ColorOption> colors = new ArrayList<>();

    public Product() {}

    public Product(String id, String name, Category category, String gender, double price, double originalPrice,
                   double rating, int reviewCount, String badge, String imageUrl, String secondaryImage,
                   String description, List<String> details, List<String> sizes, List<ColorOption> colors,
                   int stock, boolean isFeatured) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.gender = gender;
        this.price = price;
        this.originalPrice = originalPrice;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.badge = badge;
        this.imageUrl = imageUrl;
        this.secondaryImage = secondaryImage;
        this.description = description;
        this.details = details != null ? details : new ArrayList<>();
        this.sizes = sizes != null ? sizes : new ArrayList<>();
        this.colors = colors != null ? colors : new ArrayList<>();
        this.stock = stock;
        this.isFeatured = isFeatured;
    }

    @Embeddable
    public static class ColorOption {
        private String name;
        private String hex;

        public ColorOption() {}

        public ColorOption(String name, String hex) {
            this.name = name;
            this.hex = hex;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getHex() { return hex; }
        public void setHex(String hex) { this.hex = hex; }
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
    public void setStockCount(int stockCount) { this.stock = stockCount; }

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

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public Category getCategoryEntity() { return category; }
    public void setCategory(Category category) { this.category = category; }

    @JsonProperty("category")
    public String getCategory() {
        return category != null ? category.getId() : null;
    }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }

    public List<String> getSizes() { return sizes; }
    public void setSizes(List<String> sizes) { this.sizes = sizes; }

    public List<ColorOption> getColors() { return colors; }
    public void setColors(List<ColorOption> colors) { this.colors = colors; }
}
