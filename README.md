SmartERP Inventory Management System

Overview

SmartERP Inventory Management System is a comprehensive solution for managing inventory, warehouses, and stock counts. It is built using Java, Spring Boot, and Maven, and it leverages a PostgreSQL database for data persistence. The application is designed to handle various use cases and potential edge cases thoroughly, ensuring a robust and production-ready system.

Features

- **Category Management**: Create, update, delete, and retrieve categories and their associated items.
- **Warehouse Management**: Manage warehouses, including creating, updating, deleting, and retrieving warehouse details.
- **Warehouse Section Management**: Handle warehouse sections, including capacity checks and stock management.
- **Stock Count Management**: Manage stock counts, including creating, updating, deleting, and retrieving stock count details.
- **Inventory Movement**: Track inventory movements across different warehouses and sections.

Technologies Used

- **Java**: Programming language used for the application.
- **Spring Boot**: Framework used for building the application.
- **Maven**: Build automation tool used for managing dependencies and building the project.
- **PostgreSQL**: Database used for data persistence.
- **Docker**: Used for containerizing the application and database.

Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Docker (for running the application in containers)

Getting Started

Clone the Repository

```sh
git clone https://github.com/damilolaowoyele/SmartERP.git
cd SmartERP
```

Build the Application

```sh
mvn clean install
```

Running the Application

Using Docker Compose

1. Ensure Docker is installed and running.
2. Navigate to the `Inventory` directory.
3. Run the following command to start the application and PostgreSQL database:

```sh
docker-compose up
```

Without Docker

1. Ensure PostgreSQL is installed and running.
2. Update the `application.properties` file with your PostgreSQL configuration.
3. Run the application using Maven:

```sh
mvn spring-boot:run
```

API Endpoints

Category Controller

- **POST /api/categories**: Create a new category.
- **PUT /api/categories/{categoryId}**: Update an existing category.
- **GET /api/categories/{categoryId}**: Retrieve a category by its ID.
- **GET /api/categories**: Retrieve all categories.
- **GET /api/categories/root**: Retrieve root categories.
- **GET /api/categories/subcategories/{parentId}**: Retrieve subcategories by parent ID.
- **DELETE /api/categories/{categoryId}**: Delete a category.
- **POST /api/categories/{categoryId}/items/{itemId}**: Add an item to a category.
- **DELETE /api/categories/{categoryId}/items/{itemId}**: Remove an item from a category.

Warehouse Controller

- **POST /api/warehouses**: Create a new warehouse.
- **PUT /api/warehouses/{warehouseId}**: Update an existing warehouse.
- **GET /api/warehouses/{warehouseId}**: Retrieve a warehouse by its ID.
- **GET /api/warehouses**: Retrieve all warehouses.
- **GET /api/warehouses/search**: Search warehouses by location.
- **DELETE /api/warehouses/{warehouseId}**: Delete a warehouse.

Warehouse Section Controller

- **POST /api/warehouse-sections**: Create a new warehouse section.
- **PUT /api/warehouse-sections/{sectionId}**: Update an existing warehouse section.
- **GET /api/warehouse-sections/{sectionId}**: Retrieve a warehouse section by its ID.
- **GET /api/warehouse-sections**: Retrieve all warehouse sections.
- **GET /api/warehouse-sections/warehouse/{warehouseId}**: Retrieve warehouse sections by warehouse ID.
- **DELETE /api/warehouse-sections/{sectionId}**: Delete a warehouse section.
- **GET /api/warehouse-sections/{sectionId}/has-enough-capacity**: Check if a warehouse section has enough capacity.
- **GET /api/warehouse-sections/{sectionId}/total-stock-count**: Retrieve the total stock count for a warehouse section.
- **POST /api/warehouse-sections/{sectionId}/add-stock**: Add stock to a warehouse section.
- **POST /api/warehouse-sections/{sectionId}/remove-stock**: Remove stock from a warehouse section.

Stock Count Controller

- **POST /api/stock-counts**: Create a new stock count.
- **GET /api/stock-counts/{countId}**: Retrieve a stock count by its ID.
- **GET /api/stock-counts**: Retrieve all stock counts.
- **GET /api/stock-counts/item/{itemId}**: Retrieve stock counts by item ID.
- **GET /api/stock-counts/warehouse/{warehouseId}**: Retrieve stock counts by warehouse ID.
- **GET /api/stock-counts/warehouse/{warehouseId}/section/{sectionId}**: Retrieve stock counts by warehouse and section ID.
- **GET /api/stock-counts/available**: Retrieve available stock counts.
- **DELETE /api/stock-counts/{countId}**: Delete a stock count.
- **GET /api/stock-counts/total-batch-quantity/{itemId}**: Retrieve the total batch quantity for an item.
- **GET /api/stock-counts/total-aggregated-quantity/{itemId}**: Retrieve the total aggregated quantity for an item.

Inventory Movement Controller

- **POST /api/inventory-movements**: Create a new inventory movement.
- **GET /api/inventory-movements/{movementId}**: Retrieve an inventory movement by its ID.
- **GET /api/inventory-movements**: Retrieve all inventory movements.
- **GET /api/inventory-movements/item-batch/{itemBatchId}**: Retrieve inventory movements by item batch ID.
- **GET /api/inventory-movements/warehouse/{warehouseId}**: Retrieve inventory movements by warehouse ID.
- **GET /api/inventory-movements/date-range**: Retrieve inventory movements by date range.
- **GET /api/inventory-movements/user/{userId}**: Retrieve inventory movements by user ID.

Exception Handling

The application uses a global exception handler to manage exceptions such as `ResourceNotFoundException` and `InvalidOperationException`. This ensures that meaningful error messages are returned to the client.

Security

Sensitive information such as database credentials should be stored in environment variables or a `.env` file, which is referenced in the `docker-compose.yml` file.

Contributing

Contributions are welcome! Please fork the repository and create a pull request with your changes.

License

This project is licensed under the MIT License. See the `LICENSE` file for details.

Contact

For any inquiries or issues, please contact the repository owner at [damilolaowoyele](https://github.com/damilolaowoyele).
