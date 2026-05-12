import java.sql.*;

public class CheckDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to MySQL!");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username, role, enabled, deleted FROM users");
            while (rs.next()) {
                System.out.println("User: " + rs.getString("username") + ", Role: " + rs.getString("role") + ", Enabled: " + rs.getBoolean("enabled") + ", Deleted: " + rs.getBoolean("deleted"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
