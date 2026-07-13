# EVD SYS API

This is a Java Spring Boot Backend Technical Test for the EVD document management module and SYS user management.

## Tech stack

* Java 21, Spring Boot 3
* PostgreSQL 17 with Docker Compose
* Spring Data JPA: user/document CRUD, document pagination/filter/search/sort
* Spring Security, BCrypt password hashing, JWT Bearer authentication
* Flyway database migrations

## Run the source code

Requirements: Docker Desktop and Java 21.

Start PostgreSQL:

```cmd
docker compose up -d
docker compose ps
```

Open a terminal in the project directory:

```cmd
docker run --rm --name evd-sys-api -p 8081:8080 --network evd-sys-api_default -e DB_URL=jdbc:postgresql://postgres:5432/evd_sys -e DB_USERNAME=evd_user -e DB_PASSWORD=evd_password -v "%cd%:/workspace" -w /workspace maven:3.9.11-eclipse-temurin-21 mvn -q spring-boot:run
```

## Accounts

* admin/password -> role: ADMIN
* staff/password -> role: STAFF

## Authentication

`POST /api/auth/login`

## 1. Introduction

This is a Java Spring Boot Backend Technical Test for the EVD document management module and SYS user management.

Main features:

* Login using a JWT Access Token.
* ADMIN and STAFF authorization.
* Document management.
* User management.
* Document pagination, searching, filtering, and sorting.
* Statistics of the number of documents by status.
* Validation and centralized exception handling.

---

## 2. Technologies used

* Java 21.
* Spring Boot.
* Spring Security.
* JWT Access Token.
* Spring JDBC.
* PostgreSQL.
* Flyway Migration.
* Docker Compose.
* Maven.

---

## 3. Authorization

### ADMIN

ADMIN has permission to:

* View all documents.
* Create, update, and delete documents.
* Manage users.
* View document statistics.

### STAFF

STAFF has permission to:

* Create documents.
* View documents created by themselves.
* Update documents created by themselves.

STAFF is not allowed to:

* View or update documents created by other users.
* Delete documents.
* Access user management functions.

---

## 4. Project structure

```text
src/main/java/com/lotte/evdsys
├── auth        # Login and JWT
├── security    # Spring Security and JWT Filter
├── document    # Document management
├── user        # User management
├── common      # Shared exceptions and responses
└── EvdSysApiApplication.java
```

Processing architecture:

```text
Controller
→ Service
→ Repository
→ PostgreSQL
```

---

## 5. Database and Flyway

The database uses PostgreSQL.

Tables and sample data are automatically created by Flyway when the application starts.

Migrations are located at:

```text
src/main/resources/db/migration
```

## 6. Login

### ADMIN

```http
POST /api/auth/login
Content-Type: application/json
```

Request:

```json
{
  "username": "admin",
  "password": "password"
}
```

The response returns a JWT Access Token:

```json
{
  "accessToken": "eyJ..."
}
```

Use the token in protected APIs:

```http
Authorization: Bearer <access-token>
```

The project currently only uses a JWT Access Token and has not implemented a Refresh Token.

---

## 7. Document API

### Create a document

```http
POST /api/documents
```

Sample request:

```json
{
  "code": "DOC-001",
  "title": "User Guide",
  "description": "System user guide document",
  "category": "GUIDE",
  "status": "DRAFT"
}
```

### Document list

```http
GET /api/documents
```

Supports:

* Pagination.
* Filtering by status and category.
* Searching by code or title.
* Data sorting.

Example:

```http
GET /api/documents?page=0&size=10&status=DRAFT&keyword=DOC
```

### Document details

```http
GET /api/documents/{id}
```

### Update a document

```http
PUT /api/documents/{id}
```

### Delete a document

```http
DELETE /api/documents/{id}
```

Only ADMIN is allowed to delete documents.

---

## 10. Statistics

The system has a query to count documents by status using SQL `GROUP BY`.

Example result:

```json
[
  {
    "status": "DRAFT",
    "count": 5
  },
  {
    "status": "APPROVED",
    "count": 2
  }
]
```

---

## 11. Validation and exception handling

The project uses Jakarta Validation and `GlobalExceptionHandler`.

Main HTTP Status codes:

* | 400 | Invalid request data |
* | 401 | Not authenticated or invalid token |
* | 403 | No access permission |
* | 404 | Data not found |
* | 409 | Duplicate username or document code |
* | 500 | System error |

---

## 12. Testing with Postman

* Extract the `EVD-SYS-API-test-kit` file.
* Run PostgreSQL and the source code.
* Import `EVD-SYS-API.postman_collection.json`.
* Import `EVD-SYS-Local.postman_environment.json`.
* Select the `EVD SYS - Local` environment.
* Run the folders in order from 00 to 07.
