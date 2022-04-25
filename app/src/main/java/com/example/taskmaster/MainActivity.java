package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button addTask = findViewById(R.id.addTask);
        addTask.setOnClickListener(view -> {
            Intent addTaskActivity = new Intent(getApplicationContext() , AddTaskActivity.class);
            startActivity(addTaskActivity);
        });

        Button allTask = findViewById(R.id.allTask);
        allTask.setOnClickListener(view -> {
            Intent allTaskActivity = new Intent(getApplicationContext() , AllTaskActivity.class);
            startActivity(allTaskActivity);
        });
    }
}