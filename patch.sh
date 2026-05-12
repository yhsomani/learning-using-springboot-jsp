sed -i "s/'9_S6O9rV7U0'/'dQw4w9WgXcQ'/g" ./src/main/resources/data.sql
sed -i "s/'fS8fE8G6S8s'/'jNQXAC9IVRw'/g" ./src/main/resources/data.sql
cat << 'INNER_EOF' >> ./src/main/resources/data.sql

-- Seed Lessons (for second course)
INSERT INTO lessons (course_id, video_id, title, thumbnail, duration, order_index)
SELECT c.id, 'jNQXAC9IVRw', 'Introduction to Digital Tools', 'https://i.ytimg.com/vi/jNQXAC9IVRw/mqdefault.jpg', '05:00', 0
FROM courses c WHERE c.title = 'Digital Literacy for Rural Entrepreneurs' AND c.deleted = false
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'jNQXAC9IVRw');

INSERT INTO lessons (course_id, video_id, title, thumbnail, duration, order_index)
SELECT c.id, 'tgbNymZ7vqY', 'Social Media Marketing', 'https://i.ytimg.com/vi/tgbNymZ7vqY/mqdefault.jpg', '08:30', 1
FROM courses c WHERE c.title = 'Digital Literacy for Rural Entrepreneurs' AND c.deleted = false
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.course_id = c.id AND l.video_id = 'tgbNymZ7vqY');
INNER_EOF
