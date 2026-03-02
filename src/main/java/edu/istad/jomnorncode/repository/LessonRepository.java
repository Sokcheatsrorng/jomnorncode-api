package edu.istad.jomnorncode.repository;

import edu.istad.jomnorncode.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Page<Lesson> findByCourseCourseId(Long courseId, Pageable pageable);

    List<Lesson> findByCourseCourseIdOrderBySequenceNumberAsc(Long courseId);
}
