package com.example.taskmaster.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities ={TaskData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static AppDatabase taskData ;

    public AppDatabase(){

    }

    public static synchronized AppDatabase getInstance(Context context){
        if(taskData == null)
        {
            taskData = Room.databaseBuilder(context,
                    AppDatabase.class, "AppDatabase").allowMainThreadQueries().build();
        }
        return taskData;
    }

}
