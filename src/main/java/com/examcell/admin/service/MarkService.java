package com.examcell.admin.service;

import com.examcell.admin.dto.MarkDTO;
import com.examcell.admin.entity.Mark;
import com.examcell.admin.entity.Student;
import com.examcell.admin.entity.Subject;
import com.examcell.admin.exception.ResourceNotFoundException;
import com.examcell.admin.repository.MarkRepository;
import com.examcell.admin.repository.StudentRepository;
import com.examcell.admin.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkService {
    
    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    
    public Page<MarkDTO> getAllMarks(String studentSearch, String subjectCode, String semester, Pageable pageable) {
        return markRepository.findMarksWithFilters(studentSearch, subjectCode, semester, pageable)
                .map(this::convertToDTO);
    }
    
    public MarkDTO getMarkById(Long id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark not found with id: " + id));
        return convertToDTO(mark);
    }
    
    public List<MarkDTO> getMarksByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return markRepository.findByStudent(student).stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    public MarkDTO createMark(MarkDTO markDTO) {
        Student student = studentRepository.findById(markDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + markDTO.getStudentId()));
        Subject subject = subjectRepository.findById(markDTO.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + markDTO.getSubjectId()));
        
        Mark mark = convertToEntity(markDTO, student, subject);
        mark = markRepository.save(mark);
        return convertToDTO(mark);
    }
    
    public MarkDTO updateMark(Long id, MarkDTO markDTO) {
        Mark existingMark = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark not found with id: " + id));
        
        existingMark.setMarks(markDTO.getMarks());
        existingMark.setExamType(markDTO.getExamType());
        existingMark.setAcademicYear(markDTO.getAcademicYear());
        existingMark.setUploadedBy(markDTO.getUploadedBy());
        
        existingMark = markRepository.save(existingMark);
        return convertToDTO(existingMark);
    }
    
    public void deleteMark(Long id) {
        if (!markRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mark not found with id: " + id);
        }
        markRepository.deleteById(id);
    }
    
    public List<MarkDTO> uploadMarksFromExcel(MultipartFile file, Long subjectId, String semester, String uploadedBy) throws IOException {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));
        
        List<MarkDTO> uploadedMarks = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                String rollNo = getCellValueAsString(row.getCell(0));
                Integer marks = getCellValueAsInteger(row.getCell(1));
                
                if (rollNo != null && marks != null) {
                    Student student = studentRepository.findByRollNo(rollNo)
                            .orElseThrow(() -> new ResourceNotFoundException("Student not found with roll number: " + rollNo));
                    
                    MarkDTO markDTO = new MarkDTO();
                    markDTO.setStudentId(student.getId());
                    markDTO.setSubjectId(subject.getId());
                    markDTO.setMarks(marks);
                    markDTO.setUploadedBy(uploadedBy);
                    markDTO.setExamType(Mark.ExamType.FINAL);
                    
                    MarkDTO savedMark = createMark(markDTO);
                    uploadedMarks.add(savedMark);
                }
            }
        }
        
        return uploadedMarks;
    }
    
    public byte[] generateExcelTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Marks Template");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Roll Number");
            headerRow.createCell(1).setCellValue("Marks");
            
            // Add sample data
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("CS001");
            sampleRow.createCell(1).setCellValue(85);
            
            // Auto-size columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            
            // Convert to byte array
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }
    
    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Integer.parseInt(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }
    
    private MarkDTO convertToDTO(Mark mark) {
        MarkDTO dto = new MarkDTO();
        dto.setId(mark.getId());
        dto.setStudentId(mark.getStudent().getId());
        dto.setSubjectId(mark.getSubject().getId());
        dto.setMarks(mark.getMarks());
        dto.setGrade(mark.getGrade());
        dto.setExamType(mark.getExamType());
        dto.setAcademicYear(mark.getAcademicYear());
        dto.setUploadedBy(mark.getUploadedBy());
        dto.setStudentName(mark.getStudent().getName());
        dto.setStudentRollNo(mark.getStudent().getRollNo());
        dto.setSubjectName(mark.getSubject().getName());
        dto.setSubjectCode(mark.getSubject().getCode());
        dto.setCreatedAt(mark.getCreatedAt());
        dto.setUpdatedAt(mark.getUpdatedAt());
        return dto;
    }
    
    private Mark convertToEntity(MarkDTO dto, Student student, Subject subject) {
        Mark mark = new Mark();
        mark.setStudent(student);
        mark.setSubject(subject);
        mark.setMarks(dto.getMarks());
        mark.setExamType(dto.getExamType() != null ? dto.getExamType() : Mark.ExamType.FINAL);
        mark.setAcademicYear(dto.getAcademicYear());
        mark.setUploadedBy(dto.getUploadedBy());
        return mark;
    }
}
