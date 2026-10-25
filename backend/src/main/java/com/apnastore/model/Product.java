package com.apnastore.model;

import java.util.List;

public class Product {
    private String id;
    private String name;
    private String category;
    private String gender;
    private double price;
    private double originalPrice;
    private double rating;
    private int reviewCount;
    private String badge;
    private String image;
    private String secondaryImage;
    private String description;
    private List<String> details;
    private List<String> sizes;
    private List<ColorOption> colors;
    private boolean inStock;
    private int stockCount;
    private boolean isFeatured;

    public Product() {}

    public Product(String id, String name, String category, String gender, double price, double originalPrice,
                   double rating, int reviewCount, String badge, String image, String secondaryImage,
                   String description, List<String> details, List<String> sizes, List<ColorOption> colors,
                   boolean inStock, int stockCount, boolean isFeatured) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.gender = gender;
        this.price = price;
        this.originalPrice = originalPrice;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.badge = badge;
        this.image = image;
        this.secondaryImage = secondaryImage;
        this.description = description;
        this.details = details;
        this.sizes = sizes;
        this.colors = colors;
        this.inStock = inStock;
        this.stockCount = stockCount;
        this.isFeatured = isFeatured;
    }

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

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getSecondaryImage() { return secondaryImage; }
    public void setSecondaryImage(String secondaryImage) { this.secondaryImage = secondaryImage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }

    public List<String> getSizes() { return sizes; }
    public void setSizes(List<String> sizes) { this.sizes = sizes; }

    public List<ColorOption> getColors() { return colors; }
    public void setColors(List<ColorOption> colors) { this.colors = colors; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public int getStockCount() { return stockCount; }
    public void setStockCount(int stockCount) { this.stockCount = stockCount; }

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }
}
