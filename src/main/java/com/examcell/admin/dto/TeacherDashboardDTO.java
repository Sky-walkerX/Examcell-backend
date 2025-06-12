package com.examcell.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardDTO {
    private TeacherDTO teacher;
    private Long totalStudents;
    private Long pendingQueries;
    private Long subjectsTeaching;
    private Long marksUploaded;
    private List<QueryDTO> recentQueries;
    private List<SubjectDTO> assignedSubjects;
}
