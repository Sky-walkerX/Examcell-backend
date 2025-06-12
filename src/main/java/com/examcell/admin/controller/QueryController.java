package com.examcell.admin.controller;

import com.examcell.admin.dto.QueryDTO;
import com.examcell.admin.entity.Query;
import com.examcell.admin.service.QueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queries")
@RequiredArgsConstructor
public class QueryController {
    
    private final QueryService queryService;
    
    @GetMapping
    public ResponseEntity<Page<QueryDTO>> getAllQueries(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Query.QueryStatus status,
            Pageable pageable) {
        Page<QueryDTO> queries = queryService.getAllQueries(search, status, pageable);
        return ResponseEntity.ok(queries);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<QueryDTO> getQueryById(@PathVariable Long id) {
        QueryDTO query = queryService.getQueryById(id);
        return ResponseEntity.ok(query);
    }
    
    @PostMapping
    public ResponseEntity<QueryDTO> createQuery(@Valid @RequestBody QueryDTO queryDTO) {
        QueryDTO createdQuery = queryService.createQuery(queryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuery);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<QueryDTO> updateQueryStatus(
            @PathVariable Long id,
            @RequestParam Query.QueryStatus status) {
        QueryDTO updatedQuery = queryService.updateQueryStatus(id, status);
        return ResponseEntity.ok(updatedQuery);
    }
    
    @PostMapping("/{id}/respond")
    public ResponseEntity<QueryDTO> respondToQuery(
            @PathVariable Long id,
            @RequestParam String response,
            @RequestParam String respondedBy) {
        QueryDTO updatedQuery = queryService.respondToQuery(id, response, respondedBy);
        return ResponseEntity.ok(updatedQuery);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuery(@PathVariable Long id) {
        queryService.deleteQuery(id);
        return ResponseEntity.noContent().build();
    }
}
