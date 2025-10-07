package com.example.ma_mobile;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.adapter.GuildBossProgressAdapter;
import com.example.ma_mobile.models.Guild;
import com.example.ma_mobile.models.GuildBossBattle;
import com.example.ma_mobile.models.GuildBossMissionProgress;
import com.example.ma_mobile.models.GuildBossMissionSummary;
import com.example.ma_mobile.models.User;
import com.example.ma_mobile.repository.GuildBossBattleRepository;
import com.example.ma_mobile.repository.GuildRepository;
import com.example.ma_mobile.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GuildBossBattleFragment extends Fragment {

    private static final String TAG = "GuildBossBattleFragment";
    private static final String ARG_GUILD_ID = "guild_id";
    private static final int AUTO_REFRESH_INTERVAL = 30000; // 30 seconds

    private TextView tvBossName;
    private TextView tvBossHp;
    private ProgressBar bossProgressBar;
    private TextView tvProgressPercentage;
    private TextView tvTimeRemaining;
    private TextView tvMemberCount;
    private Button btnStartMission;
    private TextView tvMyDamage;
    private TextView tvMyBadge;
    private RecyclerView rvMemberProgress;
    private ProgressBar progressBar;
    private View layoutNoBattle;
    private View layoutActiveBattle;

    private GuildBossBattleRepository bossBattleRepository;
    private GuildRepository guildRepository;
    private UserRepository userRepository;
    private GuildBossProgressAdapter progressAdapter;

    private Long guildId;
    private Guild currentGuild;
    private Long currentUserId;
    private GuildBossBattle activeBattle;
    private Handler autoRefreshHandler;
    private Runnable autoRefreshRunnable;

    public GuildBossBattleFragment() {
        // Required empty public constructor
    }

    public static GuildBossBattleFragment newInstance(Long guildId) {
        GuildBossBattleFragment fragment = new GuildBossBattleFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GUILD_ID, guildId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            guildId = getArguments().getLong(ARG_GUILD_ID);
        }
        bossBattleRepository = new GuildBossBattleRepository(getContext());
        guildRepository = new GuildRepository(getContext());
        userRepository = new UserRepository(getContext());
        autoRefreshHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guild_boss_battle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        loadCurrentUser();
        loadGuildDetails();
        loadActiveBattle();
        setupAutoRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (autoRefreshHandler != null && autoRefreshRunnable != null) {
            autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
        }
    }

    private void initializeViews(View view) {
        tvBossName = view.findViewById(R.id.tv_boss_name);
        tvBossHp = view.findViewById(R.id.tv_boss_hp);
        bossProgressBar = view.findViewById(R.id.boss_progress_bar);
        tvProgressPercentage = view.findViewById(R.id.tv_progress_percentage);
        tvTimeRemaining = view.findViewById(R.id.tv_time_remaining);
        tvMemberCount = view.findViewById(R.id.tv_member_count);
        btnStartMission = view.findViewById(R.id.btn_start_mission);
        tvMyDamage = view.findViewById(R.id.tv_my_damage);
        tvMyBadge = view.findViewById(R.id.tv_my_badge);
        rvMemberProgress = view.findViewById(R.id.rv_member_progress);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutNoBattle = view.findViewById(R.id.layout_no_battle);
        layoutActiveBattle = view.findViewById(R.id.layout_active_battle);
    }

    private void setupRecyclerView() {
        progressAdapter = new GuildBossProgressAdapter();
        rvMemberProgress.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMemberProgress.setAdapter(progressAdapter);
    }

    private void setupListeners() {
        btnStartMission.setOnClickListener(v -> startMission());
    }

    private void setupAutoRefresh() {
        autoRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                if (activeBattle != null && "IN_PROGRESS".equals(activeBattle.getStatus())) {
                    loadProgressData();
                }
                autoRefreshHandler.postDelayed(this, AUTO_REFRESH_INTERVAL);
            }
        };
        autoRefreshHandler.postDelayed(autoRefreshRunnable, AUTO_REFRESH_INTERVAL);
    }

    private void loadCurrentUser() {
        userRepository.getCurrentUserProfile(new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                currentUserId = user.getId();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load current user: " + error);
            }
        });
    }

    private void loadGuildDetails() {
        progressBar.setVisibility(View.VISIBLE);

        guildRepository.getGuildById(guildId, new GuildRepository.GuildCallback() {
            @Override
            public void onSuccess(Guild guild) {
                currentGuild = guild;
                updateGuildInfo();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load guild: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadActiveBattle() {
        Log.d(TAG, "loadActiveBattle called with guildId: " + guildId);

        if (guildId == null || guildId == 0) {
            Log.e(TAG, "guildId is null or 0!");
            Toast.makeText(getContext(), "Invalid guild ID", Toast.LENGTH_SHORT).show();
            return;
        }

        bossBattleRepository.getActiveGuildBossBattle(guildId, new GuildBossBattleRepository.BattleCallback() {
            @Override
            public void onSuccess(GuildBossBattle battle) {
                Log.d(TAG, "loadActiveBattle SUCCESS - battle: " + (battle != null ? battle.getId() : "null"));
                progressBar.setVisibility(View.GONE);
                activeBattle = battle;

                if (battle == null) {
                    // No active battle
                    Log.d(TAG, "No active battle, showing 'no battle' layout");
                    layoutNoBattle.setVisibility(View.VISIBLE);
                    layoutActiveBattle.setVisibility(View.GONE);
                    updateStartButton();
                } else {
                    // Active battle exists
                    Log.d(TAG, "Active battle exists, showing battle info");
                    layoutNoBattle.setVisibility(View.GONE);
                    layoutActiveBattle.setVisibility(View.VISIBLE);
                    updateBattleInfo();
                    loadProgressData();
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "loadActiveBattle ERROR: " + error);
                progressBar.setVisibility(View.GONE);

                // If no active battle (404 or similar), show no battle layout
                if (error.contains("404") || error.contains("not found")) {
                    Log.d(TAG, "Treating as 'no active battle'");
                    layoutNoBattle.setVisibility(View.VISIBLE);
                    layoutActiveBattle.setVisibility(View.GONE);
                    updateStartButton();
                } else {
                    Toast.makeText(getContext(), "Failed to load battle: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateGuildInfo() {
        if (currentGuild != null) {
            tvMemberCount.setText("Members: " + currentGuild.getMemberCount());
        }
    }

    private void updateStartButton() {
        if (currentGuild != null && currentUserId != null) {
            boolean isLeader = currentGuild.isLeader(currentUserId);
            btnStartMission.setEnabled(isLeader);
            btnStartMission.setText(isLeader ? "Start Special Mission" : "Only Leader Can Start");
        }
    }

    private void updateBattleInfo() {
        if (activeBattle == null) return;

        tvBossName.setText(activeBattle.getBossName());
        tvBossHp.setText(activeBattle.getCurrentHp() + " / " + activeBattle.getMaxHp() + " HP");

        // Update progress bar
        int progress = (int) (activeBattle.getProgressPercentage() != null ? activeBattle.getProgressPercentage() : 0);
        bossProgressBar.setProgress(progress);
        tvProgressPercentage.setText(progress + "%");

        // Update time remaining
        updateTimeRemaining();
    }

    private void updateTimeRemaining() {
        if (activeBattle == null || activeBattle.getEndsAt() == null) return;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date endDate = sdf.parse(activeBattle.getEndsAt());
            Date now = new Date();

            if (endDate != null) {
                long diff = endDate.getTime() - now.getTime();
                if (diff > 0) {
                    long days = diff / (24 * 60 * 60 * 1000);
                    long hours = (diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
                    tvTimeRemaining.setText(String.format("Time Remaining: %d days, %d hours", days, hours));
                } else {
                    tvTimeRemaining.setText("Mission Expired");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse date", e);
            tvTimeRemaining.setText("Time: " + activeBattle.getEndsAt());
        }
    }

    private void loadProgressData() {
        Log.d(TAG, "loadProgressData called with guildId: " + guildId);

        if (guildId == null || guildId == 0) {
            Log.e(TAG, "Cannot load progress - guildId is null or 0!");
            return;
        }

        bossBattleRepository.getGuildBossBattleProgress(guildId, new GuildBossBattleRepository.SummaryCallback() {
            @Override
            public void onSuccess(GuildBossMissionSummary summary) {
                Log.d(TAG, "loadProgressData SUCCESS");
                if (summary.getBattle() != null) {
                    activeBattle = summary.getBattle();
                    updateBattleInfo();
                }

                if (summary.getMemberProgress() != null) {
                    Log.d(TAG, "Member progress count: " + summary.getMemberProgress().size());
                    progressAdapter.setProgressList(summary.getMemberProgress());

                    // Update my progress
                    if (currentUserId != null) {
                        for (GuildBossMissionProgress progress : summary.getMemberProgress()) {
                            if (progress.getUserId().equals(currentUserId)) {
                                updateMyProgress(progress);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "loadProgressData ERROR: " + error);
            }
        });
    }

    private void updateMyProgress(GuildBossMissionProgress progress) {
        String badgeEmoji = getBadgeEmoji(progress.getBadge());
        tvMyDamage.setText("My Damage: " + progress.getTotalDamageDealt() + " HP");
        tvMyBadge.setText("My Badge: " + badgeEmoji + " " + progress.getBadgeTitle());
    }

    private String getBadgeEmoji(String badge) {
        if (badge == null) return "";
        switch (badge) {
            case "BRONZE": return "🥉";
            case "SILVER": return "🥈";
            case "GOLD": return "🥇";
            case "PLATINUM": return "💎";
            case "DIAMOND": return "💠";
            default: return "";
        }
    }

    private void startMission() {
        Log.d(TAG, "startMission called with guildId: " + guildId);

        if (guildId == null || guildId == 0) {
            Log.e(TAG, "Cannot start mission - guildId is null or 0!");
            Toast.makeText(getContext(), "Invalid guild ID", Toast.LENGTH_SHORT).show();
            return;
        }

        btnStartMission.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        bossBattleRepository.startGuildBossBattle(guildId, new GuildBossBattleRepository.BattleCallback() {
            @Override
            public void onSuccess(GuildBossBattle battle) {
                Log.d(TAG, "startMission SUCCESS - battle ID: " + (battle != null ? battle.getId() : "null"));
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Special Mission Started!", Toast.LENGTH_SHORT).show();
                loadActiveBattle();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "startMission ERROR: " + error);
                progressBar.setVisibility(View.GONE);
                btnStartMission.setEnabled(true);
                Toast.makeText(getContext(), "Failed to start mission: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
