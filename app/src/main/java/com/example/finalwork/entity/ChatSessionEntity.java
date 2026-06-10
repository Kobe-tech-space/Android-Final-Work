package com.example.finalwork.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_sessions")
public class ChatSessionEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public String title;
    public long createdAt;
    public long updatedAt;

    public ChatSessionEntity(String username, String title, long createdAt, long updatedAt) {
        this.username = username;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
