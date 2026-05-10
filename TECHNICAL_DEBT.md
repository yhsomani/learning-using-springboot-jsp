# Technical Debt Report

## 1. Definition
This document tracks existing technical debt, architectural shortcuts, and areas requiring future refactoring within RuralEduHub.

## 2. Addressed Technical Debt
- **Disconnected Views:** All `href="#"` placeholders and unlinked buttons were mapped to their correct routing endpoints.
- **Hardcoded State:** Hardcoded metrics (e.g., SDG percentages, scholar counts) were eliminated and replaced with dynamic model attributes populated by the Service layer.
- **Database Standardization:** Eliminated the dual dependency on H2 and Postgres in `pom.xml`. Standardized strictly on MySQL.
- **JSP Standards:** Fixed missing JSTL imports (`jakarta.tags.core`) that caused raw tag rendering on several pages.

## 3. Outstanding Technical Debt (PROPOSED)
- **Monolithic Frontend Coupling:** 
  - *Issue:* JSP views are tightly coupled to Spring Controllers. Returning HTML limits the ability to build mobile native apps or standalone SPAs.
  - *Resolution:* Extract the frontend to React/Next.js and convert all Spring `@Controller` classes to `@RestController`.
- **Schema Management:**
  - *Issue:* Utilizing `spring.jpa.hibernate.ddl-auto=update` is dangerous for production deployments as it can cause unintended schema drift or data loss.
  - *Resolution:* Introduce **Flyway** or **Liquibase** for strict, version-controlled database migrations.
- **Error Handling Coverage:**
  - *Issue:* While a `GlobalExceptionHandler` exists, many specific business exceptions (e.g., `CourseNotFoundException`) result in generic 404 or 500 errors instead of graceful UI feedback.
  - *Resolution:* Expand custom exception classes and map them to friendly error pages or structured JSON API responses.
- **Hardcoded Seed Logic:**
  - *Issue:* `data.sql` handles seeding on startup.
  - *Resolution:* Move seeding to a dedicated programmatic seeder (e.g., `CommandLineRunner`) that checks if data exists before inserting to prevent unique constraint violations on restarts.