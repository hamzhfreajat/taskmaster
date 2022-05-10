package com.example.taskmaster.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task_data")
    List<TaskData> getAll();

    @Query("SELECT * FROM task_data WHERE id = :id")
    TaskData getTaskByID(Long id);

    @Insert
    Long insertTask(TaskData task);
}
