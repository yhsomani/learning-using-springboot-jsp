package com.ruraledu.util;

import java.sql.*;

public class UserAudit {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT username, role, enabled, deleted FROM users";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("--- User Audit ---");
                while (rs.next()) {
                    System.out.printf("User: %s | Role: %s | Enabled: %b | Deleted: %b%n",
                            rs.getString("username"), rs.getString("role"),
                            rs.getBoolean("enabled"), rs.getBoolean("deleted"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
