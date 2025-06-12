package com.examcell.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalStudents;
    private long pendingQueries;
    private long bonafideRequests;
    private long resultsPublished;
    private long activeStudents;
    private long resolvedQueries;
    private long approvedBonafides;
    private long rejectedBonafides;
}
