package com.example.finalwork.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.finalwork.R;
import com.example.finalwork.activity.ExamActivity;
import com.example.finalwork.activity.LoginActivity;
import com.example.finalwork.api.DeepSeekClient;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.ChatSessionEntity;
import com.example.finalwork.entity.CourseEntity;
import com.example.finalwork.entity.ExamEntity;
import com.example.finalwork.entity.TodoEntity;
import com.example.finalwork.utils.SessionManager;
import com.example.finalwork.utils.ThemeManager;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

public class ProfileFragment extends Fragment {
    private AppDatabase db;
    private TextView courseCount, todoCount, examCount, aiCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        db = AppDatabase.getInstance(requireContext());
        SessionManager sessionManager = new SessionManager(requireContext());
        ((TextView) view.findViewById(R.id.tvProfileName)).setText(sessionManager.getUsername());
        courseCount = view.findViewById(R.id.tvCourseCount);
        todoCount = view.findViewById(R.id.tvTodoCount);
        examCount = view.findViewById(R.id.tvExamCount);
        aiCount = view.findViewById(R.id.tvAiCount);
        SwitchMaterial darkModeSwitch = view.findViewById(R.id.switchDarkMode);
        darkModeSwitch.setChecked(ThemeManager.isDarkMode(requireContext()));
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> ThemeManager.setDarkMode(requireContext(), isChecked));
        view.findViewById(R.id.btnExam).setOnClickListener(v -> startActivity(new Intent(requireContext(), ExamActivity.class)));
        view.findViewById(R.id.btnApiKey).setOnClickListener(v -> showApiKeyDialog(sessionManager));
        view.findViewById(R.id.btnClearAi).setOnClickListener(v -> confirmClearAiHistory());
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        loadStats();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
    }

    private void showApiKeyDialog(SessionManager sessionManager) {
        EditText input = new EditText(requireContext());
        input.setHint("输入你的 DeepSeek API Key");
        input.setText(sessionManager.getApiKey());
        input.setPadding(32, 24, 32, 24);
        input.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
        input.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint));
        input.setBackgroundResource(R.drawable.bg_input_round);

        LinearLayout container = new LinearLayout(requireContext());
        container.setPadding(16, 16, 16, 16);
        container.addView(input);

        new AlertDialog.Builder(requireContext())
                .setTitle("🔑 设置 API Key")
                .setMessage("请输入你的 DeepSeek API Key。\n仅支持 api.deepseek.com 的 Key。")
                .setView(container)
                .setPositiveButton("保存", (dialog, which) -> {
                    String key = input.getText().toString().trim();
                    sessionManager.saveApiKey(key);
                    DeepSeekClient.setApiKey(key);
                    Toast.makeText(requireContext(),
                            key.isEmpty() ? "已清除 API Key" : "API Key 已保存", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void loadStats() {
        if (db == null) return;
        new Thread(() -> {
            String username = new SessionManager(requireContext()).getUsername();
            List<CourseEntity> courses = db.courseDao().getByUsername(username);
            List<TodoEntity> todos = db.todoDao().getTopUnfinished(username);
            List<ExamEntity> exams = db.examDao().getByUsername(username);
            List<ChatSessionEntity> sessions = db.chatSessionDao().getByUsername(username);
            requireActivity().runOnUiThread(() -> {
                courseCount.setText("📚 课程\n" + courses.size());
                todoCount.setText("✅ 待办\n" + todos.size());
                examCount.setText("📝 考试\n" + exams.size());
                aiCount.setText("🤖 会话\n" + sessions.size());
            });
        }).start();
    }

    private void confirmClearAiHistory() {
        new AlertDialog.Builder(requireContext())
                .setTitle("清空 AI 历史")
                .setMessage("确定删除所有 AI 会话和聊天消息吗？")
                .setPositiveButton("清空", (dialog, which) -> clearAiHistory())
                .setNegativeButton("取消", null)
                .show();
    }

    private void clearAiHistory() {
        new Thread(() -> {
            String username = new SessionManager(requireContext()).getUsername();
            List<ChatSessionEntity> sessions = db.chatSessionDao().getByUsername(username);
            for (ChatSessionEntity session : sessions) {
                db.chatMessageDao().deleteBySession(session.id);
                db.chatSessionDao().deleteById(session.id);
            }
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "AI 历史已清空", Toast.LENGTH_SHORT).show();
                loadStats();
            });
        }).start();
    }
}
