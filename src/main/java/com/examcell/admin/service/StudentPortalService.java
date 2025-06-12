package com.examcell.admin.service;

import com.examcell.admin.dto.*;
import com.examcell.admin.entity.Student;
import com.examcell.admin.entity.Subject;
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
public class StudentPortalService {

    private final StudentRepository studentRepository;
    private final MarkRepository markRepository;
    private final QueryRepository queryRepository;
    private final BonafideRequestRepository bonafideRequestRepository;
    private final SubjectRepository subjectRepository;
    private final QueryService queryService;
    private final BonafideRequestService bonafideRequestService;
    private final MarkService markService;

    public StudentDashboardDTO getStudentDashboard(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        StudentDashboardDTO dashboard = new StudentDashboardDTO();

        // Student info
        dashboard.setStudent(convertStudentToDTO(student));

        // Calculate CGPA
        List<MarkDTO> allMarks = markService.getMarksByStudent(studentId);
        dashboard.setCgpa(calculateCGPA(allMarks));

        // Semester info
        dashboard.setCompletedSemesters(getSemesterNumber(student.getSemester()));
        dashboard.setTotalSemesters(8); // Assuming 8 semester program

        // Query stats
        dashboard.setPendingQueries(queryRepository.countByStudentAndStatus(student,
                com.examcell.admin.entity.Query.QueryStatus.PENDING));

        // Certificate stats
        dashboard.setAvailableCertificates(bonafideRequestRepository.countByStudentAndStatus(student,
                com.examcell.admin.entity.BonafideRequest.RequestStatus.APPROVED));

        // Recent results (last 5)
        List<MarkDTO> recentResults = allMarks.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());
        dashboard.setRecentResults(recentResults);

        // Recent queries (last 5)
        List<QueryDTO> recentQueries = queryRepository.findByStudentOrderByCreatedAtDesc(student)
                .stream()
                .limit(5)
                .map(this::convertQueryToDTO)
                .collect(Collectors.toList());
        dashboard.setRecentQueries(recentQueries);

        return dashboard;
    }

    public List<MarkDTO> getStudentResults(Long studentId) {
        return markService.getMarksByStudent(studentId);
    }

    public Page<QueryDTO> getStudentQueries(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Page<com.examcell.admin.entity.Query> queries = queryRepository.findByStudentOrderByCreatedAtDesc(student, pageable);
        List<QueryDTO> queryDTOs = queries.getContent().stream()
                .map(this::convertQueryToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(queryDTOs, pageable, queries.getTotalElements());
    }

    @Transactional
    public QueryDTO submitQuery(QueryDTO queryDTO) {
        // Validate that studentId is set
        if (queryDTO.getStudentId() == null) {
            throw new IllegalArgumentException("Student ID is required");
        }
        return queryService.createQuery(queryDTO);
    }

    public Page<BonafideRequestDTO> getBonafideRequests(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Page<com.examcell.admin.entity.BonafideRequest> requests =
                bonafideRequestRepository.findByStudentOrderByCreatedAtDesc(student, pageable);

        List<BonafideRequestDTO> requestDTOs = requests.getContent().stream()
                .map(this::convertBonafideRequestToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(requestDTOs, pageable, requests.getTotalElements());
    }

    @Transactional
    public BonafideRequestDTO submitBonafideRequest(BonafideRequestDTO requestDTO) {
        // Validate that studentId is set
        if (requestDTO.getStudentId() == null) {
            throw new IllegalArgumentException("Student ID is required");
        }
        return bonafideRequestService.createRequest(requestDTO);
    }

    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findByActiveTrue().stream()
                .map(this::convertSubjectToDTO)
                .collect(Collectors.toList());
    }

    private Double calculateCGPA(List<MarkDTO> marks) {
        if (marks.isEmpty()) return 0.0;

        double totalGradePoints = 0.0;
        int totalCredits = 0;

        for (MarkDTO mark : marks) {
            int gradePoints = getGradePoints(mark.getGrade());
            int credits = 4; // Default credits, you might want to get this from subject
            totalGradePoints += gradePoints * credits;
            totalCredits += credits;
        }

        return totalCredits > 0 ? Math.round((totalGradePoints / totalCredits) * 100.0) / 100.0 : 0.0;
    }

    private int getGradePoints(String grade) {
        return switch (grade) {
            case "A+" -> 10;
            case "A" -> 9;
            case "B+" -> 8;
            case "B" -> 7;
            case "C+" -> 6;
            case "C" -> 5;
            case "D" -> 4;
            default -> 0;
        };
    }

    private int getSemesterNumber(String semester) {
        if (semester == null) return 0;
        try {
            return Integer.parseInt(semester.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private StudentDTO convertStudentToDTO(Student student) {
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

    private QueryDTO convertQueryToDTO(com.examcell.admin.entity.Query query) {
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

    private BonafideRequestDTO convertBonafideRequestToDTO(com.examcell.admin.entity.BonafideRequest request) {
        BonafideRequestDTO dto = new BonafideRequestDTO();
        dto.setId(request.getId());
        dto.setStudentId(request.getStudent().getId());
        dto.setPurpose(request.getPurpose());
        dto.setCustomPurpose(request.getCustomPurpose());
        dto.setAdditionalInfo(request.getAdditionalInfo());
        dto.setStatus(request.getStatus());
        dto.setApprovedBy(request.getApprovedBy());
        dto.setApprovedAt(request.getApprovedAt());
        dto.setRejectionReason(request.getRejectionReason());
        dto.setCertificateNumber(request.getCertificateNumber());
        dto.setCertificatePath(request.getCertificatePath());
        dto.setStudentName(request.getStudent().getName());
        dto.setStudentRollNo(request.getStudent().getRollNo());
        dto.setStudentEmail(request.getStudent().getEmail());
        dto.setStudentSemester(request.getStudent().getSemester());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
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
        dto.setFaculty("Faculty Name"); // You might want to add faculty field to Subject entity
        return dto;
    }
}
