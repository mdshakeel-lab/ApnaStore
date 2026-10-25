# Apna Store – Full-Stack E-Commerce Platform
### Resume Project Description & Technical Documentation

---

## 📌 Summary for Resume (Short & Impactful)

> **Apna Store – Royal Couture E-Commerce Platform** | *Java 17, Spring Boot, REST APIs, JavaScript ES6+, HTML5, CSS3, Maven*
> * Architected and built a full-stack e-commerce web application with a Java 17 / Spring Boot backend and an interactive responsive frontend.
> * Designed RESTful APIs for catalog queries, category filtering, live search, and automated order processing with promotional discount validation.
> * Integrated asynchronous JavaScript (`fetch` API) with client-side state persistence (`localStorage`) and graceful offline fallback handling.

---

## 📄 Standard Resume Bullet Points (Ready to Copy-Paste)

### **Option A: Full-Stack Developer Focus**
* **Architected Full-Stack E-Commerce System**: Built a full-stack luxury retail web application using **Java 17**, **Spring Boot 3.2**, and modern **Vanilla JavaScript (ES6+)**.
* **Developed RESTful Services**: Created robust Spring REST Controllers (`/api/products`, `/api/orders`) handling product filtering, live search, and order checkout pipelines.
* **Implemented Automated Order Engine**: Engineered an order service calculating real-time subtotals, promotional code discounts (`APNA20`), shipping logic, and returning printable receipt DTOs.
* **Designed Dynamic UI/UX**: Crafted responsive glassmorphism UI using modern **CSS3**, custom CSS variables, modal quick-views, and persistent cart/wishlist management via **HTML5 Web Storage**.
* **Resilient Client Architecture**: Integrated asynchronous API calls in `app.js` with automatic fallback to client-side catalog state when backend API is offline.

---

### **Option B: Backend Java / Spring Boot Focus**
* **Engineered Java Spring Boot REST API**: Developed layered backend architecture (**Controller-Service-Model**) using **Java 17** and **Spring Boot 3.2**.
* **Built Catalog & Order Pipelines**: Created modular endpoints for catalog queries, category filtering, keyword searching, and POST-based multi-item transaction management.
* **Implemented Business Logic & Data DTOs**: Formatted strongly-typed JSON DTOs for `OrderRequest` and `OrderResponse`, incorporating discount algorithms and itemized receipts.
* **Configured Cross-Origin & Static Hosting**: Built custom `WebMvcConfigurer` handling CORS policies and static asset serving for seamless full-stack execution.

---

## 🛠 Tech Stack Breakdown

| Layer | Technologies Used |
|---|---|
| **Backend Framework** | Java 17, Spring Boot 3.2, Spring MVC, Jackson JSON, Maven (`pom.xml`) |
| **Frontend Core** | JavaScript (ES6+ Modules), HTML5, CSS3 (Glassmorphism, Flexbox, CSS Grid) |
| **User Experience** | Font Awesome 6.5, Google Fonts (*Cinzel*, *Plus Jakarta Sans*), Micro-animations |
| **Data & State Management** | RESTful JSON APIs, In-Memory Repository Service, `localStorage` |
| **Server & Hosting** | Embedded Tomcat (Port 8080), PowerShell Static Server (`server.ps1`) |

---

## 💡 Key Features Highlight

1. **RESTful Catalog & Search APIs**: Fast, filtered product querying by category (`ethnic-women`, `ethnic-men`, `jewelry`, `outerwear`) and keyword search.
2. **Order & Checkout Processing Engine**: Real-time receipt generator supporting promo codes (`APNA20` for 20% off), tax, and express shipping calculations.
3. **Cart & Wishlist Persistence**: Client-side state saving allowing persistent cart items across browser sessions.
4. **Interactive UI/UX**: Quick-view modal dialogs, size/color selectors, toast notifications, responsive mobile drawer, and printable order receipts.
5. **Fault-Tolerant API Integration**: Asynchronous API communication with graceful client-side fallback handling.

---

## ❓ Behavioral & Technical Interview Talking Points

### 1. **Why did you use Spring Boot for the backend?**
> *"I chose Spring Boot for its enterprise-grade features, Dependency Injection (`@Service`, `@Autowired`), and seamless REST API creation with `@RestController`. It allows scalable business logic separation (Controller-Service-Model architecture) and easy database expansion in the future using Spring Data JPA."*

### 2. **How does the Frontend communicate with the Backend?**
> *"The frontend relies on JavaScript ES6 `async/await` and `fetch` requests to communicate with the Spring Boot REST endpoints on `http://localhost:8080/api/`. I implemented a graceful error-handling mechanism so that if the Java service is offline, the client seamlessly falls back to local catalog data."*

### 3. **How is the Order Processing logic structured?**
> *"When a user clicks 'Place Order', the client constructs an `OrderRequest` JSON object containing customer details, promo codes, and item array. The Java `OrderService` validates product availability, calculates itemized totals, applies discount rules, generates a unique receipt ID (`APNA-xxxxxx`), and returns an `OrderResponse` object."*
