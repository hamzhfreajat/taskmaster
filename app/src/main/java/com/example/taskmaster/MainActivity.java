package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.TaskData;
import com.example.taskmaster.ui.CustomRecyclerView;
import com.example.taskmaster.ui.SettingActivity;
import com.example.taskmaster.ui.TaskDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mUserTitle;
//    private Button labTask;
//    private Button codeChallangeTask;
//    private Button sleepTask;
    List<TaskData> taskData = new ArrayList<>();
    private Handler handler;
    private List<Task> mytasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Tutorial", "Could not initialize Amplify", e);
        }


//        getData();

//        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//
//        CustomRecyclerView customRecyclerView = new CustomRecyclerView(taskData);
//
//        recyclerView.setAdapter(customRecyclerView);
//
//        recyclerView.setHasFixedSize(true);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button addTaskBtn = findViewById(R.id.btn_add_task);
        addTaskBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext() , AddTaskActivity.class));
        });


        mUserTitle = findViewById(R.id.text_user);
//        labTask = findViewById(R.id.btn_taskmyTaskText1);
//        codeChallangeTask = findViewById(R.id.btn_task2);
//        sleepTask = findViewById(R.id.btn_task3);

//        navigateToDetail();


//        Button allTask = findViewById(R.id.allTask);
//        allTask.setOnClickListener(view -> {
//            Intent allTaskActivity = new Intent(getApplicationContext() , AllTaskActivity.class);
//            startActivity(allTaskActivity);
//        });
//        mUserTitle = findViewById(R.id.myTaskText);

    }

    @Override
    protected void onResume() {
        setUserName();
        mytasks = new ArrayList<>();
        Handler handler = new Handler(Looper.getMainLooper() , msg -> {
            RecyclerView recyclerView = findViewById(R.id.recycler_view);

            CustomRecyclerView customRecyclerView = new CustomRecyclerView(mytasks, new CustomRecyclerView.CustomClickListener() {
                @Override
                public void onTaskItemClicked(int position) {
                    Intent taskDetailActivity = new Intent(getApplicationContext() , TaskDetailActivity.class);
                    taskDetailActivity.putExtra("id" ,  mytasks.get(position).getId().toString());
                    startActivity(taskDetailActivity);
                }
            });

            recyclerView.setAdapter(customRecyclerView);



            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            return true ;
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String teamName = sharedPreferences.getString("teamName" , "") ;
        Log.i("teamName" , teamName) ;
        Amplify.API.query(
                ModelQuery.list(Team.class ,Team.NAME.contains(teamName) ),
                response -> {

                    for (Team team : response.getData()) {
                        mytasks = team.getTasks();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("data" , "Done");

                    Message message = new Message();
                    message.setData(bundle);
                    handler.sendMessage(message);

                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );


//        List<TaskData> taskData2 = AppDatabase.getInstance(this).taskDao().getAll();


        super.onResume();
    }

//    public void getData(){
//        taskData.add(new TaskData("task 1" , "Solve task 1 " , "new"));
//        taskData.add(new TaskData("task 2" , "Solve task 2 " , "assigned"));
//        taskData.add(new TaskData("task 3" , "Solve task 3 " , "assigned"));
//        taskData.add(new TaskData("task 4" , "Solve task 4 " , "in progress"));
//        taskData.add(new TaskData("task 5" , "Solve task 5 " , "in progress"));
//        taskData.add(new TaskData("task 6" , "Solve task 6 " , "complete"));
//        taskData.add(new TaskData("task 7" , "Solve task 7 " , "complete"));
//        taskData.add(new TaskData("task 8" , "Solve task 8 " , "new"));
//        taskData.add(new TaskData("task 9" , "Solve task 9 " , "new"));
//    }
//    @Override
//    protected void onResume() {
//        setUserName();
//        super.onResume();
//    }
//
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
//
    public void navigateToSettings(){
        Intent settingIntent = new Intent(this , SettingActivity.class);
        startActivity(settingIntent);
    }
//    public void navigateToDetail(){
//        labTask.setOnClickListener(view -> {
//            Intent taskDetailActivity = new Intent(getApplicationContext() , TaskDetailActivity.class);
//            taskDetailActivity.putExtra("title" , labTask.getText().toString());
//            startActivity(taskDetailActivity);
//        });
//
//        codeChallangeTask.setOnClickListener(view -> {
//            Intent taskDetailActivity = new Intent(getApplicationContext() , TaskDetailActivity.class);
//            taskDetailActivity.putExtra("title" , codeChallangeTask.getText().toString());
//            startActivity(taskDetailActivity);
//        });
//
//        sleepTask.setOnClickListener(view -> {
//            Intent taskDetailActivity = new Intent(getApplicationContext() , TaskDetailActivity.class);
//            taskDetailActivity.putExtra("title" , sleepTask.getText().toString());
//            startActivity(taskDetailActivity);
//        });
//    }
//
    public void setUserName(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserTitle.setText( sharedPreferences.getString("teamName" , "team1") + " : " +sharedPreferences.getString("username" , "My") +" Tasks");
    }
}