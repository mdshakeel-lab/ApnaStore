# Apna Store — Full-Stack Royal Couture E-Commerce Platform
### Professional Resume Project Description & Technical Interview Guide

---

## 📌 Resume Summary Snippet (Short & High-Impact)

> **Apna Store — Full-Stack E-Commerce Platform** | *Java 17, Spring Boot 3.2, Spring Security (JWT), Spring Data JPA, Hibernate, H2 Database, OpenAPI 3 (Swagger UI), JUnit 5, Mockito, GitHub Actions CI, JavaScript ES6+, HTML5, CSS3*
> * Architected and deployed a full-stack luxury e-commerce web application featuring stateless JWT authentication (RBAC), declarative Spring Data JPA persistence, and a responsive glassmorphism storefront.
> * Implemented an order-processing engine enforcing server-side pricing integrity, transactional stock deduction, promotional code validation (`APNA20`), and Bean Validation with unified error responses.
> * Designed interactive API documentation via SpringDoc OpenAPI 3 with Bearer JWT authorization, and achieved automated test coverage via JUnit 5, Mockito, MockMvc, and a GitHub Actions CI pipeline.

---

## 📄 Production-Ready Resume Bullet Points

### **Option 1: Full-Stack Software Engineer Focus**
* **Architected Enterprise Full-Stack Application**: Developed a responsive full-stack e-commerce platform using **Java 17**, **Spring Boot 3.2**, **Spring Security 6**, and modern **Vanilla JavaScript (ES6+)** with a glassmorphism CSS design system.
* **Secured Authentication & RBAC**: Engineered stateless JWT authentication utilizing **JJWT (0.12)** and **BCrypt** hashing; implemented custom `JwtAuthFilter` and `SecurityFilterChain` enforcing public browsing, customer checkout (`ROLE_USER`), and administrative endpoints (`ROLE_ADMIN`).
* **Engineered Server-Side Pricing & Inventory Engine**: Designed a transactional checkout pipeline (`@Transactional`) calculating item totals exclusively from database prices, validating promotional discount codes (`APNA20`), and deducting stock with race-condition resilience.
* **Integrated Interactive API Documentation**: Configured **SpringDoc OpenAPI 3** and **Swagger UI** (`/swagger-ui.html`) with interactive Bearer JWT Authorization, enabling visual testing and documentation of all REST endpoints.
* **Automated CI/CD & Testing**: Established a **GitHub Actions CI** pipeline for automated Maven compilation, dependency caching, and test execution; authored comprehensive unit, controller (`@WebMvcTest`), and integration tests (`@SpringBootTest`) with an isolated in-memory H2 profile.
* **Crafted High-Performance Client**: Built single-page checkout, modal quick-views, persistent shopping cart/wishlist management via **HTML5 `localStorage`**, and customer order history views with responsive error alerts.

---

### **Option 2: Backend Java / Spring Boot Focus**
* **Built Layered Spring Boot 3.2 Architecture**: Designed modular **Controller-Service-Repository** layers using **Java 17**, **Spring Data JPA**, **Hibernate**, and **H2 Embedded Database** (file persistence for dev, in-memory for testing).
* **Implemented Robust Input Validation & Error Handling**: Leveraged **Jakarta Bean Validation** (`@Valid`, `@NotBlank`, `@Email`, `@Min`) alongside a `@RestControllerAdvice` global handler, delivering standardized JSON error formats (`ErrorResponse` with itemized `fieldErrors`).
* **Secured Endpoints with Spring Security & JWT**: Constructed stateless token-based authorization with custom authentication entry points returning structured JSON for 401 Unauthorized and 403 Forbidden responses.
* **Built Data Seeding & Relational Schema**: Designed normalized entity relationships (`Category`, `Product`, `User`, `Order`, `OrderItem`) with cascading associations and automated startup database seeding (`CommandLineRunner`).
* **Delivered 100% Automated Testing Suite**: Authored unit tests with **JUnit 5** and **Mockito**, controller slice tests with **MockMvc**, and full integration flows validating registration, JWT extraction, order placement, and inventory decrement.

---

### **Option 3: DevOps, Reliability & API Focus**
* **Continuous Integration with GitHub Actions**: Authored `.github/workflows/ci.yml` automating JDK 17 setup, Maven dependency caching, and wrapper test runs on every push and pull request to `main`.
* **Standardized OpenAPI 3 Specification**: Documented RESTful APIs with SpringDoc OpenAPI, exposing `/v3/api-docs` and `/swagger-ui.html` with security scheme annotations for seamless Bearer token testing.
* **Dual-Profile Database Architecture**: Configured environment-driven properties (`application-dev.properties`, `application-test.properties`, `application-prod.properties`) separating local file-based database execution from ephemeral in-memory test databases.
* **Resilient Low-Level Knowledge**: Maintained a zero-dependency reference implementation (`legacy/ApnaStoreServer.java`) using raw JDK socket handlers (`com.sun.net.httpserver`) as a benchmark demonstrating the architectural transition to Spring Boot.

---

## 🛠 Comprehensive Tech Stack Breakdown

| Layer | Technologies & Tools |
|---|---|
| **Core Language & Runtime** | Java 17 (OpenJDK / Eclipse Temurin), Node/Static HTTP tooling |
| **Backend Framework** | Spring Boot 3.2.3, Spring MVC, Spring Data JPA, Hibernate ORM 6.4 |
| **Security & Authentication** | Spring Security 6, JJWT (io.jsonwebtoken 0.12.6), BCryptPasswordEncoder |
| **API Documentation** | SpringDoc OpenAPI 3 (`springdoc-openapi-starter-webmvc-ui` 2.3.0), Swagger UI |
| **Databases & Persistence** | H2 Database Engine (File mode `./data/apnastore` for dev; In-Memory `mem:testdb` for tests) |
| **Validation & Error Handling** | Jakarta Validation (`@Valid`), Spring Validation Starter, `@RestControllerAdvice` |
| **Testing & Quality Assurance** | JUnit 5 (Jupiter), Mockito (subclass mock-maker), Spring Boot Starter Test, Spring Security Test, MockMvc |
| **CI/CD & Build Tools** | GitHub Actions (`.github/workflows/ci.yml`), Apache Maven 3.9, Maven Wrapper (`mvnw` / `mvnw.cmd`) |
| **Frontend Technologies** | Vanilla JavaScript (ES6+ async/await, Fetch API), HTML5, CSS3 Custom Properties, LocalStorage |
| **Design & UI Assets** | Glassmorphism, Font Awesome 6.5 Icons, Google Fonts (*Cinzel* serif & *Plus Jakarta Sans*) |

---

## 🎯 Key Implemented Features

1. **Stateless JWT Security**:
   - `POST /api/auth/register`: Hashes passwords using BCrypt, prevents duplicate email registration, and returns signed JWT tokens.
   - `POST /api/auth/login`: Verifies salted password hashes and issues 24-hour expiration JWT tokens with user email and role claims.
   - `JwtAuthFilter`: Intercepts incoming requests, validates tokens via `JwtUtil`, extracts the user principal, and sets authentication in `SecurityContextHolder`.

2. **Strict Server-Side Pricing & Stock Deduction**:
   - Client-submitted prices are completely ignored. Subtotals are calculated solely using verified unit prices from `ProductRepository`.
   - Inventory availability is validated before order confirmation. If requested quantity exceeds stock, transaction aborts with `InsufficientStockException` (HTTP 409).
   - Upon confirmation, product inventory is atomically decremented and order items are persisted via JPA cascading.

3. **Promotional Discount Engine**:
   - Supports promo codes `APNA20` (20% off), `ROYAL20`, `LUMIERE20`, `WELCOME10` (10% off), and `VIP50` (flat ₹50 off).
   - Applies complimentary shipping on orders over ₹300, or a flat ₹25 shipping fee.

4. **Interactive Swagger UI**:
   - Access at `http://localhost:8080/swagger-ui.html`.
   - Click the green **Authorize** button to inject Bearer JWT tokens for executing authenticated calls to `/api/orders` directly in the browser.

5. **Customer Order History ("My Orders")**:
   - `GET /api/orders/my`: Returns order history for the logged-in customer.
   - Frontend renders itemized order cards with status badges (`CONFIRMED`), product thumbnails, delivery addresses, and payment breakdowns.

6. **Continuous Integration**:
   - GitHub Actions workflow (`.github/workflows/ci.yml`) triggers on every push and PR to `main`, validating the build across 21 automated unit, slice, and integration tests.

---

## 💬 Behavioral & Technical Interview Talking Points

### 1. Why use JWT instead of standard HTTP session cookies?
> *"In modern microservices and multi-client environments, stateless JWTs eliminate server-side session memory overhead and simplify horizontal scaling. With Spring Security and JJWT, the server verifies the cryptographic HMAC-SHA256 signature on every request without needing a shared session store (like Redis), while role claims in the payload enable fast, local permission checks."*

### 2. How did you ensure financial and inventory integrity during checkout?
> *"E-commerce frontends can be tampered with in browser DevTools. In `OrderService`, I treat all client-sent prices as untrusted. The service re-queries the database for each product's authoritative price, checks available stock, and decrements stock within a single `@Transactional` method. If any item is out of stock, an `InsufficientStockException` is thrown, rolling back the transaction and returning a clean HTTP 409 Conflict."*

### 3. How did you design the automated testing strategy?
> *"I used a pyramid testing strategy:
> 1. **Unit tests with JUnit 5 & Mockito** for core business logic (`OrderServiceTest`, `AuthServiceTest`) isolating calculation rules and BCrypt hashing.
> 2. **Controller slice tests with `@WebMvcTest` and MockMvc** (`ProductControllerTest`, `OrderControllerTest`) verifying HTTP status codes, security filters (401 Unauthorized), and Bean Validation error mappings.
> 3. **End-to-end integration tests with `@SpringBootTest`** (`OrderFlowIntegrationTest`) using an in-memory H2 database under a dedicated `test` profile to test the complete user journey: registration, login, JWT token usage, order placement, and database stock decrement."*

### 4. Why is `legacy/ApnaStoreServer.java` kept in the repository?
> *"I built `legacy/ApnaStoreServer.java` using raw JDK `com.sun.net.httpserver` without any third-party frameworks. It demonstrates low-level proficiency with raw HTTP byte streams, MIME headers, URI query parsing, and thread pools. It provides a great conversational bridge during interviews to explain why enterprise teams transition to Spring Boot for declarative transactions, dependency injection, and centralized exception handling."*
