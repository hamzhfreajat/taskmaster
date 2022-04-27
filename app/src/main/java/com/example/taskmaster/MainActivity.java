package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmaster.ui.SettingActivity;
import com.example.taskmaster.ui.TaskDetailActivity;

public class MainActivity extends AppCompatActivity {

    private TextView mUserTitle;
    private Button labTask;
    private Button codeChallangeTask;
    private Button sleepTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserTitle = findViewById(R.id.myTaskText);
        labTask = findViewById(R.id.btn_task1);
        codeChallangeTask = findViewById(R.id.btn_task2);
        sleepTask = findViewById(R.id.btn_task3);

        navigateToDetail();


//        Button allTask = findViewById(R.id.allTask);
//        allTask.setOnClickListener(view -> {
//            Intent allTaskActivity = new Intent(getApplicationContext() , AllTaskActivity.class);
//            startActivity(allTaskActivity);
//        });
        mUserTitle = findViewById(R.id.myTaskText);

    }

    @Override
    protected void onResume() {
        setUserName();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                navigateToSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void navigateToSettings(){
        Intent setttingIntent = new Intent(this , SettingActivity.class);
        startActivity(setttingIntent);
    }
    public void navigateToDetail(){
        labTask.setOnClickListener(view -> {
            Intent taskDetailActivity = new Intent(getApplicationContext() , TaskDetailActivity.class);
            taskDetailActivity.putExtra("title" , labTask.getText().toString());
            startActivity(taskDetailActivity);
        });

        codeChallangeTask.setOnClickListener(view -> {
            Intent taskDetailActivity = new Intent(getApplicationContext() , TaskDetailActivity.class);
            taskDetailActivity.putExtra("title" , codeChallangeTask.getText().toString());
            startActivity(taskDetailActivity);
        });

        sleepTask.setOnClickListener(view -> {
            Intent taskDetailActivity = new Intent(getApplicationContext() , TaskDetailActivity.class);
            taskDetailActivity.putExtra("title" , sleepTask.getText().toString());
            startActivity(taskDetailActivity);
        });
    }

    public void setUserName(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserTitle.setText(sharedPreferences.getString("username" , "My") + " Tasks");
    }
}