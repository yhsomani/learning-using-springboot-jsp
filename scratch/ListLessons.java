import java.sql.*;

public class ListLessons {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT id, title, course_id FROM lessons";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("ID | Title | CourseID");
                System.out.println("---------------------");
                while (rs.next()) {
                    System.out.printf("%d | %s | %d\n", 
                        rs.getLong("id"), 
                        rs.getString("title"), 
                        rs.getLong("course_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
