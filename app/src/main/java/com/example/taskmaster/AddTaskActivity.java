package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Button addTask = findViewById(R.id.addTask);
        addTask.setOnClickListener(v -> {
            Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();
        });
    }
}