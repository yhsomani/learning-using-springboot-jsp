package com.ruraledu.service;

import com.ruraledu.entity.User;
import com.ruraledu.entity.Course;
import com.ruraledu.entity.Certificate;
import com.ruraledu.repository.CertificateRepository;
import com.ruraledu.exception.CourseNotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.File;
import java.time.LocalDateTime;

@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certificateRepository;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CertificateService.class);

    @Async
    public void generateCertificate(User student, Course course) {
        System.out.println("DEBUG: Starting certificate generation for " + student.getFullName());
        try {
            String fileName = "certificate_" + student.getId() + "_" + course.getId() + ".pdf";
            String relativePath = "certificates/" + fileName;
            File file = new File(relativePath);
            
            System.out.println("DEBUG: File path: " + file.getAbsolutePath());

            if (!file.getParentFile().exists()) {
                boolean created = file.getParentFile().mkdirs();
                System.out.println("DEBUG: Created parents: " + created);
            }

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 24);
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText("CERTIFICATE OF COMPLETION");
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 18);
                    contentStream.newLineAtOffset(100, 650);
                    contentStream.showText("This is to certify that " + student.getFullName());
                    contentStream.newLineAtOffset(0, -30);
                    contentStream.showText("has successfully completed the course:");
                    contentStream.newLineAtOffset(0, -30);
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
                    contentStream.showText(course.getTitle());
                    contentStream.endText();
                }
                document.save(file);
                logger.info("Certificate generated successfully for student {} in course {}", student.getUsername(), course.getTitle());
            }

            Certificate cert = new Certificate();
            cert.setUser(student);
            cert.setCourse(course);
            cert.setCertificateUrl(relativePath); // Store relative path
            cert.setIssuedDate(LocalDateTime.now());
            certificateRepository.save(cert);

        } catch (Exception e) {
            logger.error("Error generating certificate for student {} in course {}: {}", 
                         student.getUsername(), course.getTitle(), e.getMessage(), e);
        }
    }

    public byte[] getCertificateData(Long studentId, Long courseId) throws java.io.IOException {
        Certificate cert = certificateRepository.findByUserIdAndCourseId(studentId, courseId).orElse(null);
        if (cert == null) {
            throw new CourseNotFoundException("Course not found or no certificate exists for course ID: " + courseId);
        }
        
        File file = new File(cert.getCertificateUrl());
        if (!file.exists()) return null;
        
        return java.nio.file.Files.readAllBytes(file.toPath());
    }
}
