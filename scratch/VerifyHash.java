import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class VerifyHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashInDb = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        String password = "password";
        
        System.out.println("Testing password: " + password);
        System.out.println("Matches: " + encoder.matches(password, hashInDb));
        
        String hash2 = "$2a$10$69HskhMzpFvU9ws9XBmMBuF3jmALJ1m7O8DPmwujun7LxahZqUKYi";
        System.out.println("Testing hash2 with 'password': " + encoder.matches(password, hash2));
    }
}
