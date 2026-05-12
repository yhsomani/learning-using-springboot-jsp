import java.sql.*;

public class FixPasswords {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub";
        String user = "root";
        String password = "root";
        String workingHash = "$2a$10$69HskhMzpFvU9ws9XBmMBuF3jmALJ1m7O8DPmwujun7LxahZqUKYi";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "UPDATE users SET password = ? WHERE password != ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, workingHash);
                pstmt.setString(2, workingHash);
                int rows = pstmt.executeUpdate();
                System.out.println("Updated " + rows + " users with working password hash.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
