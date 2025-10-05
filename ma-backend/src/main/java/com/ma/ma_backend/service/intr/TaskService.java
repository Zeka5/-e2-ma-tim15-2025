package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.CreateTaskRequest;
import com.ma.ma_backend.dto.TaskDto;
import com.ma.ma_backend.dto.UpdateTaskRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {
    List<TaskDto> getAllTasksForUser();
    List<TaskDto> getTasksByStatus(String status);
    List<TaskDto> getTasksByDateRange(LocalDateTime start, LocalDateTime end);
    TaskDto getTaskById(Long id);
    TaskDto createTask(CreateTaskRequest request);
    TaskDto updateTask(Long id, UpdateTaskRequest request);
    TaskDto completeTask(Long id);
    void deleteTask(Long id);
}
