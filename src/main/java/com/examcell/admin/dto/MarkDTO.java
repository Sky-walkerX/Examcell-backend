package com.examcell.admin.dto;

import com.examcell.admin.entity.Mark;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkDTO {
    private Long id;

    // Remove @NotNull since these might be set by the controller
    private Long studentId;

    private Long subjectId;

    @Min(value = 0, message = "Marks cannot be negative")
    @Max(value = 100, message = "Marks cannot exceed 100")
    @NotNull(message = "Marks are required")
    private Integer marks;

    private String grade;
    private Mark.ExamType examType;
    private String academicYear;
    private String uploadedBy;

    // Additional fields for display
    private String studentName;
    private String studentRollNo;
    private String subjectName;
    private String subjectCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
