# Apna Store - Java Spring Boot Backend Service

This directory contains the production-ready **Java Spring Boot 3.2** backend REST API for **Apna Store (Royal Couture & Ethnic Wear)**.

---

## 🚀 Architecture Overview

* **Framework**: Java 17 + Spring Boot 3.2.3
* **Build System**: Apache Maven (`pom.xml`)
* **Service Port**: `8080`
* **Static Asset Host**: Configured via `WebConfig.java` to serve frontend files (`index.html`, `styles.css`, `app.js`, `assets/`) on `http://localhost:8080/`.

---

## 🛠 Project Structure

```
backend/
├── pom.xml                                  # Maven build configuration
├── mvnw.cmd                                 # Windows Maven wrapper
└── src/main/java/com/apnastore/
    ├── ApnaStoreApplication.java            # Main Spring Boot entry point
    ├── config/
    │   └── WebConfig.java                   # CORS & Static resource routing
    ├── controller/
    │   ├── ProductController.java           # REST Endpoints: GET /api/products
    │   └── OrderController.java             # REST Endpoints: POST /api/orders
    ├── model/
    │   ├── Product.java                     # Product domain model
    │   ├── Category.java                    # Category domain model
    │   ├── OrderRequest.java                # Checkout request DTO
    │   └── OrderResponse.java               # Order receipt response DTO
    └── service/
        ├── ProductService.java              # In-memory product repository & search
        └── OrderService.java                # Order calculation & promo engine
```

---

## 📡 REST API Endpoints

| HTTP Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/products` | Fetch all products in catalog |
| `GET` | `/api/products?category=ethnic-women` | Filter products by category |
| `GET` | `/api/products?q=silk` | Search catalog by query string |
| `GET` | `/api/products/{id}` | Get specific product details by ID |
| `GET` | `/api/products/categories` | Retrieve category list with counts |
| `POST` | `/api/orders` | Submit a new order & receive order receipt |
| `GET` | `/api/orders/{orderId}` | Retrieve order status by ID |

---

## 💻 How to Run the Java Backend

### Prerequisites
* **Java Development Kit (JDK 17 or higher)** installed on your machine.
* **Apache Maven** (or use the included `mvnw.cmd` wrapper).

### Command to Start Server
Open terminal in the `backend/` directory and run:

```bash
mvn spring-boot:run
```

Or using the Maven wrapper on Windows:

```cmd
.\mvnw.cmd spring-boot:run
```

Once started:
* **Frontend Application**: [http://localhost:8080/](http://localhost:8080/)
* **Products API Endpoint**: [http://localhost:8080/api/products](http://localhost:8080/api/products)
