package com.examcell.admin.repository;

import com.examcell.admin.entity.Mark;
import com.examcell.admin.entity.Student;
import com.examcell.admin.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    List<Mark> findByStudent(Student student);
    List<Mark> findBySubject(Subject subject);
    Optional<Mark> findByStudentAndSubjectAndExamType(Student student, Subject subject, Mark.ExamType examType);
    
    @Query("SELECT m FROM Mark m JOIN m.student s JOIN m.subject sub WHERE " +
           "(:studentSearch IS NULL OR :studentSearch = '' OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :studentSearch, '%')) OR " +
           "LOWER(s.rollNo) LIKE LOWER(CONCAT('%', :studentSearch, '%'))) AND " +
           "(:subjectCode IS NULL OR :subjectCode = '' OR sub.code = :subjectCode) AND " +
           "(:semester IS NULL OR :semester = '' OR s.semester = :semester)")
    Page<Mark> findMarksWithFilters(@Param("studentSearch") String studentSearch,
                                  @Param("subjectCode") String subjectCode,
                                  @Param("semester") String semester,
                                  Pageable pageable);
    
    @Query("SELECT AVG(m.marks) FROM Mark m WHERE m.subject.code = :subjectCode")
    Double getAverageMarksBySubject(@Param("subjectCode") String subjectCode);
    
    long countByGrade(String grade);
}
