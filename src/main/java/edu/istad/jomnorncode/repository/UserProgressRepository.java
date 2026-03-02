package edu.istad.jomnorncode.repository;

import edu.istad.jomnorncode.entity.UserProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    Page<UserProgress> findByUserUserIdAndLessonLessonId(Long userId, Long lessonId, Pageable pageable);

    Page<UserProgress> findByUserUserId(Long userId, Pageable pageable);

    Page<UserProgress> findByLessonLessonId(Long lessonId, Pageable pageable);

    // REMOVED: findByRolesRoleName — this belongs in UserRepository

    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.user.userId = :userId AND up.completionDate IS NOT NULL")
    long countByUserUserIdAndCompletedAtIsNotNull(@Param("userId") Long userId);

    @Query("SELECT " +
            "CASE WHEN COUNT(l) = 0 THEN 0.0 " +
            "ELSE (COUNT(up) * 100.0 / COUNT(l)) END " +
            "FROM Lesson l " +
            "LEFT JOIN UserProgress up ON up.lesson = l AND up.user.userId = :userId AND up.isCompleted = true " +
            "WHERE l.course.courseId = :courseId")
    Double calculateCourseProgress(@Param("userId") Long userId, @Param("courseId") Long courseId);
}