package com.example.coyg.todolist.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "remainder")
public class RemainderEntry
{
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;
    private String name;
    private String latLng;
    private String type;

    public RemainderEntry(String name, String latLng, String type)
    {
        this.name = name;
        this.latLng = latLng;
        this.type = type;
    }

    @NonNull
    public int getId()
    {
        return id;
    }

    public void setId(@NonNull int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLatLng()
    {
        return latLng;
    }

    public void setLatLng(String latLng)
    {
        this.latLng = latLng;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
