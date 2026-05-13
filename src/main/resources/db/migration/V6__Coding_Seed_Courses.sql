-- V6__Coding_Seed_Courses.sql
-- Add technology and coding related courses for RuralEduHub

-- 1. Python Programming
INSERT INTO courses (title, description, category, difficulty, thumbnail, youtube_playlist_url, youtube_playlist_id, teacher_id, deleted)
SELECT 
    'Introduction to Python for Beginners',
    'Master the basics of Python programming. Learn about variables, loops, functions, and how to build simple automation scripts.',
    'Technology', 
    'BEGINNER',
    'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=800&q=80',
    'https://www.youtube.com/playlist?list=PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Zc',
    'PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Zc',
    (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 
    false
FROM dual 
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Introduction to Python for Beginners');

-- 2. Web Development
INSERT INTO courses (title, description, category, difficulty, thumbnail, youtube_playlist_url, youtube_playlist_id, teacher_id, deleted)
SELECT 
    'Building Your First Website (HTML & CSS)',
    'Learn the building blocks of the web. Create your own personal website using HTML5 and CSS3 from scratch.',
    'Technology', 
    'BEGINNER',
    'https://images.unsplash.com/photo-1547658719-da2b51169166?auto=format&fit=crop&w=800&q=80',
    'https://www.youtube.com/playlist?list=PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Zd',
    'PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Zd',
    (SELECT id FROM users WHERE username = 'teacher' LIMIT 1), 
    false
FROM dual 
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Building Your First Website (HTML & CSS)');

-- 3. Data Science for Agriculture
INSERT INTO courses (title, description, category, difficulty, thumbnail, youtube_playlist_url, youtube_playlist_id, teacher_id, deleted)
SELECT 
    'Data Science in Agriculture',
    'Discover how to use data and simple analytics to improve crop yields and predict market prices.',
    'Technology', 
    'INTERMEDIATE',
    'https://images.unsplash.com/photo-1551288049-bbbda5366391?auto=format&fit=crop&w=800&q=80',
    'https://www.youtube.com/playlist?list=PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Ze',
    'PLbxi6Z7u7I0X-W5h6_xYm6p6l1T-O9_Ze',
    (SELECT id FROM users WHERE username = 'teacher' LIMIT 1), 
    false
FROM dual 
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE title = 'Data Science in Agriculture');

-- Add initial lessons for coding courses
-- Python Lesson
INSERT INTO lessons (course_id, video_id, title, description, thumbnail, duration, order_index, video_url)
SELECT c.id, 'python_01', 'Setting up Python Environment', 'Install Python and your first IDE.', 'https://i.ytimg.com/vi/python_01/mqdefault.jpg', '05:40', 0, 'https://www.youtube.com/watch?v=python_01'
FROM courses c 
WHERE c.title = 'Introduction to Python for Beginners'
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'python_01');

-- Web Dev Lesson
INSERT INTO lessons (course_id, video_id, title, description, thumbnail, duration, order_index, video_url)
SELECT c.id, 'html_01', 'HTML Tags & Structure', 'Learn the basics of HTML document structure.', 'https://i.ytimg.com/vi/html_01/mqdefault.jpg', '12:15', 0, 'https://www.youtube.com/watch?v=html_01'
FROM courses c 
WHERE c.title = 'Building Your First Website (HTML & CSS)'
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'html_01');

-- Data Science Lesson
INSERT INTO lessons (course_id, video_id, title, description, thumbnail, duration, order_index, video_url)
SELECT c.id, 'agri_data_01', 'Introduction to Yield Prediction', 'How data helps in predicting agricultural output.', 'https://i.ytimg.com/vi/agri_data_01/mqdefault.jpg', '10:30', 0, 'https://www.youtube.com/watch?v=agri_data_01'
FROM courses c 
WHERE c.title = 'Data Science in Agriculture'
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'agri_data_01');
