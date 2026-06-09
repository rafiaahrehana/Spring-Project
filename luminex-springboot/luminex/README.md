# LumiNex — Spring Boot Production Backend

## Tech Stack
- **Java 17** · **Spring Boot 3.2.5** · **Spring Security 6** · **JWT (jjwt 0.11.5)**
- **Spring Data JPA** · **MySQL 8** · **HikariCP** · **MapStruct** · **Lombok**
- **Bean Validation** · **Spring Actuator** · **@EnableJpaAuditing** · **@Async**

---

## Project Structure (78 files · ~1 950 lines)

```
src/main/java/com/saas/luminex/
│
├── LuminexApplication.java
│
├── enums/                          ← All domain enums (STRING in DB)
│   ├── Role.java                   SUPER_ADMIN | ADMIN | EMPLOYEE | CLIENT
│   ├── RequestStatus.java          PENDING | IN_REVIEW | IN_PROGRESS | ON_HOLD | COMPLETED | CANCELLED
│   ├── Priority.java               LOW | NORMAL | HIGH | URGENT
│   ├── PaymentStatus.java          PENDING | PAID | FAILED | REFUNDED
│   ├── PaymentMethod.java          BKASH | NAGAD | ROCKET | CARD | BANK_TRANSFER | CASH
│   ├── PriceType.java              FIXED | HOURLY | MONTHLY
│   └── NotificationType.java       REQUEST_SUBMITTED | ASSIGNED | UPDATED | COMPLETED | PAYMENT_*
│
├── entity/                         ← JPA entities (extend BaseEntity → createdAt/updatedAt)
│   ├── BaseEntity.java
│   ├── User.java                   implements UserDetails
│   ├── Category.java
│   ├── Service.java
│   ├── ServiceRequest.java
│   ├── Payment.java
│   ├── Notification.java
│   ├── AuditLog.java
│   ├── Subscription.java
│   └── KnowledgeBase.java
│
├── repository/                     ← JpaRepository + custom @Query methods
│   └── (9 repositories)
│
├── dto/
│   ├── request/                    LoginRequest, RegisterRequest, UserCreateRequest,
│   │                               ServiceRequestCreateRequest, ServiceRequestUpdateRequest,
│   │                               PaymentRequest, PasswordChangeRequest
│   └── response/                   ApiResponse<T>, AuthResponse, UserResponse,
│                                   ServiceRequestResponse, DashboardStatsResponse
│
├── exception/
│   ├── GlobalExceptionHandler.java ← @RestControllerAdvice — handles all errors uniformly
│   ├── ResourceNotFoundException.java
│   └── BadRequestException.java
│
├── security/
│   ├── JwtUtil.java                ← generate / validate / extract
│   └── JwtAuthenticationFilter.java
│
├── config/
│   ├── SecurityConfig.java         ← Full Spring Security 6 config + CORS
│   ├── AsyncConfig.java            ← @EnableAsync for audit logs
│   └── DataSeeder.java             ← Seeds SUPER_ADMIN + ADMIN on first boot
│
├── service/                        ← Interfaces
│   └── impl/                       ← Implementations
│       ├── AuthServiceImpl.java
│       ├── UserServiceImpl.java
│       ├── UserDetailsServiceImpl.java
│       ├── ServiceRequestServiceImpl.java
│       ├── PaymentServiceImpl.java
│       ├── NotificationServiceImpl.java
│       ├── CategoryServiceImpl.java
│       ├── ServiceManagementServiceImpl.java
│       ├── SubscriptionServiceImpl.java
│       ├── DashboardServiceImpl.java
│       ├── AuditLogService.java    ← @Async — never blocks main flow
│       └── PasswordService.java
│
├── controller/
│   ├── AuthController.java         POST /api/auth/login, /register, /refresh
│   ├── ProfileController.java      GET|PATCH /api/profile, POST /change-password
│   ├── NotificationController.java GET|PATCH /api/notifications/**
│   ├── CatalogueController.java    Public: /api/categories, /services, /subscriptions
│   ├── ClientController.java       /api/client/** — CLIENT role
│   ├── EmployeeController.java     /api/employee/** — EMPLOYEE role
│   ├── AdminController.java        /api/admin/** — ADMIN + SUPER_ADMIN
│   ├── AdminCatalogueController.java /api/admin/categories|services|subscriptions
│   └── SuperAdminController.java   /api/superadmin/** — SUPER_ADMIN only
│
└── util/
    └── SecurityUtil.java           ← getCurrentUser() from SecurityContext
```

---

## Role Permission Matrix

| Endpoint Group              | SUPER_ADMIN | ADMIN | EMPLOYEE | CLIENT |
|-----------------------------|:-----------:|:-----:|:--------:|:------:|
| `POST /api/auth/**`         | ✅          | ✅    | ✅       | ✅     |
| `GET /api/profile`          | ✅          | ✅    | ✅       | ✅     |
| `GET /api/notifications`    | ✅          | ✅    | ✅       | ✅     |
| `GET /api/services`         | ✅          | ✅    | ✅       | ✅     |
| `/api/client/**`            | ❌          | ❌    | ❌       | ✅     |
| `/api/employee/**`          | ❌          | ❌    | ✅       | ❌     |
| `/api/admin/**`             | ✅          | ✅    | ❌       | ❌     |
| `/api/superadmin/**`        | ✅          | ❌    | ❌       | ❌     |

---

## API Reference

### Auth  (`/api/auth`)
| Method | Path         | Body                    | Response        |
|--------|--------------|-------------------------|-----------------|
| POST   | `/login`     | `LoginRequest`          | `AuthResponse`  |
| POST   | `/register`  | `RegisterRequest`       | `AuthResponse`  |
| POST   | `/refresh`   | `?refreshToken=...`     | `AuthResponse`  |

### Profile  (`/api/profile`)  — all authenticated
| Method | Path                | Notes                        |
|--------|---------------------|------------------------------|
| GET    | `/`                 | Own profile                  |
| PATCH  | `/`                 | Update name/phone/address/image |
| POST   | `/change-password`  | `PasswordChangeRequest`      |

### Notifications  (`/api/notifications`)  — all authenticated
| Method | Path                    |
|--------|-------------------------|
| GET    | `/`                     |
| GET    | `/unread-count`         |
| PATCH  | `/mark-all-read`        |
| PATCH  | `/{id}/read`            |

### Public Catalogue  (`/api`)  — no auth
| Method | Path                            |
|--------|---------------------------------|
| GET    | `/categories`                   |
| GET    | `/services?search=`             |
| GET    | `/services/category/{id}`       |
| GET    | `/subscriptions`                |

### Client  (`/api/client`)  — ROLE_CLIENT
| Method | Path             | Notes                            |
|--------|------------------|----------------------------------|
| GET    | `/dashboard`     | Client stats                     |
| GET    | `/requests`      | Paginated own requests           |
| GET    | `/requests/{id}` |                                  |
| POST   | `/requests`      | `ServiceRequestCreateRequest`    |
| GET    | `/payments`      | Own payments                     |
| POST   | `/payments`      | `PaymentRequest`                 |

### Employee  (`/api/employee`)  — ROLE_EMPLOYEE
| Method | Path                  | Notes                              |
|--------|-----------------------|------------------------------------|
| GET    | `/dashboard`          |                                    |
| GET    | `/tasks`              | Assigned tasks (paginated)         |
| GET    | `/tasks/{id}`         |                                    |
| PATCH  | `/tasks/{id}`         | Update progress/status/workedHours |
| GET    | `/knowledge-base`     |                                    |
| GET    | `/knowledge-base/{id}`|                                    |

### Admin  (`/api/admin`)  — ROLE_ADMIN or ROLE_SUPER_ADMIN
| Method | Path                          | Notes                        |
|--------|-------------------------------|------------------------------|
| GET    | `/dashboard`                  | Full platform stats          |
| GET    | `/clients`                    | Paginated                    |
| GET    | `/employees`                  | Paginated                    |
| POST   | `/clients`                    | `UserCreateRequest`          |
| POST   | `/employees`                  | `UserCreateRequest`          |
| GET    | `/users/{id}`                 |                              |
| PATCH  | `/users/{id}`                 | Partial update               |
| PATCH  | `/users/{id}/toggle-active`   |                              |
| DELETE | `/users/{id}`                 |                              |
| GET    | `/requests`                   | All requests, paginated      |
| GET    | `/requests/{id}`              |                              |
| PATCH  | `/requests/{id}`              | Assign employee, set status  |
| DELETE | `/requests/{id}`              |                              |
| GET    | `/payments`                   |                              |
| GET    | `/payments/revenue`           | Total revenue figure         |
| PATCH  | `/payments/{id}/status`       | `?status=PAID`               |
| GET    | `/audit-logs`                 | Paginated                    |
| GET/POST/PUT/DELETE | `/categories`    |                              |
| GET/POST/PUT/DELETE | `/services`      |                              |
| GET/POST/PUT/DELETE | `/subscriptions` |                              |
| GET/POST/PUT/DELETE | `/knowledge-base`|                              |

### SuperAdmin  (`/api/superadmin`)  — ROLE_SUPER_ADMIN only
| Method | Path                          | Notes                         |
|--------|-------------------------------|-------------------------------|
| GET    | `/admins`                     | All admins                    |
| GET    | `/admins/{id}`                |                               |
| POST   | `/admins`                     | Create new admin              |
| PATCH  | `/admins/{id}`                |                               |
| PATCH  | `/admins/{id}/toggle-active`  |                               |
| DELETE | `/admins/{id}`                |                               |
| GET    | `/users/{id}`                 | Any user on the platform      |
| DELETE | `/users/{id}`                 | Hard delete any user          |

---

## All API responses use `ApiResponse<T>`
```json
{
  "success": true,
  "message": "Login successful",
  "data": { ... },
  "timestamp": "2025-06-07T10:00:00"
}
```

---

## Getting Started

### 1. Create MySQL Database
```sql
CREATE DATABASE luminex_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configure Environment Variables (or edit application.properties)
```
DB_USERNAME=root
DB_PASSWORD=yourpassword
JWT_SECRET=LumiNexSuperSecretKeyMustBe256BitsLongForHMACSHA256Algorithm
```

### 3. Run
```bash
./mvnw spring-boot:run
```

### 4. Default seeded credentials
| Role        | Email                    | Password       |
|-------------|--------------------------|----------------|
| SUPER_ADMIN | superadmin@luminex.com   | superadmin123  |
| ADMIN       | admin@luminex.com        | admin123       |

### 5. Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"superadmin@luminex.com","password":"superadmin123"}'
```

---

## Production Patterns Used

| Pattern | Where |
|---|---|
| `@Enumerated(EnumType.STRING)` | Every status/role field — human-readable DB values |
| `BaseEntity` + `@EnableJpaAuditing` | Auto `createdAt`/`updatedAt` on all entities |
| `ApiResponse<T>` wrapper | Consistent JSON shape on all endpoints |
| `@PreAuthorize("hasRole(...)")` | Method-level security per controller |
| `@Async` audit logging | Never blocks business logic on audit writes |
| `@Transactional(readOnly=true)` | All read-only queries — performance optimised |
| DTO → Entity separation | Entities never exposed directly; mapped to response DTOs |
| `CommandLineRunner` data seeder | One-time seed guard with `existsByEmail` check |
| `GlobalExceptionHandler` | Centralised error handling; no try/catch in controllers |
| HikariCP pool config | `maximum-pool-size=20`, `minimum-idle=5` |
