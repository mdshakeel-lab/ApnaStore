package com.apnastore.service;

import com.apnastore.model.Category;
import com.apnastore.model.Product;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final Map<String, Product> productMap = new LinkedHashMap<>();
    private final List<Category> categories = new ArrayList<>();

    @PostConstruct
    public void initData() {
        // Initialize Categories
        categories.add(new Category("all", "All Collection", 14));
        categories.add(new Category("ethnic-women", "Women's Royal Ethnic", 5));
        categories.add(new Category("ethnic-men", "Men's Royal Sherwanis", 4));
        categories.add(new Category("outerwear", "Luxury Outerwear", 3));
        categories.add(new Category("jewelry", "Royal Jewelry", 2));

        // Seed Product 1
        addProduct(new Product(
            "prod-ind-01", "Royal Zardozi Silk Bridal Lehenga", "ethnic-women", "women",
            1050.0, 1250.0, 5.0, 64, "Royal Heritage",
            "./assets/royal_lehenga.png",
            "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
            "A masterpiece of royal heritage crafted in pure crimson mulberry raw silk, lavishly embroidered with hand-drawn gold zardozi, dabka, and pearl embellishments.",
            Arrays.asList("100% Pure Raw Mulberry Silk with Double Net Dupatta", "Handcrafted zardozi, gold zari, and pearl work", "Custom waist tailoring with latkan tassels", "Includes padded blouse piece and can-can underskirt layering"),
            Arrays.asList("XS", "S", "M", "L", "Custom"),
            Arrays.asList(new Product.ColorOption("Royal Maroon", "#4A0E17"), new Product.ColorOption("Deep Emerald", "#0B3B24"), new Product.ColorOption("Imperial Gold", "#D4AF37")),
            true, 4, true
        ));

        // Seed Product 2
        addProduct(new Product(
            "prod-ind-02", "Imperial Velvet Zardozi Wedding Sherwani", "ethnic-men", "men",
            880.0, 1050.0, 4.9, 48, "Groom Collection",
            "https://images.unsplash.com/photo-1583391733956-6c78276477e2?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80",
            "High-collared royal velvet sherwani detailed with regal tone-on-tone French knots and metallic threadwork. Accompanied by a pleated silk churidar and stole.",
            Arrays.asList("Deep Royal Navy Micro-Velvet", "Hand-cut antique brass crest buttons", "Includes silk Churidar & embroidered Dupatta stole", "Specialist dry clean only"),
            Arrays.asList("S", "M", "L", "XL", "XXL"),
            Arrays.asList(new Product.ColorOption("Imperial Navy", "#0D1B2A"), new Product.ColorOption("Royal Ivory", "#FDFBF7"), new Product.ColorOption("Wine Velvet", "#3B111A")),
            true, 6, true
        ));

        // Seed Product 3
        addProduct(new Product(
            "prod-ind-03", "Kanjivaram Pure Silk Gold Zari Saree", "ethnic-women", "women",
            550.0, 680.0, 5.0, 52, "Handloom",
            "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1583391733956-6c78276477e2?auto=format&fit=crop&w=800&q=80",
            "Traditional Kanjivaram silk saree hand-woven by master weavers in Tamil Nadu, featuring temple motif gold zari borders and rich pallu.",
            Arrays.asList("100% Certified Mulberry Handloom Silk", "Pure silver-gold tested zari weaving", "Unstitched matching blouse piece included (80cm)", "Silk Mark certified product"),
            Arrays.asList("Free Size"),
            Arrays.asList(new Product.ColorOption("Crimson Red", "#990000"), new Product.ColorOption("Peacock Blue", "#005F73"), new Product.ColorOption("Mustard Gold", "#E0A96D")),
            true, 8, true
        ));

        // Seed Product 4
        addProduct(new Product(
            "prod-ind-04", "Emerald Velvet Gota Patti Anarkali Suit", "ethnic-women", "women",
            460.0, 540.0, 4.8, 39, "Trending",
            "https://images.unsplash.com/photo-1566174053879-31528523f8ae?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80",
            "Floor-length flared velvet Anarkali featuring intricate Rajasthani gota patti handwork along the hemline, neck, and organza dupatta.",
            Arrays.asList("Plush Silk Velvet with Organza Dupatta", "Handcrafted Gota Patti & Mirror Work", "Comes with stretch lycra leggings", "Dry clean recommended"),
            Arrays.asList("XS", "S", "M", "L", "XL"),
            Arrays.asList(new Product.ColorOption("Royal Emerald", "#004B23"), new Product.ColorOption("Ruby Pink", "#A4133C")),
            true, 9, false
        ));

        // Seed Product 5
        addProduct(new Product(
            "prod-ind-05", "Raw Silk Nehru Jacket & Kurta Set", "ethnic-men", "men",
            290.0, 350.0, 4.9, 31, "Festive Classic",
            "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=800&q=80",
            "Structured raw silk mandarin collared sleeveless jacket featuring botanical thread embroidery, layered over a crisp cotton-silk kurta pajama.",
            Arrays.asList("Raw Silk Jacket, Cotton Silk Kurta Set", "Fabric-covered metallic buttons", "Mandarin collar with welt pocket", "Machine wash gentle / dry clean"),
            Arrays.asList("S", "M", "L", "XL"),
            Arrays.asList(new Product.ColorOption("Cream & Gold", "#EEDC9A"), new Product.ColorOption("Royal Navy", "#1D2A44")),
            true, 14, false
        ));

        // Seed Product 6
        addProduct(new Product(
            "prod-ind-06", "Antique Kundan & Pearl Heritage Necklace", "jewelry", "women",
            670.0, 790.0, 5.0, 41, "Artisanal Jewelry",
            "https://images.unsplash.com/photo-1630019852942-f89202989a59?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=800&q=80",
            "Handcrafted Meenakari Kundan choker necklace set embedded with natural freshwater pearls and ruby drop beads.",
            Arrays.asList("22K Gold Foil Plated Brass Base", "Uncut glass Kundan stones & cultured pearls", "Includes matching Jhumka earrings and Maang Tikka", "Store in airtight velvet pouch provided"),
            Arrays.asList("Adjustable Choker"),
            Arrays.asList(new Product.ColorOption("Kundan Gold", "#D4AF37")),
            true, 7, true
        ));

        // Seed Product 7
        addProduct(new Product(
            "prod-01", "Apna Store Silk Trench Coat", "outerwear", "women",
            480.0, 590.0, 4.9, 42, "Bestseller",
            "./assets/silk_blazer.png",
            "https://images.unsplash.com/photo-1548883354-7622d03aca27?auto=format&fit=crop&w=800&q=80",
            "Masterfully tailored from pure mulberry silk blend, featuring structured shoulder lines, custom brass buttons, and a cinched waist belt.",
            Arrays.asList("70% Mulberry Silk, 30% Fine Wool", "Double-breasted front closure", "Water-repellent finish", "Dry clean only"),
            Arrays.asList("XS", "S", "M", "L"),
            Arrays.asList(new Product.ColorOption("Midnight Navy", "#0F172A"), new Product.ColorOption("Ivory White", "#F8FAFC")),
            true, 12, true
        ));
    }

    private void addProduct(Product p) {
        productMap.put(p.getId(), p);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }

    public Optional<Product> getProductById(String id) {
        return Optional.ofNullable(productMap.get(id));
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Product> getProductsByCategory(String categoryId) {
        if ("all".equalsIgnoreCase(categoryId)) {
            return getAllProducts();
        }
        return productMap.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(categoryId))
                .collect(Collectors.toList());
    }

    public List<Product> filterProducts(String category, String query) {
        return productMap.values().stream()
                .filter(p -> {
                    if (category == null || category.trim().isEmpty() || "all".equalsIgnoreCase(category.trim())) {
                        return true;
                    }
                    return p.getCategory().equalsIgnoreCase(category.trim());
                })
                .filter(p -> {
                    if (query == null || query.trim().isEmpty()) {
                        return true;
                    }
                    String q = query.toLowerCase().trim();
                    return p.getName().toLowerCase().contains(q) ||
                           p.getDescription().toLowerCase().contains(q) ||
                           p.getCategory().toLowerCase().contains(q);
                })
                .collect(Collectors.toList());
    }
}
