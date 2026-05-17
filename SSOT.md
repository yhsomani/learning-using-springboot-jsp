# SSOT — RuralEduHub E-Learning Platform

> **Status:** Production-Ready | **Stack:** Java 17 + Spring Boot 3.2.4 + MySQL 8.0 + JSP/JSTL + Caffeine + Docker
> **Last Audit:** 2026-05-17 | **SDG Alignment:** UN SDG 4 — Quality Education

---

## 1. PROJECT OVERVIEW

RuralEduHub is a multi-role educational portal for rural communities in India. Core features:
- YouTube playlist ingestion as structured courses (no API key required)
- Role-based portals: Student, Teacher, Parent, Admin
- Gamification: points, leaderboard, badges
- PDF certificate generation on course completion
- Parent monitoring of linked children
- Real-time mentor chat (WebSocket/STOMP)
- TCP socket monitor for rural center heartbeats

### User Roles
| Role | URL Prefix | Key Capabilities |
|------|-----------|-----------------|
| STUDENT | /student/** | Enroll, watch lessons, take quizzes, earn certs |
| TEACHER | /teacher/** | Create/manage own YouTube-backed courses |
| PARENT | /parent/** | Link children, monitor progress |
| ADMIN | /admin/** | Full platform: users, courses, audit logs, CSV export |

---

## 2. TECHNOLOGY STACK

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.4 |
| Build | Maven (bundled `./maven/`) | 3.x |
| Database | MySQL | 8.0 |
| Migrations | Flyway | Managed |
| Caching | Caffeine (in-process) | Latest |
| View | JSP/JSTL (Jakarta EE) | — |
| Security | Spring Security + BCrypt(cost 12) | — |
| WebSocket | STOMP over SockJS | — |
| PDF | Apache PDFBox | 3.x |
| HTML Parsing | Jsoup | Latest |
| JSON | Jackson | Latest |
| Container | Docker + Docker Compose | Latest |
| OpenAPI | SpringDoc | — |
| Mail | Spring Mail (JavaMailSender) | — |

---

## 3. PROJECT STRUCTURE

```
ELEARNING/
  maven/                        # Bundled Maven — always use ./maven/bin/mvn.cmd
  src/main/java/com/ruraledu/
    config/                     # SecurityConfig, WebSocketConfig, AsyncConfig
    controller/                 # MainController, StudentController, TeacherController,
                                #   ParentController, AdminController, ApiController
    dto/                        # PlaylistImportRequest, CourseImportResponse,
                                #   PointsRequest, QuizSubmissionRequest, VideoMetadata
    entity/                     # User, Course, Lesson, Enrollment, LessonProgress,
                                #   Quiz, Question, QuizAttempt, Certificate,
                                #   LeaderboardEntry, Badge, AuditLog, Payment
    exception/                  # GlobalExceptionHandler, CourseNotFoundException,
                                #   UserAlreadyExistsException
    repository/                 # Spring Data JPA: UserRepository, CourseRepository,
                                #   LessonRepository, EnrollmentRepository,
                                #   LessonProgressRepository, CertificateRepository,
                                #   LeaderboardRepository, AuditLogRepository
    service/                    # UserService, CourseService, EnrollmentService,
                                #   GamificationService, CertificateService,
                                #   NotificationService, YoutubeService,
                                #   ProjectSdgService, AuditService,
                                #   RuralRealTimeMonitorService
      extractor/                # YoutubeExtractor (3-strategy fallback engine)
  src/main/resources/
    application.properties      # Master config
    db/migration/               # Flyway: V1–V6 SQL files
    static/                     # CSS, JS, images
  src/main/webapp/WEB-INF/jsp/  # JSP views by role
  src/test/java/com/ruraledu/   # 40+ unit + integration tests
  Dockerfile                    # Multi-stage: maven builder → JRE 17 slim
  docker-compose.yml            # MySQL 8.0 + Spring Boot app
  pom.xml                       # Dependencies
```

---

## 4. DATABASE SCHEMA

### Flyway Migration History
| File | Description |
|------|-------------|
| V1__Initial_Schema.sql | 12 tables + all indexes |
| V2__Seed_Data.sql | Default admin user + sample courses |
| V3__Add_Youtube_Playlist_Id.sql | `youtube_playlist_id` column on `courses` |
| V4__Add_Missing_Columns.sql | Additional nullable columns |
| V5__More_Seed_Courses.sql | Extended seed courses |
| V6__Coding_Seed_Courses.sql | Coding category seed data |

### Table Definitions

```sql
-- users
id BIGINT PK | username VARCHAR(50) UNIQUE | password VARCHAR(255)
full_name VARCHAR(255) | email VARCHAR(255) UNIQUE
role ENUM('STUDENT','TEACHER','PARENT','ADMIN')
points INT DEFAULT 0 | location | bio TEXT | language DEFAULT 'English'
enabled BOOLEAN DEFAULT TRUE | deleted BOOLEAN DEFAULT FALSE
parent_id BIGINT FK→users(id) | created_at | updated_at

-- courses
id | title | description TEXT | category VARCHAR(100) | difficulty
thumbnail VARCHAR(500) | youtube_playlist_url | youtube_playlist_id
teacher_id FK→users | deleted BOOLEAN | created_at | updated_at

-- lessons
id | course_id FK→courses | video_id VARCHAR(50) | title | thumbnail
duration VARCHAR(20) | order_index INT | created_at

-- enrollments  [UNIQUE: student_id + course_id]
id | student_id FK→users | course_id FK→courses
enrollment_date | progress INT DEFAULT 0 | completed BOOLEAN
completion_date | last_watched_lesson_id

-- lesson_progress  [UNIQUE: student_id + lesson_id]
id | student_id FK→users | lesson_id FK→lessons
completed BOOLEAN | watched_duration INT | last_watched_at

-- quizzes
id | title | course_id FK→courses | created_at

-- questions
id | content TEXT | option_a/b/c/d VARCHAR(500) | correct_answer | quiz_id FK

-- quiz_attempts
id | student_id FK→users | quiz_id FK→quizzes | score INT | attempt_date

-- certificates
id | user_id FK→users | course_id FK→courses | certificate_url | issued_date

-- leaderboard  [UNIQUE: user_id]
id | user_id FK→users | total_points INT | rank INT

-- badges
id | name | description TEXT | icon_url | user_id FK→users

-- audit_logs
id | action | performed_by | target_entity | target_id | details TEXT | timestamp

-- payments
id | user_id FK→users | course_id FK→courses | amount DOUBLE
transaction_id | status DEFAULT 'PENDING' | payment_date
```

### Performance Indexes
```sql
idx_users_role, idx_users_username
idx_courses_category, idx_courses_teacher
idx_lessons_course
idx_enrollments_student, idx_enrollments_course
idx_lesson_progress_student
idx_quiz_course, idx_questions_quiz
idx_certificates_user
idx_leaderboard_points DESC
```

---

## 5. CONFIGURATION

### application.properties (key settings)
| Property | Value / Default |
|----------|----------------|
| server.port | 8081 |
| spring.datasource.url | `${DB_URL:jdbc:mysql://localhost:3306/ruraleduhub...}` |
| spring.datasource.username | `${DB_USERNAME:root}` |
| spring.datasource.password | `${DB_PASSWORD:root}` |
| spring.jpa.hibernate.ddl-auto | validate (Flyway controls DDL) |
| spring.flyway.enabled | true |
| spring.cache.type | caffeine |
| spring.cache.caffeine.spec | maximumSize=500,expireAfterWrite=600s |
| spring.mail.host | `${MAIL_HOST:smtp.gmail.com}` |
| spring.mail.username | `${MAIL_USERNAME:}` |
| spring.mail.password | `${MAIL_PASSWORD:}` |
| management.endpoints.web.exposure.include | health,info |
| spring.servlet.multipart.max-file-size | 10MB |
| monitor.port | 9090 |
| logging.level.com.ruraledu | DEBUG |
| spring.jpa.open-in-view | false |

### Environment Variables (Production Override)
| Variable | Purpose |
|----------|---------|
| DB_URL | Full JDBC connection string |
| DB_USERNAME | Database user |
| DB_PASSWORD | Database password |
| MAIL_HOST | SMTP host |
| MAIL_USERNAME | SMTP account |
| MAIL_PASSWORD | SMTP password |
| MANAGEMENT_PORT | Actuator port (default 8081) |
| LOG_PATH | Log file directory |

---

## 6. SECURITY

### Authentication
- Spring Security form-login (session-based for MVC views)
- Custom `UserDetailsService` — loads by username from `users` table
- Password: BCrypt, cost 12

### Authorization Rules (SecurityConfig)
```
PUBLIC:           /, /login, /register, /css/**, /js/**, /images/**
ROLE_STUDENT:     /student/**
ROLE_TEACHER:     /teacher/**
ROLE_PARENT:      /parent/**
ROLE_ADMIN:       /admin/**, /actuator/**
AUTHENTICATED:    /api/**
```

### Registration Security Guards (UserService)
1. Username uniqueness enforced
2. Email uniqueness enforced
3. ADMIN role blocked from self-registration
4. BCrypt encoding applied before persistence
5. Welcome email dispatched asynchronously

### Known Issues
- **SEC-01 (HIGH):** CSRF configuration may have overly broad exemptions — see §14

---

## 7. CONTROLLER & ROUTE MAP

### MainController
| Method | URL | Auth | Description |
|--------|-----|------|-------------|
| GET | / | Public | Landing page with platform stats |
| GET | /login | Public | Login form |
| GET | /register | Public | Registration form |
| POST | /register | Public | Create account → redirect /login |
| GET | /main/dashboard | Auth | Role dispatcher → role-specific dashboard |

### StudentController (/student)
| Method | URL | Description |
|--------|-----|-------------|
| GET | /student/dashboard | Enrollments, recommendations, leaderboard, certs |
| GET | /student/courses | Browse catalogue (optional ?search=) |
| GET | /student/enroll/{id} | Enroll in course → redirect dashboard |
| GET | /student/course/{id} | View course + lesson progress |
| GET | /student/course/{id}/quiz | Take quiz (enrollment + quiz required) |

### TeacherController (/teacher)
| Method | URL | Description |
|--------|-----|-------------|
| GET | /teacher/dashboard | Own courses, total students, avg progress |

### ParentController (/parent) `@PreAuthorize("hasRole('PARENT')")`
| Method | URL | Description |
|--------|-----|-------------|
| GET | /parent/dashboard | Children stats |
| POST | /parent/add-child | Link child by username |
| POST | /parent/remove-child | Unlink child by ID |

### AdminController (/admin) `@PreAuthorize("hasRole('ADMIN')")`
| Method | URL | Description |
|--------|-----|-------------|
| GET | /admin/dashboard | Users (paged), stats, SDG metrics, audit log |
| GET | /admin/users/{id} | JSON user object |
| POST | /admin/users/{id}/toggle-status | Enable/disable account |
| POST | /admin/users/{id}/update | Update name/email/role |
| DELETE | /admin/users/{id} | Soft-delete |
| POST | /admin/users/bulk-delete | Bulk soft-delete |
| GET | /admin/users/export | Streaming CSV export |
| GET | /admin/courses | All courses |
| GET | /admin/certificates | Certificate management |

### ApiController (/api)
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/lessons/{id}/complete | Mark lesson done, +10 pts |
| POST | /api/courses/{id}/complete | Force 100% completion |
| GET | /api/courses/{id}/progress | Get progress % |
| POST | /api/gamification/points | Add points |
| GET | /api/leaderboard | Top 10 |
| POST | /api/quiz/{courseId}/submit | Submit answers → score |
| GET | /api/certificates/{courseId} | Download PDF |
| POST | /api/youtube/import | Import playlist as course |
| GET | /api/youtube/validate | Validate YouTube URL |
| POST | /api/youtube/sync/{courseId} | Sync existing course |

---

## 8. SERVICE LAYER

### YoutubeExtractor — 3-Strategy Fallback Engine
```
Strategy 1 (Primary):
  HTTP GET youtube.com/playlist?list={id}
  → parse ytInitialData JSON → playlistVideoListRenderer

Strategy 2 (Jsoup fallback):
  Jsoup.connect() → select ytd-playlist-video-renderer elements
  → extract video IDs and titles from HTML

Strategy 3 (Invidious fallback):
  Tries: inv.tux.pizza → invidious.io.lol → yewtu.be
  GET /api/v1/playlists/{id} → JSON video array
```
- No YouTube Data API key required
- Skips private/deleted videos automatically
- Randomised User-Agent rotation (3 agents)
- `validateYoutubeUrl()` — classifies as playlist or video, extracts ID
- `extractSingleVideo()` — single video metadata with OG tag fallback
- `isDuplicateVideo()` — prevents duplicate lesson creation

### EnrollmentService — Progress Flow
```
updateLessonProgress(studentId, lessonId, completed=true)
  → save LessonProgress
  → if first completion: student.points += 10
  → recalculate overall progress = (completedLessons / totalLessons) * 100
  → updateProgress(studentId, courseId, overallProgress%)
      → if progress >= 100 and not already completed:
          → e.setCompleted(true), e.setCompletionDate(now)
          → certificateService.generateCertificate() [Async]
          → notificationService.sendCourseCompletionEmail() [Async]
```

### GamificationService — Leaderboard
```
addPoints(userId, points) — transactional increment
updateWeeklyLeaderboard() — @Scheduled(cron="0 0 0 * * SUN")
  → deleteAll leaderboard entries
  → fetch top 10 students by points
  → rebuild with rank 1-10
```

### CertificateService — PDF Generation
```
generateCertificate(student, course) — @Async @Transactional
  → skip if certificate already exists (idempotent)
  → create PDFBox document (A4 page)
  → write: "CERTIFICATE OF COMPLETION", student name, course title
  → save to: certificates/certificate_{studentId}_{courseId}.pdf
  → persist Certificate entity with relative path
```

### RuralRealTimeMonitorService — TCP Socket Server
```
@PostConstruct: opens ServerSocket on port 9090 (configurable)
Thread pool: 5 threads (Executors.newFixedThreadPool)
Per client: reads heartbeat lines → responds "ACK: Heartbeat received from {msg}"
@PreDestroy: closes socket, shuts down executor
Purpose: Satisfies academic CO4 (Socket Programming requirement)
```

---

## 9. CACHING STRATEGY

| Cache | Key Pattern | TTL | Evicted By |
|-------|------------|-----|-----------|
| `courses` | `'all'` | 600s | `saveCourse()`, `deleteCourse()` |
| `certificates` | `{studentId}_{courseId}` | 600s | Manual (no auto-eviction) |

- Backend: Caffeine (in-process, no Redis dependency)
- Max 500 entries total

---

## 10. ASYNC & SCHEDULING

| Operation | Mechanism | Pool |
|-----------|----------|------|
| Certificate PDF generation | `@Async` | execution (core=5, max=10) |
| Welcome email | `@Async` | execution |
| Course completion email | `@Async` | execution |
| Weekly leaderboard rebuild | `@Scheduled` (Sun 00:00) | scheduling (size=2) |

---

## 11. EXCEPTION HANDLING

`GlobalExceptionHandler` (`@ControllerAdvice`) auto-routes:
- API requests (`/api/**` or `Accept: application/json`) → JSON `{ "message": "..." }`
- MVC requests → JSP error page

| Exception | Status | View / Response |
|-----------|--------|----------------|
| CourseNotFoundException | 404 | error/404.jsp |
| UserAlreadyExistsException | 400 | register.jsp with error |
| AccessDeniedException | 403 | error/403.jsp |
| Exception (catch-all) | 500 | error/404.jsp (no 500.jsp — **DEBT SEC-02**) |

---

## 12. WEBSOCKET

| Property | Value |
|----------|-------|
| Endpoint | `/ws-mentor-chat` (SockJS) |
| Message broker prefix | `/topic` |
| App destination prefix | `/app` |
| Purpose | Real-time mentor chat |

---

## 13. DTOs

| DTO | Key Fields |
|-----|-----------|
| `PlaylistImportRequest` | playlistUrl, courseTitle, category, difficulty |
| `CourseImportResponse` | success, message, courseId, lessonCount |
| `PointsRequest` | userId, points |
| `QuizSubmissionRequest` | courseId, answers (Map<Long,String>) |
| `VideoMetadata` | videoId, title, thumbnail, duration, orderIndex, available, deleted, private, ageRestricted, errorMessage |

---

## 14. TECHNICAL DEBT REGISTER

| ID | Severity | Area | Issue | Recommended Fix |
|----|---------|------|-------|----------------|
| **SEC-01** | 🔴 HIGH | Security | CSRF may have blanket API exemptions conflicting with form-login | Audit SecurityConfig; apply `CsrfTokenRequestAttributeHandler` selectively |
| **PERF-01** | 🔴 HIGH | Database | `CourseRepository.findAll()` triggers N+1 for teacher association | Add `@EntityGraph(attributePaths={"teacher","lessons"})` to `findAll()` |
| **OBS-01** | 🟡 MEDIUM | Observability | MDC correlation IDs not propagated through `@Async` tasks | Implement `MDCTaskDecorator` on `ThreadPoolTaskExecutor` |
| **SEC-02** | 🟡 MEDIUM | Error Handling | 500 errors render 404 page (no `error/500.jsp`) | Create `src/main/webapp/WEB-INF/jsp/error/500.jsp` |
| **ARCH-01** | 🟢 LOW | Architecture | System alerts hardcoded in `AdminController` | Move to config or DB-driven alerts table |
| **CERT-01** | 🟢 LOW | Files | Certificate PDFs stored as relative paths — breaks in containers | Use absolute path via property or migrate to object storage |
| **TEST-01** | 🟢 LOW | Testing | No E2E browser tests; 40+ unit/integration tests only | Add Selenium/Playwright suite for critical user workflows |

---

## 15. BUILD & DEPLOYMENT

### Commands
```bash
# Development (Windows)
.\maven\bin\mvn.cmd spring-boot:run

# Development (Unix)
./maven/bin/mvn spring-boot:run

# Full build
.\maven\bin\mvn.cmd clean package -DskipTests

# Run tests
.\maven\bin\mvn.cmd test

# Docker
docker-compose up --build

# Docker (detached)
docker-compose up -d db && .\maven\bin\mvn.cmd spring-boot:run
```

### Access Points
| URL | Description |
|-----|-------------|
| http://localhost:8081 | Application |
| http://localhost:8081/swagger-ui.html | OpenAPI UI |
| http://localhost:8081/api-docs | OpenAPI JSON |
| http://localhost:8081/actuator/health | Health (Admin only) |
| TCP port 9090 | Rural monitor socket |

---

## 16. REBUILD FROM SCRATCH

### Prerequisites
- Java 17 JDK
- Docker + Docker Compose (for MySQL)
- Git

### Steps
```bash
# 1. Clone
git clone <repo-url> && cd ELEARNING

# 2. Start MySQL container
docker-compose up -d db

# 3. Run app (Flyway auto-runs V1-V6 migrations)
.\maven\bin\mvn.cmd spring-boot:run

# 4. Verify
curl http://localhost:8081/actuator/health
```

### Production Hardening Checklist
- [ ] Set `DB_PASSWORD` env var (not default `root`)
- [ ] Set `MAIL_USERNAME` + `MAIL_PASSWORD` for real email
- [ ] Uncomment `management.server.address=127.0.0.1` in application.properties
- [ ] Fix **CERT-01**: configure absolute path for certificate PDFs
- [ ] Fix **SEC-01**: audit and tighten CSRF configuration
- [ ] Create `error/500.jsp` (**SEC-02**)
- [ ] Add `@EntityGraph` to `CourseRepository.findAll()` (**PERF-01**)
- [ ] Implement `MDCTaskDecorator` for correlation IDs (**OBS-01**)

---

## 17. SDG IMPACT METRICS (ProjectSdgService)

Aligns with **UN SDG 4 — Quality Education**.

Metrics exposed on all dashboards:
| Metric | Calculation |
|--------|------------|
| studentsImpacted | Total enrollments |
| certificationsCompleted | Total certificates issued |
| completionRate | (certificates / enrollments) × 100 |
| inProgressRate | 100 - completionRate |
| impactScore | (enrollments × 10) + (certificates × 50) |

---

## 18. JSP VIEW INVENTORY

```
/WEB-INF/jsp/
  index.jsp              Landing page (stats counters)
  login.jsp              Login form
  register.jsp           Registration form
  admin/
    dashboard.jsp        User table (paged), SDG metrics, audit log, system alerts
    courses.jsp          Course management
    certificates.jsp     Certificate admin
  student/
    dashboard.jsp        Enrollments, recommendations, leaderboard, SDG
    courses.jsp          Browse + search catalogue
    course_view.jsp      Lesson player, progress tracker
    quiz.jsp             MCQ quiz interface
  teacher/
    dashboard.jsp        Course list, student count, avg progress
  parent/
    dashboard.jsp        Children cards with enroll stats
  error/
    403.jsp              Access denied
    404.jsp              Not found / fallback 500
```

---

## 19. REPOSITORY CUSTOM QUERIES

### CourseRepository
| Method | Query Purpose |
|--------|--------------|
| `findByCategory` | Category filter, deleted=false |
| `findByYoutubePlaylistUrl` | Duplicate URL prevention |
| `findByYoutubePlaylistId` | Duplicate playlist prevention |
| `findRecommendations` | Same category, not enrolled by student |
| `searchCourses` | LIKE on title + description |
| `findByTeacherId` | Teacher-scoped courses (@EntityGraph) |
| `findNewArrivals` | Latest 5 courses (Pageable) |

### EnrollmentRepository
| Method | Purpose |
|--------|---------|
| `findByStudentId` | Student's enrollments |
| `findByCourseId` | Course's enrollments |
| `findByStudentIdAndCourseId` | Single enrollment lookup |
| `countByCourseId` | Enrollment count for a course |
| `getAverageProgress` | Platform-wide avg progress |

### UserRepository
| Method | Purpose |
|--------|---------|
| `findByUsername` | Auth lookup |
| `findByEmail` | Uniqueness validation |
| `findByParentId` | Children of a parent |
| `findTop5ByOrderByPointsDesc` | Dashboard leaderboard |
| `findTopStudents(Pageable)` | Weekly leaderboard rebuild |
| `findAllActive` | Non-deleted users |

---

## 20. SUPPLEMENTAL DOCUMENTS

| Document | Status | Notes |
|----------|--------|-------|
| `README.md` | Active | Public quick-start reference |
| `PRODUCTION_AUDIT_REPORT.md` | Archived | Findings absorbed into §14 |
| `schema.sql.bak` | Historical | Flyway migrations are authoritative |

---

*SSOT.md — RuralEduHub v1.0 — Generated 2026-05-17*
*All information verified against live source code.*
