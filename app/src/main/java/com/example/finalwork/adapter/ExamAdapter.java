package com.example.finalwork.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalwork.R;
import com.example.finalwork.entity.ExamEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.VH> {
    public interface Listener {
        void onEdit(ExamEntity exam);
        void onDelete(ExamEntity exam);
    }

    private List<ExamEntity> data = new ArrayList<>();
    private final Listener listener;

    public ExamAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<ExamEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ExamEntity exam = data.get(position);
        holder.title.setText(exam.examName);
        holder.subtitle.setText(exam.examTime + " · " + exam.location);
        holder.countdown.setText(getCountdownText(exam.examTime));
        holder.itemView.setOnClickListener(v -> listener.onEdit(exam));
        holder.delete.setOnClickListener(v -> listener.onDelete(exam));
    }

    private String getCountdownText(String examTime) {
        try {
            String datePart = examTime.length() >= 10 ? examTime.substring(0, 10) : examTime;
            Date examDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(datePart);
            if (examDate == null) return "待定";
            long diff = examDate.getTime() - System.currentTimeMillis();
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            if (days < 0) return "已结束";
            if (days == 0) return "今天\n考试";
            return "还有\n" + days + "天";
        } catch (Exception e) {
            return "待定";
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle, countdown;
        ImageButton delete;

        VH(View view) {
            super(view);
            title = view.findViewById(R.id.tvTitle);
            subtitle = view.findViewById(R.id.tvSubtitle);
            countdown = view.findViewById(R.id.tvCountdown);
            delete = view.findViewById(R.id.btnDelete);
        }
    }
}
