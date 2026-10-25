# Apna Store - Royal Couture E-Commerce Platform

A production-grade, full-stack luxury e-commerce web platform powered by **Java 17 & Spring Boot 3.2** with a responsive, glassmorphism-styled modern frontend.

---

## 📁 Project Directory Structure

```text
apna-store/
│
├── frontend/                     # Modern UI Storefront (Vanilla HTML5 / CSS3 / ES6 JS)
│   ├── index.html                # Main storefront catalog & navigation
│   ├── styles.css                # Royal aesthetic, glassmorphism design & animations
│   ├── app.js                    # Core client logic, state management, API integration
│   ├── data.js                   # Client-side product catalog & fallback state
│   └── assets/                   # High-resolution hero banners & product photography
│
├── backend/                      # Production Spring Boot 3.2 Architecture
│   ├── pom.xml                   # Maven build descriptor
│   ├── mvnw / mvnw.cmd           # Self-contained Unix and Windows Maven wrappers
│   ├── .mvn/wrapper/             # Maven wrapper properties & binaries
│   ├── README.md                 # Backend documentation
│   └── src/main/                 # Layered Spring Boot Architecture
│       ├── java/com/apnastore/
│       │   ├── ApnaStoreApplication.java  # Spring Boot entry point
│       │   ├── config/WebConfig.java      # Static asset routing & CORS
│       │   ├── controller/                # REST Controllers (Product & Order)
│       │   ├── service/                   # Business logic & pricing/promo engines
│       │   └── model/                     # Domain entities & request/response DTOs
│       └── resources/
│           └── application.properties     # Server configuration (Port 8080)
│
├── legacy/                       # Low-Level Java Reference Implementation
│   └── ApnaStoreServer.java      # Zero-dependency JDK Server (com.sun.net.httpserver)
│
├── docs/                         # Technical Documentation
│   └── RESUME_PROJECT_DESCRIPTION.md # Interview talking points & architecture highlights
│
├── start-store.bat / run.bat     # 1-Click launcher (boots Spring Boot via mvnw & opens browser)
├── start-store.ps1               # PowerShell launcher
├── stop-store.bat                # 1-Click server stop script
├── server.ps1                    # Standalone PowerShell HTTP Server (Port 3000, fallback)
└── README.md                     # Project documentation
```

---

## 🏛 Why `legacy/ApnaStoreServer.java` Exists

The project includes `legacy/ApnaStoreServer.java`, a zero-dependency server built using standard Java JDK libraries (`com.sun.net.httpserver`):

1. **Interview Demonstration**: Shows recruiters and interviewers that you understand low-level networking, raw HTTP byte streams, MIME types, URI decoding, and socket handlers under the hood without relying on Spring Boot abstractions.
2. **Framework-Free Resilience**: Can be executed standalone with `java legacy/ApnaStoreServer.java` on any machine with Java 11+ without installing Maven or downloading dependencies.
3. **Evolution Story**: Serves as a clear architectural comparison point during interviews to explain *why* enterprises transition from raw socket handlers to Spring Boot (Dependency Injection, annotations, declarative transaction management, validation, and auto-configuration).

---

## 🚀 How to Run

### Method 1: 1-Click Launch (Recommended)
1. Double-click `start-store.bat` (or `run.bat`).
2. The launcher automatically:
   - Detects your Java JDK
   - Launches Spring Boot 3 using the Maven Wrapper (`mvnw.cmd spring-boot:run`)
   - Opens `http://localhost:8080/` in your default browser.

### Method 2: Command Line (PowerShell / Terminal)
```bash
# Navigate to backend:
cd backend

# Windows:
.\mvnw.cmd spring-boot:run

# Linux / macOS:
./mvnw spring-boot:run
```
Then open [http://localhost:8080/](http://localhost:8080/) in your browser.

### Method 3: PowerShell Server (Frontend-Only Fallback)
If Java is unavailable, launch the native PowerShell HTTP server:
```powershell
.\server.ps1
```
Then open [http://localhost:3000/](http://localhost:3000/) in your browser.

---

## 🛑 How to Stop the Server
Double-click `stop-store.bat` or press `Ctrl + C` in the server terminal window.

---

## 📡 REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/` | Serves the store frontend (`index.html`) |
| `GET` | `/api/products` | Returns all products (supports `?category=...` and `?q=...`) |
| `GET` | `/api/products/categories` | Returns category list and item counts |
| `GET` | `/api/products/{id}` | Returns single product details |
| `POST` | `/api/orders` | Processes checkout order, calculates discounts, returns order receipt |
| `GET` | `/api/orders/{id}` | Retrieves past order status by ID |

---

## 💎 Features
- **One-Command Full-Stack Execution**: Spring Boot hosts both the REST API and the static frontend UI on port 8080.
- **Dynamic Category Filtering**: Ethnic Women, Ethnic Men, Jewelry, Luxury Outerwear.
- **Search & Live Filtering**: Instant search across titles, categories, and descriptions.
- **Cart & Wishlist Persistence**: Saved across browser reloads using HTML5 `localStorage`.
- **Promo Discount Engine**: Supports `APNA20`, `ROYAL20`, `WELCOME10`, and `VIP50`.