package com.ruraledu.util;

import java.sql.*;

public class FullSystemAudit {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("=== RURALEDUHUB FULL DATABASE AUDIT ===");
            
            // 1. Users Audit
            auditTable(conn, "users", "username, role, enabled, deleted, points");
            
            // 2. Courses Audit
            auditTable(conn, "courses", "id, title, category, teacher_id, deleted");
            
            // 3. Lessons Audit
            auditTable(conn, "lessons", "id, course_id, title, video_id");
            
            // 4. Quizzes Audit
            auditTable(conn, "quizzes", "id, course_id, title");
            
            // 5. Questions Audit
            auditTable(conn, "questions", "id, quiz_id, content");
            
            // 6. Enrollments Audit
            auditTable(conn, "enrollments", "id, student_id, course_id, completed, progress");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void auditTable(Connection conn, String table, String columns) throws SQLException {
        System.out.println("\n--- Table: " + table + " ---");
        String sql = "SELECT " + columns + " FROM " + table;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= colCount; i++) {
                    sb.append(meta.getColumnName(i)).append(": ").append(rs.getString(i)).append(" | ");
                }
                System.out.println(sb.toString());
            }
        }
    }
}
