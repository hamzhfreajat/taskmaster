package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmaster.R;
import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.TaskData;

import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        TextView textTitle = findViewById(R.id.text_title);
        TextView textDesc = findViewById(R.id.text_description);
        TextView textStatus = findViewById(R.id.text_status);
        Intent intent = getIntent();

        TaskData taskData = AppDatabase.getInstance(this).taskDao().getTaskByID(Long.valueOf(intent.getStringExtra("id")));
        textTitle.setText(taskData.getTitle());
        textDesc.setText(taskData.getBody());
        textStatus.setText(taskData.getState().toString());
    }
}