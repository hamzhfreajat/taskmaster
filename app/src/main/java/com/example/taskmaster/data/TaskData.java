package com.example.taskmaster.data;

import android.util.Log;

public class TaskData {
    private String title ;
    private String body ;
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

    public String getBody() {
        return body;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return "TaskData{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", state='" + state + '\'' +
                '}';
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
