# StockSimulator_backend

No problem 👍 Here’s a **clear, professional README** tailored just for your **Stock Simulator Backend (Spring Boot + JWT + MySQL)** repo:

---

# 📈 Stock Simulator – Backend

This is the **backend service** of the Stock Simulator project, built with **Spring Boot**.
It provides secure **REST APIs** for portfolio management, transactions, watchlists, and user authentication. It also integrates with the **Twelve Data API** to fetch real-time stock prices.

---

## 🚀 Features

* 🔐 **JWT Authentication** – secure login and protected API routes
* 👤 **User Management** – registration, login, portfolio balance tracking
* 📊 **Portfolio Management** – add, update, and delete stock holdings
* 💹 **Real-Time Stock Prices** – integrated with Twelve Data API
* 📝 **Transactions & Watchlist** – record trades and track favorite stocks
* 💰 **Profit/Loss Calculation** – portfolio value and performance tracking
* 🛡 **Role-Based Access Control** – Admin & User endpoints

---

## 🛠 Tech Stack

* **Java 17+**
* **Spring Boot** (Web, Security, JPA, Validation)
* **JWT** for authentication
* **MySQL** as the database
* **Hibernate / JPA** for ORM
* **Maven** for build & dependencies
* **Docker** for containerization

---

## 📂 Project Structure

```
backend/
│── src/main/java/com/stocksimulator/   # Source code
│   ├── controller/    # REST Controllers
│   ├── service/       # Business Logic
│   ├── repository/    # JPA Repositories
│   ├── model/         # Entities & DTOs
│   └── security/      # JWT & Security Config
│── src/main/resources/
│   ├── application.properties  # DB & API config
│── pom.xml
```

---

## ⚡ Getting Started

### 1. Clone the repo

```bash
git clone https://github.com/<your-username>/stock-simulator-backend.git
cd stock-simulator-backend
```

### 2. Configure Database

In `src/main/resources/application.properties`, update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stocksim_db
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### 3. Run with Maven

```bash
./mvnw spring-boot:run
```

The backend will start on:

```
http://localhost:8080
```

---

## 🔑 API Endpoints (Sample)

* `POST /api/auth/register` → Register new user
* `POST /api/auth/login` → Login & get JWT token
* `GET /api/portfolio/user/{id}` → Get user’s portfolio
* `POST /api/portfolio/add` → Add stock to portfolio
* `DELETE /api/portfolio/delete/{id}` → Remove stock
* `GET /api/stock-price/closing-price?stocksymbol=XYZ` → Fetch latest price (via Twelve Data API)
* `GET /api/admin/active-traders` → Admin-only: get most active traders

---

## 🐳 Run with Docker

### Build Docker image

```bash
docker build -t stock-simulator-backend .
```

### Run container

```bash
docker run -p 8080:8080 stock-simulator-backend
```

---

## 📌 Future Improvements

* Add caching for stock price API
* Improve analytics dashboard
* CI/CD deployment setup


