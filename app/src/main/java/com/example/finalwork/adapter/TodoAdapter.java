package com.example.finalwork.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalwork.R;
import com.example.finalwork.entity.TodoEntity;
import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.VH> {
    public interface Listener {
        void onToggle(TodoEntity todo);
        void onDelete(TodoEntity todo);
        void onEdit(TodoEntity todo);
    }

    private List<TodoEntity> data = new ArrayList<>();
    private final Listener listener;

    public TodoAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<TodoEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TodoEntity todo = data.get(position);
        holder.checkBox.setChecked(todo.completed);
        holder.title.setText(todo.title);
        String deadline = todo.deadline == null || todo.deadline.isEmpty() ? "未设置截止时间" : todo.deadline;
        String content = todo.content == null || todo.content.isEmpty() ? "暂无备注" : todo.content;
        holder.subtitle.setText(deadline + " · " + content);
        holder.category.setText("分类：" + (todo.category == null ? "作业" : todo.category));
        holder.priority.setText((todo.priority == null ? "中" : todo.priority) + "优先级");
        holder.title.setPaintFlags(todo.completed ? holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : holder.title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        holder.checkBox.setOnClickListener(v -> listener.onToggle(todo));
        holder.itemView.setOnClickListener(v -> listener.onEdit(todo));
        holder.delete.setOnClickListener(v -> listener.onDelete(todo));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView title, subtitle, category, priority;
        ImageButton delete;

        VH(View view) {
            super(view);
            checkBox = view.findViewById(R.id.cbDone);
            title = view.findViewById(R.id.tvTitle);
            subtitle = view.findViewById(R.id.tvSubtitle);
            category = view.findViewById(R.id.tvCategory);
            priority = view.findViewById(R.id.tvPriority);
            delete = view.findViewById(R.id.btnDelete);
        }
    }
}
