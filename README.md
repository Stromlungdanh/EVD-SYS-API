# EVD SYS API

Spring Boot REST API for SYS user administration and EVD document management.

## Tech stack

- Java 21, Spring Boot 3
- PostgreSQL 17 with Docker Compose
- Spring Data JPA: user/document CRUD, document pagination/filter/search/sort
- Spring JDBC (`JdbcTemplate`): document count-by-status report
- Spring Security, BCrypt password hashing, JWT Bearer authentication
- Flyway database migrations

## Run locally

Prerequisites: Docker Desktop and Java 21. Maven is run through Docker in the command below, so a local Maven installation is not required.

Start PostgreSQL:

```cmd
docker compose up -d
docker compose ps
```

The PostgreSQL container must show `healthy`.

Start the API from a second CMD window in the project root:

```cmd
docker run --rm --name evd-sys-api -p 8081:8080 --network evd-sys-api_default -e DB_URL=jdbc:postgresql://postgres:5432/evd_sys -e DB_USERNAME=evd_user -e DB_PASSWORD=evd_password -v "%cd%:/workspace" -w /workspace maven:3.9.11-eclipse-temurin-21 mvn -q spring-boot:run
```

The API is available at `http://localhost:8081`. Stop the API with `Ctrl+C`. Stop PostgreSQL with `docker compose down`.

## Sample accounts

| Username | Password | Role |
|---|---|---|
| `admin` | `password` | `ADMIN` |
| `staff` | `password` | `STAFF` |

## Authentication

`POST /api/auth/login`

```json
{
  "username": "admin",
  "password": "password"
}
```

The response contains `accessToken`. Send it on all protected requests:

```text
Authorization: Bearer <accessToken>
```

## API summary

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/login` | Public |
| POST | `/api/users` | ADMIN |
| GET | `/api/users` | ADMIN |
| GET | `/api/users/{id}` | ADMIN |
| PUT | `/api/users/{id}` | ADMIN |
| DELETE | `/api/users/{id}` | ADMIN |
| POST | `/api/documents` | ADMIN, STAFF |
| GET | `/api/documents/{id}` | ADMIN, document owner |
| PUT | `/api/documents/{id}` | ADMIN, document owner |
| DELETE | `/api/documents/{id}` | ADMIN only |
| POST | `/api/documents/{id}/file` | ADMIN, document owner |
| GET | `/api/documents` | ADMIN, STAFF |
| GET | `/api/documents/reports/count-by-status` | ADMIN |

### Document list

```text
GET /api/documents?page=0&size=10&status=DRAFT&category=HR&keyword=contract&sort=createdAt,desc
```

- `page`: zero-based; default `0`
- `size`: 1–100; default `10`
- `status`, `category`, `keyword`: optional filters
- `keyword`: case-insensitive search on title or code
- `sort`: one of `id`, `code`, `title`, `category`, `status`, `createdAt`, `updatedAt`, followed by `asc` or `desc`

## Authorization rules

- **ADMIN**: user management; full document CRUD; report access.
- **STAFF**: create documents; only list/view/update documents they created; cannot delete documents; no access to user management or reports.

Ownership is enforced in the service layer, not only at the controller/UI layer.

## Database migrations

Flyway migrations are under `src/main/resources/db/migration` and run automatically on startup. They create the schema and seed the sample users.

## Error handling

The API returns consistent JSON errors for validation failures, missing resources, duplicate usernames/document codes, forbidden operations, and invalid credentials.

## Bonus status

- File upload/attachment: implemented with `multipart/form-data`, part name `file`; uploaded files are stored in the configured `uploads/` folder and the generated file name is saved on the document.
- Audit log: not implemented.
- Unit tests: not implemented.
