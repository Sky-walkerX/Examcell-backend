package com.examcell.admin.repository;

import com.examcell.admin.entity.Query;
import com.examcell.admin.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepository<Query, Long> {
    List<Query> findByStudent(Student student);
    List<Query> findByStudentOrderByCreatedAtDesc(Student student);
    Page<Query> findByStudentOrderByCreatedAtDesc(Student student, Pageable pageable);
    List<Query> findByStatus(Query.QueryStatus status);
    List<Query> findByPriority(Query.Priority priority);
    Long countByStudentAndStatus(Student student, Query.QueryStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT q FROM Query q JOIN q.student s WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(q.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(q.subject) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:status IS NULL OR q.status = :status)")
    Page<Query> findQueriesWithFilters(@Param("search") String search,
                                       @Param("status") Query.QueryStatus status,
                                       Pageable pageable);

    long countByStatus(Query.QueryStatus status);
    long countByPriority(Query.Priority priority);
}
