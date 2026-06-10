package com.example.finalwork.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.finalwork.R;
import com.example.finalwork.activity.ExamActivity;
import com.example.finalwork.activity.MainActivity;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.ChatSessionEntity;
import com.example.finalwork.entity.CourseEntity;
import com.example.finalwork.entity.ExamEntity;
import com.example.finalwork.entity.TodoEntity;
import com.example.finalwork.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    private TextView welcome, course, todo;
    private TextView recentCourse, recentTodo, recentExam, recentAi;
    private TextView tvExamCountdown;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        welcome = view.findViewById(R.id.tvWelcome);
        course = view.findViewById(R.id.tvTodayCourse);
        todo = view.findViewById(R.id.tvTodayTodo);
        recentCourse = view.findViewById(R.id.tvRecentCourse);
        recentTodo = view.findViewById(R.id.tvRecentTodo);
        recentExam = view.findViewById(R.id.tvRecentExam);
        recentAi = view.findViewById(R.id.tvRecentAi);
        tvExamCountdown = view.findViewById(R.id.tvExamCountdown);
        db = AppDatabase.getInstance(requireContext());
        welcome.setText(getGreeting() + "，" + new SessionManager(requireContext()).getUsername());
        view.findViewById(R.id.cardAi).setOnClickListener(v -> openAi(""));
        view.findViewById(R.id.btnStudyPlan).setOnClickListener(v ->
                openAi("请根据我的课程和考试安排，帮我制定一份清晰可执行的学习规划。"));
        view.findViewById(R.id.btnHomework).setOnClickListener(v ->
                openAi("我有一道作业题需要答疑，请你先引导我描述题目，然后分步骤讲解思路。"));
        view.findViewById(R.id.btnKnowledge).setOnClickListener(v ->
                openAi("请帮我解释一个课程知识点，用通俗语言、例子和重点总结来说明。"));
        view.findViewById(R.id.btnResume).setOnClickListener(v ->
                openAi("请帮我优化简历中的项目经历描述，突出技术栈、职责、成果和亮点。"));
        view.findViewById(R.id.cardExam).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ExamActivity.class)));
        load();
        return view;
    }

    private String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 6) return "夜深了";
        if (hour < 12) return "早上好";
        if (hour < 14) return "中午好";
        if (hour < 18) return "下午好";
        return "晚上好";
    }

    private void openAi(String prompt) {
        MainActivity.pendingAiPrompt = prompt;
        BottomNavigationView nav = requireActivity().findViewById(R.id.bottomNavigation);
        nav.setSelectedItemId(R.id.nav_ai);
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        new Thread(() -> {
            String username = new SessionManager(requireContext()).getUsername();
            int weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            if (weekday == 0) weekday = 7;
            List<CourseEntity> courses = db.courseDao().getByWeekday(username, weekday);
            List<TodoEntity> todos = db.todoDao().getTopUnfinished(username);
            List<ExamEntity> exams = db.examDao().getByUsername(username);
            List<ChatSessionEntity> sessions = db.chatSessionDao().getByUsername(username);

            // 最近考试倒计时
            String countdownText = "暂无考试";
            if (!exams.isEmpty()) {
                ExamEntity nearest = exams.get(0);
                try {
                    String datePart = nearest.examTime.length() >= 10
                            ? nearest.examTime.substring(0, 10) : nearest.examTime;
                    Date examDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(datePart);
                    if (examDate != null) {
                        long days = TimeUnit.MILLISECONDS.toDays(
                                examDate.getTime() - System.currentTimeMillis());
                        if (days < 0) countdownText = "已结束";
                        else if (days == 0) countdownText = "今天考试！";
                        else countdownText = "还有 " + days + " 天";
                    }
                } catch (Exception ignored) {}
            }
            String finalCountdown = countdownText;

            requireActivity().runOnUiThread(() -> {
                course.setText("今日课程\n" + courses.size() + " 门");
                todo.setText("待完成事项\n" + todos.size() + " 项");
                recentCourse.setText(courses.isEmpty() ? "📚  暂无课程"
                        : "📚  " + courses.get(0).courseName + " · " + courses.get(0).classroom);
                recentTodo.setText(todos.isEmpty() ? "✅  暂无待办"
                        : "✅  " + todos.get(0).title + " · " + todos.get(0).deadline);
                recentExam.setText(exams.isEmpty() ? "📝  暂无考试"
                        : "📝  " + exams.get(0).examName + " · " + exams.get(0).examTime);
                recentAi.setText(sessions.isEmpty() ? "🤖  暂无历史会话"
                        : "🤖  " + sessions.get(0).title);
                tvExamCountdown.setText(finalCountdown);
            });
        }).start();
    }
}
