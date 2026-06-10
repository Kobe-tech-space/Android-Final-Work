package com.example.finalwork.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.finalwork.entity.CourseEntity;
import java.util.List;

@Dao
public interface CourseDao {
    @Insert
    long insert(CourseEntity course);

    @Update
    void update(CourseEntity course);

    @Delete
    void delete(CourseEntity course);

    @Query("SELECT * FROM courses WHERE username = :username ORDER BY weekday, startSection")
    List<CourseEntity> getByUsername(String username);

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    CourseEntity getById(int id);

    @Query("SELECT * FROM courses WHERE username = :username AND weekday = :weekday ORDER BY startSection")
    List<CourseEntity> getByWeekday(String username, int weekday);

    /** 冲突检测：同用户、同星期、节次重叠 */
    @Query("SELECT * FROM courses WHERE username = :username AND weekday = :weekday " +
           "AND NOT (endSection < :startSec OR startSection > :endSec)")
    List<CourseEntity> findConflict(String username, int weekday, int startSec, int endSec);
}
