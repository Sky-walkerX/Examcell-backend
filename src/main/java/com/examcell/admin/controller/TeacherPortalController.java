package com.examcell.admin.controller;

import com.examcell.admin.dto.*;
import com.examcell.admin.service.TeacherPortalService;
import com.examcell.admin.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class TeacherPortalController {

    private final TeacherPortalService teacherPortalService;
    private final MarkService markService;

    @GetMapping("/dashboard/{teacherId}")
    public ResponseEntity<TeacherDashboardDTO> getDashboard(@PathVariable Long teacherId) {
        TeacherDashboardDTO dashboard = teacherPortalService.getTeacherDashboard(teacherId);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/{teacherId}/queries")
    public ResponseEntity<Page<QueryDTO>> getTeacherQueries(
            @PathVariable Long teacherId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        Page<QueryDTO> queries = teacherPortalService.getTeacherQueries(teacherId, status, pageable);
        return ResponseEntity.ok(queries);
    }

    @PostMapping("/{teacherId}/queries/{queryId}/respond")
    public ResponseEntity<QueryDTO> respondToQuery(
            @PathVariable Long teacherId,
            @PathVariable Long queryId,
            @RequestParam String response) {
        QueryDTO updatedQuery = teacherPortalService.respondToQuery(queryId, response, teacherId);
        return ResponseEntity.ok(updatedQuery);
    }

    @PostMapping("/{teacherId}/queries/admin")
    public ResponseEntity<QueryDTO> submitQueryToAdmin(
            @PathVariable Long teacherId,
            @Valid @RequestBody QueryDTO queryDTO) {
        QueryDTO createdQuery = teacherPortalService.submitQueryToAdmin(teacherId, queryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuery);
    }

    @GetMapping("/{teacherId}/subjects/{subjectId}/marks")
    public ResponseEntity<List<MarkDTO>> getSubjectMarks(
            @PathVariable Long teacherId,
            @PathVariable Long subjectId) {
        List<MarkDTO> marks = teacherPortalService.getTeacherSubjectMarks(teacherId, subjectId);
        return ResponseEntity.ok(marks);
    }

    @PostMapping("/{teacherId}/marks")
    public ResponseEntity<MarkDTO> createMark(
            @PathVariable Long teacherId,
            @Valid @RequestBody MarkDTO markDTO) {
        MarkDTO createdMark = teacherPortalService.createMark(teacherId, markDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMark);
    }

    @PutMapping("/{teacherId}/marks/{markId}")
    public ResponseEntity<MarkDTO> updateMark(
            @PathVariable Long teacherId,
            @PathVariable Long markId,
            @Valid @RequestBody MarkDTO markDTO) {
        MarkDTO updatedMark = teacherPortalService.updateMark(teacherId, markId, markDTO);
        return ResponseEntity.ok(updatedMark);
    }

    @DeleteMapping("/{teacherId}/marks/{markId}")
    public ResponseEntity<Void> deleteMark(
            @PathVariable Long teacherId,
            @PathVariable Long markId) {
        teacherPortalService.deleteMark(teacherId, markId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teacherId}/marks/upload")
    public ResponseEntity<List<MarkDTO>> uploadMarks(
            @PathVariable Long teacherId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("semester") String semester) throws IOException {
        List<MarkDTO> uploadedMarks = markService.uploadMarksFromExcel(file, subjectId, semester, "Teacher-" + teacherId);
        return ResponseEntity.ok(uploadedMarks);
    }
}
