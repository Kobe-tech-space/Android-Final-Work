package com.example.finalwork.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses")
public class CourseEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public String courseName;
    public String teacher;
    public String classroom;
    public int weekday;
    public int startSection;
    public int endSection;
}
