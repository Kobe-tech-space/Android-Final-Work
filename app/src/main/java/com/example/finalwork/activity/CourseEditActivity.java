package com.example.finalwork.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalwork.R;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.CourseEntity;
import java.util.List;

public class CourseEditActivity extends AppCompatActivity {
    AppDatabase db;
    CourseEntity course;
    EditText name, teacher, room;
    Spinner weekdaySpinner, startSpinner, endSpinner;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_course_edit);
        db = AppDatabase.getInstance(this);
        name = findViewById(R.id.etCourseName);
        teacher = findViewById(R.id.etTeacher);
        room = findViewById(R.id.etClassroom);
        weekdaySpinner = findViewById(R.id.spWeekday);
        startSpinner = findViewById(R.id.spStartSection);
        endSpinner = findViewById(R.id.spEndSection);
        setupSpinners();
        int id = getIntent().getIntExtra("id", 0);
        if (id > 0) {
            new Thread(() -> {
                course = db.courseDao().getById(id);
                runOnUiThread(() -> {
                    if (course != null) {
                        name.setText(course.courseName);
                        teacher.setText(course.teacher);
                        room.setText(course.classroom);
                        weekdaySpinner.setSelection(Math.max(0, course.weekday - 1));
                        startSpinner.setSelection(Math.max(0, course.startSection - 1));
                        endSpinner.setSelection(Math.max(0, course.endSection - 1));
                    }
                });
            }).start();
        }
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }

    private void setupSpinners() {
        String[] weekdays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        String[] sections = new String[12];
        for (int i = 0; i < sections.length; i++) {
            sections[i] = "第 " + (i + 1) + " 节";
        }
        weekdaySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, weekdays));
        startSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sections));
        endSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sections));
    }

    void save() {
        String courseName = name.getText().toString().trim();
        String teacherName = teacher.getText().toString().trim();
        String classroom = room.getText().toString().trim();
        int startSection = startSpinner.getSelectedItemPosition() + 1;
        int endSection = endSpinner.getSelectedItemPosition() + 1;
        if (courseName.isEmpty() || teacherName.isEmpty() || classroom.isEmpty()) {
            Toast.makeText(this, "请填写完整课程信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (endSection < startSection) {
            Toast.makeText(this, "结束节次不能早于开始节次", Toast.LENGTH_SHORT).show();
            return;
        }
        if (course == null) course = new CourseEntity();
        course.courseName = courseName;
        course.teacher = teacherName;
        course.classroom = classroom;
        course.weekday = weekdaySpinner.getSelectedItemPosition() + 1;
        course.startSection = startSection;
        course.endSection = endSection;
        if (course.username == null) {
            course.username = new com.example.finalwork.utils.SessionManager(this).getUsername();
        }
        new Thread(() -> {
            // 冲突检测（仅新增或修改了星期/节次时）
            if (course.id <= 0 || true) {
                List<com.example.finalwork.entity.CourseEntity> conflicts =
                        db.courseDao().findConflict(course.username, course.weekday,
                                course.startSection, course.endSection);
                boolean hasConflict = false;
                for (com.example.finalwork.entity.CourseEntity c : conflicts) {
                    if (c.id != course.id) { hasConflict = true; break; }
                }
                if (hasConflict) {
                    runOnUiThread(() -> Toast.makeText(this,
                            "⚠ 该时间段已有课程，请检查", Toast.LENGTH_SHORT).show());
                }
            }
            if (course.id > 0) db.courseDao().update(course);
            else db.courseDao().insert(course);
            runOnUiThread(() -> {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
