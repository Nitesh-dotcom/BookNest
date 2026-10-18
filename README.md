# 📚 BookNest — Online Bookstore

**BookNest** is a full-stack online bookstore web application designed to make discovering, browsing, and purchasing books simple and convenient. It provides customers with a smooth shopping experience and administrators with tools to manage books, inventory, and orders.

## ✨ Features

### 🛍️ Customer Features
- Browse books by category, author, and title.
- Search, filter, sort, and explore the book catalog.
- View detailed book information and cover images.
- Add books to a shopping cart and wishlist.
- Register, log in, and manage an account.
- Place orders using Cash on Delivery (COD).
- View order history and track order status.
- Submit ratings and reviews, subject to purchase and review rules.

### 🛠️ Admin Features
- Add, update, and manage books.
- Manage categories, prices, and stock quantities.
- Upload or update book cover images.
- View and manage customer orders.
- Update order statuses.
- Monitor inventory and sales information through an admin dashboard.

### 🔐 Security
- Password hashing.
- Authentication and role-based authorization.
- Server-side validation.
- Protected customer and administrator operations.
- Secure order and inventory management.

## 🧰 Tech Stack

| Technology | Purpose |
|---|---|
| HTML5 | Webpage structure |
| CSS3 | Styling and responsive design |
| JavaScript | Frontend functionality |
| Java | Backend development |
| Spring Boot | REST API development |
| Spring Data JPA | Database access |
| Spring Security | Authentication and authorization |
| MySQL | Relational database |
| Maven | Dependency management |
| Git & GitHub | Version control |
| Postman | API testing |

## 🏗️ Project Architecture

```text
                 ┌─────────────────────┐
                 │     Customer /      │
                 │       Admin         │
                 └──────────┬──────────┘
                            │
                 ┌──────────▼──────────┐
                 │      Frontend       │
                 │ HTML, CSS, JavaScript│
                 └──────────┬──────────┘
                            │ REST API
                 ┌──────────▼──────────┐
                 │    Spring Boot      │
                 │ Controllers         │
                 │ Services            │
                 │ Repositories        │
                 │ Security & Validation│
                 └──────────┬──────────┘
                            │
                 ┌──────────▼──────────┐
                 │        MySQL        │
                 │ Books, Users,       │
                 │ Carts, Orders       │
                 └─────────────────────┘
```

## 📂 Project Structure

```text
BookNest/
├── frontend/
│   ├── index.html
│   ├── books.html
│   ├── book-details.html
│   ├── cart.html
│   ├── checkout.html
│   ├── login.html
│   ├── register.html
│   ├── account.html
│   ├── orders.html
│   ├── wishlist.html
│   ├── css/
│   ├── js/
│   └── assets/
│
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   └── resources/
│       └── test/
│
├── database/
│   └── schema.sql
│
├── .env.example
├── .gitignore
└── README.md
```

*The final directory structure may vary slightly depending on the implementation.*

## ⚙️ Getting Started

### Prerequisites

Install the following software before running the project:

- [Java JDK](https://www.oracle.com/java/technologies/downloads/)
- [MySQL](https://dev.mysql.com/downloads/)
- [Apache Maven](https://maven.apache.org/download.cgi)
- [Visual Studio Code](https://code.visualstudio.com/) or IntelliJ IDEA
- [Git](https://git-scm.com/)

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/BookNest.git
cd BookNest
```

Replace `YOUR_USERNAME` with your GitHub username.

### 2. Set up the database

Start MySQL and create a database:

```sql
CREATE DATABASE booknest;
```

Configure the database connection using environment variables or the backend's configuration file.

Example environment variables:

```text
DB_URL=jdbc:mysql://localhost:3306/booknest
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
```

Use your own local credentials. Never commit actual passwords or secrets to GitHub.

If the project includes `database/schema.sql`, follow the instructions in that file or the backend documentation to initialize the schema and sample data.

### 3. Configure the backend

Open a terminal:

```bash
cd backend
```

Build the project:

```bash
mvn clean package
```

Run the application:

```bash
mvn spring-boot:run
```

The default Spring Boot address is usually:

```text
http://localhost:8080
```

This assumes the backend is configured to use port `8080`.

### 4. Run the frontend

Open the `frontend` folder in VS Code and use the Live Server extension or another local development server.

For example, the frontend may be available at:

```text
http://127.0.0.1:5500
```

Configure the frontend API base URL to point to the running backend.

Ensure the backend CORS configuration permits the frontend's development origin.

**Note:** These commands and addresses are defaults. Follow the actual project configuration if it differs.

## 🔌 API Overview

The backend can expose REST endpoints such as:

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/books` | Retrieve books |
| GET | `/api/books/{id}` | Retrieve book details |
| GET | `/api/categories` | Retrieve categories |
| POST | `/api/auth/register` | Register a customer |
| POST | `/api/auth/login` | Authenticate a customer |
| GET | `/api/cart` | Retrieve the current cart |
| POST | `/api/cart/items` | Add an item to the cart |
| GET | `/api/orders` | Retrieve the customer's orders |
| POST | `/api/orders` | Place an order |
| POST | `/api/admin/books` | Add a book as an administrator |
| GET | `/api/admin/orders` | View orders as an administrator |

The exact routes and authentication requirements depend on the implemented backend. Refer to the API documentation and source code for the final endpoint definitions.

## 🗄️ Database Overview

The database may contain the following tables:

- `users` — Customer accounts and roles.
- `books` — Book details, prices, and inventory.
- `categories` — Book categories.
- `carts` and `cart_items` — Shopping cart information.
- `orders` and `order_items` — Purchase records.
- `wishlists` and `wishlist_items` — Saved books.
- `reviews` — Customer ratings and feedback.

Foreign keys, unique constraints, and transactions help maintain data integrity.

## 🧪 Testing

Run backend tests using:

```bash
cd backend
mvn test
```

Test the following workflows before considering the application ready:

- Registration, login, and logout.
- Book search, filtering, and pagination.
- Cart quantity changes and total calculations.
- Checkout and order creation.
- Stock validation and concurrent purchases.
- Customer access restrictions.
- Administrator authorization.
- Order cancellation and inventory restoration.

Use Postman to test API requests and error responses.

## 🔒 Security Notes

- Store passwords using a secure password-hashing algorithm.
- Keep database credentials and secrets out of source control.
- Validate user input on the server.
- Protect administrative endpoints with role-based authorization.
- Calculate order totals using trusted backend data.
- Do not store payment card details.
- Use HTTPS and secure cookie settings in production.

## 🚀 Future Enhancements

- Online payments using a payment gateway.
- Book recommendations.
- Coupon codes and promotional offers.
- Email notifications for orders.
- Advanced sales analytics.
- Product reviews and moderation tools.
- Docker-based deployment.
- Automated CI/CD using GitHub Actions.

## 🎯 Project Goals

BookNest aims to demonstrate practical full-stack development skills, including frontend development, REST API design, relational database management, authentication, inventory management, and order processing.

## 👨‍💻 Author

**Nitesh**

Engineering Student | Aspiring Software Developer

GitHub: [YOUR_GITHUB_PROFILE](https://github.com/YOUR_USERNAME)

---

⭐ If you find this project useful, consider giving the repository a star!

*Note: Update this README to reflect the features actually implemented and tested in your repository. Do not advertise incomplete functionality as production-ready.*
