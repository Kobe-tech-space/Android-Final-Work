package com.example.finalwork.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalwork.R;
import com.example.finalwork.adapter.ExamAdapter;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.ExamEntity;
import java.util.List;

public class ExamActivity extends AppCompatActivity {
    private AppDatabase db;
    private ExamAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_exam);
        db = AppDatabase.getInstance(this);
        emptyView = findViewById(R.id.tvEmpty);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExamAdapter(new ExamAdapter.Listener() {
            @Override
            public void onEdit(ExamEntity exam) {
                Intent intent = new Intent(ExamActivity.this, ExamEditActivity.class);
                intent.putExtra("id", exam.id);
                startActivity(intent);
            }

            @Override
            public void onDelete(ExamEntity exam) {
                new Thread(() -> {
                    db.examDao().delete(exam);
                    load();
                }).start();
            }
        });
        recyclerView.setAdapter(adapter);
        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new Intent(this, ExamEditActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        new Thread(() -> {
            String username = new com.example.finalwork.utils.SessionManager(this).getUsername();
            List<ExamEntity> data = db.examDao().getByUsername(username);
            runOnUiThread(() -> {
                adapter.setData(data);
                emptyView.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
            });
        }).start();
    }
}
