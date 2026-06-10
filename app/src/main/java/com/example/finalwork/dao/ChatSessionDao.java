package com.example.finalwork.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.finalwork.entity.ChatSessionEntity;
import java.util.List;

@Dao
public interface ChatSessionDao {
    @Insert
    long insert(ChatSessionEntity session);

    @Update
    void update(ChatSessionEntity session);

    @Delete
    void delete(ChatSessionEntity session);

    @Query("SELECT * FROM chat_sessions WHERE username = :username ORDER BY updatedAt DESC")
    List<ChatSessionEntity> getByUsername(String username);

    @Query("SELECT * FROM chat_sessions WHERE id = :id LIMIT 1")
    ChatSessionEntity getById(int id);

    @Query("DELETE FROM chat_sessions WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM chat_sessions WHERE username = :username")
    void deleteAllByUsername(String username);
}
