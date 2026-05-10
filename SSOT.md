# SSOT — RuralEduHub E-Learning Platform

Last updated: 2026-05-10

---

## 1. Project Identity

- **Name:** RuralEduHub
- **Type:** Monolithic Spring Boot WAR application
- **Java:** 17
- **Spring Boot:** 3.2.4
- **Build:** Maven (pom.xml at project root)
- **Artifact:** `RuralEduHub-0.0.1-SNAPSHOT.war`
- **Alignment:** UN SDG 4 — Quality Education

---

## 2. Tech Stack

| Category         | Technology                          |
|------------------|-------------------------------------|
| Framework        | Spring Boot 3.2.4                   |
| Language         | Java 17                             |
| View Engine      | JSP with JSTL (jakarta.tags.core)   |
| Database         | MySQL (InnoDB, UTF8MB4)             |
| Migration        | Flyway                              |
| Caching          | Redis 7.0                           |
| Security         | Spring Security + JWT               |
| PDF Generation   | Apache PDFBox                       |
| WebSocket        | Spring WebSocket support            |
| Raw Socket       | Custom `SocketMonitor` on port 9090 |
| Testing          | JUnit, Spring Boot Test             |
| CI               | GitHub Actions (maven.yml)          |
| Containerization | Docker, Docker Compose              |
| Frontend styling | CSS (global.css), Bootstrap 5       |

---

## 3. System Architecture & Relational Data Flow

The project follows a standard layered monolithic architecture using Spring MVC:
- **Presentation Layer**: JSP templates integrated with Spring MVC Controllers.
- **Service Layer**: Handles core business logic, including gamification, real-time sync, and SDG metric calculations.
- **Persistence Layer**: Spring Data JPA repositories interfacing with a normalized MySQL database.

### Entities & Relationships (Database Schema)

| Entity           | Key fields                                                                                 | Relationships & Constraints |
|------------------|--------------------------------------------------------------------------------------------|-----------------------------|
| User             | id, username (UNIQUE), email (UNIQUE), password, role, points, location, bio, language, enabled, parent_id | FK parent_id -> User(id) |
| Course           | id, title, description, category, difficulty, thumbnail, youtube_playlist_url, teacher_id  | FK teacher_id -> User(id) |
| Lesson           | id, course_id, video_id, title, thumbnail, duration, order_index                           | FK course_id -> Course(id), UNIQUE(course_id, video_id) |
| Enrollment       | id, student_id, course_id, enrollment_date, progress, completed, completion_date           | FK student_id -> User(id), FK course_id -> Course(id) |
| LessonProgress   | id, student_id, lesson_id, completed, watched_duration, last_watched_at                    | FK student_id -> User(id), FK lesson_id -> Lesson(id), UNIQUE(student_id, lesson_id) |
| Quiz             | id, title, course_id                                                                       | FK course_id -> Course(id) |
| Question         | id, content, option_a, option_b, option_c, option_d, correct_answer, quiz_id               | FK quiz_id -> Quiz(id) |
| QuizAttempt      | id, attempt_date, score, quiz_id, student_id                                               | FK quiz_id -> Quiz(id), FK student_id -> User(id) |
| Payment          | id, user_id, course_id, amount, transaction_id, status, payment_date                       | FK user_id -> User(id), FK course_id -> Course(id) |
| Certificate      | id, user_id, course_id, certificate_url, issued_date                                       | FK user_id -> User(id), FK course_id -> Course(id) |
| Badge            | id, name, description, icon_url, user_id                                                   | FK user_id -> User(id) |
| LeaderboardEntry | id, user_id, total_points, rank                                                            | FK user_id -> User(id) |

---

## 4. Frontend Structure (JSP Views)

Located under `src/main/webapp/WEB-INF/jsp/`:

| View                        | Purpose                                 |
|-----------------------------|-----------------------------------------|
| index.jsp                   | Landing page (Dynamic stats via MainController) |
| login.jsp                   | Login form (Spring Security managed)    |
| register.jsp                | Registration form                       |
| admin/dashboard.jsp         | Admin command center (SDG metrics, users, pagination, bulk actions) |
| parent/dashboard.jsp        | Parent portal (Manage/monitor children) |
| student/dashboard.jsp       | Student home (Progress, recommendations, new arrivals) |
| student/courses.jsp         | Course discovery catalog (Monetization ready) |
| student/course_view.jsp     | Deep-dive into specific enrolled course |
| student/quiz.jsp            | Assessment interface                    |
| student/checkout.jsp        | Secure course purchase interface         |
| teacher/dashboard.jsp       | Teacher portal (Course import, metrics) |
| error/404.jsp               | Custom Page Not Found                   |
| error/403.jsp               | Custom Access Denied                    |

---

## 5. Application Configuration & Environment

Environment variables handle credential injection:

| Variable            | Description                            | Default in properties |
|---------------------|----------------------------------------|-----------------------|
| `DB_URL`            | MySQL connection string                | `jdbc:mysql://localhost:3306/ruraleduhub` |
| `DB_USERNAME`       | MySQL Username                         | `root` |
| `DB_PASSWORD`       | MySQL Password                         | `root` |
| `REDIS_HOST`        | Redis server host                      | `localhost` |
| `REDIS_PORT`        | Redis server port                      | `6379` |
| `YOUTUBE_API_KEY`   | API Key for CourseImportService        | `YOUR_API_KEY_HERE` |

### Initialization
- Schema management is handled via **Flyway** (`V1__init_schema.sql`).
- Initial data seed is performed via `data.sql` (configured with `spring.sql.init.mode=always`).

---

## 6. Role System & Workflows

| Role    | Scope & Capabilities                                            |
|---------|-----------------------------------------------------------------|
| ADMIN   | User Governance (CRUD, Bulk Delete, Export), Analytics, Monitoring. |
| TEACHER | Course Engineering (Import, Metadata), Student Analytics.        |
| STUDENT | Monetized Enrollment, Interactive Lessons, Gamification, Certs. |
| PARENT  | Guardian Oversight, Real-time Progress Sync.                   |

---

## 7. Migration & Stabilization (COMPLETED)

- **Database:** Standardized on production-grade MySQL with Flyway migration support.
- **Performance:** Implemented Redis caching, JPA EntityGraphs (N+1 fix), and Result-Set Pagination.
- **Monetization:** Integrated a functional Payment subsystem and secure Checkout flow.
- **UX/UI:** Resolved interaction blockers, corrupted JSP tags, and added professional error pages.

---

## 8. Technical Debt & Future Roadmap (PROPOSED)

- **PROPOSED:** Transition to a fully decoupled architecture with a React/Next.js frontend.
- **PROPOSED:** Integrate a real-world Payment Gateway SDK (Stripe/Razorpay) to replace simulated logic.
- **PROPOSED:** Add comprehensive E2E testing using Selenium or Playwright.
