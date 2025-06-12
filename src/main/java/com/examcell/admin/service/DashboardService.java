package com.examcell.admin.service;

import com.examcell.admin.dto.DashboardStatsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final StudentService studentService;
    private final QueryService queryService;
    private final BonafideRequestService bonafideRequestService;
    
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        stats.setTotalStudents(studentService.getTotalStudents());
        stats.setActiveStudents(studentService.getActiveStudents());
        stats.setPendingQueries(queryService.getPendingQueriesCount());
        stats.setResolvedQueries(queryService.getResolvedQueriesCount());
        stats.setBonafideRequests(bonafideRequestService.getPendingRequestsCount());
        stats.setApprovedBonafides(bonafideRequestService.getApprovedRequestsCount());
        stats.setRejectedBonafides(bonafideRequestService.getRejectedRequestsCount());
        
        // Mock results published count - you can implement this based on your requirements
        stats.setResultsPublished(156);
        
        return stats;
    }
}
