package com.examcell.admin.controller;

import com.examcell.admin.dto.MarkDTO;
import com.examcell.admin.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/marks")
@RequiredArgsConstructor
public class MarkController {
    
    private final MarkService markService;
    
    @GetMapping
    public ResponseEntity<Page<MarkDTO>> getAllMarks(
            @RequestParam(required = false) String studentSearch,
            @RequestParam(required = false) String subjectCode,
            @RequestParam(required = false) String semester,
            Pageable pageable) {
        Page<MarkDTO> marks = markService.getAllMarks(studentSearch, subjectCode, semester, pageable);
        return ResponseEntity.ok(marks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MarkDTO> getMarkById(@PathVariable Long id) {
        MarkDTO mark = markService.getMarkById(id);
        return ResponseEntity.ok(mark);
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<MarkDTO>> getMarksByStudent(@PathVariable Long studentId) {
        List<MarkDTO> marks = markService.getMarksByStudent(studentId);
        return ResponseEntity.ok(marks);
    }
    
    @PostMapping
    public ResponseEntity<MarkDTO> createMark(@Valid @RequestBody MarkDTO markDTO) {
        MarkDTO createdMark = markService.createMark(markDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMark);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MarkDTO> updateMark(@PathVariable Long id, @Valid @RequestBody MarkDTO markDTO) {
        MarkDTO updatedMark = markService.updateMark(id, markDTO);
        return ResponseEntity.ok(updatedMark);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMark(@PathVariable Long id) {
        markService.deleteMark(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/upload")
    public ResponseEntity<List<MarkDTO>> uploadMarks(
            @RequestParam("file") MultipartFile file,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("semester") String semester,
            @RequestParam("uploadedBy") String uploadedBy) throws IOException {
        List<MarkDTO> uploadedMarks = markService.uploadMarksFromExcel(file, subjectId, semester, uploadedBy);
        return ResponseEntity.ok(uploadedMarks);
    }
    
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        byte[] template = markService.generateExcelTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "marks_template.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(template);
    }
}
