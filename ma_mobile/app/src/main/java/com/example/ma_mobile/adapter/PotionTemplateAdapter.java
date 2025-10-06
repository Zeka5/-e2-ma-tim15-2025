package com.example.ma_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.R;
import com.example.ma_mobile.models.PotionTemplate;

import java.util.ArrayList;
import java.util.List;

public class PotionTemplateAdapter extends RecyclerView.Adapter<PotionTemplateAdapter.PotionTemplateViewHolder> {

    private List<PotionTemplate> potions;
    private OnPurchaseListener listener;

    public interface OnPurchaseListener {
        void onPurchasePotion(PotionTemplate potion);
    }

    public PotionTemplateAdapter(OnPurchaseListener listener) {
        this.potions = new ArrayList<>();
        this.listener = listener;
    }

    public void setPotions(List<PotionTemplate> potions) {
        this.potions = potions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PotionTemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop_potion, parent, false);
        return new PotionTemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PotionTemplateViewHolder holder, int position) {
        PotionTemplate potion = potions.get(position);
        holder.bind(potion);
    }

    @Override
    public int getItemCount() {
        return potions.size();
    }

    class PotionTemplateViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvPowerBonus;
        private TextView tvPrice;
        private TextView tvType;
        private TextView tvDescription;
        private Button btnPurchase;

        public PotionTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_shop_potion_name);
            tvPowerBonus = itemView.findViewById(R.id.tv_shop_potion_power);
            tvPrice = itemView.findViewById(R.id.tv_shop_potion_price);
            tvType = itemView.findViewById(R.id.tv_shop_potion_type);
            tvDescription = itemView.findViewById(R.id.tv_shop_potion_description);
            btnPurchase = itemView.findViewById(R.id.btn_purchase_potion);
        }

        public void bind(PotionTemplate potion) {
            tvName.setText(potion.getName() != null ? potion.getName() : "Unknown Potion");
            tvPowerBonus.setText("Power: +" + (potion.getPowerBonus() != null ? potion.getPowerBonus() : 0));
            tvPrice.setText(String.valueOf(potion.getCalculatedPrice() != null ? potion.getCalculatedPrice() : 0) + " coins");
            tvType.setText(potion.getIsPermanent() != null && potion.getIsPermanent() ? "Permanent" : "Temporary");

            if (potion.getDescription() != null && !potion.getDescription().isEmpty()) {
                tvDescription.setText(potion.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            btnPurchase.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPurchasePotion(potion);
                }
            });
        }
    }
}
