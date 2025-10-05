package com.ma.ma_backend.controller.user;

import com.ma.ma_backend.dto.TaskInstanceDto;
import com.ma.ma_backend.service.intr.TaskInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-instances")
@RequiredArgsConstructor
public class TaskInstanceController {
    private final TaskInstanceService taskInstanceService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskInstanceDto>> getTaskInstancesByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskInstanceService.getTaskInstancesByTaskId(taskId));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskInstanceDto> completeTaskInstance(@PathVariable Long id) {
        return ResponseEntity.ok(taskInstanceService.completeTaskInstance(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTaskInstance(@PathVariable Long id) {
        taskInstanceService.deleteTaskInstance(id);
        return ResponseEntity.ok("Task instance successfully deleted");
    }
}
