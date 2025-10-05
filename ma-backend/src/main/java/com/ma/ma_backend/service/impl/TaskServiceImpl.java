package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.*;
import com.ma.ma_backend.dto.CreateTaskRequest;
import com.ma.ma_backend.dto.TaskDto;
import com.ma.ma_backend.dto.UpdateTaskRequest;
import com.ma.ma_backend.exception.BadRequestException;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.mapper.EntityMapper;
import com.ma.ma_backend.repository.CategoryRepository;
import com.ma.ma_backend.repository.TaskInstanceRepository;
import com.ma.ma_backend.repository.TaskRepository;
import com.ma.ma_backend.service.intr.TaskService;
import com.ma.ma_backend.service.intr.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final EntityMapper entityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasksForUser() {
        User user = userService.getLogedInUser();
        return taskRepository.findByUserId(user.getId())
                .stream()
                .map(entityMapper::taskToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByStatus(String status) {
        // This method is now irrelevant as status is on TaskInstance, not Task
        // Return all tasks for now
        return getAllTasksForUser();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByDateRange(LocalDateTime start, LocalDateTime end) {
        // This method needs to work with TaskInstance, not Task
        // For now, return all tasks
        return getAllTasksForUser();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id) {
        User user = userService.getLogedInUser();
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));
        return entityMapper.taskToDto(task);
    }

    @Override
    @Transactional
    public TaskDto createTask(CreateTaskRequest request) {
        User user = userService.getLogedInUser();

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId()));

        if (request.getIsRecurring()) {
            validateRecurringTask(request.getIsRecurring(), request.getRecurrenceInterval(),
                    request.getRecurrenceUnit(), request.getEndDate());
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .user(user)
                .difficulty(TaskDifficulty.valueOf(request.getDifficulty().toUpperCase()))
                .importance(TaskImportance.valueOf(request.getImportance().toUpperCase()))
                .isRepeating(request.getIsRecurring())
                .recurrenceInterval(request.getRecurrenceInterval())
                .recurrenceUnit(request.getRecurrenceUnit() != null ? RepeatUnit.valueOf(request.getRecurrenceUnit().toUpperCase()) : null)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        Task savedTask = taskRepository.save(task);

        // Generate task instances
        List<TaskInstance> instances = generateTaskInstances(savedTask);
        taskInstanceRepository.saveAll(instances);

        return entityMapper.taskToDto(savedTask);
    }

    @Override
    @Transactional
    public TaskDto updateTask(Long id, UpdateTaskRequest request) {
        User user = userService.getLogedInUser();
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId()));
            task.setCategory(category);
        }

        if (request.getDifficulty() != null) {
            task.setDifficulty(TaskDifficulty.valueOf(request.getDifficulty().toUpperCase()));
        }

        if (request.getImportance() != null) {
            task.setImportance(TaskImportance.valueOf(request.getImportance().toUpperCase()));
        }

        if (request.getStartDate() != null) {
            task.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            task.setEndDate(request.getEndDate());
        }

        Task updatedTask = taskRepository.save(task);
        return entityMapper.taskToDto(updatedTask);
    }

    @Override
    @Transactional
    public TaskDto completeTask(Long id) {
        // This method should work with TaskInstance, not Task
        // For now, just throw an exception
        throw new BadRequestException("Use TaskInstance endpoints to complete tasks");
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        User user = userService.getLogedInUser();
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));

        // Delete all task instances first
        List<TaskInstance> instances = taskInstanceRepository.findByTaskId(task.getId());
        taskInstanceRepository.deleteAll(instances);

        taskRepository.delete(task);
    }

    private void validateRecurringTask(Boolean isRecurring, Integer interval, String unit, LocalDateTime endDate) {
        if (isRecurring != null && isRecurring) {
            if (interval == null || interval < 1) {
                throw new BadRequestException("Recurrence interval must be at least 1 for recurring tasks");
            }
            if (unit == null || unit.isBlank()) {
                throw new BadRequestException("Recurrence unit is required for recurring tasks");
            }
            if (endDate == null) {
                throw new BadRequestException("End date is required for recurring tasks");
            }
        }
    }

    private List<TaskInstance> generateTaskInstances(Task task) {
        List<TaskInstance> instances = new ArrayList<>();

        if (task.getIsRepeating()) {
            // Generate instances for recurring task
            LocalDateTime currentDate = task.getStartDate();
            LocalDateTime endDate = task.getEndDate();

            while (!currentDate.isAfter(endDate)) {
                TaskInstance instance = TaskInstance.builder()
                        .task(task)
                        .status(TaskStatus.ACTIVE)
                        .xpAwarded(false)
                        .xpAmount(0)
                        .startDate(currentDate)
                        .build();

                instances.add(instance);

                // Increment based on unit
                if (task.getRecurrenceUnit() == RepeatUnit.DAY) {
                    currentDate = currentDate.plusDays(task.getRecurrenceInterval());
                } else if (task.getRecurrenceUnit() == RepeatUnit.WEEK) {
                    currentDate = currentDate.plusWeeks(task.getRecurrenceInterval());
                }
            }
        } else {
            // Create single instance for non-recurring task
            TaskInstance instance = TaskInstance.builder()
                    .task(task)
                    .status(TaskStatus.ACTIVE)
                    .xpAwarded(false)
                    .xpAmount(0)
                    .startDate(task.getStartDate())
                    .build();
            instances.add(instance);
        }

        return instances;
    }
}
