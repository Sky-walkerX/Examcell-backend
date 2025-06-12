package com.examcell.admin.controller;

import com.examcell.admin.entity.Subject;
import com.examcell.admin.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {
    
    private final SubjectRepository subjectRepository;
    
    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findByActiveTrue();
        return ResponseEntity.ok(subjects);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        return subjectRepository.findById(id)
                .map(subject -> ResponseEntity.ok(subject))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/semester/{semester}")
    public ResponseEntity<List<Subject>> getSubjectsBySemester(@PathVariable String semester) {
        List<Subject> subjects = subjectRepository.findBySemester(semester);
        return ResponseEntity.ok(subjects);
    }
}
