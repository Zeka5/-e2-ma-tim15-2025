package com.example.ma_mobile;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ma_mobile.models.UserStatistics;
import com.example.ma_mobile.repository.StatisticsRepository;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private TextView tvActiveDaysStreak;
    private TextView tvLongestStreak;
    private TextView tvMissionsStarted;
    private TextView tvMissionsCompleted;
    private PieChart chartTaskOverview;
    private BarChart chartTasksByCategory;
    private LineChart chartAverageDifficulty;
    private LineChart chartXpLast7Days;
    private ProgressBar progressBar;

    private StatisticsRepository statisticsRepository;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statisticsRepository = new StatisticsRepository(getContext());
        initializeViews(view);
        loadStatistics();
    }

    private void initializeViews(View view) {
        tvActiveDaysStreak = view.findViewById(R.id.tv_active_days_streak);
        tvLongestStreak = view.findViewById(R.id.tv_longest_streak);
        tvMissionsStarted = view.findViewById(R.id.tv_missions_started);
        tvMissionsCompleted = view.findViewById(R.id.tv_missions_completed);
        chartTaskOverview = view.findViewById(R.id.chart_task_overview);
        chartTasksByCategory = view.findViewById(R.id.chart_tasks_by_category);
        chartAverageDifficulty = view.findViewById(R.id.chart_average_difficulty);
        chartXpLast7Days = view.findViewById(R.id.chart_xp_last_7_days);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void loadStatistics() {
        showLoading(true);

        statisticsRepository.getUserStatistics(new StatisticsRepository.StatisticsCallback() {
            @Override
            public void onSuccess(UserStatistics statistics) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        displayStatistics(statistics);
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showToast("Failed to load statistics: " + error);
                        // Show mock data as fallback
                        UserStatistics mockStats = createMockStatistics();
                        displayStatistics(mockStats);
                    });
                }
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private UserStatistics createMockStatistics() {
        UserStatistics stats = new UserStatistics();

        // Basic stats
        stats.setActiveDaysStreak(34);
        stats.setTasksCreated(50);
        stats.setTasksCompleted(32);
        stats.setTasksPending(10);
        stats.setTasksCancelled(8);
        stats.setLongestCompletionStreak(12);
        stats.setSpecialMissionsStarted(8);
        stats.setSpecialMissionsCompleted(5);

        // Tasks by category
        Map<String, Integer> tasksByCategory = new HashMap<>();
        tasksByCategory.put("Health", 10);
        tasksByCategory.put("Learning", 14);
        tasksByCategory.put("Work", 8);
        tasksByCategory.put("Exercise", 12);
        tasksByCategory.put("Hobbies", 6);
        stats.setTasksByCategory(tasksByCategory);

        // Average difficulty history (last 7 days)
        List<UserStatistics.DifficultyDataPoint> difficultyHistory = new ArrayList<>();
        difficultyHistory.add(new UserStatistics.DifficultyDataPoint("Mon", 2.5f));
        difficultyHistory.add(new UserStatistics.DifficultyDataPoint("Tue", 3.0f));
        difficultyHistory.add(new UserStatistics.DifficultyDataPoint("Wed", 2.8f));
        difficultyHistory.add(new UserStatistics.DifficultyDataPoint("Thu", 3.2f));
        difficultyHistory.add(new UserStatistics.DifficultyDataPoint("Fri", 2.9f));
        difficultyHistory.add(new UserStatistics.DifficultyDataPoint("Sat", 3.5f));
        difficultyHistory.add(new UserStatistics.DifficultyDataPoint("Sun", 3.1f));
        stats.setAverageDifficultyHistory(difficultyHistory);

        // XP last 7 days
        List<UserStatistics.XpDataPoint> xpHistory = new ArrayList<>();
        xpHistory.add(new UserStatistics.XpDataPoint("Mon", 120));
        xpHistory.add(new UserStatistics.XpDataPoint("Tue", 150));
        xpHistory.add(new UserStatistics.XpDataPoint("Wed", 90));
        xpHistory.add(new UserStatistics.XpDataPoint("Thu", 180));
        xpHistory.add(new UserStatistics.XpDataPoint("Fri", 110));
        xpHistory.add(new UserStatistics.XpDataPoint("Sat", 200));
        xpHistory.add(new UserStatistics.XpDataPoint("Sun", 160));
        stats.setXpLast7Days(xpHistory);

        return stats;
    }

    private void displayStatistics(UserStatistics stats) {
        if (stats == null) {
            showToast("No statistics data available");
            return;
        }

        // Display basic stats with null checks
        tvActiveDaysStreak.setText((stats.getActiveDaysStreak() != null ? stats.getActiveDaysStreak() : 0) + " days");
        tvLongestStreak.setText((stats.getLongestCompletionStreak() != null ? stats.getLongestCompletionStreak() : 0) + " days");
        tvMissionsStarted.setText(String.valueOf(stats.getSpecialMissionsStarted() != null ? stats.getSpecialMissionsStarted() : 0));
        tvMissionsCompleted.setText(String.valueOf(stats.getSpecialMissionsCompleted() != null ? stats.getSpecialMissionsCompleted() : 0));

        // Setup charts
        setupTaskOverviewChart(stats);
        setupTasksByCategoryChart(stats);
        setupAverageDifficultyChart(stats);
        setupXpChart(stats);
    }

    private void setupTaskOverviewChart(UserStatistics stats) {
        int completed = stats.getTasksCompleted() != null ? stats.getTasksCompleted() : 0;
        int pending = stats.getTasksPending() != null ? stats.getTasksPending() : 0;
        int cancelled = stats.getTasksCancelled() != null ? stats.getTasksCancelled() : 0;

        List<PieEntry> entries = new ArrayList<>();
        if (completed > 0) entries.add(new PieEntry(completed, "Completed"));
        if (pending > 0) entries.add(new PieEntry(pending, "Pending"));
        if (cancelled > 0) entries.add(new PieEntry(cancelled, "Cancelled"));

        if (entries.isEmpty()) {
            chartTaskOverview.setVisibility(View.GONE);
            return;
        }
        chartTaskOverview.setVisibility(View.VISIBLE);

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
                Color.parseColor("#4CAF50"), // Green for completed
                Color.parseColor("#FFC107"), // Yellow for pending
                Color.parseColor("#F44336")  // Red for cancelled
        });
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        chartTaskOverview.setData(data);
        chartTaskOverview.getDescription().setEnabled(false);
        chartTaskOverview.setDrawHoleEnabled(true);
        chartTaskOverview.setHoleColor(Color.WHITE);
        chartTaskOverview.setHoleRadius(50f);
        chartTaskOverview.setTransparentCircleRadius(55f);
        chartTaskOverview.setEntryLabelColor(Color.BLACK);
        chartTaskOverview.setEntryLabelTextSize(12f);
        chartTaskOverview.getLegend().setTextSize(12f);
        chartTaskOverview.animateY(1000);
        chartTaskOverview.invalidate();
    }

    private void setupTasksByCategoryChart(UserStatistics stats) {
        Map<String, Integer> tasksByCategory = stats.getTasksByCategory();
        if (tasksByCategory == null || tasksByCategory.isEmpty()) {
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> entry : tasksByCategory.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Tasks Completed");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.8f);

        chartTasksByCategory.setData(data);
        chartTasksByCategory.getDescription().setEnabled(false);
        chartTasksByCategory.setFitBars(true);
        chartTasksByCategory.animateY(1000);

        // Configure X axis
        XAxis xAxis = chartTasksByCategory.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-45f);

        // Configure Y axes
        YAxis leftAxis = chartTasksByCategory.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);

        chartTasksByCategory.getAxisRight().setEnabled(false);
        chartTasksByCategory.getLegend().setEnabled(false);
        chartTasksByCategory.invalidate();
    }

    private void setupAverageDifficultyChart(UserStatistics stats) {
        List<UserStatistics.DifficultyDataPoint> difficultyHistory = stats.getAverageDifficultyHistory();
        if (difficultyHistory == null || difficultyHistory.isEmpty()) {
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < difficultyHistory.size(); i++) {
            UserStatistics.DifficultyDataPoint point = difficultyHistory.get(i);
            entries.add(new Entry(i, point.getAverageDifficulty()));
            labels.add(point.getDate());
        }

        LineDataSet dataSet = new LineDataSet(entries, "Average Difficulty");
        dataSet.setColor(Color.parseColor("#9C27B0"));
        dataSet.setCircleColor(Color.parseColor("#9C27B0"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#E1BEE7"));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData data = new LineData(dataSet);
        chartAverageDifficulty.setData(data);
        chartAverageDifficulty.getDescription().setEnabled(false);
        chartAverageDifficulty.animateX(1000);

        // Configure X axis
        XAxis xAxis = chartAverageDifficulty.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        // Configure Y axes
        YAxis leftAxis = chartAverageDifficulty.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(5f);

        chartAverageDifficulty.getAxisRight().setEnabled(false);
        chartAverageDifficulty.getLegend().setTextSize(12f);
        chartAverageDifficulty.invalidate();
    }

    private void setupXpChart(UserStatistics stats) {
        List<UserStatistics.XpDataPoint> xpHistory = stats.getXpLast7Days();
        if (xpHistory == null || xpHistory.isEmpty()) {
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < xpHistory.size(); i++) {
            UserStatistics.XpDataPoint point = xpHistory.get(i);
            entries.add(new Entry(i, point.getXp()));
            labels.add(point.getDate());
        }

        LineDataSet dataSet = new LineDataSet(entries, "XP Earned");
        dataSet.setColor(Color.parseColor("#FF5722"));
        dataSet.setCircleColor(Color.parseColor("#FF5722"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#FFCCBC"));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData data = new LineData(dataSet);
        chartXpLast7Days.setData(data);
        chartXpLast7Days.getDescription().setEnabled(false);
        chartXpLast7Days.animateX(1000);

        // Configure X axis
        XAxis xAxis = chartXpLast7Days.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        // Configure Y axes
        YAxis leftAxis = chartXpLast7Days.getAxisLeft();
        leftAxis.setAxisMinimum(0f);

        chartXpLast7Days.getAxisRight().setEnabled(false);
        chartXpLast7Days.getLegend().setTextSize(12f);
        chartXpLast7Days.invalidate();
    }
}
