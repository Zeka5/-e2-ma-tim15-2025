package com.example.ma_mobile;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ma_mobile.models.Task;
import com.example.ma_mobile.repository.TaskRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private LinearLayout llCalendarTasksList;
    private TextView tvNoTasksForDate;
    private TextView tvSelectedDate;
    private TaskRepository taskRepository;
    private List<Task> allTasks = new ArrayList<>();
    private String selectedDate;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskRepository = new TaskRepository(requireContext());

        // Set initial selected date to today
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        loadAllTasks();
    }

    private void initializeViews(View view) {
        calendarView = view.findViewById(R.id.calendarView);
        llCalendarTasksList = view.findViewById(R.id.ll_calendar_tasks_list);
        tvNoTasksForDate = view.findViewById(R.id.tv_no_tasks_for_date);
        tvSelectedDate = view.findViewById(R.id.tv_selected_date);

        // Set up calendar date change listener
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // month is 0-indexed, so add 1
                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                updateSelectedDateLabel();
                displayTasksForSelectedDate();
            }
        });

        updateSelectedDateLabel();
    }

    private void updateSelectedDateLabel() {
        if (tvSelectedDate != null && selectedDate != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
                Date date = inputFormat.parse(selectedDate);
                if (date != null) {
                    tvSelectedDate.setText("Tasks for " + outputFormat.format(date));
                }
            } catch (Exception e) {
                tvSelectedDate.setText("Tasks for " + selectedDate);
            }
        }
    }

    private void loadAllTasks() {
        taskRepository.getAllTasks(new TaskRepository.TaskListCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                allTasks = tasks;
                displayTasksForSelectedDate();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Error loading tasks: " + error, Toast.LENGTH_SHORT).show();
                allTasks = new ArrayList<>();
                displayTasksForSelectedDate();
            }
        });
    }

    private void displayTasksForSelectedDate() {
        if (llCalendarTasksList == null || tvNoTasksForDate == null) {
            return;
        }

        llCalendarTasksList.removeAllViews();

        // Filter tasks for the selected date
        List<Task> tasksForDate = filterTasksForDate(selectedDate);

        if (tasksForDate.isEmpty()) {
            tvNoTasksForDate.setVisibility(View.VISIBLE);
            llCalendarTasksList.setVisibility(View.GONE);
        } else {
            tvNoTasksForDate.setVisibility(View.GONE);
            llCalendarTasksList.setVisibility(View.VISIBLE);

            // Sort tasks by start time if available
            tasksForDate.sort((t1, t2) -> {
                String time1 = extractTime(t1.getStartDate());
                String time2 = extractTime(t2.getStartDate());
                return time1.compareTo(time2);
            });

            for (Task task : tasksForDate) {
                View taskView = createCalendarTaskView(task);
                llCalendarTasksList.addView(taskView);
            }
        }
    }

    private List<Task> filterTasksForDate(String dateStr) {
        List<Task> filteredTasks = new ArrayList<>();

        for (Task task : allTasks) {
            if (isTaskOnDate(task, dateStr)) {
                filteredTasks.add(task);
            }
        }

        return filteredTasks;
    }

    private boolean isTaskOnDate(Task task, String dateStr) {
        // Check if task's startDate matches the selected date
        if (task.getStartDate() != null) {
            String taskDate = task.getStartDate().substring(0, Math.min(10, task.getStartDate().length()));
            if (taskDate.equals(dateStr)) {
                return true;
            }
        }

        // For recurring tasks, check if the date falls within the recurrence pattern
        if (task.getIsRecurring() != null && task.getIsRecurring() && task.getStartDate() != null) {
            return isRecurringTaskOnDate(task, dateStr);
        }

        return false;
    }

    private boolean isRecurringTaskOnDate(Task task, String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date selectedDateObj = sdf.parse(dateStr);
            Date taskStartDate = sdf.parse(task.getStartDate().substring(0, 10));

            if (selectedDateObj == null || taskStartDate == null) {
                return false;
            }

            // Check if selected date is before task start date
            if (selectedDateObj.before(taskStartDate)) {
                return false;
            }

            // Check if selected date is after task end date (if specified)
            if (task.getEndDate() != null && !task.getEndDate().isEmpty()) {
                Date taskEndDate = sdf.parse(task.getEndDate().substring(0, 10));
                if (taskEndDate != null && selectedDateObj.after(taskEndDate)) {
                    return false;
                }
            }

            // Calculate the difference in days
            long diffInMillis = selectedDateObj.getTime() - taskStartDate.getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            String recurrenceUnit = task.getRecurrenceUnit();
            Integer recurrenceInterval = task.getRecurrenceInterval();

            if (recurrenceInterval == null || recurrenceInterval == 0) {
                return false;
            }

            if ("DAY".equalsIgnoreCase(recurrenceUnit)) {
                return diffInDays % recurrenceInterval == 0;
            } else if ("WEEK".equalsIgnoreCase(recurrenceUnit)) {
                long diffInWeeks = diffInDays / 7;
                return (diffInDays % 7 == 0) && (diffInWeeks % recurrenceInterval == 0);
            } else if ("MONTH".equalsIgnoreCase(recurrenceUnit)) {
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(taskStartDate);
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.setTime(selectedDateObj);

                int monthsDiff = (selectedCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)) * 12
                        + (selectedCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH));

                return (selectedCal.get(Calendar.DAY_OF_MONTH) == startCal.get(Calendar.DAY_OF_MONTH))
                        && (monthsDiff % recurrenceInterval == 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private String extractTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.length() < 16) {
            return "00:00";
        }
        try {
            // Extract time from ISO format (yyyy-MM-ddTHH:mm:ss)
            if (dateTimeStr.contains("T")) {
                return dateTimeStr.substring(11, 16);
            }
            return "00:00";
        } catch (Exception e) {
            return "00:00";
        }
    }

    private View createCalendarTaskView(Task task) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(16, 16, 16, 16);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(12);
        background.setStroke(2, Color.parseColor("#E0E0E0"));
        itemLayout.setBackground(background);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 12);
        itemLayout.setLayoutParams(layoutParams);

        // Time slot (left side)
        TextView timeView = new TextView(getContext());
        String timeSlot = extractTime(task.getStartDate());
        timeView.setText(timeSlot);
        timeView.setTextSize(14);
        timeView.setTextColor(Color.parseColor("#424242"));
        timeView.setTypeface(timeView.getTypeface(), android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.2f
        );
        timeView.setLayoutParams(timeParams);
        itemLayout.addView(timeView);

        // Category color bar
        View colorBar = new View(getContext());
        GradientDrawable colorDrawable = new GradientDrawable();
        try {
            colorDrawable.setColor(Color.parseColor(task.getCategoryColor()));
        } catch (IllegalArgumentException e) {
            colorDrawable.setColor(Color.GRAY);
        }
        colorDrawable.setCornerRadius(4);
        colorBar.setBackground(colorDrawable);

        LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(8, LinearLayout.LayoutParams.MATCH_PARENT);
        colorParams.setMargins(8, 0, 12, 0);
        colorBar.setLayoutParams(colorParams);
        itemLayout.addView(colorBar);

        // Task details (right side)
        LinearLayout detailsLayout = new LinearLayout(getContext());
        detailsLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.8f
        );
        detailsLayout.setLayoutParams(detailsParams);

        // Task title
        TextView titleView = new TextView(getContext());
        titleView.setText(task.getTitle());
        titleView.setTextSize(16);
        titleView.setTextColor(Color.BLACK);
        titleView.setTypeface(titleView.getTypeface(), android.graphics.Typeface.BOLD);
        detailsLayout.addView(titleView);

        // Category name with color chip
        LinearLayout categoryLayout = new LinearLayout(getContext());
        categoryLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams categoryLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        categoryLayoutParams.setMargins(0, 4, 0, 0);
        categoryLayout.setLayoutParams(categoryLayoutParams);

        TextView categoryView = new TextView(getContext());
        categoryView.setText(task.getCategoryName());
        categoryView.setTextSize(12);
        categoryView.setTextColor(Color.parseColor("#757575"));
        categoryLayout.addView(categoryView);

        detailsLayout.addView(categoryLayout);

        // Description (if available)
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            TextView descView = new TextView(getContext());
            descView.setText(task.getDescription());
            descView.setTextSize(12);
            descView.setTextColor(Color.parseColor("#9E9E9E"));
            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            descParams.setMargins(0, 2, 0, 0);
            descView.setLayoutParams(descParams);
            detailsLayout.addView(descView);
        }

        itemLayout.addView(detailsLayout);

        // Click listener to open task details
        itemLayout.setOnClickListener(v -> openTaskDetails(task));

        return itemLayout;
    }

    private void openTaskDetails(Task task) {
        TaskDetailsDialog dialog = new TaskDetailsDialog(requireContext(), task,
                new TaskDetailsDialog.TaskDetailsListener() {
                    @Override
                    public void onTaskUpdated() {
                        loadAllTasks();
                    }

                    @Override
                    public void onTaskDeleted() {
                        loadAllTasks();
                    }
                });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload tasks when fragment is resumed
        loadAllTasks();
    }
}
