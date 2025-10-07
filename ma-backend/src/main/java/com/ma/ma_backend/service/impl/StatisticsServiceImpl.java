package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.*;
import com.ma.ma_backend.dto.UserStatisticsDto;
import com.ma.ma_backend.repository.BossBattleRepository;
import com.ma.ma_backend.repository.TaskInstanceRepository;
import com.ma.ma_backend.repository.TaskRepository;
import com.ma.ma_backend.service.intr.StatisticsService;
import com.ma.ma_backend.service.intr.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    private final TaskRepository taskRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    private final BossBattleRepository bossBattleRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public UserStatisticsDto getUserStatistics() {
        try {
//            log.info("Starting getUserStatistics");
            User user = userService.getLogedInUser();
            Long userId = user.getId();
//            log.info("User ID: {}", userId);

//            log.info("Calculating activeDaysStreak");
            Integer activeDaysStreak = calculateActiveDaysStreak(userId);
//            log.info("activeDaysStreak: {}", activeDaysStreak);

//            log.info("Calculating tasksCreated");
            Integer tasksCreated = calculateTasksCreated(userId);
//            log.info("tasksCreated: {}", tasksCreated);

//            log.info("Calculating tasksCompleted");
            Integer tasksCompleted = calculateTasksCompleted(userId);
//            log.info("tasksCompleted: {}", tasksCompleted);

//            log.info("Calculating tasksPending");
            Integer tasksPending = calculateTasksPending(userId);
//            log.info("tasksPending: {}", tasksPending);

//            log.info("Calculating tasksCancelled");
            Integer tasksCancelled = calculateTasksCancelled(userId);
//            log.info("tasksCancelled: {}", tasksCancelled);

//            log.info("Calculating longestCompletionStreak");
            Integer longestCompletionStreak = calculateLongestCompletionStreak(userId);
//            log.info("longestCompletionStreak: {}", longestCompletionStreak);

//            log.info("Calculating tasksByCategory");
            Map<String, Integer> tasksByCategory = calculateTasksByCategory(userId);
//            log.info("tasksByCategory: {}", tasksByCategory);

//            log.info("Calculating averageDifficultyHistory");
            List<UserStatisticsDto.DifficultyDataPoint> averageDifficultyHistory = calculateAverageDifficultyHistory(userId);
//            log.info("averageDifficultyHistory size: {}", averageDifficultyHistory.size());

//            log.info("Calculating xpLast7Days");
            List<UserStatisticsDto.XpDataPoint> xpLast7Days = calculateXpLast7Days(userId);
//            log.info("xpLast7Days size: {}", xpLast7Days.size());

//            log.info("Calculating specialMissionsStarted");
            Integer specialMissionsStarted = calculateSpecialMissionsStarted(userId);
//            log.info("specialMissionsStarted: {}", specialMissionsStarted);

//            log.info("Calculating specialMissionsCompleted");
            Integer specialMissionsCompleted = calculateSpecialMissionsCompleted(userId);
//            log.info("specialMissionsCompleted: {}", specialMissionsCompleted);

//            log.info("Building UserStatisticsDto");
            return UserStatisticsDto.builder()
                    .activeDaysStreak(activeDaysStreak)
                    .tasksCreated(tasksCreated)
                    .tasksCompleted(tasksCompleted)
                    .tasksPending(tasksPending)
                    .tasksCancelled(tasksCancelled)
                    .longestCompletionStreak(longestCompletionStreak)
                    .tasksByCategory(tasksByCategory)
                    .averageDifficultyHistory(averageDifficultyHistory)
                    .xpLast7Days(xpLast7Days)
                    .specialMissionsStarted(specialMissionsStarted)
                    .specialMissionsCompleted(specialMissionsCompleted)
                    .build();
        } catch (Exception e) {
            log.error("Error in getUserStatistics", e);
            throw e;
        }
    }

    private Integer calculateActiveDaysStreak(Long userId) {
        List<TaskInstance> allInstances = taskInstanceRepository.findByTaskUserId(userId);

        if (allInstances.isEmpty()) {
            return 0;
        }

        // Get distinct dates when user had any task activity (completed)
        Set<LocalDate> activeDates = allInstances.stream()
                .filter(ti -> ti.getCompletedAt() != null)
                .map(ti -> ti.getCompletedAt().toLocalDate())
                .collect(Collectors.toSet());

        if (activeDates.isEmpty()) {
            return 0;
        }

        // Sort dates in descending order
        List<LocalDate> sortedDates = activeDates.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // Check if today or yesterday is in the list
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        if (!sortedDates.contains(today) && !sortedDates.contains(yesterday)) {
            return 0;
        }

        // Count consecutive days from today backwards
        int streak = 0;
        LocalDate currentDate = sortedDates.contains(today) ? today : yesterday;

        for (LocalDate date : sortedDates) {
            if (date.equals(currentDate)) {
                streak++;
                currentDate = currentDate.minusDays(1);
            }
        }

        return streak;
    }

    private Integer calculateTasksCreated(Long userId) {
        return taskRepository.findByUserId(userId).size();
    }

    private Integer calculateTasksCompleted(Long userId) {
        return taskInstanceRepository.findByTaskUserIdAndStatus(userId, TaskStatus.COMPLETED).size();
    }

    private Integer calculateTasksPending(Long userId) {
        return taskInstanceRepository.findByTaskUserIdAndStatus(userId, TaskStatus.ACTIVE).size();
    }

    private Integer calculateTasksCancelled(Long userId) {
        return taskInstanceRepository.findByTaskUserIdAndStatus(userId, TaskStatus.CANCELLED).size();
    }

    private Integer calculateLongestCompletionStreak(Long userId) {
        List<TaskInstance> completedInstances = taskInstanceRepository
                .findByTaskUserIdAndStatus(userId, TaskStatus.COMPLETED);

        if (completedInstances.isEmpty()) {
            return 0;
        }

        // Group by date
        Map<LocalDate, List<TaskInstance>> instancesByDate = completedInstances.stream()
                .filter(ti -> ti.getCompletedAt() != null)
                .collect(Collectors.groupingBy(ti -> ti.getCompletedAt().toLocalDate()));

        // Get all dates and sort them
        List<LocalDate> dates = new ArrayList<>(instancesByDate.keySet());
        Collections.sort(dates);

        if (dates.isEmpty()) {
            return 0;
        }

        int maxStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < dates.size(); i++) {
            LocalDate prevDate = dates.get(i - 1);
            LocalDate currDate = dates.get(i);

            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(prevDate, currDate);

            if (daysBetween == 1) {
                // Consecutive day
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                // Gap in dates - streak continues as long as there are no failed tasks
                // For simplicity, we'll just reset
                currentStreak = 1;
            }
        }

        return maxStreak;
    }

    private Map<String, Integer> calculateTasksByCategory(Long userId) {
        List<TaskInstance> completedInstances = taskInstanceRepository
                .findByTaskUserIdAndStatus(userId, TaskStatus.COMPLETED);

        if (completedInstances.isEmpty()) {
            return new HashMap<>();
        }

        return completedInstances.stream()
                .filter(ti -> ti.getTask() != null && ti.getTask().getCategory() != null)
                .collect(Collectors.groupingBy(
                        ti -> ti.getTask().getCategory().getName(),
                        Collectors.summingInt(ti -> 1)
                ));
    }

    private List<UserStatisticsDto.DifficultyDataPoint> calculateAverageDifficultyHistory(Long userId) {
        List<TaskInstance> completedInstances = taskInstanceRepository
                .findByTaskUserIdAndStatus(userId, TaskStatus.COMPLETED);

        // Group by date and calculate average difficulty
        Map<LocalDate, List<TaskInstance>> instancesByDate = completedInstances.stream()
                .filter(ti -> ti.getCompletedAt() != null)
                .collect(Collectors.groupingBy(ti -> ti.getCompletedAt().toLocalDate()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return instancesByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<TaskInstance> instances = entry.getValue();

                    double avgDifficulty = instances.stream()
                            .mapToInt(ti -> ti.getTask().getDifficulty().getXp())
                            .average()
                            .orElse(0.0);

                    return UserStatisticsDto.DifficultyDataPoint.builder()
                            .date(date.format(formatter))
                            .averageDifficulty((float) avgDifficulty)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<UserStatisticsDto.XpDataPoint> calculateXpLast7Days(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.minusDays(6).atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<TaskInstance> completedInstances = taskInstanceRepository
                .findByTaskUserIdAndStatus(userId, TaskStatus.COMPLETED).stream()
                .filter(ti -> ti.getCompletedAt() != null)
                .filter(ti -> ti.getCompletedAt().isAfter(startOfDay) && ti.getCompletedAt().isBefore(endOfDay))
                .collect(Collectors.toList());

        // Group by date
        Map<LocalDate, Integer> xpByDate = completedInstances.stream()
                .collect(Collectors.groupingBy(
                        ti -> ti.getCompletedAt().toLocalDate(),
                        Collectors.summingInt(ti -> ti.getXpAwarded() ? ti.getXpAmount() : 0)
                ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<UserStatisticsDto.XpDataPoint> result = new ArrayList<>();

        // Ensure all 7 days are present
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int xp = xpByDate.getOrDefault(date, 0);

            result.add(UserStatisticsDto.XpDataPoint.builder()
                    .date(date.format(formatter))
                    .xp(xp)
                    .build());
        }

        return result;
    }

    private Integer calculateSpecialMissionsStarted(Long userId) {
        User user = userService.getLogedInUser();
        if (user.getGameStats() == null) {
            return 0;
        }

        return (int) bossBattleRepository.countByUserGameStatsId(user.getGameStats().getId());
    }

    private Integer calculateSpecialMissionsCompleted(Long userId) {
        User user = userService.getLogedInUser();
        if (user.getGameStats() == null) {
            return 0;
        }

        return (int) bossBattleRepository.countByUserGameStatsIdAndStatus(
                user.getGameStats().getId(),
                BattleStatus.WON
        );
    }
}