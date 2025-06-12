package com.examcell.admin.service;

import com.examcell.admin.dto.TeacherDTO;
import com.examcell.admin.entity.Subject;
import com.examcell.admin.entity.Teacher;
import com.examcell.admin.exception.DuplicateResourceException;
import com.examcell.admin.exception.ResourceNotFoundException;
import com.examcell.admin.repository.SubjectRepository;
import com.examcell.admin.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    public Page<TeacherDTO> getAllTeachers(String search, String department, Pageable pageable) {
        return teacherRepository.findTeachersWithFilters(search, department, pageable)
                .map(this::convertToDTO);
    }

    public TeacherDTO getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        return convertToDTO(teacher);
    }

    public TeacherDTO getTeacherByEmployeeId(String employeeId) {
        Teacher teacher = teacherRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with employee ID: " + employeeId));
        return convertToDTO(teacher);
    }

    public TeacherDTO createTeacher(TeacherDTO teacherDTO) {
        if (teacherRepository.existsByEmployeeId(teacherDTO.getEmployeeId())) {
            throw new DuplicateResourceException("Teacher with employee ID " + teacherDTO.getEmployeeId() + " already exists");
        }
        if (teacherRepository.existsByEmail(teacherDTO.getEmail())) {
            throw new DuplicateResourceException("Teacher with email " + teacherDTO.getEmail() + " already exists");
        }

        Teacher teacher = convertToEntity(teacherDTO);
        teacher = teacherRepository.save(teacher);
        return convertToDTO(teacher);
    }

    public TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO) {
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        // Check for duplicate employee ID (excluding current teacher)
        if (!existingTeacher.getEmployeeId().equals(teacherDTO.getEmployeeId()) &&
                teacherRepository.existsByEmployeeId(teacherDTO.getEmployeeId())) {
            throw new DuplicateResourceException("Teacher with employee ID " + teacherDTO.getEmployeeId() + " already exists");
        }

        // Check for duplicate email (excluding current teacher)
        if (!existingTeacher.getEmail().equals(teacherDTO.getEmail()) &&
                teacherRepository.existsByEmail(teacherDTO.getEmail())) {
            throw new DuplicateResourceException("Teacher with email " + teacherDTO.getEmail() + " already exists");
        }

        updateTeacherFields(existingTeacher, teacherDTO);
        existingTeacher = teacherRepository.save(existingTeacher);
        return convertToDTO(existingTeacher);
    }

    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    public long getTotalTeachers() {
        return teacherRepository.count();
    }

    private TeacherDTO convertToDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setEmployeeId(teacher.getEmployeeId());
        dto.setName(teacher.getName());
        dto.setEmail(teacher.getEmail());
        dto.setDepartment(teacher.getDepartment());
        dto.setDesignation(teacher.getDesignation());
        dto.setPhoneNumber(teacher.getPhoneNumber());
        dto.setSpecialization(teacher.getSpecialization());
        dto.setActive(teacher.getActive());
        dto.setSubjectIds(teacher.getSubjectIds());
        dto.setCreatedAt(teacher.getCreatedAt());
        dto.setUpdatedAt(teacher.getUpdatedAt());

        // Get subject names
        if (teacher.getSubjectIds() != null && !teacher.getSubjectIds().isEmpty()) {
            List<String> subjectNames = teacher.getSubjectIds().stream()
                    .map(subjectId -> subjectRepository.findById(subjectId)
                            .map(Subject::getName)
                            .orElse("Unknown Subject"))
                    .collect(Collectors.toList());
            dto.setSubjectNames(subjectNames);
        }

        return dto;
    }

    private Teacher convertToEntity(TeacherDTO dto) {
        Teacher teacher = new Teacher();
        teacher.setEmployeeId(dto.getEmployeeId());
        teacher.setName(dto.getName());
        teacher.setEmail(dto.getEmail());
        teacher.setDepartment(dto.getDepartment());
        teacher.setDesignation(dto.getDesignation());
        teacher.setPhoneNumber(dto.getPhoneNumber());
        teacher.setSpecialization(dto.getSpecialization());
        teacher.setActive(dto.getActive() != null ? dto.getActive() : true);
        teacher.setSubjectIds(dto.getSubjectIds());
        return teacher;
    }

    private void updateTeacherFields(Teacher teacher, TeacherDTO dto) {
        teacher.setEmployeeId(dto.getEmployeeId());
        teacher.setName(dto.getName());
        teacher.setEmail(dto.getEmail());
        teacher.setDepartment(dto.getDepartment());
        teacher.setDesignation(dto.getDesignation());
        teacher.setPhoneNumber(dto.getPhoneNumber());
        teacher.setSpecialization(dto.getSpecialization());
        teacher.setSubjectIds(dto.getSubjectIds());
        if (dto.getActive() != null) {
            teacher.setActive(dto.getActive());
        }
    }
}
