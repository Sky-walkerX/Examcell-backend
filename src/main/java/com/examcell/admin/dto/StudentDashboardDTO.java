package com.examcell.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardDTO {
    private StudentDTO student;
    private Double cgpa;
    private Integer completedSemesters;
    private Integer totalSemesters;
    private Long pendingQueries;
    private Long availableCertificates;
    private List<MarkDTO> recentResults;
    private List<QueryDTO> recentQueries;
}
