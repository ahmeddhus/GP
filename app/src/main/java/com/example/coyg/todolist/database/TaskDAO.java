package com.example.coyg.todolist.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDAO
{
    @Query ("SELECT * FROM task")
    LiveData<List<TaskEntry>> loadAllTasks();

    @Insert
    void insertTask(TaskEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(TaskEntry taskEntry);

    @Delete
    void delete (TaskEntry taskEntry);

    @Query ("DELETE FROM task")
    void deleteALL();

    @Query ("SELECT * FROM task WHERE id = :id")
    LiveData<TaskEntry> loadTaskById(String id);
}
