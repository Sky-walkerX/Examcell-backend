package com.examcell.admin.repository;

import com.examcell.admin.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);
    List<Subject> findBySemester(String semester);
    List<Subject> findByDepartment(String department);
    List<Subject> findByActiveTrue();
    boolean existsByCode(String code);
}
