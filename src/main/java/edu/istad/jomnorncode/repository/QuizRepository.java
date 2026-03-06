package edu.istad.jomnorncode.repository;

import edu.istad.jomnorncode.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Page<Quiz> findByLessonLessonId(Long lessonId, Pageable pageable);

    long countByLessonLessonId(Long lessonId);
}
