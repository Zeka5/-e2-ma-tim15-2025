package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.TaskDifficulty;
import com.ma.ma_backend.domain.TaskImportance;
import com.ma.ma_backend.domain.TaskInstance;
import com.ma.ma_backend.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskInstanceRepository extends JpaRepository<TaskInstance, Long> {
    List<TaskInstance> findByTaskUserId(Long userId);
    List<TaskInstance> findByTaskUserIdAndStatus(Long userId, TaskStatus status);
    List<TaskInstance> findByTaskId(Long taskId);
    List<TaskInstance> findByTaskIdAndTaskUserIdOrderByStartDateAsc(Long taskId, Long userId);
    Optional<TaskInstance> findByIdAndTaskUserId(Long id, Long userId);

    // Count completed instances by difficulty and importance in time period
    @Query("SELECT COUNT(ti) FROM TaskInstance ti WHERE ti.task.user.id = :userId " +
            "AND ti.task.difficulty = :difficulty AND ti.task.importance = :importance " +
            "AND ti.status = 'COMPLETED' AND ti.xpAwarded = true " +
            "AND ti.completedAt >= :startDate AND ti.completedAt <= :endDate")
    long countCompletedByDifficultyAndImportanceInPeriod(
            @Param("userId") Long userId,
            @Param("difficulty") TaskDifficulty difficulty,
            @Param("importance") TaskImportance importance,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Count completed instances by difficulty only in time period
    @Query("SELECT COUNT(ti) FROM TaskInstance ti WHERE ti.task.user.id = :userId " +
            "AND ti.task.difficulty = :difficulty " +
            "AND ti.status = 'COMPLETED' AND ti.xpAwarded = true " +
            "AND ti.completedAt >= :startDate AND ti.completedAt <= :endDate")
    long countCompletedByDifficultyInPeriod(
            @Param("userId") Long userId,
            @Param("difficulty") TaskDifficulty difficulty,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Count completed instances by importance only in time period
    @Query("SELECT COUNT(ti) FROM TaskInstance ti WHERE ti.task.user.id = :userId " +
            "AND ti.task.importance = :importance " +
            "AND ti.status = 'COMPLETED' AND ti.xpAwarded = true " +
            "AND ti.completedAt >= :startDate AND ti.completedAt <= :endDate")
    long countCompletedByImportanceInPeriod(
            @Param("userId") Long userId,
            @Param("importance") TaskImportance importance,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
