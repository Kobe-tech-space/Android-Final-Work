package com.example.finalwork.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exams")
public class ExamEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public String examName;
    public String location;
    public String examTime;
}
