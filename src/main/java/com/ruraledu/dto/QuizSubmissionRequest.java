package com.ruraledu.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for quiz submission with validation.
 */
@Data
public class QuizSubmissionRequest {
    
    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score cannot be negative")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Integer score;
    
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    private Long timeSpentSeconds;
    
    private Integer attempts;
}
