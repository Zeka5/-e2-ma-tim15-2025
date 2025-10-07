package com.example.ma_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.R;
import com.example.ma_mobile.models.UserWeapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserWeaponAdapter extends RecyclerView.Adapter<UserWeaponAdapter.WeaponViewHolder> {

    private List<UserWeapon> weapons;
    private OnWeaponActionListener listener;

    public interface OnWeaponActionListener {
        void onUpgradeWeapon(UserWeapon weapon);
    }

    public UserWeaponAdapter(OnWeaponActionListener listener) {
        this.weapons = new ArrayList<>();
        this.listener = listener;
    }

    public void setWeapons(List<UserWeapon> weapons) {
        this.weapons = weapons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WeaponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_weapon, parent, false);
        return new WeaponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeaponViewHolder holder, int position) {
        UserWeapon weapon = weapons.get(position);
        holder.bind(weapon);
    }

    @Override
    public int getItemCount() {
        return weapons.size();
    }

    class WeaponViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvType;
        private TextView tvBonus;
        private TextView tvLevel;
        private TextView tvDuplicates;
        private TextView tvDescription;
        private Button btnUpgrade;

        public WeaponViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_weapon_name);
            tvType = itemView.findViewById(R.id.tv_weapon_type);
            tvBonus = itemView.findViewById(R.id.tv_weapon_bonus);
            tvLevel = itemView.findViewById(R.id.tv_weapon_level);
            tvDuplicates = itemView.findViewById(R.id.tv_weapon_duplicates);
            tvDescription = itemView.findViewById(R.id.tv_weapon_description);
            btnUpgrade = itemView.findViewById(R.id.btn_upgrade_weapon);
        }

        public void bind(UserWeapon weapon) {
            tvName.setText(weapon.getName() != null ? weapon.getName() : "Unknown Weapon");
            tvType.setText(weapon.getType() != null ? weapon.getType() : "");
            tvBonus.setText(String.format(Locale.US, "Bonus: %.2f%%",
                weapon.getCurrentBonusPercentage() != null ? weapon.getCurrentBonusPercentage() : 0.0));
            tvLevel.setText("Level: " + (weapon.getUpgradeLevel() != null ? weapon.getUpgradeLevel() : 0));
            tvDuplicates.setText("Duplicates: " + (weapon.getDuplicateCount() != null ? weapon.getDuplicateCount() : 0));

            if (weapon.getDescription() != null && !weapon.getDescription().isEmpty()) {
                tvDescription.setText(weapon.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            // Upgrade button
            btnUpgrade.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUpgradeWeapon(weapon);
                }
            });
        }
    }
}
