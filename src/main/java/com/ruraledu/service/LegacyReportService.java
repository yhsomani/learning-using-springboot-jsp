package com.ruraledu.service;

import com.ruraledu.repository.EnrollmentRepository;
import com.ruraledu.repository.EnrollmentStatsProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Standardized Reporting Service utilizing JPA Projections.
 */
@Service
public class LegacyReportService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<Map<String, Object>> getEnrollmentStats() {
        List<EnrollmentStatsProjection> projections = enrollmentRepository.getEnrollmentStats();
        List<Map<String, Object>> stats = new ArrayList<>();
        
        for (EnrollmentStatsProjection p : projections) {
            Map<String, Object> row = new HashMap<>();
            row.put("courseTitle", p.getCourseTitle());
            row.put("studentCount", p.getStudentCount().intValue());
            stats.add(row);
        }
        
        return stats;
    }
}
