package com.examcell.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {
    private Long id;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    private String department;
    private String designation;
    private String phoneNumber;
    private String specialization;
    private Boolean active;
    private List<Long> subjectIds;
    private List<String> subjectNames; // For display
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
