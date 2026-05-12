package com.ruraledu.util;

import java.sql.*;

public class CourseCheck {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT id, title, deleted FROM courses";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("--- Course Status Audit ---");
                while (rs.next()) {
                    System.out.printf("ID: %d | Title: %s | Deleted: %b%n",
                            rs.getInt("id"), rs.getString("title"), rs.getBoolean("deleted"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
