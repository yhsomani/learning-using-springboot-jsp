import java.sql.*;

public class UnDeleteAll {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        try (Connection conn = DriverManager.getConnection(url, "root", "root")) {
            Statement stmt = conn.createStatement();
            int count = stmt.executeUpdate("UPDATE users SET deleted = false");
            System.out.println("Undeleted " + count + " users.");
        }
    }
}
