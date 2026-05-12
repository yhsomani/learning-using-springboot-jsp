import java.sql.*;

public class UnDeleteAdmin {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to MySQL!");
            Statement stmt = conn.createStatement();
            int rows = stmt.executeUpdate("UPDATE users SET deleted = false WHERE username = 'admin'");
            System.out.println("Updated " + rows + " rows.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
