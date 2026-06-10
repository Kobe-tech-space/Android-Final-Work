package com.example.finalwork.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalwork.R;
import com.example.finalwork.database.AppDatabase;
import com.example.finalwork.entity.UserEntity;

public class ChangePasswordActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_change_password);
        db = AppDatabase.getInstance(this);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etOldPassword = findViewById(R.id.etOldPassword);
        EditText etNewPassword = findViewById(R.id.etNewPassword);

        findViewById(R.id.btnChangePassword).setOnClickListener(v -> new Thread(() -> {
            String username = etUsername.getText().toString().trim();
            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();

            if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show());
                return;
            }

            UserEntity user = db.userDao().login(username, oldPassword);
            if (user != null) {
                db.userDao().updatePassword(username, newPassword);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, user != null ? "修改成功" : "用户不存在或旧密码错误", Toast.LENGTH_SHORT).show();
                if (user != null) finish();
            });
        }).start());
    }
}
