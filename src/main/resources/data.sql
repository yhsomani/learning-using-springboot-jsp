-- =====================================================
-- RuralEduHub - Initial Seed Data
-- All passwords: 'password' (BCrypt encoded)
-- BCrypt hash generated with cost factor 10
-- =====================================================

-- Seed Users
-- BCrypt hash for 'password': $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (username, password, full_name, email, role, points, location, bio, language, enabled, deleted)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Academy Admin', 'admin@ruraledu.com', 'ADMIN', 1000, 'Pune', 'Chief Administrator of RuralEduHub Platform', 'English', true, false)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO users (username, password, full_name, email, role, points, location, bio, language, enabled, deleted)
VALUES ('aryan', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Aryan Sharma', 'aryan@student.com', 'STUDENT', 500, 'Pune', 'Aspiring Agronomist and Technology Enthusiast', 'Hindi', true, false)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO users (username, password, full_name, email, role, points, location, bio, language, enabled, deleted)
VALUES ('teacher', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Dr. Savita Rao', 'savita@teacher.com', 'TEACHER', 1500, 'Nagpur', 'Senior Agricultural Scientist with 15 years of experience', 'Marathi', true, false)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO users (username, password, full_name, email, role, points, location, bio, language, enabled, deleted)
VALUES ('parent1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Priya Sharma', 'priya@parent.com', 'PARENT', 0, 'Pune', 'Parent of Aryan Sharma', 'Hindi', true, false)
ON DUPLICATE KEY UPDATE username = username;

-- Seed Courses
INSERT INTO courses (title, description, category, difficulty, thumbnail, youtube_playlist_url, teacher_id, deleted)
SELECT 'Sustainable Organic Farming',
       'Master the art of natural farming and soil regeneration. This masterclass covers everything from soil microbiology to market-ready crop planning.',
       'Agriculture', 'INTERMEDIATE',
       'https://images.unsplash.com/photo-1500651230702-0e2d8a49d4ad?auto=format&fit=crop&w=800&q=80',
       'https://www.youtube.com/playlist?list=PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Yx',
       (SELECT id FROM users WHERE username = 'admin' LIMIT 1), false
FROM dual WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Sustainable Organic Farming' AND deleted = false);

INSERT INTO courses (title, description, category, difficulty, thumbnail, youtube_playlist_url, teacher_id, deleted)
SELECT 'Digital Literacy for Rural Entrepreneurs',
       'Learn how to leverage digital tools to grow your local business and reach wider markets. Covers social media marketing, e-commerce basics, and financial technology.',
       'Technology', 'BEGINNER',
       'https://images.unsplash.com/photo-1519389950473-47ba0277781c?auto=format&fit=crop&w=800&q=80',
       'https://www.youtube.com/playlist?list=PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Yy',
       (SELECT id FROM users WHERE username = 'teacher' LIMIT 1), false
FROM dual WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Digital Literacy for Rural Entrepreneurs' AND deleted = false);

-- Seed Lessons (for first course)
INSERT INTO lessons (course_id, video_id, title, thumbnail, duration, order_index)
SELECT c.id, 'dQw4w9WgXcQ', 'Introduction to Regenerative Soil', 'https://i.ytimg.com/vi/9_S6O9rV7U0/mqdefault.jpg', '12:45', 0
FROM courses c WHERE c.title = 'Sustainable Organic Farming' AND c.deleted = false
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'dQw4w9WgXcQ');

INSERT INTO lessons (course_id, video_id, title, thumbnail, duration, order_index)
SELECT c.id, 'jNQXAC9IVRw', 'Understanding Micro-climates', 'https://i.ytimg.com/vi/fS8fE8G6S8s/mqdefault.jpg', '15:20', 1
FROM courses c WHERE c.title = 'Sustainable Organic Farming' AND c.deleted = false
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'jNQXAC9IVRw');

-- Seed Quizzes
INSERT INTO quizzes (title, course_id)
SELECT 'Soil Mastery Final', c.id
FROM courses c WHERE c.title = 'Sustainable Organic Farming' AND c.deleted = false
AND NOT EXISTS (SELECT 1 FROM quizzes q WHERE q.course_id = c.id);

-- Seed Questions
INSERT INTO questions (content, option_a, option_b, option_c, option_d, correct_answer, quiz_id)
SELECT 'What is the primary role of mycorrhizal fungi?', 'Pest control', 'Nutrient absorption', 'Water storage', 'Seed dispersal', 'B', q.id
FROM quizzes q JOIN courses c ON q.course_id = c.id WHERE c.title = 'Sustainable Organic Farming' AND c.deleted = false
AND NOT EXISTS (SELECT 1 FROM questions qu WHERE qu.quiz_id = q.id AND qu.content = 'What is the primary role of mycorrhizal fungi?');

INSERT INTO questions (content, option_a, option_b, option_c, option_d, correct_answer, quiz_id)
SELECT 'Which soil type is best for organic farming?', 'Sandy soil', 'Loamy soil', 'Clay soil', 'Rocky soil', 'B', q.id
FROM quizzes q JOIN courses c ON q.course_id = c.id WHERE c.title = 'Sustainable Organic Farming' AND c.deleted = false
AND NOT EXISTS (SELECT 1 FROM questions qu WHERE qu.quiz_id = q.id AND qu.content = 'Which soil type is best for organic farming?');

INSERT INTO questions (content, option_a, option_b, option_c, option_d, correct_answer, quiz_id)
SELECT 'What is composting?', 'Burning waste', 'Decomposing organic matter', 'Mixing chemicals', 'Plastic recycling', 'B', q.id
FROM quizzes q JOIN courses c ON q.course_id = c.id WHERE c.title = 'Sustainable Organic Farming' AND c.deleted = false
AND NOT EXISTS (SELECT 1 FROM questions qu WHERE qu.quiz_id = q.id AND qu.content = 'What is composting?');

-- Seed Lessons (for second course)
INSERT INTO lessons (course_id, video_id, title, thumbnail, duration, order_index)
SELECT c.id, 'jNQXAC9IVRw', 'Introduction to Digital Tools', 'https://i.ytimg.com/vi/jNQXAC9IVRw/mqdefault.jpg', '05:00', 0
FROM courses c WHERE c.title = 'Digital Literacy for Rural Entrepreneurs' AND c.deleted = false
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'jNQXAC9IVRw');

INSERT INTO lessons (course_id, video_id, title, thumbnail, duration, order_index)
SELECT c.id, 'tgbNymZ7vqY', 'Social Media Marketing', 'https://i.ytimg.com/vi/tgbNymZ7vqY/mqdefault.jpg', '08:30', 1
FROM courses c WHERE c.title = 'Digital Literacy for Rural Entrepreneurs' AND c.deleted = false
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'tgbNymZ7vqY');
