package com.examcell.admin.dto;

import com.examcell.admin.entity.BonafideRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BonafideRequestDTO {
    private Long id;

    // Remove @NotNull from studentId since it's set by the controller
    private Long studentId;

    @NotBlank(message = "Purpose is required")
    private String purpose;

    private String customPurpose;
    private String additionalInfo;
    private BonafideRequest.RequestStatus status;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private String certificateNumber;
    private String certificatePath;

    // Additional fields for display
    private String studentName;
    private String studentRollNo;
    private String studentEmail;
    private String studentSemester;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
