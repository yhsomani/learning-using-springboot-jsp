# Missing Feature Implementation Plan

## 1. Overview
This execution workflow dictates the roadmap for the next major release (v1.1) of RuralEduHub. It focuses on closing functional gaps, resolving technical debt, and implementing advanced features.

## 2. Phase 1: Infrastructure & Reliability (Weeks 1-2)
- **Task 1.1:** Integrate **Flyway** for database migrations. Remove `ddl-auto=update`. Move `schema.sql` to `V1__init.sql`.
- **Task 2.1:** Implement **Spring Boot Actuator** for health checks (`/actuator/health`).
- **Task 3.1:** Containerize the application. Create a `Dockerfile` and `docker-compose.yml` bundling Spring Boot, MySQL, and Redis.

## 3. Phase 2: Core Feature Completion (Weeks 3-4)
- **Task 2.1 (Email):** Build `NotificationService.java`. Fire async events on user registration and course completion to dispatch real emails via SMTP.
- **Task 2.2 (Payments):** Implement the Stripe/Razorpay SDK. Convert `PaymentController` to handle checkout sessions and webhooks for course unlock features.
- **Task 2.3 (Reporting):** Refactor `LegacyReportService` to use Spring Data JPA Projections instead of raw JDBC, standardizing data access.

## 4. Phase 3: Performance & Scalability (Weeks 5-6)
- **Task 3.1 (Caching):** Configure Redis. Annotate high-traffic methods (e.g., `CourseService.getAllCourses`) with `@Cacheable`.
- **Task 3.2 (Pagination):** Refactor `UserRepository` and `CourseRepository` methods to accept `Pageable` parameters to prevent massive memory loads when users/courses scale > 10,000.
- **Task 3.3 (Query Optimization):** Perform explain-plan analysis on the SDG metrics queries. Add compound indexes on `(course_id, student_id)` to `enrollments` and `lesson_progress`.

## 5. Phase 4: Frontend Decoupling (Future Roadmap)
- **Task 4.1:** Map out all JSP views to REST JSON contracts.
- **Task 4.2:** Build a standalone Next.js frontend consuming the `/api/*` endpoints.
- **Task 4.3:** Deprecate and remove the `src/main/webapp/WEB-INF/jsp` directory entirely.