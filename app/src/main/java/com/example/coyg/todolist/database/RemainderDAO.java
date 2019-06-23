package com.example.coyg.todolist.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RemainderDAO
{
    @Query("SELECT * FROM remainder")
    LiveData<List<RemainderEntry>> loadAllRemainders();

    @Insert
    void insertRemainder(RemainderEntry remainderEntry);

    @Delete
    void delete (RemainderEntry remainderEntry);

    @Query ("DELETE FROM remainder")
    void deleteALL();

    @Query ("SELECT * FROM remainder WHERE id = :id")
    LiveData<RemainderEntry> loadRemainderById(String id);
}
