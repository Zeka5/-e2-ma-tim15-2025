package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.TaskInstance;
import com.ma.ma_backend.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskInstanceRepository extends JpaRepository<TaskInstance, Long> {
    List<TaskInstance> findByTaskUserId(Long userId);
    List<TaskInstance> findByTaskUserIdAndStatus(Long userId, TaskStatus status);
    List<TaskInstance> findByTaskId(Long taskId);
    Optional<TaskInstance> findByIdAndTaskUserId(Long id, Long userId);
}
