package com.examcell.admin.dto;

import com.examcell.admin.entity.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryDTO {
    private Long id;

    // Remove @NotNull from studentId since it's set by the controller
    private Long studentId;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Faculty name is required")
    private String faculty;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private Query.QueryStatus status;
    private Query.Priority priority;
    private String response;
    private String respondedBy;
    private LocalDateTime respondedAt;
    private List<String> attachments;

    // Additional fields for display
    private String studentName;
    private String studentRollNo;
    private String studentEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
