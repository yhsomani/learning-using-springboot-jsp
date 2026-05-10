# RuralEduHub - Rural E-Learning & Skill Development Hub

RuralEduHub is a full-stack, enterprise-grade Java web application designed to empower rural communities through accessible quality education, real-time mentoring, and skill development. The platform is engineered for low-bandwidth environments and features robust gamification, certification, and offline-sync capabilities.

## UN Sustainable Development Goal (SDG) Alignment
**Goal 4: Quality Education**
This project directly addresses SDG 4 by providing an accessible, low-bandwidth-friendly platform for rural students to access educational resources, earn certifications, and interact with mentors. The application measures real-time impact via enrollment counts, completion rates, and gamified progress points.

---

## 🏗 Architecture Overview

RuralEduHub follows a monolithic **Spring Boot MVC** architecture backed by a relational **MySQL** database.

### Core Stack
- **Backend:** Java 17, Spring Boot 3.2.4 (Spring Web, Spring Data JPA, Spring Security)
- **Database:** MySQL (InnoDB, UTF8MB4)
- **Frontend:** JSP with JSTL (jakarta.tags.core), Bootstrap 5, Vanilla CSS
- **Authentication:** Spring Security with JWT token-based auth and Role-Based Access Control (RBAC).
- **Tooling:** Maven, JUnit

### Database Architecture
The system is fully normalized and relational, featuring 10 primary entities:
- `users`: Core identity management (Students, Teachers, Parents, Admins).
- `courses` & `lessons`: Curriculum structure with YouTube API integration.
- `enrollments` & `lesson_progress`: Fine-grained student tracking and analytics.
- `quizzes`, `questions`, & `quiz_attempts`: Built-in assessment engine.
- `certificates`, `badges`, & `leaderboard`: Automated gamification and credentials.

*For full schema details, entity relationships, and architectural documentation, refer to `SSOT.md`.*

---

## 🚀 Setup & Installation

### Prerequisites
- **JDK 17** or higher
- **Maven** (bundled via `./mvnw`)
- **MySQL 8.0+** running locally or remotely

### 1. Database Configuration
By default, the application expects a MySQL database named `ruraleduhub`. 
You can override the default connection properties by setting the following environment variables:

```bash
export DB_URL="jdbc:mysql://localhost:3306/ruraleduhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USERNAME="root"
export DB_PASSWORD="root"
```

### 2. Initialization & Seeding
The application is configured to automatically generate the database schema and seed it with initial data upon startup.
- **Schema Generation:** `spring.jpa.hibernate.ddl-auto=update`
- **Data Seeding:** Driven by `src/main/resources/data.sql` (configured via `spring.sql.init.mode=always`).

The seed file includes default accounts. The passwords are encrypted using verified BCrypt hashes.
- **Admin:** `admin` / `password`
- **Teacher:** `teacher` / `password`
- **Student:** `aryan` / `password`

### 3. Running the Application
From the root of the project, execute the Maven Spring Boot plugin:

```bash
# Windows
.\maven\bin\mvn.cmd spring-boot:run

# Mac/Linux (if using mvnw)
./mvnw spring-boot:run
```
*The server will start on `http://localhost:8081`.*

---

## 🧪 Testing Workflow

The application includes a suite of unit and integration tests covering core services and API controllers.

Run the test suite using Maven:

```bash
.\maven\bin\mvn.cmd clean test
```

### Verification Checklist
- [x] **No Hardcoded Logic:** All dashboards, analytics, and metrics are fully dynamic and fetched from the MySQL persistence layer.
- [x] **Role Access:** Users are restricted to their specific portals (Admin, Teacher, Student, Parent) via Spring Security configuration.
- [x] **Database Constraints:** Relationships (e.g., preventing orphaned enrollments when courses are deleted) are protected by Foreign Key constraints.

---

## 🔧 Development & Advanced Configuration

### Zero-Config Course Import
Teachers and Admins can import entire course modules automatically via the **Advanced YouTube Scraper**.
1. Navigate to the Teacher Dashboard.
2. Provide a valid YouTube Playlist URL.
3. The platform automatically extracts metadata, thumbnails, and video ordering to create lessons.

*Note: For high-volume or production usage, configure a dedicated API key by setting the `YOUTUBE_API_KEY` environment variable.*

### Deployment Instructions (Production)
For production deployments:
1. Set `spring.jpa.hibernate.ddl-auto=validate` to prevent accidental schema drops.
2. Disable automatic seeding: `spring.sql.init.mode=never`.
3. Provide robust database credentials via secrets management.
4. Ensure the `certificates/` directory has proper write permissions or map it to cloud storage (e.g., AWS S3).

---

## 📚 Project Documentation

- **Single Source of Truth (`SSOT.md`):** Complete system architecture, API contracts, entity mappings, and technical debt reports.
- **`data.sql`:** Relational seed data for local development."# learning-using-springboot-jsp" 
