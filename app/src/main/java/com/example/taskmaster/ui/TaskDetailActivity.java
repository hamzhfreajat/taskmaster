package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.R;
import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.TaskData;

import java.util.ArrayList;
import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {

    private String title , desc , status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_detail);
        TextView textTitle = findViewById(R.id.text_title);
        TextView textDesc = findViewById(R.id.text_description);
        TextView textStatus = findViewById(R.id.text_status);
        Intent intent = getIntent();

//        try {
//            Amplify.addPlugin(new AWSDataStorePlugin());
//            Amplify.addPlugin(new AWSApiPlugin());
//            Amplify.configure(getApplicationContext());
//
//            Log.i("Tutorial", "Initialized Amplify");
//        } catch (AmplifyException e) {
//            Log.e("Tutorial", "Could not initialize Amplify", e);
//        }

        Handler handler = new Handler(Looper.getMainLooper() , msg -> {

            textTitle.setText(title);
            textDesc.setText(desc);
            textStatus.setText(status.toString());
            return true ;
        });

        Amplify.API.query(
                    ModelQuery.get(Task.class, intent.getStringExtra("id")),
                    response -> {

                        Log.i("MyAmplifyApp", (response.getData()).getTitle()) ;

                        title = response.getData().getTitle();
                        desc = response.getData().getBody();
                        status = response.getData().getStatus();



                        Bundle bundle = new Bundle();


                        Message message = new Message();
                        message.setData(bundle);
                        handler.sendMessage(message);


                    },
                    error -> Log.e("MyAmplifyApp", error.toString(), error)
            );


//        TaskData taskData = AppDatabase.getInstance(this).taskDao().getTaskByID(Long.valueOf(intent.getStringExtra("id")));

    }
}