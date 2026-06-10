package com.example.finalwork.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalwork.R;
import com.example.finalwork.entity.CourseEntity;
import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.VH> {

    public interface Listener {
        void onEdit(CourseEntity course);
        void onDelete(CourseEntity course);
    }

    private List<CourseEntity> data = new ArrayList<>();
    private final Listener listener;

    public CourseAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<CourseEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CourseEntity course = data.get(position);
        holder.badge.setText("周" + course.weekday);
        holder.title.setText(course.courseName);
        holder.subtitle.setText("第" + course.startSection + "-" + course.endSection + "节 · " + course.classroom + " · " + course.teacher);
        holder.itemView.setOnClickListener(v -> listener.onEdit(course));
        holder.delete.setOnClickListener(v -> listener.onDelete(course));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle, badge;
        ImageButton delete;

        VH(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            subtitle = itemView.findViewById(R.id.tvSubtitle);
            badge = itemView.findViewById(R.id.tvBadge);
            delete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
