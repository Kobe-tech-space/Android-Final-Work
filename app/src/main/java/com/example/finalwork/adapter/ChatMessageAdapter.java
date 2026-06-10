package com.example.finalwork.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalwork.R;
import com.example.finalwork.entity.ChatMessageEntity;
import com.example.finalwork.model.ChatMessage;
import io.noties.markwon.Markwon;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.VH> {
    public interface Listener {
        void onDelete(ChatMessageEntity message);
    }

    private final List<ChatMessageEntity> data = new ArrayList<>();
    private Listener listener;
    private Markwon markwon;
    private RecyclerView recyclerView;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<ChatMessageEntity> messages) {
        data.clear();
        data.addAll(messages);
        notifyDataSetChanged();
    }

    /** 流式更新：直接 setText，不触发 Markwon 重渲染，避免抽搐 */
    public void updateMessageContent(int messageId, String content) {
        for (int i = data.size() - 1; i >= 0; i--) {
            ChatMessageEntity message = data.get(i);
            if (message.id == messageId) {
                message.content = content;
                if (recyclerView != null) {
                    RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(i);
                    if (vh instanceof VH && ((VH) vh).text != null) {
                        ((VH) vh).text.setText(content);
                    }
                }
                return;
            }
        }
    }

    /** 流式结束后调用，触发 Markwon 渲染 */
    public void finalizeMessage(int messageId) {
        for (int i = data.size() - 1; i >= 0; i--) {
            if (data.get(i).id == messageId) {
                notifyItemChanged(i);
                return;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == ChatMessage.TYPE_USER ? R.layout.item_chat_user : R.layout.item_chat_ai;
        if (markwon == null) {
            markwon = Markwon.create(parent.getContext());
        }
        return new VH(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ChatMessageEntity message = data.get(position);
        holder.time.setText(DateFormat.format("HH:mm", message.createdAt));
        if (message.type == ChatMessage.TYPE_AI) {
            markwon.setMarkdown(holder.text, message.content);
        } else {
            holder.text.setText(message.content);
        }
        holder.itemView.setOnLongClickListener(v -> {
            showActionDialog(v.getContext(), message);
            return true;
        });
    }

    private void showActionDialog(Context context, ChatMessageEntity message) {
        new AlertDialog.Builder(context)
                .setTitle("消息操作")
                .setItems(new CharSequence[]{"复制", "删除"}, (dialog, which) -> {
                    if (which == 0) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        if (clipboard != null) {
                            clipboard.setPrimaryClip(ClipData.newPlainText("chat_message", message.content));
                        }
                    } else if (which == 1 && listener != null) {
                        listener.onDelete(message);
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView text;
        TextView time;

        VH(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tvMessage);
            time = itemView.findViewById(R.id.tvTime);
        }
    }
}
