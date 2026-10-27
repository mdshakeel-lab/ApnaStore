package com.apnastore.config;

import com.apnastore.model.Category;
import com.apnastore.model.Product;
import com.apnastore.model.Role;
import com.apnastore.model.User;
import com.apnastore.repository.CategoryRepository;
import com.apnastore.repository.ProductRepository;
import com.apnastore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Database Seeder (CommandLineRunner)
 * Automatically seeds initial categories and products into the H2 database
 * ONLY if the tables are currently empty.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Password for the seed admin account; read from config / env var. */
    @Value("${app.seed.admin-password:Admin@Store2026}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        seedCategories();
        seedProducts();
        seedAdminUser();
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) {
            System.out.println("[*] Categories table already populated. Skipping category seeding.");
            return;
        }

        System.out.println("[*] Seeding categories into database...");
        categoryRepository.saveAll(Arrays.asList(
            new Category("all", "All Collection", 14),
            new Category("ethnic-women", "Women's Royal Ethnic", 5),
            new Category("ethnic-men", "Men's Royal Sherwanis", 4),
            new Category("outerwear", "Luxury Outerwear", 3),
            new Category("jewelry", "Royal Jewelry", 2)
        ));
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            System.out.println("[*] Products table already populated. Skipping product seeding.");
            return;
        }

        System.out.println("[*] Seeding 9 luxury products into database...");
        Category ethnicWomen = categoryRepository.findById("ethnic-women").orElse(null);
        Category ethnicMen = categoryRepository.findById("ethnic-men").orElse(null);
        Category outerwear = categoryRepository.findById("outerwear").orElse(null);
        Category jewelry = categoryRepository.findById("jewelry").orElse(null);

        List<Product> catalog = Arrays.asList(
            new Product(
                "prod-ind-01", "Royal Zardozi Silk Bridal Lehenga", ethnicWomen, "women",
                1050.0, 1250.0, 5.0, 64, "Royal Heritage",
                "./assets/royal_lehenga.png",
                "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
                "A masterpiece of royal heritage crafted in pure crimson mulberry raw silk, lavishly embroidered with hand-drawn gold zardozi, dabka, and pearl embellishments.",
                Arrays.asList("100% Pure Raw Mulberry Silk with Double Net Dupatta", "Handcrafted zardozi, gold zari, and pearl work", "Custom waist tailoring with latkan tassels", "Includes padded blouse piece and can-can underskirt layering"),
                Arrays.asList("XS", "S", "M", "L", "Custom"),
                Arrays.asList(new Product.ColorOption("Royal Maroon", "#4A0E17"), new Product.ColorOption("Deep Emerald", "#0B3B24"), new Product.ColorOption("Imperial Gold", "#D4AF37")),
                4, true
            ),
            new Product(
                "prod-ind-02", "Imperial Velvet Zardozi Wedding Sherwani", ethnicMen, "men",
                880.0, 1050.0, 4.9, 48, "Groom Collection",
                "https://images.unsplash.com/photo-1583391733956-6c78276477e2?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80",
                "High-collared royal velvet sherwani detailed with regal tone-on-tone French knots and metallic threadwork. Accompanied by a pleated silk churidar and stole.",
                Arrays.asList("Deep Royal Navy Micro-Velvet", "Hand-cut antique brass crest buttons", "Includes silk Churidar & embroidered Dupatta stole", "Specialist dry clean only"),
                Arrays.asList("S", "M", "L", "XL", "XXL"),
                Arrays.asList(new Product.ColorOption("Imperial Navy", "#0D1B2A"), new Product.ColorOption("Royal Ivory", "#FDFBF7"), new Product.ColorOption("Wine Velvet", "#3B111A")),
                6, true
            ),
            new Product(
                "prod-ind-03", "Kanjivaram Pure Silk Gold Zari Saree", ethnicWomen, "women",
                550.0, 680.0, 5.0, 52, "Handloom",
                "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1583391733956-6c78276477e2?auto=format&fit=crop&w=800&q=80",
                "Traditional Kanjivaram silk saree hand-woven by master weavers in Tamil Nadu, featuring temple motif gold zari borders and rich pallu.",
                Arrays.asList("100% Certified Mulberry Handloom Silk", "Pure silver-gold tested zari weaving", "Unstitched matching blouse piece included (80cm)", "Silk Mark certified product"),
                Arrays.asList("Free Size"),
                Arrays.asList(new Product.ColorOption("Crimson Red", "#990000"), new Product.ColorOption("Peacock Blue", "#005F73"), new Product.ColorOption("Mustard Gold", "#E0A96D")),
                8, true
            ),
            new Product(
                "prod-ind-04", "Emerald Velvet Gota Patti Anarkali Suit", ethnicWomen, "women",
                460.0, 540.0, 4.8, 39, "Trending",
                "https://images.unsplash.com/photo-1566174053879-31528523f8ae?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80",
                "Floor-length flared velvet Anarkali featuring intricate Rajasthani gota patti handwork along the hemline, neck, and organza dupatta.",
                Arrays.asList("Plush Silk Velvet with Organza Dupatta", "Handcrafted Gota Patti & Mirror Work", "Comes with stretch lycra leggings", "Dry clean recommended"),
                Arrays.asList("XS", "S", "M", "L", "XL"),
                Arrays.asList(new Product.ColorOption("Royal Emerald", "#004B23"), new Product.ColorOption("Ruby Pink", "#A4133C")),
                9, false
            ),
            new Product(
                "prod-ind-05", "Raw Silk Nehru Jacket & Kurta Set", ethnicMen, "men",
                290.0, 350.0, 4.9, 31, "Festive Classic",
                "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=800&q=80",
                "Structured raw silk mandarin collared sleeveless jacket featuring botanical thread embroidery, layered over a crisp cotton-silk kurta pajama.",
                Arrays.asList("Raw Silk Jacket, Cotton Silk Kurta Set", "Fabric-covered metallic buttons", "Mandarin collar with welt pocket", "Machine wash gentle / dry clean"),
                Arrays.asList("S", "M", "L", "XL"),
                Arrays.asList(new Product.ColorOption("Cream & Gold", "#EEDC9A"), new Product.ColorOption("Royal Navy", "#1D2A44")),
                14, false
            ),
            new Product(
                "prod-ind-06", "Antique Kundan & Pearl Heritage Necklace", jewelry, "women",
                670.0, 790.0, 5.0, 41, "Artisanal Jewelry",
                "https://images.unsplash.com/photo-1630019852942-f89202989a59?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=800&q=80",
                "Handcrafted Meenakari Kundan choker necklace set embedded with natural freshwater pearls and ruby drop beads.",
                Arrays.asList("22K Gold Foil Plated Brass Base", "Uncut glass Kundan stones & cultured pearls", "Includes matching Jhumka earrings and Maang Tikka", "Store in airtight velvet pouch provided"),
                Arrays.asList("Adjustable Choker"),
                Arrays.asList(new Product.ColorOption("Kundan Gold", "#D4AF37")),
                7, true
            ),
            new Product(
                "prod-01", "Apna Store Silk Trench Coat", outerwear, "women",
                480.0, 590.0, 4.9, 42, "Bestseller",
                "./assets/silk_blazer.png",
                "https://images.unsplash.com/photo-1548883354-7622d03aca27?auto=format&fit=crop&w=800&q=80",
                "Masterfully tailored from pure mulberry silk blend, featuring structured shoulder lines, custom brass buttons, and a cinched waist belt.",
                Arrays.asList("70% Mulberry Silk, 30% Fine Wool", "Double-breasted front closure", "Water-repellent finish", "Dry clean only"),
                Arrays.asList("XS", "S", "M", "L", "XL"),
                Arrays.asList(new Product.ColorOption("Onyx Black", "#111111"), new Product.ColorOption("Champagne Silk", "#E5D3B3")),
                8, false
            ),
            new Product(
                "prod-02", "Midnight Velvet Gala Evening Gown", ethnicWomen, "women",
                650.0, 720.0, 5.0, 28, "Limited Edition",
                "./assets/velvet_dress.png",
                "https://images.unsplash.com/photo-1566174053879-31528523f8ae?auto=format&fit=crop&w=800&q=80",
                "An enchanting floor-length velvet gown with a delicate back plunge, hand-embroidered metallic accents, and a fluid silhouette.",
                Arrays.asList("100% Deep Velvet Cotton Silk", "Concealed back zip closure", "Made in Paris Atelier"),
                Arrays.asList("XS", "S", "M", "L"),
                Arrays.asList(new Product.ColorOption("Midnight Blue", "#0F1B29")),
                5, false
            ),
            new Product(
                "prod-03", "Signature Lambskin Moto Jacket", outerwear, "men",
                520.0, 600.0, 4.8, 56, "Iconic",
                "./assets/leather_jacket.png",
                "https://images.unsplash.com/photo-1521223890158-f9f7c3d5d504?auto=format&fit=crop&w=800&q=80",
                "Crafted from butter-soft full-grain lambskin with heavy brushed silver hardware, quilted shoulder panels, and asymmetric zip.",
                Arrays.asList("100% Italian Lambskin Leather", "100% Cupro lining", "YKK silver zippers"),
                Arrays.asList("S", "M", "L", "XL"),
                Arrays.asList(new Product.ColorOption("Obsidian Black", "#0D0D0D")),
                12, false
            )
        );

        productRepository.saveAll(catalog);
        System.out.println("[✓] Successfully seeded " + catalog.size() + " products into the database.");
    }

    /**
     * Seeds a single ADMIN user — dev profile only.
     * The password is read from app.seed.admin-password (application-dev.properties
     * or the ADMIN_SEED_PASSWORD environment variable). Never committed in plain text.
     */
    private void seedAdminUser() {
        if (userRepository.existsByEmail("admin@apnastore.com")) {
            System.out.println("[*] Admin user already exists. Skipping admin seeding.");
            return;
        }

        String hash = passwordEncoder.encode(adminPassword);
        User admin = new User("Apna Admin", "admin@apnastore.com", hash, Role.ADMIN);
        userRepository.save(admin);
        System.out.println("[✓] Seeded ADMIN user: admin@apnastore.com");
    }
}
