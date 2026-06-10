package com.example.finalwork.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.finalwork.entity.ChatMessageEntity;
import java.util.List;

@Dao
public interface ChatMessageDao {
    @Insert
    long insert(ChatMessageEntity message);

    @Delete
    void delete(ChatMessageEntity message);

    @Update
    void update(ChatMessageEntity message);

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY createdAt ASC, id ASC")
    List<ChatMessageEntity> getBySession(int sessionId);

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    void deleteBySession(int sessionId);

    @Query("DELETE FROM chat_messages WHERE id = :id")
    void deleteById(int id);
}
