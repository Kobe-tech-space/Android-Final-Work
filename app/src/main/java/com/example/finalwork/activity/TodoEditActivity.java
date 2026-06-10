package com.example.finalwork.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import com.example.finalwork.R;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.TodoEntity;

public class TodoEditActivity extends AppCompatActivity {
    AppDatabase db;
    TodoEntity todo;
    EditText title, content, deadline;
    Spinner categorySpinner, prioritySpinner;
    String[] categories = {"作业", "复习", "考试", "生活", "社团"};
    String[] priorities = {"高", "中", "低"};

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_todo_edit);
        db = AppDatabase.getInstance(this);
        title = findViewById(R.id.etTitle);
        content = findViewById(R.id.etContent);
        deadline = findViewById(R.id.etDeadline);
        deadline.setFocusable(false);
        deadline.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (dp, year, month, day) ->
                    deadline.setText(year + "-" + String.format("%02d", month + 1)
                            + "-" + String.format("%02d", day)),
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });
        categorySpinner = findViewById(R.id.spCategory);
        prioritySpinner = findViewById(R.id.spPriority);
        categorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));
        prioritySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorities));
        int id = getIntent().getIntExtra("id", 0);
        if (id > 0) {
            new Thread(() -> {
                todo = db.todoDao().getById(id);
                runOnUiThread(() -> {
                    title.setText(todo.title);
                    content.setText(todo.content);
                    deadline.setText(todo.deadline);
                    setSpinner(categorySpinner, categories, todo.category == null ? "作业" : todo.category);
                    setSpinner(prioritySpinner, priorities, todo.priority == null ? "中" : todo.priority);
                });
            }).start();
        }
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }

    void save() {
        if (title.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (todo == null) todo = new TodoEntity();
        todo.title = title.getText().toString().trim();
        todo.content = content.getText().toString().trim();
        todo.deadline = deadline.getText().toString().trim();
        todo.category = categorySpinner.getSelectedItem().toString();
        todo.priority = prioritySpinner.getSelectedItem().toString();
        if (todo.username == null) {
            todo.username = new com.example.finalwork.utils.SessionManager(this).getUsername();
        }
        new Thread(() -> {
            if (todo.id > 0) db.todoDao().update(todo); else db.todoDao().insert(todo);
            runOnUiThread(() -> {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    void setSpinner(Spinner spinner, String[] values, String value) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }
}
