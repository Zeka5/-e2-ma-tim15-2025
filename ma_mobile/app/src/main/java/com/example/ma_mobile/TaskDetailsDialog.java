package com.example.ma_mobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ma_mobile.models.Task;
import com.example.ma_mobile.models.TaskInstance;
import com.example.ma_mobile.repository.TaskRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskDetailsDialog extends Dialog {

    private Task task;
    private TaskDetailsListener listener;
    private TaskRepository taskRepository;

    // Views
    private TextView tvTaskTitle;
    private TextView tvCategoryName;
    private View viewCategoryColor;
    private TextView tvTaskDescription;
    private TextView tvTaskDifficulty;
    private TextView tvTaskImportance;
    private TextView tvTaskXp;
    private TextView tvTaskStatus;
    private TextView tvTaskStartDate;
    private TextView tvTaskEndDate;
    private TextView tvTaskRecurrence;
    private TextView labelEndDate;
    private TextView labelRecurrence;
    private TextView labelInstances;
    private LinearLayout llTaskInstances;
    private Button btnCompleteTask;
    private Button btnEditTask;
    private Button btnDeleteTask;
    private Button btnClose;

    private List<TaskInstance> taskInstances;

    public interface TaskDetailsListener {
        void onTaskUpdated();
        void onTaskDeleted();
    }

    public TaskDetailsDialog(@NonNull Context context, Task task, TaskDetailsListener listener) {
        super(context);
        this.task = task;
        this.listener = listener;
        this.taskRepository = new TaskRepository(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_task_details);

        // Set dialog width to match parent with margins
        Window window = getWindow();
        if (window != null) {
            android.view.WindowManager.LayoutParams lp = new android.view.WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }

        initializeViews();
        populateTaskDetails();
        setupButtons();

        // Load task instances for all tasks to check status
        loadTaskInstances();
    }

    private void initializeViews() {
        tvTaskTitle = findViewById(R.id.tv_task_title);
        tvCategoryName = findViewById(R.id.tv_category_name);
        viewCategoryColor = findViewById(R.id.view_category_color);
        tvTaskDescription = findViewById(R.id.tv_task_description);
        tvTaskDifficulty = findViewById(R.id.tv_task_difficulty);
        tvTaskImportance = findViewById(R.id.tv_task_importance);
        tvTaskXp = findViewById(R.id.tv_task_xp);
        tvTaskStatus = findViewById(R.id.tv_task_status);
        tvTaskStartDate = findViewById(R.id.tv_task_start_date);
        tvTaskEndDate = findViewById(R.id.tv_task_end_date);
        tvTaskRecurrence = findViewById(R.id.tv_task_recurrence);
        labelEndDate = findViewById(R.id.label_end_date);
        labelRecurrence = findViewById(R.id.label_recurrence);
        labelInstances = findViewById(R.id.label_instances);
        llTaskInstances = findViewById(R.id.ll_task_instances);
        btnCompleteTask = findViewById(R.id.btn_complete_task);
        btnEditTask = findViewById(R.id.btn_edit_task);
        btnDeleteTask = findViewById(R.id.btn_delete_task);
        btnClose = findViewById(R.id.btn_close);
    }

    private void populateTaskDetails() {
        // Title
        tvTaskTitle.setText(task.getTitle());

        // Category
        tvCategoryName.setText(task.getCategoryName());

        // Category color
        GradientDrawable colorDrawable = new GradientDrawable();
        try {
            colorDrawable.setColor(Color.parseColor(task.getCategoryColor()));
        } catch (IllegalArgumentException e) {
            colorDrawable.setColor(Color.GRAY);
        }
        colorDrawable.setCornerRadius(12);
        viewCategoryColor.setBackground(colorDrawable);

        // Description
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            tvTaskDescription.setText(task.getDescription());
        } else {
            tvTaskDescription.setText("No description");
            tvTaskDescription.setTextColor(Color.parseColor("#9E9E9E"));
        }

        // Difficulty
        tvTaskDifficulty.setText(formatDifficulty(task.getDifficulty()));

        // Importance
        tvTaskImportance.setText(formatImportance(task.getImportance()));

        // XP
        tvTaskXp.setText(task.getTotalXp() + " XP");

        // Start Date
        tvTaskStartDate.setText(formatDate(task.getStartDate()));

        // Repeating task info
        if (task.getIsRecurring() != null && task.getIsRecurring()) {
            // Show end date
            if (task.getEndDate() != null && !task.getEndDate().isEmpty()) {
                labelEndDate.setVisibility(View.VISIBLE);
                tvTaskEndDate.setVisibility(View.VISIBLE);
                tvTaskEndDate.setText(formatDate(task.getEndDate()));
            }

            // Show recurrence info
            labelRecurrence.setVisibility(View.VISIBLE);
            tvTaskRecurrence.setVisibility(View.VISIBLE);

            String recurrenceText = "Repeats every " + task.getRecurrenceInterval() + " ";
            switch (task.getRecurrenceUnit()) {
                case "DAY":
                    recurrenceText += task.getRecurrenceInterval() == 1 ? "day" : "days";
                    break;
                case "WEEK":
                    recurrenceText += task.getRecurrenceInterval() == 1 ? "week" : "weeks";
                    break;
                case "MONTH":
                    recurrenceText += task.getRecurrenceInterval() == 1 ? "month" : "months";
                    break;
                default:
                    recurrenceText += task.getRecurrenceUnit().toLowerCase();
            }
            tvTaskRecurrence.setText(recurrenceText);
        } else {
            labelEndDate.setVisibility(View.GONE);
            tvTaskEndDate.setVisibility(View.GONE);
            labelRecurrence.setVisibility(View.GONE);
            tvTaskRecurrence.setVisibility(View.GONE);
        }
    }

    private void setupButtons() {
        btnCompleteTask.setOnClickListener(v -> completeTask());
        btnEditTask.setOnClickListener(v -> editTask());
        btnDeleteTask.setOnClickListener(v -> confirmDeleteTask());
        btnClose.setOnClickListener(v -> dismiss());
    }

    private void completeTask() {
        // For regular tasks, find the first pending instance and complete it
        // For repeating tasks, user should complete from the instances list (not implemented here for simplicity)

        if (taskInstances != null && !taskInstances.isEmpty()) {
            // Find first pending instance
            TaskInstance pendingInstance = null;
            for (TaskInstance instance : taskInstances) {
                if (instance.getStatus() == null || instance.getStatus().equals("PENDING") || instance.getStatus().equals("ACTIVE")) {
                    pendingInstance = instance;
                    break;
                }
            }

            if (pendingInstance != null) {
                completeTaskInstance(pendingInstance);
            } else {
                Toast.makeText(getContext(), "No pending tasks to complete", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Load instances first, then complete
            taskRepository.getTaskInstances(task.getId(), new TaskRepository.TaskInstanceListCallback() {
                @Override
                public void onSuccess(List<TaskInstance> instances) {
                    taskInstances = instances;
                    if (!instances.isEmpty()) {
                        TaskInstance pendingInstance = null;
                        for (TaskInstance instance : instances) {
                            if (instance.getStatus() == null || instance.getStatus().equals("PENDING") || instance.getStatus().equals("ACTIVE")) {
                                pendingInstance = instance;
                                break;
                            }
                        }
                        if (pendingInstance != null) {
                            completeTaskInstance(pendingInstance);
                        } else {
                            Toast.makeText(getContext(), "No pending tasks to complete", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), "Error loading task: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void completeTaskInstance(TaskInstance instance) {
        taskRepository.completeTaskInstance(instance.getId(), new TaskRepository.TaskInstanceCallback() {
            @Override
            public void onSuccess(TaskInstance completedInstance) {
                String message;
                if (completedInstance.getXpAwarded() != null && completedInstance.getXpAwarded()) {
                    message = "Task completed! +" + completedInstance.getXpAmount() + " XP";
                } else {
                    message = "Task completed! (Daily XP quota reached - no XP awarded)";
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                if (listener != null) {
                    listener.onTaskUpdated();
                }
                dismiss();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editTask() {
        dismiss();
        AddEditTaskDialog editDialog = new AddEditTaskDialog(getContext(), task,
                new AddEditTaskDialog.TaskDialogListener() {
                    @Override
                    public void onTaskSaved() {
                        if (listener != null) {
                            listener.onTaskUpdated();
                        }
                    }
                });
        editDialog.show();
    }

    private void confirmDeleteTask() {
        String message;
        if (task.getIsRecurring() != null && task.getIsRecurring()) {
            message = "This will delete all FUTURE occurrences of '" + task.getTitle() + "'. Past completed occurrences will remain. Continue?";
        } else {
            message = "Are you sure you want to delete '" + task.getTitle() + "'?";
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Delete Task")
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> deleteTask())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTask() {
        if (task.getIsRecurring() != null && task.getIsRecurring()) {
            deleteFutureTaskInstances();
        } else {
            deleteRegularTask();
        }
    }

    private void deleteFutureTaskInstances() {
        if (taskInstances == null || taskInstances.isEmpty()) {
            // No instances loaded, just delete the task
            deleteRegularTask();
            return;
        }

        // Count how many pending/future instances to delete
        int pendingCount = 0;
        for (TaskInstance instance : taskInstances) {
            String status = instance.getStatus();
            if (status == null || status.equals("PENDING") || status.equals("ACTIVE")) {
                pendingCount++;
            }
        }

        if (pendingCount == 0) {
            Toast.makeText(getContext(), "No future occurrences to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        // Delete all pending instances
        final int[] deletedCount = {0};
        final int totalToDelete = pendingCount;

        for (TaskInstance instance : taskInstances) {
            String status = instance.getStatus();
            if (status == null || status.equals("PENDING") || status.equals("ACTIVE")) {
                taskRepository.deleteTaskInstance(instance.getId(), new TaskRepository.DeleteCallback() {
                    @Override
                    public void onSuccess() {
                        deletedCount[0]++;
                        if (deletedCount[0] == totalToDelete) {
                            Toast.makeText(getContext(), "Deleted " + deletedCount[0] + " future occurrence(s). Past occurrences preserved.", Toast.LENGTH_LONG).show();
                            if (listener != null) {
                                listener.onTaskDeleted();
                            }
                            dismiss();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Error deleting instance: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void deleteRegularTask() {
        taskRepository.deleteTask(task.getId(), new TaskRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onTaskDeleted();
                }
                dismiss();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDifficulty(String difficulty) {
        if (difficulty == null) return "Unknown";
        switch (difficulty) {
            case "VERY_EASY": return "Very Easy";
            case "EASY": return "Easy";
            case "HARD": return "Hard";
            case "EXTREMELY_HARD": return "Extremely Hard";
            default: return difficulty;
        }
    }

    private String formatImportance(String importance) {
        if (importance == null) return "Unknown";
        switch (importance) {
            case "NORMAL": return "Normal";
            case "IMPORTANT": return "Important";
            case "EXTREMELY_IMPORTANT": return "Very Important";
            case "SPECIAL": return "Special";
            default: return importance;
        }
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "Not set";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            try {
                // Try simpler format
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateStr);
                if (date != null) {
                    return outputFormat.format(date);
                }
            } catch (Exception ex) {
                // Return original string if parsing fails
            }
        }

        return dateStr;
    }

    private void loadTaskInstances() {
        taskRepository.getTaskInstances(task.getId(), new TaskRepository.TaskInstanceListCallback() {
            @Override
            public void onSuccess(List<TaskInstance> instances) {
                taskInstances = instances;
                updateTaskStatus();
                displayTaskInstances();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Could not load task occurrences: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTaskStatus() {
        if (taskInstances == null || taskInstances.isEmpty()) {
            return;
        }

        // Check the status of instances
        int pendingCount = 0;
        int completedCount = 0;
        int failedCount = 0;
        boolean hasCompletableInstance = false;

        for (TaskInstance instance : taskInstances) {
            String status = instance.getStatus();
            if (status == null || status.equals("PENDING") || status.equals("ACTIVE")) {
                pendingCount++;

                // Check if this pending instance can be completed (not in the future and endDate passed)
                if (canCompleteInstance(instance)) {
                    hasCompletableInstance = true;
                }
            } else if (status.equals("COMPLETED")) {
                completedCount++;
            } else if (status.equals("FAILED")) {
                failedCount++;
            }
        }

        // Determine overall status
        String overallStatus;
        int statusColor;

        if (pendingCount == 0 && completedCount > 0) {
            // All instances completed
            overallStatus = "All Completed";
            statusColor = Color.parseColor("#4CAF50");

            // Disable complete button
            btnCompleteTask.setEnabled(false);
            btnCompleteTask.setAlpha(0.5f);
        } else if (pendingCount > 0 && completedCount > 0) {
            // Partially completed
            overallStatus = "In Progress (" + completedCount + "/" + taskInstances.size() + ")";
            statusColor = Color.parseColor("#FFC107");

            // Check if there's a completable instance
            if (!hasCompletableInstance) {
                btnCompleteTask.setEnabled(false);
                btnCompleteTask.setAlpha(0.5f);
            }
        } else if (pendingCount > 0 && completedCount == 0) {
            // All pending
            overallStatus = "Pending";
            statusColor = Color.parseColor("#2196F3");

            // Check if there's a completable instance
            if (!hasCompletableInstance) {
                btnCompleteTask.setEnabled(false);
                btnCompleteTask.setAlpha(0.5f);
            }
        } else {
            // Default
            overallStatus = "Unknown";
            statusColor = Color.parseColor("#9E9E9E");
        }

        // Update status view
        tvTaskStatus.setText(overallStatus);
        GradientDrawable statusBg = new GradientDrawable();
        statusBg.setCornerRadius(16);
        statusBg.setColor(statusColor);
        tvTaskStatus.setBackground(statusBg);
    }

    private boolean isTaskInFuture(String startDateStr) {
        if (startDateStr == null || startDateStr.isEmpty()) {
            return false;
        }

        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date startDate = format.parse(startDateStr);
            if (startDate != null) {
                return startDate.after(new java.util.Date());
            }
        } catch (Exception e) {
            // If parsing fails, assume it's not in the future
            return false;
        }
        return false;
    }

    private boolean canCompleteInstance(TaskInstance instance) {
        // Check if startDate is in the future
        if (isTaskInFuture(instance.getStartDate())) {
            return false;
        }

        // Check if task has endDate and if it hasn't passed yet
        if (task.getEndDate() != null && !task.getEndDate().isEmpty()) {
            if (isTaskInFuture(task.getEndDate())) {
                return false;
            }
        }

        return true;
    }

    private void displayTaskInstances() {
        if (taskInstances == null || taskInstances.isEmpty()) {
            return;
        }

        labelInstances.setVisibility(View.VISIBLE);
        llTaskInstances.setVisibility(View.VISIBLE);
        llTaskInstances.removeAllViews();

        for (int i = 0; i < taskInstances.size(); i++) {
            TaskInstance instance = taskInstances.get(i);
            View instanceView = createInstanceView(i + 1, instance);
            llTaskInstances.addView(instanceView);
        }

        // Check if all instances are completed - if yes, disable edit/delete
        checkAndDisableButtonsIfAllCompleted();
    }

    private void checkAndDisableButtonsIfAllCompleted() {
        if (taskInstances == null || taskInstances.isEmpty()) {
            return;
        }

        // For repeating tasks: check if ANY instance is completed
        // If at least one is completed, disable edit and delete
        boolean hasCompletedInstance = false;
        for (TaskInstance instance : taskInstances) {
            String status = instance.getStatus();
            if (status != null && status.equals("COMPLETED")) {
                hasCompletedInstance = true;
                break;
            }
        }

        // If at least one instance is completed, disable edit and delete buttons
        if (hasCompletedInstance) {
            btnEditTask.setEnabled(false);
            btnEditTask.setAlpha(0.5f);
            btnDeleteTask.setEnabled(false);
            btnDeleteTask.setAlpha(0.5f);
        }
    }

    private View createInstanceView(int index, TaskInstance instance) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(12, 8, 12, 8);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 4, 0, 4);
        itemLayout.setLayoutParams(layoutParams);

        // Index
        TextView tvIndex = new TextView(getContext());
        tvIndex.setText("#" + index);
        tvIndex.setTextSize(14);
        tvIndex.setTextColor(Color.parseColor("#424242"));
        tvIndex.setTypeface(tvIndex.getTypeface(), android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams indexParams = new LinearLayout.LayoutParams(
                60,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvIndex.setLayoutParams(indexParams);
        itemLayout.addView(tvIndex);

        // Date
        TextView tvDate = new TextView(getContext());
        tvDate.setText(formatDate(instance.getStartDate()));
        tvDate.setTextSize(14);
        tvDate.setTextColor(Color.parseColor("#424242"));
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        tvDate.setLayoutParams(dateParams);
        itemLayout.addView(tvDate);

        // Status
        TextView tvStatus = new TextView(getContext());
        String status = instance.getStatus() != null ? instance.getStatus() : "PENDING";

        // If completed, show XP info in status
        if (status.equals("COMPLETED")) {
            if (instance.getXpAwarded() != null && instance.getXpAwarded()) {
                tvStatus.setText("+" + instance.getXpAmount() + " XP");
            } else {
                tvStatus.setText("No XP");
            }
        } else {
            tvStatus.setText(formatStatus(status));
        }

        tvStatus.setTextSize(12);
        tvStatus.setPadding(8, 4, 8, 4);

        // Status color
        GradientDrawable statusBg = new GradientDrawable();
        statusBg.setCornerRadius(12);
        switch (status) {
            case "COMPLETED":
                if (instance.getXpAwarded() != null && instance.getXpAwarded()) {
                    statusBg.setColor(Color.parseColor("#4CAF50"));
                    tvStatus.setTextColor(Color.WHITE);
                } else {
                    statusBg.setColor(Color.parseColor("#9E9E9E"));
                    tvStatus.setTextColor(Color.WHITE);
                }
                break;
            case "PENDING":
                statusBg.setColor(Color.parseColor("#FFC107"));
                tvStatus.setTextColor(Color.BLACK);
                break;
            case "FAILED":
                statusBg.setColor(Color.parseColor("#F44336"));
                tvStatus.setTextColor(Color.WHITE);
                break;
            default:
                statusBg.setColor(Color.parseColor("#E0E0E0"));
                tvStatus.setTextColor(Color.BLACK);
        }
        tvStatus.setBackground(statusBg);
        itemLayout.addView(tvStatus);

        return itemLayout;
    }

    private String formatStatus(String status) {
        if (status == null) return "Pending";
        switch (status) {
            case "COMPLETED": return "Completed";
            case "PENDING": return "Pending";
            case "FAILED": return "Failed";
            default: return status;
        }
    }
}
