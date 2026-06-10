package com.example.finalwork.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalwork.R;
import com.example.finalwork.activity.TodoEditActivity;
import com.example.finalwork.adapter.TodoAdapter;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.TodoEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class TodoFragment extends Fragment {
    AppDatabase db;
    TodoAdapter adapter;
    TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        db = AppDatabase.getInstance(requireContext());
        emptyView = view.findViewById(R.id.tvEmpty);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TodoAdapter(new TodoAdapter.Listener() {
            @Override
            public void onToggle(TodoEntity todo) {
                todo.completed = !todo.completed;
                new Thread(() -> {
                    db.todoDao().update(todo);
                    load();
                }).start();
            }

            @Override
            public void onDelete(TodoEntity todo) {
                new Thread(() -> {
                    db.todoDao().delete(todo);
                    load();
                }).start();
            }

            @Override
            public void onEdit(TodoEntity todo) {
                Intent intent = new Intent(requireContext(), TodoEditActivity.class);
                intent.putExtra("id", todo.id);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        ((TextView) view.findViewById(R.id.tvPageTitle)).setText("待办事项");
        ((FloatingActionButton) view.findViewById(R.id.fabAdd)).setOnClickListener(v -> startActivity(new Intent(requireContext(), TodoEditActivity.class)));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    void load() {
        new Thread(() -> {
            String username = new com.example.finalwork.utils.SessionManager(requireContext()).getUsername();
            List<TodoEntity> data = db.todoDao().getByUsername(username);
            requireActivity().runOnUiThread(() -> {
                adapter.setData(data);
                emptyView.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
                emptyView.setText("暂无待办，点击右下角添加任务");
            });
        }).start();
    }
}
