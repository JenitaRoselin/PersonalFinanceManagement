# Personal Finance Management

A modern, full-stack personal finance application built with **Spring Boot 3 (Java 17)** and **React (Vite)**, backed by **PostgreSQL** with automated **Flyway** database migrations.

---

## Key Features

- **User Authentication & Security**: Stateless JWT-based authentication, BCrypt password hashing, and role-agnostic user data isolation.
- **Dynamic Budget Calculation**: Budget utilization and remaining balances are dynamically computed from transactions on the fly.
- **Safe-to-Spend Insights**: Real-time calculation of discretionary funds after accounting for monthly committed budget limits.
- **Income & Expense Tracking**: Filterable and searchable ledger with category and payment method tagging (UPI, Cash, Card, Bank).
- **Interactive Reports & Breakdowns**: Visual analytics for monthly trends, income vs. expense balance, and category-level distribution.
- **Curated Visual System**: Ivory, Charcoal, and Dark Turquoise design system with custom inline SVG icons (strictly zero emojis).

---

## Technology Stack

- **Backend**: Java 17, Spring Boot, Spring Security 6, Spring Data JPA, Flyway, PostgreSQL Driver, JJWT, Lombok.
- **Frontend**: React, Vite, Vanilla CSS Design System, Axios.
- **Database**: PostgreSQL with schema versioning via Flyway.

---

## Project Structure

```
PersonalFinanceManagement/
├── client/                     # Vite + React Frontend
│   ├── src/
│   │   ├── assets/             # SVG icons and visual assets
│   │   ├── components/         # Modular UI components (modals, tables, cards)
│   │   ├── context/            # React AuthContext and state providers
│   │   ├── pages/              # Dashboard, Transactions, Budgets, Reports, Goals
│   │   └── services/           # Axios API services
│   └── package.json
├── server/                     # Spring Boot Backend
│   ├── src/main/java/com/jenitaroselin/personalfinancemanagement/
│   │   ├── config/             # Security & CORS configuration
│   │   ├── controller/         # REST API endpoints
│   │   ├── dto/                # Request & Response Data Transfer Objects
│   │   ├── entity/             # JPA entity models
│   │   ├── repository/         # Spring Data JPA repositories
│   │   ├── service/            # Core business logic & financial calculations
│   │   └── util/               # JWT token utilities & security context helpers
│   ├── src/main/resources/
│   │   ├── db/migration/       # Flyway database migration scripts (V1-V6)
│   │   └── application.properties
│   └── pom.xml
└── README.md
```

---

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+ and npm
- PostgreSQL 14+ running locally on port 5432 (database: `personal_finance`)

### 1. Database Setup

Ensure PostgreSQL is running and create the database:
```sql
CREATE DATABASE personal_finance;
```

### 2. Run the Backend

```bash
cd server
./mvnw clean spring-boot:run
```
The backend starts on `http://localhost:8080`. Flyway will automatically run migrations `V1` through `V6`.

### 3. Run the Frontend

```bash
cd client
npm install
npm run dev
```
The frontend dev server runs at `http://localhost:5173`.
