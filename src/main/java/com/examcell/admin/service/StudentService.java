package com.examcell.admin.service;

import com.examcell.admin.dto.StudentDTO;
import com.examcell.admin.entity.Student;
import com.examcell.admin.exception.ResourceNotFoundException;
import com.examcell.admin.exception.DuplicateResourceException;
import com.examcell.admin.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {
    
    private final StudentRepository studentRepository;
    
    public Page<StudentDTO> getAllStudents(String search, String semester, String department, Pageable pageable) {
        return studentRepository.findStudentsWithFilters(search, semester, department, pageable)
                .map(this::convertToDTO);
    }
    
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return convertToDTO(student);
    }
    
    public StudentDTO getStudentByRollNo(String rollNo) {
        Student student = studentRepository.findByRollNo(rollNo)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with roll number: " + rollNo));
        return convertToDTO(student);
    }
    
    public StudentDTO createStudent(StudentDTO studentDTO) {
        if (studentRepository.existsByRollNo(studentDTO.getRollNo())) {
            throw new DuplicateResourceException("Student with roll number " + studentDTO.getRollNo() + " already exists");
        }
        if (studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new DuplicateResourceException("Student with email " + studentDTO.getEmail() + " already exists");
        }
        
        Student student = convertToEntity(studentDTO);
        student = studentRepository.save(student);
        return convertToDTO(student);
    }
    
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        
        // Check for duplicate roll number (excluding current student)
        if (!existingStudent.getRollNo().equals(studentDTO.getRollNo()) && 
            studentRepository.existsByRollNo(studentDTO.getRollNo())) {
            throw new DuplicateResourceException("Student with roll number " + studentDTO.getRollNo() + " already exists");
        }
        
        // Check for duplicate email (excluding current student)
        if (!existingStudent.getEmail().equals(studentDTO.getEmail()) && 
            studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new DuplicateResourceException("Student with email " + studentDTO.getEmail() + " already exists");
        }
        
        updateStudentFields(existingStudent, studentDTO);
        existingStudent = studentRepository.save(existingStudent);
        return convertToDTO(existingStudent);
    }
    
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }
    
    public long getTotalStudents() {
        return studentRepository.count();
    }
    
    public long getActiveStudents() {
        return studentRepository.countByActive(true);
    }
    
    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setRollNo(student.getRollNo());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setSemester(student.getSemester());
        dto.setDepartment(student.getDepartment());
        dto.setPhoneNumber(student.getPhoneNumber());
        dto.setAddress(student.getAddress());
        dto.setActive(student.getActive());
        dto.setCreatedAt(student.getCreatedAt());
        dto.setUpdatedAt(student.getUpdatedAt());
        return dto;
    }
    
    private Student convertToEntity(StudentDTO dto) {
        Student student = new Student();
        student.setRollNo(dto.getRollNo());
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setSemester(dto.getSemester());
        student.setDepartment(dto.getDepartment());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setAddress(dto.getAddress());
        student.setActive(dto.getActive() != null ? dto.getActive() : true);
        return student;
    }
    
    private void updateStudentFields(Student student, StudentDTO dto) {
        student.setRollNo(dto.getRollNo());
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setSemester(dto.getSemester());
        student.setDepartment(dto.getDepartment());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setAddress(dto.getAddress());
        if (dto.getActive() != null) {
            student.setActive(dto.getActive());
        }
    }
}
