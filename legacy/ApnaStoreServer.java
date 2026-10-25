package com.apnastore;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * ==============================================================================
 * LEGACY REFERENCE: Zero-Dependency Pure Java JDK Server (ApnaStoreServer)
 * ==============================================================================
 * Why this file exists:
 * 1. Interview Demonstration: Proves understanding of core networking, sockets,
 *    and HTTP protocol handling using only java standard library (com.sun.net.httpserver).
 * 2. Fallback execution: Can run on any system with Java 11+ without Maven or Gradle.
 * 
 * Note: The primary production backend has been migrated to Spring Boot 3 in /backend.
 * ==============================================================================
 */
public class ApnaStoreServer {

    private static final int PORT = 8080;
    private static final Path ROOT_DIR = resolveRootDir();

    private static Path resolveRootDir() {
        Path[] candidates = new Path[] {
            Paths.get("frontend"),
            Paths.get("..", "frontend"),
            Paths.get("../..", "frontend"),
            Paths.get("."),
            Paths.get("..")
        };
        for (Path candidate : candidates) {
            Path testPath = candidate.resolve("index.html").toAbsolutePath().normalize();
            if (Files.exists(testPath)) {
                return testPath.getParent();
            }
        }
        return Paths.get(".").toAbsolutePath().normalize();
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // API Contexts
        server.createContext("/api/products", new ProductsHandler());
        server.createContext("/api/orders", new OrdersHandler());
        
        // Static File Context
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // default executor
        server.start();

        System.out.println("=================================================");
        System.out.println("  Apna Store [LEGACY] JDK Backend Server Started! ");
        System.out.println("  Running on JDK Version : " + System.getProperty("java.version"));
        System.out.println("  Serving Web Directory  : " + ROOT_DIR);
        System.out.println("  Store Frontend URL     : http://localhost:" + PORT + "/");
        System.out.println("  Products API Endpoint  : http://localhost:" + PORT + "/api/products");
        System.out.println("  Orders API Endpoint    : http://localhost:" + PORT + "/api/orders");
        System.out.println("=================================================");
    }

    // CORS & Response Helpers
    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // Products API Handler
    static class ProductsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 204, "");
                return;
            }

            URI uri = exchange.getRequestURI();
            String path = uri.getPath();
            String query = uri.getQuery();

            Map<String, String> queryParams = parseQueryParams(query);

            if (path.endsWith("/categories")) {
                sendJsonResponse(exchange, 200, getCategoriesJson());
                return;
            }

            String[] parts = path.split("/");
            if (parts.length > 3) {
                String id = parts[3];
                String productJson = getProductByIdJson(id);
                if (productJson != null) {
                    sendJsonResponse(exchange, 200, productJson);
                } else {
                    sendJsonResponse(exchange, 404, "{\"error\": \"Product not found\"}");
                }
                return;
            }

            String category = queryParams.get("category");
            String q = queryParams.get("q");

            String json = getProductsJson(category, q);
            sendJsonResponse(exchange, 200, json);
        }
    }

    // Orders API Handler
    static class OrdersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 204, "");
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String orderId = "ORD-" + (10000 + new Random().nextInt(90000));
                String responseJson = String.format(
                    "{\"orderId\":\"%s\",\"status\":\"CONFIRMED\",\"message\":\"Order placed successfully via Legacy Java JDK Server\",\"estimatedDelivery\":\"3-5 Business Days\"}",
                    orderId
                );
                sendJsonResponse(exchange, 201, responseJson);
            } else {
                sendJsonResponse(exchange, 200, "{\"status\":\"ACTIVE\",\"message\":\"Apna Store Legacy Order Engine Online\"}");
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            // Guard against path traversal attacks
            Path requestedPath = ROOT_DIR.resolve(path.substring(1)).normalize();
            if (!requestedPath.startsWith(ROOT_DIR) || !Files.exists(requestedPath) || Files.isDirectory(requestedPath)) {
                byte[] notFound = "<h1>404 - Page Not Found</h1>".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, notFound.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound);
                }
                return;
            }

            String mimeType = getMimeType(requestedPath.toString());
            exchange.getResponseHeaders().set("Content-Type", mimeType);

            byte[] fileBytes = Files.readAllBytes(requestedPath);
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isBlank()) return map;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                map.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8), URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    private static String getMimeType(String filename) {
        if (filename.endsWith(".html")) return "text/html; charset=utf-8";
        if (filename.endsWith(".css")) return "text/css; charset=utf-8";
        if (filename.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".svg")) return "image/svg+xml";
        if (filename.endsWith(".json")) return "application/json";
        return "application/octet-stream";
    }

    private static String getCategoriesJson() {
        return "[" +
            "{\"id\":\"all\",\"name\":\"All Collection\",\"count\":14}," +
            "{\"id\":\"ethnic-women\",\"name\":\"Women's Royal Ethnic\",\"count\":5}," +
            "{\"id\":\"ethnic-men\",\"name\":\"Men's Royal Sherwanis\",\"count\":4}," +
            "{\"id\":\"outerwear\",\"name\":\"Luxury Outerwear\",\"count\":3}," +
            "{\"id\":\"jewelry\",\"name\":\"Royal Jewelry\",\"count\":2}" +
            "]";
    }

    private static String getProductsJson(String category, String query) {
        List<String> products = getAllProductObjects();
        List<String> filtered = new ArrayList<>();

        for (String p : products) {
            boolean matchesCategory = (category == null || category.equalsIgnoreCase("all") || p.contains("\"category\":\"" + category + "\""));
            boolean matchesQuery = (query == null || query.isBlank() || p.toLowerCase().contains(query.toLowerCase()));
            if (matchesCategory && matchesQuery) {
                filtered.add(p);
            }
        }

        return "[" + String.join(",", filtered) + "]";
    }

    private static String getProductByIdJson(String id) {
        for (String p : getAllProductObjects()) {
            if (p.contains("\"id\":\"" + id + "\"")) {
                return p;
            }
        }
        return null;
    }

    private static List<String> getAllProductObjects() {
        return List.of(
            "{\"id\":\"prod-ind-01\",\"name\":\"Royal Zardozi Silk Bridal Lehenga\",\"category\":\"ethnic-women\",\"gender\":\"women\",\"price\":1050.0,\"originalPrice\":1250.0,\"rating\":5.0,\"reviewCount\":64,\"badge\":\"Royal Heritage\",\"image\":\"./assets/royal_lehenga.png\",\"hoverImage\":\"https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80\",\"description\":\"A masterpiece of royal heritage crafted in pure crimson mulberry raw silk, lavishly embroidered with hand-drawn gold zardozi, dabka, and pearl embellishments.\",\"inStock\":true,\"stockCount\":4,\"isNew\":true}",
            "{\"id\":\"prod-ind-02\",\"name\":\"Imperial Velvet Zardozi Wedding Sherwani\",\"category\":\"ethnic-men\",\"gender\":\"men\",\"price\":880.0,\"originalPrice\":1050.0,\"rating\":4.9,\"reviewCount\":48,\"badge\":\"Groom Collection\",\"image\":\"https://images.unsplash.com/photo-1583391733956-6c78276477e2?auto=format&fit=crop&w=800&q=80\",\"hoverImage\":\"https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80\",\"description\":\"High-collared royal velvet sherwani detailed with regal tone-on-tone French knots and metallic threadwork.\",\"inStock\":true,\"stockCount\":6,\"isNew\":true}",
            "{\"id\":\"prod-ind-03\",\"name\":\"Kanjivaram Pure Silk Gold Zari Saree\",\"category\":\"ethnic-women\",\"gender\":\"women\",\"price\":550.0,\"originalPrice\":680.0,\"rating\":5.0,\"reviewCount\":52,\"badge\":\"Handloom\",\"image\":\"https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80\",\"hoverImage\":\"https://images.unsplash.com/photo-1583391733956-6c78276477e2?auto=format&fit=crop&w=800&q=80\",\"description\":\"Traditional Kanjivaram silk saree hand-woven by master weavers in Tamil Nadu.\",\"inStock\":true,\"stockCount\":8,\"isNew\":true}",
            "{\"id\":\"prod-ind-04\",\"name\":\"Emerald Velvet Gota Patti Anarkali Suit\",\"category\":\"ethnic-women\",\"gender\":\"women\",\"price\":460.0,\"originalPrice\":540.0,\"rating\":4.8,\"reviewCount\":39,\"badge\":\"Trending\",\"image\":\"https://images.unsplash.com/photo-1566174053879-31528523f8ae?auto=format&fit=crop&w=800&q=80\",\"hoverImage\":\"https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80\",\"description\":\"Floor-length flared velvet Anarkali featuring intricate Rajasthani gota patti handwork.\",\"inStock\":true,\"stockCount\":9,\"isNew\":false}",
            "{\"id\":\"prod-ind-05\",\"name\":\"Raw Silk Nehru Jacket & Kurta Set\",\"category\":\"ethnic-men\",\"gender\":\"men\",\"price\":290.0,\"originalPrice\":350.0,\"rating\":4.9,\"reviewCount\":31,\"badge\":\"Festive Classic\",\"image\":\"https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=800&q=80\",\"hoverImage\":\"https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=800&q=80\",\"description\":\"Structured raw silk mandarin collared sleeveless jacket featuring botanical thread embroidery.\",\"inStock\":true,\"stockCount\":14,\"isNew\":false}",
            "{\"id\":\"prod-ind-06\",\"name\":\"Antique Kundan & Pearl Heritage Necklace\",\"category\":\"jewelry\",\"gender\":\"women\",\"price\":670.0,\"originalPrice\":790.0,\"rating\":5.0,\"reviewCount\":41,\"badge\":\"Artisanal Jewelry\",\"image\":\"https://images.unsplash.com/photo-1630019852942-f89202989a59?auto=format&fit=crop&w=800&q=80\",\"hoverImage\":\"https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=800&q=80\",\"description\":\"Handcrafted Meenakari Kundan choker necklace set embedded with natural freshwater pearls.\",\"inStock\":true,\"stockCount\":7,\"isNew\":true}",
            "{\"id\":\"prod-01\",\"name\":\"Apna Store Silk Trench Coat\",\"category\":\"outerwear\",\"gender\":\"women\",\"price\":480.0,\"originalPrice\":590.0,\"rating\":4.9,\"reviewCount\":42,\"badge\":\"Bestseller\",\"image\":\"./assets/silk_blazer.png\",\"hoverImage\":\"https://images.unsplash.com/photo-1548883354-7622d03aca27?auto=format&fit=crop&w=800&q=80\",\"description\":\"Masterfully tailored from pure mulberry silk blend, featuring structured shoulder lines.\",\"inStock\":true,\"stockCount\":12,\"isNew\":true}"
        );
    }
}
