package com.example.ma_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.R;
import com.example.ma_mobile.models.UserPotion;

import java.util.ArrayList;
import java.util.List;

public class UserPotionAdapter extends RecyclerView.Adapter<UserPotionAdapter.PotionViewHolder> {

    private List<UserPotion> potions;
    private OnPotionActionListener listener;

    public interface OnPotionActionListener {
        void onActivatePotion(UserPotion potion);
        void onDeactivatePotion(UserPotion potion);
    }

    public UserPotionAdapter(OnPotionActionListener listener) {
        this.potions = new ArrayList<>();
        this.listener = listener;
    }

    public void setPotions(List<UserPotion> potions) {
        this.potions = potions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_potion, parent, false);
        return new PotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PotionViewHolder holder, int position) {
        UserPotion potion = potions.get(position);
        holder.bind(potion);
    }

    @Override
    public int getItemCount() {
        return potions.size();
    }

    class PotionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvPowerBonus;
        private TextView tvQuantity;
        private TextView tvType;
        private TextView tvDescription;
        private Button btnToggleActivate;

        public PotionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_potion_name);
            tvPowerBonus = itemView.findViewById(R.id.tv_potion_power_bonus);
            tvQuantity = itemView.findViewById(R.id.tv_potion_quantity);
            tvType = itemView.findViewById(R.id.tv_potion_type);
            tvDescription = itemView.findViewById(R.id.tv_potion_description);
            btnToggleActivate = itemView.findViewById(R.id.btn_toggle_activate_potion);
        }

        public void bind(UserPotion potion) {
            tvName.setText(potion.getName() != null ? potion.getName() : "Unknown Potion");
            tvPowerBonus.setText("Power: +" + (potion.getPowerBonus() != null ? potion.getPowerBonus() : 0));
            tvQuantity.setText("Qty: " + (potion.getQuantity() != null ? potion.getQuantity() : 0));
            tvType.setText(potion.getIsPermanent() != null && potion.getIsPermanent() ? "Permanent" : "Temporary");

            if (potion.getDescription() != null && !potion.getDescription().isEmpty()) {
                tvDescription.setText(potion.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            // Set button state based on activation
            if (potion.getIsActivated() != null && potion.getIsActivated()) {
                btnToggleActivate.setText("Deactivate");
                btnToggleActivate.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeactivatePotion(potion);
                    }
                });
            } else {
                btnToggleActivate.setText("Activate");
                btnToggleActivate.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onActivatePotion(potion);
                    }
                });
            }
        }
    }
}
