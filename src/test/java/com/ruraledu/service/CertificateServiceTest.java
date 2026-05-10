package com.ruraledu.service;

import com.ruraledu.entity.Certificate;
import com.ruraledu.entity.Course;
import com.ruraledu.entity.User;
import com.ruraledu.repository.CertificateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

    private User testStudent;
    private Course testCourse;
    private File testDir;
    private String expectedFilePath;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setUsername("teststudent");
        testStudent.setFullName("Test Student");

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Test Course");

        expectedFilePath = "certificates/certificate_" + testStudent.getId() + "_" + testCourse.getId() + ".pdf";
        testDir = new File("certificates");
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
    }

    @AfterEach
    void tearDown() {
        File file = new File(expectedFilePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testGenerateCertificate_Success() {
        // Act
        certificateService.generateCertificate(testStudent, testCourse);

        // Assert
        ArgumentCaptor<Certificate> certCaptor = ArgumentCaptor.forClass(Certificate.class);
        verify(certificateRepository, times(1)).save(certCaptor.capture());

        Certificate savedCert = certCaptor.getValue();
        assertEquals(testStudent, savedCert.getUser());
        assertEquals(testCourse, savedCert.getCourse());
        assertEquals(expectedFilePath, savedCert.getCertificateUrl());
        assertNotNull(savedCert.getIssuedDate());

        // Verify file was created
        File generatedFile = new File(expectedFilePath);
        assertTrue(generatedFile.exists());
        assertTrue(generatedFile.length() > 0);
    }

    @Test
    void testGetCertificateData_Success() throws IOException {
        // Arrange
        // Create a dummy file
        File dummyFile = new File(expectedFilePath);
        Files.write(dummyFile.toPath(), "dummy content".getBytes());

        Certificate cert = new Certificate();
        cert.setUser(testStudent);
        cert.setCourse(testCourse);
        cert.setCertificateUrl(expectedFilePath);

        when(certificateRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(Optional.of(cert));

        // Act
        byte[] data = certificateService.getCertificateData(1L, 1L);

        // Assert
        assertNotNull(data);
        assertEquals("dummy content", new String(data));
    }

    @Test
    void testGetCertificateData_NotFoundInDB() throws IOException {
        // Arrange
        when(certificateRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(Optional.empty());

        // Act
        byte[] data = certificateService.getCertificateData(1L, 1L);

        // Assert
        assertNull(data);
    }

    @Test
    void testGetCertificateData_FileNotFound() throws IOException {
        // Arrange
        Certificate cert = new Certificate();
        cert.setUser(testStudent);
        cert.setCourse(testCourse);
        // Pointing to a non-existent file
        cert.setCertificateUrl("certificates/non_existent.pdf");

        when(certificateRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(Optional.of(cert));

        // Act
        byte[] data = certificateService.getCertificateData(1L, 1L);

        // Assert
        assertNull(data);
    }
}
