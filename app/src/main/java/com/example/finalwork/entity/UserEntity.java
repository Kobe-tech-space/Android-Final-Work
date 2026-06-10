package com.example.finalwork.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public String password;

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
