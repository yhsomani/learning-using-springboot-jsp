import java.sql.*;

public class ListUsers {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT username, password, role, enabled, deleted FROM users";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("Username | Password | Role | Enabled | Deleted");
                System.out.println("----------------------------------------------");
                while (rs.next()) {
                    System.out.printf("%s | %s | %s | %b | %b\n", 
                        rs.getString("username"), 
                        rs.getString("password"),
                        rs.getString("role"), 
                        rs.getBoolean("enabled"),
                        rs.getBoolean("deleted"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
