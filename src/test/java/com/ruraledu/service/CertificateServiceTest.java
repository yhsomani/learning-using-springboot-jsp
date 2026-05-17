package com.ruraledu.service;

import com.ruraledu.entity.Certificate;
import com.ruraledu.entity.Course;
import com.ruraledu.entity.User;
import com.ruraledu.repository.CertificateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @InjectMocks
    private CertificateService certificateService;

    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Inject the required property value that would normally be provided by Spring
        org.springframework.test.util.ReflectionTestUtils.setField(
            certificateService, "certificateStoragePath", System.getProperty("user.dir") + "/certificates");

        student = new User();
        student.setId(1L);
        student.setUsername("testuser");
        student.setFullName("Test User");

        course = new Course();
        course.setId(100L);
        course.setTitle("Java Basics");
    }

    @AfterEach
    void tearDown() {
        // Clean up any generated files
        File certFile = new File("certificates/certificate_1_100.pdf");
        if (certFile.exists()) {
            certFile.delete();
        }
        File testCertFile = new File("certificates/test_cert.pdf");
        if (testCertFile.exists()) {
            testCertFile.delete();
        }
        File certDir = new File("certificates");
        if (certDir.exists() && certDir.list() != null && certDir.list().length == 0) {
            certDir.delete();
        }
    }

    @Test
    void testGenerateCertificate_Success() {
        // Run the async method directly (it will run synchronously in the test thread)
        certificateService.generateCertificate(student, course);

        // Verify that the file was created
        File expectedFile = new File("certificates/certificate_1_100.pdf");
        assertTrue(expectedFile.exists());

        // Verify that the repository save method was called
        verify(certificateRepository, times(1)).save(any(Certificate.class));
    }

    @Test
    void testGenerateCertificate_ExceptionHandling() {
        // Cause an exception to be thrown by passing a mock student that throws on getId()
        User mockStudent = mock(User.class);
        when(mockStudent.getFullName()).thenReturn("Mock Student");
        when(mockStudent.getUsername()).thenReturn("mockuser");
        when(mockStudent.getId()).thenThrow(new RuntimeException("Simulated exception"));

        // Since the method returns void, we just verify it doesn't propagate the exception
        assertDoesNotThrow(() -> {
            certificateService.generateCertificate(mockStudent, course);
        });

        // Verify save was never called due to the exception
        verify(certificateRepository, never()).save(any(Certificate.class));
    }

    @Test
    void testGetCertificateData_Success() throws IOException {
        // Setup mock certificate
        Certificate mockCert = new Certificate();
        mockCert.setCertificateUrl("certificates/test_cert.pdf");
        when(certificateRepository.findByUserIdAndCourseId(1L, 100L)).thenReturn(Optional.of(mockCert));

        // Create a dummy file
        File dir = new File("certificates");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File("certificates/test_cert.pdf");
        Files.write(file.toPath(), "test content".getBytes());

        // Test
        byte[] data = certificateService.getCertificateData(1L, 100L);

        assertNotNull(data);
        assertEquals("test content", new String(data));
    }

    @Test
    void testGetCertificateData_CertificateNotFound() {
        when(certificateRepository.findByUserIdAndCourseId(1L, 100L)).thenReturn(Optional.empty());

        assertThrows(com.ruraledu.exception.CourseNotFoundException.class, () -> {
            certificateService.getCertificateData(1L, 100L);
        });
    }

    @Test
    void testGetCertificateData_FileNotFound() throws IOException {
        Certificate mockCert = new Certificate();
        mockCert.setCertificateUrl("certificates/non_existent.pdf");
        when(certificateRepository.findByUserIdAndCourseId(1L, 100L)).thenReturn(Optional.of(mockCert));

        byte[] data = certificateService.getCertificateData(1L, 100L);

        assertNull(data);
    }
}
