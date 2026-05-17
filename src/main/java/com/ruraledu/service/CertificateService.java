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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    /**
     * CERT-01 fix: Configurable storage path via application property.
     * Defaults to 'certificates' subdirectory in the working directory.
     * In production / Docker, set certificate.storage.path to an absolute path
     * (e.g., /data/certificates or a mounted volume path).
     */
    @Value("${certificate.storage.path:${user.dir}/certificates}")
    private String certificateStoragePath;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CertificateService.class);

    @Async
    @Transactional
    public void generateCertificate(User student, Course course) {
        logger.debug("Starting certificate generation for {}", student.getFullName());
        try {
            // Optimization: Skip if already exists in DB
            if (certificateRepository.findByUserIdAndCourseId(student.getId(), course.getId()).isPresent()) {
                logger.info("Certificate already exists for student {} in course {}. Skipping generation.",
                            student.getUsername(), course.getTitle());
                return;
            }

            // CERT-01 fix: Always resolve to an absolute path
            String fileName = "certificate_" + student.getId() + "_" + course.getId() + ".pdf";
            Path storagePath = Paths.get(certificateStoragePath).toAbsolutePath();
            Path filePath = storagePath.resolve(fileName);
            File file = filePath.toFile();

            logger.debug("Certificate absolute file path: {}", file.getAbsolutePath());

            if (!file.getParentFile().exists()) {
                boolean created = file.getParentFile().mkdirs();
                if (created) {
                    logger.debug("Created directory for certificates: {}", file.getParentFile().getAbsolutePath());
                }
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

                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                    document.save(bos);
                }

                logger.info("Certificate generated successfully for student {} in course {}", student.getUsername(), course.getTitle());
            }

            Certificate cert = new Certificate();
            cert.setUser(student);
            cert.setCourse(course);
            // CERT-01 fix: Store absolute path so file can always be resolved
            cert.setCertificateUrl(file.getAbsolutePath());
            cert.setIssuedDate(LocalDateTime.now());
            certificateRepository.save(cert);

        } catch (Exception e) {
            logger.error("Error generating certificate for student {} in course {}: {}",
                         student.getUsername(), course.getTitle(), e.getMessage(), e);
        }
    }

    @Cacheable(value = "certificates", key = "#studentId + '_' + #courseId")
    public byte[] getCertificateData(Long studentId, Long courseId) throws java.io.IOException {
        Certificate cert = certificateRepository.findByUserIdAndCourseId(studentId, courseId).orElse(null);
        if (cert == null) throw new CourseNotFoundException("Certificate not found for student " + studentId + " in course " + courseId);

        File file = new File(cert.getCertificateUrl());
        if (!file.exists()) return null;

        return java.nio.file.Files.readAllBytes(file.toPath());
    }
}
