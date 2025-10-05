package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.TaskInstanceDto;

import java.util.List;

public interface TaskInstanceService {
    List<TaskInstanceDto> getTaskInstancesByTaskId(Long taskId);
    TaskInstanceDto completeTaskInstance(Long instanceId);
    void deleteTaskInstance(Long instanceId);
}
