# smartERP

**Enterprise Resource Planning System - Microservices Architecture**

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green)
![Security](https://img.shields.io/badge/Security-JWT%20%26%20Spring%20Security-red)
![Database](https://img.shields.io/badge/Database-PostgreSQL-blue)
![Build](https://img.shields.io/badge/Build-Maven-orange)
![Status](https://img.shields.io/badge/Status-Production%20Ready-brightgreen)

## 📖 Project Overview

**smartERP** is a modular, microservices-based Enterprise Resource Planning system designed to streamline business operations. Built with modern Java technologies, it provides a robust foundation for managing inventory, logistics, procurement, and transactions with enterprise-grade security.

### Current Capabilities
- ✅ **Secure Authentication & Authorization**: JWT-based stateless auth with Role/Permission granularity.
- ✅ **Inventory Management**: Complete lifecycle management for products, items, categories, warehouses, and stock movements.
- ✅ **Audit Trail**: Comprehensive tracking of all data changes with user attribution.
- ✅ **Database Migration**: Production-ready Flyway setup for schema versioning.
- ✅ **Test Coverage**: Extensive unit testing for business logic and security flows.

### Project Status
The **Core** (Security) and **Inventory** modules are fully implemented and tested. Modules for Logistics, Procurement, and Transaction are scaffolded and ready for development.

---

## 🚀 Key Features

### Security Module (Core)
- **JWT Authentication**: Stateless authentication with Access and Refresh tokens.
- **Role-Based Access Control (RBAC)**: Granular permissions assigned via roles.
- **User Management**: Registration, profile management, and secure password handling (BCrypt).
- **Auto-Discovery**: Secure endpoint exposure via Spring Actuator.

### Inventory Module
- **Product & Item Management**: Hierarchical categorization and detailed item attributes.
- **Warehouse Management**: Multi-warehouse support with section/bin tracking.
- **Stock Control**: Real-time stock levels, batch tracking, and expiration management.
- **Movements**: Detailed logging of stock-in, stock-out, transfers, and adjustments.
- **Stock Counting**: Cycle count and physical inventory reconciliation workflows.

---

## 🏗 Architecture

smartERP follows a **Modular Monolith** architecture designed for easy extraction into microservices.

```
┌─────────────┐      ┌──────────────┐
│   Client    │─────▶│  API Gateway │
└─────────────┘      └──────┬───────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
   ┌─────────┐        ┌──────────┐       ┌───────────┐
   │  Core   │        │ Inventory│       │ Logistics │
   │ (Auth)  │        │          │       │ (Skeleton)│
   └────┬────┘        └────┬─────┘       └────┬──────┘
        │                  │                  │
        └──────────────────┴──────────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │ PostgreSQL  │
                    │   +Flyway   │
                    └─────────────┘
```

### Module Structure
| Module | Responsibility | Status |
| :--- | :--- | :--- |
| **Core** | Security, Auth, User Mgmt, Common Utils | ✅ Complete |
| **Inventory** | Products, Stock, Warehouses, Movements | ✅ Complete |
| **Logistics** | Shipping, Routing, Delivery | 🚧 Skeleton |
| **Procurement** | Purchasing, Suppliers, Orders | 🚧 Skeleton |
| **Transaction** | Financials, Invoicing, Payments | 🚧 Skeleton |

---

## 🛠 Technology Stack

| Component | Technology | Version |
| :--- | :--- | :--- |
| **Language** | Java | 21 |
| **Framework** | Spring Boot | 3.4.0 |
| **Security** | Spring Security 6 + JWT | Latest |
| **Database** | PostgreSQL | 15+ |
| **ORM** | Hibernate / JPA | 6.x |
| **Migrations** | Flyway | 10.x |
| **Build Tool** | Maven | 3.9+ |
| **Mapping** | MapStruct | 1.5+ |
| **Utils** | Lombok | 1.18+ |
| **Testing** | JUnit 5, Mockito, H2 | Latest |

---

## 📂 Project Structure

```text
smartERP/
├── Core/                   # Security & Auth Module
│   ├── src/main/java/...   # Entities, Repos, Services, Controllers, Security Config
│   └── src/test/...        # Unit & Integration Tests
├── Inventory/              # Inventory Management Module
│   ├── src/main/java/...   # Domain Logic
│   └── src/test/...        # Unit Tests
├── Logistics/              # Future Module
├── Procurement/            # Future Module
├── Transaction/            # Future Module
├── pom.xml                 # Parent POM
└── README.md
```

---

## ⚙️ Prerequisites

Before running smartERP, ensure you have:
- **JDK 21** installed and `JAVA_HOME` set.
- **Maven 3.9+** installed.
- **PostgreSQL 15+** running locally or accessible via network.
- **Docker** (optional) for containerized database.

---

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/damilolaowoyele/SmartERP.git
cd SmartERP
```

### 2. Database Configuration
Create a PostgreSQL database named `smarterp`:
```sql
CREATE DATABASE smarterp;
```

Update `application.properties` in the `Core` module (or use Environment Variables):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smarterp
spring.datasource.username=your_user
spring.datasource.password=your_password
```

### 3. Build the Application
From the root directory:
```bash
mvn clean install
```

### 4. Run Migrations & Start
Flyway migrations run automatically on startup. Run the Core module first to establish security:
```bash
cd Core
mvn spring-boot:run
```

To run the Inventory module (requires Core to be running for auth):
```bash
cd ../Inventory
mvn spring-boot:run
```

---

## 🔐 Authentication & Authorization

### Flow
1. **Register**: `POST /api/auth/register`
2. **Login**: `POST /api/auth/login` → Returns `accessToken` & `refreshToken`.
3. **Access Protected Resources**: Include `Authorization: Bearer <accessToken>` in headers.
4. **Refresh**: `POST /api/auth/refresh` with `refreshToken`.

### Roles & Permissions
- **Roles**: `ADMIN`, `USER`, `MANAGER` (Configurable via DB seed).
- **Permissions**: Granular strings like `inventory:item:create`, `user:read`.
- **Enforcement**: Methods secured with `@PreAuthorize("hasAuthority('...')")`.

### Default Admin Account
On first run, the system seeds an `ADMIN` role. You must register the first admin user manually via the registration endpoint.

---

## 🗄 Database & Flyway

smartERP uses **Flyway** for strict schema versioning. Hibernate is configured to `validate` only.

- **Location**: `src/main/resources/db/migration` (in Core module)
- **Naming**: `V<Version>__<description>.sql` (e.g., `V1__initial_schema.sql`)
- **Baseline**: Automatically applied on first run against an empty DB.
- **Seed Data**: Default roles and permissions are inserted via `V3__seed_roles_permissions.sql`.

**⚠️ Important**: Never modify an applied migration file. Create a new versioned file for changes.

#### Current Migrations
1. `V1__initial_schema.sql`: Core tables (users, roles, permissions, relationships).
2. `V2__create_inventory_schema.sql`: Inventory tables (products, items, warehouses, stock).
3. `V3__seed_roles_permissions.sql`: Default security data.

---

## 🧪 Testing

Run all tests from root:
```bash
mvn test
```

Run specific module tests:
```bash
mvn test -pl Core
mvn test -pl Inventory
```

**Coverage**:
- Service Layer Logic (Auth, Inventory)
- Security Filters & JWT Utils
- Authentication Flows
- Exception Handling

---

## 📝 API Documentation

### Authentication Endpoints
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login & get tokens | No |
| POST | `/api/auth/refresh` | Refresh access token | No (Refresh Token) |
| GET | `/api/auth/me` | Get current user profile | Yes |

### Inventory Endpoints (Sample)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| GET | `/api/inventory/categories` | List categories | Yes |
| POST | `/api/inventory/products` | Create product | Yes (Permission) |
| GET | `/api/inventory/warehouses` | List warehouses | Yes |
| POST | `/api/inventory/movements` | Record stock movement | Yes (Permission) |

*(Full API spec available via Postman collection or Swagger UI if enabled)*

---

## 🛠 Development Guide

### Adding a New Feature
1. Create Entity in respective module.
2. Create Flyway migration `Vx__.sql` in `Core/src/main/resources/db/migration`.
3. Create Repository, Service, Controller.
4. Add Unit Tests.
5. Secure endpoints with `@PreAuthorize`.

### Coding Conventions
- Use **Lombok** for boilerplate (`@Data`, `@Builder`, etc.).
- Use **MapStruct** for DTO mapping.
- Follow **REST** principles for controllers.
- Keep services transactional (`@Transactional`).
- Always use DTOs for API requests/responses.

---

## 🔧 Troubleshooting

### Database Connection Errors
Ensure PostgreSQL is running and credentials in `application.properties` match your setup.

### Flyway Migration Failures
If migrations fail due to existing tables, ensure the DB is empty or configure `flyway.baseline-on-first=true`.

### JWT Token Expired
Use the `/api/auth/refresh` endpoint with your valid refresh token to get a new access token.

---

## 📄 License
MIT License - See LICENSE file for details.

---

## 🤝 Contributing
Contributions are welcome! Please fork the repository and create a pull request with your changes. Ensure all tests pass and migrations are valid before submitting.

## 📬 Contact
For any inquiries or issues, please contact the repository owner at [damilolaowoyele](https://github.com/damilolaowoyele).
