package com.examcell.admin.controller;

import com.examcell.admin.dto.*;
import com.examcell.admin.service.StudentPortalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class StudentPortalController {

    private final StudentPortalService studentPortalService;

    @GetMapping("/dashboard/{studentId}")
    public ResponseEntity<StudentDashboardDTO> getDashboard(@PathVariable Long studentId) {
        StudentDashboardDTO dashboard = studentPortalService.getStudentDashboard(studentId);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/{studentId}/results")
    public ResponseEntity<List<MarkDTO>> getStudentResults(@PathVariable Long studentId) {
        List<MarkDTO> results = studentPortalService.getStudentResults(studentId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{studentId}/queries")
    public ResponseEntity<Page<QueryDTO>> getStudentQueries(
            @PathVariable Long studentId,
            Pageable pageable) {
        Page<QueryDTO> queries = studentPortalService.getStudentQueries(studentId, pageable);
        return ResponseEntity.ok(queries);
    }

    @PostMapping("/{studentId}/queries")
    public ResponseEntity<QueryDTO> submitQuery(
            @PathVariable Long studentId,
            @Valid @RequestBody QueryDTO queryDTO) {
        // Set studentId before calling service
        queryDTO.setStudentId(studentId);
        QueryDTO createdQuery = studentPortalService.submitQuery(queryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuery);
    }

    @GetMapping("/{studentId}/bonafide-requests")
    public ResponseEntity<Page<BonafideRequestDTO>> getBonafideRequests(
            @PathVariable Long studentId,
            Pageable pageable) {
        Page<BonafideRequestDTO> requests = studentPortalService.getBonafideRequests(studentId, pageable);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{studentId}/bonafide-requests")
    public ResponseEntity<BonafideRequestDTO> submitBonafideRequest(
            @PathVariable Long studentId,
            @Valid @RequestBody BonafideRequestDTO requestDTO) {
        // Set studentId before calling service
        requestDTO.setStudentId(studentId);
        BonafideRequestDTO createdRequest = studentPortalService.submitBonafideRequest(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectDTO>> getSubjects() {
        List<SubjectDTO> subjects = studentPortalService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }
}
