-- V5__More_Seed_Courses.sql
-- Add additional default courses for RuralEduHub

-- 1. Water Management and Irrigation
INSERT INTO courses (title, description, category, difficulty, thumbnail, youtube_playlist_url, youtube_playlist_id, teacher_id, deleted)
SELECT 
    'Advanced Water Management & Irrigation',
    'Learn efficient water usage techniques, drip irrigation setup, and rainwater harvesting for small-scale farms.',
    'Agriculture', 
    'INTERMEDIATE',
    'https://images.unsplash.com/photo-1591193302685-6031267606e9?auto=format&fit=crop&w=800&q=80',
    'https://www.youtube.com/playlist?list=PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Yz',
    'PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Yz',
    (SELECT id FROM users WHERE username = 'teacher' LIMIT 1), 
    false
FROM dual 
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Advanced Water Management & Irrigation');

-- 2. Rural Healthcare Basics
INSERT INTO courses (title, description, category, difficulty, thumbnail, youtube_playlist_url, youtube_playlist_id, teacher_id, deleted)
SELECT 
    'First Aid & Community Healthcare',
    'Essential health education for rural communities, covering first aid, hygiene, and preventive care.',
    'Healthcare', 
    'BEGINNER',
    'https://images.unsplash.com/photo-1505751172876-fa1923c5c528?auto=format&fit=crop&w=800&q=80',
    'https://www.youtube.com/playlist?list=PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Za',
    'PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Za',
    (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 
    false
FROM dual 
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'First Aid & Community Healthcare');

-- 3. Animal Husbandry
INSERT INTO courses (title, description, category, difficulty, thumbnail, youtube_playlist_url, youtube_playlist_id, teacher_id, deleted)
SELECT 
    'Modern Poultry Farming',
    'Comprehensive guide to starting and managing a successful poultry business in rural areas.',
    'Agriculture', 
    'BEGINNER',
    'https://images.unsplash.com/photo-1516467508483-a7212febe31a?auto=format&fit=crop&w=800&q=80',
    'https://www.youtube.com/playlist?list=PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Zb',
    'PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Zb',
    (SELECT id FROM users WHERE username = 'teacher' LIMIT 1), 
    false
FROM dual 
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Modern Poultry Farming');

-- Add some initial lessons for the new courses to ensure they aren't empty
-- Water Management Lesson
INSERT INTO lessons (course_id, video_id, title, description, thumbnail, duration, order_index, video_url)
SELECT c.id, 'irrigation_01', 'Basics of Drip Irrigation', 'Introduction to drip irrigation systems.', 'https://i.ytimg.com/vi/irrigation_01/mqdefault.jpg', '10:00', 0, 'https://www.youtube.com/watch?v=irrigation_01'
FROM courses c 
WHERE c.title = 'Advanced Water Management & Irrigation'
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'irrigation_01');

-- Healthcare Lesson
INSERT INTO lessons (course_id, video_id, title, description, thumbnail, duration, order_index, video_url)
SELECT c.id, 'health_01', 'Emergency First Aid Basics', 'Learn the basics of emergency response.', 'https://i.ytimg.com/vi/health_01/mqdefault.jpg', '08:45', 0, 'https://www.youtube.com/watch?v=health_01'
FROM courses c 
WHERE c.title = 'First Aid & Community Healthcare'
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'health_01');

-- Poultry Lesson
INSERT INTO lessons (course_id, video_id, title, description, thumbnail, duration, order_index, video_url)
SELECT c.id, 'poultry_01', 'Setting up your first Coop', 'Practical guide to building a poultry coop.', 'https://i.ytimg.com/vi/poultry_01/mqdefault.jpg', '15:30', 0, 'https://www.youtube.com/watch?v=poultry_01'
FROM courses c 
WHERE c.title = 'Modern Poultry Farming'
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'poultry_01');
