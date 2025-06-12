package com.examcell.admin.repository;

import com.examcell.admin.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmployeeId(String employeeId);
    Optional<Teacher> findByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    List<Teacher> findByActiveTrue();

    @Query("SELECT t FROM Teacher t WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.employeeId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:department IS NULL OR :department = '' OR t.department = :department)")
    Page<Teacher> findTeachersWithFilters(@Param("search") String search,
                                          @Param("department") String department,
                                          Pageable pageable);
}
