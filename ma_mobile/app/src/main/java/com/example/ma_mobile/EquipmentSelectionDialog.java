package com.example.ma_mobile;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class EquipmentSelectionDialog extends DialogFragment {

    private static final String ARG_SELECTED_IDS = "selected_ids";

    private RecyclerView rvWeapons, rvClothing;
    private TextView tvNoWeapons, tvNoClothing;
    private MaterialButton btnConfirm, btnCancel;

    private List<Long> selectedEquipmentIds;
    private OnEquipmentSelectedListener listener;

    public interface OnEquipmentSelectedListener {
        void onEquipmentSelected(List<Long> equipmentIds);
    }

    public static EquipmentSelectionDialog newInstance(List<Long> selectedIds) {
        EquipmentSelectionDialog fragment = new EquipmentSelectionDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SELECTED_IDS, new ArrayList<>(selectedIds));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedEquipmentIds = (List<Long>) getArguments().getSerializable(ARG_SELECTED_IDS);
        }
        if (selectedEquipmentIds == null) {
            selectedEquipmentIds = new ArrayList<>();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_equipment_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerViews();
        setupClickListeners();
        loadEquipment();
    }

    private void initializeViews(View view) {
        rvWeapons = view.findViewById(R.id.rv_weapons);
        rvClothing = view.findViewById(R.id.rv_clothing);
        tvNoWeapons = view.findViewById(R.id.tv_no_weapons);
        tvNoClothing = view.findViewById(R.id.tv_no_clothing);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnCancel = view.findViewById(R.id.btn_cancel);
    }

    private void setupRecyclerViews() {
        rvWeapons.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvClothing.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupClickListeners() {
        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEquipmentSelected(selectedEquipmentIds);
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void loadEquipment() {
        // TODO: Load weapons and clothing from API
        // For now, show empty state

        // Simulated empty state
        List<EquipmentItem> weapons = new ArrayList<>();
        List<EquipmentItem> clothing = new ArrayList<>();

        if (weapons.isEmpty()) {
            rvWeapons.setVisibility(View.GONE);
            tvNoWeapons.setVisibility(View.VISIBLE);
        } else {
            rvWeapons.setVisibility(View.VISIBLE);
            tvNoWeapons.setVisibility(View.GONE);
            EquipmentAdapter weaponAdapter = new EquipmentAdapter(weapons, selectedEquipmentIds, this::onEquipmentToggled);
            rvWeapons.setAdapter(weaponAdapter);
        }

        if (clothing.isEmpty()) {
            rvClothing.setVisibility(View.GONE);
            tvNoClothing.setVisibility(View.VISIBLE);
        } else {
            rvClothing.setVisibility(View.VISIBLE);
            tvNoClothing.setVisibility(View.GONE);
            EquipmentAdapter clothingAdapter = new EquipmentAdapter(clothing, selectedEquipmentIds, this::onEquipmentToggled);
            rvClothing.setAdapter(clothingAdapter);
        }
    }

    private void onEquipmentToggled(Long equipmentId, boolean selected) {
        if (selected) {
            if (!selectedEquipmentIds.contains(equipmentId)) {
                selectedEquipmentIds.add(equipmentId);
            }
        } else {
            selectedEquipmentIds.remove(equipmentId);
        }
    }

    public void setOnEquipmentSelectedListener(OnEquipmentSelectedListener listener) {
        this.listener = listener;
    }

    // Inner classes for equipment handling
    static class EquipmentItem {
        Long id;
        String name;
        String bonus;
        Integer battlesRemaining;
        boolean isClothing;

        public EquipmentItem(Long id, String name, String bonus, Integer battlesRemaining, boolean isClothing) {
            this.id = id;
            this.name = name;
            this.bonus = bonus;
            this.battlesRemaining = battlesRemaining;
            this.isClothing = isClothing;
        }
    }

    static class EquipmentAdapter extends RecyclerView.Adapter<EquipmentViewHolder> {
        private final List<EquipmentItem> items;
        private final List<Long> selectedIds;
        private final OnEquipmentToggleListener listener;

        interface OnEquipmentToggleListener {
            void onToggle(Long equipmentId, boolean selected);
        }

        public EquipmentAdapter(List<EquipmentItem> items, List<Long> selectedIds, OnEquipmentToggleListener listener) {
            this.items = items;
            this.selectedIds = selectedIds;
            this.listener = listener;
        }

        @NonNull
        @Override
        public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equipment, parent, false);
            return new EquipmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position) {
            EquipmentItem item = items.get(position);
            holder.tvName.setText(item.name);
            holder.tvBonus.setText(item.bonus);

            if (item.isClothing && item.battlesRemaining != null) {
                holder.tvBattlesRemaining.setVisibility(View.VISIBLE);
                holder.tvBattlesRemaining.setText(item.battlesRemaining + " battles remaining");
            } else {
                holder.tvBattlesRemaining.setVisibility(View.GONE);
            }

            holder.cbSelect.setChecked(selectedIds.contains(item.id));
            holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onToggle(item.id, isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    static class EquipmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBonus, tvBattlesRemaining;
        android.widget.CheckBox cbSelect;

        public EquipmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_equipment_name);
            tvBonus = itemView.findViewById(R.id.tv_equipment_bonus);
            tvBattlesRemaining = itemView.findViewById(R.id.tv_battles_remaining);
            cbSelect = itemView.findViewById(R.id.cb_select);
        }
    }
}
