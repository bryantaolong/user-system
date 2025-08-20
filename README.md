# User System

[中文说明请见这里 (Chinese README here)](./README_zh.md)

## Project Overview

This project is a user management system based on Spring Boot 3, supporting user registration, login, information management, role-based access control, and data export. The backend uses PostgreSQL as the main database and Redis for caching and distributed scenarios. JWT is used for stateless authentication and role-based authorization.

## Tech Stack

* Java 17
* Spring Boot 3.5.4
* MyBatis
* PostgreSQL 17.x
* Redis
* Spring Security
* EasyExcel (Alibaba Excel export)
* Lombok
* JJWT (JWT token)
* Maven 3.9.x

## Project Structure

```
src/
  main/
    java/com/bryan/system/
      config/         # Configuration classes (security, Redis, MyBatis-Plus, etc.)
      controller/     # RESTful controllers
      domain/         # Entities, request/response objects, VO
      filter/         # JWT authentication filter
      handler/        # MyBatis auto-fill, global exception handler
      mapper/         # MyBatis Mapper interfaces
      service/        # Service layer
      util/           # Utility classes (JWT, HTTP, etc.)
    resources/
      application.yaml
      application-dev.yaml
      mapper/         # MyBatis mapper xmls
      sql/            # Database schema scripts
  test/
    java/com/bryan/system/
      UserSystemApplicationTests.java
```

## Requirements

* JDK 17+
* Maven 3.9.9+
* PostgreSQL 17.x
* Redis 6.x or above

## Configuration

* Update database and Redis settings in `src/main/resources/application-dev.yaml`.
* General settings (logging, MyBatis-Plus logic delete, etc.) are in `src/main/resources/application.yaml`.
* Database schema scripts are in [`src/main/resources/sql/create_table.sql`](src/main/resources/sql/create_table.sql).

## Getting Started

1. Initialize the PostgreSQL database by running the schema script:

   ```sh
   psql -U postgres -d postgres -f src/main/resources/sql/create_table.sql
   ```
2. Start the Redis service.
3. Build and run the project with Maven:

   ```sh
   ./mvnw spring-boot:run
   ```

   Or run the packaged jar:

   ```sh
   mvn clean package
   java -jar target/user-system-0.0.1-SNAPSHOT.jar
   ```

## 🐳 Containerized Deployment (Docker)

This project supports containerized deployment with **Docker** and **Docker Compose**.

### 1. Build the Project

Make sure you have **Docker** and **Docker Compose** installed, then build the JAR:

```bash
mvn clean package -DskipTests
```

### 2. Create Dockerfile

In the project root, create a file named `Dockerfile`:

```dockerfile
# Use official OpenJDK 17 as base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built jar into container
COPY target/user-system-0.0.1-SNAPSHOT.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3. Create docker-compose.yml

In the project root, create `docker-compose.yml`:

```yaml
version: "3.9"
services:
  postgres:
    image: postgres:17
    container_name: user-postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: user_system
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./src/main/resources/sql/create_table.sql:/docker-entrypoint-initdb.d/create_table.sql
    ports:
      - "5432:5432"

  redis:
    image: redis:6
    container_name: user-redis
    ports:
      - "6379:6379"

  app:
    build: .
    container_name: user-system
    depends_on:
      - postgres
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/user_system
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
    ports:
      - "8080:8080"

volumes:
  postgres_data:
```

### 4. Update Spring Configuration

Edit `src/main/resources/application-dev.yaml` to use container hostnames instead of `localhost`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/user_system
    username: postgres
    password: postgres
  redis:
    host: redis
    port: 6379
```

### 5. Start Services

Run the following command to build and start all services:

```bash
docker-compose up -d --build
```

### 6. Access the Application

* Application API: [http://localhost:8080/api](http://localhost:8080/api)
* PostgreSQL: `localhost:5432` (user: `postgres`, password: `postgres`)
* Redis: `localhost:6379`

## Main APIs

* User registration: `POST /api/auth/register`
* User login: `POST /api/auth/login`
* Get all users: `GET /api/user/all` (admin only)
* Search users: `POST /api/user/search`
* User update, role change, password update, ban/unban, logical delete, etc. are detailed in [`UserController`](src/main/java/com/bryan/system/controller/UserController.java)
* Export user data: `GET /api/user/export/all`, `POST /api/user/export/field` (admin only)

## Notes

* For production, inject JWT secret via configuration file instead of hardcoding.
* Global exception handling is in [`GlobalExceptionHandler`](src/main/java/com/bryan/system/handler/GlobalExceptionHandler.java).
* Logical delete field is `deleted`: 0 means active, 1 means deleted.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
