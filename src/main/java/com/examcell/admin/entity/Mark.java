package com.examcell.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "marks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "subject_id", "exam_type"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "Student is required")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @NotNull(message = "Subject is required")
    private Subject subject;

    @Min(value = 0, message = "Marks cannot be negative")
    @Max(value = 100, message = "Marks cannot exceed 100")
    @NotNull(message = "Marks are required")
    private Integer marks;

    private String grade;

    @Enumerated(EnumType.STRING)
    private ExamType examType = ExamType.FINAL;

    private String academicYear;
    private String uploadedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum ExamType {
        MIDTERM, FINAL, ASSIGNMENT, QUIZ, PROJECT
    }

    @PrePersist
    @PreUpdate
    private void calculateGrade() {
        if (marks != null) {
            if (marks >= 90) grade = "A+";
            else if (marks >= 85) grade = "A";
            else if (marks >= 80) grade = "B+";
            else if (marks >= 75) grade = "B";
            else if (marks >= 70) grade = "C+";
            else if (marks >= 65) grade = "C";
            else if (marks >= 60) grade = "D";
            else grade = "F";
        }
    }
}
