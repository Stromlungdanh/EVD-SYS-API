# EVD SYS API

Đây là bài Technical Test Backend Java Spring Boot cho module quản lý tài liệu EVD và quản lý người dùng SYS.

## Tech stack

- Java 21, Spring Boot 3
- PostgreSQL 17 with Docker Compose
- Spring Data JPA: user/document CRUD, document pagination/filter/search/sort
- Spring Security, BCrypt password hashing, JWT Bearer authentication
- Flyway database migrations

## Chạy source

yêu cầu: Docker Desktop and Java 21.

Start PostgreSQL:
```cmd
docker compose up -d
docker compose ps
```

Mở terminal tại thư mục project:

```cmd
docker run --rm --name evd-sys-api -p 8081:8080 --network evd-sys-api_default -e DB_URL=jdbc:postgresql://postgres:5432/evd_sys -e DB_USERNAME=evd_user -e DB_PASSWORD=evd_password -v "%cd%:/workspace" -w /workspace maven:3.9.11-eclipse-temurin-21 mvn -q spring-boot:run
```

## accounts
admin/password -> role: ADMIN
staff/password -> role: STAFF

## Authentication

`POST /api/auth/login`


## 1. Giới thiệu

Đây là bài Technical Test Backend Java Spring Boot cho module quản lý tài liệu EVD và quản lý người dùng SYS.

Các chức năng chính:

- Đăng nhập bằng JWT Access Token.
- Phân quyền ADMIN và STAFF.
- Quản lý tài liệu.
- Quản lý người dùng.
- Phân trang, tìm kiếm, lọc và sắp xếp tài liệu.
- Thống kê số lượng tài liệu theo trạng thái.
- Validation và xử lý lỗi tập trung.
---

## 2. Công nghệ sử dụng

- Java 21.
- Spring Boot.
- Spring Security.
- JWT Access Token.
- Spring JDBC.
- PostgreSQL.
- Flyway Migration.
- Docker Compose.
- Maven.
- JUnit 5 và Mockito.

Project sử dụng Spring JDBC, không sử dụng JPA/Hibernate.

---

## 3. Phân quyền

### ADMIN

ADMIN có quyền:

- Xem toàn bộ tài liệu.
- Tạo, cập nhật và xóa tài liệu.
- Quản lý người dùng.
- Xem thống kê tài liệu.

### STAFF

STAFF có quyền:

- Tạo tài liệu.
- Xem tài liệu do mình tạo.
- Cập nhật tài liệu do mình tạo.

STAFF không được:

- Xem hoặc sửa tài liệu của người khác.
- Xóa tài liệu.
- Truy cập chức năng quản lý người dùng.

---

## 4. Cấu trúc project

```text
src/main/java/com/lotte/evdsys
├── auth        # Đăng nhập và JWT
├── security    # Spring Security và JWT Filter
├── document    # Quản lý tài liệu
├── user        # Quản lý người dùng
├── common      # Exception và response dùng chung
└── EvdSysApiApplication.java
```

Kiến trúc xử lý:

```text
Controller
→ Service
→ Repository
→ PostgreSQL
```

---

## 5. Database và Flyway

Database sử dụng PostgreSQL.

Các bảng và dữ liệu mẫu được tự động tạo bằng Flyway khi ứng dụng khởi động.

Migration nằm tại:

```text
src/main/resources/db/migration
```

## 6. Đăng nhập

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

Response trả về JWT Access Token:

```json
{
  "accessToken": "eyJ..."
}
```

Sử dụng token trong các API được bảo vệ:

```http
Authorization: Bearer <access-token>
```

Project hiện chỉ sử dụng JWT Access Token, chưa triển khai Refresh Token.

---

## 7. API document

### Tạo tài liệu

```http
POST /api/documents
```

Request mẫu:

```json
{
  "code": "DOC-001",
  "title": "Hướng dẫn sử dụng",
  "description": "Tài liệu hướng dẫn hệ thống",
  "category": "GUIDE",
  "status": "DRAFT"
}
```

### Danh sách tài liệu

```http
GET /api/documents
```

Hỗ trợ:

- Phân trang.
- Lọc theo status và category.
- Tìm kiếm theo code hoặc title.
- Sắp xếp dữ liệu.

Ví dụ:

```http
GET /api/documents?page=0&size=10&status=DRAFT&keyword=DOC
```

### Chi tiết tài liệu

```http
GET /api/documents/{id}
```

### Cập nhật tài liệu

```http
PUT /api/documents/{id}
```

### Xóa tài liệu

```http
DELETE /api/documents/{id}
```

Chỉ ADMIN được phép xóa tài liệu.

---

## 10. Thống kê

Hệ thống có truy vấn thống kê số lượng tài liệu theo trạng thái bằng SQL `GROUP BY`.

Ví dụ kết quả:

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

## 11. Validation và xử lý lỗi

Project sử dụng Jakarta Validation và `GlobalExceptionHandler`.

Các HTTP Status chính:

| 400 | Dữ liệu request không hợp lệ |
| 401 | Chưa đăng nhập hoặc token không hợp lệ |
| 403 | Không có quyền truy cập |
| 404 | Không tìm thấy dữ liệu |
| 409 | Trùng username hoặc mã tài liệu |
| 500 | Lỗi hệ thống |

---

## 12. Test bằng Postman

giải nén file  "EVD-SYS-API-test-kit"
Chạy PostgreSQL và source.
Import EVD-SYS-API.postman_collection.json.
Import EVD-SYS-Local.postman_environment.json.
Chọn environment EVD SYS - Local.
Chạy các folder lần lượt từ 00 đến 07.




