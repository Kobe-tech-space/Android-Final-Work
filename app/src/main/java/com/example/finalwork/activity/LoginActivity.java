package com.example.finalwork.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalwork.R;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.UserEntity;
import com.example.finalwork.utils.SessionManager;
import com.example.finalwork.utils.ThemeManager;

public class LoginActivity extends AppCompatActivity {

    private AppDatabase db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle b) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(b);

        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        db = AppDatabase.getInstance(this);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etApiKey = findViewById(R.id.etApiKey);

        // 回显已保存的 API Key
        String savedKey = session.getApiKey();
        if (!savedKey.isEmpty()) {
            etApiKey.setText(savedKey);
        }

        findViewById(R.id.btnLogin).setOnClickListener(v -> new Thread(() -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String apiKey = etApiKey.getText().toString().trim();
            UserEntity user = db.userDao().login(username, password);
            runOnUiThread(() -> {
                if (user != null) {
                    // 保存 API Key（如果有输入）
                    if (!apiKey.isEmpty()) {
                        session.saveApiKey(apiKey);
                    }
                    session.login(user.username);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            });
        }).start());

        findViewById(R.id.tvRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        findViewById(R.id.tvChangePassword).setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class)));
    }
}
