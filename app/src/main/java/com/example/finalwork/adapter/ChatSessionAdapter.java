package com.example.finalwork.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalwork.R;
import com.example.finalwork.entity.ChatSessionEntity;
import java.util.ArrayList;
import java.util.List;

public class ChatSessionAdapter extends RecyclerView.Adapter<ChatSessionAdapter.VH> {
    public interface Listener {
        void onOpen(ChatSessionEntity session);
        void onDelete(ChatSessionEntity session);
    }

    private final Listener listener;
    private List<ChatSessionEntity> data = new ArrayList<>();

    public ChatSessionAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<ChatSessionEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_session, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ChatSessionEntity session = data.get(position);
        holder.title.setText(session.title);
        holder.time.setText(DateFormat.format("MM-dd HH:mm", session.updatedAt));
        holder.itemView.setOnClickListener(v -> listener.onOpen(session));
        holder.delete.setOnClickListener(v -> listener.onDelete(session));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        ImageButton delete;

        VH(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvSessionTitle);
            time = itemView.findViewById(R.id.tvSessionTime);
            delete = itemView.findViewById(R.id.btnDeleteSession);
        }
    }
}
