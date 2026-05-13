-- V3__Add_Youtube_Playlist_Id.sql
-- Add youtube_playlist_id to courses table to support YouTube integration
ALTER TABLE courses ADD COLUMN youtube_playlist_id VARCHAR(50) AFTER youtube_playlist_url;
CREATE INDEX idx_courses_playlist_id ON courses(youtube_playlist_id);
