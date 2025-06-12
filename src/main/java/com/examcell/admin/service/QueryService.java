package com.examcell.admin.service;

import com.examcell.admin.dto.QueryDTO;
import com.examcell.admin.entity.Query;
import com.examcell.admin.entity.Student;
import com.examcell.admin.exception.ResourceNotFoundException;
import com.examcell.admin.repository.QueryRepository;
import com.examcell.admin.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class QueryService {
    
    private final QueryRepository queryRepository;
    private final StudentRepository studentRepository;
    
    public Page<QueryDTO> getAllQueries(String search, Query.QueryStatus status, Pageable pageable) {
        return queryRepository.findQueriesWithFilters(search, status, pageable)
                .map(this::convertToDTO);
    }
    
    public QueryDTO getQueryById(Long id) {
        Query query = queryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Query not found with id: " + id));
        return convertToDTO(query);
    }
    
    public QueryDTO createQuery(QueryDTO queryDTO) {
        Student student = studentRepository.findById(queryDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + queryDTO.getStudentId()));
        
        Query query = convertToEntity(queryDTO, student);
        query = queryRepository.save(query);
        return convertToDTO(query);
    }
    
    public QueryDTO updateQueryStatus(Long id, Query.QueryStatus status) {
        Query query = queryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Query not found with id: " + id));
        
        query.setStatus(status);
        query = queryRepository.save(query);
        return convertToDTO(query);
    }
    
    public QueryDTO respondToQuery(Long id, String response, String respondedBy) {
        Query query = queryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Query not found with id: " + id));
        
        query.setResponse(response);
        query.setRespondedBy(respondedBy);
        query.setRespondedAt(LocalDateTime.now());
        query.setStatus(Query.QueryStatus.RESOLVED);
        
        query = queryRepository.save(query);
        return convertToDTO(query);
    }
    
    public void deleteQuery(Long id) {
        if (!queryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Query not found with id: " + id);
        }
        queryRepository.deleteById(id);
    }
    
    public long getPendingQueriesCount() {
        return queryRepository.countByStatus(Query.QueryStatus.PENDING);
    }
    
    public long getResolvedQueriesCount() {
        return queryRepository.countByStatus(Query.QueryStatus.RESOLVED);
    }
    
    private QueryDTO convertToDTO(Query query) {
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
    
    private Query convertToEntity(QueryDTO dto, Student student) {
        Query query = new Query();
        query.setStudent(student);
        query.setSubject(dto.getSubject());
        query.setFaculty(dto.getFaculty());
        query.setTitle(dto.getTitle());
        query.setDescription(dto.getDescription());
        query.setStatus(dto.getStatus() != null ? dto.getStatus() : Query.QueryStatus.PENDING);
        query.setPriority(dto.getPriority() != null ? dto.getPriority() : Query.Priority.MEDIUM);
        query.setAttachments(dto.getAttachments());
        return query;
    }
}
