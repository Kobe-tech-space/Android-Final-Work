package com.example.finalwork.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.finalwork.entity.ExamEntity;
import java.util.List;

@Dao
public interface ExamDao {
    @Insert
    long insert(ExamEntity exam);

    @Update
    void update(ExamEntity exam);

    @Delete
    void delete(ExamEntity exam);

    @Query("SELECT * FROM exams WHERE username = :username ORDER BY examTime ASC")
    List<ExamEntity> getByUsername(String username);

    @Query("SELECT * FROM exams WHERE id = :id LIMIT 1")
    ExamEntity getById(int id);
}
