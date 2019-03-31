package com.example.coyg.todolist.notes;

public class GetNoteCloud
{
    private String date;
    private String note;

    public GetNoteCloud(String date, String note)
    {
        this.date = date;
        this.note = note;
    }

    public String getDate()
    {
        return date;
    }


    public String getNote()
    {
        return note;
    }
}
