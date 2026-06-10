package com.example.finalwork.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalwork.R;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.UserEntity;

public class RegisterActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_register);
        db = AppDatabase.getInstance(this);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirm = findViewById(R.id.etConfirmPassword);

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm)) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> {
                UserEntity existing = db.userDao().findByUsername(username);
                if (existing == null) {
                    db.userDao().insert(new UserEntity(username, password));
                }
                runOnUiThread(() -> {
                    if (existing == null) {
                        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}
