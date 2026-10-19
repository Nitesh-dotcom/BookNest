# BookNest — Online Bookstore Web Application

BookNest is a complete, enterprise-grade, full-stack online bookstore application built with **Java 17**, **Spring Boot 3**, **Spring Security + JWT**, **Spring Data JPA**, **MySQL 8**, and **Vanilla HTML5/CSS3/JavaScript (ES6+)**.

---

## Table of Contents
1. [Architecture & Technology Stack](#1-architecture--technology-stack)
2. [Project Structure](#2-project-structure)
3. [Prerequisites](#3-prerequisites)
4. [Step-by-Step Local Setup](#4-step-by-step-local-setup)
5. [Default Accounts](#5-default-accounts)
6. [Business Logic & Key Features](#6-business-logic--key-features)
7. [REST API Documentation](#7-rest-api-documentation)
8. [Testing & Verification](#8-testing--verification)

---

## 1. Architecture & Technology Stack

```
┌────────────────────────────────────────────────────────┐
│           BookNest Frontend (Static Web Client)        │
│  HTML5 + CSS3 + Vanilla ES6 JS (Fetch API + JWT Auth)  │
│  Works in any browser / VS Code Live Server (Port 5500)│
└───────────────────────────┬────────────────────────────┘
                            │ REST / JSON (JWT Bearer)
                            ▼
┌────────────────────────────────────────────────────────┐
│        BookNest Backend (Spring Boot 3.2.5 - Java 17)  │
│  • Spring Web (REST Controllers)                       │
│  • Spring Security 6 (Stateless JWT Filter & RBAC)     │
│  • Spring Data JPA (Hibernate 6 ORM)                   │
│  • Jakarta Bean Validation                             │
│  • Running on http://localhost:8080                    │
└───────────────────────────┬────────────────────────────┘
                            │ JDBC (HikariCP)
                            ▼
┌────────────────────────────────────────────────────────┐
│                 MySQL 8.0 Relational Database          │
│  • Database: booknest_db                               │
│  • 11 Normalized Tables with Foreign Keys & Constraints│
│  • Running on Port 3307 (or default 3306)              │
└────────────────────────────────────────────────────────┘
```

- **Frontend**: Vanilla ES6 JavaScript, HTML5, CSS3 with responsive design (desktop/tablet/mobile), Google Fonts (`Playfair Display` + `Plus Jakarta Sans`), warm cream & terracotta editorial color system. Zero heavy framework dependencies.
- **Backend**: Spring Boot 3.2.5, Java 17, Spring Security 6 with JJWT (`io.jsonwebtoken`), Spring Data JPA, Maven.
- **Database**: MySQL 8.0, 11 tables with relational constraints, foreign keys, cascade rules, and indexes.

---

## 2. Project Structure

```
booknest/
├── frontend/
│   ├── index.html           # Homepage (Hero, Categories, Featured, Bestsellers, Newsletter)
│   ├── books.html           # Catalog (Search, Category filters, Price range, In-stock, Sort, Pagination)
│   ├── book-details.html    # Book details, Verified customer reviews, Related titles, Quantity selector
│   ├── cart.html            # Shopping cart (Guest localStorage + Authenticated DB sync, Subtotal, Free shipping bar)
│   ├── checkout.html        # Checkout workflow, Shipping info, COD & Simulated card payment, Idempotency protection
│   ├── login.html           # Authentication with redirect support, show/hide password, error banners
│   ├── register.html        # Customer registration with live 4-tier password strength indicator
│   ├── account.html         # User profile, statistics, editable name/phone, password change
│   ├── orders.html          # Order tracking history, expandable items snapshot, status badges, cancel button
│   ├── wishlist.html        # Personal wishlist with "Move to Cart" and quick toggle
│   ├── admin.html           # Comprehensive Admin Dashboard (Stats, Book CRUD, Order Status, Category CRUD, Users)
│   ├── about.html           # Story, principles, team, impact statistics
│   ├── contact.html         # Contact form, location information, interactive FAQ accordion
│   ├── css/
│   │   └── style.css        # Unified Design System (CSS variables, buttons, badges, modals, toasts, typography)
│   ├── js/
│   │   ├── api.js           # Centralized fetch wrapper (JWT Bearer injection, auto 401 redirect, error handling)
│   │   ├── auth.js          # Session state, login, register, logout, requireAuth, requireAdmin
│   │   ├── cart.js          # Hybrid cart: Guest (localStorage) + User (MySQL DB) with auto-merge on login
│   │   ├── wishlist.js      # Wishlist state and real-time badge counters
│   │   └── main.js          # Shared navbar, dropdown, search, footer, and toast notification system
│   └── assets/
│       └── placeholder.svg  # High-quality SVG fallback cover artwork
├── backend/
│   ├── pom.xml              # Maven dependencies & build configuration
│   └── src/
│       ├── main/
│       │   ├── java/com/booknest/
│       │   │   ├── configuration/   # SecurityConfig, WebConfig, DataInitializer
│       │   │   ├── controllers/     # REST Endpoints (Auth, Book, Cart, Order, Wishlist, Category, Admin, Newsletter)
│       │   │   ├── dto/             # Data Transfer Objects & Validation Requests
│       │   │   ├── entities/        # JPA Entities (User, Book, Category, Cart, Order, Wishlist, Review, etc.)
│       │   │   ├── exceptions/      # GlobalExceptionHandler, ResourceNotFoundException, etc.
│       │   │   ├── repositories/    # Spring Data JPA Repositories
│       │   │   ├── security/        # JwtTokenProvider, JwtAuthenticationFilter, UserPrincipal
│       │   │   └── services/        # Transactional Business Services
│       │   └── resources/
│       │       └── application.properties # Server port, DataSource, JWT, JPA config
│       └── test/
│           ├── java/com/booknest/   # 13 comprehensive JUnit 5 integration tests
│           └── resources/
│               └── application-test.properties # In-memory H2 MySQL mode test profile
├── database/
│   ├── schema.sql           # Complete DDL script for all 11 tables
│   └── seed_data.sql        # 24 books, 6 categories, default accounts & sample reviews
├── .env.example             # Configuration reference for environment variables
├── .gitignore               # Ignored build outputs and sensitive files
└── README.md                # Documentation and guide
```

---

## 3. Prerequisites

1. **Java Development Kit (JDK) 17+**
2. **Apache Maven 3.8+**
3. **MySQL 8.0+**
4. Any static web server (VS Code Live Server, Python `http.server`, or Node `serve`)

---

## 4. Step-by-Step Local Setup

### Quickest Way: 1-Click Startup (Windows)
Double-click [`start-all.bat`](file:///d:/Code/Project/New%20folder/booknest/start-all.bat) in the project root. It will:
1. Start MySQL 8.0 on port 3307 in its own window.
2. Start the Spring Boot backend on port 8080.
3. Automatically launch the BookNest storefront in your default web browser.

Alternatively, you can run the individual services:
- **MySQL**: Double-click `start-mysql.bat` (or run `& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" --datadir="C:\Users\nkp61\tools\mysql_data" --port=3307 --console`)
- **Backend**: Double-click `start-backend.bat` (or run `java -jar backend/target/booknest-backend-1.0.0.jar`)
- **Frontend**: Double-click `frontend/index.html`

---

### Manual Setup

### Step 1: Start MySQL Database
Ensure MySQL 8 is running on port **3307** (or port **3306** by configuring `application.properties`):

```sql
CREATE DATABASE IF NOT EXISTS booknest_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Optionally initialize schema and seed data manually (the backend `DataInitializer` also automatically seeds data on startup if empty):
```powershell
mysql -u root -proot -P 3307 booknest_db < database/schema.sql
mysql -u root -proot -P 3307 booknest_db < database/seed_data.sql
```

### Step 2: Configure Database Credentials
Edit `backend/src/main/resources/application.properties` or set environment variables:
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/booknest_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

### Step 3: Run the Backend
Using Maven from the `backend/` folder:
```powershell
cd backend
mvn clean spring-boot:run
```
Or execute the pre-built standalone JAR:
```powershell
java -jar backend/target/booknest-backend-1.0.0.jar
```
The REST API will be ready at `http://localhost:8080`.

### Step 4: Run the Frontend
Serve the `frontend/` directory using any static web server:
- **VS Code Live Server**: Right-click `frontend/index.html` → *Open with Live Server*.
- **Python**:
  ```powershell
  cd frontend
  python -m http.server 5500
  ```
- **Node**:
  ```powershell
  npx serve frontend -p 5500
  ```
Navigate to `http://localhost:5500` in your web browser.

---

## 5. Default Accounts

The database comes pre-seeded with two accounts:

| Role | Email Address | Password | Permissions |
|---|---|---|---|
| **Administrator** | `admin@booknest.com` | `Admin@123` | Full access to Admin Dashboard (`/admin.html`), inventory, order status updates, category CRUD, customer management |
| **Customer** | `customer@booknest.com` | `Customer@123` | Browsing, persistent cart, wishlist, checkout, verified reviews, order cancellation |

New users can also register freely at `/register.html`. Newly registered accounts are assigned the `CUSTOMER` role by default.

---

## 6. Business Logic & Key Features

1. **Guest & Authenticated Cart Management**:
   - Guests can add items without logging in (stored in browser `localStorage`).
   - Upon logging in or registering, guest items are automatically merged with their database cart via `POST /api/cart/merge`.
   - Free shipping is automatically applied on orders where subtotal is **$50.00 or higher** (otherwise a flat $4.99 shipping fee is assessed).
   - A standard 5% tax is calculated automatically on the server.
2. **Order Placement & Stock Atomicity**:
   - Every order creation runs in a Spring `@Transactional` block.
   - Available stock quantities are checked and decremented atomically.
   - If requested quantity exceeds inventory, an `InsufficientStockException` is thrown and the transaction rolls back cleanly.
   - Historical item snapshots (unit price and product title) are preserved in `order_items` even if book prices change in the future.
3. **Order Lifecycle**:
   - Status transitions: `PENDING` → `CONFIRMED` → `PROCESSING` → `SHIPPED` → `DELIVERED` (or `CANCELLED`).
   - Customers can cancel an order while it is in `PENDING` or `CONFIRMED` status. Cancellation automatically restores stock quantities to the catalog.
4. **Verified Customer Reviews**:
   - Only customers who have an order with `DELIVERED` status containing a specific book receive a verified purchase badge (`✓ Verified Purchase`) on their reviews.
5. **Catalog Search & Filtering**:
   - Server-side filtering by search query, category, price bounds, availability, and sorting (`price_asc`, `price_desc`, `title_asc`, `title_desc`, `newest`, `rating`).
   - Pagination with preservation of active filters across history navigation.

---

## 7. REST API Documentation

### Public Endpoints
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register new customer account |
| `POST` | `/api/auth/login` | Authenticate and obtain JWT Bearer token |
| `GET` | `/api/categories` | List all active book categories |
| `GET` | `/api/categories/{id}` | Get category by ID |
| `GET` | `/api/books` | Paged book catalog with search, category, price, and sort filters |
| `GET` | `/api/books/{id}` | Get single book details |
| `GET` | `/api/books/{id}/reviews` | Get customer reviews for a book |
| `POST` | `/api/newsletter/subscribe` | Subscribe email to reader newsletter |

### Customer Protected Endpoints (Requires `Authorization: Bearer <token>`)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/cart` | Get current customer's shopping cart |
| `POST` | `/api/cart/items` | Add book to cart |
| `PATCH` | `/api/cart/items/{id}` | Update quantity of cart item |
| `DELETE` | `/api/cart/items/{id}` | Remove item from cart |
| `DELETE` | `/api/cart` | Clear all items from cart |
| `POST` | `/api/cart/merge` | Merge guest items into customer cart |
| `GET` | `/api/orders` | View customer's order history (paged) |
| `GET` | `/api/orders/{id}` | View single order detail |
| `POST` | `/api/orders` | Place new order from current cart |
| `DELETE` | `/api/orders/{id}` | Cancel pending/confirmed order and restore stock |
| `GET` | `/api/wishlist` | View personal wishlist |
| `POST` | `/api/wishlist/items` | Add book to wishlist |
| `DELETE` | `/api/wishlist/items/{bookId}`| Remove book from wishlist |
| `POST` | `/api/wishlist/items/{bookId}/move-to-cart` | Move wishlist item directly to cart |
| `POST` | `/api/books/{id}/reviews` | Post a customer review (1–5 stars) |
| `GET` | `/api/users/profile` | Get logged-in user profile |
| `PUT` | `/api/users/profile` | Update profile (full name, phone) |
| `PUT` | `/api/users/password` | Change password |

### Administrator Endpoints (Requires `ROLE_ADMIN`)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/admin/dashboard` | Dashboard metrics (total books, orders, users, revenue, alerts) |
| `GET` | `/api/admin/books` | Get all books including inactive titles |
| `POST` | `/api/admin/books` | Create a new book |
| `PUT` | `/api/admin/books/{id}` | Update book details |
| `PATCH` | `/api/admin/books/{id}/stock` | Quick-update book stock quantity |
| `PATCH` | `/api/admin/books/{id}/toggle-active` | Toggle visibility of book in catalog |
| `GET` | `/api/admin/orders` | View all customer orders |
| `PATCH` | `/api/admin/orders/{id}/status` | Transition order status |
| `POST` | `/api/admin/categories` | Create new category |
| `PUT` | `/api/admin/categories/{id}` | Update category |
| `DELETE` | `/api/admin/categories/{id}` | Delete empty category |
| `GET` | `/api/admin/users` | List all registered users |

---

## 8. Testing & Verification

The backend includes a comprehensive suite of unit and integration tests executing against an in-memory H2 database in MySQL mode.

To run all automated tests:
```powershell
cd backend
mvn test
```

### Test Coverage Highlights:
- **Authentication**: Registration, duplicate email rejection, password confirmation validation.
- **Catalog**: Book creation, retrieval, unique ISBN validation, title/author search.
- **Cart**: Subtotal & tax calculations, free shipping threshold ($50.00), stock bounds checks.
- **Orders**: Atomic stock reduction, cart clearing upon order, stock restoration upon cancellation, tenant order privacy protection.
