package com.example.finalwork.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalwork.R;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.ExamEntity;
import com.example.finalwork.utils.NotificationHelper;

public class ExamEditActivity extends AppCompatActivity {
    private AppDatabase db;
    private ExamEntity exam;
    private EditText etName, etLocation, etTime;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_exam_edit);
        db = AppDatabase.getInstance(this);
        etName = findViewById(R.id.etExamName);
        etLocation = findViewById(R.id.etLocation);
        etTime = findViewById(R.id.etExamTime);

        int id = getIntent().getIntExtra("id", 0);
        if (id > 0) {
            isEdit = true;
            new Thread(() -> {
                exam = db.examDao().getById(id);
                runOnUiThread(() -> {
                    if (exam != null) {
                        etName.setText(exam.examName);
                        etLocation.setText(exam.location);
                        etTime.setText(exam.examTime);
                    }
                });
            }).start();
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String time = etTime.getText().toString().trim();

        if (name.isEmpty() || location.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "请填写完整考试信息", Toast.LENGTH_SHORT).show();
            return;
        }

        if (exam == null) exam = new ExamEntity();
        exam.examName = name;
        exam.location = location;
        exam.examTime = time;
        if (exam.username == null) {
            exam.username = new com.example.finalwork.utils.SessionManager(this).getUsername();
        }

        new Thread(() -> {
            if (isEdit) {
                db.examDao().update(exam);
            } else {
                long id = db.examDao().insert(exam);
                exam.id = (int) id;
            }
            runOnUiThread(() -> {
                if (!isEdit) {
                    NotificationHelper.notifyExam(this, "考试提醒：" + exam.examName,
                            exam.examTime + " · " + exam.location, exam.id);
                }
                Toast.makeText(this, isEdit ? "修改成功" : "考试已添加并创建提醒", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
