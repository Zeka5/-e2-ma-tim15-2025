package com.example.ma_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.R;
import com.example.ma_mobile.models.GuildBossMissionProgress;

import java.util.ArrayList;
import java.util.List;

public class GuildBossProgressAdapter extends RecyclerView.Adapter<GuildBossProgressAdapter.ProgressViewHolder> {

    private List<GuildBossMissionProgress> progressList = new ArrayList<>();

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guild_boss_progress, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        GuildBossMissionProgress progress = progressList.get(position);
        holder.bind(progress);
    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    public void setProgressList(List<GuildBossMissionProgress> progressList) {
        this.progressList = progressList != null ? progressList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvBadge;
        private TextView tvTotalDamage;
        private TextView tvTasksCompleted;
        private TextView tvShopPurchases;
        private TextView tvBossHits;
        private TextView tvMessagesCount;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvBadge = itemView.findViewById(R.id.tv_badge);
            tvTotalDamage = itemView.findViewById(R.id.tv_total_damage);
            tvTasksCompleted = itemView.findViewById(R.id.tv_tasks_completed);
            tvShopPurchases = itemView.findViewById(R.id.tv_shop_purchases);
            tvBossHits = itemView.findViewById(R.id.tv_boss_hits);
            tvMessagesCount = itemView.findViewById(R.id.tv_messages_count);
        }

        public void bind(GuildBossMissionProgress progress) {
            tvUsername.setText(progress.getUsername());
            tvBadge.setText(getBadgeEmoji(progress.getBadge()) + " " + progress.getBadgeTitle());
            tvTotalDamage.setText("Total Damage: " + progress.getTotalDamageDealt() + " HP");
            tvTasksCompleted.setText("Tasks: " + progress.getTotalTasksCompleted());
            tvShopPurchases.setText("Shop: " + progress.getShopPurchases() + "/5");
            tvBossHits.setText("Boss Hits: " + progress.getBossHits() + "/10");
            tvMessagesCount.setText("Messages: " + progress.getDaysWithMessages() + " days");
        }

        private String getBadgeEmoji(String badge) {
            if (badge == null) return "";
            switch (badge) {
                case "BRONZE":
                    return "🥉";
                case "SILVER":
                    return "🥈";
                case "GOLD":
                    return "🥇";
                case "PLATINUM":
                    return "💎";
                case "DIAMOND":
                    return "💠";
                default:
                    return "";
            }
        }
    }
}
