package com.example.coyg.todolist.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.coyg.todolist.database.AppDatabase;
import com.example.coyg.todolist.database.TaskEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel
{
    private LiveData<List<TaskEntry>> task;



    public MainViewModel(@NonNull Application application)
    {
        super (application);
        AppDatabase appDatabase = AppDatabase.getsInstance (this.getApplication ());
        task = appDatabase.taskDAO ().loadAllTasks ();
    }

    public LiveData<List<TaskEntry>> getTask()
    {
        return task;
    }
}
