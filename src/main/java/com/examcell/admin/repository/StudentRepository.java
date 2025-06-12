package com.examcell.admin.repository;

import com.examcell.admin.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNo(String rollNo);
    Optional<Student> findByEmail(String email);
    boolean existsByRollNo(String rollNo);
    boolean existsByEmail(String email);
    
    @Query("SELECT s FROM Student s WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.rollNo) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:semester IS NULL OR :semester = '' OR s.semester = :semester) AND " +
           "(:department IS NULL OR :department = '' OR s.department = :department)")
    Page<Student> findStudentsWithFilters(@Param("search") String search,
                                        @Param("semester") String semester,
                                        @Param("department") String department,
                                        Pageable pageable);
    
    long countByActive(boolean active);
    long countBySemester(String semester);
}
