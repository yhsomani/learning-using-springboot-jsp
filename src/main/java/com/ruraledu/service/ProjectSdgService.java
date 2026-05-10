package com.ruraledu.service;

import com.ruraledu.repository.EnrollmentRepository;
import com.ruraledu.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Requirement: Measurable SDG impact metric.
 * Aligns with UN SDG 4: Quality Education.
 */
@Service
public class ProjectSdgService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    public Map<String, Object> getSdg4ImpactMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("goal", "SDG 4: Quality Education");
        metrics.put("description", "Ensure inclusive and equitable quality education and promote lifelong learning opportunities for all.");
        
        long totalEnrollments = enrollmentRepository.count();
        long totalCertificates = certificateRepository.count();
        
        double completionRate = totalEnrollments > 0 ? (double) totalCertificates / totalEnrollments * 100.0 : 0.0;
        
        metrics.put("studentsImpacted", totalEnrollments);
        metrics.put("certificationsCompleted", totalCertificates);
        metrics.put("impactScore", (totalEnrollments * 10) + (totalCertificates * 50));
        metrics.put("completionRate", completionRate);
        metrics.put("inProgressRate", totalEnrollments > 0 ? 100.0 - completionRate : 0.0);
        
        return metrics;
    }
}
