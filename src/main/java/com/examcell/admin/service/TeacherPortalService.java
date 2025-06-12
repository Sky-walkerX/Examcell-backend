package com.examcell.admin.service;

import com.examcell.admin.dto.*;
import com.examcell.admin.entity.Query;
import com.examcell.admin.entity.Subject;
import com.examcell.admin.entity.Teacher;
import com.examcell.admin.exception.ResourceNotFoundException;
import com.examcell.admin.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherPortalService {

    private final TeacherRepository teacherRepository;
    private final QueryRepository queryRepository;
    private final SubjectRepository subjectRepository;
    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final QueryService queryService;
    private final MarkService markService;

    public TeacherDashboardDTO getTeacherDashboard(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        TeacherDashboardDTO dashboard = new TeacherDashboardDTO();

        // Teacher info
        dashboard.setTeacher(convertTeacherToDTO(teacher));

        // Get assigned subjects
        List<Subject> assignedSubjects = getAssignedSubjects(teacher);
        dashboard.setAssignedSubjects(assignedSubjects.stream()
                .map(this::convertSubjectToDTO)
                .collect(Collectors.toList()));

        // Calculate stats
        dashboard.setSubjectsTeaching((long) assignedSubjects.size());

        // Get queries for teacher's subjects
        List<Query> teacherQueries = getQueriesForTeacher(teacher);
        dashboard.setPendingQueries(teacherQueries.stream()
                .filter(q -> q.getStatus() == Query.QueryStatus.PENDING)
                .count());

        // Recent queries (last 5)
        List<QueryDTO> recentQueries = teacherQueries.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(this::convertQueryToDTO)
                .collect(Collectors.toList());
        dashboard.setRecentQueries(recentQueries);

        // Count total students in teacher's subjects
        dashboard.setTotalStudents(studentRepository.count()); // Simplified for now

        // Count marks uploaded by teacher
        dashboard.setMarksUploaded(markRepository.count()); // Simplified for now

        return dashboard;
    }

    public Page<QueryDTO> getTeacherQueries(Long teacherId, String status, Pageable pageable) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        List<Query> allQueries = getQueriesForTeacher(teacher);

        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            Query.QueryStatus queryStatus = Query.QueryStatus.valueOf(status.toUpperCase());
            allQueries = allQueries.stream()
                    .filter(q -> q.getStatus() == queryStatus)
                    .collect(Collectors.toList());
        }

        // Apply pagination manually (simplified)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allQueries.size());
        List<Query> pageContent = allQueries.subList(start, end);

        List<QueryDTO> queryDTOs = pageContent.stream()
                .map(this::convertQueryToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(queryDTOs, pageable, allQueries.size());
    }

    @Transactional
    public QueryDTO respondToQuery(Long queryId, String response, Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        return queryService.respondToQuery(queryId, response, teacher.getName());
    }

    @Transactional
    public QueryDTO submitQueryToAdmin(Long teacherId, QueryDTO queryDTO) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        // Create a special query to admin (using a system student ID or special handling)
        queryDTO.setStudentId(1L); // Use a system student or handle differently
        queryDTO.setTitle("[TEACHER QUERY] " + queryDTO.getTitle());
        queryDTO.setDescription("From Teacher: " + teacher.getName() + " (" + teacher.getEmployeeId() + ")\n\n" + queryDTO.getDescription());

        return queryService.createQuery(queryDTO);
    }

    public List<MarkDTO> getTeacherSubjectMarks(Long teacherId, Long subjectId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        // Verify teacher teaches this subject
        if (teacher.getSubjectIds() == null || !teacher.getSubjectIds().contains(subjectId)) {
            throw new ResourceNotFoundException("Teacher is not assigned to this subject");
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        return markRepository.findBySubject(subject).stream()
                .map(this::convertMarkToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MarkDTO createMark(Long teacherId, MarkDTO markDTO) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        // Verify teacher teaches this subject
        if (teacher.getSubjectIds() == null || !teacher.getSubjectIds().contains(markDTO.getSubjectId())) {
            throw new ResourceNotFoundException("Teacher is not assigned to this subject");
        }

        markDTO.setUploadedBy(teacher.getName());
        return markService.createMark(markDTO);
    }

    @Transactional
    public MarkDTO updateMark(Long teacherId, Long markId, MarkDTO markDTO) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        markDTO.setUploadedBy(teacher.getName());
        return markService.updateMark(markId, markDTO);
    }

    @Transactional
    public void deleteMark(Long teacherId, Long markId) {
        // Verify teacher has permission (simplified for now)
        teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        markService.deleteMark(markId);
    }

    private List<Subject> getAssignedSubjects(Teacher teacher) {
        if (teacher.getSubjectIds() == null || teacher.getSubjectIds().isEmpty()) {
            return List.of();
        }

        return teacher.getSubjectIds().stream()
                .map(subjectId -> subjectRepository.findById(subjectId).orElse(null))
                .filter(subject -> subject != null)
                .collect(Collectors.toList());
    }

    private List<Query> getQueriesForTeacher(Teacher teacher) {
        List<Subject> assignedSubjects = getAssignedSubjects(teacher);
        List<String> subjectNames = assignedSubjects.stream()
                .map(Subject::getName)
                .collect(Collectors.toList());

        return queryRepository.findAll().stream()
                .filter(query -> subjectNames.contains(query.getSubject()))
                .collect(Collectors.toList());
    }

    private TeacherDTO convertTeacherToDTO(Teacher teacher) {
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
        return dto;
    }

    private QueryDTO convertQueryToDTO(Query query) {
        QueryDTO dto = new QueryDTO();
        dto.setId(query.getId());
        dto.setStudentId(query.getStudent().getId());
        dto.setSubject(query.getSubject());
        dto.setFaculty(query.getFaculty());
        dto.setTitle(query.getTitle());
        dto.setDescription(query.getDescription());
        dto.setStatus(query.getStatus());
        dto.setPriority(query.getPriority());
        dto.setResponse(query.getResponse());
        dto.setRespondedBy(query.getRespondedBy());
        dto.setRespondedAt(query.getRespondedAt());
        dto.setAttachments(query.getAttachments());
        dto.setStudentName(query.getStudent().getName());
        dto.setStudentRollNo(query.getStudent().getRollNo());
        dto.setStudentEmail(query.getStudent().getEmail());
        dto.setCreatedAt(query.getCreatedAt());
        dto.setUpdatedAt(query.getUpdatedAt());
        return dto;
    }

    private SubjectDTO convertSubjectToDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setCode(subject.getCode());
        dto.setName(subject.getName());
        dto.setSemester(subject.getSemester());
        dto.setDepartment(subject.getDepartment());
        dto.setCredits(subject.getCredits());
        dto.setActive(subject.getActive());
        return dto;
    }

    private MarkDTO convertMarkToDTO(com.examcell.admin.entity.Mark mark) {
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
}
