package com.ruraledu;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class SecurityPropertyTest {

    @Test
    public void testNoHardcodedAdminPassword() throws IOException {
        Path path = Paths.get("src/main/resources/application.properties");
        List<String> lines = Files.readAllLines(path);

        boolean hasAdminPassword = lines.stream()
                .anyMatch(line -> line.contains("spring.security.user.password") && line.contains("admin123"));

        assertFalse(hasAdminPassword, "Application properties should not contain hardcoded default admin password");
    }
}
