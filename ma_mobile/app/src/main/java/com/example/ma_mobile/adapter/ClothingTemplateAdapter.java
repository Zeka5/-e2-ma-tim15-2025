package com.example.ma_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ma_mobile.R;
import com.example.ma_mobile.models.ClothingTemplate;

import java.util.ArrayList;
import java.util.List;

public class ClothingTemplateAdapter extends RecyclerView.Adapter<ClothingTemplateAdapter.ClothingTemplateViewHolder> {

    private List<ClothingTemplate> clothingList;
    private OnPurchaseListener listener;

    public interface OnPurchaseListener {
        void onPurchaseClothing(ClothingTemplate clothing);
    }

    public ClothingTemplateAdapter(OnPurchaseListener listener) {
        this.clothingList = new ArrayList<>();
        this.listener = listener;
    }

    public void setClothingList(List<ClothingTemplate> clothingList) {
        this.clothingList = clothingList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClothingTemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop_clothing, parent, false);
        return new ClothingTemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothingTemplateViewHolder holder, int position) {
        ClothingTemplate clothing = clothingList.get(position);
        holder.bind(clothing);
    }

    @Override
    public int getItemCount() {
        return clothingList.size();
    }

    class ClothingTemplateViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvType;
        private TextView tvBonus;
        private TextView tvPrice;
        private TextView tvDescription;
        private Button btnPurchase;

        public ClothingTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_shop_clothing_name);
            tvType = itemView.findViewById(R.id.tv_shop_clothing_type);
            tvBonus = itemView.findViewById(R.id.tv_shop_clothing_bonus);
            tvPrice = itemView.findViewById(R.id.tv_shop_clothing_price);
            tvDescription = itemView.findViewById(R.id.tv_shop_clothing_description);
            btnPurchase = itemView.findViewById(R.id.btn_purchase_clothing);
        }

        public void bind(ClothingTemplate clothing) {
            tvName.setText(clothing.getName() != null ? clothing.getName() : "Unknown Clothing");
            tvType.setText(clothing.getType() != null ? clothing.getType() : "");
            tvBonus.setText("Bonus: +" + (clothing.getBonus() != null ? clothing.getBonus() : 0));
            tvPrice.setText(String.valueOf(clothing.getCalculatedPrice() != null ? clothing.getCalculatedPrice() : 0) + " coins");

            if (clothing.getDescription() != null && !clothing.getDescription().isEmpty()) {
                tvDescription.setText(clothing.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            btnPurchase.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPurchaseClothing(clothing);
                }
            });
        }
    }
}
