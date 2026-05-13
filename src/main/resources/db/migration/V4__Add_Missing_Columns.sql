-- V4__Add_Missing_Columns.sql
-- Add description and video_url to lessons table
ALTER TABLE lessons ADD COLUMN description TEXT AFTER title;
ALTER TABLE lessons ADD COLUMN video_url VARCHAR(500) AFTER order_index;
