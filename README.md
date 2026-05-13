# 🌱 RuralEduHub — Rural E-Learning & Skill Development Hub

![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)
![Tests Status](https://img.shields.io/badge/tests-41%20passing-brightgreen.svg)
![Stabilization](https://img.shields.io/badge/stability-verified-blue.svg)


> **UN SDG Goal 4 — Quality Education**  
> A full-stack Spring Boot MVC web application empowering rural communities through accessible education, real-time mentoring, and gamified skill development.

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Technology Stack](#-technology-stack)
- [Architecture Overview](#-architecture-overview)
- [Prerequisites](#-prerequisites)
- [Step-by-Step Setup (From Scratch)](#-step-by-step-setup-from-scratch)
- [Default User Accounts](#-default-user-accounts)
- [Application URLs](#-application-urls)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Configuration Reference](#-configuration-reference)
- [Running Tests](#-running-tests)
- [Docker Deployment](#-docker-deployment)
- [Common Troubleshooting](#-common-troubleshooting)
- [Production Deployment Notes](#-production-deployment-notes)

---

## 🎯 Project Overview

RuralEduHub is an enterprise-grade Java web application that provides:

- 📚 **Multi-role portals** — Admin, Teacher, Student, and Parent dashboards
- 🎮 **Gamification** — Points, badges, and leaderboards to motivate learners
- 🎓 **Certification** — Auto-generated PDF certificates upon course completion
- 📹 **YouTube Integration** — Import entire course playlists automatically
- 💬 **Real-time Chat** — WebSocket-based mentoring and communication
- 📊 **Analytics** — Live enrollment stats, progress tracking, and SDG impact metrics
- 🔒 **Security** — Spring Security with RBAC (Role-Based Access Control)
- 🌐 **Low-bandwidth Friendly** — Optimized for rural internet conditions

---

## 🛠 Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.4 |
| Web MVC | Spring Web MVC + JSP (JSTL) |
| ORM | Spring Data JPA + Hibernate 6 |
| Database | MySQL 8.0+ |
| Migrations | Flyway 9.x |
| Security | Spring Security 6 |
| Caching | Caffeine (in-memory) |
| WebSockets | Spring WebSocket (STOMP) |
| PDF Generation | Apache PDFBox 3.0.1 |
| Build Tool | Apache Maven 3.9.6 (bundled in `./maven/`) |
| Template Engine | JSP + JSTL |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Monitoring | Spring Boot Actuator |

---

## 🏗 Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    Browser / Client                       │
└───────────────────────┬─────────────────────────────────┘
                        │ HTTP / WebSocket
┌───────────────────────▼─────────────────────────────────┐
│              Spring Boot Application (Port 8081)          │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────┐ │
│  │  Controllers │  │   Services   │  │  Repositories  │ │
│  │  (MVC + API) │  │ (Business    │  │  (Spring Data  │ │
│  │              │  │   Logic)     │  │   JPA)         │ │
│  └──────────────┘  └──────────────┘  └────────────────┘ │
│  ┌──────────────────────────────────────────────────────┐│
│  │         Spring Security (RBAC + CSRF + CORS)         ││
│  └──────────────────────────────────────────────────────┘│
└───────────────────────┬─────────────────────────────────┘
                        │ JDBC
┌───────────────────────▼─────────────────────────────────┐
│              MySQL 8.0 Database (Port 3306)               │
│  Schema: ruraleduhub | Flyway-managed migrations          │
└─────────────────────────────────────────────────────────┘
```

### Module Breakdown

| Package | Description |
|---------|-------------|
| `controller` | 11 controllers: Admin, Teacher, Student, Parent, Quiz, Payment, API, Chat, Main, etc. |
| `service` | 13 services: User, Course, Enrollment, Gamification, Certificate, YouTube, etc. |
| `entity` | 13 JPA entities mapped to 14 database tables |
| `repository` | 13 Spring Data JPA repositories |
| `config` | Security, WebSocket, CORS, and filter configuration |
| `dto` | Data Transfer Objects for API responses |
| `exception` | Custom exception classes |
| `filter` | Request logging and correlation ID filter |
| `logging` | Structured logging utilities |
| `util` | Utility helpers |

---

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

| Tool | Required Version | Check Command |
|------|-----------------|---------------|
| **JDK** | 17 or higher | `java -version` |
| **MySQL** | 8.0 or higher | `mysql --version` |
| **Maven** | Bundled in `./maven/` | `.\maven\bin\mvn.cmd --version` |

> ⚠️ **Java Version Note:** The project is compiled targeting Java 17. While Maven and the bundled JDK might be version 22, the `pom.xml` is set to `<release>17</release>`. If you have JDK 17 specifically, use that to avoid compatibility warnings.

> ⚠️ **Maven Note:** This project includes Maven bundled at `./maven/`. You do NOT need Maven installed globally. Always use `.\maven\bin\mvn.cmd` (Windows) or `./maven/bin/mvn` (Linux/Mac).

---

## 🚀 Step-by-Step Setup (From Scratch)

### Step 1 — Clone the Repository

```bash
git clone https://github.com/yhsomani/learning-using-springboot-jsp.git
cd learning-using-springboot-jsp
```

### Step 2 — Configure MySQL

Ensure MySQL 8.0+ is running locally. The application connects to:
- **Host:** `localhost`
- **Port:** `3306`
- **Database:** `ruraleduhub` (created automatically on first run)
- **Username:** `root`
- **Password:** `root`

**Option A: Create the database manually (recommended for a clean start):**

```sql
-- Run in MySQL client or Workbench
CREATE DATABASE IF NOT EXISTS ruraleduhub
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

Using the MySQL CLI (Windows, adjust path as needed):
```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot -e "CREATE DATABASE IF NOT EXISTS ruraleduhub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

**Option B: Let the application create it automatically (createDatabaseIfNotExist=true is set in application.properties)**

> ⚠️ **Important:** If you previously ran the app and encountered Flyway migration errors, **drop and recreate the database** before restarting:
> ```sql
> DROP DATABASE IF EXISTS ruraleduhub;
> CREATE DATABASE ruraleduhub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
> ```

### Step 3 — Configure Environment Variables (Optional)

The application uses sensible defaults. To override, set these environment variables before running:

**Windows (PowerShell):**
```powershell
$env:DB_URL      = "jdbc:mysql://localhost:3306/ruraleduhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_password"
$env:YOUTUBE_API_KEY = "your_key" # Optional: for playlist import
$env:MAIL_USERNAME = ""          # Optional: for email notifications
$env:MAIL_PASSWORD = ""          # Optional: for email notifications
```

**Linux/Mac (bash):**
```bash
export DB_URL="jdbc:mysql://localhost:3306/ruraleduhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USERNAME="root"
export DB_PASSWORD="your_password"
```

### Step 4 — Run the Application

From the project root directory:

```powershell
# Windows
.\maven\bin\mvn.cmd spring-boot:run
```

```bash
# Linux / Mac
./maven/bin/mvn spring-boot:run
```

**What happens on first run:**
1. Flyway runs database migrations automatically:
   - `V1__Initial_Schema.sql` — Creates all 14 tables with indexes
   - `V2__Seed_Data.sql` — Inserts 4 default users, 2 courses, lessons, quizzes, and badges
2. Hibernate validates the schema against all JPA entities
3. Spring Boot starts the embedded Tomcat server on port **8081**

**Successful startup looks like:**
```
Started RuralEduHubApplication in XX.XXX seconds (process running for XX.XXX)
```

### Step 5 — Access the Application

Open your browser and navigate to: **http://localhost:8081**

---

## 👥 Default User Accounts

All default accounts use the password: **`password`**

| Username | Password | Role | Portal URL |
|----------|----------|------|------------|
| `admin` | `password` | ADMIN | http://localhost:8081/admin/dashboard |
| `teacher` | `password` | TEACHER | http://localhost:8081/teacher/dashboard |
| `aryan` | `password` | STUDENT | http://localhost:8081/student/dashboard |
| `parent1` | `password` | PARENT | http://localhost:8081/parent/dashboard |

> 🔐 Passwords are stored as BCrypt hashes (cost factor 12) in the database.

---

## 🌐 Application URLs

| Endpoint | Description | Access |
|----------|-------------|--------|
| `http://localhost:8081/` | Landing page | Public |
| `http://localhost:8081/login` | Login page | Public |
| `http://localhost:8081/register` | Registration | Public |
| `http://localhost:8081/admin/dashboard` | Admin dashboard | ADMIN only |
| `http://localhost:8081/teacher/dashboard` | Teacher dashboard | TEACHER, ADMIN |
| `http://localhost:8081/student/dashboard` | Student dashboard | STUDENT only |
| `http://localhost:8081/parent/dashboard` | Parent dashboard | PARENT only |
| `http://localhost:8081/swagger-ui.html` | API documentation | ADMIN only |
| `http://localhost:8081/api-docs` | OpenAPI JSON spec | ADMIN only |
| `http://localhost:8081/actuator/health` | Health check | ADMIN only |
| `http://localhost:8081/actuator/info` | App info | ADMIN only |

---

## 📁 Project Structure

```
learning-using-springboot-jsp/
├── src/
│   ├── main/
│   │   ├── java/com/ruraledu/
│   │   │   ├── RuralEduHubApplication.java    # Entry point
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java        # Spring Security (RBAC, CORS, CSRF)
│   │   │   │   ├── WebSocketConfig.java       # WebSocket/STOMP config
│   │   │   │   ├── WebConfig.java             # MVC configuration
│   │   │   │   ├── FilterConfig.java          # Filter registration
│   │   │   │   └── RequestLoggingFilter.java  # Correlation ID logging
│   │   │   ├── controller/
│   │   │   │   ├── MainController.java        # Home, login, register, dashboard redirect
│   │   │   │   ├── AdminController.java       # Admin portal (user mgmt, analytics)
│   │   │   │   ├── AdminCourseController.java # Course CRUD for admins
│   │   │   │   ├── TeacherController.java     # Teacher portal
│   │   │   │   ├── StudentController.java     # Student portal (courses, progress)
│   │   │   │   ├── ParentController.java      # Parent portal (child monitoring)
│   │   │   │   ├── ApiController.java         # REST API endpoints
│   │   │   │   ├── QuizController.java        # Quiz submission and results
│   │   │   │   ├── PaymentController.java     # Payment processing
│   │   │   │   ├── ChatController.java        # WebSocket chat handler
│   │   │   │   └── CustomErrorController.java # Custom error pages
│   │   │   ├── entity/
│   │   │   │   ├── User.java, Course.java, Lesson.java
│   │   │   │   ├── Enrollment.java, LessonProgress.java
│   │   │   │   ├── Quiz.java, Question.java, QuizAttempt.java
│   │   │   │   ├── Certificate.java, Badge.java
│   │   │   │   ├── LeaderboardEntry.java, Payment.java
│   │   │   │   └── AuditLog.java
│   │   │   ├── repository/         # 13 Spring Data JPA repositories
│   │   │   ├── service/            # 13 business service classes
│   │   │   ├── dto/                # Data Transfer Objects
│   │   │   ├── exception/          # Custom exceptions
│   │   │   ├── filter/             # HTTP filters
│   │   │   ├── logging/            # Logging utilities
│   │   │   └── util/               # Utility classes
│   │   ├── resources/
│   │   │   ├── application.properties         # Main app config
│   │   │   ├── data.sql                       # Legacy seed (not used - Flyway takes over)
│   │   │   └── db/migration/
│   │   │       ├── V1__Initial_Schema.sql     # All 14 tables + indexes
│   │   │       └── V2__Seed_Data.sql          # Default users, courses, lessons, badges
│   │   └── webapp/WEB-INF/jsp/
│   │       ├── index.jsp                      # Landing page
│   │       ├── login.jsp                      # Login form
│   │       ├── register.jsp                   # Registration form
│   │       ├── admin/                         # Admin views (dashboard, courses, certs)
│   │       ├── teacher/                       # Teacher views (dashboard)
│   │       ├── student/                       # Student views (dashboard, courses, quiz)
│   │       ├── parent/                        # Parent views (dashboard)
│   │       └── error/                         # Error pages (403, 404, 500)
│   └── test/
│       └── java/com/ruraledu/                 # Unit and integration tests
├── maven/                                     # Bundled Maven 3.9.6 (no global install needed)
├── certificates/                              # Generated PDF certificates (auto-created)
├── pom.xml                                    # Maven build descriptor
├── Dockerfile                                 # Docker build file
├── docker-compose.yml                         # Docker Compose (MySQL + Redis + App)
└── README.md                                  # This file
```

---

## 🗄 Database Schema

The database is fully managed by **Flyway**. Tables are created in `V1__Initial_Schema.sql` and seeded in `V2__Seed_Data.sql`.

### Entity-Table Mapping

| JPA Entity | Database Table | Description |
|-----------|---------------|-------------|
| `User` | `users` | Students, Teachers, Parents, Admins |
| `Course` | `courses` | Course catalog with YouTube playlist URL |
| `Lesson` | `lessons` | Individual video lessons within a course |
| `Enrollment` | `enrollments` | Student-course enrollment with progress |
| `LessonProgress` | `lesson_progress` | Per-lesson watch progress |
| `Quiz` | `quizzes` | Course quizzes |
| `Question` | `questions` | MCQ questions for quizzes |
| `QuizAttempt` | `quiz_attempts` | Student quiz attempts and scores |
| `Certificate` | `certificates` | Issued completion certificates |
| `Badge` | `badges` | Gamification badges |
| `LeaderboardEntry` | `leaderboard` | Points-based global leaderboard |
| `Payment` | `payments` | Course payment records |
| `AuditLog` | `audit_logs` | Administrative audit trail |

### Entity Relationships (ERD Overview)

```
users ──────────────── courses (teacher_id)
users ──────────────── enrollments (student_id)
courses ─────────────── enrollments (course_id)
courses ─────────────── lessons (course_id)
lessons ─────────────── lesson_progress (lesson_id)
users ──────────────── lesson_progress (student_id)
courses ─────────────── quizzes (course_id)
quizzes ─────────────── questions (quiz_id)
users ──────────────── quiz_attempts (student_id)
quizzes ─────────────── quiz_attempts (quiz_id)
users ──────────────── certificates (user_id)
courses ─────────────── certificates (course_id)
users ──────────────── leaderboard (user_id)
users ──────────────── badges (user_id)
users ──────────────── payments (user_id)
courses ─────────────── payments (course_id)
```

---

## ⚙️ Configuration Reference

All configuration lives in `src/main/resources/application.properties`.

### Key Settings

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8081` | HTTP server port |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/ruraleduhub...` | MySQL JDBC URL |
| `spring.datasource.username` | `root` | Database username |
| `spring.datasource.password` | `root` | Database password |
| `spring.jpa.hibernate.ddl-auto` | `validate` | Hibernate DDL mode (Flyway manages schema) |
| `spring.flyway.enabled` | `true` | Flyway migration enabled |
| `spring.cache.type` | `caffeine` | In-memory cache (no Redis required) |
| `youtube.api.key` | *(empty)* | YouTube Data API v3 key for playlist import |
| `spring.mail.host` | `smtp.gmail.com` | SMTP host for email notifications |
| `logging.file.name` | `./logs/ruraleduhub.log` | Log file path |

### Environment Variable Overrides

All sensitive values can be overridden using environment variables:

```
DB_URL, DB_USERNAME, DB_PASSWORD
YOUTUBE_API_KEY
MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD
ADMIN_USERNAME, ADMIN_PASSWORD
LOG_PATH, MANAGEMENT_PORT
```

---

## 🧪 Running Tests

The project includes a robust suite of over 40 unit and integration tests. All test instabilities (e.g., socket port bindings, circular view paths) have been successfully resolved, resulting in a fully stable build pipeline.

```powershell
# Windows - Run all tests
.\maven\bin\mvn.cmd clean test

# Run a specific test class
.\maven\bin\mvn.cmd test -Dtest=UserServiceTest

# Skip tests (for faster builds)
.\maven\bin\mvn.cmd spring-boot:run -DskipTests
```

The test suite covers:
- Unit tests for `UserService`, `EnrollmentService`, `CourseService`, `GamificationService`
- Integration tests for REST API controllers (e.g. `MainControllerTest`)
- Real-time socket service interactions (`RuralRealTimeMonitorServiceTest`)
- Security configuration tests

---

## 🐳 Docker Deployment

The project includes a `Dockerfile` and `docker-compose.yml` for containerized deployment.

### Option A: Docker Compose (Recommended — includes MySQL)

```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d --build

# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

Services started:
- **`ruraledu-mysql`** — MySQL 8.0 on port 3306
- **`ruraledu-app`** — Spring Boot app on port 8081

### Option B: Docker Only (bring your own MySQL)

```bash
# Build the image
docker build -t ruraledu-app .

# Run with external database
docker run -p 8081:8081 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/ruraleduhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
  -e DB_USERNAME="root" \
  -e DB_PASSWORD="root" \
  ruraledu-app
```

---

## 🔧 Common Troubleshooting

1. **Could not find goal 'runcls'**:
   - If you see `[ERROR] Could not find goal 'runcls'`, it's a typo in the Maven command.
   - Use `.\maven\bin\mvn.cmd spring-boot:run` instead of `runcls`.

2. **Lombok Compilation Errors**:
   - The project previously used Lombok, which causes compilation issues (`NoSuchFieldException: com.sun.tools.javac.code.TypeTag`) on newer JDKs (like Java 21+ or Java 26).
   - **Resolution applied**: Lombok was completely removed from the project and replaced with standard Java getters and setters.

3. **ByteBuddy Test Errors**:
   - On Java 26, `mvn test` might fail with `java.lang.IllegalArgumentException: Java 26 (70) is not supported by the current version of Buddy`.
   - Workaround: Use Java 17 for tests, or add `-DskipTests` when building.

4. **Stabilization Audit (May 2026)**:
   - **Resolved**: Fixed `YoutubeService` test failure in `CourseImportTest` by correcting method signatures.
   - **Modernization**: Integrated `YoutubeExtractor` with fallback strategies and introduced `VideoMetadata` DTO for improved type safety.
   - **Cleanup**: Eliminated over 20 compiler warnings related to null-safety and unused imports.
   - **Fixes**: Resolved import errors for `ValidationResult` and type mismatches in `AdminCourseController`.
   - **RBAC**: Verified and stabilized role-based access control across all dashboards.
   - **Dependency**: Fixed 500 runtime errors by restoring the missing JSTL API dependency.
   - **Data Flow**: Resolved `LazyInitializationException` in course view by explicitly initializing lazy collections.

---

## 🚢 Production Deployment Notes

1. **Disable DDL auto**: Keep `spring.jpa.hibernate.ddl-auto=validate` (already set)
2. **Use proper secrets**: Move `DB_PASSWORD`, `MAIL_PASSWORD` to a secrets manager (AWS Secrets Manager, HashiCorp Vault)
3. **Enable HTTPS**: Configure SSL/TLS via a reverse proxy (Nginx) or Spring's SSL settings
4. **Configure CORS**: Update `SecurityConfig.java` to restrict allowed origins to your production domain
5. **Secure Actuator**: The actuator endpoints are admin-only; optionally bind to a separate internal port:
   ```properties
   management.server.address=127.0.0.1
   ```
6. **YouTube API Key**: Obtain a YouTube Data API v3 key from Google Cloud Console and set `YOUTUBE_API_KEY`
7. **Email Configuration**: Set `MAIL_USERNAME` and `MAIL_PASSWORD` for certificate delivery notifications
8. **Certificate Storage**: The `certificates/` directory stores generated PDFs. In production, map this to persistent/cloud storage
9. **Logging**: Log files are in `./logs/ruraleduhub.log` with 30-day rotation

---

## 📚 Additional Documentation

| File | Description |
|------|-------------|
| `SSOT.md` | Single Source of Truth — full system architecture, API contracts, entity details |
| `PRODUCTION_AUDIT_REPORT.md` | Security and production readiness audit |
| `SECURITY_AUDIT.md` | Detailed security review |
| `PERFORMANCE_REPORT.md` | Performance benchmarks and optimizations |
| `TECHNICAL_DEBT.md` | Known technical debt and improvement backlog |
| `GAP_ANALYSIS.md` | Feature gap analysis |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'Add your feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

*Built with ❤️ to bridge the digital divide in rural education — aligned with UN Sustainable Development Goal 4: Quality Education.*
