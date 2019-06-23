package com.example.coyg.todolist.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MainViewModel extends AndroidViewModel
{
    private LiveData<List<TaskEntry>> task;
    private LiveData<List<RemainderEntry>> remainder;

    public MainViewModel(@NonNull Application application)
    {
        super (application);
        AppDatabase appDatabase = AppDatabase.getsInstance (this.getApplication ());

        task = appDatabase.taskDAO ().loadAllTasks ();
        remainder = appDatabase.remainderDAO ().loadAllRemainders ();
    }

    public LiveData<List<TaskEntry>> getTask()
    {
        return task;
    }
    public LiveData<List<RemainderEntry>> getRemainder()
    {
        return remainder;
    }

}
