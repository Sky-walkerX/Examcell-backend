package com.examcell.admin.repository;

import com.examcell.admin.entity.BonafideRequest;
import com.examcell.admin.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonafideRequestRepository extends JpaRepository<BonafideRequest, Long> {
    List<BonafideRequest> findByStudent(Student student);
    Page<BonafideRequest> findByStudentOrderByCreatedAtDesc(Student student, Pageable pageable);
    List<BonafideRequest> findByStatus(BonafideRequest.RequestStatus status);
    Long countByStudentAndStatus(Student student, BonafideRequest.RequestStatus status);

    @Query("SELECT br FROM BonafideRequest br JOIN br.student s WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.rollNo) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(br.purpose) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:status IS NULL OR br.status = :status)")
    Page<BonafideRequest> findRequestsWithFilters(@Param("search") String search,
                                                  @Param("status") BonafideRequest.RequestStatus status,
                                                  Pageable pageable);

    long countByStatus(BonafideRequest.RequestStatus status);
}
