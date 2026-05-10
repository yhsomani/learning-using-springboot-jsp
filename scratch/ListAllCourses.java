import java.sql.*;

public class ListAllCourses {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub";
        String user = "root";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT id, title, deleted FROM courses";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("ID | Title | Deleted");
                System.out.println("---------------------");
                while (rs.next()) {
                    System.out.printf("%d | %s | %b\n", 
                        rs.getLong("id"), 
                        rs.getString("title"), 
                        rs.getBoolean("deleted"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
