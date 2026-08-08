# Changelog

All notable changes to smartERP will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-01-XX

### Added

#### Security & Authentication (Core Module)
- Complete JWT-based authentication system with access and refresh tokens
- Spring Security 6 configuration with stateless authentication
- Role-Based Access Control (RBAC) with granular permissions
- User registration, login, and profile management endpoints
- BCrypt password hashing for secure credential storage
- Custom `JwtAuthenticationFilter` for request validation
- `User`, `Role`, and `Permission` entities with proper relationships
- Default role seeding (ADMIN, USER, MANAGER) on startup

#### Inventory Management Module
- Complete product and item lifecycle management
- Hierarchical category system with parent-child relationships
- Multi-warehouse support with section and bin tracking
- Real-time stock level tracking and batch management
- Inventory movement logging (stock-in, stock-out, transfers, adjustments)
- Stock counting workflows for physical inventory reconciliation
- Item expiration date tracking
- Comprehensive audit trail for all entity changes

#### Database & Migrations
- Flyway database migration framework integration
- Versioned SQL migrations for schema management
- Initial schema creation for Core module (users, roles, permissions)
- Inventory schema creation (products, items, warehouses, stock tables)
- Seed data migration for default roles and permissions
- Proper foreign key constraints and referential integrity
- Index optimization for frequently queried fields

#### Testing
- Comprehensive unit tests for AuthService (10 test cases)
- JwtService unit tests (14 test cases)
- JwtAuthenticationFilter tests (9 test cases)
- Inventory service layer tests (7 test cases for CategoryService)
- Mock-based testing with Mockito and H2 in-memory database
- Edge case and exception handling verification

#### Documentation
- Professional README.md with architecture diagrams
- API endpoint documentation
- Setup and installation guides
- Development guidelines
- Troubleshooting section

### Changed

- Upgraded from Java 11 to Java 21
- Updated to Spring Boot 3.4.0
- Migrated from Hibernate auto DDL to Flyway-managed schema
- Changed Hibernate `ddl-auto` from `update` to `validate`
- Refactored service implementations for better transaction management
- Improved exception handling with global exception handler
- Enhanced error messages for better debugging

### Fixed

- Service implementation method calls and property references
- ID getter methods to use correct field names
- Quantity-related method corrections across services
- Enum references and field mappings
- User reference handling in audit fields

### Security

- Implemented JWT token expiration handling
- Added refresh token mechanism for seamless re-authentication
- Secured all inventory endpoints with role-based authorization
- Protected sensitive user data with password hashing
- Implemented CSRF protection (stateless API)
- Added input validation on all authentication endpoints

### Database

- Created 3 initial Flyway migrations:
  - V1: Core security schema (users, roles, permissions, relationships)
  - V2: Inventory schema (products, items, categories, warehouses, stock)
  - V3: Seed data for default roles and permissions
- Added indexes on username, email, and foreign keys
- Implemented proper cascade rules for relationships
- Added timestamp columns with timezone support

---

## [0.1.0] - Initial Scaffold

### Added
- Multi-module Maven project structure
- Core, Inventory, Logistics, Procurement, and Transaction modules
- Basic entity classes for Inventory domain
- Repository interfaces
- DTO and Mapper classes
- Controller scaffolding

### Changed
- Initial project setup

---

## Future Releases (Planned)

### v1.1.0 - Logistics Module
- Shipment tracking and management
- Route optimization
- Delivery scheduling
- Carrier integration

### v1.2.0 - Procurement Module
- Purchase order management
- Supplier relationship management
- Requisition workflows
- Vendor evaluation

### v1.3.0 - Transaction Module
- Invoice generation and management
- Payment processing
- Financial reporting
- General ledger integration

### v2.0.0 - Advanced Features
- Multi-tenant support
- Advanced analytics dashboard
- Mobile application API
- Third-party integrations (ERP, CRM, eCommerce)

---

**Note**: Dates in future releases are estimates and subject to change based on development priorities.
