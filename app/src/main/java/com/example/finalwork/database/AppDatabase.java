package com.example.finalwork.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.finalwork.dao.ChatMessageDao;
import com.example.finalwork.dao.ChatSessionDao;
import com.example.finalwork.dao.CourseDao;
import com.example.finalwork.dao.ExamDao;
import com.example.finalwork.dao.TodoDao;
import com.example.finalwork.dao.UserDao;
import com.example.finalwork.entity.ChatMessageEntity;
import com.example.finalwork.entity.ChatSessionEntity;
import com.example.finalwork.entity.CourseEntity;
import com.example.finalwork.entity.ExamEntity;
import com.example.finalwork.entity.TodoEntity;
import com.example.finalwork.entity.UserEntity;

@Database(entities = {UserEntity.class, CourseEntity.class, TodoEntity.class, ExamEntity.class, ChatSessionEntity.class, ChatMessageEntity.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract CourseDao courseDao();
    public abstract TodoDao todoDao();
    public abstract ExamDao examDao();
    public abstract ChatSessionDao chatSessionDao();
    public abstract ChatMessageDao chatMessageDao();

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE chat_sessions ADD COLUMN username TEXT");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE courses ADD COLUMN username TEXT");
            db.execSQL("ALTER TABLE todos ADD COLUMN username TEXT");
            db.execSQL("ALTER TABLE exams ADD COLUMN username TEXT");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "campus_ai_db")
                            .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                            .build();
                }
            }
        }
        return instance;
    }
}
