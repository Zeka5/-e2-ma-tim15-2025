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

import com.example.ma_mobile.models.Category;
import com.example.ma_mobile.repository.CategoryRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CategoriesFragment extends Fragment {

    private LinearLayout llCategoriesList;
    private TextView tvNoCategories;
    private FloatingActionButton fabAddCategory;
    private CategoryRepository categoryRepository;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryRepository = new CategoryRepository(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        loadCategories();
    }

    private void initializeViews(View view) {
        llCategoriesList = view.findViewById(R.id.ll_categories_list);
        tvNoCategories = view.findViewById(R.id.tv_no_categories);
        fabAddCategory = view.findViewById(R.id.fab_add_category);

        fabAddCategory.setOnClickListener(v -> openAddCategoryDialog());
    }

    private void loadCategories() {
        categoryRepository.getAllCategories(new CategoryRepository.CategoryListCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                if (categories.isEmpty()) {
                    showNoCategories();
                } else {
                    displayCategories(categories);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                showNoCategories();
            }
        });
    }

    private void showNoCategories() {
        if (tvNoCategories != null && llCategoriesList != null) {
            tvNoCategories.setVisibility(View.VISIBLE);
            llCategoriesList.setVisibility(View.GONE);
        }
    }

    private void displayCategories(List<Category> categories) {
        if (tvNoCategories != null && llCategoriesList != null) {
            tvNoCategories.setVisibility(View.GONE);
            llCategoriesList.setVisibility(View.VISIBLE);
            llCategoriesList.removeAllViews();

            for (Category category : categories) {
                View categoryView = createCategoryView(category);
                llCategoriesList.addView(categoryView);
            }
        }
    }

    private View createCategoryView(Category category) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
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

        // Color indicator
        View colorView = new View(getContext());
        GradientDrawable colorDrawable = new GradientDrawable();
        try {
            colorDrawable.setColor(Color.parseColor(category.getColor()));
        } catch (IllegalArgumentException e) {
            colorDrawable.setColor(Color.GRAY);
        }
        colorDrawable.setCornerRadius(8);
        colorView.setBackground(colorDrawable);

        LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(40, 40);
        colorParams.setMargins(0, 0, 16, 0);
        colorView.setLayoutParams(colorParams);
        itemLayout.addView(colorView);

        // Category name
        TextView nameView = new TextView(getContext());
        nameView.setText(category.getName());
        nameView.setTextSize(16);
        nameView.setTextColor(Color.BLACK);
        nameView.setTypeface(nameView.getTypeface(), android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        nameView.setLayoutParams(nameParams);
        itemLayout.addView(nameView);

        // Actions
        itemLayout.setOnClickListener(v -> openEditCategoryDialog(category));
        itemLayout.setOnLongClickListener(v -> {
            confirmDeleteCategory(category);
            return true;
        });

        return itemLayout;
    }

    private void openAddCategoryDialog() {
        AddEditCategoryDialog dialog = new AddEditCategoryDialog(requireContext(), null,
                new AddEditCategoryDialog.CategoryDialogListener() {
                    @Override
                    public void onCategorySaved() {
                        loadCategories();
                    }
                });
        dialog.show();
    }

    private void openEditCategoryDialog(Category category) {
        AddEditCategoryDialog dialog = new AddEditCategoryDialog(requireContext(), category,
                new AddEditCategoryDialog.CategoryDialogListener() {
                    @Override
                    public void onCategorySaved() {
                        loadCategories();
                    }
                });
        dialog.show();
    }

    private void confirmDeleteCategory(Category category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete category '" + category.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> deleteCategory(category))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCategory(Category category) {
        categoryRepository.deleteCategory(category.getId(),
                new CategoryRepository.DeleteCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show();
                        loadCategories();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
