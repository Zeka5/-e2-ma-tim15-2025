package com.example.ma_mobile;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ma_mobile.models.Task;
import com.example.ma_mobile.models.TaskInstance;
import com.example.ma_mobile.repository.TaskRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private LinearLayout llTasksList;
    private TextView tvNoTasks;
    private FloatingActionButton fabAddTask;
    private FloatingActionButton fabCalendar;
    private FloatingActionButton fabAddCategory;
    private TabLayout tabLayout;
    private TaskRepository taskRepository;

    private List<Task> allTasks = new ArrayList<>();
    private boolean showingRepeatingTasks = false;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskRepository = new TaskRepository(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        loadTasks();
    }

    private void initializeViews(View view) {
        llTasksList = view.findViewById(R.id.ll_tasks_list);
        tvNoTasks = view.findViewById(R.id.tv_no_tasks);
        fabAddTask = view.findViewById(R.id.fab_add_task);
        fabCalendar = view.findViewById(R.id.fab_calendar);
        fabAddCategory = view.findViewById(R.id.fab_add_category);
        tabLayout = view.findViewById(R.id.tab_layout);

        fabAddTask.setOnClickListener(v -> openAddTaskDialog());
        fabCalendar.setOnClickListener(v -> openCalendar());
        fabAddCategory.setOnClickListener(v -> openCategoriesPage());

        setupTabLayout();
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showingRepeatingTasks = tab.getPosition() == 1;
                filterAndDisplayTasks();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void loadTasks() {
        taskRepository.getAllTasks(new TaskRepository.TaskListCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                allTasks = tasks;
                filterAndDisplayTasks();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                allTasks = new ArrayList<>();
                filterAndDisplayTasks();
            }
        });
    }

    private void filterAndDisplayTasks() {
        List<Task> filteredTasks = new ArrayList<>();

        for (Task task : allTasks) {
            boolean isRepeating = task.getIsRecurring() != null && task.getIsRecurring();

            if (showingRepeatingTasks && isRepeating) {
                filteredTasks.add(task);
            } else if (!showingRepeatingTasks && !isRepeating) {
                filteredTasks.add(task);
            }
        }

        if (filteredTasks.isEmpty()) {
            showNoTasks();
        } else {
            displayTasks(filteredTasks);
        }
    }

    private void showNoTasks() {
        if (tvNoTasks != null && llTasksList != null) {
            tvNoTasks.setVisibility(View.VISIBLE);
            llTasksList.setVisibility(View.GONE);
        }
    }

    private void displayTasks(List<Task> tasks) {
        if (tvNoTasks != null && llTasksList != null) {
            tvNoTasks.setVisibility(View.GONE);
            llTasksList.setVisibility(View.VISIBLE);
            llTasksList.removeAllViews();

            for (Task task : tasks) {
                View taskView = createTaskView(task);
                llTasksList.addView(taskView);
            }
        }
    }

    private View createTaskView(Task task) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(32, 24, 32, 24);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(16);
        background.setStroke(2, Color.parseColor("#E0E0E0"));
        itemLayout.setBackground(background);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16);
        itemLayout.setLayoutParams(layoutParams);

        // Header with category color indicator
        LinearLayout headerLayout = new LinearLayout(getContext());
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setPadding(0, 0, 0, 8);

        // Category color indicator
        View colorView = new View(getContext());
        GradientDrawable colorDrawable = new GradientDrawable();
        try {
            colorDrawable.setColor(Color.parseColor(task.getCategoryColor()));
        } catch (IllegalArgumentException e) {
            colorDrawable.setColor(Color.GRAY);
        }
        colorDrawable.setCornerRadius(8);
        colorView.setBackground(colorDrawable);

        LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(40, 40);
        colorParams.setMargins(0, 0, 16, 0);
        colorView.setLayoutParams(colorParams);
        headerLayout.addView(colorView);

        // Task title
        TextView titleView = new TextView(getContext());
        titleView.setText(task.getTitle());
        titleView.setTextSize(18);
        titleView.setTextColor(Color.BLACK);
        titleView.setTypeface(titleView.getTypeface(), android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        titleView.setLayoutParams(titleParams);
        headerLayout.addView(titleView);

        // XP badge
        TextView xpView = new TextView(getContext());
        xpView.setText(task.getTotalXp() + " XP");
        xpView.setTextSize(14);
        xpView.setTextColor(Color.parseColor("#9C27B0"));
        xpView.setTypeface(xpView.getTypeface(), android.graphics.Typeface.BOLD);
        headerLayout.addView(xpView);

        itemLayout.addView(headerLayout);

        // Category name
        TextView categoryView = new TextView(getContext());
        categoryView.setText(task.getCategoryName());
        categoryView.setTextSize(12);
        categoryView.setTextColor(Color.parseColor("#757575"));
        LinearLayout.LayoutParams categoryParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        categoryParams.setMargins(0, 4, 0, 8);
        categoryView.setLayoutParams(categoryParams);
        itemLayout.addView(categoryView);

        // Description (if available)
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            TextView descView = new TextView(getContext());
            descView.setText(task.getDescription());
            descView.setTextSize(14);
            descView.setTextColor(Color.parseColor("#424242"));
            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            descParams.setMargins(0, 0, 0, 8);
            descView.setLayoutParams(descParams);
            itemLayout.addView(descView);
        }

        // Task info (difficulty, importance, execution time)
        LinearLayout infoLayout = new LinearLayout(getContext());
        infoLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        infoParams.setMargins(0, 8, 0, 0);
        infoLayout.setLayoutParams(infoParams);

        TextView difficultyView = new TextView(getContext());
        difficultyView.setText(formatDifficulty(task.getDifficulty()));
        difficultyView.setTextSize(12);
        difficultyView.setTextColor(Color.parseColor("#FF5722"));
        difficultyView.setPadding(12, 4, 12, 4);
        infoLayout.addView(difficultyView);

        TextView importanceView = new TextView(getContext());
        importanceView.setText(formatImportance(task.getImportance()));
        importanceView.setTextSize(12);
        importanceView.setTextColor(Color.parseColor("#2196F3"));
        importanceView.setPadding(12, 4, 12, 4);
        LinearLayout.LayoutParams impParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        impParams.setMargins(8, 0, 0, 0);
        importanceView.setLayoutParams(impParams);
        infoLayout.addView(importanceView);

        itemLayout.addView(infoLayout);

        // Click listeners
        itemLayout.setOnClickListener(v -> openTaskDetailsDialog(task));
        itemLayout.setOnLongClickListener(v -> {
            showTaskOptions(task);
            return true;
        });

        return itemLayout;
    }

    private String formatDifficulty(String difficulty) {
        switch (difficulty) {
            case "VERY_EASY": return "Very Easy";
            case "EASY": return "Easy";
            case "HARD": return "Hard";
            case "EXTREMELY_HARD": return "Extremely Hard";
            default: return difficulty;
        }
    }

    private String formatImportance(String importance) {
        switch (importance) {
            case "NORMAL": return "Normal";
            case "IMPORTANT": return "Important";
            case "EXTREMELY_IMPORTANT": return "Very Important";
            case "SPECIAL": return "Special";
            default: return importance;
        }
    }

    private void openAddTaskDialog() {
        AddEditTaskDialog dialog = new AddEditTaskDialog(requireContext(), null,
                new AddEditTaskDialog.TaskDialogListener() {
                    @Override
                    public void onTaskSaved() {
                        loadTasks();
                    }
                });
        dialog.show();
    }

    private void openTaskDetailsDialog(Task task) {
        TaskDetailsDialog dialog = new TaskDetailsDialog(requireContext(), task,
                new TaskDetailsDialog.TaskDetailsListener() {
                    @Override
                    public void onTaskUpdated() {
                        loadTasks();
                    }

                    @Override
                    public void onTaskDeleted() {
                        loadTasks();
                    }
                });
        dialog.show();
    }

    private void openEditTaskDialog(Task task) {
        AddEditTaskDialog dialog = new AddEditTaskDialog(requireContext(), task,
                new AddEditTaskDialog.TaskDialogListener() {
                    @Override
                    public void onTaskSaved() {
                        loadTasks();
                    }
                });
        dialog.show();
    }

    private void showTaskOptions(Task task) {
        String[] options = {"Complete", "Edit", "Delete"};

        new AlertDialog.Builder(requireContext())
                .setTitle(task.getTitle())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Complete
                            completeTask(task);
                            break;
                        case 1: // Edit
                            openEditTaskDialog(task);
                            break;
                        case 2: // Delete
                            confirmDeleteTask(task);
                            break;
                    }
                })
                .show();
    }

    private void completeTask(Task task) {
        // Load task instances and complete the first pending one
        taskRepository.getTaskInstances(task.getId(), new TaskRepository.TaskInstanceListCallback() {
            @Override
            public void onSuccess(List<TaskInstance> instances) {
                if (instances.isEmpty()) {
                    Toast.makeText(requireContext(), "No task instances found", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Find first pending/active instance
                TaskInstance pendingInstance = null;
                for (TaskInstance instance : instances) {
                    String status = instance.getStatus();
                    if (status == null || status.equals("PENDING") || status.equals("ACTIVE")) {
                        pendingInstance = instance;
                        break;
                    }
                }

                if (pendingInstance != null) {
                    completeTaskInstance(pendingInstance);
                } else {
                    Toast.makeText(requireContext(), "No pending tasks to complete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                loadTasks();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteTask(Task task) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete task '" + task.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> deleteTask(task))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTask(Task task) {
        taskRepository.deleteTask(task.getId(), new TaskRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                loadTasks();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCalendar() {
        CalendarFragment calendarFragment = new CalendarFragment();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, calendarFragment, "CalendarFragment")
                .addToBackStack(null)
                .commit();
    }

    private void openCategoriesPage() {
        CategoriesFragment categoriesFragment = new CategoriesFragment();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, categoriesFragment, "CategoriesFragment")
                .addToBackStack(null)
                .commit();
    }
}
