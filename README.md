<div align="center">

# 📇 Belajar Spring RESTful API

**A RESTful Contact Management API built with Spring Boot, featuring token-based authentication, layered architecture, request validation, and centralized exception handling.**

[![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/Database-MySQL-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Last Commit](https://img.shields.io/github/last-commit/YogaHarisCode/belajar-spring-restful-api)](https://github.com/YogaHarisCode/belajar-spring-restful-api/commits/main)
[![Issues](https://img.shields.io/github/issues/YogaHarisCode/belajar-spring-restful-api)](https://github.com/YogaHarisCode/belajar-spring-restful-api/issues)

</div>

---

## 📖 Overview

**Belajar Spring RESTful API** is a backend service for managing personal contacts and their associated addresses. Each user registers an account, authenticates to receive an API token, and then manages their own contacts and addresses through a REST API — with every operation scoped to the authenticated user.

The project was built as a hands-on exercise in designing a RESTful API with **Spring Boot 4**, applying core backend engineering practices such as:

- Clean, layered architecture (Controller → Service → Repository)
- Request validation with meaningful, localizable error messages
- Centralized exception handling with consistent response contracts
- Token-based authentication via a custom argument resolver
- Comprehensive MockMvc-driven integration testing

It serves as a **backend Java portfolio project** demonstrating practical Spring Data JPA, Spring MVC, and API design skills.

---

## ✨ Features

- 🔐 **User registration, login, and logout** with token-based session management
- 👤 **User profile retrieval and partial updates**
- 📇 **Full CRUD for contacts**, scoped to the authenticated user
- 🔎 **Contact search** with optional filters (name, email, phone) and pagination
- 🏠 **Full CRUD for addresses**, nested under a contact
- ✅ **Request validation** using Jakarta Bean Validation with custom, localized error messages
- ⚠️ **Centralized exception handling** for validation errors, constraint violations, and HTTP status errors
- 🔑 **Password hashing** using a self-contained BCrypt implementation
- 🧪 **Integration test suite** covering all controllers with MockMvc (62 test cases)

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 25 | Core language |
| Spring Boot | 4.1.0 | Application framework |
| Spring Web MVC | — | REST controllers and routing |
| Spring Data JPA | — | Database access and ORM |
| Spring Validation | — | Request/DTO validation |
| Hibernate | — | JPA provider |
| MySQL Connector/J | — | MySQL database driver |
| Maven | — | Build and dependency management |
| Lombok | — | Boilerplate reduction (getters/setters/builders) |
| JUnit + MockMvc | — | Controller-level integration testing |

> This project does not use Spring Security, JWT, Docker, or an OpenAPI/Swagger layer. Authentication is handled via a custom token header and argument resolver (see [Authentication](#-authentication)).

---

## 🏗️ Architecture

The application follows a classic layered architecture:

```
Controller  →  Service  →  Repository  →  Database
     ↓             ↓
   DTOs        Entities
```

| Layer | Responsibility |
|---|---|
| **Controller** | Exposes REST endpoints, delegates to services, maps request/response DTOs |
| **Service** | Contains business logic; each service has an interface + implementation |
| **Repository** | Spring Data JPA interfaces for database access; `ContactRepository` additionally uses `JpaSpecificationExecutor` for dynamic search queries |
| **Entity** | JPA-mapped domain objects (`User`, `Contact`, `Address`) |
| **Model (DTO)** | Request/response objects decoupled from entities (e.g. `CreateContactRequest`, `ContactResponse`) |
| **Resolver** | `UserArgumentResolver` injects the authenticated `User` directly into controller method parameters |
| **Configuration** | Registers the custom argument resolver with Spring MVC |
| **Controller Advice** | `ErrorController` centralizes exception-to-response mapping |

### Entity Relationships

```
User (username) 1 ── * Contact (id) 1 ── * Address (id)
```

- A `User` owns many `Contact` records.
- A `Contact` belongs to exactly one `User` and owns many `Address` records.

---

## 📁 Project Structure

```
src/main/java/yogaharis/restful/
├── configuration/
│   └── WebConfiguration.java        # Registers custom argument resolver
├── controller/
│   ├── UserController.java          # /api/users endpoints
│   ├── ContactController.java       # /api/contacts endpoints
│   ├── AddressController.java       # /api/contacts/{id}/addresses endpoints
│   └── ErrorController.java         # @RestControllerAdvice — global exception handling
├── entity/
│   ├── User.java
│   ├── Contact.java
│   └── Address.java
├── model/                           # Request/response DTOs
│   ├── RegisterUserRequest.java, LoginUserRequest.java, UpdateUserRequest.java
│   ├── CreateContactRequest.java, UpdateContactRequest.java, SearchContactRequest.java
│   ├── CreateAddressRequest.java, UpdateAddressRequest.java
│   ├── UserResponse.java, TokenResponse.java, ContactResponse.java, AddressResponse.java
│   └── WebResponse.java, PagingResponse.java
├── repository/
│   ├── UserRepository.java
│   ├── ContactRepository.java       # extends JpaSpecificationExecutor for dynamic search
│   └── AddressRepository.java
├── resolver/
│   └── UserArgumentResolver.java    # Resolves `User` from the X-API-TOKEN header
├── security/
│   └── BCrypt.java                  # Self-contained OpenBSD-style bcrypt implementation
└── service/
    ├── UserService.java / UserServiceImpl.java
    ├── ContactService.java / ContactServiceImpl.java
    └── AddressService.java / AddressServiceImpl.java

src/main/resources/
├── application.properties           # Datasource + Hikari + Hibernate configuration
└── messages.properties               # Localized validation error messages

src/test/java/yogaharis/restful/
├── controller/
│   ├── UserControllerTest.java      # 19 test cases
│   ├── ContactControllerTest.java   # 19 test cases
│   └── AddressControllerTest.java   # 24 test cases
└── BelajarSpringResfultApiApplicationTests.java

docs/
├── user.md                          # User Management API specification
├── contact.md                       # Contact Management API specification
└── address.md                       # Address Management API specification
```

---

## 📡 API Documentation

All endpoints return a consistent envelope: `{ "data": ... }` on success and `{ "errors": "..." }` on failure. Endpoints other than register/login require the `X-API-TOKEN` header.

### User Management

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/users` | Register a new user | No |
| `POST` | `/api/users/login` | Authenticate and receive a token | No |
| `GET` | `/api/users/current` | Get the current authenticated user | Yes |
| `PATCH` | `/api/users/current` | Partially update the current user (name and/or password) | Yes |
| `DELETE` | `/api/users/logout` | Invalidate the current session token | Yes |

### Contact Management

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/contacts` | Create a new contact | Yes |
| `GET` | `/api/contacts/{contactId}` | Get a contact by ID | Yes |
| `PUT` | `/api/contacts/{contactId}` | Update a contact | Yes |
| `DELETE` | `/api/contacts/{contactId}` | Delete a contact | Yes |
| `GET` | `/api/contacts` | Search contacts by `name`, `email`, `phone` with `page`/`size` pagination | Yes |

### Address Management

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/contacts/{contactId}/addresses` | Create an address for a contact | Yes |
| `GET` | `/api/contacts/{contactId}/addresses/{addressId}` | Get a specific address | Yes |
| `PUT` | `/api/contacts/{contactId}/addresses/{addressId}` | Update an address | Yes |
| `DELETE` | `/api/contacts/{contactId}/addresses/{addressId}` | Delete an address | Yes |
| `GET` | `/api/contacts/{contactId}/addresses` | List all addresses for a contact | Yes |

Full request/response payload examples for every endpoint are available in [`docs/user.md`](docs/user.md), [`docs/contact.md`](docs/contact.md), and [`docs/address.md`](docs/address.md).

---

## ✅ Validation

Request bodies are validated using **Jakarta Bean Validation** (`@NotBlank`, `@Size`, `@Email`), and path variables are validated with method-level `@Validated` + `@NotBlank`.

- Validation messages are externalized in [`messages.properties`](src/main/resources/messages.properties), allowing error text to stay consistent and easy to change without touching code.
- Example constraints: `firstName` is required (max 100 chars), `email` must be a valid format (max 100 chars), `country` is required for addresses.

When validation fails, the API returns a `400 Bad Request` with all violated field messages joined into a single string:

```json
{
  "errors": "first name cannot blank, Wrong email format"
}
```

---

## ⚠️ Error Handling

All exceptions are handled centrally in `ErrorController` (`@RestControllerAdvice`), which maps three exception types to consistent JSON error responses:

| Exception | HTTP Status | Trigger |
|---|---|---|
| `MethodArgumentNotValidException` | `400 Bad Request` | `@Valid` request body validation failure |
| `ConstraintViolationException` | `400 Bad Request` | `@Validated` path variable / parameter validation failure |
| `ResponseStatusException` | *(varies)* | Explicitly thrown business errors, e.g. `404 Not Found`, `401 Unauthorized` |

Every error response follows the same shape:

```json
{
  "errors": "<message>"
}
```

---

## 🗄️ Database

- **Database:** MySQL
- **Access layer:** Spring Data JPA (Hibernate) with `spring.jpa.properties.hibernate.show_sql` and `format_sql` enabled for local development
- **Connection pooling:** HikariCP, configured with a max pool size of 50 and minimum idle of 10
- **Schema:** managed via JPA entity mappings (`User`, `Contact`, `Address`) — no separate migration tool (e.g. Flyway/Liquibase) is included

---

## 🔑 Authentication

Authentication is **not** implemented with Spring Security or JWT. Instead:

1. A user registers via `POST /api/users` and logs in via `POST /api/users/login`.
2. On successful login, the server generates a random UUID token and stores it on the `User` entity with a 30-day expiration timestamp.
3. Subsequent requests must include the token in the **`X-API-TOKEN`** header.
4. A custom `UserArgumentResolver` (a `HandlerMethodArgumentResolver`) intercepts any controller method parameter of type `User`, looks up the user by token, checks expiration, and injects the resolved `User` — or throws a `401 Unauthorized` `ResponseStatusException` if the token is missing, invalid, or expired.
5. Passwords are hashed using a self-contained, dependency-free **BCrypt** implementation (`security/BCrypt.java`) rather than Spring Security's `PasswordEncoder`.

---

## 🧪 Testing

The project includes an integration test suite (62 test cases total) built with **JUnit** and **MockMvc**, covering every controller:

| Test Class | Test Cases |
|---|---|
| `UserControllerTest` | 19 |
| `ContactControllerTest` | 19 |
| `AddressControllerTest` | 24 |

Tests exercise success paths as well as failure scenarios (missing/invalid tokens, validation errors, not-found resources, and cross-user access attempts).

Run the full test suite with:

```bash
./mvnw test
```

---

## 🚀 Installation

### Prerequisites

- Java 25 (JDK)
- Maven (or use the included `mvnw` wrapper)
- MySQL server running locally

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/YogaHarisCode/belajar-spring-restful-api.git
   cd belajar-spring-restful-api
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE belajar_spring_restful_api;
   ```

3. **Configure the connection** in `src/main/resources/application.properties` (see [Configuration](#-configuration) below).

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Or build a runnable jar**
   ```bash
   ./mvnw clean package
   java -jar target/belajar-spring-resfult-api-0.0.1-SNAPSHOT.jar
   ```

---

## ⚙️ Configuration

Database connection settings live in [`application.properties`](src/main/resources/application.properties):

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/belajar_spring_restful_api
spring.datasource.username=root
spring.datasource.password=1234
spring.datasource.hikari.maximum-pool-size=50
spring.datasource.hikari.minimum-idle=10
```

> ⚠️ The committed values are local development defaults. For any shared or deployed environment, override `spring.datasource.username`/`password` (e.g. via environment variables or a separate untracked properties file) rather than committing real credentials.

---

## 💻 Running the Application

| Method | Command |
|---|---|
| **Maven wrapper** | `./mvnw spring-boot:run` |
| **Packaged jar** | `java -jar target/belajar-spring-resfult-api-0.0.1-SNAPSHOT.jar` |
| **IDE** | Run `BelajarSpringResfultApiApplication.java` directly |

---

## 📬 Example Requests

**Register a user**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username": "johndoe", "password": "password123", "name": "John Doe"}'
```

**Login**
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username": "johndoe", "password": "password123"}'
```

**Create a contact**
```bash
curl -X POST http://localhost:8080/api/contacts \
  -H "X-API-TOKEN: <token>" \
  -H "Content-Type: application/json" \
  -d '{"first_name": "Jane", "last_name": "Doe", "email": "jane@example.com", "phone": "081234567890"}'
```

**Search contacts**
```bash
curl -X GET "http://localhost:8080/api/contacts?name=jane&page=0&size=10" \
  -H "X-API-TOKEN: <token>"
```

---

## 🎓 Learning Outcomes

This project was built to practice and demonstrate:

- Designing and implementing a RESTful API from scratch with Spring Boot
- Layered architecture (Controller / Service / Repository / Entity / DTO)
- Request validation with Jakarta Bean Validation and localized messages
- Centralized exception handling with `@RestControllerAdvice`
- Custom `HandlerMethodArgumentResolver` for token-based authentication
- Dynamic query building with `JpaSpecificationExecutor`
- Writing thorough MockMvc-based integration tests
- Structuring and maintaining API specification documents alongside code

---

## 🔮 Future Improvements

- [ ] Migrate authentication to Spring Security with JWT
- [ ] Add Docker support for containerized deployment
- [ ] Set up CI/CD with GitHub Actions
- [ ] Introduce a schema migration tool (Flyway or Liquibase)
- [ ] Add OpenAPI/Swagger documentation
- [ ] Externalize database credentials via environment variables
- [ ] Reconcile the `docs/*.md` specification files with the actual `X-API-TOKEN` header name

---

## 👤 Author

**Yoga Haris**

- GitHub: [@YogaHarisCode](https://github.com/YogaHarisCode)
- LinkedIn: *add your LinkedIn URL here*
- Email: *add your contact email here*

---

## 📄 License

No license file is currently included in this repository. All rights reserved by the author unless a license is added.
