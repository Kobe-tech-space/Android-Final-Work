package com.example.finalwork.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.finalwork.entity.TodoEntity;
import java.util.List;

@Dao
public interface TodoDao {
    @Insert
    long insert(TodoEntity todo);

    @Update
    void update(TodoEntity todo);

    @Delete
    void delete(TodoEntity todo);

    @Query("SELECT * FROM todos WHERE username = :username ORDER BY completed ASC, deadline ASC")
    List<TodoEntity> getByUsername(String username);

    @Query("SELECT * FROM todos WHERE id = :id LIMIT 1")
    TodoEntity getById(int id);

    @Query("SELECT * FROM todos WHERE username = :username AND completed = 0 ORDER BY deadline ASC LIMIT 5")
    List<TodoEntity> getTopUnfinished(String username);
}
