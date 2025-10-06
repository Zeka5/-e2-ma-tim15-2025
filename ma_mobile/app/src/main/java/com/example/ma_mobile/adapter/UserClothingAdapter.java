package com.example.ma_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.R;
import com.example.ma_mobile.models.UserClothing;

import java.util.ArrayList;
import java.util.List;

public class UserClothingAdapter extends RecyclerView.Adapter<UserClothingAdapter.ClothingViewHolder> {

    private List<UserClothing> clothingList;
    private OnClothingActionListener listener;

    public interface OnClothingActionListener {
        void onActivateClothing(UserClothing clothing);
        void onDeactivateClothing(UserClothing clothing);
    }

    public UserClothingAdapter(OnClothingActionListener listener) {
        this.clothingList = new ArrayList<>();
        this.listener = listener;
    }

    public void setClothingList(List<UserClothing> clothingList) {
        this.clothingList = clothingList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClothingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_clothing, parent, false);
        return new ClothingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothingViewHolder holder, int position) {
        UserClothing clothing = clothingList.get(position);
        holder.bind(clothing);
    }

    @Override
    public int getItemCount() {
        return clothingList.size();
    }

    class ClothingViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvType;
        private TextView tvBonus;
        private TextView tvBattlesRemaining;
        private TextView tvDescription;
        private Button btnToggleActivate;

        public ClothingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_clothing_name);
            tvType = itemView.findViewById(R.id.tv_clothing_type);
            tvBonus = itemView.findViewById(R.id.tv_clothing_bonus);
            tvBattlesRemaining = itemView.findViewById(R.id.tv_battles_remaining);
            tvDescription = itemView.findViewById(R.id.tv_clothing_description);
            btnToggleActivate = itemView.findViewById(R.id.btn_toggle_activate_clothing);
        }

        public void bind(UserClothing clothing) {
            tvName.setText(clothing.getName() != null ? clothing.getName() : "Unknown Clothing");
            tvType.setText(clothing.getType() != null ? clothing.getType() : "");
            tvBonus.setText("Bonus: +" + (clothing.getAccumulatedBonus() != null ? clothing.getAccumulatedBonus() : 0));
            tvBattlesRemaining.setText("Battles: " + (clothing.getBattlesRemaining() != null ? clothing.getBattlesRemaining() : 0));

            if (clothing.getDescription() != null && !clothing.getDescription().isEmpty()) {
                tvDescription.setText(clothing.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            // Set button state based on activation
            if (clothing.getIsActive() != null && clothing.getIsActive()) {
                btnToggleActivate.setText("Deactivate");
                btnToggleActivate.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeactivateClothing(clothing);
                    }
                });
            } else {
                btnToggleActivate.setText("Activate");
                btnToggleActivate.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onActivateClothing(clothing);
                    }
                });
            }
        }
    }
}
