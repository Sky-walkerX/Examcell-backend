package com.examcell.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private Long id;
    private String code;
    private String name;
    private String semester;
    private String department;
    private Integer credits;
    private Boolean active;
    private String faculty; // Added for student portal
}
