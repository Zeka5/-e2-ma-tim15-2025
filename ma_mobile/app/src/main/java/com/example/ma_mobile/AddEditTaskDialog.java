package com.example.ma_mobile;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ma_mobile.models.Category;
import com.example.ma_mobile.models.CreateTaskRequest;
import com.example.ma_mobile.models.Task;
import com.example.ma_mobile.repository.CategoryRepository;
import com.example.ma_mobile.repository.TaskRepository;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditTaskDialog extends Dialog {

    private EditText etTaskTitle;
    private EditText etTaskDescription;
    private Spinner spinnerCategory;
    private Spinner spinnerDifficulty;
    private Spinner spinnerImportance;
    private CheckBox cbRepeating;
    private Button btnSelectStartDate;
    private TextView tvEndDateLabel;
    private Button btnSelectEndDateSingle;
    private LinearLayout llRepeatOptions;
    private EditText etRepeatInterval;
    private Spinner spinnerRepeatUnit;
    private Button btnSelectEndDateRepeat;
    private Button btnSave;
    private Button btnCancel;

    private Task task;
    private TaskRepository taskRepository;
    private CategoryRepository categoryRepository;
    private TaskDialogListener listener;

    private List<Category> categories = new ArrayList<>();
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public interface TaskDialogListener {
        void onTaskSaved();
    }

    public AddEditTaskDialog(@NonNull Context context, Task task, TaskDialogListener listener) {
        super(context);
        this.task = task;
        this.listener = listener;
        this.taskRepository = new TaskRepository(context);
        this.categoryRepository = new CategoryRepository(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_edit_task);

        if (getWindow() != null) {
            getWindow().setLayout(
                (int)(getContext().getResources().getDisplayMetrics().widthPixels * 0.95),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        initializeViews();
        setupSpinners();
        loadCategories();
        setupButtons();
        setupRepeatToggle();

        if (task != null) {
            populateTaskData();
            lockEditModeFields();
        }
    }

    private void initializeViews() {
        etTaskTitle = findViewById(R.id.et_task_title);
        etTaskDescription = findViewById(R.id.et_task_description);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        spinnerImportance = findViewById(R.id.spinner_importance);
        cbRepeating = findViewById(R.id.cb_repeating);
        btnSelectStartDate = findViewById(R.id.btn_select_start_date);
        tvEndDateLabel = findViewById(R.id.tv_end_date_label);
        btnSelectEndDateSingle = findViewById(R.id.btn_select_end_date_single);
        llRepeatOptions = findViewById(R.id.ll_repeat_options);
        etRepeatInterval = findViewById(R.id.et_repeat_interval);
        spinnerRepeatUnit = findViewById(R.id.spinner_repeat_unit);
        btnSelectEndDateRepeat = findViewById(R.id.btn_select_end_date);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupSpinners() {
        // Difficulty spinner
        String[] difficulties = {"Very Easy (1 XP)", "Easy (3 XP)", "Hard (7 XP)", "Extremely Hard (20 XP)"};
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, difficulties);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);

        // Importance spinner
        String[] importances = {"Normal (1 XP)", "Important (3 XP)", "Extremely Important (10 XP)", "Special (100 XP)"};
        ArrayAdapter<String> importanceAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, importances);
        importanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImportance.setAdapter(importanceAdapter);

        // Repeat unit spinner
        String[] repeatUnits = {"Day", "Week"};
        ArrayAdapter<String> repeatUnitAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, repeatUnits);
        repeatUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeatUnit.setAdapter(repeatUnitAdapter);
    }

    private void loadCategories() {
        categoryRepository.getAllCategories(new CategoryRepository.CategoryListCallback() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                categories = categoryList;
                List<String> categoryNames = new ArrayList<>();
                for (Category category : categories) {
                    categoryNames.add(category.getName());
                }

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, categoryNames);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);

                // Set category if editing task
                if (task != null && task.getCategoryId() != null) {
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).getId().equals(task.getCategoryId())) {
                            spinnerCategory.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Failed to load categories: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupButtons() {
        btnSelectStartDate.setOnClickListener(v -> showDateTimePicker(time -> {
            startDateTime = time;
            btnSelectStartDate.setText(formatDateTime(time));
        }));

        btnSelectEndDateSingle.setOnClickListener(v -> showDateTimePicker(time -> {
            endDateTime = time;
            btnSelectEndDateSingle.setText(formatDateTime(time));
        }));

        btnSelectEndDateRepeat.setOnClickListener(v -> showDateTimePicker(time -> {
            endDateTime = time;
            btnSelectEndDateRepeat.setText(formatDateTime(time));
        }));

        btnSave.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void setupRepeatToggle() {
        cbRepeating.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llRepeatOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            // Show/hide single task end date
            tvEndDateLabel.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            btnSelectEndDateSingle.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });
    }

    private void showDateTimePicker(DateTimeCallback callback) {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            new TimePickerDialog(getContext(), (timeView, hourOfDay, minute) -> {
                LocalDateTime selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute);
                callback.onDateTimeSelected(selectedDateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private interface DateTimeCallback {
        void onDateTimeSelected(LocalDateTime dateTime);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
            return dateTime.format(formatter);
        }
        return dateTime.toString();
    }

    private void saveTask() {
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Please enter task title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerCategory.getSelectedItemPosition() < 0 || categories.isEmpty()) {
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDateTime == null) {
            Toast.makeText(getContext(), "Please select start date/time", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isRecurring = cbRepeating.isChecked();

        if (isRecurring) {
            String intervalStr = etRepeatInterval.getText().toString().trim();
            if (intervalStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter repeat interval", Toast.LENGTH_SHORT).show();
                return;
            }

            if (endDateTime == null) {
                Toast.makeText(getContext(), "Please select end date", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle(title);
        request.setDescription(description.isEmpty() ? null : description);
        request.setCategoryId(categories.get(spinnerCategory.getSelectedItemPosition()).getId());
        request.setDifficulty(getDifficultyValue(spinnerDifficulty.getSelectedItemPosition()));
        request.setImportance(getImportanceValue(spinnerImportance.getSelectedItemPosition()));
        request.setIsRecurring(isRecurring);

        // Set startDate from startDateTime
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            request.setStartDate(startDateTime.toString());
        }

        if (isRecurring) {
            request.setRecurrenceInterval(Integer.parseInt(etRepeatInterval.getText().toString().trim()));
            request.setRecurrenceUnit(spinnerRepeatUnit.getSelectedItemPosition() == 0 ? "DAY" : "WEEK");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                request.setEndDate(endDateTime.toString());
            }
        } else if (endDateTime != null) {
            // Set end date for non-recurring tasks (optional)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                request.setEndDate(endDateTime.toString());
            }
        }

        if (task == null) {
            taskRepository.createTask(request, new TaskRepository.TaskCallback() {
                @Override
                public void onSuccess(Task task) {
                    Toast.makeText(getContext(), "Task created", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onTaskSaved();
                    }
                    dismiss();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            taskRepository.updateTask(task.getId(), request, new TaskRepository.TaskCallback() {
                @Override
                public void onSuccess(Task task) {
                    Toast.makeText(getContext(), "Task updated", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onTaskSaved();
                    }
                    dismiss();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private String getDifficultyValue(int position) {
        switch (position) {
            case 0: return "VERY_EASY";
            case 1: return "EASY";
            case 2: return "HARD";
            case 3: return "EXTREMELY_HARD";
            default: return "EASY";
        }
    }

    private String getImportanceValue(int position) {
        switch (position) {
            case 0: return "NORMAL";
            case 1: return "IMPORTANT";
            case 2: return "EXTREMELY_IMPORTANT";
            case 3: return "SPECIAL";
            default: return "NORMAL";
        }
    }

    private void populateTaskData() {
        TextView tvDialogTitle = findViewById(R.id.tv_dialog_title);
        tvDialogTitle.setText("Edit Task");

        etTaskTitle.setText(task.getTitle());
        etTaskDescription.setText(task.getDescription());

        // Set difficulty
        int difficultyPosition = getDifficultyPosition(task.getDifficulty());
        spinnerDifficulty.setSelection(difficultyPosition);

        // Set importance
        int importancePosition = getImportancePosition(task.getImportance());
        spinnerImportance.setSelection(importancePosition);

        // Set category (will be set after categories are loaded)
        // Set recurring checkbox
        if (task.getIsRecurring() != null && task.getIsRecurring()) {
            cbRepeating.setChecked(true);
            if (task.getRecurrenceInterval() != null) {
                etRepeatInterval.setText(String.valueOf(task.getRecurrenceInterval()));
            }
            if (task.getRecurrenceUnit() != null) {
                spinnerRepeatUnit.setSelection(task.getRecurrenceUnit().equals("DAY") ? 0 : 1);
            }
        }

        // Parse and set dates
        try {
            if (task.getStartDate() != null && !task.getStartDate().isEmpty()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startDateTime = LocalDateTime.parse(task.getStartDate());
                    btnSelectStartDate.setText(formatDateTime(startDateTime));
                }
            }

            if (task.getEndDate() != null && !task.getEndDate().isEmpty()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    endDateTime = LocalDateTime.parse(task.getEndDate());
                    if (task.getIsRecurring() != null && task.getIsRecurring()) {
                        btnSelectEndDateRepeat.setText(formatDateTime(endDateTime));
                    } else {
                        btnSelectEndDateSingle.setText(formatDateTime(endDateTime));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lockEditModeFields() {
        // Disable category selection
        spinnerCategory.setEnabled(false);
        spinnerCategory.setAlpha(0.5f);

        // Disable start date only (end date can be changed)
        btnSelectStartDate.setEnabled(false);
        btnSelectStartDate.setAlpha(0.5f);

        // Disable repeating checkbox and interval
        cbRepeating.setEnabled(false);
        cbRepeating.setAlpha(0.5f);
        etRepeatInterval.setEnabled(false);
        etRepeatInterval.setAlpha(0.5f);
        spinnerRepeatUnit.setEnabled(false);
        spinnerRepeatUnit.setAlpha(0.5f);
    }

    private int getDifficultyPosition(String difficulty) {
        if (difficulty == null) return 1;
        switch (difficulty) {
            case "VERY_EASY": return 0;
            case "EASY": return 1;
            case "HARD": return 2;
            case "EXTREMELY_HARD": return 3;
            default: return 1;
        }
    }

    private int getImportancePosition(String importance) {
        if (importance == null) return 0;
        switch (importance) {
            case "NORMAL": return 0;
            case "IMPORTANT": return 1;
            case "EXTREMELY_IMPORTANT": return 2;
            case "SPECIAL": return 3;
            default: return 0;
        }
    }
}
