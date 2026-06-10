package com.example.finalwork.fragment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalwork.R;
import com.example.finalwork.adapter.ChatMessageAdapter;
import com.example.finalwork.adapter.ChatSessionAdapter;
import com.example.finalwork.api.DeepSeekClient;
import com.example.finalwork.api.DeepSeekRequest;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.ChatMessageEntity;
import com.example.finalwork.entity.ChatSessionEntity;
import com.example.finalwork.model.ChatMessage;
import com.example.finalwork.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiChatFragment extends Fragment {
    private AppDatabase db;
    private ChatMessageAdapter adapter;
    private RecyclerView recyclerChat;
    private EditText input;
    private TextView currentSessionText;
    private TextView tvApiHint;
    private View inputArea;
    private int currentSessionId = 0;
    private String username;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_ai_chat, container, false);
        db = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());
        username = sessionManager.getUsername();
        recyclerChat = view.findViewById(R.id.recyclerChat);
        input = view.findViewById(R.id.etMessage);
        inputArea = view.findViewById(R.id.inputArea);
        currentSessionText = view.findViewById(R.id.tvCurrentSession);
        tvApiHint = view.findViewById(R.id.tvApiHint);
        adapter = new ChatMessageAdapter();
        adapter.setListener(this::showMessageActions);
        recyclerChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerChat.setAdapter(adapter);

        // 检查 API Key
        if (sessionManager.isApiKeyConfigured()) {
            DeepSeekClient.setApiKey(sessionManager.getApiKey());
            showChatMode();
        } else {
            showNoApiKeyMode();
        }

        view.findViewById(R.id.btnSend).setOnClickListener(v -> send());
        view.findViewById(R.id.btnNewSession).setOnClickListener(v -> createNewSession(true));
        view.findViewById(R.id.btnHistory).setOnClickListener(v -> showSessionsDialog());
        view.findViewById(R.id.btnDeleteSession).setOnClickListener(v -> confirmDeleteCurrentSession());
        loadLatestOrCreate();
        applyPendingPrompt();
        return view;
    }

    /** 未配置 API Key 时的 UI */
    private void showNoApiKeyMode() {
        recyclerChat.setVisibility(View.GONE);
        inputArea.setVisibility(View.GONE);
        tvApiHint.setVisibility(View.VISIBLE);
        tvApiHint.setText("🔑 尚未配置 DeepSeek API Key\n\n"
                + "当前用户：" + username + "\n\n"
                + "请在「我的 → 设置 DeepSeek API Key」中配置后使用 AI 助手。\n\n"
                + "仅支持 api.deepseek.com 的 Key。");
    }

    /** 已配置 API Key 时的 UI */
    private void showChatMode() {
        recyclerChat.setVisibility(View.VISIBLE);
        inputArea.setVisibility(View.VISIBLE);
        tvApiHint.setVisibility(View.GONE);
    }

    /** 刷新 API Key 状态（从 Profile 改完 Key 回来后调用） */
    private void refreshApiKeyStatus() {
        if (sessionManager.isApiKeyConfigured()) {
            DeepSeekClient.setApiKey(sessionManager.getApiKey());
            showChatMode();
            loadLatestOrCreate();
        } else {
            showNoApiKeyMode();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshApiKeyStatus();
    }

    private void applyPendingPrompt() {
        String prompt = com.example.finalwork.activity.MainActivity.pendingAiPrompt;
        if (prompt != null && !prompt.isEmpty()) {
            input.setText(prompt);
            input.setSelection(input.getText().length());
            com.example.finalwork.activity.MainActivity.pendingAiPrompt = "";
        }
    }

    private void loadLatestOrCreate() {
        if (!sessionManager.isApiKeyConfigured()) return;
        new Thread(() -> {
            List<ChatSessionEntity> sessions = db.chatSessionDao().getByUsername(username);
            if (sessions.isEmpty()) {
                long now = System.currentTimeMillis();
                long id = db.chatSessionDao().insert(
                        new ChatSessionEntity(username, "新的学习对话", now, now));
                currentSessionId = (int) id;
                db.chatMessageDao().insert(new ChatMessageEntity(currentSessionId,
                        "你好，我是 CampusAI。你可以问我学习规划、作业答疑、课程知识解释和简历优化。",
                        ChatMessage.TYPE_AI, now));
            } else {
                currentSessionId = sessions.get(0).id;
            }
            loadMessages();
        }).start();
    }

    private void createNewSession(boolean showToast) {
        if (!sessionManager.isApiKeyConfigured()) {
            Toast.makeText(requireContext(), "请先配置 API Key", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            long now = System.currentTimeMillis();
            long id = db.chatSessionDao().insert(
                    new ChatSessionEntity(username, "新的学习对话", now, now));
            currentSessionId = (int) id;
            db.chatMessageDao().insert(new ChatMessageEntity(currentSessionId,
                    "新的会话已开始。你可以继续提问，我会帮你整理学习思路。",
                    ChatMessage.TYPE_AI, now));
            requireActivity().runOnUiThread(() -> {
                if (showToast) Toast.makeText(requireContext(), "已新建会话", Toast.LENGTH_SHORT).show();
                loadMessages();
            });
        }).start();
    }

    private void loadMessages() {
        new Thread(() -> {
            ChatSessionEntity session = db.chatSessionDao().getById(currentSessionId);
            List<ChatMessageEntity> messages = db.chatMessageDao().getBySession(currentSessionId);
            requireActivity().runOnUiThread(() -> {
                currentSessionText.setText(session == null ? "当前会话" : session.title);
                adapter.setData(messages);
                scrollToBottom();
            });
        }).start();
    }

    private void send() {
        if (!sessionManager.isApiKeyConfigured()) {
            Toast.makeText(requireContext(), "请先在个人中心配置 DeepSeek API Key", Toast.LENGTH_SHORT).show();
            return;
        }
        String text = input.getText().toString().trim();
        if (text.isEmpty()) return;
        input.setText("");
        long now = System.currentTimeMillis();
        new Thread(() -> {
            if (currentSessionId == 0) {
                long id = db.chatSessionDao().insert(
                        new ChatSessionEntity(username, makeTitle(text), now, now));
                currentSessionId = (int) id;
            }
            ChatSessionEntity session = db.chatSessionDao().getById(currentSessionId);
            if (session != null) {
                if ("新的学习对话".equals(session.title)) session.title = makeTitle(text);
                session.updatedAt = now;
                db.chatSessionDao().update(session);
            }
            db.chatMessageDao().insert(new ChatMessageEntity(currentSessionId, text, ChatMessage.TYPE_USER, now));
            long msgId = db.chatMessageDao().insert(new ChatMessageEntity(currentSessionId, "", ChatMessage.TYPE_AI, now + 1));
            requireActivity().runOnUiThread(this::loadMessages);
            requestAiStream(text, (int) msgId);
        }).start();
    }

    private void requestAiStream(String text, int messageId) {
        List<ChatMessageEntity> allMessages = db.chatMessageDao().getBySession(currentSessionId);
        List<DeepSeekRequest.Message> history = new ArrayList<>();
        int endIndex = Math.max(0, allMessages.size() - 2);
        for (int i = 0; i < endIndex; i++) {
            ChatMessageEntity msg = allMessages.get(i);
            String role = msg.type == ChatMessage.TYPE_USER ? "user" : "assistant";
            history.add(new DeepSeekRequest.Message(role, msg.content));
        }

        DeepSeekRequest request = new DeepSeekRequest(history, text);
        DeepSeekClient.getApi().chatStream("Bearer " + DeepSeekClient.getApiKey(), request)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!isAdded()) return;
                        if (!response.isSuccessful() || response.body() == null) {
                            saveFinalContent(messageId, "AI 请求失败，状态码：" + response.code());
                            return;
                        }
                        new Thread(() -> {
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(response.body().byteStream()))) {
                                StringBuilder fullContent = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (line.startsWith("data: ")) {
                                        String data = line.substring(6);
                                        if ("[DONE]".equals(data)) break;
                                        try {
                                            JsonObject chunk = new Gson().fromJson(data, JsonObject.class);
                                            JsonObject delta = chunk.getAsJsonArray("choices")
                                                    .get(0).getAsJsonObject()
                                                    .getAsJsonObject("delta");
                                            if (delta.has("content")) {
                                                String content = delta.get("content").getAsString();
                                                fullContent.append(content);
                                                String current = fullContent.toString();
                                                requireActivity().runOnUiThread(() ->
                                                        adapter.updateMessageContent(messageId, current));
                                            }
                                        } catch (Exception ignored) {}
                                    }
                                }
                                String finalContent = fullContent.toString();
                                if (finalContent.isEmpty()) finalContent = "AI 返回为空。";
                                saveFinalContent(messageId, finalContent);
                                requireActivity().runOnUiThread(() -> adapter.finalizeMessage(messageId));
                            } catch (Exception e) {
                                saveFinalContent(messageId, "流式读取失败：" + e.getMessage());
                            }
                        }).start();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (!isAdded()) return;
                        saveFinalContent(messageId, "请求失败：" + t.getClass().getSimpleName()
                                + "，" + t.getMessage());
                    }
                });
    }

    private void saveFinalContent(int messageId, String content) {
        new Thread(() -> {
            List<ChatMessageEntity> messages = db.chatMessageDao().getBySession(currentSessionId);
            for (ChatMessageEntity msg : messages) {
                if (msg.id == messageId) {
                    msg.content = content;
                    msg.createdAt = System.currentTimeMillis();
                    db.chatMessageDao().update(msg);
                    break;
                }
            }
            ChatSessionEntity session = db.chatSessionDao().getById(currentSessionId);
            if (session != null) {
                session.updatedAt = System.currentTimeMillis();
                db.chatSessionDao().update(session);
            }
            requireActivity().runOnUiThread(() -> adapter.finalizeMessage(messageId));
        }).start();
    }

    private void showSessionsDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_chat_sessions, null);
        RecyclerView sessionsView = dialogView.findViewById(R.id.recyclerSessions);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(dialogView).create();
        ChatSessionAdapter sessionAdapter = new ChatSessionAdapter(new ChatSessionAdapter.Listener() {
            @Override
            public void onOpen(ChatSessionEntity session) {
                currentSessionId = session.id;
                dialog.dismiss();
                loadMessages();
            }
            @Override
            public void onDelete(ChatSessionEntity session) {
                deleteSession(session.id, () -> { dialog.dismiss(); loadLatestOrCreate(); });
            }
        });
        sessionsView.setLayoutManager(new LinearLayoutManager(requireContext()));
        sessionsView.setAdapter(sessionAdapter);
        new Thread(() -> {
            List<ChatSessionEntity> sessions = db.chatSessionDao().getByUsername(username);
            requireActivity().runOnUiThread(() -> sessionAdapter.setData(sessions));
        }).start();
        dialog.show();
    }

    private void confirmDeleteCurrentSession() {
        if (currentSessionId == 0) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("删除当前会话")
                .setMessage("确定删除吗？")
                .setPositiveButton("删除", (d, w) -> deleteSession(currentSessionId, this::loadLatestOrCreate))
                .setNegativeButton("取消", null).show();
    }

    private void deleteSession(int sessionId, Runnable afterDelete) {
        new Thread(() -> {
            db.chatMessageDao().deleteBySession(sessionId);
            db.chatSessionDao().deleteById(sessionId);
            if (currentSessionId == sessionId) currentSessionId = 0;
            requireActivity().runOnUiThread(afterDelete);
        }).start();
    }

    private void showMessageActions(ChatMessageEntity message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("消息操作")
                .setItems(new CharSequence[]{"复制", "删除"}, (dialog, which) -> {
                    if (which == 0) {
                        ClipboardManager cm = (ClipboardManager) requireContext()
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText("msg", message.content));
                        Toast.makeText(requireContext(), "已复制", Toast.LENGTH_SHORT).show();
                    } else {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("删除消息").setMessage("确定删除这条消息吗？")
                                .setPositiveButton("删除", (d, w) -> deleteMessage(message))
                                .setNegativeButton("取消", null).show();
                    }
                }).show();
    }

    private void deleteMessage(ChatMessageEntity message) {
        new Thread(() -> {
            db.chatMessageDao().deleteById(message.id);
            requireActivity().runOnUiThread(this::loadMessages);
        }).start();
    }

    private String makeTitle(String text) {
        return text.length() > 16 ? text.substring(0, 16) + "..." : text;
    }

    private void scrollToBottom() {
        recyclerChat.post(() -> {
            if (adapter.getItemCount() > 0)
                recyclerChat.smoothScrollToPosition(adapter.getItemCount() - 1);
        });
    }
}
