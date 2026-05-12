package com.ruraledu.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class UserReset {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(url, "root", "root")) {
            Statement stmt = conn.createStatement();
            int count = stmt.executeUpdate("UPDATE users SET deleted = false, enabled = true");
            System.out.println("SUCCESS: Undeleted and enabled " + count + " users.");
        }
    }
}
