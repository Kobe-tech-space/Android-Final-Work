package com.example.finalwork.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "todos")
public class TodoEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public String title;
    public String content;
    public String deadline;
    public boolean completed;
    public String category;
    public String priority;
}
