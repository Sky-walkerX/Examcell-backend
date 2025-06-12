package com.examcell.admin.controller;

import com.examcell.admin.dto.BonafideRequestDTO;
import com.examcell.admin.entity.BonafideRequest;
import com.examcell.admin.service.BonafideRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bonafide-requests")
@RequiredArgsConstructor
public class BonafideRequestController {
    
    private final BonafideRequestService bonafideRequestService;
    
    @GetMapping
    public ResponseEntity<Page<BonafideRequestDTO>> getAllRequests(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BonafideRequest.RequestStatus status,
            Pageable pageable) {
        Page<BonafideRequestDTO> requests = bonafideRequestService.getAllRequests(search, status, pageable);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BonafideRequestDTO> getRequestById(@PathVariable Long id) {
        BonafideRequestDTO request = bonafideRequestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }
    
    @PostMapping
    public ResponseEntity<BonafideRequestDTO> createRequest(@Valid @RequestBody BonafideRequestDTO requestDTO) {
        BonafideRequestDTO createdRequest = bonafideRequestService.createRequest(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<BonafideRequestDTO> approveRequest(
            @PathVariable Long id,
            @RequestParam String approvedBy) {
        BonafideRequestDTO approvedRequest = bonafideRequestService.approveRequest(id, approvedBy);
        return ResponseEntity.ok(approvedRequest);
    }
    
    @PostMapping("/{id}/reject")
    public ResponseEntity<BonafideRequestDTO> rejectRequest(
            @PathVariable Long id,
            @RequestParam String rejectionReason,
            @RequestParam String rejectedBy) {
        BonafideRequestDTO rejectedRequest = bonafideRequestService.rejectRequest(id, rejectionReason, rejectedBy);
        return ResponseEntity.ok(rejectedRequest);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        bonafideRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}
