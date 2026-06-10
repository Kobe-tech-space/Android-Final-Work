package com.example.finalwork.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalwork.R;
import com.example.finalwork.activity.CourseEditActivity;
import com.example.finalwork.adapter.CourseAdapter;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.CourseEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CourseFragment extends Fragment {
    AppDatabase db;
    CourseAdapter adapter;
    TextView emptyView;
    int selectedWeekday = 0;
    final List<TextView> weekButtons = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        db = AppDatabase.getInstance(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.tvEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CourseAdapter(new CourseAdapter.Listener() {
            @Override
            public void onEdit(CourseEntity course) {
                Intent intent = new Intent(requireContext(), CourseEditActivity.class);
                intent.putExtra("id", course.id);
                startActivity(intent);
            }

            @Override
            public void onDelete(CourseEntity course) {
                new Thread(() -> {
                    db.courseDao().delete(course);
                    load();
                }).start();
            }
        });
        recyclerView.setAdapter(adapter);
        ((TextView) view.findViewById(R.id.tvPageTitle)).setText("课程表");
        setupWeekFilter(view);
        ((FloatingActionButton) view.findViewById(R.id.fabAdd)).setOnClickListener(v -> startActivity(new Intent(requireContext(), CourseEditActivity.class)));
        return view;
    }

    private void setupWeekFilter(View view) {
        HorizontalScrollView scrollView = view.findViewById(R.id.weekFilterScroll);
        LinearLayout container = view.findViewById(R.id.weekFilterContainer);
        scrollView.setVisibility(View.VISIBLE);
        container.removeAllViews();
        weekButtons.clear();
        String[] labels = {"全部", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (int i = 0; i < labels.length; i++) {
            TextView button = new TextView(requireContext());
            button.setText(labels[i]);
            button.setGravity(android.view.Gravity.CENTER);
            button.setTextSize(14);
            button.setSingleLine(true);
            button.setMinWidth(dp(64));
            button.setPadding(dp(18), 0, dp(18), 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(44));
            params.setMargins(0, 0, dp(10), 0);
            button.setLayoutParams(params);
            final int weekday = i;
            button.setOnClickListener(v -> {
                selectedWeekday = weekday;
                updateFilterStyle();
                load();
            });
            weekButtons.add(button);
            container.addView(button);
        }
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if (today == 0) today = 7;
        selectedWeekday = today;
        updateFilterStyle();
    }

    private void updateFilterStyle() {
        for (int i = 0; i < weekButtons.size(); i++) {
            TextView button = weekButtons.get(i);
            boolean selected = i == selectedWeekday;
            button.setTextColor(ContextCompat.getColor(requireContext(), selected ? android.R.color.white : R.color.text_main));
            button.setText(selected ? "✓ " + getWeekLabel(i) : getWeekLabel(i));
            button.setTypeface(null, selected ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
            button.setBackgroundResource(selected ? R.drawable.bg_campus_gradient : R.drawable.bg_soft_card);
        }
    }

    private String getWeekLabel(int index) {
        String[] labels = {"全部", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return labels[index];
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    void load() {
        new Thread(() -> {
            String username = new com.example.finalwork.utils.SessionManager(requireContext()).getUsername();
            List<CourseEntity> data = selectedWeekday == 0
                    ? db.courseDao().getByUsername(username)
                    : db.courseDao().getByWeekday(username, selectedWeekday);
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                adapter.setData(data);
                emptyView.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
                emptyView.setText(selectedWeekday == 0 ? "还没有课程，点击右下角添加第一门课" : "周" + selectedWeekday + "还没有课程，点击右下角添加第一门课");
            });
        }).start();
    }
}
