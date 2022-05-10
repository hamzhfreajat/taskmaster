package com.example.taskmaster.data;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_data")
public class TaskData {
    @PrimaryKey(autoGenerate = true)
    private Long id ;
    @ColumnInfo(name = "title")
    private String title ;
    @ColumnInfo(name = "body")
    private String body ;



    @ColumnInfo(name = "state")
    private String state ;

    public TaskData(String title, String body, String state) {
        this.title = title;
        this.body = body;
       setState(state);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public String getState() {
        return state;
    }

    public Long getId() {
        return id;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setState(String state) {
        switch (state){
            case "new" :
                this.state = state;
                break;
            case "assigned" :
                this.state = state;
                break;
            case "in progress" :
                this.state =state;
                break;
            case "complete" :
                this.state = state;
                break;
            default:
                Log.i("error" , "The input word is wrong");
        }
    }
}
