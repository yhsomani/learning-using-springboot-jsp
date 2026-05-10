-- Performance Indexes for Scalability
-- Optimized for high-frequency dashboard queries and relationship lookups

-- Enrollments: Speed up user-specific and course-specific student tracking
CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_enrollments_status ON enrollments(completed);

-- Lesson Progress: Critical for real-time tracking in low-bandwidth sessions
CREATE INDEX idx_lesson_progress_student ON lesson_progress(student_id);
CREATE INDEX idx_lesson_progress_lesson ON lesson_progress(lesson_id);
CREATE INDEX idx_lesson_progress_composite ON lesson_progress(student_id, lesson_id, completed);

-- Users: Speed up role-based analytics and status filtering
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_enabled_deleted ON users(enabled, deleted);

-- Courses: Optimize category-based recommendations
CREATE INDEX idx_courses_category ON courses(category);
CREATE INDEX idx_courses_teacher ON courses(teacher_id);
CREATE INDEX idx_courses_deleted ON courses(deleted);

-- Audit Logs: Fast retrieval of recent administrative actions
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp DESC);
CREATE INDEX idx_audit_logs_actor ON audit_logs(performed_by);
