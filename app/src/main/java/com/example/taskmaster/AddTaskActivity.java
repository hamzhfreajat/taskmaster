package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.TaskData;

import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = AddTaskActivity.class.getName();
    private EditText taskTitle;
    private EditText taskDesc;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


//        try {
//            Amplify.addPlugin(new AWSDataStorePlugin());
//            Amplify.addPlugin(new AWSApiPlugin());
//            Amplify.configure(getApplicationContext());
//
//            Log.i("Tutorial", "Initialized Amplify");
//        } catch (AmplifyException e) {
//            Log.e("Tutorial", "Could not initialize Amplify", e);
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Button addTask = findViewById(R.id.btn_submit_task);
        addTask.setOnClickListener(v -> {

            taskTitle = findViewById(R.id.edit_task_title);
            taskDesc = findViewById(R.id.edit_task_desc);
            spinner = findViewById(R.id.spinner);


            Task item = Task.builder()
                    .title(taskTitle.getText().toString())
                    .body(taskDesc.getText().toString())
                    .status(spinner.getSelectedItem().toString())
                    .build();
//
//            Amplify.DataStore.save(item,
//                    success -> Log.i(TAG, "Saved item "),
//                    error -> Log.e(TAG, "Could not save item to DataStore", error)
//            );

            Amplify.API.mutate(ModelMutation.create(item) ,
                    success -> Log.i(TAG, "Saved item API") ,
                    error -> Log.e(TAG, "Could not save item to DataStore", error)
            );

//            TaskData taskData = new TaskData(taskTitle.getText().toString() , taskDesc.getText().toString() ,spinner.getSelectedItem().toString());
//            Long newTask = AppDatabase.getInstance(getApplicationContext()).taskDao().insertTask(taskData);
            startActivity(new Intent(getApplicationContext() , MainActivity.class));
        });
    }
}