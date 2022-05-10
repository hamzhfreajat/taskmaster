package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.TaskData;

import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText taskTitle;
    private EditText taskDesc;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Button addTask = findViewById(R.id.btn_submit_task);
        addTask.setOnClickListener(v -> {

            taskTitle = findViewById(R.id.edit_task_title);
            taskDesc = findViewById(R.id.edit_task_desc);
            spinner = findViewById(R.id.spinner);


            TaskData taskData = new TaskData(taskTitle.getText().toString() , taskDesc.getText().toString() ,spinner.getSelectedItem().toString());
            Long newTask = AppDatabase.getInstance(getApplicationContext()).taskDao().insertTask(taskData);
            startActivity(new Intent(getApplicationContext() , MainActivity.class));
        });
    }
}