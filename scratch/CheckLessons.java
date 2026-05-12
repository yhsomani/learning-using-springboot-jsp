import java.sql.*;

public class CheckLessons {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ruraleduhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to MySQL!");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM lessons WHERE course_id = 7");
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("Lesson: " + rs.getString("title") + " | VideoId: " + rs.getString("video_id"));
            }
            System.out.println("Total lessons for course 7: " + count);
            
            rs = stmt.executeQuery("SELECT * FROM courses WHERE id = 7");
            if (rs.next()) {
                System.out.println("Course Title: " + rs.getString("title"));
                System.out.println("Youtube Playlist: " + rs.getString("youtube_playlist_url"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
