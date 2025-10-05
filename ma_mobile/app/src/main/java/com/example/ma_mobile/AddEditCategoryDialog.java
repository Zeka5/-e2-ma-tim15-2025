package com.example.ma_mobile;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ma_mobile.models.Category;
import com.example.ma_mobile.models.CreateCategoryRequest;
import com.example.ma_mobile.models.UpdateCategoryRequest;
import com.example.ma_mobile.repository.CategoryRepository;

public class AddEditCategoryDialog extends Dialog {

    private EditText etCategoryName;
    private GridLayout gridColors;
    private Button btnSave;
    private Button btnCancel;

    private Category category;
    private String selectedColor;
    private CategoryRepository categoryRepository;
    private CategoryDialogListener listener;

    private static final String[] COLORS = {
            "#FF5252", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
            "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
            "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800",
            "#FF5722", "#795548", "#9E9E9E", "#607D8B", "#000000"
    };

    public interface CategoryDialogListener {
        void onCategorySaved();
    }

    public AddEditCategoryDialog(@NonNull Context context, Category category, CategoryDialogListener listener) {
        super(context);
        this.category = category;
        this.listener = listener;
        this.categoryRepository = new CategoryRepository(context);
        if (category != null) {
            this.selectedColor = category.getColor();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_edit_category);

        // Set dialog width to almost full screen and make background transparent for rounded corners
        if (getWindow() != null) {
            getWindow().setLayout(
                (int)(getContext().getResources().getDisplayMetrics().widthPixels * 0.95),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        initializeViews();
        setupColorPicker();
        setupButtons();

        if (category != null) {
            etCategoryName.setText(category.getName());
        }
    }

    private void initializeViews() {
        etCategoryName = findViewById(R.id.et_category_name);
        gridColors = findViewById(R.id.grid_colors);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupColorPicker() {
        gridColors.removeAllViews();

        for (String color : COLORS) {
            View colorView = createColorView(color);
            gridColors.addView(colorView);
        }
    }

    private View createColorView(String color) {
        View view = new View(getContext());

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.parseColor(color));

        if (color.equals(selectedColor)) {
            drawable.setStroke(8, Color.BLACK);
        }

        view.setBackground(drawable);

        int size = 85;
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = size;
        params.height = size;
        params.setMargins(10, 10, 10, 10);
        view.setLayoutParams(params);

        view.setOnClickListener(v -> {
            selectedColor = color;
            setupColorPicker(); // Refresh to show selection
        });

        return view;
    }

    private void setupButtons() {
        btnSave.setOnClickListener(v -> saveCategory());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void saveCategory() {
        String name = etCategoryName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Please enter category name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedColor == null || selectedColor.isEmpty()) {
            Toast.makeText(getContext(), "Please select a color", Toast.LENGTH_SHORT).show();
            return;
        }

        if (category == null) {
            // Create new category
            CreateCategoryRequest request = new CreateCategoryRequest(name, selectedColor);
            categoryRepository.createCategory(request, new CategoryRepository.CategoryCallback() {
                @Override
                public void onSuccess(Category category) {
                    Toast.makeText(getContext(), "Category created", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onCategorySaved();
                    }
                    dismiss();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // Update existing category
            UpdateCategoryRequest request = new UpdateCategoryRequest(name, selectedColor);
            categoryRepository.updateCategory(category.getId(), request, new CategoryRepository.CategoryCallback() {
                @Override
                public void onSuccess(Category category) {
                    Toast.makeText(getContext(), "Category updated", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onCategorySaved();
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
}
