package com.examcell.admin.service;

import com.examcell.admin.dto.BonafideRequestDTO;
import com.examcell.admin.entity.BonafideRequest;
import com.examcell.admin.entity.Student;
import com.examcell.admin.exception.ResourceNotFoundException;
import com.examcell.admin.repository.BonafideRequestRepository;
import com.examcell.admin.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BonafideRequestService {
    
    private final BonafideRequestRepository bonafideRequestRepository;
    private final StudentRepository studentRepository;
    
    public Page<BonafideRequestDTO> getAllRequests(String search, BonafideRequest.RequestStatus status, Pageable pageable) {
        return bonafideRequestRepository.findRequestsWithFilters(search, status, pageable)
                .map(this::convertToDTO);
    }
    
    public BonafideRequestDTO getRequestById(Long id) {
        BonafideRequest request = bonafideRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bonafide request not found with id: " + id));
        return convertToDTO(request);
    }
    
    public BonafideRequestDTO createRequest(BonafideRequestDTO requestDTO) {
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + requestDTO.getStudentId()));
        
        BonafideRequest request = convertToEntity(requestDTO, student);
        request = bonafideRequestRepository.save(request);
        return convertToDTO(request);
    }
    
    public BonafideRequestDTO approveRequest(Long id, String approvedBy) {
        BonafideRequest request = bonafideRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bonafide request not found with id: " + id));
        
        request.setStatus(BonafideRequest.RequestStatus.APPROVED);
        request.setApprovedBy(approvedBy);
        request.setApprovedAt(LocalDateTime.now());
        request.setCertificateNumber(generateCertificateNumber());
        
        request = bonafideRequestRepository.save(request);
        return convertToDTO(request);
    }
    
    public BonafideRequestDTO rejectRequest(Long id, String rejectionReason, String rejectedBy) {
        BonafideRequest request = bonafideRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bonafide request not found with id: " + id));
        
        request.setStatus(BonafideRequest.RequestStatus.REJECTED);
        request.setRejectionReason(rejectionReason);
        request.setApprovedBy(rejectedBy);
        request.setApprovedAt(LocalDateTime.now());
        
        request = bonafideRequestRepository.save(request);
        return convertToDTO(request);
    }
    
    public void deleteRequest(Long id) {
        if (!bonafideRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bonafide request not found with id: " + id);
        }
        bonafideRequestRepository.deleteById(id);
    }
    
    public long getPendingRequestsCount() {
        return bonafideRequestRepository.countByStatus(BonafideRequest.RequestStatus.PENDING);
    }
    
    public long getApprovedRequestsCount() {
        return bonafideRequestRepository.countByStatus(BonafideRequest.RequestStatus.APPROVED);
    }
    
    public long getRejectedRequestsCount() {
        return bonafideRequestRepository.countByStatus(BonafideRequest.RequestStatus.REJECTED);
    }
    
    private String generateCertificateNumber() {
        return "BF" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
    
    private BonafideRequestDTO convertToDTO(BonafideRequest request) {
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
    
    private BonafideRequest convertToEntity(BonafideRequestDTO dto, Student student) {
        BonafideRequest request = new BonafideRequest();
        request.setStudent(student);
        request.setPurpose(dto.getPurpose());
        request.setCustomPurpose(dto.getCustomPurpose());
        request.setAdditionalInfo(dto.getAdditionalInfo());
        request.setStatus(dto.getStatus() != null ? dto.getStatus() : BonafideRequest.RequestStatus.PENDING);
        return request;
    }
}
