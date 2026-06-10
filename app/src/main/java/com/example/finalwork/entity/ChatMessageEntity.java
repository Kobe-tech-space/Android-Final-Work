package com.example.finalwork.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_messages")
public class ChatMessageEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int sessionId;
    public String content;
    public int type;
    public long createdAt;

    public ChatMessageEntity(int sessionId, String content, int type, long createdAt) {
        this.sessionId = sessionId;
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
    }
}
