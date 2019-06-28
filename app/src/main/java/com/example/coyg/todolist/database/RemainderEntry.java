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
    private double lat;
    private double lng;
    private String type;

    public RemainderEntry(String name, double lat, double lng, String type)
    {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
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

    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLng()
    {
        return lng;
    }

    public void setLng(double lng)
    {
        this.lng = lng;
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
