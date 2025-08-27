package com.example.ma_mobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsFragment extends Fragment {

    private LinearLayout llNotificationsList;
    private TextView tvNoNotifications;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        loadNotifications();
    }

    private void initializeViews(View view) {
        llNotificationsList = view.findViewById(R.id.ll_notifications_list);
        tvNoNotifications = view.findViewById(R.id.tv_no_notifications);
    }

    private void loadNotifications() {
        List<NotificationItem> notifications = createSampleNotifications();

        if (notifications.isEmpty()) {
            showNoNotifications();
        } else {
            displayNotifications(notifications);
        }
    }

    private List<NotificationItem> createSampleNotifications() {
        List<NotificationItem> notifications = new ArrayList<>();

        notifications.add(new NotificationItem(
                "Welcome!",
                "Welcome to the app! Explore all the features.",
                new Date(System.currentTimeMillis() - 3600000) // 1 hour ago
        ));

        notifications.add(new NotificationItem(
                "Account Created",
                "Your account has been successfully created.",
                new Date(System.currentTimeMillis() - 7200000) // 2 hours ago
        ));

        return notifications;
    }

    private void showNoNotifications() {
        tvNoNotifications.setVisibility(View.VISIBLE);
        llNotificationsList.setVisibility(View.GONE);
    }

    private void displayNotifications(List<NotificationItem> notifications) {
        tvNoNotifications.setVisibility(View.GONE);
        llNotificationsList.setVisibility(View.VISIBLE);

        llNotificationsList.removeAllViews();

        for (NotificationItem notification : notifications) {
            View notificationView = createNotificationView(notification);
            llNotificationsList.addView(notificationView);
        }
    }

    private View createNotificationView(NotificationItem notification) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(32, 24, 32, 24);
        itemLayout.setBackground(getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));

        // Set margin
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16);
        itemLayout.setLayoutParams(layoutParams);

        // Title
        TextView titleView = new TextView(getContext());
        titleView.setText(notification.title);
        titleView.setTextSize(16);
        titleView.setTextColor(getResources().getColor(android.R.color.black));
        titleView.setTypeface(titleView.getTypeface(), android.graphics.Typeface.BOLD);
        itemLayout.addView(titleView);

        // Message
        TextView messageView = new TextView(getContext());
        messageView.setText(notification.message);
        messageView.setTextSize(14);
        messageView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        messageParams.setMargins(0, 8, 0, 0);
        messageView.setLayoutParams(messageParams);
        itemLayout.addView(messageView);

        // Timestamp
        TextView timeView = new TextView(getContext());
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        timeView.setText(sdf.format(notification.timestamp));
        timeView.setTextSize(12);
        timeView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        timeParams.setMargins(0, 8, 0, 0);
        timeView.setLayoutParams(timeParams);
        itemLayout.addView(timeView);

        return itemLayout;
    }

    private static class NotificationItem {
        String title;
        String message;
        Date timestamp;

        NotificationItem(String title, String message, Date timestamp) {
            this.title = title;
            this.message = message;
            this.timestamp = timestamp;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }
}